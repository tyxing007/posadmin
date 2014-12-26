package mmb.posadmin.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.posadmin.domain.Balance;
import mmb.posadmin.service.BalanceService;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class BalanceAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;

	private BalanceService bs = new BalanceService();
	private List<Balance> balanceList;
	private String posCode; //POS机编号
	
	public List<Balance> getBalanceList() {
		return balanceList;
	}

	public void setBalanceList(List<Balance> balanceList) {
		this.balanceList = balanceList;
	}
	
	public String getPosCode() {
		return posCode;
	}

	public void setPosCode(String posCode) {
		this.posCode = posCode;
	}

	/**
	 * 跳转至交接列表界面
	 * @return
	 */
	public String balanceList(){
		HttpServletRequest request = ServletActionContext.getRequest();
		
		//日期
		String date = request.getParameter("date");
		if(StringUtils.isBlank(date)) {
			date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		}
		
		//查询参数
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("posCode", posCode);
		param.put("date", date);
		this.balanceList = bs.getBalanceList(param);
		request.setAttribute("date", date);
		
		return SUCCESS;
	}

}
