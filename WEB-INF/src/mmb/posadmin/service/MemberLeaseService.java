package mmb.posadmin.service;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mmb.posadmin.domain.MemberLease;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbOperation;
import mmboa.util.db.DbUtil;

import org.apache.log4j.Logger;

import com.google.gson.stream.JsonReader;

public class MemberLeaseService extends BaseService {
	
	private static Logger log = Logger.getLogger(MemberLeaseService.class);
    
	/**
	 * 租赁时生成会员租赁信息的JSON字符串
	 * @param memberId
	 * @return
	 */
	public String getMemberLeaseJson(String memberId){
		StringBuilder temp = new StringBuilder(200);
		DbOperation db = new DbOperation("oa");
		try{
			Date d  = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(d);
			String sql_del = "delete from member_lease where product_id = 0 and (end_time < '" + date+"' or type = 0 )";
			db.executeUpdate(sql_del);
			StringBuilder sql = new StringBuilder(50);
			sql.append("select ml.id,ml.type,ml.serial_number,ml.start_time,ml.end_time,ml.type,ml.member_id,m.name memberName,m.id_card,m.use_state,ma.available_balance,ma.consumption,ma.freeze_balance,ml.product_id,p.bar_code,p.name productName from member_lease ml LEFT JOIN member m ON ml.member_id = m.id LEFT JOIN member_account ma on ml.member_id = ma.id LEFT JOIN product p on ml.product_id = p.id where  ml.member_id = '"+memberId+"'");
			ResultSet rs = db.executeQuery(sql.toString());
			temp.append("{\"products\":[");
			if(!rs.next()){
				temp.append("],");
				String sql_member = "SELECT * from member m LEFT JOIN member_account ma on m.id = ma.id where m.id = '"+memberId+"'";
				ResultSet rs_member = db.executeQuery(sql_member.toString());
				if(rs_member.next()){
					temp.append("\"memberId\":\"").append(rs_member.getString("id").replaceAll("\"", "\\\\\"")).append("\",");
				    temp.append("\"memberName\":\"").append(rs_member.getString("name").replaceAll("\"", "\\\\\"")).append("\",");
				    temp.append("\"idCard\":\"").append(rs_member.getString("id_card").replaceAll("\"", "\\\\\"")).append("\",");
				    temp.append("\"useState\":").append(rs_member.getString("use_state")).append(",");
				    temp.append("\"availableBalance\":").append(rs_member.getDouble("available_balance")).append(",");
				    temp.append("\"consumption\":").append(rs_member.getDouble("consumption")).append(",");
				    temp.append("\"freezeBalance\":").append(rs_member.getDouble("freeze_balance")).append("}");
				}else{
					temp = new StringBuilder("{\"message\":\"没有该会员\"}");
				}
				return temp.toString();
			}else{
				rs.previous();
			}
			while(rs.next()){
				temp.append("{\"id\":").append(rs.getInt("id")).append(",");
				temp.append("\"productId\":").append(rs.getInt("product_id")).append(",");
				temp.append("\"barCode\":\"").append(rs.getString("bar_code") == null ? "" : rs.getString("bar_code").replaceAll("\"", "\\\\\"")).append("\",");
				temp.append("\"productName\":\"").append(rs.getString("productName") == null ? "" : rs.getString("productName").replaceAll("\"", "\\\\\"")).append("\",");
				temp.append("\"startTime\":").append(rs.getTimestamp("start_time").getTime()).append(",");
				temp.append("\"endTime\":").append(rs.getTimestamp("end_time").getTime()).append(",");
				temp.append("\"type\":").append(rs.getInt("type")).append(",");
				temp.append("\"serialNumber\":\"").append(rs.getString("serial_number")).append("\"},");
				if(rs.isLast()){
					temp.deleteCharAt(temp.length()-1);
				}
			}
			rs.previous();
			temp.append("],");
			temp.append("\"memberId\":\"").append(rs.getString("member_id").replaceAll("\"", "\\\\\"")).append("\",");
		    temp.append("\"memberName\":\"").append(rs.getString("memberName").replaceAll("\"", "\\\\\"")).append("\",");
		    temp.append("\"idCard\":\"").append(rs.getString("id_card").replaceAll("\"", "\\\\\"")).append("\",");
		    temp.append("\"useState\":").append(rs.getString("use_state")).append(",");
		    temp.append("\"availableBalance\":").append(rs.getDouble("available_balance")).append(",");
		    temp.append("\"consumption\":").append(rs.getDouble("consumption")).append(",");
		    temp.append("\"freezeBalance\":").append(rs.getDouble("freeze_balance")).append("}");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.release(db);
		}
		return temp.toString();
	}
	
