package mmb.posadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import mmb.posadmin.domain.MemberAccount;
import mmboa.util.db.BaseService;

import org.apache.log4j.Logger;

public class MemberAccountService extends BaseService {
	
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(MemberAccountService.class);
	
	/**
	 * 根据会员id获取会员账户对象信息
	 * @param memberId 会员id
	 * @return
	 */
	public MemberAccount getMemberAccountById(String memberId) {
		return (MemberAccount) this.getXXX("`id`='"+memberId+"'", "member_account", MemberAccount.class.getName());
	}
	
	/**
	 * 根据会员id更新会员账户信息
	 * @param conn
	 * @param memberAccount
	 * @throws SQLException
	 */
	public void updateMemberAccount(Connection conn , MemberAccount memberAccount) throws SQLException{
			PreparedStatement ps = conn.prepareStatement(" update member_account ma set ma.score = ?,ma.available_balance = ?,ma.consumption=?,ma.freeze_balance = ? where ma.id = ? ");
			ps.setInt(1, memberAccount.getScore());
			ps.setDouble(2, memberAccount.getAvailableBalance());
			ps.setDouble(3, memberAccount.getConsumption());
			ps.setDouble(4, memberAccount.getFreezeBalance());
			ps.setString(5,memberAccount.getId());
			ps.executeUpdate();
	}
	
}
