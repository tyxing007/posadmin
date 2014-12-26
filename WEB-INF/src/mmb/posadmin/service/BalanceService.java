package mmb.posadmin.service;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mmb.posadmin.domain.Balance;
import mmb.posadmin.domain.Cashier;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.stream.JsonReader;

public class BalanceService extends BaseService  {
	
	private static Logger log = Logger.getLogger(BalanceService.class);
	
	/**
	 * 获取指定日期的所有POS机的交接记录
	 * @param param [date:查询日期；posCode:POS机编号]
	 * @return
	 */
	public List<Balance> getBalanceList(Map<String, Object> param) {
		List<Balance> balanceList = new ArrayList<Balance>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			//查询条件
			String date = (String)param.get("date");
			String posCode = (String)param.get("posCode");
			StringBuilder condSql = new StringBuilder();
			if(StringUtils.isNotBlank(posCode)) {
				condSql.append(" and b.pos_code = ?");
			}
			
			//获取列表数据
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT b.id,b.pos_code,b.cashier_id,c.`name` cashierName,b.start_time,b.theory_cash,b.pos_cash,b.handover_id,rc.`name` receiveName,b.init_poscash,b.add_poscash ");
			sql.append(" from balance b join cashier c on b.cashier_id=c.id left join cashier rc on b.handover_id=rc.id");
			sql.append(" where b.start_time>STR_TO_DATE(?,'%Y-%m-%d') and b.start_time<ADDDATE(STR_TO_DATE(?,'%Y-%m-%d'), 1)");
			sql.append(condSql);
			sql.append(" ORDER BY b.pos_code, b.start_time");
			conn = DbUtil.getConnection();
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, date);
			ps.setString(2, date);
			if(StringUtils.isNotBlank(posCode)) {
				ps.setString(3, posCode);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				Balance balance = new Balance();
				balance.setId(rs.getInt("id"));
				balance.setPosCode(rs.getString("pos_code"));
				balance.setStartTime(rs.getTimestamp("start_time"));
				balance.setTheoryCash(rs.getDouble("theory_cash"));
				balance.setPosCash(rs.getDouble("pos_cash"));
				balance.setInitPoscash(rs.getDouble("init_poscash"));
				balance.setAddPoscash(rs.getDouble("add_poscash"));
				//收银员信息
				Cashier cashier = new Cashier();
				cashier.setName(rs.getString("cashierName"));
				balance.setCashierId(rs.getInt("cashier_id"));
				balance.setCashier(cashier);
				//接收人信息
				Cashier handover = new Cashier();
				handover.setName(rs.getString("receiveName"));
				balance.setHandoverId(rs.getInt("handover_id"));
				balance.setHandover(handover);
				balanceList.add(balance);
			}
		}catch(Exception e){
			log.error("获取指定日期的所有POS机的交接记录时出现异常：", e);
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return balanceList;
	}
	
	/**
	 * 返回收银机状态：
	 *  0--录银头，1—正常，2—强制日结（保留），3—交接（保留）
	 * @return
	 */
	public String getPosState(String posCode){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = DbUtil.getConnection();
			ps = conn.prepareStatement("select count(1) from balance b where b.pos_code = ? and b.start_time < ? and b.start_time > ?");
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date1 = sdf.format(date);
			String date2 = date1.substring(0,11).trim().concat(" 00:00:00");
			System.out.println(date2);
			
		    ps.setString(1, posCode);
		    ps.setString(2, date1);
		    ps.setString(3, date2);
		    
		    rs = ps.executeQuery();
		    
		    int count = -1;
		    if(rs.next()){
		    	count = rs.getInt(1);
		    }
			if(count == 0){
				return "0";
			}else if(count > 0){
				return "1";
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return "1";
	}
  /**
   * 根据pos机传来数据导入录入银头信息
   * @param json pos机数据
   */
	public String InsertBalance(String json) {
		String result = "error";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			String posCode = null; // pos机编码
			int cashierId = 0; // 收银员id
			double initPoscash = 0.00;
			JsonReader jr = new JsonReader(new StringReader(json));
			jr.beginObject();
			String attrName = null;
			while (jr.hasNext()) {
				attrName = jr.nextName();
				if ("posCode".equals(attrName)) {
					posCode = jr.nextString();
				} else if ("cashierId".equals(attrName)) {
					cashierId = jr.nextInt();
				} else if ("initPoscash".equals(attrName)) {
					initPoscash = jr.nextDouble();
				}
			}
			jr.endObject();

			conn = DbUtil.getConnection();
			ps = conn
					.prepareStatement("insert into balance(pos_code,cashier_id,init_poscash,start_time) values(?,?,?,?)");
			Date d = new Date();
			Timestamp t = new Timestamp(d.getTime());
			ps.setString(1, posCode);
			ps.setInt(2, cashierId);
			ps.setDouble(3, initPoscash);
			ps.setTimestamp(4, t);
			ps.executeUpdate();
			result = "ok";

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		
		return result;
	}
    
	 /**
	   * 根据pos机传来数据追加现金信息
	   * @param json pos机数据
	   */
		public String addCashBalance(String json) {
			String result = "error";
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				String posCode = null; // pos机编码
				int cashierId = 0; // 收银员id
				double addPoscash = 0.00;
				JsonReader jr = new JsonReader(new StringReader(json));
				jr.beginObject();
				String attrName = null;
				while (jr.hasNext()) {
					attrName = jr.nextName();
					if ("posCode".equals(attrName)) {
						posCode = jr.nextString();
					} else if ("cashierId".equals(attrName)) {
						cashierId = jr.nextInt();
					} else if ("addPoscash".equals(attrName)) {
						addPoscash = jr.nextDouble();
					}
				}
				jr.endObject();

				conn = DbUtil.getConnection();
				ps = conn
						.prepareStatement("update balance set add_poscash = add_poscash + ? where pos_code = ? and cashier_id = ?  order by id desc limit 1");
				ps.setDouble(1, addPoscash);
				ps.setString(2, posCode);
				ps.setInt(3, cashierId);
				
				ps.executeUpdate();
				result = "ok";

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (ps != null) {
						ps.close();
					}
					if (conn != null) {
						conn.close();
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
			
			return result;
		}
		
		
		 /**
		   * pos端交接之前查询交接信息
		   * @param json pos机数据
		   */
			public String queryCurrentBalance(String json) {
				StringBuilder sb = new StringBuilder();
				Connection conn = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					Timestamp startTime = null;
					int cashierId = 0;
					String posCode = null; // pos机编码
					double theoryCash = 0.00;
					double initPoscash = 0.00;
					double addPoscash = 0.00;
					JsonReader jr = new JsonReader(new StringReader(json));
					jr.beginObject();
					String attrName = null;
					while (jr.hasNext()) {
						attrName = jr.nextName();
						if ("posCode".equals(attrName)) {
							posCode = jr.nextString();
						}
					}
					jr.endObject();

					conn = DbUtil.getConnection();
					//查询上班时间
					ps = conn.prepareStatement("select add_poscash,init_poscash,cashier_id, start_time from balance where pos_code = ? order by id desc limit 1");
					ps.setString(1, posCode);
					rs = ps.executeQuery();
					if(rs.next()){
						addPoscash = rs.getDouble("add_poscash");
						initPoscash = rs.getDouble("init_poscash");
						startTime = rs.getTimestamp("start_time");
						cashierId = rs.getInt("cashier_id");
					}
					
					Date d = new Date();
					Timestamp endTime = new Timestamp(d.getTime());
					
					//统计理论金额
				
					ps = conn.prepareStatement("select sum(cash) from cash_stream where pos_code = ? and cashier_id = ? and get_time between ? and ? ");
					ps.setString(1, posCode);
					ps.setInt(2, cashierId);
					ps.setTimestamp(3, startTime);
					ps.setTimestamp(4, endTime);
					rs = ps.executeQuery();
					if(rs.next()){
						theoryCash = rs.getDouble(1);
					}
					theoryCash = theoryCash + initPoscash + addPoscash;
					//将理论金额更新至数据库
					ps = conn
							.prepareStatement("update balance set theory_cash = ? where pos_code = ? and cashier_id = ?  order by id desc limit 1");
					ps.setDouble(1, theoryCash);
					ps.setString(2, posCode);
					ps.setInt(3, cashierId);
					ps.executeUpdate();
					
				    sb.append("{\"posCode\":\""+posCode+"\",\"theoryCash\":"+theoryCash+"}");
				    return sb.toString();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (rs != null) {
							rs.close();
						}
						if (ps != null) {
							ps.close();
						}
						if (conn != null) {
							conn.close();
						}

					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
				
				return sb.toString();
			}
		
			
	/**
	 * POS端提交交接信息
	 * @param json pos机数据
	 */
	public String doHandoverBalance(String json) {
		String result = "error";
		Connection conn = DbUtil.getConnection();;
		PreparedStatement ps = null;
		boolean oldAutoCommit = true;
		try {
			//解析JSON字符串
			String posCode = null; // pos机编码
			int cashierId = 0; // 收银员id
			double posCash = 0.00;
			int handoverId = 0;// 接收人id
			String backText = null; // 备注信息
			int isShopkeeper = -1;
			JsonReader jr = new JsonReader(new StringReader(json));
			jr.beginObject();
			String attrName = null;
			while (jr.hasNext()) {
				attrName = jr.nextName();
				if ("posCode".equals(attrName)) {
					posCode = jr.nextString();
				} else if ("cashierId".equals(attrName)) {
					cashierId = jr.nextInt();
				} else if ("posCash".equals(attrName)) {
					posCash = jr.nextDouble();
				} else if ("theoryCash".equals(attrName)) {
					jr.nextDouble();
				} else if ("handoverId".equals(attrName)) {
					handoverId = jr.nextInt();
				} else if ("backText".equals(attrName)) {
					backText = jr.nextString();
				} else if ("isShopkeeper".equals(attrName)) {
					isShopkeeper = jr.nextInt();
				}
			}
			jr.endObject();
			
			//记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			//关闭自动提交事务
			conn.setAutoCommit(false);
			
			// 接收人不是店长
			if (isShopkeeper == 0) {
				//更新收银员的交接记录信息
				ps = conn.prepareStatement("update balance set end_time = ? , pos_cash = ? , handover_id = ? , back_text = ? , is_finish = ? where pos_code = ? and cashier_id = ?  order by id desc limit 1");
				Date d = new Date();
				Timestamp t = new Timestamp(d.getTime());
				ps.setTimestamp(1, t);
				ps.setDouble(2, posCash);
				ps.setInt(3, handoverId);
				ps.setString(4, backText);
				ps.setInt(5, 0);
				ps.setString(6, posCode);
				ps.setInt(7, cashierId);
				ps.executeUpdate();
				
				// 记录接收人的收银初始化信息
				ps = conn.prepareStatement("insert into balance(pos_code,cashier_id,init_poscash,start_time) values(?,?,?,?)");
				ps.setString(1, posCode);
				ps.setInt(2, handoverId);
				ps.setDouble(3, posCash);
				ps.setTimestamp(4, t);
				ps.executeUpdate();
			} 
			// 接收人是店长
			else {
				ps = conn.prepareStatement("update balance set end_time = ? , pos_cash = ? ,  back_text = ?  where pos_code = ? and cashier_id = ?  order by id desc limit 1");
				Date d = new Date();
				Timestamp t = new Timestamp(d.getTime());
				ps.setTimestamp(1, t);
				ps.setDouble(2, posCash);
				ps.setString(3, backText);
				ps.setString(4, posCode);
				ps.setInt(5, cashierId);
				ps.executeUpdate();
			}

			result = "ok";
		} catch (Exception e) {
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {}
			log.error("POS端提交交接信息时出现异常：", e);
		} finally {
			DbUtil.closeConnection(null, ps, null);
			if(conn != null){
				try {
					//还原事务提交方式
					conn.setAutoCommit(oldAutoCommit);
					conn.close();
				} catch (SQLException e) {}
			}
		}
		return result;
	}
	
	/**
	 * POS端日结
	 * @param json POS机数据
	 */
	public String doFinishBalance(String json) {
		String result = "error";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			String posCode = null; // pos机编码
			String backText = null; // 备注信息
			JsonReader jr = new JsonReader(new StringReader(json));
			jr.beginObject();
			String attrName = null;
			while (jr.hasNext()) {
				attrName = jr.nextName();
				if ("posCode".equals(attrName)) {
					posCode = jr.nextString();
				} else if ("posCash".equals(attrName)) {
					jr.nextDouble();
				} else if ("backText".equals(attrName)) {
					backText = jr.nextString();
				}
			}
			jr.endObject();

			//日结，更新该POS机的最后一条交接记录
			conn = DbUtil.getConnection();
			ps = conn.prepareStatement("update balance set end_time = ? , back_text = CONCAT(back_text , ? ) , is_finish = ? where pos_code = ?  order by id desc limit 1");
			ps.setTimestamp(1, new Timestamp(new Date().getTime()));
			ps.setString(2, backText);
			ps.setInt(3, 1);
			ps.setString(4, posCode);
			ps.executeUpdate();
			result = "ok";
		} catch (Exception e) {
			log.error("POS端日结时出现异常：", e);
		} finally {
			DbUtil.closeConnection(null, ps, conn);
		}
		return result;
	}
	
}
