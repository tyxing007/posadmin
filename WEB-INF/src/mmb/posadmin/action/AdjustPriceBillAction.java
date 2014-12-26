package mmb.posadmin.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.posadmin.domain.AdjustPriceBill;
import mmb.posadmin.service.AdjustPriceBillService;
import mmb.posadmin.util.ResponseUtils;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class AdjustPriceBillAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AdjustPriceBillAction.class);
	private HttpServletRequest request = ServletActionContext.getRequest();

	private AdjustPriceBillService apbs = new AdjustPriceBillService();
	private Page<AdjustPriceBill> page = new Page<AdjustPriceBill>();
	private AdjustPriceBill adjustPriceBill = new AdjustPriceBill();
	
	private String billNumber; //调价单号
	private String productName; //商品名称
	private int auditStatus; //审核状态
	private int useStatus; //使用状态
	
	public Page<AdjustPriceBill> getPage() {
		return page;
	}

	public void setPage(Page<AdjustPriceBill> page) {
		this.page = page;
	}

	public AdjustPriceBill getAdjustPriceBill() {
		return adjustPriceBill;
	}

	public void setAdjustPriceBill(AdjustPriceBill adjustPriceBill) {
		this.adjustPriceBill = adjustPriceBill;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(int auditStatus) {
		this.auditStatus = auditStatus;
	}

	public int getUseStatus() {
		return useStatus;
	}

	public void setUseStatus(int useStatus) {
		this.useStatus = useStatus;
	}
	
	/**
	 * 跳转至调价单列表界面
	 * @return
	 */
	public String adjustPriceBillList(){
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("billNumber", this.billNumber);
		param.put("productName", this.productName);
		param.put("auditStatus", this.auditStatus);
		param.put("useStatus", this.useStatus);
		apbs.getAdjustPriceBillPage(page, param);
		return SUCCESS;
	}
	
	/**
	 * 跳转到调价单表单页面
	 * @return
	 */
	public String toFormView() {
		//修改
		if(adjustPriceBill.getId() != 0) {
			this.adjustPriceBill = apbs.getDetailById(adjustPriceBill.getId());
		}
		return INPUT;
	}
	
	/**
	 * 保存调价单信息
	 * @return
	 */
	public void saveAdjustPriceBill() {
		String result = "success";
		try {
			//新建
			if(adjustPriceBill.getId() == 0) {
				apbs.saveAdjustPriceBill(adjustPriceBill);
			}
			//修改
			else {
				apbs.updateAdjustPriceBill(adjustPriceBill);
			}
		} catch (Exception e) {
			log.error("保存调价单信息时出现异常：", e);
			result = "保存调价单信息时出现异常："+e;
		}
		ResponseUtils.renderText(ServletActionContext.getResponse(), result);
	}
	
	/**
	 * 删除调价单信息
	 * @return
	 */
	public String deleteAdjustPriceBill() {
		apbs.deleteById(this.adjustPriceBill.getId());
		return this.adjustPriceBillList();
	}
	
	/**
	 * 从中心库同步调价单的审核状态
	 */
	public void syncAdjustPriceBill() {
		String result = "true";
		try {
			//加一个application范围的锁
			synchronized (request.getServletContext()) {
				apbs.syncAdjustPriceBill();
			}
		} catch (Exception e) {
			result = "从中心库同步调价单的审核状态时出现异常："+e;
		}
		ResponseUtils.renderText(ServletActionContext.getResponse(), result);
	}
	
}
