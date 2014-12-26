package mmb.posadmin.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author likaige 2013-03-12
 * 租赁订单商品
 */
public class LeaseOrderProduct implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 租赁订单商品id
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
	 * 租赁订单id
	 */
	public int leaseOrderId;
	
	/**
	 * 购买数量
	 */
	public int count;
	
	/**
	 * 单价
	 */
	public double prePrice;
	
	/**
	 * 单押金
	 */
	public double perDeposit;
	
	/**
     * 租赁开始时间
     */
	public Timestamp startTime;
	
	/**
	 * 租赁结束时间
	 */
	public Timestamp endTime;
	
	/**
	 * 租赁时长
	 */
	public double timeLength;
	
	/**
	 * 租赁方式[0:按日租赁；1:包月租赁]
	 */
	public int leaseStyle;

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
	
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getLeaseOrderId() {
		return leaseOrderId;
	}

	public void setLeaseOrderId(int leaseOrderId) {
		this.leaseOrderId = leaseOrderId;
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

	public double getPerDeposit() {
		return perDeposit;
	}

	public void setPerDeposit(double perDeposit) {
		this.perDeposit = perDeposit;
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

	public double getTimeLength() {
		return timeLength;
	}

	public void setTimeLength(double timeLength) {
		this.timeLength = timeLength;
	}

	public int getLeaseStyle() {
		return leaseStyle;
	}

	public void setLeaseStyle(int leaseStyle) {
		this.leaseStyle = leaseStyle;
	}

}
