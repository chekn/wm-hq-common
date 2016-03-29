package com.assist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
public class TestUtils2 {
	
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
	
	public static void changeMapForMongo(Map<String,Object> outMap){
		for(String key:outMap.keySet()){
			Object val=outMap.get(key);
			
			if(key.contains(".")){
				outMap.remove(key);
				outMap.put(key.replace(".", "_"), val);
			}
			
			if(val instanceof Map)
				changeMapForMongo((Map<String, Object>) val);
			
		}
	}
	
	public static void main(String[] args){
		MongoClient mongoClient=getMongoClient();
		insertEndPoint(mongoClient, "break100", "CCCMMM");
		queryEndPoint(mongoClient,"break100");
		
		
		
		Map<String,Object> testMap=new HashMap<String,Object>(); 
		Map<String,Object> nestMap=new HashMap<String,Object>();
		nestMap.put("klx.90", "xks.cc");
		nestMap.put("klc.90", "xks.c.c");
		testMap.put("kl.lx", "cs...d");
		changeMapForMongo(testMap);
		System.out.println(testMap);
	}

}
