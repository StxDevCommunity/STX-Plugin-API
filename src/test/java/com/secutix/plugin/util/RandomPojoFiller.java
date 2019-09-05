package com.secutix.plugin.util;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class RandomPojoFiller {
	private static final int DATE_WINDOW_MILLIS = 24*60*60*1000;

	public static void randomlyPopulateFields(Object object) {
		new RandomValueFieldPopulator().populate(object);
	}

	public static class RandomValueFieldPopulator {
		public void populate(Object object) {
			try {
				Random r  = new Random();
				Field[] fields = object.getClass().getDeclaredFields();
				for(Field f : fields){
					f.setAccessible(true);
					Class<?> type = f.getType();
					Object val = generateRandomValue(r, type);
					f.set(object, val);
					
				}
			} catch (Exception e) {
				
				throw new RuntimeException(e);
			} 

		}

		
	}

	public static Object generateRandomValue(Random random, Class<?> fieldType) {
		if (fieldType.equals(String.class)) {
			return UUID.randomUUID().toString();
		} else if (Date.class.isAssignableFrom(fieldType)) {
			return new Date(System.currentTimeMillis() - random.nextInt(DATE_WINDOW_MILLIS));
		} else if (Number.class.isAssignableFrom(fieldType)) {
			return random.nextInt(Byte.MAX_VALUE) + 1;
		} else if (fieldType.equals(Integer.TYPE)) {
			return random.nextInt();
		} else if (fieldType.equals(Long.TYPE)) {
			return random.nextInt();
		} else if (Enum.class.isAssignableFrom(fieldType)) {
			Object[] enumValues = fieldType.getEnumConstants();
			return enumValues[random.nextInt(enumValues.length)];
		} else {
			return null;
		}
	}
}
