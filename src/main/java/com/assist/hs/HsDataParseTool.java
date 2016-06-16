package com.assist.hs;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Pactera-NEN
 * @date 2016年6月2日-下午2:59:35
 */
public class HsDataParseTool {
	
	/**
	 * HQ OriData parse
	 * @param oriData
	 * @return
	 */
	public static LinkedHashMap<String, Map<String,Object>> parseHqOriData(Map<String, List<Object>> oriData){
		LinkedHashMap<String, Map<String,Object>> rev=new LinkedHashMap<String, Map<String,Object>>();
		
		List<Object> fieldNames=oriData.get("fields");
		int fieldCount=fieldNames.size();
		for(String key:oriData.keySet()){
			if(key.equals("fields")) continue;
			
			Map<String,Object> pVal=new HashMap<String,Object>();
			List<Object> oVal=oriData.get(key);
			for(int i=0; i<fieldCount; i++){
				pVal.put((String)fieldNames.get(i), oVal.get(i));
			}
			rev.put(key, pVal);
		}
		
		return rev;
	}

}
