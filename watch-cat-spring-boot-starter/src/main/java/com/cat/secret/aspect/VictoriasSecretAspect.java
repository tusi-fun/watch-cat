package com.cat.secret.aspect;//package com.cat.watchcat.secret.aspect;
//
//import service.secret.com.cat.DataEncryptService;
//import service.secret.com.cat.SecretCatException;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.util.ReflectionUtils;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.ConstraintViolation;
//import javax.validation.Validator;
//import java.lang.reflect.Field;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * 请求解密、响应加密控制切面
// *
// * @author hudongshan
// * @version 20200227
// *
// * 参考：https://blog.csdn.net/qq_15076569/article/details/100923074
// *
// */
//@Slf4j
//@Aspect
//@Order(-98)
//@Component
//public class VictoriasSecretAspect {
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @Autowired
//    private DataEncryptService dataEncryptService;
//
//    @Pointcut("@args(com.cat.watchcat.secret.annotation.VictoriasSecret)")
//    public void pointCut() {}
//
//    @Around("pointCut()")
//    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//
//        log.info("-> VictoriasSecretAspect");
//
//        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
//        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
//        HttpServletRequest request = sra.getRequest();
//
//
//
//        // 将解密后的参数传递给方法
//        Object[] args = proceedingJoinPoint.getArgs();
//
//
//        Object proceedResult = proceedingJoinPoint.proceed(args);
//
//        // 返回值是否需要加密
////        if(secretCat.encryptedPong()) {
////            encryptResult(aeskey,proceedResult,secretCat.pongEncryptField());
////        }
//
//        log.info("VictoriasSecretAspect ->");
//
////     * 反射修改属性
////     * https://blog.csdn.net/u011402896/article/details/79550913
//        return proceedResult;
//    }
//
//    /**
//     * 请求原文参数验证
//     * @param o
//     */
//    private void validParam(Object o) {
//
//        // 执行参数验证
//        Set<ConstraintViolation<Object>> constraintViolationSet = applicationContext.getBean(Validator.class).validate(o);
//
//        if (!constraintViolationSet.isEmpty()) {
//
//            Map<String,String> errMsgMap = new HashMap();
//
//            // 验证对象
//            for (ConstraintViolation violation: constraintViolationSet) {
//
//                // 获取参数名
//                String fieldName = violation.getPropertyPath().toString();
//
//                StringBuilder sb = new StringBuilder();
//
//                if(errMsgMap.containsKey(fieldName)) {
//                    sb.append(errMsgMap.get(fieldName)).append(" 且 ");
//                } else {
//                    sb.append("参数" + fieldName);
//                }
//
//                sb.append(violation.getMessage());
//
//                errMsgMap.put(fieldName,sb.toString());
//            }
//
//            throw new SecretCatException(errMsgMap.values().stream().collect(Collectors.joining(",")));
//        }
//    }
//
//    /**
//     * 加密响应参数
//     * @param aeskey
//     * @param proceedResult
//     * @param pongEncryptField
//     * @throws IllegalAccessException
//     */
//    private void encryptResult(byte[] aeskey,Object proceedResult,String pongEncryptField) throws IllegalAccessException {
//
//        Field dataField = ReflectionUtils.findField(proceedResult.getClass(),pongEncryptField);
//
//        if(dataField!=null) {
//
//            dataField.setAccessible(true);
//
//            Object dataFieldValue = dataField.get(proceedResult);
//
//            if(dataFieldValue!=null) {
//
//                dataField.set(proceedResult, dataEncryptService.encryptData(aeskey, dataFieldValue));
//            }
//        }
//    }
//
//}