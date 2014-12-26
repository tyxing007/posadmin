package mmb.posadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mmb.posadmin.domain.BuyGiftEvent;
import mmb.posadmin.domain.BuyGiftProduct;
import mmb.posadmin.domain.Event;
import mmb.posadmin.domain.Product;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;

import org.apache.log4j.Logger;

public class BuyGiftEventService extends BaseService {
	
	private static Logger log = Logger.getLogger(BuyGiftEventService.class);
	
	/**
	 * 保存买赠活动
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
			
			//保存买赠活动
			ps = conn.prepareStatement("insert into buy_gift_event(event_id,product_id,product_count,user_limit,remain_count,id) values(?,?,?,?,?,?)");
			for(BuyGiftEvent buyGiftEvent : event.getBuyGiftEventList()) {
				ps.setInt(1, event.getId());
				ps.setInt(2, buyGiftEvent.getProductId());
				ps.setInt(3, buyGiftEvent.getProductCount());
				ps.setInt(4, buyGiftEvent.getUserLimit());
				ps.setInt(5, buyGiftEvent.getRemainCount());
				ps.setInt(6, buyGiftEvent.getId());
				ps.addBatch();
			}
			ps.executeBatch();
			
        	//保存赠品
			ps = conn.prepareStatement("insert into buy_gift_product(buy_gift_event_id,gift_product_id,max_gift_count,id) values(?,?,?,?)");
			for(BuyGiftEvent buyGiftEvent : event.getBuyGiftEventList()) {
				for(BuyGiftProduct gift : buyGiftEvent.getGiftList()) {
					ps.setInt(1, buyGiftEvent.getId());
					ps.setInt(2, gift.getGiftProductId());
					ps.setInt(3, gift.getMaxGiftCount());
					ps.setInt(4, gift.getId());
					ps.addBatch();
				}
			}
			ps.executeBatch();
		} catch (Exception e) {
			log.error("保存买赠活动时出现异常：", e);
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

			//获取促销商品信息
			ps = conn.prepareStatement("select b.id,b.product_id,b.product_count,b.user_limit,b.remain_count,p.name from buy_gift_event b join product p on p.id=b.product_id where b.event_id=?");
			ps.setInt(1, eventId);
			rs = ps.executeQuery();
			List<BuyGiftEvent> buyGiftEventList = new ArrayList<BuyGiftEvent>();
			BuyGiftEvent buyGiftEvent = null;
			while (rs.next()) {
				buyGiftEvent = new BuyGiftEvent();
				buyGiftEvent.setId(rs.getInt("id"));
				buyGiftEvent.setEventId(eventId);
				buyGiftEvent.setProductCount(rs.getInt("product_count"));
				buyGiftEvent.setUserLimit(rs.getInt("user_limit"));
				buyGiftEvent.setRemainCount(rs.getInt("remain_count"));
				Product p = new Product();
				p.setId(rs.getInt("product_id"));
				p.setName(rs.getString("name"));
				buyGiftEvent.setProductId(p.getId());
				buyGiftEvent.setProduct(p);
				buyGiftEventList.add(buyGiftEvent);
			}
			event.setBuyGiftEventList(buyGiftEventList);
			
			//获取赠品信息
			StringBuilder sql = new StringBuilder();
			sql.append("select g.id,g.buy_gift_event_id,g.gift_product_id,g.max_gift_count,p.name ");
			sql.append(" from buy_gift_product g join product p on p.id=g.gift_product_id where g.buy_gift_event_id in(");
			for(BuyGiftEvent gEvent : buyGiftEventList) {
				sql.append(gEvent.getId()).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(")");
			sql.append(" order by g.buy_gift_event_id");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			BuyGiftProduct gift = null;
			while (rs.next()) {
				gift = new BuyGiftProduct();
				gift.setId(rs.getInt("id"));
				gift.setBuyGiftEventId(rs.getInt("buy_gift_event_id"));
				gift.setMaxGiftCount(rs.getInt("max_gift_count"));
				Product p = new Product();
				p.setId(rs.getInt("gift_product_id"));
				p.setName(rs.getString("name"));
				gift.setGiftProductId(p.getId());
				gift.setGiftProduct(p);
				
				for(BuyGiftEvent gEvent : buyGiftEventList) {
					if(gEvent.getId() == gift.getBuyGiftEventId()) {
						gEvent.getGiftList().add(gift);
					}
				}
			}
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
		ResultSet rs = null;
		try {
			//获取促销商品信息
			ps = conn.prepareStatement("select b.id from buy_gift_event b where b.event_id=?");
			ps.setInt(1, eventId);
			rs = ps.executeQuery();
			List<Integer> buyGiftEventIdList = new ArrayList<Integer>();
			while (rs.next()) {
				buyGiftEventIdList.add(rs.getInt("id"));
			}
			
			//删除赠品
			StringBuilder sql = new StringBuilder();
			sql.append("delete from buy_gift_product where buy_gift_event_id in(");
			for(Integer gEventId : buyGiftEventIdList) {
				sql.append(gEventId).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(")");
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
			//删除促销商品
			ps = conn.prepareStatement("delete from buy_gift_event where event_id=?");
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
			DbUtil.closeConnection(rs, ps, null);
		}
	}

	/**
	 * 获取有人数限制的买赠活动的已用数量
	 * @param conn 本方法不处理事务
	 * @param eventId 活动id
	 * @return
	 */
	public List<BuyGiftEvent> getUsedCount(Connection conn, int eventId) {
		List<BuyGiftEvent> buyGiftEventList = new ArrayList<BuyGiftEvent>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			//获取促销商品信息
			ps = conn.prepareStatement("select b.id,b.product_id,b.user_limit,b.remain_count from buy_gift_event b where b.event_id=? and b.user_limit>0");
			ps.setInt(1, eventId);
			rs = ps.executeQuery();
			BuyGiftEvent buyGiftEvent = null;
			while (rs.next()) {
				buyGiftEvent = new BuyGiftEvent();
				buyGiftEvent.setId(rs.getInt("id"));
				buyGiftEvent.setProductId(rs.getInt("product_id"));
				buyGiftEvent.setUserLimit(rs.getInt("user_limit"));
				buyGiftEvent.setRemainCount(rs.getInt("remain_count"));
				buyGiftEventList.add(buyGiftEvent);
			}
		} catch (Exception e) {
			log.error("获取有人数限制的买赠活动的已用数量时出现异常：", e);
		} finally {
			DbUtil.closeConnection(rs, ps, null);
		}
		return buyGiftEventList;
	}

}
