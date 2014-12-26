package mmb.posadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;

import mmb.posadmin.domain.SystemConfig;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;

import org.apache.log4j.Logger;

public class SystemConfigService extends BaseService {
	
	private static Logger log = Logger.getLogger(SystemConfigService.class);

	/**
	 * 获取系统配置信息
	 * @return
	 */
	public SystemConfig getSystemConfig() {
		return (SystemConfig) this.getXXX("`id`=1", "system_config", SystemConfig.class.getName());
	}

	/**
	 * 更新系统配置信息
	 * @param systemConfig 系统配置信息
	 * @throws Exception 
	 */
	public void updateSystemConfig(SystemConfig systemConfig) throws Exception {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		try{
			String sql = "update system_config set saled_order_last_submit_time=?, lease_order_last_submit_time=?, " +
					" invoice_last_submit_time=?, member_last_submit_time=?, member_last_sync_time=?, " +
					" version_number=?, version_name=?, version_desc=? where id=1";
			ps = conn.prepareStatement(sql);
			ps.setTimestamp(1, systemConfig.getSaledOrderLastSubmitTime());
			ps.setTimestamp(2, systemConfig.getLeaseOrderLastSubmitTime());
			ps.setTimestamp(3, systemConfig.getInvoiceLastSubmitTime());
			ps.setTimestamp(4, systemConfig.getMemberLastSubmitTime());
			ps.setTimestamp(5, systemConfig.getMemberLastSyncTime());
			ps.setString(6, systemConfig.getVersionNumber());
			ps.setString(7, systemConfig.getVersionName());
			ps.setString(8, systemConfig.getVersionDesc());
			ps.executeUpdate();
		}catch(Exception e){
			log.error("更新系统配置信息时出现异常：", e);
		}finally{
			DbUtil.closeConnection(null, ps, conn);
		}
	}

}
