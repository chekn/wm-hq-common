package com.assist.parser.util;

public final class ClassUtil {
	private ClassUtil() {
	}

	public static boolean containSuperClass(Class<?> targetClass, Class<?> superClass) {
		Class<?> tmpClass = targetClass;
		do {
			if (tmpClass.equals(superClass)) {
				return true;
			}
		} while ((tmpClass = tmpClass.getSuperclass()) != null);
		return false;
	}

	public static boolean containInterface(Class<?> targetClass, Class<?> interfaceClass) {
		if (!interfaceClass.isInterface()) {
			throw new IllegalArgumentException(interfaceClass + "is not a interface");
		}
		do {
			Class<?>[] interfaces = targetClass.getInterfaces();
			for (Class<?> clazz : interfaces) {
				if (interfaceClass.equals(clazz)) {
					return true;
				}
			}
		} while ((targetClass = targetClass.getSuperclass()) != null);
		return false;
	}
}
