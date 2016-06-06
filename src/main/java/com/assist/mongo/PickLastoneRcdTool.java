package com.assist.mongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

/**
 * 
 * @author Pactera-NEN
 * @date 2016年3月21日-下午6:42:19
 */
public class PickLastoneRcdTool {
	
	private static String collName="hq_run_data";
	
	public static Map<String,Object> queryLastRunData(MongoClient mongoClient, String mongoDbName, String uniqueName) {
		Document revDoc=new Document();
		
		MongoDatabase db=mongoClient.getDatabase(mongoDbName);
		MongoCollection<Document> coll=db.getCollection(collName);
		FindIterable<Document> fi= coll.find(Filters.eq("uniqueName", uniqueName)).sort(new Document("startDate",-1)).limit(1);
		for(Document doc:fi)
			revDoc=doc;
		
		return revDoc;
	}
	
	/**
	 * 插入数据,放回ObjectId  state: 0 运行状态  1 没获取到有效数据 2 获取到有效数据 
	 * @param mongoClient
	 * @param uniqueName
	 * @param parsedData
	 */
	public static Map<String,Object> insertRunData(MongoClient mongoClient, String mongoDbName, String uniqueName,
			Date startDate, Date endDate, String refId, int state, String comparableStr, String retEmptyRes){
		return insertRunData(mongoClient, mongoDbName, uniqueName, startDate, endDate ,refId, state, comparableStr, retEmptyRes, null);
	}
	
	/**
	 * 插入数据,放回ObjectId  state: 0 运行状态  1 没获取到有效数据 2 获取到有效数据 
	 * @param mongoClient
	 * @param uniqueName
	 * @param parsedData
	 */
	public static Map<String,Object> insertRunData(MongoClient mongoClient, String mongoDbName, String uniqueName,
			Date startDate, Date endDate, String refId, int state, String comparableStr, String retEmptyRes, Map<String, ?> extKeys){
		Document doc=new Document();
		doc.append("uniqueName", uniqueName);
		doc.append("startDate", new Date());
		doc.append("endDate", new Date());
		doc.append("refId", refId);
		doc.append("state", state);
		doc.append("comparableStr",comparableStr);
		doc.append("retEmptyRes", retEmptyRes);
	    if(extKeys!=null) {
	    	//添加自定义列
	    	for(String key:extKeys.keySet())
	    		doc.append(key,extKeys.get(key));
	    }
		
		MongoDatabase db=mongoClient.getDatabase(mongoDbName);
		MongoCollection<Document> coll=db.getCollection("hq_run_data");
		coll.insertOne(doc);
		
		return doc;
	}
	
	/*
	public static long queryComaprableStr(MongoClient mongoClient, String mongoDbName, String comparableStr, String uniqueName){
		
		Document cass=new Document();
		cass.append("uniqueName", uniqueName);
		cass.append("comparableStr", comparableStr);
		MongoDatabase db=mongoClient.getDatabase(mongoDbName);
		MongoCollection<Document> coll=db.getCollection(collName);
		
		return coll.count(cass);
	}
	public static void updateRunData(MongoClient mongoClient, String mongoDbName, String id, int state, String comparableStr, String refId){
		updateRunData(mongoClient,mongoDbName,id, state, comparableStr, refId, null);
	}
	
	public static void updateRunData(MongoClient mongoClient, String mongoDbName, String id, int state, String comparableStr, String refId, Map<String, ?> extKeys){
		Document updatePartDoc=new Document();
		updatePartDoc.append("state", state);
		updatePartDoc.append("comparableStr",comparableStr);
		updatePartDoc.append("endDate", new Date());
		updatePartDoc.append("refRsId", refId);
	    if(extKeys!=null) {
	    	//添加自定义列
	    	for(String key:extKeys.keySet())
	    		updatePartDoc.append(key,extKeys.get(key));
	    }
	    	
		MongoDatabase db=mongoClient.getDatabase(mongoDbName);
		MongoCollection<Document> coll=db.getCollection(collName);
		UpdateResult updateResult= coll.updateOne(Filters.eq("_id", new ObjectId(id)), new Document("$set", updatePartDoc));
		
		updateResult.getModifiedCount();
	}
	*/
	
}
