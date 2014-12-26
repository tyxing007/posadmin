package mmb.posadmin.domain;

import java.sql.Timestamp;

/**
 * 会员租赁信息
 */
public class MemberLease {
	
	/**
	 * 序号 
	 */
	public int id ;
	
	/**
	 * 租赁订单流水号
	 */
	public String serialNumber;
	
	/**
	 * 会员编号
	 */
	public String memberId;
	
	/**
	 * 商品id
	 */
	public int productId;
	
	/**
	 * 租赁开始时间
	 */
	public Timestamp startTime;
	
	/**
	 * 租赁结束时间
	 */
	public Timestamp endTime;
	
	/**
	 * 租赁类型[0-日租，1-月租]
	 */
	public int type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
