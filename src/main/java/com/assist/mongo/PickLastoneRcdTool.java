package com.assist.mongo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

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
	 * 
	 * @param mongoClient
	 * @param mongoDbName
	 * @param comparableStr
	 * @param uniqueName
	 * @return
	 */
	@Deprecated
	public static long queryComaprableStr(MongoClient mongoClient, String mongoDbName, String comparableStr, String uniqueName){
		
		Document cass=new Document();
		cass.append("uniqueName", uniqueName);
		cass.append("comparableStr", comparableStr);
		MongoDatabase db=mongoClient.getDatabase(mongoDbName);
		MongoCollection<Document> coll=db.getCollection(collName);
		
		return coll.count(cass);
	}
	
	/**
	 * 插入数据,放回ObjectId  state: 0 运行状态  1 没获取到有效数据 2 获取到有效数据 
	 * @param mongoClient
	 * @param uniqueName
	 * @param parsedData
	 */
	public static Map<String,Object> insertRunData(MongoClient mongoClient, String mongoDbName, String uniqueName){
		Document doc=new Document();
		doc.append("uniqueName", uniqueName);
		doc.append("state", 0);
		doc.append("startDate", new Date());
		
		MongoDatabase db=mongoClient.getDatabase(mongoDbName);
		MongoCollection<Document> coll=db.getCollection("hq_run_data");
		coll.insertOne(doc);
		
		return doc;
	}
	
	public static void updateRunData(MongoClient mongoClient, String mongoDbName, String id, int state, String comparableStr){
		Map<String,Object>filterMap= new HashMap<String,Object>();
		filterMap.put("noFilter","NA");
		updateRunData(mongoClient,mongoDbName,id, state, comparableStr,filterMap);
	}
	
	public static void updateRunData(MongoClient mongoClient, String mongoDbName, String id, int state, String comparableStr,Map<String,?> filterMap){
		Document updatePartDoc=new Document();
		updatePartDoc.append("state", state);
		updatePartDoc.append("comparableStr",comparableStr);
		updatePartDoc.append("endDate", new Date());
	    if(!filterMap.containsKey("noFilter"))updatePartDoc.append("filterMap",filterMap);
		
		MongoDatabase db=mongoClient.getDatabase(mongoDbName);
		MongoCollection<Document> coll=db.getCollection(collName);
		UpdateResult updateResult= coll.updateOne(Filters.eq("_id", new ObjectId(id)), new Document("$set", updatePartDoc));
		
		updateResult.getModifiedCount();
	}
	
}
