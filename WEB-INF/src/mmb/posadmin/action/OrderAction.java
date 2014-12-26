package mmb.posadmin.action;

import java.util.HashMap;
import java.util.Map;

import mmb.posadmin.domain.LeaseOrder;
import mmb.posadmin.domain.SaledOrder;
import mmb.posadmin.service.OrderService;
import mmb.posadmin.util.ResponseUtils;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class OrderAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(OrderAction.class);
	OrderService os = new OrderService();
	private Page<LeaseOrder> leaseOrderPage = new Page<LeaseOrder>(); //租赁订单
	private Page<SaledOrder> saledOrderPage = new Page<SaledOrder>(); //销售订单
	private int orderId; //订单id
	private int orderType; //订单类型
	private String memberName; //会员名称
	private String serialNumber; //订单流水号
	private int payMethod; //支付方式
	private String swipCardNumber; //刷卡流水号
	
	public Page<LeaseOrder> getLeaseOrderPage() {
		return leaseOrderPage;
	}

	public void setLeaseOrderPage(Page<LeaseOrder> leaseOrderPage) {
		this.leaseOrderPage = leaseOrderPage;
	}

	public Page<SaledOrder> getSaledOrderPage() {
		return saledOrderPage;
	}

	public void setSaledOrderPage(Page<SaledOrder> saledOrderPage) {
		this.saledOrderPage = saledOrderPage;
	}
	
	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}
	
	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	public int getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(int payMethod) {
		this.payMethod = payMethod;
	}

	public String getSwipCardNumber() {
		return swipCardNumber;
	}

	public void setSwipCardNumber(String swipCardNumber) {
		this.swipCardNumber = swipCardNumber;
	}

	/**
	 * 跳转至租赁订单列表界面
	 * @return
	 */
	public String leaseOrderList(){
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("orderId", orderId);
		param.put("orderType", orderType); //订单类型
		param.put("memberName", memberName);
		this.leaseOrderPage = os.getLeaseOrderPage(this.leaseOrderPage, param);
		return "leaseOrderList";
	}
	
	/**
	 * 跳转至销售订单列表界面
	 * @return
	 */
	public String saledOrderList(){
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("orderId", orderId);
		param.put("orderType", orderType); //订单类型
		param.put("memberName", memberName);
		param.put("serialNumber", serialNumber);
		param.put("payMethod", payMethod);
		param.put("swipCardNumber", swipCardNumber);
		this.saledOrderPage = os.getSaledOrderPage(this.saledOrderPage, param);
		return "saledOrderList";
	}
	
	/**
	 * 向中心库提交租赁订单信息
	 */
	public void syncLeaseOrderInfoToCenter() {
		String result = "true";
		try {
			//加一个application范围的锁，避免重复提交
			synchronized (ServletActionContext.getRequest().getServletContext()) {
				os.syncLeaseOrderInfoToCenter();
			}
		} catch (Exception e) {
			result = "向中心库提交租赁订单信息时出现异常："+e;
		}
		ResponseUtils.renderText(ServletActionContext.getResponse(), result);
	}
	
	/**
	 * 向中心库提交销售订单信息
	 */
	public void syncSaledOrderInfoToCenter() {
		String result = "true";
		try {
			//加一个application范围的锁，避免重复提交
			synchronized (ServletActionContext.getRequest().getServletContext()) {
				os.syncSaledOrderInfoToCenter();
			}
		} catch (Exception e) {
			result = "向中心库提交销售订单信息时出现异常："+e;
		}
		ResponseUtils.renderText(ServletActionContext.getResponse(), result);
	}

}
