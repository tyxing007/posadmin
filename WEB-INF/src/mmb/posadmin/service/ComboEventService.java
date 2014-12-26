package mmb.posadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mmb.posadmin.domain.ComboEvent;
import mmb.posadmin.domain.ComboProduct;
import mmb.posadmin.domain.Event;
import mmb.posadmin.domain.Product;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;

import org.apache.log4j.Logger;

public class ComboEventService extends BaseService {
	
	private static Logger log = Logger.getLogger(ComboEventService.class);
	
	/**
	 * 保存套餐活动
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
			
			//保存套餐
			ps = conn.prepareStatement("insert into combo_event(event_id,combo_price,id) values(?,?,?)");
			for(ComboEvent comboEvent : event.getComboEventList()) {
				ps.setInt(1, event.getId());
				ps.setDouble(2, comboEvent.getComboPrice());
				ps.setInt(3, comboEvent.getId());
				ps.addBatch();
			}
			ps.executeBatch();
			
        	//保存赠品
			ps = conn.prepareStatement("insert into combo_product(combo_event_id,product_id,product_count,id) values(?,?,?,?)");
			for(ComboEvent comboEvent : event.getComboEventList()) {
				for(ComboProduct product : comboEvent.getComboProductList()) {
					ps.setInt(1, comboEvent.getId());
					ps.setInt(2, product.getProductId());
					ps.setInt(3, product.getProductCount());
					ps.setInt(4, product.getId());
					ps.addBatch();
				}
			}
			ps.executeBatch();
		} catch (Exception e) {
			log.error("保存套餐活动时出现异常：", e);
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

			//获取套餐信息
			ps = conn.prepareStatement("select b.id,b.event_id,b.combo_price from combo_event b where b.event_id=?");
			ps.setInt(1, eventId);
			rs = ps.executeQuery();
			List<ComboEvent> comboEventList = new ArrayList<ComboEvent>();
			ComboEvent comboEvent = null;
			while (rs.next()) {
				comboEvent = new ComboEvent();
				comboEvent.setId(rs.getInt("id"));
				comboEvent.setEventId(rs.getInt("event_id"));
				comboEvent.setComboPrice(rs.getDouble("combo_price"));
				comboEventList.add(comboEvent);
			}
			event.setComboEventList(comboEventList);
			
			//获取套餐商品
			StringBuilder sql = new StringBuilder();
			sql.append("select cp.id,cp.combo_event_id,cp.product_id,cp.product_count,p.name ");
			sql.append(" from combo_product cp join product p on p.id=cp.product_id where cp.combo_event_id in(");
			for(ComboEvent cEvent : comboEventList) {
				sql.append(cEvent.getId()).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(")");
			sql.append(" order by cp.combo_event_id");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			ComboProduct comboProduct = null;
			while (rs.next()) {
				comboProduct = new ComboProduct();
				comboProduct.setId(rs.getInt("id"));
				comboProduct.setComboEventId(rs.getInt("combo_event_id"));
				comboProduct.setProductCount(rs.getInt("product_count"));
				Product p = new Product();
				p.setId(rs.getInt("product_id"));
				p.setName(rs.getString("name"));
				comboProduct.setProductId(p.getId());
				comboProduct.setProduct(p);
				
				for(ComboEvent cEvent : comboEventList) {
					if(cEvent.getId() == comboProduct.getComboEventId()) {
						cEvent.getComboProductList().add(comboProduct);
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
			//获取套餐信息
			ps = conn.prepareStatement("select b.id from combo_event b where b.event_id=?");
			ps.setInt(1, eventId);
			rs = ps.executeQuery();
			List<Integer> comboEventIdList = new ArrayList<Integer>();
			while (rs.next()) {
				comboEventIdList.add(rs.getInt("id"));
			}
			
			//删除套餐商品
			StringBuilder sql = new StringBuilder();
			sql.append("delete from combo_product where combo_event_id in(");
			for(Integer cEventId : comboEventIdList) {
				sql.append(cEventId).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(")");
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
			//删除套餐
			ps = conn.prepareStatement("delete from combo_event where event_id=?");
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

}
