package mmb.posadmin.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import mmb.posadmin.domain.ReceiveOrder;
import mmb.posadmin.domain.ReceiveOrderItem;
import mmb.posadmin.service.ReceiveOrderItemService;
import mmb.posadmin.service.ReceiveOrderService;

import com.opensymphony.xwork2.ActionSupport;

public class ReceiveOrderItemAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private ReceiveOrderItemService rois = new ReceiveOrderItemService();
	private Page<ReceiveOrderItem> page = new Page<ReceiveOrderItem>();
	private ReceiveOrder receiveOrder = new ReceiveOrder();
	private ReceiveOrderItem receiveOrderItem = new ReceiveOrderItem();
	private int receiveOrderId;
	
	public Page<ReceiveOrderItem> getPage() {
		return page;
	}

	public void setPage(Page<ReceiveOrderItem> page) {
		this.page = page;
	}

	public ReceiveOrder getReceiveOrder() {
		return receiveOrder;
	}

	public void setReceiveOrder(ReceiveOrder receiveOrder) {
		this.receiveOrder = receiveOrder;
	}

	public ReceiveOrderItem getReceiveOrderItem() {
		return receiveOrderItem;
	}

	public void setReceiveOrderItem(ReceiveOrderItem receiveOrderItem) {
		this.receiveOrderItem = receiveOrderItem;
	}

	public int getReceiveOrderId() {
		return receiveOrderId;
	}

	public void setReceiveOrderId(int receiveOrderId) {
		this.receiveOrderId = receiveOrderId;
	}

	/**
	 * 跳转至收货单条目列表界面
	 * @return
	 */
	public String receiveOrderItemList(){
		HttpServletRequest request = ServletActionContext.getRequest();
		//获取收货单信息
		ReceiveOrderService sos = new ReceiveOrderService();
		this.receiveOrder = sos.getReceiveOrderById(this.receiveOrderId);
		
		//分页获取收货单条目列表信息
		//Map<String, Object> param = new HashMap<String, Object>();
		//param.put("receiveOrderId", receiveOrderId);
		//rois.getReceiveOrderItemPage(page, param);
		
		//获取收货单下的所有收货单条目数据
		List<ReceiveOrderItem> allItemList = rois.getAllItemListByOrderId(receiveOrderId);
		request.setAttribute("allItemList", allItemList);
		return SUCCESS;
	}

}
