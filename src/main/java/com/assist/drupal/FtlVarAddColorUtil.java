package com.assist.drupal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Pactera-NEN
 * @date 2016年2月26日-下午2:12:40
 */
public class FtlVarAddColorUtil {
	private static Pattern var = Pattern.compile("\\$\\{\\b*[^\\}]*?\\b*?\\}");
	private static String str1 = "<font color=\"blue\">";
	private static String str2 = "</font>";


    public static void addFontColorTag(String fDir) throws IOException{
		List<String> fileNameList = new ArrayList<String>();
		getFiles(fDir, fileNameList); //返回fDir路径下的所有文件名放到fileNameList
		//		Pattern ftlPat1 = Pattern.compile("^((?!color_).)*$");
		for(int i = 0; i < fileNameList.size(); i++){
			String fileName = fileNameList.get(i);
			Pattern p = Pattern.compile("color_");
			Matcher matP = p.matcher(fileName);
			if(!matP.find()){
				String origFile = fDir + "/" + fileName;
				String outFile = fDir + "/color_" + fileName;
				createFileWithColorTag(origFile,outFile);
			}
		}
	}

	
	public static void createFileWithColorTag(String origFtl, String colorFtl) throws IOException{
		BufferedReader br = null;
		Reader reader = null;
		Writer fw = null;
		try {
			File file = new File(colorFtl);
			if (file.exists()){
				file.delete();
			}else{
				file.createNewFile();
			}
			fw = new OutputStreamWriter(new FileOutputStream(file),"utf-8");

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("为colorftl创建filewriter 失败");
		}
		try {
			reader = new InputStreamReader(new FileInputStream(origFtl),"utf-8");
			br = new BufferedReader(reader);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("读取文件：" + origFtl + "失败！");
		}
		String newLine = null;
		try {
			while((newLine = br.readLine()) != null){
				if (!newLine.isEmpty()){
					Matcher m = var.matcher(newLine);
					while (m.find()){
						String str = m.group();
						newLine = newLine.replace(str, str1+m.group()+str2);
					}
					fw.write(newLine);
					fw.write("\r\n");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fw.close();
		br.close();
		reader.close();
	}
	
	// 获取路径path下的所有文件名，存放在List<String>
	public static List<String> getFiles(String path, List<String> data) {
		File f = new File(path);
		if (f.isDirectory()) {
			File[] fs = f.listFiles();
			for (int i = 0; i < fs.length; i++) {
				data = getFiles(fs[i].getPath(), data);
			}
		} else if (f.getName().endsWith(".ftl")) {
			data.add(f.getName());
		}
		return data;
	}
	
	
}
