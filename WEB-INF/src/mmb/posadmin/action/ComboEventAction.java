package mmb.posadmin.action;

import mmb.posadmin.domain.Event;
import mmb.posadmin.service.ComboEventService;

import com.opensymphony.xwork2.ActionSupport;

public class ComboEventAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private ComboEventService ces = new ComboEventService();
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
		this.event = ces.getDetailEvent(event.getId());
		return "detail";
	}
	
}
