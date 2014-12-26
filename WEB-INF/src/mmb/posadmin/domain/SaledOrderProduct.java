package mmb.posadmin.domain;

import java.io.Serializable;

/**
 * 销售订单商品
 * @author likaige 2013-03-12
 */
public class SaledOrderProduct implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 销售订单商品id
	 */
	public int id;
	
	/**
	 * 商品id
	 */
	public int productId;
	
	/**
	 * 对应商品
	 */
	private Product product;
	
	/**
	 * 销售订单id
	 */
	public int saledOrderId;
	
	/**
	 * 购买数量
	 */
	public int count;
	
	/**
	 * 单价
	 */
	public double prePrice;
	
	/**
	 * 活动备注
	 */
	public String eventRemark;
	
	/****************数据传输字段*****************/
	/**
	 * 商品名称[用于数据传输]
	 */
	private String productName;
	
	/**
	 * 商品条形码[用于数据传输]
	 */
	private String barCode;
	
	/**
	 * 使用红卡id
	 */
	private String redId;
	
	/**
	 * 使用红卡点数
	 */
	private double redPoint;
	
	/**
	 * 使用蓝卡id
	 */
	private String blueId;
	
	/**
	 * 使用蓝卡点数
	 */
	private double bluePoint;
	
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
	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Product getProduct() {
		return product;
	}
	
	public void setProduct(Product product) {
		this.product = product;
	}

	public int getSaledOrderId() {
		return saledOrderId;
	}

	public void setSaledOrderId(int saledOrderId) {
		this.saledOrderId = saledOrderId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getPrePrice() {
		return prePrice;
	}

	public void setPrePrice(double prePrice) {
		this.prePrice = prePrice;
	}

	public String getEventRemark() {
		return eventRemark;
	}

	public void setEventRemark(String eventRemark) {
		this.eventRemark = eventRemark;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public double getRedPoint() {
		return redPoint;
	}

	public void setRedPoint(double redPoint) {
		this.redPoint = redPoint;
	}

	public double getBluePoint() {
		return bluePoint;
	}

	public void setBluePoint(double bluePoint) {
		this.bluePoint = bluePoint;
	}

	public String getRedId() {
		return redId;
	}

	public void setRedId(String redId) {
		this.redId = redId;
	}

	public String getBlueId() {
		return blueId;
	}

	public void setBlueId(String blueId) {
		this.blueId = blueId;
	}
	
}
