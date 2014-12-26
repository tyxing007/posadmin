package mmb.posadmin.action;

import mmb.posadmin.domain.Event;
import mmb.posadmin.service.BuyGiftEventService;

import com.opensymphony.xwork2.ActionSupport;

public class BuyGiftEventAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private BuyGiftEventService bfes = new BuyGiftEventService();
	private Event event = new Event();
	
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * 跳转到买赠活动详情页面
	 * @return
	 */
	public String toEventDetailView() {
		//获取活动信息
		this.event = bfes.getDetailEvent(event.getId());
		return "detail";
	}
}
