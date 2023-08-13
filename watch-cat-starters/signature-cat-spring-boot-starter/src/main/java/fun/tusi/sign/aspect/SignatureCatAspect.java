package fun.tusi.sign.aspect;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.ServletUtil;
import fun.tusi.sign.annotation.SignatureCat;
import fun.tusi.sign.config.SignatureCatProperties;
import fun.tusi.sign.service.AppService;
import fun.tusi.sign.service.SignCommonService;
import fun.tusi.sign.service.SignatureCatException;
import fun.tusi.sign.util.ApiSignUtils4Sha;
import fun.tusi.sign.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 请求验签、响应加签控制切面
 * @author xy783
 *
 * 参考：https://blog.csdn.net/qq_15076569/article/details/100923074
 *
 */
@Slf4j
@Aspect
@Order(-100)
public class SignatureCatAspect {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    Validator validator;

    @Autowired
    SignCommonService signCommonService;

    @Autowired
    SignatureCatProperties signatureCatProperties;

    @Pointcut("@annotation(signatureCat)")
    public void pointCut(SignatureCat signatureCat) {}

    @Around("pointCut(signatureCat)")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint, SignatureCat signatureCat) throws Throwable {

        log.info("<SignatureCat doAround in>");

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        if(!StringUtils.hasText(request.getContentType())) {
            throw new SignatureCatException("签名验证失败，ContentType 获取失败");
        }

        MediaType mediaType;

        try {
            mediaType = MediaType.parseMediaType(request.getContentType());
        } catch (InvalidMediaTypeException e) {
            throw new SignatureCatException("签名验证失败，ContentType = "+request.getContentType()+"，解析失败");
        }

        // 获取签名元参数（appid、nonce、timestamp、sign）
        String appId = request.getHeader(ApiSignUtils4Sha.APPID_KEY),
               nonce = request.getHeader(ApiSignUtils4Sha.NONCE_KEY),
               timestamp = request.getHeader(ApiSignUtils4Sha.TIMESTAMP_KEY),
               sign = request.getHeader(ApiSignUtils4Sha.SIGN_KEY),
               method = request.getMethod(),
               path = request.getServletPath();

        // 验证 appId、nonce、timestamp、sign 是否传递
        if(!(StringUtils.hasText(appId) && StringUtils.hasText(nonce) && StringUtils.hasText(timestamp) && StringUtils.hasText(sign))) {
            throw new SignatureCatException("签名验证失败，签名元参数（"+ApiSignUtils4Sha.APPID_KEY+"、"+ApiSignUtils4Sha.NONCE_KEY+"、"+ApiSignUtils4Sha.TIMESTAMP_KEY+"、"+ApiSignUtils4Sha.SIGN_KEY+"）获取失败");
        }

        // 验证 timestamp 是否在宽容时间内
        if(!signCommonService.checkTimestampTolerant(timestamp, signatureCatProperties.getDigest().getTolerant())) {
            throw new SignatureCatException("签名验证失败，" + ApiSignUtils4Sha.TIMESTAMP_KEY + " 无效");
        }

        // 验证 sign 在一定周期内是否已经使用
        if(!signCommonService.checkSign(sign, signatureCatProperties.getDigest().getTolerant())) {
            throw new SignatureCatException("签名验证失败，" + ApiSignUtils4Sha.SIGN_KEY + " 无效");
        }

        // 获取业务参数
        Map<String, String> signDataMap = request.getParameterMap().entrySet().stream().collect(
                Collectors.toMap(
                        item -> item.getKey(),
                        item -> item.getValue()!=null && item.getValue().length>0 ? item.getValue()[0] : ""
                )
        );

        // 填充元参数
        signDataMap.put(ApiSignUtils4Sha.APPID_KEY, appId);
        signDataMap.put(ApiSignUtils4Sha.METHOD_KEY, method);
        signDataMap.put(ApiSignUtils4Sha.PATH_KEY, path);
        signDataMap.put(ApiSignUtils4Sha.NONCE_KEY, nonce);
        signDataMap.put(ApiSignUtils4Sha.TIMESTAMP_KEY, timestamp);

        // application/json 方式，获取 jsonContentMd5
        if(mediaType.includes(MediaType.APPLICATION_JSON)) {
            String jsonContentMd5 = checkJson(request, proceedingJoinPoint.getArgs(), signatureCat.jsonTarget());
            signDataMap.put(ApiSignUtils4Sha.CONTENT_MD5_KEY, jsonContentMd5);
        }

        log.info("[验签]signDataMap = {}",signDataMap);

        // 获取 secret
        String appSecret = signatureCatProperties.getDigest().getApps().get(appId);

        if(!StringUtils.hasText(appSecret)) {

            AppService appService = null;

            try {
                appService = applicationContext.getBean(AppService.class);
            } catch (NoSuchBeanDefinitionException e) {
                throw new SignatureCatException("签名验证失败，未提供 "+ AppService.class.getName() +" 接口实现类");
            }

            appSecret = appService.getAppSecret(appId);
        }

        if(!StringUtils.hasText(appSecret)) {
            throw new SignatureCatException("签名验证失败，" + ApiSignUtils4Sha.SECRET_KEY+"获取失败（请使用 配置文件 或 实现AppService接口 的方式配置）");
        }

        Boolean isPassed = ApiSignUtils4Sha.verify(appSecret, signatureCatProperties.getDigest().getAlgorithm(), sign, signDataMap);

        if(!isPassed) {
            throw new SignatureCatException("签名验证失败，" + ApiSignUtils4Sha.SIGN_KEY + " 验证失败");
        }

        log.info("<SignatureCat doAround out>");

        return proceedingJoinPoint.proceed();
    }

    /**
     * 验证 json 提交（非json请求，返空字符串）
     * @param request
     * @return
     */
    private String checkJson(HttpServletRequest request, Object[] args, Class jsonTarget) {

        String outJsonContentMd5 = request.getHeader(ApiSignUtils4Sha.CONTENT_MD5_KEY);

        // 验证提交Json内容时，是否传递 outJsonContentMd5
        if (!StringUtils.hasText(outJsonContentMd5)) {
            throw new SignatureCatException("签名验证失败，Content-Type=application/json 时，需传递 " + ApiSignUtils4Sha.CONTENT_MD5_KEY + " 参数");
        }

        // 获取json原文
        String jsonData = ServletUtil.getBody(request);

        log.info("[验签]jsonData = \n{}", jsonData);

        String jsonContentMd5 = SecureUtil.md5(jsonData);

        log.info("[验签]outJsonContentMd5 = {}", jsonContentMd5);

        // 验证json原文MD5 和 提交的contentMd5是否一致
        if (!jsonContentMd5.equals(outJsonContentMd5)) {

            log.warn("[验签]md5不一致，jsonContentMd5 = {},outJsonContentMd5 = {}", jsonContentMd5, outJsonContentMd5);

            throw new SignatureCatException("签名验证失败，" + ApiSignUtils4Sha.CONTENT_MD5_KEY + " 和提交内容的md5值不一致");
        }

        // 验证 Json 参数
        for (int i=0;i<args.length;i++) {

            Object o = args[i];

            if(o != null && jsonTarget.isInstance(o)) {

                BeanUtils.copyProperties(JsonUtils.toBean(jsonData, o.getClass()),o);

                args[i] = o;

                // 验证参数
                Set<ConstraintViolation<Object>> constraintViolationSet = validator.validate(o);
                if (!constraintViolationSet.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (ConstraintViolation violation: constraintViolationSet) {
                        sb.append(violation.getPropertyPath().toString());
                        sb.append(violation.getMessage());
                    }
                    throw new SignatureCatException(sb.toString());
                }
            }
        }

        return outJsonContentMd5;

    }

