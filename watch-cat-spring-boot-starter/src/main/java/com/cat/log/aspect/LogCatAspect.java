package com.cat.log.aspect;

import com.cat.common.RequestInfo;
import com.cat.log.annotation.LogCat;
import com.cat.log.event.LogCatService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 日志记录切面，对 Controller 层请求、响应日志输出、持久化（使用 Json 格式输出，解决在大量请求时日志输出时序混乱的问题）
 * @author hudongshan
 * @version 20210608
 */
@Slf4j
@Order(-100)
@Aspect
public class LogCatAspect {

    private final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private final ThreadLocal<String> requestId = new ThreadLocal<>();

    private static final String originReqFormat = "\r\n[Req OrigParams] %s";
    private static final String logFormat =
            "\r\n-----<%s>-----" +
            "\r\n[Req Info      ] %s > %s > %s" +
            "\r\n[Req Headers   ] %s" + "%s" +
            "\r\n[Req Params    ] %s" +
            "\r\n[Resp          ] %s" +
            "\r\n[Time Cost     ] %sms（Start:%s ~ End:%s）" +
            "\r\n-----<%s>-----";

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

        log.info("<LogCat doAround in>");

        // 20230309 System.currentTimeMillis() 方法的性能比 LocalDateTime.now() 方法要快大约 50 倍。但这个结果并不是绝对的，具体的性能差距会因环境和实现而异
        startTime.set(System.currentTimeMillis());

        requestId.set(UUID.randomUUID().toString().replaceAll("-",""));

        // 执行业务方法
        Object retult = proceedingJoinPoint.proceed();

        buildLog(proceedingJoinPoint, logCat,false, retult);

        log.info("<LogCat doAround out>");

        return retult;
    }

    /**
     * 响应异常日志打印
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "pointCut(logCat)", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, LogCat logCat, RuntimeException e) {

        buildLog(joinPoint, logCat,true, e);

        log.info("<LogCat doAfterThrow out>");
    }

    /**
     * 构建日志
     */
    private void buildLog(JoinPoint joinPoint, LogCat logCat, boolean isError, Object data) {

        Long endTime = System.currentTimeMillis();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        RequestInfo requestInfo = RequestInfo.builder()
                .startTime(startTime.get())
                .bid(StringUtils.hasText(logCat.bid())?getKey(joinPoint, logCat.bid()):null)
                .actionGroup(logCat.actionGroup())
                .action(logCat.action())
                .ip(request.getRemoteAddr())
                .url(request.getRequestURL().toString())
                .httpMethod(request.getMethod())
                .requestHeaders(getRequestHeaders(request))
                .requestParams(getParameters(joinPoint))
                .classMethod(String.format("%s.%s", methodSignature.getDeclaringTypeName(), methodSignature.getName()))
                .exception(isError?String.valueOf(data):null)
                .result(isError?null:data)
                .endTime(endTime)
                .timeCost(endTime-startTime.get())
                .build();

        if(logCat.print()) {
            log.info(String.format(logFormat,
                    requestId.get(),
                    requestInfo.getIp(),
                    requestInfo.getHttpMethod(),
                    requestInfo.getUrl(),
                    requestInfo.getRequestHeaders(),
                    logCat.printOrig()? String.format(originReqFormat,getParameters(request)):"",
                    requestInfo.getRequestParams(),
                    isError?requestInfo.getException():requestInfo.getResult(),
                    requestInfo.getTimeCost(),requestInfo.getStartTime(),requestInfo.getEndTime(),
                    requestId.get()));
        }

        if(logCat.callback()) {

            LogCatService logCatService;

            try {

                logCatService = applicationContext.getBean(LogCatService.class);
                logCatService.callback(requestInfo);

            } catch (NoSuchBeanDefinitionException e) {
                log.error("未找到 {} 接口的实现类", LogCatService.class.getName());
            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }
    }

    /**
     * 获取请求原始参数（获取所有参数）
     * @return
     */
    private Map<String, Object> getParameters(HttpServletRequest request) {

        Map<String, Object> params = new HashMap<>();

        Enumeration<String> paramNames = request.getParameterNames();

        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                params.put(paramName, paramValues[0]);
            } else {
                params.put(paramName, Arrays.asList(paramValues));
            }
        }

        return params;
    }

    /**
     * 获取请求参数（只能获取到方法定义的参数）
     * @param joinPoint
     * @return
     */
    private Map<String, Object> getParameters(JoinPoint joinPoint) {

        String[] parameterNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();

        Object[] args = joinPoint.getArgs();

        Map<String, Object> requestParams = new HashMap<>();

        for (int i = 0; i < args.length; i++) {

            Object arg = args[i];

            if(arg instanceof MultipartFile) {
                MultipartFile file = (MultipartFile)arg;
                requestParams.put(parameterNames[i],String.format("%s(%d bytes)",file.getOriginalFilename(),file.getSize()));
            } else {
                requestParams.put(parameterNames[i],arg);
            }
        }
        return requestParams;
    }

    /**
     * 获取请求头参数
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

    /**
     * 获取 bid 的值
     * @param joinPoint
     * @param key
     * @return
     */
    private String getKey(JoinPoint joinPoint, String key) {

        // 获取方法签名(通过此签名获取目标方法信息)
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();

        // 获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer localVariableTable = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = localVariableTable.getParameterNames(methodSignature.getMethod());

        // 使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();

        // SPEL上下文 使用 StandardEvaluationContext 有注入的隐患， SimpleEvaluationContext 比较安全
        EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        // 把方法参数放入SPEL上下文中
        for(int i=0; i<paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }

        String fullKey = parser.parseExpression(key).getValue(context, String.class);
        if(!StringUtils.hasText(fullKey)) {
            return null;
        }

        return parser.parseExpression(key).getValue(context, String.class);
    }

}
