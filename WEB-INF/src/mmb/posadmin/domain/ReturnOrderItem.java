package mmb.posadmin.domain;

import java.io.Serializable;

/**
 * 退货单条目
 */
public class ReturnOrderItem implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 退货单条目id
	 */
	public int id;
	
	/**
	 * 所属退货单id
	 */
	public int returnOrderId;
	
	/**
	 * 商品id
	 */
	public int productId;
	
	/**
	 * 对应商品
	 */
	public Product product;
	
	/**
	 * 商品数量
	 */
	public int count;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getReturnOrderId() {
		return returnOrderId;
	}

	public void setReturnOrderId(int returnOrderId) {
		this.returnOrderId = returnOrderId;
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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
