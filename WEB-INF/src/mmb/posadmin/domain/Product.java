package mmb.posadmin.domain;

import java.io.Serializable;

/**
 * 商品
 * @author likaige
 */
public class Product implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	public int id;
	
	/**
	 * 商品分类id
	 */
	public String goodsClassId;
	
	/**
	 * 商品分类
	 */
	private GoodsClass goodsClass;
	
	/**
	 * 供应商id
	 */
	public int supplierId;
	
	/**
	 * 条形码
	 */
	public String barCode;
	
	/**
	 * 商品名称
	 */
	public String name;
	
	/**
	 * 日租赁价格
	 */
	public double leasePrice;
	
	/**
	 * 包月租赁价格
	 */
	public double monthLeasePrice;
	
	/**
	 * 押金
	 */
	public double deposit;
	
	/**
	 * 标牌价
	 * <br/>销售价格
	 */
	public double salePrice;
	
	/**
	 * 限价
	 */
	public double limitPrice;
	
	/**
	 * 锁价
	 */
	public double lockPrice;
	
	/**
	 * 红卡额度
	 */
	public double redLines;
	
	/**
	 * 蓝卡额度
	 */
	public double blueLines;
	
	/**
	 * 库存量
	 */
	public int stock;
	
	/**
	 * 是否删除[0:未删除；1:已删除]
	 */
	public int isDelete;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGoodsClassId() {
		return goodsClassId;
	}

	public void setGoodsClassId(String goodsClassId) {
		this.goodsClassId = goodsClassId;
	}

	public GoodsClass getGoodsClass() {
		return goodsClass;
	}

	public void setGoodsClass(GoodsClass goodsClass) {
		this.goodsClass = goodsClass;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLeasePrice() {
		return leasePrice;
	}

	public void setLeasePrice(double leasePrice) {
		this.leasePrice = leasePrice;
	}

	public double getMonthLeasePrice() {
		return monthLeasePrice;
	}

	public void setMonthLeasePrice(double monthLeasePrice) {
		this.monthLeasePrice = monthLeasePrice;
	}

	public double getDeposit() {
		return deposit;
	}

	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}

	public double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}

	public double getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(double limitPrice) {
		this.limitPrice = limitPrice;
	}

	public double getLockPrice() {
		return lockPrice;
	}

	public void setLockPrice(double lockPrice) {
		this.lockPrice = lockPrice;
	}

	public double getRedLines() {
		return redLines;
	}

	public void setRedLines(double redLines) {
		this.redLines = redLines;
	}

	public double getBlueLines() {
		return blueLines;
	}

	public void setBlueLines(double blueLines) {
		this.blueLines = blueLines;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

}
