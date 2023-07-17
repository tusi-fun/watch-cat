package fun.tusi.sensitive.utils;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ObjectUtil;
import fun.tusi.sensitive.annotation.SensitiveField;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author xy783
 */
public class DesensitizedUtils {

    /**
     * 获取脱敏json串(递归引用会导致java.lang.StackOverflowError)
     *
     * @param dataObject
     * @return
     */
    public static Object dataValue(Object dataObject) {

        Object clone = null;

        try {

//          if (dataObject.getClass().isInterface()) return clone;

            /* 克隆出一个实体进行字段修改，避免修改原实体 */
            clone = ObjectUtil.cloneByStream(dataObject);

            if(clone==null){
                return clone;
            }

            /* 定义一个计数器，用于避免重复循环自定义对象类型的字段 */
            Set<Integer> referenceCounter = new HashSet<Integer>();

            /* 对克隆实体进行脱敏操作 */
            DesensitizedUtils.replace(clone, referenceCounter);

            /* 清空计数器 */
            referenceCounter.clear();

            referenceCounter = null;

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return clone;
    }
//
//    public static void main(String[] args) throws IllegalAccessException {
//
//        User user1 = new User();
//        user1.setIdCard("123");
//        user1.setRealName("测试真实姓名1");
//        user1.setPassword("123456789");
//
//        User user2 = new User();
//        user2.setIdCard("123");
//        user2.setRealName("测试真实姓名1");
//        user2.setPassword("123456789");
//
//        List<User> users = new ArrayList<User>();
//        users.add(user1);
//        users.add(user2);
//
////        getSensitiveFields(user1);
//        getSensitiveFields(users);
//
//    }
//
//    private static Field[] getSensitiveFields(Object object) throws IllegalAccessException {
//
//        if(object instanceof Collection<?>) {
//
//            IterUtil.getElementType((Iterable<?>) object);
//        }
//
//        Class<?> clazz = object.getClass();
//
//        System.out.println(clazz+"-isArray:"+clazz.isArray());
//        System.out.println(clazz+"-isCollection:"+(object instanceof Collection<?>));
//        System.out.println(clazz+"-isMap:"+(object instanceof Map<?,?>));
//
//        Field[] fields = ClassUtil.getDeclaredFields(object.getClass());
//
//        System.out.println("User 字段---");
//
//        Field[] annotationFields = null;
//
//        for (Field field : fields) {
//
//            field.setAccessible(true);
//
//            Type type = field.getGenericType();
//
//            Boolean isAnnotationPresent = field.isAnnotationPresent(SensitiveField.class);
//
//            Object fieldObj = field.get(object);
//
//            System.out.println("SensitiveField 注解标注："+isAnnotationPresent+"，" +
//                    "字段名："+field.getName()+"，" +
//                    "字段类型："+field.getType()+"，" +
//                    "是否为基本数据类型："+ClassUtil.isBasicType(field.getType())+"，" +
//                    "字段值："+fieldObj+"，" +
//                    "GenericType："+type
//            );
//
////            if(fieldObj instanceof Collection<?>) {
////                getSensitiveFields(fieldObj);
////            }
//        }
//
//        System.out.println("User 字段---");
//
//        return annotationFields ;
//    }

    public static Field[] getAllFields(Object objSource) {
        /*获得当前类的所有属性(private、protected、public)*/
        List<Field> fieldList = new ArrayList<Field>();
        Class tempClass = objSource.getClass();

        //当父类为null的时候说明到达了最上层的父类(Object类)
        while (tempClass != null && !"java.lang.object".equalsIgnoreCase(tempClass.getName())) {
            fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }

        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    /**
     * 对需要脱敏的字段进行转化
     * @param object
     * @param referenceCounter
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static void replace(Object object, Set<Integer> referenceCounter) throws IllegalArgumentException, IllegalAccessException {

        Field[] fields = getAllFields(object);

        if (null != fields && fields.length > 0) {

            for (Field field : fields) {

                field.setAccessible(true);

                if (null != field && null != object) {

                    Object value = field.get(object);

                    if (null != value) {
                        Class<?> type = value.getClass();
                        //处理子属性，包括集合中的
                        if (type.isArray()) {//对数组类型的字段进行递归过滤
                            int len = Array.getLength(value);
                            for (int i = 0; i < len; i++) {
                                Object arrayObject = Array.get(value, i);
                                if (arrayObject!=null ) {
                                    replace(arrayObject, referenceCounter);
                                }
                            }
                        } else if (value instanceof Collection<?>) {//对集合类型的字段进行递归过滤
                            Collection<?> c = (Collection<?>) value;
                            Iterator<?> it = c.iterator();
                            while (it.hasNext()) {// TODO: 待优化
                                Object arrayObject = it.next();
                                if (arrayObject!=null ) {
                                    replace(arrayObject, referenceCounter);
                                }
                            }
                        } else if (value instanceof Map<?, ?>) {//对Map类型的字段进行递归过滤
                            Map<?, ?> m = (Map<?, ?>) value;
                            Set<?> set = m.entrySet();
                            for (Object o : set) {
                                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                                Object mapVal = entry.getValue();
                                if (mapVal!=null ) {
                                    replace(mapVal, referenceCounter);
                                }
                            }
                        } else if (value instanceof Enum<?>) {
                            continue;
                        } else { /*除基础类型、jdk类型的字段之外，对其他类型的字段进行递归过滤*/
                            if (!type.isPrimitive()
                                    && type.getPackage() != null
                                    && !(type.getPackage().getName()).startsWith("javax.")
                                    && !(type.getPackage().getName()).startsWith("java.")
                                    && !(field.getType().getName()).startsWith("javax.")
                                    && !(field.getName()).startsWith("java.")
                                    && referenceCounter.add(value.hashCode())) {
                                replace(value, referenceCounter);
                            }
                        }
                    }

                    //脱敏操作
                    setNewValueForField(object, field, value);
                }
            }
        }
    }

    /**
     * 脱敏操作（按照规则转化需要脱敏的字段并设置新值）
     * 目前只支持String类型的字段，如需要其他类型如BigDecimal、Date等类型，可以添加
     *
     * @param javaBean
     * @param field
     * @param value
     * @throws IllegalAccessException
     */
    public static void setNewValueForField(Object javaBean, Field field, Object value) throws IllegalAccessException {

        //处理自身的属性
        SensitiveField annotation = field.getAnnotation(SensitiveField.class);

        if (field.getType().equals(String.class) && null != annotation) {

            String valueStr = (String) value;

            if (StringUtils.hasText(valueStr)) {

                field.set(javaBean, DesensitizedUtil.desensitized(valueStr,annotation.type()));
            }
        }
    }

}