package mmboa.util.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mmb.posadmin.service.PosManagerService;
import mmb.system.admin.AdminService;
import mmboa.util.Base64x;
import mmboa.util.CookieUtil;
import mmboa.util.LogUtil;
import mmboa.base.voUser;
import mmb.framework.IConstants;




/**
 * @author Bomb
 *
 */
public class UserControlFilter implements Filter {
    public static void main(String[] args){

    }

	public static Pattern accessAllowedIp = Pattern.compile("192\\.168\\..*|127\\.0\\..*|221\\.179\\.215\\..*|218\\.205\\.223\\.4[0-3]|58\\.215\\.221\\.4[2-6]|58\\.214\\.14\\.74|183\\.62\\.25\\.19[4-8]|218\\.240\\.58\\..*|120\\.195\\.108\\.136|124\\.207\\.34\\.1[3-5][0-9]");

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
//		loginURL = config.getInitParameter("LoginURL");
//		String pmask = config.getServletContext().getInitParameter("pmask");
//		voUser.pmaskId = StringUtil.toInt(pmask);
		//新IP地址过滤初始化
//		UserControlUtil.initAllowedIp();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest hsr = (HttpServletRequest)request;
		HttpSession session = hsr.getSession(false);
		
		String url = hsr.getServletPath();

		if (url.startsWith("/enter")){
			String ip = hsr.getRemoteAddr();
			PosManagerService pms = new PosManagerService();
			Set<String> ipSet = pms.getPosIp();
			if(ipSet.contains(ip)){
				chain.doFilter(request,response);
			}else{
				request.setAttribute("message", "对不起，您没有访问权限！<br/>如需访问，请联系管理员！");
				request.getRequestDispatcher("/error.jsp").forward(request, response);
			}
			return;
		}
		voUser user = null;
		if(session != null)
			user = (voUser)session.getAttribute("userView");
		
		if (user != null){

			chain.doFilter(request,response);
			return;
		} else 	{	// 试图从cookie登陆
			CookieUtil ck = new CookieUtil(hsr, (HttpServletResponse)response);		// 根据cookie登陆
			String username = ck.getCookieValue("opau");
			String password = ck.getCookieValue("opap");
			if(password != null)
				password = Base64x.decodeString(password);
			if(username != null && password != null)
			{
				voUser vo = new AdminService().getAdmin(username, password);
				if(vo != null)
				{
					session = hsr.getSession(true);
					session.setAttribute("waiburukou", "");
			    	
					session.setAttribute(IConstants.USER_VIEW_KEY, vo);
					// cookie登陆之前有日志但是没有记录username
	                String str = vo.getUsername() + "\t" + vo.getId() + "\t" + url + "\t" + hsr.getRemoteAddr();
	                LogUtil.logAccess(str);
					chain.doFilter(request,response);
					return;
				}
			}
		}
		String uri = hsr.getRequestURI();
		if(!uri.startsWith("/enter") && uri.length() > 15 && hsr.getMethod().equalsIgnoreCase("get")) {
			String qs = hsr.getQueryString();
			if(qs != null)
				uri += "?" + qs;
			((HttpServletResponse)response).sendRedirect(hsr.getContextPath() + "/enter/login.jsp?from=" + URLEncoder.encode(uri, "utf8"));
		} else {
			((HttpServletResponse)response).sendRedirect(hsr.getContextPath() + "/enter/login.jsp");
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
