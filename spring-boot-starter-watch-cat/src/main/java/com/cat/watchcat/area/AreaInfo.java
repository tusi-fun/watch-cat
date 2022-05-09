package com.cat.watchcat.area;
//package com.cat.watchcat.area;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import lombok.*;
//import org.springframework.util.StringUtils;
//
//import java.io.Serializable;
//
///**
// * 地址完整对象
// * @author hudongshan
// * @version 20210825
// */
//@Data
//@Builder
//@ToString
//@NoArgsConstructor
//@AllArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class AreaInfo implements Serializable {
//
//    private static final long serialVersionUID = -4851404274777661164L;
//
//    /**
//     * 请求的 areaCodes 参数原文
//     */
//    private String areaCodes;
//
//    private String areaNames;
//    private String address;
//
//    private String province;
//    private String city;
//    private String district;
//    private String community;
//    private String street;
//
//    public String getAddress() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(province).append(city).append(district);
//
//        if(StringUtils.hasText(community)) {
//            sb.append(community);
//        }
//
//        if(StringUtils.hasText(street)) {
//            sb.append(street);
//        }
//        return sb.toString();
//    }
//
//    public String getAreaNames() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(province).append("|").append(city).append("|").append(district);
//        if(StringUtils.hasText(community)) {
//            sb.append("|").append(community);
//        }
//        return sb.toString();
//    }
//}
