package mmb.posadmin.action;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.posadmin.domain.Member;
import mmb.posadmin.domain.MemberScore;
import mmb.posadmin.service.MemberScoreService;
import mmb.posadmin.service.MemberService;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class MemberScoreAction  extends ActionSupport {

	private static final long serialVersionUID = 8127093282584528416L;
	
	private static Logger log = Logger.getLogger(MemberScoreAction.class);

	private MemberScoreService mss = new MemberScoreService();
	private List<MemberScore> list;
	private Page<MemberScore> page;
	private MemberScore memberScore = new MemberScore();
	
	private String memberId;
	
	private int type;
	
	private String scoreFlow;//[积分流向：add：新增 minus：兑换]
	public List<MemberScore> getList() {
		return list;
	}
	public void setList(List<MemberScore> list) {
		this.list = list;
	}
	public Page<MemberScore> getPage() {
		return page;
	}
	public void setPage(Page<MemberScore> page) {
		this.page = page;
	}
	public MemberScore getMemberScore() {
		return memberScore;
	}
	public void setMemberScore(MemberScore memberScore) {
		this.memberScore = memberScore;
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
	public String getScoreFlow() {
		return scoreFlow;
	}
	public void setScoreFlow(String scoreFlow) {
		this.scoreFlow = scoreFlow;
	}
	
	/**
	 * 跳转至会员积分列表界面
	 * @return
	 */
	public String memberScoreList(){
		HttpServletRequest request = ServletActionContext.getRequest();
		try{
			if(this.page == null){
				this.page = new Page<MemberScore>();
			}
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
			param.put("scoreFlow", scoreFlow);
			setPage(mss.getMemberScoreList(page,param));
			setList(this.page.list);
			//获取会员信息
			Member member = new MemberService().getMemberById(memberId);
			request.setAttribute("member", member);
		}catch(Exception e){
			log.error("展示积分列表页面出错", e);
		}
		return SUCCESS;
	}

}
