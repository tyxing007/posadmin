package mmb.posadmin.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.posadmin.domain.ReturnOrder;
import mmb.posadmin.domain.ReturnOrderItem;
import mmb.posadmin.service.ReturnOrderItemService;
import mmb.posadmin.service.ReturnOrderService;
import mmb.posadmin.util.AuthHelper;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class ReturnOrderItemAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ReturnOrderItemAction.class);
	
	private ReturnOrderItemService sois = new ReturnOrderItemService();
	private Page<ReturnOrderItem> page = new Page<ReturnOrderItem>();
	private ReturnOrder returnOrder = new ReturnOrder();
	private ReturnOrderItem returnOrderItem = new ReturnOrderItem();
	private int returnOrderId;
	
	public Page<ReturnOrderItem> getPage() {
		return page;
	}

	public void setPage(Page<ReturnOrderItem> page) {
		this.page = page;
	}

	public ReturnOrder getReturnOrder() {
		return returnOrder;
	}

	public void setReturnOrder(ReturnOrder returnOrder) {
		this.returnOrder = returnOrder;
	}

	public ReturnOrderItem getReturnOrderItem() {
		return returnOrderItem;
	}

	public void setReturnOrderItem(ReturnOrderItem returnOrderItem) {
		this.returnOrderItem = returnOrderItem;
	}

	public int getReturnOrderId() {
		return returnOrderId;
	}

	public void setReturnOrderId(int returnOrderId) {
		this.returnOrderId = returnOrderId;
	}

	/**
	 * 跳转至退货单条目列表界面
	 * @return
	 */
	public String returnOrderItemList(){
		//获取当前用户信息
		HttpServletRequest request = ServletActionContext.getRequest();
		request.setAttribute("currentUser", AuthHelper.getCurrentUser());
		
		//获取退货单信息
		ReturnOrderService sos = new ReturnOrderService();
		this.returnOrder = sos.getReturnOrderById(this.returnOrderId);
		
		//获取查询参数
		String productName = request.getParameter("productName");
		request.setAttribute("productName", productName);
		
		//分页获取退货单条目列表信息
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("productName", productName);
		param.put("returnOrderId", returnOrderId);
		sois.getReturnOrderItemPage(page, param);
		return SUCCESS;
	}

	/**
	 * 跳转到退货单条目表单页面
	 * @return
	 */
	public String toReturnOrderItemFormView() {
		//获取退货单信息
		ReturnOrderService sos = new ReturnOrderService();
		this.returnOrder = sos.getReturnOrderById(returnOrderId);
		
		//修改
		if(returnOrderItem.getId() != 0) {
			this.returnOrderItem = sois.getDetailById(this.returnOrderItem.getId());
		}
		return INPUT;
	}
	
	/**
	 * 保存退货单条目信息
	 * @return
	 */
	public String saveReturnOrderItem() {
		try {
			//新建
			if(returnOrderItem.getId() == 0) {
				sois.addXXX(returnOrderItem, "return_order_item");
			}
			//修改
			else {
				sois.updateReturnOrderItem(returnOrderItem);
			}
		} catch (Exception e) {
			ServletActionContext.getRequest().setAttribute("message", e.getMessage());
			log.error("保存退货单条目信息时出现异常：", e);
			return ERROR;
		}
		return this.returnOrderItemList();
	}
	
	/**
	 * 删除退货单条目信息
	 * @return
	 */
	public String deleteReturnOrderItem() {
		//删除
		sois.deleteItemById(this.returnOrderItem.getId());
		return this.returnOrderItemList();
	}
	
}
