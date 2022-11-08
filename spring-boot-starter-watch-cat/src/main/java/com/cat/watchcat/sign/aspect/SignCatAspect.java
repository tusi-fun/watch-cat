package com.cat.watchcat.sign.aspect;

import com.cat.watchcat.sign.annotation.SignCat;
import com.cat.watchcat.sign.service.SignKeyEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;

/**
 * 请求验签、响应加签控制切面
 *
 * @author hudongshan
 * @version 20200227
 *
 * 参考：https://blog.csdn.net/qq_15076569/article/details/100923074
 *
 */
@Slf4j
@Aspect
@Order(-100)
@Component
public class SignCatAspect {

    @Pointcut("@annotation(signCat)")
    public void pointCut(SignCat signCat) {}

    @Around("pointCut(signCat)")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint, SignCat signCat) throws Throwable {

        log.info("-> SignCatAspect");

        Object proceed = proceedingJoinPoint.proceed();

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        // 获取请求参数
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();
//        Map<String,String> signDataMap = Maps.newHashMap(request.getQueryParams().toSingleValueMap());
        Map<String,String[]> signDataMap = Maps.newHashMap(request.getParameterMap());

        // 附加签名参数中的 method
        signDataMap.put(SignKeyEnum.METHOD_KEY.value,new String[]{request.getMethod()});

        // 附加签名参数中的 path
        signDataMap.put(SignKeyEnum.PATH_KEY.value,new String[]{request.getServletPath()});

        Enumeration<String> headerNames = request.getHeaderNames();

        if(!Objects.equals(request.getMethod(), HttpMethod.GET) && !Objects.equals(request.getMethod(), HttpMethod.DELETE)) {

        }

        log.info("SignCatAspect:签名 signDataMap -> {}",signDataMap);

//        requestInfo.setRequestHeaders(getRequestHeaders(request));
//        requestInfo.setClassMethod(String.format("%s.%s", methodSignature.getDeclaringTypeName(), methodSignature.getName()));
//        requestInfo.setRequestParams(getRequestParamsByProceedingJoinPoint(proceedingJoinPoint));
//        requestInfo.setResult(proceed);
//        requestInfo.setEndTime(LocalDateTime.now());
//        requestInfo.setTimeCost(Duration.between(requestInfo.getStartTime(),requestInfo.getEndTime()).toMillis());

        log.info("SignCatAspect ->");

        return proceed;
    }



}