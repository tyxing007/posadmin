package mmb.posadmin.service;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.posadmin.action.Page;
import mmb.posadmin.domain.CashStream;
import mmb.posadmin.domain.Member;
import mmb.posadmin.domain.MemberAccount;
import mmb.posadmin.domain.MemberAccountDetail;
import mmb.posadmin.domain.SystemConfig;
import mmb.posadmin.util.HttpURLUtil;
import mmboa.util.Constants;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbOperation;
import mmboa.util.db.DbUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.stream.JsonReader;

public class MemberService extends BaseService {
	
	private static Logger log = Logger.getLogger(MemberLeaseService.class);
	
	/**
	 * 获取会员列表信息
	 * @param page 分页信息
	 * @return
	 */
	public Page<Member> getMemberList(Page<Member> page,String memberId,String memberName){
		page = getPageFullValues(page,memberId,memberName);
		if(page.getTotalRecords() <= 0) {
			return page;
		}
		List<Member>  tmp = new ArrayList<Member>();
		DbOperation db = new DbOperation();
		Member m;
		MemberAccount ma;
		try{
			StringBuilder sql = new StringBuilder(50);
			sql.append("select m.id,m.name,m.mobile,m.id_card,m.register_time,m.change_time,m.use_state,ma.available_balance,ma.freeze_balance,ma.consumption,ma.score from member m left join member_account ma on m.id=ma.id where 1 = 1 ");
			if(StringUtils.isNotBlank(memberId)){
				sql.append(" and m.id like '%"+memberId+"%' ");
			}
			if(StringUtils.isNotBlank(memberName)){
				sql.append(" and m.name like '%"+memberName+"%' ");
			}
			sql.append(" order by m.use_state asc limit ");
			sql.append(page.getFirstResult()).append(",").append(page.getPageCount());
			ResultSet rs = db.executeQuery(sql.toString());
			while(rs.next()){
			    m = new Member();
			    ma = new MemberAccount();
			    m.setId(rs.getString("id"));
			    m.setName(rs.getString("name"));
			    m.setMobile(rs.getString("mobile"));
			    m.setIdCard(rs.getString("id_card"));
			    m.setUseState(rs.getInt("use_state"));
			    m.setRegisterTime(rs.getTimestamp("register_time"));
			    ma.setAvailableBalance(rs.getDouble("available_balance"));
			    ma.setFreezeBalance(rs.getDouble("freeze_balance"));
			    ma.setConsumption(rs.getDouble("consumption"));
			    ma.setScore(rs.getInt("score"));
			    m.setMemberAccount(ma);
			    tmp.add(m);
			}
			page.setList(tmp);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.release(db);
		}
		
		return page;
	}

	/**
	 *   获取当前分页的信息
	 * @param page
	 * @return
	 */
	public Page<Member> getPageFullValues(Page<Member> page,String memberId,String memberName){
		DbOperation db = new DbOperation();
		try{
			StringBuilder sb = new StringBuilder(50);
			sb.append("select count(1) from member m where 1 = 1 ");
			if(StringUtils.isNotBlank(memberId)){
				sb.append(" and m.id like '%"+memberId+"%' ");
			}
			if(StringUtils.isNotBlank(memberName)){
				sb.append(" and m.name like '%"+memberName+"%'");
			}
			ResultSet rs = db.executeQuery(sb.toString());
		    if(rs.next()){
		    	page.setTotalRecords(rs.getInt(1));
		    }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.release(db);
		}
		return page;
	}
	
	/**
	 * 根据会员id获取会员对象信息
	 * @param id 会员id
	 * @return
	 */
	public Member getMemberById(String id) {
		Member member = (Member) this.getXXX("`id`='"+id+"'", "member", Member.class.getName());
		if(member != null){
			MemberAccount memberAccount = (MemberAccount) this.getXXX("`id`='"+id+"'", "member_account", MemberAccount.class.getName());
			member.setMemberAccount(memberAccount);
		}
		return member;
	}

