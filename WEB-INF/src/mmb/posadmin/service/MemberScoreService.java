package mmb.posadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mmb.posadmin.action.Page;
import mmb.posadmin.domain.MemberScore;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class MemberScoreService extends BaseService{
	
	private static Logger log = Logger.getLogger(MemberScoreService.class);
	
	/**
	 * 增加会员积分记录
	 * @param conn
	 * @param memberScore
	 * @throws SQLException
	 */
	public void addMemberScore(Connection conn , MemberScore memberScore) throws SQLException{
		PreparedStatement  ps  = conn.prepareStatement("insert into member_score(member_id,order_id,current_score,add_score,minus_score,create_time,type) values(?,?,?,?,?,?,?)");
		ps.setString(1, memberScore.getMemberId());
		ps.setInt(2,memberScore.getOrderId());
		ps.setInt(3, memberScore.getCurrentScore());
		ps.setInt(4, memberScore.getAddScore());
		ps.setInt(5, memberScore.getMinusScore());
		ps.setTimestamp(6, memberScore.getCreateTime());
		ps.setInt(7, memberScore.getType());
		ps.executeUpdate();
	}

	/**
	 * 分页获取会员积分信息
	 * @param page
	 * @param memberId
	 * @return
	 */
	public Page<MemberScore> getMemberScoreList(Page<MemberScore> page,Map<String, Object> param) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			Timestamp startTime = (Timestamp) param.get("startTime");
			Timestamp endTime = (Timestamp) param.get("endTime");
			String memberId = (String) param.get("memberId");
			Integer type = (Integer) param.get("type");
			String scoreFlow = (String) param.get("scoreFlow");
			
			StringBuilder condSql = new StringBuilder();
			
			if (StringUtils.isNotBlank(memberId)) {
				condSql.append(" and ms.member_id = ? ");
			}
			if ("add".equals(scoreFlow)) {
				condSql.append(" and ms.add_score != 0 ");
			}
			if ("minus".equals(scoreFlow)) {
				condSql.append(" and ms.minus_score != 0 ");
			}
			if (type != 0) {
				condSql.append(" and ms.type=? ");
			}
			if (startTime != null) {
				condSql.append(" and ms.create_time >= ? ");
			}
			if (endTime != null) {
				condSql.append(" and ms.create_time <= ? ");
			}
			

			// 查询总记录数
			int index = 1;
			StringBuilder sbcount = new StringBuilder();
			sbcount.append("select count(1) from member_score ms where 1=1 " + condSql);
			
			ps = conn.prepareStatement(sbcount.toString());
			
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
			int totalRecords = 0;
			if(rs.next()){
				totalRecords = rs.getInt(1);
			}
			page.setTotalRecords(totalRecords);
			
			if(totalRecords > 0){
			StringBuilder sb = new StringBuilder();
			sb.append(" select ms.member_id,ms.id,ms.add_score,ms.create_time,ms.current_score,ms.minus_score,ms.order_id,ms.type,so.serial_number from member_score ms left join saled_order so on so.id = ms.order_id ");
			sb.append(" where 1=1 ").append(condSql);
			sb.append(" order by ms.id desc limit ");
			sb.append(page.getFirstResult()).append(",").append(page.getPageCount());
			ps = conn.prepareStatement(sb.toString());
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
			List<MemberScore> list = new ArrayList<MemberScore>();
			MemberScore ms = null;
			while(rs.next()){
				ms = new MemberScore();
				ms.setAddScore(rs.getInt("add_score"));
				ms.setCreateTime(rs.getTimestamp("create_time"));
				ms.setCurrentScore(rs.getInt("current_score"));
				ms.setMemberId(rs.getString("member_id"));
				ms.setMinusScore(rs.getInt("minus_score"));
				ms.setOrderId(rs.getInt("order_id"));
				ms.setType(rs.getInt("type"));
				ms.setId(rs.getInt("id"));
				ms.setSerialNumber(rs.getString("serial_number"));
				list.add(ms);
			}
			page.setList(list);
		  }
		}catch(Exception e){
			log.error("查询会员积分信息出错", e);
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return page;
	}

}
