package mmb.posadmin.action;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.posadmin.domain.ReturnOrder;
import mmb.posadmin.service.ReturnOrderService;
import mmb.posadmin.util.AuthHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class ReturnOrderAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ReturnOrderAction.class);
	
	private ReturnOrderService ros = new ReturnOrderService();
	private Page<ReturnOrder> page = new Page<ReturnOrder>();
	private ReturnOrder returnOrder = new ReturnOrder();
	
	//导入退货单
	private File excel;
	private String excelFileName;

	public Page<ReturnOrder> getPage() {
		return page;
	}

	public void setPage(Page<ReturnOrder> page) {
		this.page = page;
	}

	public ReturnOrder getReturnOrder() {
		return returnOrder;
	}

	public void setReturnOrder(ReturnOrder returnOrder) {
		this.returnOrder = returnOrder;
	}
	
	public File getExcel() {
		return excel;
	}

	public void setExcel(File excel) {
		this.excel = excel;
	}

	public String getExcelFileName() {
		return excelFileName;
	}

	public void setExcelFileName(String excelFileName) {
		this.excelFileName = excelFileName;
	}

	/**
	 * 跳转至退货单列表界面
	 * @return
	 */
	public String returnOrderList(){
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
			param.put("returnOrder", returnOrder);
			
			//分页获取退货单列表信息
			ros.getReturnOrderPage(page, param);
		} catch (ParseException e) {
			log.error("跳转至退货单列表界面时出现异常：", e);
		}
		
		return SUCCESS;
	}

	/**
	 * 跳转到退货单表单页面
	 * @return
	 */
	public String toReturnOrderFormView() {
		//修改
		if(returnOrder.getId() != 0) {
			this.returnOrder = ros.getReturnOrderById(this.returnOrder.getId());
		}else{
			this.returnOrder.setOrderNumber("TH"+String.valueOf(new Date().getTime()).substring(2, 10));
		}
		return INPUT;
	}
	
	/**
	 * 保存退货单信息
	 * @return
	 */
	public String saveReturnOrder() {
		try {
			//新建
			if(returnOrder.getId() == 0) {
				returnOrder.setCreateTime(new Timestamp(new Date().getTime()));
				ros.addXXX(returnOrder, "return_order");
			}
			//修改
			else {
				ros.updateReturnOrder(returnOrder);
			}
		} catch (Exception e) {
			ServletActionContext.getRequest().setAttribute("message", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		this.returnOrder = new ReturnOrder();
		return this.returnOrderList();
	}
	
	/**
	 * 删除退货单信息
	 * @return
	 */
	public String deleteReturnOrder() {
		//删除
		ros.deleteReturnOrderById(this.returnOrder.getId());
		return this.returnOrderList();
	}
	
	/**
	 * 提交退货单
	 * @return
	 */
	public String submitReturnOrder() {
		ros.submitReturnOrder(this.returnOrder.getId());
		return this.returnOrderList();
	}
	
	/**
	 * 导入退货单
	 * @return
	 */
	public String importReturnOrder() {
		boolean success = false;
		HttpServletRequest request = ServletActionContext.getRequest();
		
		//文件为空
		if(StringUtils.isBlank(excelFileName)) {
			request.setAttribute("message", "退货单文件为空！");
		}
		//非excel文件
		else if(!excelFileName.toUpperCase().endsWith(".XLS")) {
			request.setAttribute("message", "退货单文件必须为Excel文件！");
		} else {
			try {
				ros.importReturnOrder(excel);
				success = true;
			} catch (Exception e) {
				request.setAttribute("message", e);
			}
		}
		
		if(success) {
			return this.returnOrderList();
		} else {
			return ERROR;
		}
	}
	
}
