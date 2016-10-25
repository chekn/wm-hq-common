package com.assist.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 
 * @author Pactera-NEN
 * @date 2016年5月31日-上午10:50:27
 */
public class GilStockEntityDao {
	
	public static Map<String,String> queryStkCodeEnMapper(JdbcTemplate gilJt) {
		Map<String,String> revMapper=new HashMap<String,String>();
		
		String sql="select SecuCode, EngName from secumain where SecuCategory=1";
		List<Map<String,Object>> stkMappers= gilJt.queryForList(sql);
		for(Map<String,Object> stkMapper:stkMappers) {
			revMapper.put((String)stkMapper.get("SecuCode"), (String)stkMapper.get("EngName"));
		}
		return revMapper;
	}
	
}
