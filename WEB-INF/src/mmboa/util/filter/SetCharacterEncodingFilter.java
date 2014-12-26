package mmboa.util.filter;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import mmb.posadmin.service.SystemUpdateService;
import mmb.posadmin.timertask.SyncCenterData;
import mmboa.base.voUser;
import mmboa.util.Constants;
import mmboa.util.LogUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;



/**
 * @author ���
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SetCharacterEncodingFilter implements Filter {

    // ----------------------------------------------------- Instance Variables

    /**
     * The default character encoding to set for requests that pass through this
     * filter.
     */
    protected String encoding = null;

    /**
     * The filter configuration object we are associated with. If this value is
     * null, this filter instance is not currently configured.
     */
    protected FilterConfig filterConfig = null;

    /**
     * Should a character encoding specified by the client be ignored?
     */
    protected boolean ignore = true;

    // --------------------------------------------------------- Public Methods

    /**
     * Take this filter out of service.
     */
    public void destroy() {

        this.encoding = null;
        this.filterConfig = null;

    }

    /**
     * Select and set (if specified) the character encoding to be used to
     * interpret request parameters for this request.
     * 
     * @param request
     *            The servlet request we are processing
     * @param result
     *            The servlet response we are creating
     * @param chain
     *            The filter chain we are processing
     * 
     * @exception IOException
     *                if an input/output error occurs
     * @exception ServletException
     *                if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        // Conditionally select and set the character encoding to be used
        if (ignore || (request.getCharacterEncoding() == null)) {
            String encoding = selectEncoding(request);
            if (encoding != null)
                request.setCharacterEncoding(encoding);
        }


        //用户访问记录
        try {
            HttpServletRequest hsr = (HttpServletRequest) request;
            String url = hsr.getRequestURL().toString();
            if (url.indexOf("GetChatUserList.do") != -1
                    || url.indexOf("ChatMessages.do") != -1) {
                chain.doFilter(request, response);
                return;
            }
            if ((url.toLowerCase().endsWith(".jsp") || url.toLowerCase()
                    .endsWith(".do"))
                    && !url.endsWith("login.do")) {
                String qs = hsr.getQueryString();
                HttpSession session = hsr.getSession();
                if (qs != null) {
                    url += "?" + qs;
                }
                voUser user = (voUser) session.getAttribute("userView");
                int userId = 0;
                String userName = "null";
                if (user != null) {
                    userId = user.getId();
                    userName = user.getUsername();
                }
                String str = userName + "\t" + userId + "\t" + url + "\t" + hsr.getRemoteAddr();
                LogUtil.logAccess(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        chain.doFilter(request, response);

    }

    /**
     * Place this filter into service.
     * 
     * @param filterConfig
     *            The filter configuration object
     */
    public void init(FilterConfig filterConfig) throws ServletException {

        this.filterConfig = filterConfig;
        this.encoding = filterConfig.getInitParameter("encoding");
        String value = filterConfig.getInitParameter("ignore");
        if (value == null)
            this.ignore = true;
        else if (value.equalsIgnoreCase("true"))
            this.ignore = true;
        else if (value.equalsIgnoreCase("yes"))
            this.ignore = true;
        else
            this.ignore = false;

        loadOther(filterConfig.getServletContext());
        
        //更新数据库
        try {
			new SystemUpdateService().update();
		} catch (Exception e) {
			System.exit(0);
			throw new ServletException(e);
		}
        
        //启动系统定时任务
        String syncCenterDataTime = Constants.config.getProperty("syncCenterDataTime");
        if(StringUtils.isNotBlank(syncCenterDataTime)) {
			new SyncCenterData().startSyncTask();
		}
    }
    
    private static void loadOther(ServletContext context) {
		
		String conf = context.getInitParameter("conf");
		if(conf != null) {
			Constants.CONFIG_PATH = conf;
		}

		try {
			loadConfig();
		} catch (Exception e) {
			System.out.println("[ERROR]config file load failed : " + Constants.CONFIG_PATH + "conf.properties");
			e.printStackTrace();
		}

    	String upload = context.getInitParameter("upload");
		if(upload != null) {
			Constants.UPLOAD_ROOT = upload;
			Constants.UPLOAD_PRODUCT_IMAGE = upload + "productImage/";
		}
    	String rep = context.getInitParameter("rep");
		if(rep != null) {
			Constants.RESOURCE_PRODUCT_IMAGE = rep + "/upload/productImage/";
		}
	}
    
    public static void loadConfig() throws Exception {
    	if(Constants.CONFIG_PATH == null) {
    		// 默认配置文件在项目路径下
    		Constants.CONFIG_PATH = Constants.class.getResource("/").toURI().resolve("../config").getPath() + "/";
    	}

		// 修改log4j配置文件位置
		PropertyConfigurator.configure(Constants.CONFIG_PATH + "log4j.properties");
		
		FileInputStream fis;

		fis = new FileInputStream(Constants.CONFIG_PATH + "conf.properties");
		Constants.config.load(fis);
		fis.close();
		
		// 如果有配置文件，以配置文件为主覆盖
    	String site = Constants.config.getProperty("site");
		if(site != null) {
			Constants.SITE = site;
		}
    	String upload = Constants.config.getProperty("upload");
		if(upload != null) {
			Constants.UPLOAD_ROOT = upload;
			Constants.UPLOAD_PRODUCT_IMAGE = upload + "productImage/";
		}
    	String rep = Constants.config.getProperty("rep");
		if(rep != null) {
			Constants.RESOURCE_PRODUCT_IMAGE = rep + "/upload/productImage/";
		}
    	String web = Constants.config.getProperty("web");	// 用于从前台获取页面预览用
		if(web != null) {
			Constants.WEB_ROOT = web;
		}
    	String download = Constants.config.getProperty("download");
		if(download != null) {
			Constants.DOWNLOAD_PATH = download;
		}
		String uploadDiary=Constants.config.getProperty("uploadDiary");
		if(uploadDiary != null) {
			Constants.UPLOAD_DIARY=uploadDiary;
		}
		System.out.println("[MSG]config file loaded : " + Constants.CONFIG_PATH + "conf.properties");
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Select an appropriate character encoding to be used, based on the
     * characteristics of the current request and/or filter initialization
     * parameters. If no character encoding should be set, return
     * <code>null</code>.
     * <p>
     * The default implementation unconditionally returns the value configured
     * by the <strong>encoding </strong> initialization parameter for this
     * filter.
     * 
     * @param request
     *            The servlet request we are processing
     */
    protected String selectEncoding(ServletRequest request) {

        return (this.encoding);

    }

}
