package mmb.posadmin.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 调价单
 * @author likaige
 */
public class AdjustPriceBill implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	public int id;
	
	/**
	 * 商品id
	 */
	public int productId;
	
	/**
	 * 商品
	 */
	private Product product;
	
	/**
	 * 调价单号
	 */
	public String billNumber;
	
	/**
	 * 目标价
	 */
	public double targetPrice;
	
	/**
	 * 审核状态[1:待审核；2:审核通过；3:审核未通过]
	 */
	public int auditStatus;
	
	/**
	 * 使用状态[1:未提交；2:已提交]
	 */
	public int useStatus;
	
	/**
     * 创建时间
     */
	public Timestamp createTime;
	
	/**
	 * 店面编号[用于数据传输]
	 */
	private String shopCode;

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
	
	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public double getTargetPrice() {
		return targetPrice;
	}

	public void setTargetPrice(double targetPrice) {
		this.targetPrice = targetPrice;
	}

	public int getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(int auditStatus) {
		this.auditStatus = auditStatus;
	}

	public int getUseStatus() {
		return useStatus;
	}

	public void setUseStatus(int useStatus) {
		this.useStatus = useStatus;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getShopCode() {
		return shopCode;
	}

	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}
	
}
