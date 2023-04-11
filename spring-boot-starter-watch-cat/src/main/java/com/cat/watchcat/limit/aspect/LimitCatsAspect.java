package com.cat.watchcat.limit.aspect;

import com.cat.watchcat.limit.annotation.LimitCat;
import com.cat.watchcat.limit.annotation.LimitCats;
import com.cat.watchcat.limit.service.LimitCatException;
import com.cat.watchcat.limit.service.LimitCatService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 业务频率控制记录切面
 * @author hudongshan
 * @version 20210608
 */
@Slf4j
@Order(-100)
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

        check(proceedingJoinPoint);

        Object proceed = proceedingJoinPoint.proceed();

        update(proceedingJoinPoint,null);

        return proceed;
    }

    /**
     * 响应异常
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, RuntimeException e) {
        update(joinPoint,e);
    }

    /**
     * 检查频率计数
     * @param joinPoint
     */
    private void check(JoinPoint joinPoint) {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Method method = methodSignature.getMethod();

        LimitCats limitCats = method.getAnnotation(LimitCats.class);

        if(limitCats == null) {

            limitCats = new LimitCats() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return LimitCats.class;
                }

                @Override
                public LimitCat[] value() {
                    return new LimitCat[]{method.getAnnotation(LimitCat.class)};
                }
            };
        }

        Arrays.stream(limitCats.value()).forEach(item -> {

            log.info("limitCat:check -> {}",item.scene());

            String scene = StringUtils.hasText(item.scene())?item.scene():methodSignature.getName();
            String key = getKey(joinPoint,scene,item.key());

            // 调用频率验证
            limitCatService.checkFrequency(scene,key,item);

        });

    }

    /**
     * 更新频率计数
     * @param joinPoint
     * @param e
     */
    private void update(JoinPoint joinPoint, RuntimeException e) {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Method method = methodSignature.getMethod();

        LimitCats limitCats = method.getAnnotation(LimitCats.class);

        if(limitCats == null) {

            limitCats = new LimitCats() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return LimitCats.class;
                }

                @Override
                public LimitCat[] value() {
                    return new LimitCat[]{method.getAnnotation(LimitCat.class)};
                }
            };
        }

        Arrays.stream(limitCats.value()).forEach(item -> {
            // 根据异常类型来判断是否触发计数
            // 1、e!=null && instanceofOneof(item,e)      > 异常，且异常类型满足触发条件
            // 2、e==null && item.triggerFor().length==0  > 正常，且未设置 triggerFor
            if((e!=null && instanceofOneof(item,e)) || (e==null && item.triggerFor().length==0)) {
                log.info("limitCat:update -> {}",item.scene());
                String scene = StringUtils.hasText(item.scene())?item.scene():joinPoint.getSignature().getName();
                String key = getKey(joinPoint,scene,item.key());
                limitCatService.updateFrequency(scene,key,item.rules());
            }
        });

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

        Set<String> codes = new HashSet<>(Arrays.asList(limitCat.triggerForCode()));

        Object codeObj = null;

        if(!codes.isEmpty()) {

            // 获取异常的属性值
            Method method = ReflectionUtils.findMethod(e.getClass(), "get"+StringUtils.capitalize(limitCat.triggerForCodeField()));

            if(method != null) {
                codeObj = ReflectionUtils.invokeMethod(method, e);
            }
        }

        for (Class<? extends RuntimeException> eClass : triggerFor) {

            if(eClass.isInstance(e)) {

                // triggerForCode 不设置时，只检查 Exception 是否满足
                if(codes.isEmpty()) {

                    return true;

                } else if(codeObj!=null) {

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
    private String getKey(JoinPoint joinPoint, String scene, String key) {

        // TODO key 默认为客户端 ip + userAgent 是否合理？
        // TODO 对于周期内频率限制的场景，考虑使用 redis 临牌桶？是否合理？

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

        //把方法参数放入SPEL上下文中
        for(int i=0; i<paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }

        String fullKey = parser.parseExpression(key).getValue(context, String.class);

        if(!StringUtils.hasText(fullKey)) {
            throw new LimitCatException("频率限制场景 "+ scene +" 的key "+ key +" 不能为空");
        }

        return fullKey;
    }
}