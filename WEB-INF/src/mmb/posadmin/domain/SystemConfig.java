package mmb.posadmin.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import mmb.posadmin.service.SystemConfigService;

import org.apache.log4j.Logger;

/**
 * 系统配置信息
 * <br/>为了便于部署店面系统，把系统配置信息保存到数据库中
 * @author likaige
 */
public class SystemConfig implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(SystemConfig.class);

	/**
	 * id
	 */
	public int id;
	
	/**
	 * 店面编号
	 */
	public String shopCode;
	
	/*****************数据同步时间 开始***********************/
	/**
	 * 销售订单上次提交时间
	 * <br>提交到中心库
	 */
	public Timestamp saledOrderLastSubmitTime;
	
	/**
	 * 租赁订单上次提交时间
	 * <br>提交到中心库
	 */
	public Timestamp leaseOrderLastSubmitTime;
	
	/**
	 * 进销存上次提交时间
	 * <br>提交到中心库
	 */
	public Timestamp invoiceLastSubmitTime;
	
	/**
	 * 会员信息上次提交时间
	 * <br>提交到中心库
	 */
	public Timestamp memberLastSubmitTime;
	
	/**
	 * 会员信息上次同步时间
	 * <br>从中心库同步
	 */
	public Timestamp memberLastSyncTime;
	/*****************数据同步时间 结束***********************/
	
	/******************版本信息 开始**************************/
	/**
	 * 店面系统的版本号
	 */
	public String versionNumber;
	
	/**
	 * 店面系统的版本名称
	 */
	public String versionName;
	
	/**
	 * 店面系统的版本描述
	 */
	public String versionDesc;
	/******************版本信息 结束**************************/
	
	
	/**
	 * 创建系统配置信息对象的实例
	 * @return
	 */
	public static SystemConfig getInstance() {
		SystemConfig instance = new SystemConfigService().getSystemConfig();
		if(instance == null) {
			log.error("从数据库获取系统配置信息时出现异常，系统配置信息为空！", new NullPointerException());
		}
		return instance;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getShopCode() {
		return shopCode;
	}

	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}

	public Timestamp getSaledOrderLastSubmitTime() {
		return saledOrderLastSubmitTime;
	}

	public void setSaledOrderLastSubmitTime(Timestamp saledOrderLastSubmitTime) {
		this.saledOrderLastSubmitTime = saledOrderLastSubmitTime;
	}

	public Timestamp getLeaseOrderLastSubmitTime() {
		return leaseOrderLastSubmitTime;
	}

	public void setLeaseOrderLastSubmitTime(Timestamp leaseOrderLastSubmitTime) {
		this.leaseOrderLastSubmitTime = leaseOrderLastSubmitTime;
	}

	public Timestamp getInvoiceLastSubmitTime() {
		return invoiceLastSubmitTime;
	}

	public void setInvoiceLastSubmitTime(Timestamp invoiceLastSubmitTime) {
		this.invoiceLastSubmitTime = invoiceLastSubmitTime;
	}

	public Timestamp getMemberLastSubmitTime() {
		return memberLastSubmitTime;
	}

	public void setMemberLastSubmitTime(Timestamp memberLastSubmitTime) {
		this.memberLastSubmitTime = memberLastSubmitTime;
	}

	public Timestamp getMemberLastSyncTime() {
		return memberLastSyncTime;
	}

	public void setMemberLastSyncTime(Timestamp memberLastSyncTime) {
		this.memberLastSyncTime = memberLastSyncTime;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getVersionDesc() {
		return versionDesc;
	}

	public void setVersionDesc(String versionDesc) {
		this.versionDesc = versionDesc;
	}
	
}