	/**
	 * 根据pos机传来的数据    更新会员信息【开新卡操作】
	 * @param json
	 * @return
	 * @throws  
	 */
	public String parseMemberJson(String json) {
		String sysState = "error";
		boolean oldAutoCommit = true;
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		try {
			// 记录原来事物提交的方式
			oldAutoCommit = conn.getAutoCommit();
			// 关闭自动提交事务
			conn.setAutoCommit(false);

			//解析JSON字符串，获取会员信息
			JsonReader jr = new JsonReader(new StringReader(json));
			String id = "";
			String name = "";
			String idCard = "";
			String posCode = ""; // pos机编号
			int cashierId = 0; // 收银员id
			String mobile = ""; // 手机号
			double availableBalance = 0.0;
			jr.beginObject();
			String attrName = null;
			while (jr.hasNext()) {
				attrName = jr.nextName();
				if ("id".equals(attrName)) {
					id = jr.nextString();
				} else if ("name".equals(attrName)) {
					name = jr.nextString();
				} else if ("idCard".equals(attrName)) {
					idCard = jr.nextString();
				} else if ("availableBalance".equals(attrName)) {
					availableBalance = jr.nextDouble();
				} else if ("posCode".equals(attrName)) {
					posCode = jr.nextString();
				} else if ("cashierId".equals(attrName)) {
					cashierId = jr.nextInt();
				} else if ("mobile".equals(attrName)) {
					mobile = jr.nextString();
				}
			}
			jr.endObject();
			
			//从数据库中获取会员信息
			Member member = getMemberById(id);
			
			//会员卡无效
			if (member == null) {
				sysState = "null";
				return sysState;
			}
			//会员卡不是‘新卡’状态
			else if (member.getUseState() != 2) {
				sysState = "inused";
				return sysState;
			}
			
			//更新会员信息
			ps1 = conn.prepareStatement("update member set id_card = ?,name = ?,register_time = ?,use_state = ?,mobile = ?,change_time = ?  where id = ?");
			ps1.setString(1, idCard);
			ps1.setString(2, name);
			ps1.setTimestamp(3, new Timestamp(new Date().getTime()));
			ps1.setInt(4, 0);
			ps1.setString(5, mobile);
			ps1.setTimestamp(6, new Timestamp(new Date().getTime()));
			ps1.setString(7, id);
			ps1.executeUpdate();
			
			//更新会员账户信息
			ps2 = conn.prepareStatement("update member_account set available_balance = ?, freeze_balance=0, consumption=0 where id = ? ");
			ps2.setDouble(1, availableBalance);
			ps2.setString(2, id);
			ps2.executeUpdate();

			// 保存现金流
			if (availableBalance > 0) {
				CashStreamService cs = new CashStreamService();
				CashStream cashStream = new CashStream();
				cashStream.setSerialNumber(null);
				cashStream.setPosCode(posCode);
				cashStream.setCashierId(cashierId);
				cashStream.setCash(availableBalance);
				cs.saveCashStream(conn, cashStream);
			}
			conn.commit();

			sysState = "ok";
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("POS机开新卡操作时出现异常：", e);
		} finally {
			DbUtil.closeConnection(null, ps1, null);
			DbUtil.closeConnection(null, ps2, null);
			if(conn != null){
				try {
					//还原事务提交方式
					conn.setAutoCommit(oldAutoCommit);
					conn.close();
				} catch (SQLException e) {}
			}
		}

		return sysState;
	}
	
