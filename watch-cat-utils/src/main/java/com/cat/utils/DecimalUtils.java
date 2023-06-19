package com.cat.utils;

import java.math.BigDecimal;

/**
 * 金额处理工具类
 * @author xy783
 */
public class DecimalUtils {

    /**
     * Long分 > BigDecimal元
     * @param fen
     * @return
     */
    public static BigDecimal longFenToBigDecimalYuan(Long fen){
        return BigDecimal.valueOf(fen).divide(BigDecimal.valueOf(100L));
    }

    /**
     * BigDecimal元 > Long分
     * @param yuan
     * @return
     */
    public static Long bigDecimalYuanToLongFen(BigDecimal yuan){
        BigDecimal priceFen = yuan.multiply(BigDecimal.valueOf(100L));
        return priceFen.longValue();
    }
}