	/**
	 * 还租时生成会员租赁信息的JSON字符串
	 * @param memberId
	 * @return
	 */
	public String getMemberLeaseBackJson(String memberId){
		StringBuilder temp = new StringBuilder(200);
		DbOperation db = new DbOperation("oa");
		try{
			Date d  = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(d);
			String sql_del = "delete from member_lease where product_id = 0 and (end_time < '"+ date+"' or type = 0 )";
			db.executeUpdate(sql_del);
			StringBuilder sql = new StringBuilder(50);
			sql.append("select ml.id,ml.type,ml.serial_number,ml.start_time,ml.end_time,ml.type,ml.member_id,m.name memberName,m.id_card,m.use_state,ma.available_balance,ma.consumption,ma.freeze_balance,ml.product_id,p.bar_code,p.name productName from member_lease ml LEFT JOIN member m ON ml.member_id = m.id LEFT JOIN member_account ma on ml.member_id = ma.id LEFT JOIN product p on ml.product_id = p.id where ml.product_id != 0 and ml.member_id = '"+memberId+"'");
			ResultSet rs = db.executeQuery(sql.toString());
			temp.append("{\"products\":[");
			if(!rs.next()){
				temp.append("],");
				String sql_member = "SELECT * from member m LEFT JOIN member_account ma on m.id = ma.id where m.id = '"+memberId+"'";
				ResultSet rs_member = db.executeQuery(sql_member.toString());
				if(rs_member.next()){
					temp.append("\"memberId\":\"").append(rs_member.getString("id").replaceAll("\"", "\\\\\"")).append("\",");
				    temp.append("\"memberName\":\"").append(rs_member.getString("name").replaceAll("\"", "\\\\\"")).append("\",");
				    temp.append("\"idCard\":\"").append(rs_member.getString("id_card").replaceAll("\"", "\\\\\"")).append("\",");
				    temp.append("\"useState\":").append(rs_member.getString("use_state")).append(",");
				    temp.append("\"availableBalance\":").append(rs_member.getDouble("available_balance")).append(",");
				    temp.append("\"consumption\":").append(rs_member.getDouble("consumption")).append(",");
				    temp.append("\"freezeBalance\":").append(rs_member.getDouble("freeze_balance")).append("}");
				}else{
					temp = new StringBuilder("{\"message\":\"没有该会员\"}");
				}
				return temp.toString();
			}else{
				rs.previous();
			}
			while(rs.next()){
				temp.append("{\"id\":").append(rs.getInt("id")).append(",");
				temp.append("\"productId\":").append(rs.getInt("product_id")).append(",");
				temp.append("\"barCode\":\"").append(rs.getString("bar_code") == null ? "" : rs.getString("bar_code").replaceAll("\"", "\\\\\"")).append("\",");
				temp.append("\"productName\":\"").append(rs.getString("productName") == null ? "" : rs.getString("productName").replaceAll("\"", "\\\\\"")).append("\",");
				temp.append("\"startTime\":").append(rs.getTimestamp("start_time").getTime()).append(",");
				temp.append("\"endTime\":").append(rs.getTimestamp("end_time").getTime()).append(",");
				temp.append("\"type\":").append(rs.getInt("type")).append(",");
				temp.append("\"serialNumber\":\"").append(rs.getString("serial_number")).append("\"},");
				if(rs.isLast()){
					temp.deleteCharAt(temp.length()-1);
				}
			}
			rs.previous();
			temp.append("],");
			temp.append("\"memberId\":\"").append(rs.getString("member_id").replaceAll("\"", "\\\\\"")).append("\",");
		    temp.append("\"memberName\":\"").append(rs.getString("memberName").replaceAll("\"", "\\\\\"")).append("\",");
		    temp.append("\"idCard\":\"").append(rs.getString("id_card").replaceAll("\"", "\\\\\"")).append("\",");
		    temp.append("\"useState\":").append(rs.getString("use_state")).append(",");
		    temp.append("\"availableBalance\":").append(rs.getDouble("available_balance")).append(",");
		    temp.append("\"consumption\":").append(rs.getDouble("consumption")).append(",");
		    temp.append("\"freezeBalance\":").append(rs.getDouble("freeze_balance")).append("}");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.release(db);
		}
		return temp.toString();
	}
	
	/**
	 * 保存或更新会员租赁信息
	 * @param conn 此方法不控制事务
	 * @param memberLeaseList 会员租赁列表数据
	 * @throws Exception 
	 */
	public void saveOrUpdateMemberLease(Connection conn, List<MemberLease> memberLeaseList) throws Exception {
		PreparedStatement ps = null;
		try {
			//区分新增和更新数据
			List<MemberLease> insertList = new ArrayList<MemberLease>();
			List<MemberLease> updateList = new ArrayList<MemberLease>();
			for(MemberLease ml : memberLeaseList) {
				if(ml.getId() == 0) {
					insertList.add(ml);
				} else {
					updateList.add(ml);
				}
			}
			
			//批量插入
			if(!insertList.isEmpty()) {
				ps = conn.prepareStatement("insert into member_lease(`serial_number`,`member_id`,`product_id`,`start_time`,`end_time`,`type`) values (?,?,?,?,?,?)");
				for(MemberLease ml : insertList) {
					ps.setString(1, ml.getSerialNumber());
					ps.setString(2, ml.getMemberId());
					ps.setInt(3, ml.getProductId());
					ps.setTimestamp(4, ml.getStartTime());
					ps.setTimestamp(5, ml.getEndTime());
					ps.setInt(6, ml.getType());
					ps.addBatch();
				}
				//批量执行
				ps.executeBatch();
			}
			
			//批量更新
			if(!updateList.isEmpty()) {
				ps = conn.prepareStatement("update member_lease set `serial_number`=?,`product_id`=? where `id`=?");
				for(MemberLease ml : updateList) {
					ps.setString(1, ml.getSerialNumber());
					ps.setInt(2, ml.getProductId());
					ps.setInt(3, ml.getId());
					ps.addBatch();
				}
				//批量执行
				ps.executeBatch();
			}
		} catch (Exception e) {
			log.error("保存或更新会员租赁信息时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(null, ps, null);
		}
	}
	
	/**
	 *  解析查询会员租赁信息的请求json
	 * @param json
	 * @return
	 */
	public String parseMemberLeaseJson(String json) {
		String memberId = "";
		try {
			JsonReader jr = new JsonReader(new StringReader(json));
			jr.beginObject();
			if ("memberId".equals(jr.nextName())) {
				memberId = jr.nextString();
			}
			jr.endObject();
		} catch (Exception e) {
			log.error("解析json格式的查询会员租赁信息数据时出现异常：", e);
		}
		return memberId;
	}
	
}
