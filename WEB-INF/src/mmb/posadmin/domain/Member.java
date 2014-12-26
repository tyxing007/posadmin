package mmb.posadmin.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 会员基本信息表
 * @author hanquan
 *
 */
public class Member  implements Serializable {

	
	private static final long serialVersionUID = -3731365178508966283L;
	
	/**
	 * 会员id
	 */
	public String id;
	
	/**
	 * 会员名称
	 */
	public String name;
	
	/**
	 * 会员身份证号
	 */
	public String idCard;
	
	/**
	 * 手机号
	 */
	public String mobile;
	 
	/**
	 * 注册时间
	 */
	public Timestamp registerTime;
	
	/**
	 * 使用状态[0:使用中; 1:已注销]
	 */
	public int useState;
	
	/**
	 * 本地会员信息更改时间，只更新pos机更新的会员时间,不更新中心库下发的更新时间
	 */
	public Timestamp changeTime;
	
	/**
	 * 会员账户信息
	 */
	private MemberAccount memberAccount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Timestamp getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Timestamp registerTime) {
		this.registerTime = registerTime;
	}

	public int getUseState() {
		return useState;
	}

	public void setUseState(int useState) {
		this.useState = useState;
	}

	public MemberAccount getMemberAccount() {
		return memberAccount;
	}

	public void setMemberAccount(MemberAccount memberAccount) {
		this.memberAccount = memberAccount;
	}

	public Timestamp getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(Timestamp changeTime) {
		this.changeTime = changeTime;
	}

	
	
	
}
