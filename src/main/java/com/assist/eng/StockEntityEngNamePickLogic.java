package com.assist.eng;

import java.util.Map;

/**
 * 
 * @author Pactera-NEN
 * @date 2016年6月6日-下午3:37:12
 */
public class StockEntityEngNamePickLogic {

	private Map<String,String> cbnStkData;
	private Map<String,String> cbnBlkData;
	private Map<String,String> gilStkData;
	
	public StockEntityEngNamePickLogic(Map<String,String> cbnStkData, Map<String,String> cbnBlkData, Map<String,String> gilStkData) {
		this.cbnStkData=cbnStkData;
		this.cbnBlkData=cbnBlkData;
		this.gilStkData=gilStkData;
	}
	
	public String getStkEngName(String stkCode){
		String cbnEng=cbnStkData.get(stkCode);
		if(cbnEng!=null)
			return cbnEng;
		else
			return gilStkData.get(stkCode);
	}
	
	public String getBlkEngName(String blkCode){
		return cbnBlkData.get(blkCode);
	}
	
}
