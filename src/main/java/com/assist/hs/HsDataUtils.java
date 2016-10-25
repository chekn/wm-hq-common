package com.assist.hs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mysql.jdbc.Driver;
import com.yicai.dt.modhs.client.HsVisitorClient;


@Service
public class HsDataUtils {
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdf8=new SimpleDateFormat("yyyyMMdd"); 
	
	/**
	 * 返回品种类型代码及所对应的合约代码规则
	 * */
	public Map<String,String> getCodeRules(){
		Map<String,String> codeRulesMap=new HashMap<String,String>();
		codeRulesMap.put("AU","^AU\\d{4}\\.XSGE$");
		codeRulesMap.put("RB","^RB\\d{4}\\.XSGE$");
		codeRulesMap.put("CU","^CU\\d{4}\\.XSGE$");
		codeRulesMap.put("RU","^RU\\d{4}\\.XSGE$");
		codeRulesMap.put("TA","^TA\\d{3}\\.XZCE$");
		codeRulesMap.put("SR","^SR\\d{3}\\.XZCE$");
		codeRulesMap.put("ZC","^ZC\\d{3}\\.XZCE$");
		codeRulesMap.put("CR","^CF\\d{3}\\.XZCE$");
		codeRulesMap.put("A","^A\\d{4}\\.XDCE$");
		codeRulesMap.put("Y","^Y\\d{4}\\.XDCE$");
		codeRulesMap.put("I","^I\\d{4}\\.XDCE$");
		return codeRulesMap;
	}
	

	
	/**
	 *查询指定交易日前一日确定的主力合约及第二合约情况（该方法独立使用时，用于在交易时段查询该交易时段应该播报的主力合约）
	 *@param initDate 指定交易日
	 *@param contract_type 合约类型代码
	 *@param conn mysql数据库的连接
	 *@return {{品种代码,交易日期,产品代码(主力合约),合约顺序(0),是否播报第二主力合约(0/1)},{品种代码,交易日期,产品代码(第二合约),合约顺序(1),是否播报第二主力合约(0/1)}}
	 * */
	public List<List> getPrevMainContractInfo(Date initDate,String contract_type){
		Connection conn=getMysqlDBConn();
		List<List> prevMainContractInfo=new ArrayList<List>();
		initDate=getUniformedTime(initDate);
		String sql="select a.contract_type,a.trd_date,a.prod_code,a.contract_order,a.rptSecCon from (select contract_type,max(trd_date) as trd_date from main_contracts where trd_date<'"+sdf.format(initDate)+"' and contract_type='"+contract_type+"') as t left join main_contracts as a on t.contract_type=a.contract_type and t.trd_date=a.trd_date";
		try {
			PreparedStatement pst=conn.prepareStatement(sql);
			ResultSet rlts=pst.executeQuery();
			while(rlts.next()){
				prevMainContractInfo.add(Arrays.asList(rlts.getString("contract_type"),rlts.getString("trd_date"),rlts.getString("prod_code"),rlts.getInt("contract_order"),rlts.getInt("rptSecCon")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return prevMainContractInfo;
	}
	
	public Double getRMBUSRate(){
		Connection conn=getMysqlDBConn();
		Double RMBUSRate=0.0;
		String sql="select central_parity from central_parity where trd_date=(select max(trd_date) as mtd from central_parity)";
		try{
			PreparedStatement pst=conn.prepareStatement(sql);
			ResultSet rlts=pst.executeQuery();
			while(rlts.next()){
			    RMBUSRate=rlts.getDouble("central_parity");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return RMBUSRate;
	}
	
	
	/**
	 * 检查某代码是否为所需要的产品合约代码
	 * */
	private boolean checkCodeRule(String prodCode,String[] prodCodeRules){
		boolean isCode=false;
		for(String rule:prodCodeRules){
			Pattern p=Pattern.compile(rule);
			Matcher m=p.matcher(prodCode);
			if(m.find())isCode=true;
		}
		return isCode;
	}
	

	
	/**
	 *连接 mysql数据库 
	 * */
	public Connection getMysqlDBConn(){
		Connection connMysql=null;
		String url="jdbc:mysql://rdsg50297hexg250tqo0o.mysql.rds.aliyuncs.com:3306/yyg_analysis";
	    Properties info=new Properties();
	    info.put("user","yyg");
	    info.put("password","yygAnalysis008");
		try {
			Driver dvr = new Driver();
			connMysql=dvr.connect(url,info);
		} 
	    catch (SQLException e) {
			e.printStackTrace();
		}
	    return connMysql;
	}
	
	/**
	 * 将恒生接口返回的字段名称与数值序列对应
	 * @param fields 字段名称
	 * @param dataList 数值序列
	 * @return namedData Document<字段名,数值> 
	 * */

	public Document getNamedData(List<String>fields,List dataList){

		Document namedData=new Document();
		for(int i=0;i<fields.size();i++){
			namedData.put(fields.get(i),dataList.get(i));
		}
		return namedData;
	}
	
	/**
	 * 查询数据库，以代码规则来获得某个品种的所有合约代码
	 * @param codeRule 某个品种的代码规则(正则),如果为空，则表示数据库中的所有代码
	 * */
	public List<String> queryFutureContractList(MongoClient mongoClient,String codeRule){
		MongoCollection<Document> collection = mongoClient.getDatabase("writingmaster_test").getCollection("futurecontract_list");
		Document doc=collection.find(Filters.eq("stat","current")).projection(new BasicDBObject("future_contract_list",1)).first();
		if(codeRule.equals("")){	
		    return (List)doc.get("future_contract_list");
		}else{
			System.out.println("codeRule:"+codeRule);
			Pattern pRule=Pattern.compile(codeRule);
			List<String> fcl=(List)doc.get("future_contract_list");
			List<String> aSortOfFutureContractList=new ArrayList();
			for(String fc:fcl){
				Matcher mRule=pRule.matcher(fc);
				if(mRule.find()){
					aSortOfFutureContractList.add(fc);
				}
			}
			System.out.println("length of aSortOfFutureContractList:"+aSortOfFutureContractList.size());
			return aSortOfFutureContractList;
		}
	}
	
	/**
	 * 判断当天是否为交易日
	 * */
	public boolean checkTradingDate(MongoClient mongoClient,String mkt,Date today){
	    Map<String,Integer> mktDates=queryMktDates(mongoClient);
	    return (String.valueOf(mktDates.get(mkt)).equals(sdf8.format(today)));
	}

	
	/**
	 * 查询数据库，获得所有三个各市场的最新交易时间
	 * */
	public Map<String,Integer> queryMktDates(MongoClient mongoClient){
		MongoCollection<Document> collection = mongoClient.getDatabase("writingmaster_test").getCollection("futurecontract_list");
		Document doc=collection.find(Filters.eq("stat","current")).projection(new BasicDBObject("mkt_date",1)).first();
        return (Map<String,Integer>)doc.get("mkt_date");
	}
	
	
	
	/**
	 * 将List合成为字符串
	 * */
	private String convertListToStr(List<String> dataList,String sep){
		String sepStr="";
		int i=0;
	    for(String d:dataList){
	    	if(i==0){
	    	    sepStr=sepStr+d;
	    	}else{
	    		sepStr=sepStr+","+d;
	    	}
	    	i++;
	    }
		return sepStr;
	}
	
	/**
	 * 获取当天日期，将时间统一调整致15:00
	 * */
	private Date getUniformedTime(Date date){
		Calendar curTime=Calendar.getInstance();
		curTime.setTime(date);
		curTime.set(Calendar.HOUR_OF_DAY,15);//24小时制,如果12时制用Hour
		curTime.set(Calendar.MINUTE,0);
		curTime.set(Calendar.SECOND,0);
		curTime.set(Calendar.MILLISECOND,0);
	    return curTime.getTime();	
	}
	
	
	
	
}

