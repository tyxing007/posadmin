package mmb.posadmin.service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mmb.posadmin.domain.SystemConfig;
import mmb.posadmin.util.HttpURLUtil;
import mmboa.util.Constants;
import mmboa.util.db.DbUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.stream.JsonReader;

public class SystemUpdateService {
	
	private static Logger log = Logger.getLogger(SystemUpdateService.class);
	
	//记录本次更新的版本号
	private String versionNumber;

	/**
	 * 更新系统
	 * @throws IOException 
	 */
	public void update() throws Exception {
		try {
			//获取店面当前的版本号
			SystemConfig systemConfig = SystemConfig.getInstance();
			String nowVersionNumber = systemConfig.getVersionNumber();
			
			//根据版本号获取更新的SQL语句列表
			List<String> sqlList = this.getUpdateSqlList(nowVersionNumber);
			
			//判断是否有更新语句
			if(!sqlList.isEmpty()) {
				//打印日志
				log.info("新版本中需要更新的SQL语句：");
				for(String sql : sqlList) {
					log.info(sql);
				}
				
				//执行数据库更新语句
				this.executeSqlList(sqlList);
				
				//更新成功之后，修改店面的版本号
				systemConfig.setVersionNumber(versionNumber);
				systemConfig.setVersionName(null); //版本名称设置为Null
				systemConfig.setVersionDesc(null); //版本描述设置为Null
				new SystemConfigService().updateSystemConfig(systemConfig);
			} else {
				log.info("新版本中没有需要更新的SQL语句！");
			}
		} catch (Exception e) {
			log.error("版本升级，更新数据库时出现异常：", e);
			throw e;
		}
	}
	
	/**
	 * 执行数据库更新语句
	 * @param sqlList 需要更新的SQL语句列表
	 * @throws Exception
	 */
	private void executeSqlList(List<String> sqlList) throws Exception {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		boolean oldAutoCommit = true;
		try {
			//记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			//关闭自动提交事务
			conn.setAutoCommit(false);
			
			log.info("执行SQL开始");
			for(String sql : sqlList) {
				log.info("执行SQL："+sql);
				ps = conn.prepareStatement(sql);
				ps.executeUpdate();
			}
			log.info("执行SQL结束");
			
			//提交事务
			conn.commit();
		}catch(Exception e){
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {}
			log.error("执行数据库更新语句时出现异常：", e);
			throw e;
		}finally{
			DbUtil.closeConnection(null, ps, null);
			if(conn != null){
				try {
					//还原事务提交方式
					conn.setAutoCommit(oldAutoCommit);
					conn.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	/**
	 * 根据版本号获取更新的SQL语句列表
	 * @param versionNumber 店面当前的版本号
	 * @return
	 * @throws Exception
	 */
	private List<String> getUpdateSqlList(String versionNumber) throws Exception {
		List<String> sqlList = new ArrayList<String>();
		try {
			//数据库更新文件
			File updatedbFile = FileUtils.getFile(Constants.CONFIG_PATH + "updatedb.sql");
			
			//过滤出需要执行的SQL语句
			List<String> allLineList = FileUtils.readLines(updatedbFile, "UTF-8");
			String nowNumber = null;
			for(String line : allLineList) {
				//空白行
				if(StringUtils.isBlank(line)) {
					continue;
				}
				//版本号行
				else if(line.matches("-*V\\d{10}")) {
					nowNumber = line.substring(line.indexOf("V"), line.indexOf("V")+11);
					if(sqlList.isEmpty()) {
						this.versionNumber = nowNumber;
					}
					if(Long.parseLong(nowNumber.substring(1)) <= Long.parseLong(versionNumber.substring(1))) {
						break;
					}
				}
				//注释行
				else if(line.startsWith("--")) {
					continue;
				}
				//sql行
				else {
					//不指定版本的SQL不执行
					if(StringUtils.isNotBlank(this.versionNumber)) {
						sqlList.add(line);
					}
				}
			}
		} catch (Exception e) {
			log.error("根据版本号获取更新的SQL语句列表时出现异常：", e);
			throw e;
		}
		return sqlList;
	}
	
	/**
	 * 向中心库获取最新版本信息
	 * @return
	 */
	public String getLatestShopVersion() {
		String result = "";
		try {
			//获取最新系统版本信息
			String getLatestShopVersionURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("getLatestShopVersionURL");
			result = HttpURLUtil.getResponseResult(getLatestShopVersionURL);
		} catch (Exception e) {
			result = "error";
			log.error("获取最新系统版本信息时出现异常：", e);
		}
		return result;
	}

	/**
	 * 检查是否需要更新系统版本信息
	 * @return
	 */
	public String checkNeedUpdate(){
		String responseJson = "";
		try{
			String versionInfo = this.getLatestShopVersion();
			if(StringUtils.isBlank(versionInfo) || "error".equals(versionInfo)) {
				responseJson =  "{\"isUpdated\":\"false\"}";
			}else{
				JsonReader jr = new JsonReader(new StringReader(versionInfo));
				jr.beginObject();
				String atrrName = "";
				String newVersionNumber = "";
				while(jr.hasNext()){
					atrrName = jr.nextName();
					if("versionNumber".equals(atrrName)){
						newVersionNumber = jr.nextString();
					}else{
						jr.skipValue();
					}
				}
				jr.endObject();
	            if(newVersionNumber.equals(SystemConfig.getInstance().versionNumber)){
	            	responseJson =  "{\"isUpdated\":\"false\"}";
	            }else{
	            	responseJson = "{\"isUpdated\":\"true\"}";
	            }
			}
		}catch (Exception e){
			log.error("检查是否需要更新系统版本信息时出现异常：", e);
		}
		return responseJson;
	}
	
	
}
