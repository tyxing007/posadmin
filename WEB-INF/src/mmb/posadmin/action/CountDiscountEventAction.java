package mmb.posadmin.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mmb.posadmin.domain.CountDiscountEvent;
import mmb.posadmin.domain.Event;
import mmb.posadmin.service.CountDiscountEventService;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class CountDiscountEventAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private HttpServletRequest request = ServletActionContext.getRequest();
	private CountDiscountEventService cdes = new CountDiscountEventService();
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
		this.event = cdes.getDetailEvent(event.getId());
		
		//分开固定折扣与阶梯折扣
		List<CountDiscountEvent> fixedCountDiscountEventList = new ArrayList<CountDiscountEvent>();
		List<List<CountDiscountEvent>> ladderCountDiscountEventListList = new ArrayList<List<CountDiscountEvent>>();
		List<CountDiscountEvent> ladderCountDiscountEventList = new ArrayList<CountDiscountEvent>();
		int nowProductId = 0;
		for(CountDiscountEvent countDiscountEvent : event.getCountDiscountEventList()) {
			if(countDiscountEvent.getType() == 2){
				fixedCountDiscountEventList.add(countDiscountEvent);
			} else {
				if(nowProductId != countDiscountEvent.getProductId()) {
					if(nowProductId != 0){
						ladderCountDiscountEventListList.add(ladderCountDiscountEventList);
					}
					ladderCountDiscountEventList = new ArrayList<CountDiscountEvent>();
				}
				ladderCountDiscountEventList.add(countDiscountEvent);
				nowProductId = countDiscountEvent.getProductId();
			}
		}
		if(fixedCountDiscountEventList.size() != event.getCountDiscountEventList().size()) {
			ladderCountDiscountEventListList.add(ladderCountDiscountEventList);
		}
		request.setAttribute("fixedCountDiscountEventList", fixedCountDiscountEventList);
		request.setAttribute("ladderCountDiscountEventListList", ladderCountDiscountEventListList);
		return "detail";
	}
	
}
