package com.assist.parser;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.assist.TestUtils;
import com.assist.parser.util.ClassUtil;
import com.assist.parser.util.JarUtil;
import com.yicai.medialab.writingmaster.extraction.core.api.InfoExtraction;

public class ParserJarInvoker {

	public Map<String, Object> extractFromRawData(String jarFilePath, Map<String, Object> rawData) {
		InfoExtraction extraction = null;
		List<String> classNameList = JarUtil.extractFullClassNames(jarFilePath);
		try {
			URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[] { new URL("file:" + jarFilePath) },
					Thread.currentThread().getContextClassLoader());
			Thread.currentThread().setContextClassLoader(urlClassLoader);
			
			for (String className : classNameList) {
				System.out.println("jar包 内部有类:"+className);
				Class<?> clazz = urlClassLoader.loadClass(className);
				if (ClassUtil.containInterface(clazz, InfoExtraction.class)) {
					extraction = (InfoExtraction) clazz.newInstance();
					extraction.extractFromRawData(rawData);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		if (extraction == null) {
			throw new IllegalArgumentException(jarFilePath + "没有包含可以实例化成InfoExtraction的类");
		}
		try {
			return addTimeoutFeature(extraction, rawData);
		} catch (Exception e) {
			System.err.println("parse jar inner error ,"+ e.getMessage());
		}
		return null;
	}

	public Map<String, Object> addTimeoutFeature(final InfoExtraction extraction, final Map<String, Object> rawData) {
		Map<String, Object> result = null;

		ExecutorService executor = Executors.newSingleThreadExecutor();
		FutureTask<Map<String, Object>> futureTask = new FutureTask<Map<String, Object>>(
				new Callable<Map<String, Object>>() {// 使用Callable接口作为构造参数
					public Map<String, Object> call() {
						return extraction.extractFromRawData(rawData);
					}
				});
		executor.execute(futureTask);
		// 在这里可以做别的任何事情
		try {
			result = futureTask.get(3000, TimeUnit.MILLISECONDS); // 取得结果，同时设置超时执行时间为3秒。同样可以用future.get()，不设置执行超时时间取得结果
		} catch (InterruptedException e) {
			System.err.println("parser jar InterruptedException ,"+e.getMessage());
			futureTask.cancel(true);
		} catch (ExecutionException e) {
			System.err.println("parser jar ExecutionException ,"+e.getMessage());
			futureTask.cancel(true);
		} catch (TimeoutException e) {
			System.err.println("parser jar TimeoutException ,"+e.getMessage());
			futureTask.cancel(true);
		} finally {
			executor.shutdown();
		}
		return result;
	}
	
	public static void main(String[] args){
		ParserJarInvoker pji=new ParserJarInvoker();
		pji.extractFromRawData("C:\\Users\\Pactera-NEN\\Desktop\\Fct\\wm-mm-hkindexquote-0.0.1.jar", TestUtils.simulateExtOfferRawData());
	}
}
