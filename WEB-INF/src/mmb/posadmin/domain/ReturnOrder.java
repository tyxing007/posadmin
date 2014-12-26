package mmb.posadmin.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 退货单
 */
public class ReturnOrder implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 退货单id
	 */
	public int id;

	/**
	 * 退货单号
	 */
	public String orderNumber;
	
	/**
	 * 负责人
	 */
	public String charger;
	
    /**
     * 创建时间
     */
	public Timestamp createTime;
	
	/**
	 * 使用状态[0:未提交；1:已提交]
	 */
	public int useStatus = -1;
	
	/**
	 * 退货单条目
	 */
	private List<ReturnOrderItem> itemList = new ArrayList<ReturnOrderItem>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getCharger() {
		return charger;
	}

	public void setCharger(String charger) {
		this.charger = charger;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public int getUseStatus() {
		return useStatus;
	}

	public void setUseStatus(int useStatus) {
		this.useStatus = useStatus;
	}

	public List<ReturnOrderItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<ReturnOrderItem> itemList) {
		this.itemList = itemList;
	}

}
