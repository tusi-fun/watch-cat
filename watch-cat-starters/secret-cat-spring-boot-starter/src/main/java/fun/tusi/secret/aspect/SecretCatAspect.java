package fun.tusi.secret.aspect;

import fun.tusi.secret.annotation.SecretCat;
import fun.tusi.secret.service.DataEncryptService;
import fun.tusi.secret.service.SecretCatException;
import fun.tusi.secret.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 请求解密、响应加密控制切面
 *
 * @author xy783
 *
 * 参考：https://blog.csdn.net/qq_15076569/article/details/100923074
 *
 */
@Slf4j
@Aspect
@Order(-98)
//@Component
public class SecretCatAspect {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataEncryptService dataEncryptService;

    @Pointcut("@annotation(secretCat)")
    public void pointCut(SecretCat secretCat) {}

    @Around("pointCut(secretCat)")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint, SecretCat secretCat) throws Throwable {

        log.info("-> SecretCatAspect");

        MethodSignature methodSignature = ((MethodSignature)proceedingJoinPoint.getSignature());
        Parameter[] parameters = methodSignature.getMethod().getParameters();

        Parameter plaintextParameter = Arrays.stream(parameters).filter(p -> p.getName().equals(secretCat.plaintextParameter())).findFirst()
                .orElseThrow(() -> new SecretCatException("解密结果接收参数 plaintextParameter 未设置"));

        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();

        String encryptKey = request.getParameter(secretCat.encryptPingKeyField()),
               encryptData = request.getParameter(secretCat.encryptPingDataField());

        String reqMsg =
                "\r\n-----------------------" +
                "\r\n【@SecretCat  】:" + secretCat +
                "\r\n【encryptKey  】:" + encryptKey +
                "\r\n【encryptData 】:" + encryptData;

        log.info(reqMsg);

        if (!StringUtils.hasText(encryptKey)) {
            throw new SecretCatException(secretCat.encryptPingKeyField()+"不能为空");
        }

        if (!StringUtils.hasText(encryptData)) {
            throw new SecretCatException(secretCat.encryptPingDataField()+"不能为空");
        }

        // 解密参数和值
        byte[] aeskey = dataEncryptService.decryptAesKey(encryptKey);

        String data = dataEncryptService.decryptData(aeskey,encryptData);

        log.info("\r\n【plainText   】:" + data + "\r\n-----------------------");

        // 将解密后的参数传递给方法
        Object[] args = proceedingJoinPoint.getArgs();

        Object targetObj = Arrays.stream(args).filter(arg -> plaintextParameter.getType().isInstance(arg)).findFirst()
                .orElseThrow(() -> new SecretCatException("解密结果接收参数 plaintextParameter 未设置"));

        // 将解密后的结果 copy 到指定对象
        BeanUtils.copyProperties(JsonUtils.toBean(data, targetObj.getClass()),targetObj);

        // 是否启用参数验证
        if(secretCat.verifyPlaintext()) {
            // 执行参数验证
            validParam(targetObj);
        }

        Object proceedResult = proceedingJoinPoint.proceed(args);

        // 响应加密
        if(secretCat.encryptPong()) {
            encryptResult(aeskey,proceedResult,secretCat.encryptPongField());
        }

        log.info("SecretCatAspect ->");

//     * 反射修改属性
//     * https://blog.csdn.net/u011402896/article/details/79550913
        return proceedResult;
    }

    /**
     * 请求原文参数验证
     * @param o
     */
    private void validParam(Object o) {

        // 执行参数验证
        Set<ConstraintViolation<Object>> constraintViolationSet = applicationContext.getBean(Validator.class).validate(o);

        if (!constraintViolationSet.isEmpty()) {

            Map<String,String> errMsgMap = new HashMap<>();

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

            throw new SecretCatException(String.join(",", errMsgMap.values()));
        }
    }

    /**
     * 加密响应参数
     * @param aeskey
     * @param proceedResult
     * @param pongEncryptField
     * @throws IllegalAccessException
     */
    private void encryptResult(byte[] aeskey, Object proceedResult, String pongEncryptField) throws IllegalAccessException {

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