package mmb.posadmin.action;

import mmb.posadmin.domain.Event;
import mmb.posadmin.service.MoneyDiscountEventService;

import com.opensymphony.xwork2.ActionSupport;

public class MoneyDiscountEventAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private MoneyDiscountEventService mdes = new MoneyDiscountEventService();
	private Event event = new Event();
	
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	
	/**
	 * 跳转到详情页面
	 * @return
	 */
	public String toEventDetailView() {
		//获取活动信息
		this.event = mdes.getDetailEvent(event.getId());
		return "detail";
	}
	
}
