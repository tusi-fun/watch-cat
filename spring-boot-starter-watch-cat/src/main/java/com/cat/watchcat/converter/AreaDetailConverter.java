package com.cat.watchcat.converter;

import com.cat.common.AreaDetail;
import com.cat.watchcat.area.service.AreaResolverException;
import com.cat.watchcat.area.service.AreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * String类型的请求参数，转接收对象中的 AreaDetail
 * @author hudongshan
 * @version 20211211
 */
@Slf4j
@Component
public class AreaDetailConverter implements Converter<String, AreaDetail> {

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public AreaDetail convert(String areaCodeStr) {

        log.info("AreaDetailConverter -> {}",areaCodeStr);

        if(!StringUtils.hasText(areaCodeStr)) {
            log.error("参数 areaCodes 不能为空");
            return null;
        }

        String[] areaCodes = areaCodeStr.split("\\|");

        // 地区codes只能为3级（省、市、县） 或 4级（省、市、县、社区）
        if(!(areaCodes.length == 3 || areaCodes.length == 4)) {
            log.error("参数 areaCodes = {} 解析失败",areaCodeStr);
            return null;
        }

        AreaService areaService = null;

        try {

            areaService = applicationContext.getBean(AreaService.class);

        } catch (NoSuchBeanDefinitionException e) {
            throw new AreaResolverException("未找到 "+ AreaService.class.getName() +" 接口的实现类");
        }

        Map<String,String> areas = areaService.parseAreaCodes(areaCodes);

        log.info("areas = {}", areas);

        if(areas==null || areas.size()==0) {
            log.error("参数 areaCodes = {} 查询失败",areaCodeStr);
            return null;
        }

        AreaDetail.AreaDetailBuilder areaBuilder = AreaDetail.builder()
                .areaCodes(areaCodeStr)
                .province(areas.get(areaCodes[0]))
                .city(areas.get(areaCodes[1]))
                .district(areas.get(areaCodes[2]));

        if(areaCodes.length == 4) {
            areaBuilder.community(areas.get(areaCodes[3]));
        }

        return areaBuilder.build();

    }
}
