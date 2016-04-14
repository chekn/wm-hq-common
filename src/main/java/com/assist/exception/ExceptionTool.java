package com.assist.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 
 * @author Pactera-NEN
 * @date 2016年3月30日-上午10:06:06
 */
public class ExceptionTool {
	
	public static String getExceptionStactInfo(Throwable e){
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
}
