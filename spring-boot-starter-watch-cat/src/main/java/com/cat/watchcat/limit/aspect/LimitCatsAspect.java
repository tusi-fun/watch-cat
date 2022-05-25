package com.cat.watchcat.limit.aspect;

import com.cat.watchcat.limit.annotation.LimitCat;
import com.cat.watchcat.limit.annotation.LimitCats;
import com.cat.watchcat.limit.service.LimitCatException;
import com.cat.watchcat.limit.service.LimitCatService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 日志记录切面，对 Controller 层请求、响应日志输出、持久化（使用 Json 格式输出，解决在大量请求时日志输出时序混乱的问题）
 * @author hudongshan
 * @version 20210608
 */
@Slf4j
@Order(-99)
@Aspect
public class LimitCatsAspect {

    @Autowired
    private LimitCatService limitCatService;

    // 20220514 切换为  @annotation(com.cat.watchcat.limit.annotation.LimitCats) 方式获取，用于兼容 单个 和 多个 @LimitCat一起使用的场景
    // @Pointcut("@annotation(limitCats)")
    // public void pointCut1(LimitCats limitCats) {}

    @Pointcut("@annotation(com.cat.watchcat.limit.annotation.LimitCats) || @annotation(com.cat.watchcat.limit.annotation.LimitCat)")
    public void pointCut() {}

    /**
     * 响应正常
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        log.info("LimitCatsAspect -> doAround");

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        Method method = methodSignature.getMethod();

        LimitCats limitCats = method.getAnnotation(LimitCats.class);

        if(limitCats!=null) {
            Arrays.stream(limitCats.value()).forEach(item -> {
                check(proceedingJoinPoint,item);
            });
        } else {
            LimitCat limitCat = method.getAnnotation(LimitCat.class);
            check(proceedingJoinPoint,limitCat);
        }

        Object proceed = proceedingJoinPoint.proceed();

        if(limitCats!=null) {
            Arrays.stream(limitCats.value()).forEach(item -> {
                update(proceedingJoinPoint,item);
            });
        } else {
            LimitCat limitCat = method.getAnnotation(LimitCat.class);
            update(proceedingJoinPoint,limitCat);
        }

        return proceed;
    }

    /**
     * 响应异常
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, RuntimeException e) {

        log.info("LimitCatsAspect -> doAfterThrow");

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Method method = methodSignature.getMethod();

        LimitCats limitCats = method.getAnnotation(LimitCats.class);

        if(limitCats!=null) {
            Arrays.stream(limitCats.value()).forEach(item -> {
                log.info("e.getMessage() = {}, e.getClass() = {}, triggerFor = {},triggerForCode = {}", e.getMessage(),e.getClass(),item.triggerFor(),item.triggerForCode());
                // 根据异常类型来判断是否触发计数
                if(instanceofOneof(item,e)) {
                    update(joinPoint, item);
                }
            });
        } else {
            LimitCat limitCat = method.getAnnotation(LimitCat.class);
            log.info("e.getMessage() = {}, e.getClass() = {}, triggerFor = {},triggerForCode = {}", e.getMessage(),e.getClass(),limitCat.triggerFor(),limitCat.triggerForCode());
            // 根据异常类型来判断是否触发计数
            if(instanceofOneof(limitCat,e)) {
                update(joinPoint, limitCat);
            }
        }
    }

    private void check(JoinPoint joinPoint,LimitCat limitCat) {
        log.info("limitCat:check -> {}",limitCat.scene());

        String scene = StringUtils.hasText(limitCat.scene())?limitCat.scene():joinPoint.getSignature().getName();
        String key = getKey(joinPoint,scene,limitCat.key());

        // 调用频率验证
        limitCatService.checkFrequency(scene,key,limitCat);
    }

    private void update(JoinPoint joinPoint,LimitCat limitCat) {
        log.info("limitCat:update -> {}",limitCat.scene());

        String scene = StringUtils.hasText(limitCat.scene())?limitCat.scene():joinPoint.getSignature().getName();
        String key = getKey(joinPoint,scene,limitCat.key());

        // 更新调用频率
        limitCatService.updateFrequency(scene,key,limitCat.rules());
    }

    /**
     * 判断 异常 和 异常中指定参数 是否满足
     * @param limitCat
     * @param e
     * @return
     */
    private boolean instanceofOneof(LimitCat limitCat, RuntimeException e) {

        Class<? extends RuntimeException>[] triggerFor = limitCat.triggerFor();

        if(triggerFor.length==0) {
            return false;
        }

        List<String> codes = Arrays.asList(limitCat.triggerForCode());

        // 获取异常的属性值
        Method method = ReflectionUtils.findMethod(e.getClass(),limitCat.triggerForCodeField());

        Object codeObj = null;
        if(method!=null) {
            codeObj = ReflectionUtils.invokeMethod(method, e);
        }

        for (Class<? extends RuntimeException> eClass : triggerFor) {

            if(eClass.isInstance(e)) {

                // triggerForCode 不设置时，只检查 Exception 是否满足
                if(codes.isEmpty()) {

                    return true;

                } else {

                    if(codeObj==null) {
                        return false;
                    }

                    if(codes.contains(String.valueOf(codeObj))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取频率限制场景 key
     * @param joinPoint
     * @param key
     * @return
     */
    private String getKey(JoinPoint joinPoint,String scene,String key) {

        // TODO key 默认为客户端 ip + userAgent 是否合理？
        // TODO 对于周期内频率限制的场景，考虑使用 redis 临牌桶？是否合理？

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

        //把方法参数放入SPEL上下文中
        for(int i=0;i<paraNameArr.length;i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }

        String fullKey = parser.parseExpression(key).getValue(context, String.class);
        if(!StringUtils.hasText(fullKey)) {
            throw new LimitCatException("频率限制场景 "+ scene +" 的key "+ key +" 不能为空");
        }

        return parser.parseExpression(key).getValue(context, String.class);
    }
}