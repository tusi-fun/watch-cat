package com.cat.watchcat.limit.service;

import cn.hutool.crypto.SecureUtil;
import com.cat.watchcat.limit.annotation.LimitCat;
import com.cat.watchcat.limit.annotation.LimitCatRule;
import com.cat.watchcat.limit.config.LimitCatProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
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

    DefaultRedisScript<Long> limitUpdateScript;
    DefaultRedisScript<Long> limitGetScript;

    public LimitCatService(RedisTemplate wcRedisTemplate, LimitCatProperties limitCatProperties) {
        this.wcRedisTemplate = wcRedisTemplate;
        this.limitCatProperties = limitCatProperties;
    }

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

        Long currentValue = wcRedisTemplate.opsForValue().increment(frequencyKey);

        if(currentValue==1) {
            wcRedisTemplate.expire(frequencyKey,duration);
        }
    }
//
//    /**
//     * 检查缓存(lua脚本)，这里未使用lua脚本方式来实现，主要是check 和 update 操作都是单命令，本身就能保证原子性，不需要使用lua脚本来确保
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
//     * 更新缓存(lua脚本)，这里未使用lua脚本方式来实现，主要是check 和 update 操作都是单命令，本身就能保证原子性，不需要使用lua脚本来确保
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

//    public static void main(String[] args) {
//        String a = "[00:15.780]我不像你以前认识的任何人\\n[00:19.620]我直面挑战\\n[00:21.020]而他们只会寻找退路\\n[00:23.350]我不会停歇 不会退缩\\n[00:25.690]没有人能阻止我\\n[00:27.690]因为我在完成一个使命\\n[00:29.710]并且我不会就这样放弃\\n[00:31.490]在一个满是追随者的世界里\\n[00:33.190]我将是一个领导者\\n[00:35.230]在一个满是怀疑者的世界里\\n[00:37.120]我将是一个信徒\\n[00:40.230]我坚定不移，勇往直前\\n[00:43.730]因为战役已经取得了胜利\\n[00:47.340]我为之奉献一切\\n[00:49.520]我再也不会\\n[00:51.620]只为我自己活着\\n[00:53.610]我追随着耶稣的脚步\\n[00:55.240]全心全意地付出\\n[00:57.740]我已经准备好登场了\\n[01:00.688]我为之奉献一切\\n[01:03.627]我为之奉献一切\\n[01:05.569]现在我迈出的\\n[01:07.188]每一步\\n[01:09.400]每一滴流在\\n[01:10.808]血管里的血\\n[01:12.489]都不会白白浪费\\n[01:16.799]我为之奉献一切\\n[01:20.019]这不仅仅是某个短暂的阶段\\n[01:23.680]你不能接收这份恩泽\\n[01:26.099]舍弃来时的路\\n[01:27.858]这份永恒所蕴含的意义\\n[01:30.188]让我绝不会半途而废\\n[01:31.620]我在完成上天赐予我的使命\\n[01:35.468]在一个满是追随者的世界里\\n[01:37.409]我会是一个领导者\\n[01:39.200]在一个满是怀疑者的世界里\\n[01:41.049]我会是一个信徒\\n[01:43.739]我坚定不移，勇往直前\\n[01:47.810]因为我的灵魂像一个竞技场\\n[01:51.498]我为之奉献一切\\n[01:53.459]我再也不会\\n[01:55.650]只为自己活着\\n[01:57.730]我追随着耶稣的脚步\\n[01:59.718]全心全意地追随我心所信\\n[02:01.918]我已经准备好登场了\\n[02:04.638]我为之奉献一切\\n[02:07.638]我为之奉献一切\\n[02:09.617]现在我迈出的\\n[02:11.278]每一步\\n[02:13.418]每一滴流在\\n[02:14.738]血管里的血\\n[02:17.808]我都会奋力拼搏\\n[02:20.897]我将为之奉献一切\\n[02:25.158]没有审判能阻止我\\n[02:29.177]给我的热情以打击\\n[02:33.009]它们只是一个机会\\n[02:37.068]来把我的信仰付诸行动\\n[02:39.718]在一个满是追随者的世界里\\n[02:41.298]我将是一个领导者\\n[02:43.318]在一个满是怀疑者的世界里\\n[02:45.208]我将是一个信徒\\n[02:48.218]我坚定不移，勇往直前\\n[02:52.078]我无所畏惧\\n[02:55.397]我为之奉献一切\\n[02:57.498]我再也不会\\n[02:59.199]只为自己活着\\n[03:01.449]追随着耶稣的脚步\\n[03:03.418]全心全意追随我的心\\n[03:05.748]而且现在我准备好登场了\\n[03:08.598]我为之奉献一切\\n[03:11.617]我为之奉献一切\\n[03:13.788]现在我迈出的\\n[03:15.188]每一步\\n[03:17.578]每一滴流在\\n[03:19.229]血管里的血\\n[03:21.177]我都会奋力拼搏，让它们变得不可或缺\\n[03:24.718]我为之奉献一切\\n[03:28.658]";
//        System.out.println(a.replaceAll("\\\\n", "\n"));
//
//    }
}