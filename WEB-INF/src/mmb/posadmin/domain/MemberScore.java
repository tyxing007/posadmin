package mmb.posadmin.domain;

import java.sql.Timestamp;

/**
 * 会员积分表
 * @author qiuranke
 *
 */
public class MemberScore {
	
	/**
	 * 主键id
	 */
	public int id;

	/**
	 * 会员id
	 */
	public  String memberId;
	
	/**
	 * 订单id
	 */
	public int orderId;
	
	/**
	 * 订单编号【数据库无此字段】
	 */
	public String serialNumber;
	
	/**
	 * 当前积分【结合过本单积分】
	 */
	public int currentScore;
	
	/**
	 * 本单增加积分
	 */
	public int addScore;
	
	/**
	 * 本单减少积分
	 */
	public int minusScore;
	
	/**
	 * 积分时间
	 */
	public Timestamp createTime;
	
	/**
	 * 积分类型【1-购物，2-退货】
	 */
	public int type;

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getCurrentScore() {
		return currentScore;
	}

	public void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
	}

	public int getAddScore() {
		return addScore;
	}

	public void setAddScore(int addScore) {
		this.addScore = addScore;
	}

	public int getMinusScore() {
		return minusScore;
	}

	public void setMinusScore(int minusScore) {
		this.minusScore = minusScore;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

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
	
	
}