//    /**
//     * 获取请求参数
//     * @param proceedingJoinPoint
//     *
//     * @return
//     * */
//    private Map<String, Object> getRequestParams(ProceedingJoinPoint proceedingJoinPoint) {
//
//        //参数名
//        String[] paramNames = ((MethodSignature)proceedingJoinPoint.getSignature()).getParameterNames();
//
//        //参数值
//        Object[] paramValues = proceedingJoinPoint.getArgs();
//
//        return buildRequestParam(paramNames, paramValues);
//    }
//
//    /**
//     * 获取请求头
//     * @param request
//     * @return
//     */
//    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
//
//        Map<String, String> requestHeadersParams = new HashMap<>();
//
//        Enumeration<String> headerNames = request.getHeaderNames();
//
//        while (headerNames.hasMoreElements()) {
//
//            String name = headerNames.nextElement();
//
//            Enumeration<String> headerValues = request.getHeaders(name);
//
//            StringBuilder sb = new StringBuilder();
//
//            while (headerValues.hasMoreElements()) {
//                sb.append(headerValues.nextElement());
//            }
//            requestHeadersParams.put(name,sb.toString());
//        }
//        return requestHeadersParams;
//    }
//
//    private Map<String, Object> buildRequestParam(String[] paramNames, Object[] paramValues) {
//
//        Map<String, Object> requestParams = new HashMap<>();
//
//        for (int i = 0; i < paramNames.length; i++) {
//
//            Object value = paramValues[i];
//
//            //如果是文件对象，获取文件名
//            if (value instanceof MultipartFile) {
//                MultipartFile file = (MultipartFile) value;
//                value = file.getOriginalFilename();
//            }
//            requestParams.put(paramNames[i], value);
//        }
//        return requestParams;
//    }
}