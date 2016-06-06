package com.assist.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * 
 * @author Pactera-NEN
 * @date 2016年6月6日-下午3:08:08
 */
public class CbnStockEntityDao {
	
	private final static String stkCollName="anno_code";
	private final static String blkCollName="industrial_segments";

	
	public static Map<String,String> queryStkCodeEnMapper(MongoClient mongoClient, String mongoDbName){
		Map<String,String> revMapper=new HashMap<String,String>();
		
		MongoDatabase db=mongoClient.getDatabase(mongoDbName);
		MongoCollection<Document> coll=db.getCollection(stkCollName);
		BasicDBObject requireFields=new BasicDBObject();
		requireFields.append("_id", 0);
		requireFields.append("code", 1);
		requireFields.append("simple_name_en", 1);
		FindIterable<Document> fi= coll.find().projection(requireFields);
		for(Document doc:fi) {
			revMapper.put(doc.getString("code"), doc.getString("simple_name_en"));
		}
		
		return revMapper;
	}
	
	public static Map<String,String> queryBlkCodeEnMapper(MongoClient mongoClient, String mongoDbName) {
		Map<String,String> revMapper=new HashMap<String,String>();
		
		MongoDatabase db=mongoClient.getDatabase(mongoDbName);
		MongoCollection<Document> coll=db.getCollection(blkCollName);
		BasicDBObject requireFields=new BasicDBObject();
		requireFields.append("_id", 0);
		requireFields.append("segment_code", 1);
		requireFields.append("eng_name", 1);
		FindIterable<Document> fi= coll.find().projection(requireFields);
		for(Document doc:fi) {
			revMapper.put(doc.getString("segment_code"), doc.getString("eng_name"));
		}
		
		return revMapper;
	}

}
