package com.cat.watchcat.limit.service;

import cn.hutool.crypto.SecureUtil;
import com.cat.watchcat.limit.annotation.LimitCat;
import com.cat.watchcat.limit.annotation.LimitCatRule;
import com.cat.watchcat.limit.config.LimitCatProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * 业务调用流控
 * @author hudongshan
 * @version 20210425
 */
@Slf4j
public class LimitCatService {

    private final RedisTemplate wcRedisTemplate;
    private final LimitCatProperties limitCatProperties;

    public static final String FREQUENCY_KEY = "watch-cat:limit:%s:%s:%s";

//    DefaultRedisScript<Long> limitUpdateScript;
//    DefaultRedisScript<Long> limitGetScript;

    public LimitCatService(RedisTemplate wcRedisTemplate, LimitCatProperties limitCatProperties) {
        this.wcRedisTemplate = wcRedisTemplate;
        this.limitCatProperties = limitCatProperties;
    }

//    @PostConstruct
//    public void initLuaScript() {
//        limitUpdateScript = new DefaultRedisScript<>();
//        limitUpdateScript.setResultType(Long.class);
//        limitUpdateScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit_update.lua")));
//
//        limitGetScript = new DefaultRedisScript<>();
//        limitGetScript.setResultType(Long.class);
//        limitGetScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit_get.lua")));
//    }

    /**
     * 业务执行前验证
     * @param scene
     * @param key
     * @param limitCat
     */
    public void checkFrequency(String scene, String key, LimitCat limitCat) {

        LimitCatRule[] rules = limitCat.rules();

        if(rules!=null && rules.length>0) {

            log.info("频率验证(规则由代码指定)，scene：{}，key：{}",scene,key);

            for(LimitCatRule limitCatRule :rules) {
                checkCache(scene,key,Duration.of(limitCatRule.interval(), ChronoUnit.SECONDS), limitCatRule.frequency(),limitCatRule.message());
            }

        } else {

            log.info("频率验证(规则由配置指定)，scene：{}，key：{}",scene,key);

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

            log.info("频率更新(规则由代码指定)，scene：{}，key：{}",scene,key);

            for(LimitCatRule limitCatRule :rules) {
                updateCache(scene,key,Duration.of(limitCatRule.interval(), ChronoUnit.SECONDS));
            }

        } else {

            log.info("频率更新(规则由配置指定)，scene：{}，key：{}",scene,key);

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

        Object object = wcRedisTemplate.opsForValue().get(frequencyKey);

        log.info("频率验证，scene：{}，key：{}，限制{}执行{}次，已执行{}次",scene,key,duration,frequency,object);

        if(object!=null && Long.parseLong(object.toString())>=frequency) {
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

        Long currentValue = wcRedisTemplate.opsForValue().increment(frequencyKey,1);

        // 初次计数，设置 key 有效期
        if(currentValue==1) {
            wcRedisTemplate.expire(frequencyKey,duration);
        }
    }
//
//    /**
//     * 检查缓存(lua脚本)，这里未使用lua脚本方式来实现，主要是 check 和 update 操作都是单命令，本身就能保证原子性，不需要使用lua脚本来确保
//     * 参考：Redis的单个命令都是原子性的，有时候我们希望能够组合多个Redis命令，并让这个组合也能够原子性的执行，甚至可以重复使用
//     * @param scene
//     * @param key
//     * @param duration
//     * @param frequency
//     */
//    private void checkCache2(String scene,String key,Duration duration,Long frequency,String msg) {
//
//        String frequencyKey = String.format(FREQUENCY_KEY,scene,SecureUtil.md5(key),duration.toString());
//
//        List<String> keys = new ArrayList<>();
//        keys.add(frequencyKey);
//
//        Long isValid = wcRedisTemplate.execute(limitGetScript,keys,frequency);
//
//        if(isValid==0) {
//            throw new LimitCatException(StringUtils.hasText(msg)?msg:String.format("操作太频繁，请稍后再试。（场景 %s 限制%s执行%s次）",scene, duration,frequency));
//        }
//    }
//
//    /**
//     * 更新缓存(lua脚本)，这里未使用lua脚本方式来实现，主要是 check 和 update 操作都是单命令，本身就能保证原子性，不需要使用lua脚本来确保
//     * 参考：Redis的单个命令都是原子性的，有时候我们希望能够组合多个Redis命令，并让这个组合也能够原子性的执行，甚至可以重复使用
//     * @param scene
//     * @param key
//     * @param duration
//     */
//    private void updateCache2(String scene,String key,Duration duration) {
//
//        String frequencyKey = String.format(FREQUENCY_KEY,scene,SecureUtil.md5(key),duration.toString());
//
//        List<String> keys = new ArrayList<>();
//        keys.add(frequencyKey);
//
//        wcRedisTemplate.execute(limitUpdateScript,keys,duration.toMillis()/1000);
//    }
}