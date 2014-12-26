package mmb.posadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mmb.posadmin.action.Page;
import mmb.posadmin.domain.MemberAccountDetail;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class MemberAccountDetailService extends BaseService {
	
	private static Logger log = Logger.getLogger(MemberAccountDetailService.class);
	
	/**
	 * 保存会员收支明细
	 * @param conn 数据库连接对象，此方法中不处理事务
	 * @param detail 会员收支明细
	 * @throws SQLException
	 */
	public void saveMemberAccountDetail(Connection conn, MemberAccountDetail detail) throws SQLException {
		String sql = "insert member_account_detail(member_id,type,order_id,income,pay,balance,create_time) values(?,?,?,?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, detail.getMemberId());
		ps.setInt(2, detail.getType());
		ps.setInt(3, detail.getOrderId());
		ps.setDouble(4, detail.getIncome());
		ps.setDouble(5, detail.getPay());
		ps.setDouble(6, detail.getBalance());
		ps.setTimestamp(7, new Timestamp(new Date().getTime()));
		ps.executeUpdate();
		ps.close();
	}
	
	/**
	 * 分页获取收支明细列表信息
	 * @param page 分页信息
	 * @param param [startTime:开始时间；endTime:结束时间；memberId:会员id；type:类型；moneyFlow:资金流向]
	 * @return
	 */
	public Page<MemberAccountDetail> getMemberAccountDetailPage(Page<MemberAccountDetail> page, Map<String, Object> param) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 查询条件
			StringBuilder condSql = new StringBuilder();
			Timestamp startTime = (Timestamp) param.get("startTime");
			Timestamp endTime = (Timestamp) param.get("endTime");
			String memberId = (String) param.get("memberId");
			Integer type = (Integer) param.get("type");
			String moneyFlow = (String) param.get("moneyFlow");
			if (StringUtils.isNotBlank(memberId)) {
				condSql.append(" and o.member_id = ? ");
			}
			if ("income".equals(moneyFlow)) {
				condSql.append(" and o.income!=0 ");
			}
			if ("pay".equals(moneyFlow)) {
				condSql.append(" and o.pay!=0 ");
			}
			if (type != 0) {
				condSql.append(" and o.type=? ");
			}
			if (startTime != null) {
				condSql.append(" and o.create_time>=? ");
			}
			if (endTime != null) {
				condSql.append(" and o.create_time<=? ");
			}

			// 查询总记录数
			int index = 1;
			ps = conn.prepareStatement("select count(o.id) from member_account_detail o where 1=1 " + condSql);
			if (StringUtils.isNotBlank(memberId)) {
				ps.setString(index++, memberId);
			}
			if (type != 0) {
				ps.setInt(index++, type);
			}
			if (startTime != null) {
				ps.setTimestamp(index++, startTime);
			}
			if (endTime != null) {
				ps.setTimestamp(index++, endTime);
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				page.setTotalRecords(rs.getInt(1));
			}

			// 获取列表数据
			if (page.getTotalRecords() > 0) {
				List<MemberAccountDetail> list = new ArrayList<MemberAccountDetail>();
				MemberAccountDetail o;
				StringBuilder sql = new StringBuilder(50);
				sql.append("select o.id,o.member_id,o.type,o.order_id,o.income,o.pay,o.balance,o.create_time from member_account_detail o");
				sql.append(" where 1=1 ").append(condSql);
				sql.append(" order by o.id desc limit ");
				sql.append(page.getFirstResult()).append(",").append(page.getPageCount());
				ps = conn.prepareStatement(sql.toString());
				index = 1;
				if (StringUtils.isNotBlank(memberId)) {
					ps.setString(index++, memberId);
				}
				if (type != 0) {
					ps.setInt(index++, type);
				}
				if (startTime != null) {
					ps.setTimestamp(index++, startTime);
				}
				if (endTime != null) {
					ps.setTimestamp(index++, endTime);
				}
				rs = ps.executeQuery();
				while (rs.next()) {
					o = new MemberAccountDetail();
					o.setId(rs.getInt("id"));
					o.setMemberId(rs.getString("member_id"));
					o.setType(rs.getInt("type"));
					o.setOrderId(rs.getInt("order_id"));
					o.setIncome(rs.getDouble("income"));
					o.setPay(rs.getDouble("pay"));
					o.setBalance(rs.getDouble("balance"));
					o.setCreateTime(rs.getTimestamp("create_time"));
					list.add(o);
				}
				page.setList(list);
			}
		} catch (Exception e) {
			log.error("分页获取收支明细列表信息时出现异常：", e);
		} finally {
			DbUtil.closeConnection(rs, ps, conn);
		}
		return page;
	}
	
}
