package com.cat.watchcat.limit.service;

import cn.hutool.crypto.SecureUtil;
import com.cat.watchcat.limit.annotation.LimitCat;
import com.cat.watchcat.limit.annotation.LimitCatRule;
import com.cat.watchcat.limit.config.LimitCatProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
                checkCache(scene,key,Duration.of(limitCatRule.intervalSeconds(), ChronoUnit.SECONDS), limitCatRule.frequency(),limitCatRule.msg());
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
                updateCache(scene,key,Duration.of(limitCatRule.intervalSeconds(), ChronoUnit.SECONDS));
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

}