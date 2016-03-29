package com.assist.mongo;

import java.util.Date;
import java.util.Map;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	
	public static String queryLastParsedDataCompareStr(String uniqueName) throws JsonProcessingException {
		MongoClient mongoClient=MongoDBTool.getMongoClient();
		ObjectMapper mapper=new ObjectMapper();
		String jsonStr="";
		
		MongoDatabase db=mongoClient.getDatabase("writingmaster_test");
		
		MongoCollection<Document> coll=db.getCollection("parsed_data");
		
		FindIterable<Document> fi= coll.find(Filters.eq("collection", uniqueName)).sort(new Document("parse_time",-1)).limit(1);
		for(Document doc:fi)
			jsonStr=mapper.writeValueAsString(((Document)((Document)doc.get("parsed_data")).get(uniqueName)).get("comparableStr"));
		
		mongoClient.close();
		return jsonStr;
	}
	
	public static void insertHaveGernParsedData(String uniqueName,Map<String,Object> parsedData){
		Document doc=new Document();
		doc.append("collection", uniqueName);
		doc.append("parse_time", new Date());
		doc.append("parsed_data", parsedData);
		
		MongoClient mongoClient=MongoDBTool.getMongoClient();
		MongoDatabase db=mongoClient.getDatabase("writingmaster_test");
		MongoCollection<Document> coll=db.getCollection("parsed_data");
		coll.insertOne(doc);
		
		mongoClient.close();
	}
	
}
