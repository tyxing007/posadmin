package mmb.posadmin.domain;

import java.io.Serializable;

/**
 * 订单的金钱来源
 * <br>暂时只针对销售订单
 */
public class MoneySource implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 金钱来源id
	 */
	public int id;
	
	/**
	 * 销售订单id
	 */
	public int orderId;
	
	/**
	 * 来源类型[1:现金；2:会员卡；3:银行卡；4:购物券]
	 */
	public int type;
	
	/**
	 * 金额
	 */
	public double money;
	
	/**
	 * 可提取money
	 */
	public double withdrawMoney;
	
	/**
	 * 刷卡单号
	 */
	public String swipCardNumber;

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

	public String getSwipCardNumber() {
		return swipCardNumber;
	}

	public void setSwipCardNumber(String swipCardNumber) {
		this.swipCardNumber = swipCardNumber;
	}

	public double getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(double withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}
	
	
	
}
