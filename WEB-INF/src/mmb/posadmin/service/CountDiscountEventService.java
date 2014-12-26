package mmb.posadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mmb.posadmin.domain.CountDiscountEvent;
import mmb.posadmin.domain.Event;
import mmb.posadmin.domain.Product;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;

import org.apache.log4j.Logger;

public class CountDiscountEventService extends BaseService {
	
	private static Logger log = Logger.getLogger(CountDiscountEventService.class);
	
	/**
	 * 保存数量折扣活动
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
			
			//保存数量折扣
			ps = conn.prepareStatement("insert into count_discount_event(event_id,product_id,product_count,type,discount,id) values(?,?,?,?,?,?)");
			for(CountDiscountEvent countDiscountEvent : event.getCountDiscountEventList()) {
				ps.setInt(1, event.getId());
				ps.setInt(2, countDiscountEvent.getProductId());
				ps.setInt(3, countDiscountEvent.getProductCount());
				ps.setInt(4, countDiscountEvent.getType());
				ps.setDouble(5, countDiscountEvent.getDiscount());
				ps.setInt(6, countDiscountEvent.getId());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (Exception e) {
			log.error("保存数量折扣活动时出现异常：", e);
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

			//获取数量折扣信息
			ps = conn.prepareStatement("select b.id,b.event_id,b.product_id,b.product_count,b.type,b.discount,p.name from count_discount_event b join product p on p.id=b.product_id where b.event_id=?");
			ps.setInt(1, eventId);
			rs = ps.executeQuery();
			List<CountDiscountEvent> countDiscountEventList = new ArrayList<CountDiscountEvent>();
			CountDiscountEvent countDiscountEvent = null;
			while (rs.next()) {
				countDiscountEvent = new CountDiscountEvent();
				countDiscountEvent.setId(rs.getInt("id"));
				countDiscountEvent.setEventId(rs.getInt("event_id"));
				countDiscountEvent.setProductCount(rs.getInt("product_count"));
				countDiscountEvent.setType(rs.getInt("type"));
				countDiscountEvent.setDiscount(rs.getDouble("discount"));
				Product p = new Product();
				p.setId(rs.getInt("product_id"));
				p.setName(rs.getString("name"));
				countDiscountEvent.setProductId(p.getId());
				countDiscountEvent.setProduct(p);
				countDiscountEventList.add(countDiscountEvent);
			}
			event.setCountDiscountEventList(countDiscountEventList);
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
			//删除数量折扣活动
			ps = conn.prepareStatement("delete from count_discount_event where event_id=?");
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
