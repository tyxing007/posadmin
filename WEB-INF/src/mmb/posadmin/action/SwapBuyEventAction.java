package mmb.posadmin.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mmb.posadmin.domain.Event;
import mmb.posadmin.domain.SwapBuyEvent;
import mmb.posadmin.service.SwapBuyEventService;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class SwapBuyEventAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private SwapBuyEventService sbes = new SwapBuyEventService();
	HttpServletRequest request = ServletActionContext.getRequest();
	private Event event = new Event();
	private List<SwapBuyEvent> swapBuyEventList = new ArrayList<SwapBuyEvent>();
	
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public List<SwapBuyEvent> getSwapBuyEventList() {
		return swapBuyEventList;
	}

	public void setSwapBuyEventList(List<SwapBuyEvent> swapBuyEventList) {
		this.swapBuyEventList = swapBuyEventList;
	}

	/**
	 * 跳转到详情页面
	 * @return
	 */
	public String toEventDetailView() {
		//获取活动信息
		this.event = sbes.getDetailEvent(event.getId());
		return "detail";
	}
	
}