	/**
	 * 批量更新Member信息表[同步中心库传来的Member数据]
	 * @param list
	 * @return
	 */
	public boolean batUpdateMemberInfo(List<Member> updatelist,List<Member> insertlist) {
		boolean sync = false;
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		PreparedStatement ps_account = null;
		ResultSet rs = null;
		boolean oldAutoCommit = true;
		try {
			// 记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			// 关闭自动提交事务
			conn.setAutoCommit(false);
			
			//从中心库更新的数据  此时不需要标记chang_time  因为它不需要再向中心库提交从中心库更新的数据
			ps = conn.prepareStatement("update member m  SET m.id_card = ?, m.mobile = ?, m.name = ?, m.register_time = ?, m.use_state = ? where m.id = ? ");
			
			int count = 0;
			for(Member member : updatelist){
				ps.setString(1, member.getIdCard());
				ps.setString(2, member.getMobile());
				ps.setString(3, member.getName());
				ps.setTimestamp(4, member.getRegisterTime());
				ps.setInt(5, member.getUseState());
				ps.setString(6, member.getId());
				count ++ ;
				ps.addBatch();
				
				//批量执行
				if (count%100==0 || count==updatelist.size()) {
					ps.executeBatch();
				}
			}
			
			ps = conn.prepareStatement("insert into member(id,name,id_card,mobile,register_time,use_state) values (?,?,?,?,?,?)");
			ps_account = conn.prepareStatement("insert into member_account(id) values(?)");
			count = 0;
			for(Member member : insertlist){
				ps.setString(1, member.getId());
				ps_account.setString(1, member.getId());
				ps.setString(2, member.getName());
				ps.setString(3, member.getIdCard());
				ps.setString(4, member.getMobile());
				ps.setTimestamp(5, member.getRegisterTime());
				ps.setInt(6, member.getUseState());
				
				count ++ ;
				ps.addBatch();
				ps_account.addBatch();
				
				//批量执行
				if (count%100==0 || count==insertlist.size()) {
					ps.executeBatch();
					ps_account.executeBatch();
				}
			}
			
			conn.commit();
			sync = true;
			
		} catch (Exception e) {
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("同步中心库店面会员信息出现异常：", e);
		} finally {
			if(conn != null){
				try {
					//还原事务提交方式
					conn.setAutoCommit(oldAutoCommit);
					conn.close();
				} catch (SQLException e) {
				}
			}
			DbUtil.closeConnection(rs, ps, conn);
			DbUtil.closeConnection(null, ps_account, null);
		}
		return sync;

	}
	
	/**
	 * 批量插入Member信息表
	 * @param list
	 * @return
	 */
	public boolean batInsertMemberInfo(List<Member> list) {
		boolean sync = false;
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean oldAutoCommit = true;
		try {
			// 记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			// 关闭自动提交事务
			conn.setAutoCommit(false);
			
			ps = conn.prepareStatement("insert into member(id,name,id_card,mobile,register_time,use_state) values (?,?,?,?,?,?)");
			
			int count = 0;
			for(Member member : list){
				ps.setString(1, member.getId());
				ps.setString(2, member.getName());
				ps.setString(3, member.getIdCard());
				ps.setString(4, member.getMobile());
				ps.setTimestamp(5, member.getRegisterTime());
				ps.setInt(6, member.getUseState());
				
				count ++ ;
				ps.addBatch();
				
				//批量执行
				if (count%100==0 || count==list.size()) {
					ps.executeBatch();
				}
			}
			conn.commit();
			sync = true;
			
		} catch (Exception e) {
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("插入会员信息出现异常：", e);
		} finally {
			if(conn != null){
				try {
					//还原事务提交方式
					conn.setAutoCommit(oldAutoCommit);
					conn.close();
				} catch (SQLException e) {
				}
			}
			DbUtil.closeConnection(rs, ps, conn);
		}
		return sync;

	}
	
