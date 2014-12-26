package mmb.posadmin.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mmb.posadmin.domain.Counter;
import mmb.posadmin.domain.SaleCounter;
import mmb.posadmin.service.OrderCountService;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class OrderCountAction extends ActionSupport {
	
	private static final long serialVersionUID = 4228473213427446073L;

	private OrderCountService ocs = new OrderCountService();
	
	/**
	 * 统计信息
	 */
	private Counter counter;
	
	private List<SaleCounter> sclist ;
	
	private Page<SaleCounter> page;
	
	public Counter getCounter() {
		return counter;
	}

	public void setCounter(Counter counter) {
		this.counter = counter;
	}

	public OrderCountService getOcs() {
		return ocs;
	}

	public void setOcs(OrderCountService ocs) {
		this.ocs = ocs;
	}

	public List<SaleCounter> getSclist() {
		return sclist;
	}

	public void setSclist(List<SaleCounter> sclist) {
		this.sclist = sclist;
	}

	public Page<SaleCounter> getPage() {
		return page;
	}

	public void setPage(Page<SaleCounter> page) {
		this.page = page;
	}
	
	/**
	 * 统计商品销售列表信息
	 * @return
	 */
	public String getOrderCountList(){
		if(this.page == null){
			this.page = new Page<SaleCounter>();
		}
		setPage(ocs.doCountSaleOrder(page, "2013-03-15 17:00:59", "2013-03-21 17:00:59"));
		setSclist(this.page.list);
		
		return SUCCESS;
	}
	
	/**
	 * 获取统计信息
	 * @return
	 */
	public String getCount(){
		//获取日期
		HttpServletRequest request = ServletActionContext.getRequest();
		String date = request.getParameter("date");
		if(StringUtils.isBlank(date)) {
			date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		}
		
		//获取统计信息
		setCounter(ocs.getCounter(date));
		request.setAttribute("date", date);
		
		return "counter";
		
	}
	

}
