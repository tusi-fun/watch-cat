package com.cat.exception;

import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * 自定义业务断言（参考 Spring Assert）
 * @author hudongshan
 * @version 20201027
 */
public abstract class BusinessAssert {

    public BusinessAssert() { }

    /**
     * 表达式必须为 true，否则抛异常
     * @param expression
     * @param baseCode
     */
    public static void isTrue(boolean expression, BaseCode baseCode, String... args) {
        if (!expression) {
            throw new BusinessException(baseCode,args);
        }
    }

    /**
     * 对象必须为 null，否则抛异常
     * @param object
     * @param baseCode
     */
    public static void isNull(Object object, BaseCode baseCode, String... args) {
        if (object != null) {
            throw new BusinessException(baseCode,args);
        }
    }

    /**
     * 对象不能为 null，否则抛异常
     * @param object
     * @param baseCode
     */
    public static void notNull(Object object, BaseCode baseCode, String... args) {
        if (object == null) {
            throw new BusinessException(baseCode,args);
        }
    }

    /**
     * 字符串不能为 null、空字符串，否则抛异常
     * @param str
     * @param baseCode
     */
    public static void hasLength(String str, BaseCode baseCode, String... args) {
        if (str == null || str.isEmpty()) {
            throw new BusinessException(baseCode,args);
        }
    }

    /**
     * 字符串不能为 null、空字符串、空白字符、空格字符，否则抛异常（判断最严格，推荐使用）
     * @param text
     * @param baseCode
     */
    public static void hasText(String text, BaseCode baseCode, String... args) {
        if (!StringUtils.hasText(text)) {
            throw new BusinessException(baseCode,args);
        }
    }

    /**
     * 字符串不能包含指定字符串，否则抛异常
     * @param textToSearch
     * @param substring
     * @param baseCode
     */
    public static void doesNotContain(String textToSearch, String substring, BaseCode baseCode, String... args) {
        if ((textToSearch != null && !textToSearch.isEmpty())
                && (substring != null && !substring.isEmpty())
                && textToSearch.contains(substring)) {
            throw new BusinessException(baseCode,args);
        }
    }

    /**
     * 数组不能为 null 且 元素不能为空，否则抛异常
     * @param array
     * @param baseCode
     */
    public static void notEmpty(Object[] array, BaseCode baseCode, String... args) {
        if (array == null || array.length == 0) {
            throw new BusinessException(baseCode,args);
        }
    }

    /**
     * 数组中的元素不能包含 null，否则抛异常
     * @param array
     * @param baseCode
     */
    public static void noNullElements(Object[] array, BaseCode baseCode, String... args) {
        if (array != null) {
            Object[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Object element = var2[var4];
                if (element == null) {
                    throw new BusinessException(baseCode,args);
                }
            }
        }
    }

    /**
     * 集合不能为 null 且 元素不能为空，否则抛异常
     * @param collection
     * @param baseCode
     */
    public static void notEmpty(Collection<?> collection, BaseCode baseCode, String... args) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(baseCode,args);
        }
    }

    /**
     * 集合中的元素不能包含 null，否则抛异常
     * @param collection
     * @param baseCode
     */
    public static void noNullElements(Collection<?> collection, BaseCode baseCode, String... args) {
        if (collection != null) {
            Iterator var2 = collection.iterator();

            while(var2.hasNext()) {
                Object element = var2.next();
                if (element == null) {
                    throw new BusinessException(baseCode,args);
                }
            }
        }
    }

    /**
     * map不能为null 且 元素为不能为空，否则抛异常
     * @param map
     * @param baseCode
     */
    public static void notEmpty(Map<?, ?> map, BaseCode baseCode, String... args) {
        if (map == null || map.isEmpty()) {
            throw new BusinessException(baseCode,args);
        }
    }

}
