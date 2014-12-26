package mmb.posadmin.domain;

import java.io.Serializable;

/**
 * @author likaige 2013-03-18
 * 会员账户对象
 */
public class MemberAccount implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 会员id
	 */
	public String id;
	
	/**
	 * 可用余额
	 */
	public double availableBalance;
	
	/**
	 * 冻结金额
	 */
	public double freezeBalance;
	
	/**
	 * 总消费金额
	 */
	public double consumption;
	
	/**
	 * 会员积分
	 */
	public int score;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(double availableBalance) {
		this.availableBalance = availableBalance;
	}

	public double getFreezeBalance() {
		return freezeBalance;
	}

	public void setFreezeBalance(double freezeBalance) {
		this.freezeBalance = freezeBalance;
	}

	public double getConsumption() {
		return consumption;
	}

	public void setConsumption(double consumption) {
		this.consumption = consumption;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	

}
