package com.test.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;


public class newLog {
	
	public static void main(String[] args) throws Exception{
		long sss = System.currentTimeMillis();
		//指定存放日志目录，任意层级
		//输出文件在项目文件夹下
		procDir("C:\\Users\\jtao\\Desktop\\日志");   
		System.out.println((System.currentTimeMillis() - sss)/1000);
	}
	

	private static Pattern compile = Pattern.compile("([\\w+\\.+]+\\w?(Exception|Error)){1}.*");
	private static int lineNumbers;
	private static String fileName;
	private static List<String> prev = Lists.newLinkedList();
	private static List<String> next = Lists.newArrayList();
	private static void procLine(String line) {
		
		Matcher matcher = compile.matcher(line);
		if(matcher.matches()) {
			String exception = matcher.group(1);
			String file = exception + ".txt";
			appendLine(file,"===================================================================");
			appendLine(file,"=== File "+fileName+":	lineNumber "+lineNumbers+" ===");
			appendLine(file,"=== PREV START ===");
			for(String l : prev) {
				appendLine(file,l);
			}
			appendLine(file,"=== PREV END ===");
			appendLine(file,line);
			for(String l : next) {
				appendLine(file,l);
			}
		}
	}
	
	private static void procFile(String filePath) throws Exception{
		File file = new File(filePath);   
		BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));

		BufferedReader reader = new BufferedReader(new InputStreamReader(fis,"GBK"),5*1024*1024);// 用5M的缓冲读取文本文件  
		String line = "";
		fileName = filePath;
		lineNumbers = 1;

		while((line = reader.readLine()) != null) {
			next.add(line);
			if(next.size() > 50) {
				String remove = next.remove(0);
				procLine(remove);
				lineNumbers++;
				prev.add(remove);
				if(prev.size() > 20) {
					prev.remove(0);
				}
			}

		}
		
		
	}
	
	private static void appendLine(String file,String content) {
		try {
			FileWriter writer = new FileWriter(file, true);
			writer.write(content+"\r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void procDir(String dir) throws Exception{
		File f = new File(dir);
		for(File sub : f.listFiles()) {
			if(sub.isDirectory()) {
				procDir(sub.getAbsolutePath());
			} else {
				procFile(sub.getAbsolutePath());
			}
		}
	}

}


