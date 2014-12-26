package mmb.posadmin.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 会员账户收支明细
 * <br>只记录会员卡的金额变动情况
 */
public class MemberAccountDetail implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	public int id;

	/**
	 * 会员id
	 */
	public String memberId;
	
	/**
	 * 类型[1:充值；2:提现；3:购买；4:租赁；5:退货]
	 */
	public int type;
	
	/**
	 * 订单id
	 */
	public int orderId;
	
	/**
	 * 收入
	 */
	public double income;
	
	/**
	 * 支出(负数)
	 */
	public double pay;
	
	/**
	 * 账户余额
	 */
	public double balance;

	/**
	 * 创建时间
	 */
	public Timestamp createTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public double getIncome() {
		return income;
	}

	public void setIncome(double income) {
		this.income = income;
	}

	public double getPay() {
		return pay;
	}

	public void setPay(double pay) {
		this.pay = pay;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
}
