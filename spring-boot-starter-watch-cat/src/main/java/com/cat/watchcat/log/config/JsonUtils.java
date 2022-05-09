package com.cat.watchcat.log.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * jackson 工具类
 *
 * @author hudongshan
 * @version 20210524
 *
 * 实现功能点：
 * 1、实现普通 Bean、List集合 的Json序列化和反序列化
 * 2、实现泛型（List、Map、自定义泛型对象）的Json序列化和反序列化
 * 3、实现 LocalDateTime、LocalDate、LocalTime 类型的序列化和反序列化
 */
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {

        /**
         * 属性值为 NULL 不序列化
         */
        objectMapper.setSerializationInclusion(Include.NON_NULL);

        /**
         * 是否多行缩进输出（美化输出），默认false
         */
        //objectMapper.configure(SerializationFeature.INDENT_OUTPUT, Boolean.TRUE);

        /**
         * 忽略Json字符串中未知字段
         */
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        /**
         * 序列化 LocalDateTime、LocalDate、LocalTime 配置
         */
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

        /**
         * 反序列化 LocalDateTime、LocalDate、LocalTime 配置
         */
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

        /**
         * 序列化 LocalDateTime、LocalDate、LocalTime 时全转换为时间戳
         */
//        javaTimeModule.addSerializer(Date.class, new DateToLongSerializer());
//        javaTimeModule.addSerializer(LocalDate.class, new LocalDateToLongSerializer());
//        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeToLongSerializer());

        objectMapper.registerModule(javaTimeModule);
    }

    /**
     * Object 转 json字符串，格式紧凑
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        if(obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Object 转 json字符串，格式美化
     * @param obj
     * @return
     */
    public static String toJsonPretty(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json字符串 转 普通Object
     *
     * @param jsonStr
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toBean(String jsonStr, Class<T> clazz) {

        if(!StringUtils.hasText(jsonStr) || clazz == null){
            return null;
        }

        try {
            return  clazz.equals(String.class) ? (T) jsonStr : objectMapper.readValue(jsonStr, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json字符串 转 List
     *
     * 使用示例：List<JsonTestObj> objs = JsonUtils.toList("jsonStr", JsonTestObj.class);
     *
     * @param jsonStr
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(String jsonStr, Class<T> clazz) {

        if(!StringUtils.hasText(jsonStr) || clazz == null){
            return null;
        }

        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);

        try {
            return objectMapper.readValue(jsonStr, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * json字符串 转 范型
     *
     * 使用示例1：List<JsonTestObj> objs = JsonUtils.toBean4TypeReference("jsonStr", new TypeReference<List<JsonTestObj>>() {});
     * 使用示例2：Map<String,JsonTestObj> c = JsonUtils.toBean4TypeReference("jsonStr",new TypeReference<Map<String,JsonTestObj>>() {});
     * 使用示例3：ResultData<JsonTestObj> d = JsonUtils.toBean4TypeReference("jsonStr", new TypeReference<ResultData<JsonTestObj>>() {});
     *
     * @param jsonStr
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T toBean4TypeReference(String jsonStr, TypeReference<T> typeReference) {

        if(!StringUtils.hasText(jsonStr) || typeReference == null){
            return null;
        }

        try {
            return objectMapper.readValue(jsonStr, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
