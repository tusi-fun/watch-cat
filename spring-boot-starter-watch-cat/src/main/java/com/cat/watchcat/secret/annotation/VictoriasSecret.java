//package com.cat.watchcat.secret.annotation;
//
//import java.lang.annotation.*;
//
///**
// * 维多利亚的秘密
// * @author hudongshan
// * @version 20220514
// */
//@Target(ElementType.PARAMETER)
//@Retention(RetentionPolicy.RUNTIME)
//@Inherited
//public @interface VictoriasSecret {
//
////    /**
////     * 是否启用响应加密
////     * @return
////     */
////    boolean encryptedPong() default false;
////
////    /**
////     * 响应加密结果填充字段
////     * @return
////     */
////    String pongEncryptField() default "data";
//
//    /**
//     * 是否启用防止重放
//     * @return
//     */
//    boolean preventReplay() default true;
//
//    /**
//     * 是否启用原文参数验证
//     * @return
//     */
//    boolean plainTextValid() default true;
//
//}