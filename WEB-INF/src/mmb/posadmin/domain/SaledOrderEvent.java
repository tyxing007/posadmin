package mmb.posadmin.domain;

/**
 * 销售订单活动表
 * @author qiuranke
 *
 */
public class SaledOrderEvent {
	/**
	 *主键id
	 */
	public int id ;
	
	/**
	 * 销售订单id
	 * 
	 */
	public int saledOrderId;
	
	/**
	 * 活动id
	 */
	public int eventId;
	
	/**
	 * 活动类型id 【展示用 数据库无此字段】
	 */
	public int eventType;
	
	/**
	 * 详细活动id
	 */
	public int detailEventId;
	
	/**
	 * 附件品赠品信息id
	 */
	public int extId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	

	public int getSaledOrderId() {
		return saledOrderId;
	}

	public void setSaledOrderId(int saledOrderId) {
		this.saledOrderId = saledOrderId;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public int getDetailEventId() {
		return detailEventId;
	}

	public void setDetailEventId(int detailEventId) {
		this.detailEventId = detailEventId;
	}

	public int getExtId() {
		return extId;
	}

	public void setExtId(int extId) {
		this.extId = extId;
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}
	
	
}
