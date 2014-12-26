package mmb.posadmin.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.posadmin.domain.Invoice;
import mmb.posadmin.domain.Product;
import mmb.posadmin.service.InvoiceService;
import mmb.posadmin.util.ResponseUtils;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class InvoiceAction extends ActionSupport{

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(InvoiceAction.class);
	
	private InvoiceService is = new InvoiceService();
	private Page<Product> productPage = new Page<Product>();
	private Page<Invoice> page = new Page<Invoice>();
	private Invoice invoice = new Invoice();
	private int productId;

	public Page<Product> getProductPage() {
		return productPage;
	}

	public void setProductPage(Page<Product> productPage) {
		this.productPage = productPage;
	}

	public Page<Invoice> getPage() {
		return page;
	}

	public void setPage(Page<Invoice> page) {
		this.page = page;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	/**
	 * 跳转至商品列表界面
	 * @return
	 */
	public String productList(){
		//获取查询参数
		HttpServletRequest request = ServletActionContext.getRequest();
		String barCode = request.getParameter("barCode");
		String productName = request.getParameter("productName");
		String goodsClassName = request.getParameter("goodsClassName");
		request.setAttribute("barCode", barCode);
		request.setAttribute("productName", productName);
		request.setAttribute("goodsClassName", goodsClassName);
		
		//分页获取盘点商品列表信息
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("barCode", barCode);
		param.put("productName", productName);
		param.put("goodsClassName", goodsClassName);
		this.productPage = is.getProductPage(this.productPage, param);
		return "productList";
	}

	/**
	 * 跳转至进销存列表界面
	 * @return
	 */
	public String invoiceList(){
		this.page = is.getInvoicePage(page, this.productId);
		return SUCCESS;
	}
	
	/**
	 * 向中心库提交进销存信息
	 */
	public void syncInvoiceInfoToPoscenter() {
		String result = "true";
		try {
			//加一个application范围的锁，避免重复提交
			synchronized (ServletActionContext.getRequest().getServletContext()) {
				is.syncInvoiceInfoToPoscenter();
			}
		} catch (Exception e) {
			result = "向中心库提交进销存信息时出现异常："+e;
		}
		ResponseUtils.renderText(ServletActionContext.getResponse(), result);
	}
	
}
