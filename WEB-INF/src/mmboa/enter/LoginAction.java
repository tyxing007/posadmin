package mmboa.enter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mmb.system.admin.AdminService;
import mmboa.util.Base64x;
import mmboa.util.CookieUtil;
import mmboa.util.StringUtil;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Namespace;

import mmboa.base.voUser;
import mmb.framework.IConstants;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Bomb
 *
 */
@Namespace("/enter")
public class LoginAction extends ActionSupport {
	


	HttpServletRequest request = ServletActionContext.getRequest();
	HttpServletResponse response = ServletActionContext.getResponse();
	
	public String execute() throws Exception {

		String username = StringUtil.dealParam(request.getParameter("username"));
		String password = request.getParameter("password"); 
		
		CookieUtil ck = new CookieUtil(request, response);		// 写入cookie
		if(username != null) {		// post提交才有效
			boolean ru = request.getParameter("ru") != null;
	    	if(ru) {		// 记住用户名
	    		if(username.length() > 0)
	    			ck.setCookie("u", username, 2000000000);
	    		ck.setCookie("ru", "1", 2000000000);
	    	} else {
	    		ck.removeCookie("u");
	    		ck.setCookie("ru", "0", 2000000000);
	    	}
		}
		HttpSession session = request.getSession(false);
		try{
			if(username == null) {
				return SUCCESS;
			}
			voUser vo = new AdminService().getAdmin(username, password);

			if(vo == null) {
				return SUCCESS;
			}

			session.setAttribute("waiburukou", "");
			
	    	boolean rp = request.getParameter("rp") != null;
	    	if(rp) {	// 自动登录（7天）
		    	ck.setCookieSafe("opau", username, 86400 * 7);
		    	ck.setCookieSafe("opap", Base64x.encodeString(password), 86400 * 7);
		    	ck.setCookie("rp", "1", 86400 * 365);
	    	} else {
		    	ck.setCookieSafe("opau", username, -1);
		    	ck.setCookieSafe("opap", Base64x.encodeString(password), -1);
		    	ck.setCookie("rp", "2", 86400 * 365);
	    	}

//	    	if(rp) {		// 记住密码
//	    		ck.setCookie("p", password, 2000000000);
//	    		ck.setCookie("rp", "1", 2000000000);
//	    		request.getSession().setAttribute("simplifyWeb", "1");// 简化版
//	    	} else {
//	    		ck.removeCookie("p");
//	    		ck.setCookie("rp", "0", 2000000000);
//	    	}
			request.getSession().setAttribute(IConstants.USER_VIEW_KEY, vo);
			
		} catch (Exception e) {
			return SUCCESS;
		}
		String red = request.getParameter("from");	// 登陆后立即跳转到该页面
		if(red == null)
			red = "../index.jsp";
		else
			session.removeAttribute("loginredirect");
		response.sendRedirect(red);
		return null;
	}
}
