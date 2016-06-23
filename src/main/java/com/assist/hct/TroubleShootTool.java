package com.assist.hct;

import java.lang.reflect.Method;

import org.apache.commons.lang.ArrayUtils;

/**
 * hct 拍错
 * @author Pactera-NEN
 * @date 2016年6月22日-下午3:21:47
 */
public class TroubleShootTool {
	
	/**
	 * NoSuchMethod 我们排错关注信息
	 * @return
	 */
	public static String viewNoSuchMethodPbClazzInfo(Class<?> clazz){
		StringBuilder strBu=new StringBuilder();
		
		Method[] methods=clazz.getDeclaredMethods();
		for(Method method:methods) {
			strBu.append(" methodName: "+method.getName() + ", returnType:" + method.getReturnType() + ", paramsType:" + ArrayUtils.toString(method.getParameterTypes())).append(", ");
		}
		
		return strBu.toString();
	}

}
