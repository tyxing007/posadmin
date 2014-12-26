package mmb.posadmin.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLUtil {

	/**
	 * 获取Http请求的响应数据，可以设置请求体的内容
	 * @param url URL地址
	 * @param requestContent 请求体内容
	 * @return
	 * @throws Exception
	 */
	public static String getResponseResult(String url, String requestContent) throws Exception {
		HttpURLConnection conn = null;
		BufferedWriter out = null;
		BufferedReader br = null;
		StringBuilder json = new StringBuilder();
		try {
			//发送请求
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestProperty("content-type", "text/html");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(500000);
			conn.setReadTimeout(500000);
			conn.connect();
			out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
			out.write(requestContent);
			out.flush();

			//返回结果
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = br.readLine()) != null) {
				json.append(line);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {}
			}
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
			if(conn != null) {
				conn.disconnect();
			}
		}
		
		return json.toString();
	}
	
	/**
	 * 获取Http请求的响应数据
	 * @param url URL地址
	 * @return 请求的返回数据
	 * @throws Exception 
	 */
	public static String getResponseResult(String url) throws Exception {
		HttpURLConnection huc = null;
		BufferedReader br = null;
		StringBuilder json = new StringBuilder();
		try {
			//建立连接
			huc = (HttpURLConnection) new URL(url).openConnection();
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
			System.setProperty("sun.net.client.defaultReadTimeout", "30000");
			huc.connect();
			
			//读取json格式字符串
			br = new BufferedReader(new InputStreamReader(huc.getInputStream(), "UTF-8"));
			String line;
			while((line=br.readLine()) != null) {
				json.append(line);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
			if(huc != null) {
				huc.disconnect();
			}
		}
		
		return json.toString();
	}
}
