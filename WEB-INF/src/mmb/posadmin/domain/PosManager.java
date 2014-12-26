package mmb.posadmin.domain;

import java.sql.Timestamp;

public class PosManager {
	
	/**
	 * 序号
	 */
	public int id;
	
	/**
	 * pos机编号
	 */
	public String posCode;
	
	/**
	 * pos机ip
	 */
	public String posIp;
	
	/**
	 * 新增日期
	 */
	public Timestamp createTime;
	
	/**
	 * 操作用户id
	 */
	public String operUser;
	
	/**
	 * 删除标志，0-未删除，1-已删除
	 */
	public int isDelete;
	
	/**
	 * 操作用户名称【数据库无此字段】
	 */
	public String operName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPosCode() {
		return posCode;
	}

	public void setPosCode(String posCode) {
		this.posCode = posCode;
	}

	public String getPosIp() {
		return posIp;
	}

	public void setPosIp(String posIp) {
		this.posIp = posIp;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getOperUser() {
		return operUser;
	}

	public void setOperUser(String operUser) {
		this.operUser = operUser;
	}

	public String getOperName() {
		return operName;
	}

	public void setOperName(String operName) {
		this.operName = operName;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}
	
	

}
