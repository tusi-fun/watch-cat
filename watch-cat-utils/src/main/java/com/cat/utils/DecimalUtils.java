package com.cat.utils;

import java.math.BigDecimal;

/**
 * @author hudongshan
 */
public class DecimalUtils {

    /**
     * long 类型的分转 BigDecimal 类型的元
     * @return
     */
    public static BigDecimal longFenToBigDecimalYuan(Long priceFen){
        return BigDecimal.valueOf(priceFen).divide(BigDecimal.valueOf(100L));
    }

    /**
     * BigDecimal 类型的元转 long 类型的分
     * @return
     */
    public static Long bigDecimalYuanToLongFen(BigDecimal priceYuan){
        BigDecimal priceFen = priceYuan.multiply(BigDecimal.valueOf(100L));
        return priceFen.longValue();
    }
}
