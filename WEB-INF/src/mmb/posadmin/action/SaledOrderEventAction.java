package mmb.posadmin.action;

import mmb.posadmin.domain.Event;
import mmb.posadmin.domain.SaledOrder;
import mmb.posadmin.service.OrderService;
import mmb.posadmin.service.SaledOrderEventService;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionSupport;

public class SaledOrderEventAction extends ActionSupport{

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(SaledOrderEventAction.class);
	
	private SaledOrderEventService soes = new SaledOrderEventService();
	private SaledOrder saledOrder = new SaledOrder();
	private Event event = new Event();
	
	public SaledOrder getSaledOrder() {
		return saledOrder;
	}

	public void setSaledOrder(SaledOrder saledOrder) {
		this.saledOrder = saledOrder;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * 订单活动展示页面
	 * @return
	 */
	public String toOrderDetailEventView(){
		this.saledOrder = new OrderService().getSaledOrder(this.saledOrder.id);
		this.event = soes.getSaledOrderEvent(this.saledOrder.id);
		return SUCCESS;
	}
	
}
