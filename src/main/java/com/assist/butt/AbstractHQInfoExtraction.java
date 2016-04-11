package com.assist.butt;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.assist.mongo.PickLastoneRcdTool;
import com.mongodb.MongoClient;
import com.yicai.dt.modhs.client.HsVisitorClient;
import com.yicai.medialab.writingmaster.extraction.core.api.InfoExtraction;

/**
 * 
 * @author Pactera-NEN
 * @date 2016年4月5日-下午2:38:30
 */
public abstract class AbstractHQInfoExtraction implements InfoExtraction {

	private Logger logger=LoggerFactory.getLogger(this.getClass());
	
	
	
	public abstract Map<String,Object> copyDrupalImporter(String comparableStr, HsVisitorClient hsv);
	
	@Override
	public abstract String getUniqueName();

	@Override
	public Map<String, Object> extractFromRawData(Map<String, Object> rawData) {
		logger.info(":::::::"+this.getUniqueName()+":::::::");
		HsVisitorClient hsv=(HsVisitorClient) rawData.get(InfoExtraction.HQ_HSCLIENT_KEY);
		MongoClient mongoClient=(MongoClient) rawData.get(InfoExtraction.HQ_MONGOCLIENT_KEY);
		String mongoDbName=(String) rawData.get(InfoExtraction.HQ_MONGODBNAME_KEY);
		
		Map<String,Object> revData=null;
		Map<String,Object> ftlMap=null;
		
		try {
			Map<String,Object> lastRunData=PickLastoneRcdTool.queryLastRunData(mongoClient, mongoDbName, this.getUniqueName());
			Integer state =(Integer) lastRunData.get("state");
			Date startDate =(Date) lastRunData.get("startDate");
	
			final int defTimeOutSecond=15;
			
			//如果结束启动下一个流程
			if(state==null || state.intValue()!=0 || System.currentTimeMillis()-startDate.getTime()>defTimeOutSecond*1000){
				Map<String,Object> currRunData=PickLastoneRcdTool.insertRunData(mongoClient, mongoDbName, this.getUniqueName());
				String currId=currRunData.get("_id").toString();
				Date currStartDate=(Date) currRunData.get("startDate");
				
				boolean isBreak=false;
				String comparableStr=(String) lastRunData.get("comparableStr");
				revData=this.copyDrupalImporter(comparableStr, hsv);
				ftlMap=(Map<String, Object>) revData.get(this.getUniqueName());
				if((Boolean)revData.get(InfoExtraction.HQ_RETIBREAK_KEY)){
					revData.clear();
					isBreak=true;
				}
				
				if(System.currentTimeMillis()-currStartDate.getTime()<defTimeOutSecond)
					PickLastoneRcdTool.updateRunData(mongoClient, mongoDbName, currId, isBreak?1:2, isBreak?comparableStr:(String)ftlMap.get("comparableStr"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("return::"+revData);
		return revData;
	}

}
