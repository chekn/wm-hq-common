package com.assist.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

public class GilSWIndSegmentDao {
	
	public static Map<String,String> queryStkSWindSegment(JdbcTemplate gilJt) {
		Map<String,String> revMapper=new HashMap<String,String>();
		
		String sql="SELECT SecuCode,sec.CompanyCode,FirstIndustryCode,FirstIndustryName,SecondIndustryCode,SecondIndustryName FROM secumain AS sec inner JOIN (SELECT DISTINCT 	l.CompanyCode,	l.InfoPublDate,	l.FirstIndustryCode,	l.FirstIndustryName,	l.SecondIndustryCode,	l.SecondIndustryNameFROM 	(		SELECT 			CompanyCode,			max(InfoPublDate) AS MaxPubDate 		FROM 			(				SELECT 					*				FROM 					lc_exgindustry				WHERE 					Standard = 24			) AS sw 		GROUP BY 			CompanyCode	) AS m INNER JOIN (	SELECT 		*	FROM 		lc_exgindustry	WHERE 		standard = 24) AS l ON m.maxPubDate = l.InfoPublDate AND m.CompanyCode = l.CompanyCode) AS ind ON sec.CompanyCode = ind.CompanyCode WHERE SecuCategory = 1 AND SecuCode REGEXP '^[0|3|6]' ORDER BY SecuCode";
		List<Map<String,Object>> stkMappers= gilJt.queryForList(sql);
		for(Map<String,Object> stkMapper:stkMappers) {
			revMapper.put((String)stkMapper.get("SecuCode"), (String)stkMapper.get("SecondIndustryCode"));
		}
		
		return revMapper;
    }
}





