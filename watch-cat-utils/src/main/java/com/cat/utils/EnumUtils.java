package com.cat.utils;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author hudongshan
 * @version 2023/6/5
 */
public class EnumUtils {

	/**
	 * 判断 enumClass 中 field 的值 是否存在 value
	 * @param enumClass
	 * @param fieldName
	 * @param value
	 * @return
	 * @param <E>
	 */
	public static <E extends Enum<E>, T> boolean isValidEnumValue(Class<E> enumClass, String fieldName, T value) {
		try {
			Field field = enumClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			for (E enumValue : enumClass.getEnumConstants()) {
				T fieldValue = (T) field.get(enumValue);
				if (fieldValue.equals(value)) {
					return true;
				}
			}
			return false;
		} catch (NoSuchFieldException | IllegalAccessException e) {
			return false;
		}
	}

	/**
	 * 判断枚举名称（name()）是否包含指定值
	 * @param enumClass
	 * @param name
	 * @return
	 * @param <E>
	 */
	public static <E extends Enum<E>> boolean anyMatch(Class<E> enumClass, String name) {
		return Arrays.asList(enumClass.getEnumConstants()).stream().anyMatch(o -> o.name().equals(name));
	}

//	public static void main(String[] args) {
//		System.out.println(EnumUtils.isValidEnumValue(GenderEnum.class, "code", -1));
//		System.out.println(EnumUtils.isValidEnumValue(GenderEnum.class, "code", 0));
//		System.out.println(EnumUtils.isValidEnumValue(GenderEnum.class, "code", 1));
//
//		System.out.println(EnumUtils.isValidEnumValue(GenderEnum.class, "code1", -11));
//
//		System.out.println(EnumUtils.isValidEnumValue(GenderEnum.class, "desc", "未知"));
//		System.out.println(EnumUtils.isValidEnumValue(GenderEnum.class, "desc", "女性"));
//		System.out.println(EnumUtils.isValidEnumValue(GenderEnum.class, "desc", "男性"));
//		System.out.println(EnumUtils.isValidEnumValue(GenderEnum.class, "desc", "2"));
//
//		System.out.println(EnumUtils.anyMatch(GenderEnum.class, "UNKNOWN"));
//		System.out.println(EnumUtils.anyMatch(GenderEnum.class, "UNKNOWN1"));
//		System.out.println(EnumUtils.anyMatch(GenderEnum.class, ""));
//		System.out.println(EnumUtils.anyMatch(GenderEnum.class, null));
//	}
}
