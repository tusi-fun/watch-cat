package com.cat.watchcat.limit.service;

import cn.hutool.crypto.SecureUtil;
import com.cat.watchcat.limit.annotation.LimitCat;
import com.cat.watchcat.limit.annotation.LimitCatRule;
import com.cat.watchcat.limit.config.LimitCatProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 业务调用流控
 * @author hudongshan
 * @version 20210425
 */
@Slf4j
public class LimitCatService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private LimitCatProperties limitCatProperties;

    public static final String FREQUENCY_KEY = "watch-cat:limit:%s:%s:%s";

    /**
     * 业务执行前验证
     * @param scene
     * @param key
     * @param limitCat
     */
    public void checkFrequency(String scene, String key, LimitCat limitCat) {

        LimitCatRule[] rules = limitCat.rules();

        if(rules!=null && rules.length>0) {

            log.info("频率验证(代码指定频率规则)，scene：{}，key：{}",scene,key);

            for(LimitCatRule limitCatRule :rules) {
                checkCache(scene,key,Duration.of(limitCatRule.interval(), ChronoUnit.SECONDS), limitCatRule.frequency(),limitCatRule.msg());
            }

        } else {

            log.info("频率验证(配置指定频率规则)，scene：{}，key：{}",scene,key);

            Map<Duration,Long> frequencySceneList = limitCatProperties.getScenes().get(scene);

            if(frequencySceneList==null || frequencySceneList.isEmpty()) {
                throw new LimitCatException("频率限制场景"+scene+"不存在或未配置");
            }

            for (Map.Entry<Duration, Long> entry : frequencySceneList.entrySet()) {
                checkCache(scene, key, entry.getKey(), entry.getValue(),limitCat.msg());
            }
        }
    }

    /**
     * 业务执行后更新（失败重试频率限制则只在失败状态下做更新）
     * @param scene
     * @param key
     * @param rules 流控规则（使用代码指定，优先级最高）
     */
    public void updateFrequency(String scene, String key, LimitCatRule[] rules) {

        if(rules!=null && rules.length>0) {

            log.info("频率更新(代码指定频率规则)，scene：{}，key：{}",scene,key);

            for(LimitCatRule limitCatRule :rules) {
                updateCache(scene,key,Duration.of(limitCatRule.interval(), ChronoUnit.SECONDS));
            }

        } else {

            log.info("频率更新(配置指定频率规则)，scene：{}，key：{}",scene,key);

            Map<String,Map<Duration,Long>> scenes = limitCatProperties.getScenes();

            if(scenes==null || scenes.isEmpty()) {
                throw new LimitCatException("频率限制场景不存在或未正确配置");
            }

            Map<Duration,Long> frequencySceneList = scenes.get(scene);

            if(frequencySceneList==null || frequencySceneList.isEmpty()) {
                throw new LimitCatException("频率限制场景"+scene+"不存在或未正确配置");
            }

            for (Map.Entry<Duration, Long> entry : frequencySceneList.entrySet()) {
                updateCache(scene,key,entry.getKey());
            }
        }
    }

    /**
     * 检查缓存
     * @param scene
     * @param key
     * @param duration
     * @param frequency
     */
    private void checkCache(String scene,String key,Duration duration,Long frequency,String msg) {

        String frequencyKey = String.format(FREQUENCY_KEY,scene,SecureUtil.md5(key),duration.toString());

        Object object = redisTemplate.opsForValue().get(frequencyKey);

        log.info("频率验证，scene：{}，key：{}，限制{}执行{}次，已执行{}次",scene,key,duration,frequency,object);

        if(object!=null && Long.valueOf(object.toString())>=frequency) {
            throw new LimitCatException(StringUtils.hasText(msg)?msg:String.format("操作太频繁，请稍后再试。（场景 %s 限制%s执行%s次）",scene, duration,frequency));
        }
    }

    /**
     * 更新缓存
     * @param scene
     * @param key
     * @param duration
     */
    private void updateCache(String scene,String key,Duration duration) {

        String frequencyKey = String.format(FREQUENCY_KEY,scene,SecureUtil.md5(key),duration.toString());

        Long currentValue = redisTemplate.opsForValue().increment(frequencyKey);

        if(currentValue==1) {
            redisTemplate.expire(frequencyKey,duration);
        }
    }

    /**
     * 更新缓存
     * @param scene
     * @param key
     * @param duration
     */
    private void updateCache2(String scene,String key,Duration duration) {

        String frequencyKey = String.format(FREQUENCY_KEY,scene,SecureUtil.md5(key),duration.toString());

        List<String> keys = new ArrayList<>();
        keys.add(frequencyKey);
        RedisScript<Long> redisScript = new DefaultRedisScript<>(buildLuaScript(),Long.class);
        Long count = redisTemplate.execute(redisScript,keys,10,10000);

        log.info( "Access try count is {} for key={}", count, frequencyKey );
        if (count != null && count == 0) {
            log.debug("令牌桶={}，获取令牌失败",key);
            throw new LimitCatException("令牌桶="+key+"，获取令牌失败");
        }
    }

    /**
     * lua 脚本操作令牌桶
     * @return
     */
    private String buildLuaScript() {
        StringBuilder luaString = new StringBuilder();
        luaString.append("local key = KEYS[1]");
        // 获取ARGV内参数Limit
        luaString.append("\nlocal limit = tonumber(ARGV[1])");
        // 获取key的次数
        luaString.append("\nlocal curentLimit = tonumber(redis.call('get', key) or \"0\")");
        luaString.append("\nif curentLimit + 1 > limit then");
        luaString.append("\nreturn 0");
        luaString.append("\nelse");
        // 自增长 1
        luaString.append("\nredis.call(\"INCRBY\", key, 1)");
        // 设置过期时间
        luaString.append("\nredis.call(\"EXPIRE\", key, ARGV[2])");
        luaString.append("\nreturn curentLimit + 1");
        luaString.append("\nend");
        return luaString.toString();
    }

    public static void main(String[] args) {
        String a = "[{\"lineLyric\":\"光的方向 (《长歌行》电视剧片头主题曲) - 张碧晨\",\"time\":\"0.0\"},{\"lineLyric\":\"词：萨吉\",\"time\":\"3.25\"},{\"lineLyric\":\"曲：金大洲\",\"time\":\"3.36\"},{\"lineLyric\":\"编曲：金大洲D-Jin\",\"time\":\"3.49\"},{\"lineLyric\":\"制作人：金大洲D-Jin\",\"time\":\"3.68\"},{\"lineLyric\":\"吉他：D-Jin\",\"time\":\"3.92\"},{\"lineLyric\":\"哨笛/风笛：Eric Rigler\",\"time\":\"4.03\"},{\"lineLyric\":\"弦乐编写/监制：D-Jin/胡静成\",\"time\":\"4.24\"},{\"lineLyric\":\"弦乐：国际首席爱乐乐团\",\"time\":\"4.6\"},{\"lineLyric\":\"弦乐录音：王小四@金田录音棚\",\"time\":\"4.95\"},{\"lineLyric\":\"人声录音：李宗远@Studio21A\",\"time\":\"5.36\"},{\"lineLyric\":\"配唱：赵贝尔\",\"time\":\"5.62\"},{\"lineLyric\":\"合声编写：D-Jin\",\"time\":\"5.79\"},{\"lineLyric\":\"合声：赵贝尔\",\"time\":\"5.95\"},{\"lineLyric\":\"混音/母带：George Dum@Liquid Fish Studio LA\",\"time\":\"6.12\"},{\"lineLyric\":\"OP：D-Jin Music(北京翊辰文化传媒有限公司）\",\"time\":\"6.45\"},{\"lineLyric\":\"音乐出品：华策音乐（天津）有限公司\",\"time\":\"6.95\"},{\"lineLyric\":\"无处可逃\",\"time\":\"8.56\"},{\"lineLyric\":\"无枝可靠\",\"time\":\"11.5\"},{\"lineLyric\":\"逆行着微笑\",\"time\":\"14.39\"},{\"lineLyric\":\"不屈不挠\",\"time\":\"20.29\"},{\"lineLyric\":\"忘了年少\",\"time\":\"23.34\"},{\"lineLyric\":\"不曾畏寂寞成行\",\"time\":\"30.21\"},{\"lineLyric\":\"泪与憾成双\",\"time\":\"33.44\"},{\"lineLyric\":\"心已滚烫 （向着远方）\",\"time\":\"36.33\"},{\"lineLyric\":\"过往风霜成刻刀\",\"time\":\"42.24\"},{\"lineLyric\":\"折裂了翅膀\",\"time\":\"45.08\"},{\"lineLyric\":\"还要 飞翔\",\"time\":\"47.89\"},{\"lineLyric\":\"循着光照的方向 把你遗忘\",\"time\":\"57.03\"},{\"lineLyric\":\"回忆折旧成我倔强的模样\",\"time\":\"62.88\"},{\"lineLyric\":\"我要凭这暗夜里的光\",\"time\":\"68.770004\"},{\"lineLyric\":\"还它与⼀曲长歌相望\",\"time\":\"71.66\"},{\"lineLyric\":\"踏着生命之河 不枉痴狂\",\"time\":\"74.72\"},{\"lineLyric\":\"独行的寥\",\"time\":\"91.479996\"},{\"lineLyric\":\"疲倦的傲\",\"time\":\"94.39\"},{\"lineLyric\":\"还剩下多少\",\"time\":\"97.2\"},{\"lineLyric\":\"心若泥沼\",\"time\":\"103.14\"},{\"lineLyric\":\"谁会知道\",\"time\":\"106.21\"},{\"lineLyric\":\"不曾畏寂寞成行\",\"time\":\"113.43\"},{\"lineLyric\":\"泪与憾成双\",\"time\":\"116.34\"},{\"lineLyric\":\"心已滚烫 （向着远方）\",\"time\":\"119.1\"},{\"lineLyric\":\"过往风霜成刻刀\",\"time\":\"125.1\"},{\"lineLyric\":\"折裂了翅膀\",\"time\":\"128.02\"},{\"lineLyric\":\"还要 飞翔\",\"time\":\"130.84\"},{\"lineLyric\":\"循着光照的方向 把你遗忘\",\"time\":\"139.8\"},{\"lineLyric\":\"回忆折旧成我倔强的模样\",\"time\":\"145.75\"},{\"lineLyric\":\"我要凭这暗夜里的光\",\"time\":\"151.64\"},{\"lineLyric\":\"还它与⼀曲长歌相望\",\"time\":\"154.7\"},{\"lineLyric\":\"踏着生命之河 不枉痴狂\",\"time\":\"157.66\"}]";
        System.out.println(a.replaceAll("\\\\n", "\n"));

    }
}