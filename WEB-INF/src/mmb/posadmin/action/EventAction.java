package mmb.posadmin.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.posadmin.domain.Event;
import mmb.posadmin.service.EventService;
import mmb.posadmin.util.AuthHelper;
import mmb.posadmin.util.ResponseUtils;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class EventAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private HttpServletRequest request = ServletActionContext.getRequest();
	private EventService es = new EventService();
	private Event event = new Event();
	private Page<Event> page = new Page<Event>();
	
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Page<Event> getPage() {
		return page;
	}

	public void setPage(Page<Event> page) {
		this.page = page;
	}

	/**
	 * 跳转至活动列表界面
	 * @return
	 */
	public String eventList() {
		//获取当前用户信息
		request.setAttribute("currentUser", AuthHelper.getCurrentUser());
		
		//获取查询参数
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("event", event);
		
		//分页获取活动列表信息
		es.getEventPage(page, param);
		
		return SUCCESS;
	}
	
	/**
	 * 从中心库同步活动信息
	 */
	public void syncEvent() {
		String result = "true";
		try {
			//加一个application范围的锁
			synchronized (request.getServletContext()) {
				es.syncEvent();
			}
		} catch (Exception e) {
			result = "从中心库同步活动信息时出现异常："+e;
		}
		ResponseUtils.renderText(ServletActionContext.getResponse(), result);
	}

}
