package mmb.posadmin.action;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.posadmin.domain.Member;
import mmb.posadmin.domain.MemberAccountDetail;
import mmb.posadmin.service.MemberAccountDetailService;
import mmb.posadmin.service.MemberService;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class MemberAccountDetailAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(MemberAccountDetailAction.class);

	private MemberAccountDetailService mads = new MemberAccountDetailService();
	private Page<MemberAccountDetail> page = new Page<MemberAccountDetail>();
	private String memberId;
	private int type;
	private String moneyFlow; //资金流向[income:收入；pay:支出]
	
	public Page<MemberAccountDetail> getPage() {
		return page;
	}

	public void setPage(Page<MemberAccountDetail> page) {
		this.page = page;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMoneyFlow() {
		return moneyFlow;
	}

	public void setMoneyFlow(String moneyFlow) {
		this.moneyFlow = moneyFlow;
	}

	/**
	 * 跳转至收支明细列表界面
	 * @return
	 */
	public String memberAccountDetailList(){
		HttpServletRequest request = ServletActionContext.getRequest();
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
			param.put("memberId", memberId);
			param.put("type", type);
			param.put("moneyFlow", moneyFlow);
			
			//获取会员信息
			Member member = new MemberService().getMemberById(memberId);
			request.setAttribute("member", member);
			
			//分页获取收支明细列表信息
			mads.getMemberAccountDetailPage(page, param);
		} catch (Exception e) {
			log.error("跳转至收支明细列表界面时出现异常：", e);
			request.setAttribute("message", e.toString());
			return ERROR;
		}
		return SUCCESS;
	}

}
