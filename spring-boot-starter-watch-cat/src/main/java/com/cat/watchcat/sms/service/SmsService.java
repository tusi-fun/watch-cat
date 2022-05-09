package com.cat.watchcat.sms.service;//package com.cat.watchcat.sms.service;
//
//import cn.hutool.crypto.SecureUtil;
//import com.aliyuncs.CommonRequest;
//import com.aliyuncs.CommonResponse;
//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.exceptions.ClientException;
//import com.aliyuncs.exceptions.ServerException;
//import com.aliyuncs.http.MethodType;
//import com.aliyuncs.profile.DefaultProfile;
//import com.xx.enumerate.SmsSceneEnum;
//import com.xx.enumerate.SmsTemplateEnum;
//import com.xx.exception.BusinessAssert;
//import com.xx.exception.BusinessException;
//import com.xx.exception.SmsErrEnum;
//import com.xx.properties.SmsProperties;
//import com.xx.properties.SmsTemplate;
//import com.xx.properties.SmsTemplateProperties;
//import com.xx.util.JsonUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Map;
//import java.util.UUID;
//
//import static com.google.common.collect.Maps.newHashMap;
//
///**
// * 梦网短信通道
// *
// * @author hudongshan
// * @version 20200307
// */
//@Slf4j
//@Component
//public class SmsService {
//
//    @Autowired
//    private SmsProperties smsProperties;
//
//    @Autowired
//    private SmsTemplateProperties smsTemplateProperties;
//
//    @Autowired
//    private RedisTemplate<String,Object> redisTemplate;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    // 梦网专属配置
//    private static final String SINGLE_API_URL = "http://61.145.229.29:7791/sms/v2/std/send_single";
//    private static final String USER_ID = "H12878";
//    private static final String PWD = "851386";
//    private static final String SEND_SUCCESS = "result=0";
//
////    /**
////     * 发送短信验证码（梦网）
////     */
////    public String sendSmsCode(SmsSceneEnum smsSceneEnum, String phoneNumber) {
////
////        String smsCode = RandomStringUtils.randomNumeric(4);
////        String outId = UUID.randomUUID().toString().replaceAll("-","");
////
////        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMddHHmmss");
////        String timestamp = df.format(LocalDateTime.now());
////        String pwd = SecureUtil.md5(String.format("%s%s%s%s",USER_ID,"00000000",PWD,timestamp));
////        String content = String.format("验证码%s（请在15分钟内输入验证码，切勿将验证码泄露给他人）",smsCode);
////        String url = String.format("%s?userid=%s&pwd=%s&mobile=%s&content=%s&timestamp=%s&custid=%s",SINGLE_API_URL,USER_ID,pwd,phoneNumber,content,timestamp,outId);
////
////        String mengWangResult = restTemplate.getForEntity(url, String.class).getBody();
////
////        log.info("mengWangResult = {}",mengWangResult);
////
////        if (StringUtils.isNotBlank(mengWangResult) && mengWangResult.contains(SEND_SUCCESS)) {
////
////            // send success
////            smsCacheService.cacheSmsCode(smsSceneEnum, outId, phoneNumber, smsCode);
////
////            return outId;
////        } else {
////            log.error("短信发送失败,phone:{},exception:{}", phoneNumber, mengWangResult);
////            throw new BusinessException(BusinessCodeEnum.SMS_SEND_ERROR, mengWangResult);
////        }
////    }
//
//
//    /**
//     * 发送短信验证码（aliyun）
//     */
//    public String sendSmsCode(SmsSceneEnum smsSceneEnum, String phoneNumber) {
//
//        SmsTemplate smsTemplate = smsTemplateProperties.getAliyun().get(SmsTemplateEnum.SMS_CODE);
//
//        String smsCode = RandomStringUtils.randomNumeric(4);
//        String outId = UUID.randomUUID().toString().replaceAll("-","");
//
//        // 调用阿里云短信接口
//        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsProperties.getAliyun().getAccessKeyId(), smsProperties.getAliyun().getSecret());
//        IAcsClient client = new DefaultAcsClient(profile);
//
//        CommonRequest request = new CommonRequest();
//        request.setSysMethod(MethodType.POST);
//        request.setSysDomain("dysmsapi.aliyuncs.com");
//        request.setSysVersion("2017-05-25");
//        request.setSysAction("SendSms");
//        request.putQueryParameter("RegionId", "cn-hangzhou");
//        request.putQueryParameter("PhoneNumbers", phoneNumber);
//        request.putQueryParameter("SignName", smsProperties.getAliyun().getSignName());
//        request.putQueryParameter("TemplateCode", smsTemplate.getCode());
//        request.putQueryParameter("TemplateParam", String.format(smsTemplate.getStructure(),smsCode,"sss"));
//        request.putQueryParameter("OutId", outId);
//        try {
//            CommonResponse response = client.getCommonResponse(request);
//            log.info("aliyunSmsResult = {}",response);
//
//            AliyunSmsResponse aliyunSmsResponse = JsonUtils.toBean(response.getData(), AliyunSmsResponse.class);
//
//            log.info("AliyunSmsResponse:{}", response.getData());
//
//            if (aliyunSmsResponse.getCode() != null && aliyunSmsResponse.getCode().equals("OK")) {
//
//                // cache smsCode
//                cacheSmsCode(smsSceneEnum, outId, phoneNumber, smsCode);
//
//                return outId;
//
//            } else {
//                log.error("短信发送失败,phone:{},exception:{}", phoneNumber, aliyunSmsResponse.getCode() + aliyunSmsResponse.getMessage());
//                throw new BusinessException(SmsErrEnum.SMS_SEND_ERROR, aliyunSmsResponse.getCode() + aliyunSmsResponse.getMessage());
//            }
//
//        } catch (ServerException e) {
//            log.error("短信发送失败,phone:{},exception:{}", phoneNumber, e.getErrCode() + e.getMessage());
//            throw new BusinessException(SmsErrEnum.SMS_SEND_ERROR, e.getErrCode() + e.getMessage());
//        } catch (ClientException e) {
//            log.error("短信发送失败,phone:{},exception:{}", phoneNumber, e.getErrCode() + e.getMessage());
//            throw new BusinessException(SmsErrEnum.SMS_SEND_ERROR, e.getErrCode() + e.getMessage());
//        }
//    }
//
//    /**
//     * 发送自定义内容短信
//     */
//    public String sendCustomSms(String phoneNumber,String msg) {
//
//        String outId = UUID.randomUUID().toString();
//
//        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMddHHmmss");
//        String timestamp = df.format(LocalDateTime.now());
//        String pwd = SecureUtil.md5(String.format("%s%s%s%s",USER_ID,"00000000",PWD,timestamp));
//        String url = String.format("%s?userid=%s&pwd=%s&mobile=%s&content=%s&timestamp=%s&custid=%s",SINGLE_API_URL,USER_ID,pwd,phoneNumber,msg,timestamp,outId);
//
//        String mengWangResult = restTemplate.getForEntity(url, String.class).getBody();
//
//        log.info("mengWangResult = {}",mengWangResult);
//
//        if (StringUtils.isNotBlank(mengWangResult) && mengWangResult.contains(SEND_SUCCESS)) {
//            return outId;
//        } else {
//            log.error("短信发送失败,phone:{},exception:{}", phoneNumber, mengWangResult);
//            throw new BusinessException(SmsErrEnum.SMS_SEND_ERROR, mengWangResult);
//        }
//    }
//
//    /**
//     * 缓存短信验证码 和 手机号的对应关系
//     */
//    private void cacheSmsCode(SmsSceneEnum aliyunSmsSceneEnum, String outId, String phoneNumber, String smsCode) {
//
//        // 缓存smsCode
//        String key = String.format(smsProperties.getCacheKeyName(), outId, aliyunSmsSceneEnum.getCode());
//
//        Map<String, String> cacheVal = newHashMap();
//        cacheVal.put("code", smsCode);
//        cacheVal.put("phone", phoneNumber);
//
//        redisTemplate.opsForHash().putAll(key, cacheVal);
//        redisTemplate.expire(key, smsProperties.getCacheKeyTimeout());
//    }
//
//    /**
//     * 验证短信验证码
//     * 验证不通过，返回 null
//     * 验证通过，返回手机号码，同时删除短信验证码缓存（特殊手机号除外）
//     */
//    public String verifySmsCode(SmsSceneEnum aliyunSmsSceneEnum, String outId, String smsCode) {
//
//        String key = String.format(smsProperties.getCacheKeyName(), outId, aliyunSmsSceneEnum.getCode());
//
//        // 验证 outid 是否正确
//        BusinessAssert.isTrue(redisTemplate.hasKey(key), SmsErrEnum.SMS_CODE_NOT_EXISTS);
//
//        Map<Object,Object> smsCodeValue = redisTemplate.opsForHash().entries(key);
//
//        BusinessAssert.isTrue(smsCode.equals(String.valueOf(smsCodeValue.get("code"))), SmsErrEnum.SMS_CODE_NOT_EXISTS);
//
//        // 验证通过，移除smsCode缓存（应用市场审核账号特殊处理）
//        if(!(outId.equals(smsProperties.getAudit().getOutId()) && smsCode.equals(smsProperties.getAudit().getSmsCode()))) {
//            redisTemplate.delete(key);
//        }
//
//        return String.valueOf(smsCodeValue.get("phone"));
//    }
//
//}