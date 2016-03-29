package com.assist.parser.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public final class JarUtil {
	private JarUtil() {

	}

	private static final Pattern DOT_REP = Pattern.compile("/|\\$");

	public static List<String> extractFullClassNames(String jarFilePath) {
		List<String> classList = new ArrayList<String>();
		try (JarFile jarFile = new JarFile(jarFilePath)) {
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				String className = jarEntry.getName();
				if (className.endsWith(".class")) {
					classList.add(DOT_REP.matcher(className.substring(0, className.length() - 6)).replaceAll("."));
				}
			}
			return classList;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
