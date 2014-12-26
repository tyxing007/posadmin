package mmb.posadmin.domain;

import java.io.Serializable;

/**
 * 收货单条目
 */
public class ReceiveOrderItem implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 收货单id
	 */
	public int id;
	
	/**
	 * 收货单号
	 */
	public int orderId;
	
	/**
	 * 商品id
	 */
	public int productId;
	
	/**
	 * 对应商品
	 */
	public Product product;
	
	/**
	 * 发货数量
	 */
	public int sendCount;

	/**
	 * 实收数量
	 */
	public int receiveCount;

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

	public int getSendCount() {
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

	public int getReceiveCount() {
		return receiveCount;
	}

	public void setReceiveCount(int receiveCount) {
		this.receiveCount = receiveCount;
	}
	
}
