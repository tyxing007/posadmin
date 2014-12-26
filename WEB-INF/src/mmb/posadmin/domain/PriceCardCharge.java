package mmb.posadmin.domain;

import java.sql.Timestamp;

/**
 * 调价卡收支明细
 */
public class PriceCardCharge {
	
	public int id;
	
	public String priceCardId;
	
	/**
	 * 销售订单id
	 */
	public int orderId;
	
	//卡类型1--红卡  2--蓝卡
	public int cardType;
	
	public Timestamp chargeTime;
	
	//消费类型 1--充值  2--消费
	public int consumeType;
	
	public double consumeCash;
	
	public double totalCash;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPriceCardId() {
		return priceCardId;
	}

	public void setPriceCardId(String priceCardId) {
		this.priceCardId = priceCardId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getCardType() {
		return cardType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}

	public Timestamp getChargeTime() {
		return chargeTime;
	}

	public void setChargeTime(Timestamp chargeTime) {
		this.chargeTime = chargeTime;
	}

	public int getConsumeType() {
		return consumeType;
	}

	public void setConsumeType(int consumeType) {
		this.consumeType = consumeType;
	}

	public double getConsumeCash() {
		return consumeCash;
	}

	public void setConsumeCash(double consumeCash) {
		this.consumeCash = consumeCash;
	}

	public double getTotalCash() {
		return totalCash;
	}

	public void setTotalCash(double totalCash) {
		this.totalCash = totalCash;
	}

}
