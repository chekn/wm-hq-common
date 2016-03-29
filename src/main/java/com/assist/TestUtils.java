package com.assist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.assist.drupal.FtlVarAddColorUtil;
import com.assist.drupal.SimpleDrupalTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.yicai.dt.modhs.client.HsVisitorClient;
import com.yicai.medialab.writingmaster.extraction.core.api.InfoExtraction;

/**
 * 
 * @author Pactera-NEN
 * @date 2016年3月8日-下午3:59:51
 */
public class TestUtils {
	
	public static Map<String,Object> simulateExtOfferRawData(){
		Map<String,Object> rawData=new HashMap<String,Object>();
		AbstractApplicationContext aac=new ClassPathXmlApplicationContext("classpath:applicationContext2.xml");
		HsVisitorClient hsv=aac.getBean(HsVisitorClient.class);
		rawData.put(InfoExtraction.HQ_HSCLIENT_KEY, hsv);
		return rawData;
	}
	
	public static MongoClient getMongoClient(){

		ServerAddress addr=new ServerAddress("121.40.81.73",27017);
		MongoCredential credential=MongoCredential.createCredential("dev", "admin", "medialab".toCharArray());
		List<MongoCredential> listCredential=new ArrayList<MongoCredential>();
		listCredential.add(credential);
		MongoClient mongoClient=new MongoClient(addr,listCredential);
		
		return mongoClient;
		
	}
	
	/**
	 * mongodb data
	 * @param mongoClient
	 * @param uniqueName
	 * @param eventId
	 */
	public static void insertEndPoint(MongoClient mongoClient, String uniqueName, String eventId){
		
		MongoDatabase db=mongoClient.getDatabase("hq_jar");
		
		MongoCollection<Document> coll=db.getCollection("hq_run_data");
		
		Document doc=new Document();
		doc.append("uniqueName", uniqueName);
		doc.append("timeStamper", System.currentTimeMillis());
		doc.append("eventId", eventId);
		coll.insertOne(doc);
		
	}
	
	public static void queryEndPoint(MongoClient mongoClient, String uniqueName){
		
		MongoDatabase db=mongoClient.getDatabase("hq_jar");
		
		MongoCollection<Document> coll=db.getCollection("hq_run_data");
		
		FindIterable<Document> fi= coll.find(Filters.eq("uniqueName", uniqueName));
		for(Document doc:fi){
			System.out.println(doc);
		}
		
	}
	
	public static Map<String,Object> changeMapForMongo(Map<String,Object> outMap){
		Map<String,Object> copyMap=new HashMap<String,Object>();
		
		for(String key:outMap.keySet()){
			Object val=outMap.get(key);
			
			String cpKey=key;
			Object cpVal=val;
			if(key.contains("."))
				cpKey=key.replace(".", "_");
			
			if(val instanceof Map)
				cpVal=changeMapForMongo((Map<String, Object>) val);
		
			copyMap.put(cpKey, cpVal);
		}
		//copyMap.put("sign", "changedByChangeForMongo");
		return copyMap;
	}
	
	public static void main(String[] args){
		MongoClient mongoClient=getMongoClient();
		insertEndPoint(mongoClient, "break100", "CCCMMM");
		//queryEndPoint(mongoClient,"break100");
		
		
		Map<String,Object> testMap=new HashMap<String,Object>(); 
		Map<String,Object> nestMap=new HashMap<String,Object>();
		nestMap.put("klx.90", "xks.cc");
		nestMap.put("klc.90", "xks.c.c");
		testMap.put("kfc.lc", nestMap);
		testMap.put("kl.lx", "cs...d");
		
		System.out.println(changeMapForMongo(testMap));
	}
	
	
	public static void simulateLocalParserFunTest(InfoExtraction infoExtraction) throws IOException{
		//ftl 重写
		String buildPath=infoExtraction.getClass().getClassLoader().getResource("").getPath().toString().substring(1);
		String srcPath=buildPath+"../../src/main/java/";
		String packagePath=infoExtraction.getClass().getPackage().getName().replace(".", "/");
		String fDir1=buildPath+packagePath;
		String fDir2=srcPath+packagePath;
		System.out.println(fDir1);
		System.out.println(fDir2);
		FtlVarAddColorUtil.addFontColorTag(fDir1);
		FtlVarAddColorUtil.addFontColorTag(fDir2);
		
		Map<String,Object> wrapParserData=infoExtraction.extractFromRawData(simulateExtOfferRawData());
		//由于freemark 要求packagePath 是相对类加载路径的绝对路径
		new SimpleDrupalTool().pushDataSet("/"+packagePath, infoExtraction.getUniqueName(), wrapParserData);
		
	}
	

}
