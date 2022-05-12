package com.cat.watchcat.secret.aspect;

import cn.hutool.crypto.SecureUtil;
import com.cat.util.JsonUtils;
import com.cat.watchcat.secret.annotation.SecretCat;
import com.cat.watchcat.secret.service.DataEncryptService;
import com.cat.watchcat.secret.service.SecretCatException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 请求加密控制切面
 *
 * @author hudongshan
 * @version 20200227
 *
 * 参考：https://blog.csdn.net/qq_15076569/article/details/100923074
 *
 */
@Slf4j
@Aspect
@Component
public class SecretCatAspect {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataEncryptService dataEncryptService;

    @Pointcut("@annotation(secretCat)")
    public void pointCut(SecretCat secretCat) {}

    @Around("pointCut(secretCat)")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint, SecretCat secretCat) throws Throwable {

        log.info("请求参数解密");

        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();

        String encryptKey = request.getParameter("encryptKey");
        String encryptData = request.getParameter("encryptData");

        log.info("encryptKey = " + encryptKey);
        log.info("encryptData = " + encryptData);

        if (!StringUtils.hasText(encryptKey)) {
            throw new SecretCatException("encryptKey不能为空");
        }

        if (!StringUtils.hasText(encryptData)) {
            throw new SecretCatException("encryptData不能为空");
        }

        // 是否启用重复提交验证
        if(secretCat.preventReplay()) {

            String hash = SecureUtil.sha1(encryptKey+encryptData);

            // 验证缓存是否存在相同提交数据
            if (!dataEncryptService.cacheEncryptHash(hash)) {
                throw new SecretCatException("不能重复提交加密数据");
            }
        }

        // 解密参数和值
        byte[] aeskey = dataEncryptService.decryptAesKey(encryptKey);
        String data = dataEncryptService.decryptData(aeskey,encryptData);

        log.info("plainText = {}",data);

        // 将解密后的参数传递给方法
        Object[] args = proceedingJoinPoint.getArgs();

        for (int i=0;i<args.length;i++) {

            Object o = args[i];

            if(o instanceof PingSecretBasic) {

                // 将解密后的结果 copy 到指定对象
                BeanUtils.copyProperties(JsonUtils.toBean(data, o.getClass()),o);

                // 是否启用参数验证
                if(secretCat.plainTextValid()) {
                    // 执行参数验证
                    validParam(o);
                }
                break;
            }
        }

        Object proceedResult = proceedingJoinPoint.proceed(args);

        // 返回值是否需要加密
        if(secretCat.encryptedPong()) {
            encryptResult(aeskey,proceedResult,secretCat.pongEncryptField());
        }

//     * 反射修改属性
//     * https://blog.csdn.net/u011402896/article/details/79550913
        return proceedResult;
    }

    /**
     * 请求原文参数验证
     * @param o
     */
    private void validParam(Object o){

        log.info("验证原文参数是否合法");

        // 执行参数验证
        Set<ConstraintViolation<Object>> constraintViolationSet = applicationContext.getBean(Validator.class).validate(o);

        if (!constraintViolationSet.isEmpty()) {

            Map<String,String> errMsgMap = new HashMap();

            // 验证对象
            for (ConstraintViolation violation: constraintViolationSet) {

                // 获取参数名
                String fieldName = violation.getPropertyPath().toString();

                StringBuilder sb = new StringBuilder();

                if(errMsgMap.containsKey(fieldName)) {
                    sb.append(errMsgMap.get(fieldName)).append(" 且 ");
                } else {
                    sb.append("参数" + fieldName);
                }

                sb.append(violation.getMessage());

                errMsgMap.put(fieldName,sb.toString());
            }

            throw new SecretCatException(errMsgMap.values().stream().collect(Collectors.joining(",")));
        }
    }

    /**
     * 加密响应参数
     * @param aeskey
     * @param proceedResult
     * @param pongEncryptField
     * @throws IllegalAccessException
     */
    private void encryptResult(byte[] aeskey,Object proceedResult,String pongEncryptField) throws IllegalAccessException {

        log.info("加密响应参数");

        Field dataField = ReflectionUtils.findField(proceedResult.getClass(),pongEncryptField);

        if(dataField!=null) {

            dataField.setAccessible(true);

            Object dataFieldValue = dataField.get(proceedResult);

            if(dataFieldValue!=null) {

                dataField.set(proceedResult, dataEncryptService.encryptData(aeskey, dataFieldValue));
            }
        }
    }

}