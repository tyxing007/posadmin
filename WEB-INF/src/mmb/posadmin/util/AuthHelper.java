package mmb.posadmin.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import mmb.framework.IConstants;
import mmboa.base.voUser;


public class AuthHelper {

	/**
	 * 获取当前用户的信息
	 * @return 当前用户对象
	 */
	public static voUser getCurrentUser() {
		voUser currentUser = null;
		HttpServletRequest request = ServletActionContext.getRequest();
		currentUser = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		return currentUser;
	}

}
