package mmb.posadmin.action;

import javax.servlet.http.HttpServletRequest;

import mmb.posadmin.domain.SystemConfig;
import mmb.posadmin.util.HttpURLUtil;
import mmboa.util.Constants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

/**
 * poscenter与posadmin接口
 * 
 */
public class PoscenterAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(PoscenterAction.class);
	
	private HttpServletRequest request = ServletActionContext.getRequest();

	/**
	 * 店面店ip地址
	 */
	private String shopIpAddress;
	
	public String getShopIpAddress() {
		return shopIpAddress;
	}

	public void setShopIpAddress(String shopIpAddress) {
		this.shopIpAddress = shopIpAddress;
	}

	/**
	 * 向中心库提交店面店IP地址
	 */
	public String shopIpUpdateForm() {
		String result = "";
		if (StringUtils.isNotBlank(shopIpAddress)) {
			try {
				//获取店面编号
				String shopCode = SystemConfig.getInstance().getShopCode();
				String syncPoscenterShopIpAddressURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("syncPoscenterShopIpAddressURL");
				String json = "{\"shopCode\":\"" + shopCode + "\",\"shopIpAddress\":\"" + shopIpAddress + "\"}";
				
				String response = HttpURLUtil.getResponseResult(syncPoscenterShopIpAddressURL, json);
				if ("{\"message\":\"ok\"}".equals(response)) {
					result = SUCCESS;
				}
			} catch (Exception e) {
				log.error("更新店面店Ip地址时出现异常：", e);
				request.setAttribute("message", "更新店面店Ip地址时出现异常："+e);
				result = ERROR;
			}
		} else {
			result = "updateip";
		}
		return result;
	}

}
