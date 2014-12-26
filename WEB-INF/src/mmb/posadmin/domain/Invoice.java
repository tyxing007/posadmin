package mmb.posadmin.domain;

import java.sql.Timestamp;

/**
 * 进销存实体对象
 * @author hanquan
 *
 */
public class Invoice {
    
	/**
	 * 记录序号
	 */
	public int id;
	
	/**
	 * 商品id
	 */
	public int productId;
	
	/**
	 * 商品编码
	 */
	public String productCode;
	
	/**
	 * 商品名称
	 */
	public String productName;
	
	/**
	 * 操作类型[0:收货；1:销售；2:退货；3:租赁；4:还租；5:盘点；6:退货到中心库]
	 */
	public String operType;
	
	/**
	 * 进销前数据量
	 */
	public int beforeCount;
	
	/**
	 * 这次进销的数据量
	 */
	public int count;
	
	/**
	 * 进销后数据量
	 */
	public int afterCount;
	
	/**
	 * 操作用户id
	 */
	public String operUser;
	
	/**
	 * 操作用户名称
	 */
	public String operName;
	
	/**
	 * 操作时间
	 */
	public Timestamp createTime;
	
	/**
	 * 备注
	 */
	public String remark;
	
	/**
	 * 流水id
	 */
	public int serialId;
	
	/**
	 * 流水编码
	 */
	public String serialNumber;

	public int getSerialId() {
		return serialId;
	}

	public void setSerialId(int serialId) {
		this.serialId = serialId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public int getBeforeCount() {
		return beforeCount;
	}

	public void setBeforeCount(int beforeCount) {
		this.beforeCount = beforeCount;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getAfterCount() {
		return afterCount;
	}

	public void setAfterCount(int afterCount) {
		this.afterCount = afterCount;
	}

	public String getOperUser() {
		return operUser;
	}

	public void setOperUser(String operUser) {
		this.operUser = operUser;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getOperName() {
		return operName;
	}

	public void setOperName(String operName) {
		this.operName = operName;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	
	
	
}
