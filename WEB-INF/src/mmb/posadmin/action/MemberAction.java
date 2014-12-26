package mmb.posadmin.action;

import java.io.UnsupportedEncodingException;
import java.util.List;

import mmb.posadmin.domain.Member;
import mmb.posadmin.service.MemberService;
import mmb.posadmin.util.ResponseUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class MemberAction extends ActionSupport {
	
	private static final long serialVersionUID = -1361017035104559995L;
	private static Logger log = Logger.getLogger(MemberAction.class);

	private MemberService ms = new MemberService();
	private List<Member> list;
	private Page<Member> page;
	private Member member = new Member();
	private String memberId;
	private String memberName;
	
	public List<Member> getList() {
		return list;
	}

	public void setList(List<Member> list) {
		this.list = list;
	}

	public Page<Member> getPage() {
		return page;
	}

	public void setPage(Page<Member> page) {
		this.page = page;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	/**
	 * 跳转至会员列表界面
	 * @return
	 */
	public String memberList(){
		if(this.page == null){
			this.page = new Page<Member>();
		}
		if(StringUtils.isNotBlank(this.memberName)){
			try {
				this.memberName = new String(this.memberName.getBytes("ISO-8859-1"),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		setPage(ms.getMemberList(page,this.memberId,this.memberName));
		setList(this.page.list);
		return SUCCESS;
	}

	/**
	 * 跳转到会员详情页面
	 * @return
	 */
	public String toMemberDetailView() {
		//获取会员信息
		this.member = ms.getMemberById(this.member.getId());
		
		return "detail";
	}
	
	/**
	 * 从中心库同步会员基本信息
	 * @return
	 */
	public String syncMemberInfoFromPoscenter() {
		try {
			ms.syncMemberInfoToPoscenter();
			ms.syncMemberInfoFromPoscenter();
		} catch (Exception e) {
			log.error("同步会员信息时出现异常：", e);
			ServletActionContext.getRequest().setAttribute("message", "同步会员信息时出现异常："+e);
			return ERROR;
		}
		return this.memberList();
	}
	
	/**
	 * 向中心库提交会员信息
	 */
	public void syncMemberInfoToPoscenter() {
		String result = "true";
		try {
			ms.syncMemberInfoToPoscenter();
		} catch (Exception e) {
			log.error("向中心库提交会员信息时出现异常：", e);
			result = "向中心库提交会员信息时出现异常："+e;
		}
		ResponseUtils.renderText(ServletActionContext.getResponse(), result);
	}
	
}
