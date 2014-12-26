package mmb.posadmin.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.posadmin.domain.LeaseOrder;
import mmb.posadmin.domain.LeaseOrderProduct;
import mmb.posadmin.domain.Member;
import mmb.posadmin.domain.MemberLease;
import mmb.posadmin.domain.SaledOrder;
import mmb.posadmin.service.AdjustPriceBillService;
import mmb.posadmin.service.BalanceService;
import mmb.posadmin.service.CashierService;
import mmb.posadmin.service.EventService;
import mmb.posadmin.service.MemberLeaseService;
import mmb.posadmin.service.MemberService;
import mmb.posadmin.service.OrderService;
import mmb.posadmin.service.PriceCardService;
import mmb.posadmin.service.ProductService;
import mmb.posadmin.service.SystemUpdateService;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

/**
 * pos机与posadmin接口 
 *
 */
public class PosAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(PosAction.class);

	/**
	 * 保存销售订单
	 */
	public void syncSaleInfoToPos() {
		String result = "error";
		try {
			//获取pos机传递的json数据
			String json = this.inputStreamToString(ServletActionContext.getRequest());
			log.info("syncSaleInfoToPos json=="+json);
			
			//解析JSON串
			SaledOrder order = new Gson().fromJson(json, SaledOrder.class);
			
			//保存销售订单信息
			if (order!=null && order.getProductList()!=null && !order.getProductList().isEmpty() && order.getMoneySourceList()!=null && !order.getMoneySourceList().isEmpty()) {
				new OrderService().saveSaledOrder(order);
				result = "ok";
			}
		} catch (Exception e) {
			log.error("保存销售订单时出现异常：", e);
		}
		
		//返回信息
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存租赁订单
	 */
	public void saveLeaseOrder() {
		OrderService os = new OrderService();
		String result = "error";
		try {
			//获取pos机传递的json数据
			String json = this.inputStreamToString(ServletActionContext.getRequest());
			
			List<ArrayList<Object>> list = os.doLeaseJson(json);
			LeaseOrder lo = (LeaseOrder) list.get(0).get(0);
			List<LeaseOrderProduct> leaseOrderProductList = new ArrayList<LeaseOrderProduct>();
			List<MemberLease> memberLeaselist = new ArrayList<MemberLease>();
			for (int i = 1; i < list.get(0).size(); i++) {
				leaseOrderProductList.add((LeaseOrderProduct) list.get(0).get(i));
			}
			for (int i = 0; i < list.get(1).size(); i++) {
				memberLeaselist.add((MemberLease) list.get(1).get(i));
			}
			
			//判断列表数据是否为空
			if(leaseOrderProductList.isEmpty()) {
				throw new Exception("租赁订单商品条目为空！");
			}
			
			if (os.saveLeaseOrder(lo, leaseOrderProductList,memberLeaselist)) {
				result = "ok";
			}
		} catch (Exception e) {
			log.error("保存租赁订单时出现异常：", e);
		}
		
		//返回信息
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(result);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 获取商品和促销活动数据
	 */
	public void syncProductToPos(){
		//获取商品列表
		String product = new ProductService().getAllProductJson();
		//获取活动列表
		String event = new EventService().posGetEventList();
		String result = "{" + product + "," + event + "}";
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据会员id查询会员信息
	 */
	public void posGetMemberById() {
		StringBuffer jsonResult = new StringBuffer("{");
		try {
			//获取POS机传递的JSON数据
			String json = this.inputStreamToString(ServletActionContext.getRequest());
			Member idMember = new Gson().fromJson(json, Member.class);
			
			//传递参数错误
			if(idMember==null || StringUtils.isBlank(idMember.getId())) {
				jsonResult.append("\"message\":\"传递参数错误！\"");
			} else {
				//获取会员信息
				Member member = new MemberService().getMemberById(idMember.getId());
				
				//拼装JSON格式的字符串
				if(member != null) {
					jsonResult.append("\"id\":\"").append(member.getId()).append("\"");
					jsonResult.append(",\"name\":\"").append(member.getName()==null ? "" : member.getName().replaceAll("\"", "\\\\\"")).append("\"");
					jsonResult.append(",\"idCard\":\"").append(member.getIdCard()==null ? "" : member.getIdCard()).append("\"");
					jsonResult.append(",\"registerTime\":").append(member.getRegisterTime()==null ? "" : member.getRegisterTime().getTime());
					jsonResult.append(",\"useState\":").append(member.getUseState());
					if(member.getMemberAccount() != null) {
						jsonResult.append(",\"availableBalance\":").append(member.getMemberAccount().getAvailableBalance());
						jsonResult.append(",\"freezeBalance\":").append(member.getMemberAccount().getFreezeBalance());
						jsonResult.append(",\"consumption\":").append(member.getMemberAccount().getConsumption());
					}
				}
			}
		} catch (Exception e) {
			jsonResult.append("\"message\":\"查询会员信息时出现异常："+e+"\"");
			log.error("根据会员id查询会员信息时出现异常：", e);
		}
		jsonResult.append("}");
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(jsonResult.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 新增会员信息(开会员卡)
	 */
	public void addNewMember(){
		String result = "error";
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		try {
			//获取POS机传递的JSON数据
			String json = this.inputStreamToString(ServletActionContext.getRequest());
			
			result = new MemberService().parseMemberJson(json.toString());
		} catch (Exception e) {
			log.error("新增会员信息(开会员卡)时出现异常：", e);
		}
		
		//返回数据
		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 退销售订单
	 * <br>可以退一个商品
	 * <br>也可以退整个订单(参与促销活动的订单)
	 */
	public void backSaledOrder(){
		//获取POS机传递的JSON数据
		String json = this.inputStreamToString(ServletActionContext.getRequest());
		
		//退销售订单
		String result = new OrderService().backSaledOrder(json);
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 归还租赁商品接口
	 */
	public void returnLeaseProduct(){
		OrderService os = new OrderService();
		String result = "error";
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		try {
			//获取pos机传递的json数据
			String json = this.inputStreamToString(ServletActionContext.getRequest());
			
			//归还租赁商品
			if(os.returnLeaseProduct(json)) {
				result = "ok";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//返回数据
		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询销售商品信息接口
	 */
	public void getSaledProduct(){
		OrderService os = new OrderService();
		String result = "";
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		try {
			//获取pos机传递的json数据
			String json = this.inputStreamToString(ServletActionContext.getRequest());
			
			//查询销售商品信息
			result = os.getSaledProduct(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//返回数据
		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询租赁商品信息接口
	 */
	public void getLeaseProduct(){
		OrderService os = new OrderService();
		String result = "";
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		try {
			//获取pos机传递的json数据
			String json = this.inputStreamToString(ServletActionContext.getRequest());
			
			//查询租赁商品信息
			result = os.getLeaseProduct(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//返回数据
		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * pos机查询收银员信息
	 * <br>首先判断收银员的用户名和密码是否正确，如果正确则返回收银员的详细信息
	 */
	public void getCashier() {
		CashierService cs = new CashierService();
		
		//获取pos机传递的json数据
		String json = this.inputStreamToString(ServletActionContext.getRequest());
		
		//查询收银员信息
		String result = cs.posGetCashier(json);
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * pos机查询店长信息
	 * <br>首先判断店长的用户名和密码是否正确，如果正确则返回店长的详细信息
	 */
	public void getShopManager() {
		CashierService cs = new CashierService();
		
		//获取pos机传递的json数据
		String json = this.inputStreamToString(ServletActionContext.getRequest());
		
		//查询店长信息
		String result = cs.posGetShopManager(json);
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * pos端录入银头信息
	 */
	public void initBalance(){
		//获取pos机传递的json数据
		String json = this.inputStreamToString(ServletActionContext.getRequest());
		
		//插入银头数据，返回操作结果
		String result = new BalanceService().InsertBalance(json);
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * pos端追加现金接口
	 */
	public void addCashBalance(){
		//获取pos机传递的json数据
		String json = this.inputStreamToString(ServletActionContext.getRequest());
		
		//插入银头数据，返回操作结果
		String result = new BalanceService().addCashBalance(json);
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * pos端交接之前查询交接信息
	 */
	public void queryCurrentBalance(){
		//获取pos机传递的json数据
		String json = this.inputStreamToString(ServletActionContext.getRequest());
		
		//插入银头数据，返回操作结果
		String result = new BalanceService().queryCurrentBalance(json);
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * POS端提交交接信息
	 * @param json pos机数据
	 */
	public void doHandoverBalance() {
		//获取POS机传递的JSON数据
		String json = this.inputStreamToString(ServletActionContext.getRequest());

		//POS端提交交接信息，返回操作结果
		String result = new BalanceService().doHandoverBalance(json);

		// 返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * POS端提交日结信息
	 * @param json pos机数据
	 */
	public void doFinishBalance() {
		// 获取POS机传递的JSON数据
		String json = this.inputStreamToString(ServletActionContext.getRequest());

		//日结
		String result = new BalanceService().doFinishBalance(json);

		// 返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * pos端租赁时查询会员租赁信息
	 */
	public void doSearchMemberForLease(){
		MemberLeaseService mls = new MemberLeaseService();
		//获取pos机传递的json数据
		String json = this.inputStreamToString(ServletActionContext.getRequest());
		//获取服务器返回的json信息
		String backjson = mls.getMemberLeaseJson(mls.parseMemberLeaseJson(json));
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(backjson);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * pos端还租时查询会员租赁信息
	 */
	public void doSearchMemberForBack(){
		MemberLeaseService mls = new MemberLeaseService();
		//获取pos机传递的json数据
		String json = this.inputStreamToString(ServletActionContext.getRequest());
		//获取服务器返回的json信息
		String backjson = mls.getMemberLeaseBackJson(mls.parseMemberLeaseJson(json));
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(backjson);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 会员提现
	 */
	public void doWithdrawMemberCash(){
		MemberService ms  = new MemberService();
		//获取pos机传递的json数据
		String json = this.inputStreamToString(ServletActionContext.getRequest());
		//获取服务器返回的json信息
		String backjson = ms.withdrawMember(ms.parseWithdrawMemberCashJson(json));
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(backjson);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 会员充值
	 */
	public void memberRecharge() {
		//获取POS机传递的JSON数据
		String json = this.inputStreamToString(ServletActionContext.getRequest());
		
		//处理POS机会员充值
		String result = new MemberService().posMemberRecharge(json);
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * pos机检查后台系统版本更新
	 */
	public void doCheckVersionUpdate(){
		//查询版本是否更新
		String result = new SystemUpdateService().checkNeedUpdate(); 
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取调价单
	 */
	public void getAdjustPriceBill() {
		//获取POS机传递的JSON数据
		String json = this.inputStreamToString(ServletActionContext.getRequest());
		
		//获取调价单
		String result = new AdjustPriceBillService().posGetAdjustPriceBill(json);
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取红蓝卡信息
	 */
	public void getPriceCard() {
		//获取POS机传递的JSON数据
		String json = this.inputStreamToString(ServletActionContext.getRequest());
		
		//获取调价卡
		String result = new PriceCardService().posGetPriceCard(json);
		
		//返回数据
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 把请求输入流转化为字符串
	 * @param request 请求对象
	 * @return
	 */
	private String inputStreamToString(HttpServletRequest request) {
		StringBuilder json = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
			String line;
			while((line=br.readLine()) != null) {
				json.append(line);
			}
		} catch (IOException e) {
			log.error("把请求输入流转化为字符串时出现异常：", e);
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
		}
		return json.toString();
	}
	
}
