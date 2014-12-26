package mmb.posadmin.domain;

import java.io.Serializable;

/**
 * 金钱去向表
 * <br>退货订单的返款明细
 */
public class MoneyDestination implements Serializable{

	private static final long serialVersionUID = -9224774374355332L;
	
	/**
	 * 金钱去向id
	 */
	public int id;
	
	/**
	 * 退货订单id
	 */
	public int orderId;
	
	/**
	 * 去向类型[1:现金；2:会员卡；3:银行卡]
	 */
	public int type;
	
	/**
	 * 金额
	 */
	public double money;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

}
