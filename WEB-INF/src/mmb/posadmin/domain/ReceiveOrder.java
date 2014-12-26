package mmb.posadmin.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 收货单
 */
public class ReceiveOrder implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 收货单id
	 */
	public int id;
	
	/**
	 * 对应发货单id
	 */
	public int sendOrderId;
	
	/**
	 * 收货单号
	 */
	public String orderNumber;
	
	/**
	 * 收货负责人
	 */
	public String charger;
	
    /**
     * 收货时间
     */
	public Timestamp createTime;
	
	/**
	 * 使用状态[0:未确认；1:已确认]
	 */
	public int useStatus = -1;
	
	private List<ReceiveOrderItem> itemList = new ArrayList<ReceiveOrderItem>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSendOrderId() {
		return sendOrderId;
	}

	public void setSendOrderId(int sendOrderId) {
		this.sendOrderId = sendOrderId;
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

	public List<ReceiveOrderItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<ReceiveOrderItem> itemList) {
		this.itemList = itemList;
	}
	
}
