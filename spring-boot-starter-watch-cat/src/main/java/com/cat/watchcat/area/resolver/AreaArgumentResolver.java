package com.cat.watchcat.area.resolver;//package com.cat.watchcat.area.resolver;
//
//import com.cat.watchcat.area.AreaInfo;
//import com.cat.watchcat.area.annotation.AreaCat;
//import com.cat.watchcat.area.service.AreaResolverException;
//import com.cat.watchcat.area.service.AreaService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.NoSuchBeanDefinitionException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.core.MethodParameter;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
//import java.util.Map;
//
///**
// * 自定义参数解析器（解析用户提交的地区信息）
// * @author hudongshan
// * @version 20201218
// */
//@Slf4j
//@Component
//public class AreaArgumentResolver implements HandlerMethodArgumentResolver {
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @Override
//    public boolean supportsParameter(MethodParameter parameter) {
//        return AreaInfo.class.isAssignableFrom(parameter.getParameterType()) && parameter.hasParameterAnnotation(AreaCat.class);
//    }
//
//    @Override
//    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest request, WebDataBinderFactory factory) {
//
//        log.info("AreaMethodArgumentResolver -> 执行地址库解析");
//
//        AreaCat areaCat = parameter.getParameterAnnotation(AreaCat.class);
//
//        String areaCodeStr = request.getParameter(areaCat.areaCodesField()),
//                street = request.getParameter(areaCat.streetField());
//
//        if(!StringUtils.hasText(areaCodeStr)) {
//            throw new AreaResolverException("参数 areaCodes 不能为空");
//        }
//
//        String[] areaCodes = areaCodeStr.split("\\|");
//
//        // 地区codes只能为3级（省、市、县） 或 4级（省、市、县、社区）
//        if(!(areaCodes.length == 3 || areaCodes.length == 4)) {
//            throw new AreaResolverException("参数 areaCodes 解析失败，请检查格式");
//        }
//
//        AreaService areaService;
//
//        try {
//
//            areaService = applicationContext.getBean(AreaService.class);
//
//        } catch (NoSuchBeanDefinitionException e) {
//            throw new AreaResolverException("未找到 "+ AreaService.class.getName() +" 接口的实现类");
//        }
//
//        Map<String,String> areas = areaService.parseAreaCodes(areaCodes);
//
//        log.info("areas = {}", areas);
//
//        if(areas==null || areas.size()==0) {
//            throw new AreaResolverException("参数 areaCodes 解析失败，请检查格式");
//        }
//
//        AreaInfo.AreaInfoBuilder areaInfoBuilder = AreaInfo.builder()
//                .areaCodes(areaCodeStr)
//                .street(street)
//                .province(areas.get(areaCodes[0]))
//                .city(areas.get(areaCodes[1]))
//                .district(areas.get(areaCodes[2]));
//
//        if(areaCodes.length == 4) {
//            areaInfoBuilder.community(areas.get(areaCodes[3]));
//        }
//
//        return areaInfoBuilder.build();
//    }
//
//}