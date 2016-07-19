package com.papi.quartz.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

public class CommonUtils {
	
	public static String subDeviceTypeCode(String idSenseDevice){
		return (String) idSenseDevice.subSequence(2, 6);
	}
	
    public static String file2String(File f, String charset) { 
        String result = null; 
        try { 
                result = stream2String(new FileInputStream(f), charset); 
        } catch (FileNotFoundException e) { 
                e.printStackTrace(); 
        } 
        return result; 
    }
    
    public static String stream2String(InputStream in, String charset) { 
        StringBuffer sb = new StringBuffer(); 
        try { 
                Reader r = new InputStreamReader(in, charset); 
                int length = 0; 
                for (char[] c = new char[1024]; (length = r.read(c)) != -1;) { 
                        sb.append(c, 0, length); 
                } 
                r.close(); 
        } catch (UnsupportedEncodingException e) { 
                e.printStackTrace(); 
        } catch (FileNotFoundException e) { 
                e.printStackTrace(); 
        } catch (IOException e) { 
                e.printStackTrace(); 
        } 
        return sb.toString(); 
    }
    
	public static String reqtoString(HttpServletRequest request)
	{
		String data = null;
		ServletInputStream sis;
		try {
			sis = request.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final int BUFFER_SIZE = 1024;
			byte[] buffer = new byte[BUFFER_SIZE];			
			int bLen=0;
			
			while((bLen=sis.read(buffer))>0){
				baos.write(buffer,0,bLen);
			}	
			
		    data=new String(baos.toByteArray(),"UTF-8");	
			
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		return data;
	}
	
	public static Properties getNettyProperties(){
		Properties properties = new Properties();		
		//String fileName = req.getServletContext().getInitParameter("netty-file");
		//InputStream in = CommonUtils.class.getClassLoader().getResourceAsStream(fileName);
		InputStream inputStream = CommonUtils.class.getClassLoader().getResourceAsStream("netty.properties");		
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return properties;
	}
	
	//输出消息
	public static void write(String content, HttpServletResponse response)
			throws IOException {
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write(content);
			pw.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			if (pw != null)
				pw.close();
		}
	}

	public static void  writeXML(String xml, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/xml; charset=UTF-8");
		write(xml, response);
	}

	public static void writeHTML(String html, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html; charset=UTF-8");
		write(html, response);
	}

	public static void writeTEXT(String html, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/plain; charset=UTF-8");
		write(html, response);
	}

	public static void writeJson(Object obj, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/plain; charset=UTF-8");
				
		write(JSONObject.fromObject(obj).toString(), response);
	}
}
