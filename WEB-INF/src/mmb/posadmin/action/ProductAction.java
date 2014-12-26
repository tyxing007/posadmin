package mmb.posadmin.action;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.posadmin.domain.Product;
import mmb.posadmin.service.GoodsClassService;
import mmb.posadmin.service.ProductService;
import mmb.posadmin.util.ResponseUtils;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class ProductAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ProductAction.class);
	private HttpServletRequest request = ServletActionContext.getRequest();

	private ProductService ps = new ProductService();
	private Page<Product> page = new Page<Product>();
	private Product product = new Product();
	
	public Page<Product> getPage() {
		return page;
	}

	public void setPage(Page<Product> page) {
		this.page = page;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Product getProduct() {
		return product;
	}
	/**
	 * 跳转至商品列表界面
	 * @return
	 */
	public String productList(){
		//获取查询参数
		String barCode = request.getParameter("barCode");
		String productName = request.getParameter("productName");
		request.setAttribute("barCode", barCode);
		request.setAttribute("productName", productName);
		
		//分页获取商品列表信息
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("barCode", barCode);
		param.put("productName", productName);
		this.page = ps.getProductPage(page, param);
		return SUCCESS;
	}
	
	/**
	 * 跳转到选择商品列表页面（供弹出层调用）
	 * @return
	 */
	public String toSelectProductListView(){
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("productName", this.product.getName());
		setPage(ps.getProductPage(page, param));
		return "selectProductList";
	}

	/**
	 * 跳转到商品表单页面
	 * @return
	 */
	public String toProductFormView() {
		//修改
		if(product.getId() != 0) {
			this.product = ps.getProductById(product.getId());
		}
		return INPUT;
	}
	
	/**
	 * 跳转到商品详情页面
	 * @return
	 */
	public String toProductDetailView() {
		//获取商品信息
		this.product = ps.getProductById(product.getId());
		return "detail";
	}
	
	/**
	 * 保存商品信息
	 * @return
	 */
	public String saveProduct() {
		try {
			//新建
			if(product.getId() == 0) {
				ps.addXXX(product, "product");
			}
			//修改
			else {
				ps.updateProduct(product);
			}
		} catch (Exception e) {
			request.setAttribute("message", "保存商品信息时出现异常："+e);
			return ERROR;
		}
		return this.productList();
	}
	
	/**
	 * 从中心库同步商品信息
	 * @return
	 */
	public String syncProductDataFromPoscenter() {
		try {
			//同步商品分类
			GoodsClassService gcs = new GoodsClassService();
			gcs.syncGoodsClassDataFromPoscenter();
			
			//同步商品
			ps.syncProductDataFromPoscenter();
		} catch (Exception e) {
			request.setAttribute("message", "同步商品分类信息时出现异常："+e.getMessage());
			log.error("同步商品分类信息时出现异常：", e);
			return ERROR;
		}
		return this.productList();
	}
	
	/**
	 * 把库存信息提交到中心库
	 * @return
	 */
	public void submitShopStockToCenter() {
		String result = ps.submitShopStockToCenter();
		ResponseUtils.renderText(ServletActionContext.getResponse(), result);
	}
	
	/**
	 * 导出Excel商品列表
	 * @return
	 */
	public void exportExcel() {
		try {
			//获取Excel数据
			byte[] data = ps.exportExcel();
			
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=productList.xls");
			OutputStream out = new BufferedOutputStream(response.getOutputStream());
			out.write(data);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("导出Excel商品列表时出现异常：", e);
		}
	}
	
	/**
	 * 跳转到查看商品销售标签图片页面
	 * @return
	 */
	public String toShowSaleTagView() {
		try {
			//获取商品信息
			this.product = ps.getProductById(product.getId());
			
			//获取销售标签图片路径
			String saleTegImgPath = ps.getSaleTagImgPath(this.product);
			request.setAttribute("saleTegImgPath", saleTegImgPath);
		} catch (Exception e) {
			request.setAttribute("message", "创建商品销售标签图片时出现异常：" + e);
			return ERROR;
		}
		return "showSaleTag";
	}
	
	/**
	 * 导出商品销售标签图片
	 */
	public void exportSaleTagImg() {
		try {
			//获取销售标签图片数据
			String saleTegImgPath = request.getParameter("saleTegImgPath");
			byte[] data = FileUtils.readFileToByteArray(new File(ServletActionContext.getServletContext().getRealPath("/") + saleTegImgPath));
			
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=" + saleTegImgPath.substring(saleTegImgPath.lastIndexOf("/")+1));
			OutputStream out = new BufferedOutputStream(response.getOutputStream());
			out.write(data);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("导出商品销售标签图片时出现异常：", e);
		}
	}
	
}
