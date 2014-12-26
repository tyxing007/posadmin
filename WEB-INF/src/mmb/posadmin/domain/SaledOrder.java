package mmb.posadmin.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author likaige 2013-03-12
 * 销售订单
 */
public class SaledOrder implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 销售订单id
	 */
	public int id;
	
	/**
	 * 订单流水号
	 */
	public String serialNumber;
	
	/**
	 * pos机编号
	 */
	public String posCode;
	
	/**
	 * 收银员id
	 */
	public int cashierId;
	
	/**
	 * 会员id
	 */
	public String memberId;
	
	/**
	 * 所属会员
	 */
	private Member member;
	
	/**
	 * 订单总金额
	 */
	public double price;
	
    /**
     * 订单积分
     */
	public int score;
	
	/**
	 * 销售时间
	 */
	public Timestamp saledTime;
	
	/**
	 * 订单类型[0:购买订单；1:退货订单]
	 */
	public int orderType;
	
	/**
	 * 订单商品列表数据
	 */
	private List<SaledOrderProduct> productList = new ArrayList<SaledOrderProduct>();
	
	/**
	 * 订单金钱来源列表数据
	 */
	private List<MoneySource> moneySourceList = new ArrayList<MoneySource>();
	
	/**
	 * 退货订单金钱去向列表
	 */
	private List<MoneyDestination> moneyDestinationList = new ArrayList<MoneyDestination>();
	
	/**
	 * 订单活动表
	 */
	private List<SaledOrderEvent> saledOrderEventList = new ArrayList<SaledOrderEvent>();

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

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
	public Member getMember() {
		return member;
}

	public void setMember(Member member) {
		this.member = member;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Timestamp getSaledTime() {
		return saledTime;
	}

	public void setSaledTime(Timestamp saledTime) {
		this.saledTime = saledTime;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public List<SaledOrderProduct> getProductList() {
		return productList;
	}

	public void setProductList(List<SaledOrderProduct> productList) {
		this.productList = productList;
	}

	public List<MoneySource> getMoneySourceList() {
		return moneySourceList;
	}

	public void setMoneySourceList(List<MoneySource> moneySourceList) {
		this.moneySourceList = moneySourceList;
	}

	public List<MoneyDestination> getMoneyDestinationList() {
		return moneyDestinationList;
	}

	public void setMoneyDestinationList(List<MoneyDestination> moneyDestinationList) {
		this.moneyDestinationList = moneyDestinationList;
	}

	public List<SaledOrderEvent> getSaledOrderEventList() {
		return saledOrderEventList;
	}

	public void setSaledOrderEventList(List<SaledOrderEvent> saledOrderEventList) {
		this.saledOrderEventList = saledOrderEventList;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	

	
	
}
