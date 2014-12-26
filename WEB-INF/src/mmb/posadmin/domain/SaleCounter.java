package mmb.posadmin.domain;

import java.sql.Timestamp;

public class SaleCounter {
	
	/**
	 * 商品序号
	 */
	private int productId;
	
	/**
	 *  商品编码
	 */
	private String barCode;
	
	/**
	 * 商品名称
	 */
	private String productName;
	
	/**
	 * 商品总销售数量
	 */
	private int count;
	
	/**
	 * 商品总销售额
	 */
	private double totalSales;
	
	/**
	 * 统计开始时间
	 */
	private Timestamp startTime;
	
	/**
	 * 统计结束时间
	 */
	private Timestamp endTime;
	
	/**
	 * 商品剩余库存
	 */
	private int stock;

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(double totalSales) {
		this.totalSales = totalSales;
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
}
