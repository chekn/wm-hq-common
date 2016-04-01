package com.assist.mongo;

import java.util.Date;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
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
	
	public static Map<String,Object> queryLastRunData(MongoClient mongoClient, String uniqueName) throws JsonProcessingException {
		Document revDoc=new Document();
		
		MongoDatabase db=mongoClient.getDatabase("hq_jar");
		MongoCollection<Document> coll=db.getCollection("hq_run_data");
		FindIterable<Document> fi= coll.find(Filters.eq("uniqueName", uniqueName)).sort(new Document("date",-1)).limit(1);
		for(Document doc:fi)
			revDoc=doc;
		
		return revDoc;
	}
	
	/**
	 * 插入数据,放回ObjectId  state: 0 启动状态  1 运行结束状态
	 * @param mongoClient
	 * @param uniqueName
	 * @param parsedData
	 */
	public static String insertRunData(MongoClient mongoClient, String uniqueName){
		Document doc=new Document();
		doc.append("uniqueName", uniqueName);
		doc.append("state", 0);
		doc.append("startDate", new Date());
		
		MongoDatabase db=mongoClient.getDatabase("hq_jar");
		MongoCollection<Document> coll=db.getCollection("hq_run_data");
		coll.insertOne(doc);
		
		return doc.get("_id").toString();
	}
	
	public static void updateRunData(MongoClient mongoClient, String id,String comparableStr){
		Document updatePartDoc=new Document();
		updatePartDoc.append("state", 1);
		updatePartDoc.append("comparableStr",comparableStr);
		updatePartDoc.append("endDate", new Date());
		
		MongoDatabase db=mongoClient.getDatabase("hq_jar");
		MongoCollection<Document> coll=db.getCollection("hq_run_data");
		System.out.println("******"+id);
		UpdateResult updateResult= coll.updateOne(Filters.eq("_id", new ObjectId(id)), new Document("$set", updatePartDoc));
		
		updateResult.getModifiedCount();
	}
	
	
}