	/**
	 * 处理POS会员提现
	 * @param data
	 * @return
	 */
	public String withdrawMember(Map<String, String> data) {
		Connection conn = DbUtil.getConnection();
		StringBuilder temp = new StringBuilder();
		PreparedStatement ps = null;
		boolean oldAutoCommit = true;
		try {
			// 记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			// 关闭自动提交事务
			conn.setAutoCommit(false);
			
			String memberId = data.get("memberId");
			Double withdrawCash = Double.valueOf(data.get("withdrawCash"));
			
			//记录会员收支明细
			MemberAccount ma = new MemberAccountService().getMemberAccountById(memberId);
			MemberAccountDetail detail = new MemberAccountDetail();
			detail.setMemberId(memberId);
			detail.setType(2); //提现
			detail.setPay(-withdrawCash);
			detail.setBalance(ma.getAvailableBalance() - withdrawCash);
			new MemberAccountDetailService().saveMemberAccountDetail(conn, detail);

			// 修改会员账户数据
			ps = conn.prepareStatement("update member_account ma set ma.available_balance = ma.available_balance - ?  where ma.id = ?");
			ps.setDouble(1, withdrawCash);
			ps.setString(2, memberId);
			ps.executeUpdate();

			// 保存现金流数据
			ps = conn.prepareStatement("insert into cash_stream(pos_code,cashier_id,cash,get_time) values(?,?,?,?)");
			ps.setString(1, data.get("posCode"));
			ps.setInt(2, Integer.parseInt(data.get("cashierId")));
			ps.setDouble(3, withdrawCash);
			ps.setTimestamp(4, new Timestamp(new Date().getTime()));
			ps.executeUpdate();

			conn.commit();
			temp.append("{\"message\":\"success\"}");
		} catch (Exception e) {
			try {
				// 回滚事务
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("处理POS会员提现时出现异常：", e);
			temp.append("{\"message\":\"处理POS会员提现时出现异常：" + e + "\"}");
		} finally {
			DbUtil.closeConnection(null, ps, null);
			if (conn != null) {
				try {
					// 还原事务提交方式
					conn.setAutoCommit(oldAutoCommit);
					conn.close();
				} catch (SQLException e) {}
			}
		}
		return temp.toString();
	}
	
	/**
	 * 解析Pos机传来的会员提现信息json串
	 * @param json
	 * @return
	 */
	public Map<String,String> parseWithdrawMemberCashJson(String json) {
		  Map<String,String> temp = new HashMap<String,String>();
		try {
			JsonReader jr = new JsonReader(new StringReader(json));
			jr.beginObject();
			String attrName;
			while (jr.hasNext()) {
				attrName = jr.nextName();
				if ("memberId".equals(attrName)) { // 会员id
					temp.put("memberId",jr.nextString());
				} else if ("withdrawCash".equals(attrName)) { // 商品数量
					temp.put("withdrawCash",String.valueOf(jr.nextDouble()));;
				} else if ("posCode".equals(attrName)) { // 单价
					temp.put("posCode",jr.nextString());
				} else if ("cashierId".equals(attrName)) {
					temp.put("cashierId",String.valueOf(jr.nextInt()));
				}
			}
			jr.endObject();
		} catch (Exception e) {
			log.error("解析json格式的pos机传递的会员提现信息数据时出现异常：", e);
		}

		return temp;

	}
	
	/**
	 *  过滤传过来的会员信息
	 * @param list center端传过来的list会员信息
	 * @return 0--更新的对象  1--需要插入对象
	 */
	public List<ArrayList<Member>> getFilterMemberList(List<Member> list){
		   List<ArrayList<Member>> temp = new ArrayList<ArrayList<Member>>();
		   List<Member>  temp_consist = new ArrayList<Member>();
		   List<Member>  temp_new  = new ArrayList<Member>();
		   Connection conn = DbUtil.getConnection();
		   PreparedStatement ps = null;
		   ResultSet rs = null;
		   try {
			    
		   for(Member m : list){
			   int i = 0;
			   ps = conn.prepareStatement("select count(1) from member where id = ?");
               ps.setString(1, m.getId());
			   rs = ps.executeQuery();
			   if(rs.next()){
				  i = rs.getInt(1);
			   }
			   if(i!=0){
				   temp_consist.add(m);
			   }else{
				   temp_new.add(m);
			   }
			   
		   }
		   temp.add((ArrayList<Member>)temp_consist);
		   temp.add((ArrayList<Member>)temp_new);
		   }catch(Exception e){
			   e.printStackTrace();
		   } finally{
			   if(rs != null){
				   try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			   }
			   if(ps != null){
				   try {
					   ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			   }
			   if(conn != null){
				   try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			   }
			  
		   }
		return temp;
	}
	
	/**
	 * 获取向中心库提交的会员列表数据
	 * @param memberLastSubmitTime 会员信息上次提交时间
	 * @return
	 */
	private List<Member> getSumbitMemberToCenter(Timestamp memberLastSubmitTime) {
		List<Member> temp = new ArrayList<Member>();
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select m.id,m.id_card,m.mobile,m.name,m.register_time,m.use_state from member m where m.change_time  > ?";
			ps = conn.prepareStatement(sql);
			ps.setTimestamp(1, memberLastSubmitTime);
			rs = ps.executeQuery();
			Member member;
			while (rs.next()) {
				member = new Member();
				member.setId(rs.getString("id"));
				member.setIdCard(rs.getString("id_card"));
				member.setMobile(rs.getString("mobile"));
				member.setName(rs.getString("name"));
				member.setRegisterTime(rs.getTimestamp("register_time"));
				member.setUseState(rs.getInt("use_state"));
				temp.add(member);
			}
		} catch (Exception e) {
			log.error("获取向中心库提交的会员列表数据时出现异常：", e);
		} finally {
			DbUtil.closeConnection(rs, ps, conn);
		}
		return temp;
	}
	
	/**
	 * 冻结会员账户的金额
	 * @param conn 数据库连接对象，此方法中不处理事务
	 * @param memberId 会员id
	 * @param freezeMoney 要冻结的金额
	 * @throws SQLException 
	 */
	public void freezeMemberAccount(Connection conn, String memberId, double freezeMoney) throws SQLException {
		String sql = "update member_account set available_balance=available_balance-"+freezeMoney+", freeze_balance=freeze_balance+"+freezeMoney+" where id=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, memberId);
		ps.executeUpdate();
		ps.close();
	}
	
	/**
	 * 会员充值
	 * @param conn 数据库连接对象，此方法中不处理事务
	 * @param memberId 会员id
	 * @param rechargeMoney 充值金额
	 * @throws SQLException
	 */
	public void recharge(Connection conn, String memberId, double rechargeMoney) throws SQLException {
		String sql = "update member_account set available_balance=available_balance+? where id=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setDouble(1, rechargeMoney);
		ps.setString(2, memberId);
		ps.executeUpdate();
		ps.close();
	}
	
	/**
	 * 扣除会员消费金额
	 * @param conn 数据库连接对象，此方法中不处理事务
	 * @param memberId 会员id
	 * @param deductMoney 要扣除的消费金额
	 * @throws SQLException
	 */
	public void deductMoney(Connection conn, String memberId, double deductMoney) throws SQLException {
		String sql = "update member_account set available_balance=available_balance-?, consumption=consumption+? where id=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setDouble(1, deductMoney);
		ps.setDouble(2, deductMoney);
		ps.setString(3, memberId);
		ps.executeUpdate();
		ps.close();
	}
	
	/**
	 * 追加会员消费金额
	 * @param conn 数据库连接对象，此方法中不处理事务
	 * @param memberId 会员id
	 * @param consumption 要追加的消费金额
	 * @throws SQLException
	 */
	public void appendConsumption(Connection conn, String memberId, double consumption) throws SQLException {
		String sql = "update member_account set consumption=consumption+? where id=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setDouble(1, consumption);
		ps.setString(2, memberId);
		ps.executeUpdate();
		ps.close();
	}
	
	/**
	 * 退款到会员账户
	 * @param conn 数据库连接对象，此方法中不处理事务
	 * @param memberId 会员id
	 * @param refund 退款金额
	 * @throws SQLException
	 */
	public void refund(Connection conn, String memberId, double refund) throws SQLException {
		String sql = "update member_account set available_balance=available_balance+?, consumption=consumption-? where id=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setDouble(1, refund);
		ps.setDouble(2, refund);
		ps.setString(3, memberId);
		ps.executeUpdate();
		ps.close();
	}
	
	/**
	 *  解析中心库同步会员基本信息的请求json
	 * @param json
	 * @return
	 */
	public List<Member> parseMemberJsonFromCenter(String json) {
		List<Member> temp = new ArrayList<Member>();
		Member member = null;
		try {
			JsonReader jr = new JsonReader(new StringReader(json));
			jr.beginObject();
			if ("member".equals(jr.nextName())) {
				jr.beginArray();
				String attrName;
				while (jr.hasNext()) {
					member = new Member();
					jr.beginObject();
					while (jr.hasNext()) {
						attrName = jr.nextName();
						if ("id".equals(attrName)) { //会员id
							member.setId(jr.nextString());
						} else if ("id_card".equals(attrName)) { //商品数量
							member.setIdCard(jr.nextString());
						} else if ("name".equals(attrName)) { //单价
							member.setName(jr.nextString());
						}else if ("mobile".equals(attrName)){
							member.setMobile(jr.nextString());
						}else if ("register_time".equals(attrName)){
							long registerTime = jr.nextLong();
							member.setRegisterTime(registerTime>0 ? new Timestamp(registerTime) : null);
						}else if ("use_state".equals(attrName)){
							member.setUseState(jr.nextInt());
						}
					}
					jr.endObject();
					temp.add(member);
				}
				jr.endArray();

			}
			jr.endObject();
		} catch (Exception e) {
			log.error("解析json格式的中心库同步会员基本信息数据时出现异常：", e);
		}
		return temp;
	}
	
	/**
	 * 生成向中心库提交的json串信息
	 * @param list
	 * @return
	 */
	public  String  doCreateMemberJsonToCenter(List<Member> list){
	     	StringBuilder temp = new StringBuilder();
	     	if(list == null || list.size() == 0){
	     		return "{\"member\":[]}";
	     	}
	     	temp.append("{\"member\":[");
	     	for(Member member : list){
	     		temp.append("{\"id\":\"").append(member.getId()).append("\",");
				temp.append("\"id_card\":\"").append(member.getIdCard()==null ? "" : member.getIdCard()).append("\",");
				temp.append("\"name\":\"").append(member.getName() ==null ? "" : member.getName()).append("\",");
				temp.append("\"mobile\":\"").append(member.getMobile() ==null ? "" : member.getMobile()).append("\",");
				temp.append("\"register_time\":").append(member.getRegisterTime().getTime()).append(",");
				temp.append("\"use_state\":").append(member.getUseState()).append("},");
	     	}
		   temp.deleteCharAt(temp.length()-1);
		   temp.append("]}");
	       return temp.toString();
	}
	
	
	/**
	 * 从中心库同步会员基本信息
	 * @throws Exception 
	 */
	public void syncMemberInfoFromPoscenter() throws Exception {
		//获取会员信息上次同步时间
		SystemConfig systemConfig = SystemConfig.getInstance();
		Timestamp memberLastSyncTime = systemConfig.getMemberLastSyncTime();
		
		// 请求数据
		String requestContent = "{\"lastUpdateTime\":" + memberLastSyncTime.getTime() + "}";

		//发送请求并获取返回结果
		String syncPoscenterMemberURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("syncPoscenterMemberURL");
		String json = HttpURLUtil.getResponseResult(syncPoscenterMemberURL, requestContent);
		
		// 获取中心库传来的member信息list对象
		List<Member> list = this.parseMemberJsonFromCenter(json);
		// 过滤中心库传来的list的member对象信息
		List<ArrayList<Member>> complexList = this.getFilterMemberList(list);
		boolean success = this.batUpdateMemberInfo(complexList.get(0), complexList.get(1));

		//修改同步时间
		if (success) {
			try {
				systemConfig.setMemberLastSyncTime(new Timestamp(new Date().getTime()));
				new SystemConfigService().updateSystemConfig(systemConfig);
			} catch (Exception e) {
				log.error("修改会员最后同步时间时出现异常：", e);
				throw new Exception("修改会员最后同步时间时出现异常");
			}
		}
	}
	
	/**
	 * 向中心库提交会员信息
	 * @throws Exception 
	 */
	public void syncMemberInfoToPoscenter() throws Exception {
		//获取会员信息上次提交时间
		SystemConfig systemConfig = SystemConfig.getInstance();
		Timestamp memberLastSubmitTime = systemConfig.getMemberLastSubmitTime();
		
		// 判断有无更改信息
		List<Member> list = this.getSumbitMemberToCenter(memberLastSubmitTime);
		if (list == null || list.size() == 0) {
			return ;
		}
		
		// 请求数据
		String requestContent = this.doCreateMemberJsonToCenter(list);

		//发送请求并获取返回结果
		String syncMemberToPoscenterURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("syncMemberToPoscenterURL");
		String json = HttpURLUtil.getResponseResult(syncMemberToPoscenterURL, requestContent);

		//修改同步时间
		if ("ok".equals(json)) {
			try {
				systemConfig.setMemberLastSubmitTime(new Timestamp(new Date().getTime()));
				new SystemConfigService().updateSystemConfig(systemConfig);
			} catch (Exception e) {
				log.error("修改会员信息上次提交时间时出现异常：", e);
				throw new Exception("修改会员信息上次提交时间时出现异常");
			}
		}
	}
	
	/**
	 * 处理POS机会员充值
	 * @param json POS机传递的JSON数据
	 * @return 操作结果JSON
	 */
	public String posMemberRecharge(String json) {
		StringBuilder result = new StringBuilder();
		Connection conn = DbUtil.getConnection();
		boolean oldAutoCommit = true;
		try {
			//解析JSON数据
			Map<String, Object> map = this.parsePosMemberRechargeJson(json);
			String memberId = (String) map.get("memberId");
			Double money = (Double) map.get("money");
			String posCode = (String) map.get("posCode");
			Integer cashierId = (Integer) map.get("cashierId");
			
			// 记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			// 关闭自动提交事务
			conn.setAutoCommit(false);
			
			//为会员充值
			this.recharge(conn, memberId, money);
			
			//保存现金流
			CashStream cashStream = new CashStream();
			cashStream.setSerialNumber(null);
			cashStream.setCash(money);
			cashStream.setCashierId(cashierId);
			cashStream.setPosCode(posCode);
			new CashStreamService().saveCashStream(conn, cashStream);
			
			//记录会员收支明细
			MemberAccount ma = new MemberAccountService().getMemberAccountById(memberId);
			MemberAccountDetail detail = new MemberAccountDetail();
			detail.setMemberId(memberId);
			detail.setType(1);
			detail.setIncome(money);
			detail.setBalance(ma.getAvailableBalance() + money);
			new MemberAccountDetailService().saveMemberAccountDetail(conn, detail);
			
			conn.commit();
			result.append("{\"message\":\"success\"}");
		} catch (Exception e) {
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			result.append("{\"message\":\"处理POS机会员充值时出现异常："+e+"\"}");
			log.error("处理POS机会员充值时出现异常：", e);
		} finally {
			if(conn != null){
				try {
					//还原事务提交方式
					conn.setAutoCommit(oldAutoCommit);
					conn.close();
				} catch (SQLException e) {}
			}
		}
		return result.toString();
	}
	
	
	/**
	 * 解析‘POS机会员充值’的JSON串
	 * @param json POS机传递的JSON数据
	 * @return
	 * @throws Exception 
	 */
	private Map<String, Object> parsePosMemberRechargeJson(String json) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JsonReader jr = new JsonReader(new StringReader(json));
			jr.beginObject();
			String attrName;
			while (jr.hasNext()) {
				attrName = jr.nextName();
				if ("memberId".equals(attrName)) { // 会员id
					map.put("memberId", jr.nextString());
				} else if ("money".equals(attrName)) { //充值金额
					map.put("money", jr.nextDouble());
				} else if ("posCode".equals(attrName)) { //POS机编号
					map.put("posCode", jr.nextString());
				} else if ("cashierId".equals(attrName)) { //收银员编号
					map.put("cashierId", jr.nextInt());
				}
			}
			jr.endObject();
		} catch (Exception e) {
			log.error("解析‘POS机会员充值’的JSON串时出现异常：", e);
			throw e;
		}
		return map;
	}
}
