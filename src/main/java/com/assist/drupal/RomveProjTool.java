package com.assist.drupal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.yicai.medialab.writingmaster.drupalhs.constant.TextFormat;
import com.yicai.medialab.writingmaster.drupalhs.importer.WMResultImporter;
import com.yicai.medialab.writingmaster.drupalhs.model.NewsDraft;
import com.yicai.medialab.writingmaster.drupalhs.model.NewsSourceItem;
import com.yicai.medialab.writingmaster.drupalhs.util.FreeMarkerUtil;
import com.yicai.medialab.writingmaster.extraction.core.api.InfoExtraction;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 
 * @author Pactera-NEN
 * @date 2016年3月10日-下午7:34:52
 */
public class RomveProjTool {
	
	public static void main(String[] args){
		
		File srcFtl=new File("C:\\Users\\Pactera-NEN\\git\\wm-ex-marketmovement");
		RomveProjTool sdt=new RomveProjTool();
		try {
			sdt.fileScan(srcFtl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void fileScan(File dir) throws IOException{
		for(File ufile:dir.listFiles()){
			if(ufile.isDirectory()){
				for(File uufile:ufile.listFiles()){
					if(!uufile.getName().equals("src") && !uufile.getName().equals("pom.xml"))
						FileUtils.forceDelete(uufile);
					
					if(uufile.getName().equals("pom.xml")){
						String content=FileUtils.readFileToString(uufile);
						String newContent=content.replace("<artifactId>wm-ex-social</artifactId>", "<artifactId>wm-ex-marketmovement</artifactId>");
						FileUtils.writeStringToFile(uufile, newContent, "utf-8");
					}
				
				}
				System.out.println(ufile.getName()+":整理完成");
			}
		}
	}
	
	
}
