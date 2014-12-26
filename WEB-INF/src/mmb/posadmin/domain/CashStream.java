package mmb.posadmin.domain;

import java.sql.Timestamp;

/**
 * @author Administrator
 * 现金流
 */
public class CashStream {
	
	public int id;
	
	/**
	 * 对应订单的流水号
	 */
	public String serialNumber;
	
	/**
	 * pos机编码
	 */
	public String posCode;
	
	/**
	 * 收银员id
	 */
	public int cashierId;
	
	/**
	 * 现金[正数：收入金额；负数：支出金额]
	 */
	public double cash;
	
	/**
	 * 收银时间
	 */
	public Timestamp getTime;

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

	public String getPosCode() {
		return posCode;
	}

	public void setPosCode(String posCode) {
		this.posCode = posCode;
	}

	public int getCashierId() {
		return cashierId;
	}

	public void setCashierId(int cashierId) {
		this.cashierId = cashierId;
	}

	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	public Timestamp getGetTime() {
		return getTime;
	}

	public void setGetTime(Timestamp getTime) {
		this.getTime = getTime;
	}

}
