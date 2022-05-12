package com.cat.common;

import com.cat.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 地址完整对象
 * @author hudongshan
 * @version 20210825
 */
@Data
@SuperBuilder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AreaDetail implements Serializable {

    private static final long serialVersionUID = 8421357717446036872L;

    /**
     * 请求的 areaCodes 参数原文
     */
    private String areaCodes;

    private String areaNames;
    private String address;

    private String province;
    private String city;
    private String district;
    private String community;

    public String getAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(province).append(city).append(district);

        if(StringUtils.hasText(community)) {
            sb.append(community);
        }
        return sb.toString();
    }

    public String getAreaNames() {
        StringBuilder sb = new StringBuilder();
        sb.append(province).append("|").append(city).append("|").append(district);
        if(StringUtils.hasText(community)) {
            sb.append("|").append(community);
        }
        return sb.toString();
    }
}
