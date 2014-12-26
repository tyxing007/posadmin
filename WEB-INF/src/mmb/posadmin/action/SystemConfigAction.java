package mmb.posadmin.action;

import mmb.posadmin.domain.SystemConfig;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionSupport;

public class SystemConfigAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(SystemConfigAction.class);
	
	private SystemConfig systemConfig;
	
	public SystemConfig getSystemConfig() {
		return systemConfig;
	}

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	/**
	 * 跳转到系统配置信息页面
	 * @return
	 */
	public String toSystemConfigView() {
		//获取系统配置信息
		this.systemConfig = SystemConfig.getInstance();
		return "systemConfig";
	}

}
