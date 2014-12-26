package mmb.posadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mmb.posadmin.domain.Event;
import mmb.posadmin.domain.MoneyDiscountEvent;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;

import org.apache.log4j.Logger;

public class MoneyDiscountEventService extends BaseService {
	
	private static Logger log = Logger.getLogger(MoneyDiscountEventService.class);
	
	/**
	 * 保存金额折扣活动
	 * @param conn 本方法不处理事务
	 * @param event 活动对象
	 * @throws Exception 
	 */
	public void saveEvent(Connection conn, Event event) throws Exception {
		PreparedStatement ps = null;
		try {
			//保存活动
			ps = conn.prepareStatement("insert into event(type,start_time,end_time,rule_desc,use_status,id) values(?,?,?,?,?,?)");
			ps.setInt(1, event.getType());
			ps.setTimestamp(2, event.getStartTime());
			ps.setTimestamp(3, event.getEndTime());
			ps.setString(4, event.getRuleDesc());
			ps.setInt(5, event.getUseStatus());
			ps.setInt(6, event.getId());
			ps.executeUpdate();
			
			//保存金额折扣
			ps = conn.prepareStatement("insert into money_discount_event(event_id,money,type,number_value,id) values(?,?,?,?,?)");
			for(MoneyDiscountEvent moneyDiscountEvent : event.getMoneyDiscountEventList()) {
				ps.setInt(1, event.getId());
				ps.setDouble(2, moneyDiscountEvent.getMoney());
				ps.setInt(3, moneyDiscountEvent.getType());
				ps.setDouble(4, moneyDiscountEvent.getNumberValue());
				ps.setInt(5, moneyDiscountEvent.getId());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (Exception e) {
			log.error("保存金额折扣活动时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(null, ps, null);
		}
	}
	
	/**
	 * 获取活动的详细信息
	 * @param eventId 活动id
	 * @return
	 */
	public Event getDetailEvent(int eventId) {
		Event event = null;
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 获取活动信息
			ps = conn.prepareStatement("select e.id,e.type,e.start_time,e.end_time,e.rule_desc,e.use_status from event e where e.id=?");
			ps.setInt(1, eventId);
			rs = ps.executeQuery();
			if (rs.next()) {
				event = new Event();
				event.setId(rs.getInt("id"));
				event.setType(rs.getInt("type"));
				event.setStartTime(rs.getTimestamp("start_time"));
				event.setEndTime(rs.getTimestamp("end_time"));
				event.setRuleDesc(rs.getString("rule_desc"));
				event.setUseStatus(rs.getInt("use_status"));
			}

			//获取金额折扣信息
			ps = conn.prepareStatement("select b.id,b.event_id,b.money,b.type,b.number_value from money_discount_event b where b.event_id=? order by b.money");
			ps.setInt(1, eventId);
			rs = ps.executeQuery();
			List<MoneyDiscountEvent> moneyDiscountEventList = new ArrayList<MoneyDiscountEvent>();
			MoneyDiscountEvent moneyDiscountEvent = null;
			while (rs.next()) {
				moneyDiscountEvent = new MoneyDiscountEvent();
				moneyDiscountEvent.setId(rs.getInt("id"));
				moneyDiscountEvent.setEventId(rs.getInt("event_id"));
				moneyDiscountEvent.setMoney(rs.getDouble("money"));
				moneyDiscountEvent.setType(rs.getInt("type"));
				moneyDiscountEvent.setNumberValue(rs.getDouble("number_value"));
				moneyDiscountEventList.add(moneyDiscountEvent);
			}
			event.setMoneyDiscountEventList(moneyDiscountEventList);
		} catch (Exception e) {
			log.error("获取活动的详细信息时出现异常：", e);
		} finally {
			DbUtil.closeConnection(rs, ps, conn);
		}
		return event;
	}
	
	/**
	 * 删除活动
	 * @param conn 本方法不处理事务
	 * @param eventId 活动id
	 * @throws Exception 
	 */
	public void deleteEvent(Connection conn, int eventId) throws Exception {
		PreparedStatement ps = null;
		try {
			//删除金额折扣活动
			ps = conn.prepareStatement("delete from money_discount_event where event_id=?");
			ps.setInt(1, eventId);
			ps.executeUpdate();
			
			//删除活动
			ps = conn.prepareStatement("delete from event where id=?");
			ps.setInt(1, eventId);
			ps.executeUpdate();
		} catch (Exception e) {
			log.error("删除活动时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(null, ps, null);
		}
	}
	
}
