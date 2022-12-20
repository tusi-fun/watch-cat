package com.cat.watchcat.sign.aspect;

import com.cat.watchcat.area.service.AreaResolverException;
import com.cat.watchcat.sign.annotation.SignCat;
import com.cat.watchcat.sign.service.ApiSignUtils4Sha;
import com.cat.watchcat.sign.service.AppService;
import com.cat.watchcat.sign.service.SignCatException;
import com.cat.watchcat.sign.service.SignKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ApiSignUtils4Sha apiSignUtils4Sha;

    @Pointcut("@annotation(signCat)")
    public void pointCut(SignCat signCat) {}

    @Around("pointCut(signCat)")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint, SignCat signCat) throws Throwable {

        log.info("-> SignCatAspect");

        Object proceed = proceedingJoinPoint.proceed();

//        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 获取请求头
//        Map<String, String> requestHeaders = getRequestHeaders(request);

        String appId = request.getHeader(SignKeyEnum.APPID_KEY.value),
                nonce = request.getHeader(SignKeyEnum.NONCE_KEY.value),
                timestamp = request.getHeader(SignKeyEnum.TIMESTAMP_KEY.value),
                sign = request.getHeader(SignKeyEnum.SIGN_KEY.value);


        // 验证签名元参数是否传递
        if(!(StringUtils.hasText(appId) && StringUtils.hasText(nonce) && StringUtils.hasText(timestamp) && StringUtils.hasText(sign))) {
            throw new SignCatException("签名参数不完整（appId、nonce、timestamp、sign）");
        }

        MediaType mediaType = MediaType.parseMediaType(request.getContentType());

        // 提交 json 内容
        if(mediaType.includes(MediaType.APPLICATION_JSON)) {

            String contentMd5 = request.getHeader(SignKeyEnum.CONTENT_MD5_KEY.value);

            // 验证提交Json内容时，是否传递了 contentMd5
            if(!StringUtils.hasText(contentMd5)) {
                throw new SignCatException("签名参数不完整（json类型参数，需提交 contentMd5 参数）");
            }
        }

        AppService appService = null;
        try {
            appService = applicationContext.getBean(AppService.class);
        } catch (NoSuchBeanDefinitionException e) {
            throw new AreaResolverException("未找到 "+ AppService.class.getName() +" 接口的实现类");
        }

        String appSecret = appService.getAppSecret(appId);

        log.info("SignCatAspect:签名 appSecret -> {}",appSecret);

        // 构建签名参数
        // 附加 请求参数 到签名参数
        Map<String, String> signDataMap = request.getParameterMap().entrySet().stream().collect(Collectors.toMap(
                item -> item.getKey(),
                item -> item.getValue()!=null&&item.getValue().length>0?item.getValue()[0]:""
        ));

        // 附加 请求头元参数 到签名参数
        signDataMap.put(SignKeyEnum.APPID_KEY.value,appId);
        signDataMap.put(SignKeyEnum.METHOD_KEY.value,request.getMethod());
        signDataMap.put(SignKeyEnum.PATH_KEY.value,request.getServletPath());

        // 附加 请求头参数 到签名参数
        signDataMap.put(SignKeyEnum.NONCE_KEY.value,nonce);
        signDataMap.put(SignKeyEnum.TIMESTAMP_KEY.value,timestamp);

        log.info("SignCatAspect:签名 signDataMap -> {}",signDataMap);

        Boolean isPassed = apiSignUtils4Sha.verify(
                appSecret,
                request.getHeader(SignKeyEnum.SIGN_KEY.value),
                request.getHeader(SignKeyEnum.NONCE_KEY.value),
                request.getHeader(SignKeyEnum.TIMESTAMP_KEY.value),
                signDataMap);

        log.info("SignCatAspect ->");

        if(!isPassed) {
            throw new SignCatException("签名值sign不合法");
        }

        return proceed;
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