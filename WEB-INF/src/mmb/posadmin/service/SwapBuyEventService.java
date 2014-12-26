package mmb.posadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mmb.posadmin.domain.Event;
import mmb.posadmin.domain.SwapBuyEvent;
import mmb.posadmin.domain.SwapBuyProduct;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;

import org.apache.log4j.Logger;

public class SwapBuyEventService extends BaseService {

	private static Logger log = Logger.getLogger(SwapBuyEventService.class);
	
	/**
	 * 保存换购活动信息
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

			//保存换购活动信息
			ps = conn.prepareStatement("insert into swap_buy_event(event_id,money,append_money,id) values(?,?,?,?)");
			for(SwapBuyEvent sbe : event.getSwapBuyEventList()){
				ps.setInt(1, sbe.getEventId());
				ps.setDouble(2, sbe.getMoney());
				ps.setDouble(3, sbe.getAppendMoney());
				ps.setInt(4, sbe.getId());
				ps.addBatch();
			}
			ps.executeBatch();
			
			//保存换购商品信息表
			ps = conn.prepareStatement("insert into swap_buy_product(swap_buy_event_id,gift_product_id,id) values(?,?,?)");
			for(SwapBuyEvent sbe : event.getSwapBuyEventList()){
				for(SwapBuyProduct sbp : sbe.getSwapBuyProductList() ){
					ps.setInt(1, sbe.getId());
					ps.setInt(2, sbp.getGiftProductId());
					ps.setInt(3, sbp.getId());
					ps.addBatch();
				}
			}
			ps.executeBatch();
		}catch(Exception e){
			log.error("保存换购活动信息时异常：", e);
			throw e;
		}finally{
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

			//获取换购活动信息
			ps = conn.prepareStatement("select sbe.id,sbe.event_id,sbe.money,sbe.append_money from swap_buy_event sbe WHERE sbe.event_id = ?");
			ps.setInt(1, eventId);
			rs = ps.executeQuery();
			List<SwapBuyEvent> swapBuyEventList = new ArrayList<SwapBuyEvent>();
			SwapBuyEvent swapBuyEvent = null;
			StringBuilder swapBuyEventIds = new StringBuilder();
			while (rs.next()) {
				swapBuyEvent = new SwapBuyEvent();
				swapBuyEventIds.append(rs.getInt("id")).append(",");
				swapBuyEvent.setId(rs.getInt("id"));
				swapBuyEvent.setEventId(rs.getInt("event_id"));
				swapBuyEvent.setMoney(rs.getDouble("money"));
				swapBuyEvent.setAppendMoney(rs.getDouble("append_money"));
				List<SwapBuyProduct> temp = new ArrayList<SwapBuyProduct>();
				swapBuyEvent.setSwapBuyProductList(temp);
				swapBuyEventList.add(swapBuyEvent);
			}
			
			if(swapBuyEventIds.length() != 0){
				swapBuyEventIds.deleteCharAt(swapBuyEventIds.length()-1);
				
				// 查询换购商品
				ps = conn.prepareStatement("select sbp.id,sbp.swap_buy_event_id,sbp.gift_product_id,p.`name` from swap_buy_product sbp LEFT JOIN product p on sbp.gift_product_id = p.id WHERE sbp.swap_buy_event_id in ( "+swapBuyEventIds.toString()+" )");
				rs = ps.executeQuery();
				SwapBuyProduct swapBuyProduct = null;
				List<SwapBuyProduct>  allSbpList = new ArrayList<SwapBuyProduct>();
				while(rs.next()){
					swapBuyProduct = new SwapBuyProduct();
					swapBuyProduct.setId(rs.getInt("id"));
					swapBuyProduct.setGiftProductId(rs.getInt("gift_product_id"));
					swapBuyProduct.setGiftProductName(rs.getString("name"));
					swapBuyProduct.setSwapBuyEventId(rs.getInt("swap_buy_event_id"));
					allSbpList.add(swapBuyProduct);
				}
				
				//将换购商品信息拆分入对象
				for(SwapBuyEvent tempSbe : swapBuyEventList){
					for(SwapBuyProduct sbp : allSbpList){
						if(tempSbe.getId() == sbp.getSwapBuyEventId()){
							tempSbe.getSwapBuyProductList().add(sbp);
						}
					}
				}
			}
			event.setSwapBuyEventList(swapBuyEventList);
		} catch (Exception e) {
			log.error("获取换购活动的详细信息时出现异常：", e);
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
			//获取换购活动信息
			ps = conn.prepareStatement("select s.id from swap_buy_event s where s.event_id = ?");
			ps.setInt(1, eventId);
			rs = ps.executeQuery();
			List<Integer> swapBuyEventIdList = new ArrayList<Integer>();
			while (rs.next()) {
				swapBuyEventIdList.add(rs.getInt("id"));
			}
			
			//删除换购商品
			StringBuilder sql = new StringBuilder();
			sql.append("delete from swap_buy_product where swap_buy_event_id in(");
			for(Integer gEventId : swapBuyEventIdList) {
				sql.append(gEventId).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(")");
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
			//删除换购信息
			ps = conn.prepareStatement("delete from swap_buy_event where event_id=?");
			ps.setInt(1, eventId);
			ps.executeUpdate();
			
			//删除活动
			ps = conn.prepareStatement("delete from event where id = ?");
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
