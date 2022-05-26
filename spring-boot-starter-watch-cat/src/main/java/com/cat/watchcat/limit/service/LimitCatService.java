package com.cat.watchcat.limit.service;

import cn.hutool.crypto.SecureUtil;
import com.cat.watchcat.limit.annotation.LimitCat;
import com.cat.watchcat.limit.annotation.LimitCatRule;
import com.cat.watchcat.limit.config.LimitCatProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
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

    DefaultRedisScript<Long> limitUpdateScript;
    DefaultRedisScript<Long> limitGetScript;

    @PostConstruct
    public void initLuaScript() {
        limitUpdateScript = new DefaultRedisScript<>();
        limitUpdateScript.setResultType(Long.class);
        limitUpdateScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit_update.lua")));

        limitGetScript = new DefaultRedisScript<>();
        limitGetScript.setResultType(Long.class);
        limitGetScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit_get.lua")));
    }

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
                checkCache(scene,key,Duration.of(limitCatRule.interval(), ChronoUnit.SECONDS), limitCatRule.frequency(),limitCatRule.message());
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
     * 检查缓存(lua脚本)，这里未使用lua脚本方式来实现，主要是check 和 update 操作都是单命令，本身就能保证原子性，不需要使用lua脚本来确保
     * Redis的单个命令都是原子性的，有时候我们希望能够组合多个Redis命令，并让这个组合也能够原子性的执行，甚至可以重复使用
     * @param scene
     * @param key
     * @param duration
     * @param frequency
     */
    private void checkCache2(String scene,String key,Duration duration,Long frequency,String msg) {

        String frequencyKey = String.format(FREQUENCY_KEY,scene,SecureUtil.md5(key),duration.toString());

        List<String> keys = new ArrayList<>();
        keys.add(frequencyKey);

        Long isValid = redisTemplate.execute(limitGetScript,keys,frequency);

        if(isValid==0) {
            throw new LimitCatException(StringUtils.hasText(msg)?msg:String.format("操作太频繁，请稍后再试。（场景 %s 限制%s执行%s次）",scene, duration,frequency));
        }
    }

    /**
     * 更新缓存(lua脚本)，这里未使用lua脚本方式来实现，主要是check 和 update 操作都是单命令，本身就能保证原子性，不需要使用lua脚本来确保
     * Redis的单个命令都是原子性的，有时候我们希望能够组合多个Redis命令，并让这个组合也能够原子性的执行，甚至可以重复使用
     * @param scene
     * @param key
     * @param duration
     */
    private void updateCache2(String scene,String key,Duration duration) {

        String frequencyKey = String.format(FREQUENCY_KEY,scene,SecureUtil.md5(key),duration.toString());

        List<String> keys = new ArrayList<>();
        keys.add(frequencyKey);

        redisTemplate.execute(limitUpdateScript,keys,duration.toMillis()/1000);
    }

//    public static void main(String[] args) {
//        String a = "[by:用户5788]\\n[00:00.80]曾经有一艘船出海\\n[00:03.27]这艘船的名字叫 Billy o' Tea\\n[00:05.70]随着风吹起，她扬帆起航\\n[00:08.21]吹吧，使劲吹\\n[00:10.75]过不了多久，“茶壶号”就要来了\\n[00:13.22]给我们带来糖、茶和朗姆酒\\n[00:15.75]当捕捉的鲸鱼被处理完\\n[00:18.14]我们走吧，再度出发\\n[00:20.53]她离开海岸还不到两星期\\n[00:23.14]正好有一只鲸鱼在她下方钻来钻去\\n[00:25.71]船长对所有船员发誓\\n[00:28.16]他将把鲸鱼捕捞上岸\\n[00:30.82]过不了多久，“茶壶号”就要来了\\n[00:32.98]给我们带来糖、茶和朗姆酒\\n[00:35.85]当捕捉的鲸鱼被处理完\\n[00:38.13]我们走吧，再度出发\\n[00:40.60]涌起的海浪拍打着船身\\n[00:43.27]船员抓住了鲸鱼的尾巴\\n[00:45.72]所有船员都到船边，手持鱼叉和她搏斗\\n[00:48.34]鲸鱼深深地潜入海中\\n[00:50.81]过不了多久，“茶壶号”就要来了\\n[00:53.11]给我们带来糖、茶和朗姆酒\\n[00:55.81]当捕捉的鲸鱼被处理完\\n[00:58.01]我们走吧，再度出发\\n[01:00.63]绳子不会断裂，鲸鱼不会被释放\\n[01:03.18]船长的心思并不贪婪\\n[01:05.70]但他有属于捕鲸人的信仰\\n[01:08.07]她把船拖着向前\\n[01:10.81]过不了多久，“茶壶号”就要来了\\n[01:13.13]给我们带来糖、茶和朗姆酒\\n[01:15.78]当捕捉的鲸鱼被处理完\\n[01:18.00]我们走吧，再度出发\\n[01:20.57]过了四十天，甚至不只四十天\\n[01:23.07]拴住鲸鱼的绳子时紧时松\\n[01:25.72]我们只剩下四只船，其他船都不知去向\\n[01:28.09]但鲸鱼还在\\n[01:30.73]过不了多久，“茶壶号”就要来了\\n[01:33.12]给我们带来糖、茶和朗姆酒\\n[01:35.66]当捕捉的鲸鱼被处理完\\n[01:38.01]我们走吧，再度出发\\n[01:40.53]据我所知，战斗还在进行中\\n[01:43.22]绳子还没剪断，鲸鱼还没离开\\n[01:45.65]“茶壶号”不断鸣号\\n[01:48.12]鼓励着船长、船员和所有一切\\n[01:50.78]过不了多久，“茶壶号”就要来了\\n[01:53.26]给我们带来糖、茶和朗姆酒\\n[01:55.80]当捕捉的鲸鱼被处理完\\n[01:58.09]我们走吧，再度出发\\n[02:00.72]过不了多久，“茶壶号”就要来了\\n[02:03.22]给我们带来糖、茶和朗姆酒\\n[02:05.80]当捕捉的鲸鱼被处理完\\n[02:08.12]我们走吧，再度出发";
//        System.out.println(a.replaceAll("\\\\n", "\n"));
//
//    }
}