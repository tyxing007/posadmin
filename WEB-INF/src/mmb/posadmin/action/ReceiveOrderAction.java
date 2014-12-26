package mmb.posadmin.action;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.posadmin.domain.ReceiveOrder;
import mmb.posadmin.domain.ReceiveOrderItem;
import mmb.posadmin.service.GoodsClassService;
import mmb.posadmin.service.ProductService;
import mmb.posadmin.service.ReceiveOrderService;
import mmb.posadmin.util.AuthHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class ReceiveOrderAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ReceiveOrderAction.class);
	
	private ReceiveOrderService ros = new ReceiveOrderService();
	private Page<ReceiveOrder> page = new Page<ReceiveOrder>();
	private ReceiveOrder receiveOrder = new ReceiveOrder();
	private List<ReceiveOrderItem> itemList;

	public Page<ReceiveOrder> getPage() {
		return page;
	}

	public void setPage(Page<ReceiveOrder> page) {
		this.page = page;
	}

	public ReceiveOrder getReceiveOrder() {
		return receiveOrder;
	}

	public void setReceiveOrder(ReceiveOrder receiveOrder) {
		this.receiveOrder = receiveOrder;
	}
	
	public List<ReceiveOrderItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<ReceiveOrderItem> itemList) {
		this.itemList = itemList;
	}

	/**
	 * 跳转至收货单列表界面
	 * @return
	 */
	public String receiveOrderList(){
		//获取当前用户信息
		HttpServletRequest request = ServletActionContext.getRequest();
		request.setAttribute("currentUser", AuthHelper.getCurrentUser());
		
		try {
			//获取查询参数
			Map<String, Object> param = new HashMap<String, Object>();
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(StringUtils.isNotBlank(startTime)) {
				param.put("startTime", new Timestamp(sdf.parse(startTime).getTime()));
				request.setAttribute("startTime", startTime);
			}
			if(StringUtils.isNotBlank(endTime)) {
				param.put("endTime", new Timestamp(sdf.parse(endTime).getTime()+(24*3600000)));
				request.setAttribute("endTime", endTime);
			}
			param.put("receiveOrder", receiveOrder);
			
			//分页获取收货单列表信息
			ros.getReceiveOrderPage(page, param);
		} catch (ParseException e) {
			log.error("跳转至收货单列表界面时出现异常：", e);
		}
		
		return SUCCESS;
	}

	/**
	 * 确认收货
	 * @return
	 */
	public String confirmReceive() {
		try {
			ros.confirmReceive(this.receiveOrder, this.itemList);
		} catch (Exception e) {
			ServletActionContext.getRequest().setAttribute("message", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		this.receiveOrder = new ReceiveOrder();
		return this.receiveOrderList();
	}
	
	/**
	 * 从中心库同步发货单信息
	 * @return
	 */
	public String syncSendOrderFromPoscenter() {
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			//加一个application范围的锁，避免重复更新
			synchronized (request.getServletContext()) {
				//同步商品分类
				GoodsClassService gcs = new GoodsClassService();
				gcs.syncGoodsClassDataFromPoscenter();
				
				//同步商品
				ProductService ps = new ProductService();
				ps.syncProductDataFromPoscenter();
				
				//同步发货单信息
				ros.syncSendOrderFromPoscenter();
			}
		} catch (Exception e) {
			request.setAttribute("message", "从中心库同步发货单信息时出现异常："+e.getMessage());
			log.error("从中心库同步发货单信息时出现异常：", e);
			return ERROR;
		}
		return this.receiveOrderList();
	}
	
	/**
	 * 导出Excel收货单
	 * @return
	 */
	public void exportExcel() {
		try {
			//获取收货单信息
			ReceiveOrder order = ros.getReceiveOrderById(this.receiveOrder.getId());
			
			//获取Excel数据
			byte[] data = ros.exportExcel(order);
			
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename="+ order.getOrderNumber() + ".xls");
			OutputStream out = new BufferedOutputStream(response.getOutputStream());
			out.write(data);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("导出Excel收货单时出现异常：", e);
		}
	}
	
}
