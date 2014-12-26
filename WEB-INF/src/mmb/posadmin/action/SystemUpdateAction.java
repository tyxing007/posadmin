package mmb.posadmin.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import mmb.posadmin.domain.SystemConfig;
import mmb.posadmin.service.SystemConfigService;
import mmb.posadmin.service.SystemUpdateService;
import mmb.posadmin.util.ResponseUtils;
import mmboa.util.Constants;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

public class SystemUpdateAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(SystemUpdateAction.class);
	
	/**
	 * 跳转到系统更新页面
	 * @return
	 */
	public String toSystemUpdateView() {
		HttpServletRequest request = ServletActionContext.getRequest();
		
		try {
			//获取当前版本信息
			SystemConfig systemConfig = SystemConfig.getInstance();
			
			//如果版本名称为空，则到中心库获取
			if(systemConfig.getVersionName() == null) {
				//向中心库获取最新版本信息
				String result = new SystemUpdateService().getLatestShopVersion();
				
				//解析JSON字符串
				SystemConfig config = new Gson().fromJson(result, SystemConfig.class);
				if(systemConfig.getVersionNumber().equals(config.getVersionNumber())) {
					systemConfig.setVersionName(config.getVersionName());
					systemConfig.setVersionDesc(config.getVersionDesc());
					
					//保存到数据库
					new SystemConfigService().updateSystemConfig(systemConfig);
				}
			}
			
			request.setAttribute("versionNumber", systemConfig.getVersionNumber());
			request.setAttribute("versionName", systemConfig.getVersionName());
			request.setAttribute("versionDesc", systemConfig.getVersionDesc());
		} catch (Exception e) {
			log.error("获取当前版本信息时出现异常：", e);
		}
		
		return "systemUpdate";
	}

	/**
	 * 检查系统更新
	 * <br/>获取最新系统版本信息
	 * @return
	 */
	public void getLatestShopVersion() {
		String result = new SystemUpdateService().getLatestShopVersion();
		ResponseUtils.renderText(ServletActionContext.getResponse(), result);
	}
	
	/**
	 * 下载更新包
	 */
	public void downloadShopVersion() {
		boolean success = false;
		HttpServletRequest request = ServletActionContext.getRequest();
		String versionNumber = request.getParameter("versionNumber");
		String suffix = request.getParameter("suffix");
		
		HttpURLConnection conn = null;
		BufferedWriter out = null;
		InputStream is = null;
		try {
			//发送请求
			String downloadShopVersionURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("downloadShopVersionURL");
			conn = (HttpURLConnection) new URL(downloadShopVersionURL).openConnection();
			conn.setRequestProperty("content-type", "text/html");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(50000);
			conn.setReadTimeout(50000);
			conn.connect();
			out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
			String requestContent = "{\"versionNumber\":\""+versionNumber+"\"}";
			out.write(requestContent);
			out.flush();

			//返回结果
			is = conn.getInputStream();
			
			//保存更新包
			File updateFile = new File(Constants.config.getProperty("saveSystemUpdatePackagePath") + versionNumber + suffix);
			if(!updateFile.getParentFile().exists()) {
				updateFile.getParentFile().mkdirs();
			}
			FileUtils.copyInputStreamToFile(is, updateFile);
			
			success = true;
		} catch (Exception e) {
			log.error("下载更新包时出现异常：", e);
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {}
			}
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {}
			}
			if(conn != null) {
				conn.disconnect();
			}
		}
		
		//返回结果
		String result = "{'success':"+success+", 'filePath':'"+Constants.config.getProperty("saveSystemUpdatePackagePath") + versionNumber + suffix +"'}";
		ResponseUtils.renderText(ServletActionContext.getResponse(), result);
	}
	
}
