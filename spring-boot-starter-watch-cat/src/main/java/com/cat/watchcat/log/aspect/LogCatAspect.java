package com.cat.watchcat.log.aspect;

import com.cat.common.RequestInfo;
import com.cat.util.JsonUtils;
import com.cat.watchcat.log.annotation.LogCat;
import com.cat.watchcat.log.event.LogCatEvent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 日志记录切面，对 Controller 层请求、响应日志输出、持久化（使用 Json 格式输出，解决在大量请求时日志输出时序混乱的问题）
 * @author hudongshan
 * @version 20210608
 */
@Slf4j
@Order(-100)
@Aspect
@Component
public class LogCatAspect {

    private final NamedThreadLocal<LocalDateTime> startTimeTL = new NamedThreadLocal<>("StartTime");
    private final NamedThreadLocal<String> requestIdTL = new NamedThreadLocal<>("RequestId");
    private static final String logFormat =
            "\r\n---------< %s >---------" +
            "\r\n【Request URI    】:%s > %s > %s" +
            "\r\n【Request Headers】:%s" +
            "\r\n【Request body   】:%s" +
            "\r\n【Result         】:%s" +
            "\r\n【Time Cost      】:%s ms" +
            "\r\n---------< %s >---------";

    @Autowired
    private ApplicationContext applicationContext;

    @Pointcut("@annotation(logCat)")
    public void pointCut(LogCat logCat) {}

    /**
     * 响应正常日志打印
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("pointCut(logCat)")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint, LogCat logCat) throws Throwable {

        log.info("---------------------< LogCat doAround in  >---------------------");

        startTimeTL.set(LocalDateTime.now());

        requestIdTL.set(UUID.randomUUID().toString().replaceAll("-",""));

        // 执行业务方法
        Object proceed = proceedingJoinPoint.proceed();

        LocalDateTime endTime = LocalDateTime.now();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        RequestInfo requestInfo = new RequestInfo();
        if(StringUtils.hasText(logCat.bid())) {
            requestInfo.setBid(getKey(proceedingJoinPoint, logCat.bid()));
        }
        requestInfo.setStartTime(startTimeTL.get());
        requestInfo.setActionGroup(logCat.actionGroup());
        requestInfo.setAction(logCat.action());
        requestInfo.setIp(request.getRemoteAddr());
        requestInfo.setUrl(request.getRequestURL().toString());
        requestInfo.setHttpMethod(request.getMethod());
        requestInfo.setRequestHeaders(getRequestHeaders(request));
        requestInfo.setClassMethod(String.format("%s.%s", methodSignature.getDeclaringTypeName(), methodSignature.getName()));
        requestInfo.setRequestParams(getRequestParamsByJoinPoint(proceedingJoinPoint));
        requestInfo.setResult(proceed);
        requestInfo.setEndTime(endTime);
        requestInfo.setTimeCost(Duration.between(startTimeTL.get(),endTime).toMillis());

        if(logCat.print()) {
            log.info(String.format(logFormat,requestIdTL.get(),
                    requestInfo.getIp(), requestInfo.getHttpMethod(), requestInfo.getUrl(), requestInfo.getRequestHeaders(),
                    requestInfo.getRequestParams(),JsonUtils.toJson(requestInfo.getResult()), requestInfo.getTimeCost(),requestIdTL.get()));
        }

        if(logCat.enableEvent()) {
            // 产生记录日志事件
            applicationContext.publishEvent(new LogCatEvent(this,requestInfo));
        }

        log.info("---------------------< LogCat doAround out >---------------------");

        return proceed;
    }

    /**
     * 响应异常日志打印
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "pointCut(logCat)", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, RuntimeException e, LogCat logCat) {

        LocalDateTime endTime = LocalDateTime.now();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        RequestInfo requestInfo = new RequestInfo();
        if(StringUtils.hasText(logCat.bid())) {
            requestInfo.setBid(getKey(joinPoint, logCat.bid()));
        }
        requestInfo.setStartTime(startTimeTL.get());
        requestInfo.setActionGroup(logCat.actionGroup());
        requestInfo.setAction(logCat.action());
        requestInfo.setIp(request.getRemoteAddr());
        requestInfo.setUrl(request.getRequestURL().toString());
        requestInfo.setHttpMethod(request.getMethod());
        requestInfo.setRequestHeaders(getRequestHeaders(request));
        requestInfo.setClassMethod(String.format("%s.%s", methodSignature.getDeclaringTypeName(), methodSignature.getName()));
        requestInfo.setRequestParams(getRequestParamsByJoinPoint(joinPoint));
        requestInfo.setException(e.getMessage());
        requestInfo.setEndTime(endTime);
        requestInfo.setTimeCost(Duration.between(startTimeTL.get(),endTime).toMillis());

        if(logCat.print()) {
            log.info(String.format(logFormat,requestIdTL.get(),
                    requestInfo.getIp(), requestInfo.getHttpMethod(), requestInfo.getUrl(), requestInfo.getRequestHeaders(),
                    requestInfo.getRequestParams(),requestInfo.getException(), requestInfo.getTimeCost(),requestIdTL.get()));
        }

        if(logCat.enableEvent()) {
            // 产生记录日志事件
            applicationContext.publishEvent(new LogCatEvent(this,requestInfo));
        }

        log.info("---------------------< LogCat doAround out >---------------------");
    }

    /**
     * 获取请求参数
     * @param joinPoint
     * @return
     */
    private Map<String, Object> getRequestParamsByJoinPoint(JoinPoint joinPoint) {

        String[] paramNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();

        Object[] paramValues = joinPoint.getArgs();

        return buildRequestParam(paramNames, paramValues);
    }

    /**
     * 获取请求头
     * @param request
     * @return
     */
    private Map<String, Object> getRequestHeaders(HttpServletRequest request) {

        Map<String, Object> requestHeadersParams = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {

            String name = headerNames.nextElement();

            Enumeration<String> headerValues = request.getHeaders(name);

            StringBuilder sb = new StringBuilder();

            while (headerValues.hasMoreElements()) {
                sb.append(headerValues.nextElement());
            }
            requestHeadersParams.put(name,sb.toString());
        }
        return requestHeadersParams;
    }

    private Map<String, Object> buildRequestParam(String[] paramNames, Object[] paramValues) {

        Map<String, Object> requestParams = new HashMap<>();

        for (int i = 0; i < paramNames.length; i++) {

            Object value = paramValues[i];

            //如果是文件对象，获取文件名
            if (value instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) value;
                value = file.getOriginalFilename();
            }
            requestParams.put(paramNames[i], value);
        }
        return requestParams;
    }

    /**
     * 获取 bid 的值
     * @param joinPoint
     * @param key
     * @return
     */
    private String getKey(JoinPoint joinPoint, String key) {

        // 获取方法签名(通过此签名获取目标方法信息)
        MethodSignature ms = (MethodSignature)joinPoint.getSignature();
        Method method = ms.getMethod();
        Object[] args = joinPoint.getArgs();

        // 获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer localVariableTable = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = localVariableTable.getParameterNames(method);

        // 使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();

        // SPEL上下文 使用 StandardEvaluationContext 有注入的隐患， SimpleEvaluationContext 比较安全
//        SimpleEvaluationContext context = new SimpleEvaluationContext.Builder().build();
        EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        // 把方法参数放入SPEL上下文中
        for(int i=0;i<paraNameArr.length;i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }

        String fullKey = parser.parseExpression(key).getValue(context, String.class);
        if(!StringUtils.hasText(fullKey)) {
            return null;
        }

        return parser.parseExpression(key).getValue(context, String.class);
    }

}
