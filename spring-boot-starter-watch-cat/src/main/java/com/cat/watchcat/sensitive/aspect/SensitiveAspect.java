package com.cat.watchcat.sensitive.aspect;

import com.cat.watchcat.sensitive.annotation.SensitiveCat;
import com.cat.watchcat.sensitive.utils.DesensitizedUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * 响应参数脱敏
 * @author yangxiujun
 */
@Slf4j
@Aspect
@Component
public class SensitiveAspect {

    @Pointcut("@annotation(sensitiveCat)")
    public void pointCut(SensitiveCat sensitiveCat) {}

    /**
     * 响应参数脱敏
     * @param sensitiveCat
     * @param result
     * @return
     * @throws Throwable
     */
    @AfterReturning(value = "pointCut(sensitiveCat)", returning = "result")
    public Object afterReturning(SensitiveCat sensitiveCat, Object result) throws IllegalAccessException {

        // 获取响应对象中的 业务对象
        Field dataField = ReflectionUtils.findField(result.getClass(), sensitiveCat.field());

        if(dataField!=null) {

            dataField.setAccessible(true);

            Object dataObject = dataField.get(result);

            if(dataObject!=null) {
                dataField.set(result,DesensitizedUtils.dataValue(dataObject));
            }
        }

        return result;
    }
}