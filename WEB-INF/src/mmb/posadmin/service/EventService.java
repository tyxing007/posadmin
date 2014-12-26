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
import mmb.posadmin.domain.BuyGiftEvent;
import mmb.posadmin.domain.BuyGiftProduct;
import mmb.posadmin.domain.ComboEvent;
import mmb.posadmin.domain.ComboProduct;
import mmb.posadmin.domain.CountDiscountEvent;
import mmb.posadmin.domain.Event;
import mmb.posadmin.domain.MoneyDiscountEvent;
import mmb.posadmin.domain.SwapBuyEvent;
import mmb.posadmin.domain.SwapBuyProduct;
import mmb.posadmin.domain.SystemConfig;
import mmb.posadmin.util.HttpURLUtil;
import mmboa.util.Constants;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class EventService extends BaseService {
	
	private static Logger log = Logger.getLogger(EventService.class);
	
	/**
	 * 分页获取活动列表信息
	 * @param page 分页信息
	 * @param param [event:活动对象]
	 * @return
	 */
	public Page<Event> getEventPage(Page<Event> page, Map<String, Object> param) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			//查询条件
			StringBuilder condSql = new StringBuilder();
			Event event = (Event) param.get("event");
			if(event.getUseStatus() != 0) {
				condSql.append(" and e.use_status=? ");
			}
			if(event.getType() != 0){
				condSql.append(" and e.type = ? ");
			}
			if(event.getStartTime() != null && event.getEndTime() != null ){
				condSql.append(" and ( e.start_time <= ? and e.end_time  >=  ? ) or ( e.start_time >= ? and e.end_time >= ? ) or ( e.start_time <= ? and e.end_time <= ? ) or ( e.start_time > =? and e.end_time <= ? )");
			}
			if(event.getStartTime() != null && event.getEndTime() == null ){
				condSql.append(" and e.end_time  >=  ? ");
			}
			if(event.getEndTime() != null && event.getStartTime() == null ){
				condSql.append(" and e.end_time  <=  ? ");
			}
       
			//查询总记录数
			int index = 1;
			ps = conn.prepareStatement("select count(e.id) from event e where 1=1 " + condSql);
			if(event.getUseStatus() != 0) {
				ps.setInt(index++, event.getUseStatus());
			}
			if(event.getType() != 0){
				ps.setInt(index++, event.getType());
			}
			if(event.getStartTime() != null && event.getEndTime() != null ){
				ps.setTimestamp(index++, event.getStartTime());
				ps.setTimestamp(index++, event.getEndTime());
				ps.setTimestamp(index++, event.getStartTime());
				ps.setTimestamp(index++, event.getEndTime());
				ps.setTimestamp(index++, event.getStartTime());
				ps.setTimestamp(index++, event.getEndTime());
				ps.setTimestamp(index++, event.getStartTime());
				ps.setTimestamp(index++, event.getEndTime());
			}
			if(event.getStartTime() != null && event.getEndTime() == null ){
				ps.setTimestamp(index++, event.getStartTime());
			}
			if(event.getEndTime() != null && event.getStartTime() == null ){
				ps.setTimestamp(index++, event.getEndTime());
			}
			rs = ps.executeQuery();
		    if(rs.next()){
		    	page.setTotalRecords(rs.getInt(1));
		    }
		    
		    //获取列表数据
		    if(page.getTotalRecords() > 0) {
		    	List<Event> list = new ArrayList<Event>();
		    	Event e;
		    	StringBuilder sql = new StringBuilder(50);
		    	sql.append("SELECT e.id,e.type,e.start_time,e.end_time,e.rule_desc,e.use_status from event e");
		    	sql.append(" where 1=1 ").append(condSql);
		    	sql.append(" order by e.id desc limit ");
		    	sql.append(page.getFirstResult()).append(",").append(page.getPageCount());
		    	ps = conn.prepareStatement(sql.toString());
		    	index = 1;
		    	if(event.getUseStatus() != 0) {
					ps.setInt(index++, event.getUseStatus());
				}
		    	if(event.getType() != 0){
					ps.setInt(index++, event.getType());
				}
		    	if(event.getStartTime() != null && event.getEndTime() != null ){
					ps.setTimestamp(index++, event.getStartTime());
					ps.setTimestamp(index++, event.getEndTime());
					ps.setTimestamp(index++, event.getStartTime());
					ps.setTimestamp(index++, event.getEndTime());
					ps.setTimestamp(index++, event.getStartTime());
					ps.setTimestamp(index++, event.getEndTime());
					ps.setTimestamp(index++, event.getStartTime());
					ps.setTimestamp(index++, event.getEndTime());
				}
				if(event.getStartTime() != null && event.getEndTime() == null ){
					ps.setTimestamp(index++, event.getStartTime());
				}
				if(event.getEndTime() != null && event.getStartTime() == null ){
					ps.setTimestamp(index++, event.getEndTime());
				}
		    	rs = ps.executeQuery();
		    	while(rs.next()){
		    		e = new Event();
		    		e.setId(rs.getInt("id"));
		    		e.setType(rs.getInt("type"));
		    		e.setStartTime(rs.getTimestamp("start_time"));
		    		e.setEndTime(rs.getTimestamp("end_time"));
		    		e.setRuleDesc(rs.getString("rule_desc"));
		    		e.setUseStatus(rs.getInt("use_status"));
		    		list.add(e);
		    	}
		    	page.setList(list);
		    }
		}catch(Exception e){
			log.error("分页获取活动列表信息时出现异常：", e);
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return page;
	}

	/**
	 * 从中心库同步活动信息
	 * @throws Exception 
	 */
	public void syncEvent() throws Exception {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			//获取所有过期活动的id(过期的活动不再同步)
			StringBuilder content = new StringBuilder();
			content.append("{\"shopCode\":\""+SystemConfig.getInstance().getShopCode()+"\",\"eventIds\":\"");
			ps = conn.prepareStatement("select id from `event` where use_status=3");
			rs = ps.executeQuery();
	    	while(rs.next()){
	    		if(!rs.isFirst()){
	    			content.append(",");
	    		}
	    		content.append(rs.getInt("id"));
	    	}
	    	content.append("\"}");
	    	log.info("submit data=="+content);
	    	
			//发送请求并获取返回结果
			String syncPoscenterEventURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("syncPoscenterEventURL");
			String json = HttpURLUtil.getResponseResult(syncPoscenterEventURL, content.toString());
			log.info("back data=="+json);
			
			//解析JSON数据
			List<Event> eventList = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(json, new TypeToken<List<Event>>(){}.getType());
			
			//保存从中心库同步的活动列表数据
			if(!eventList.isEmpty()) {
				this.saveEvent(eventList);
			}
		}catch(Exception e){
			log.error("从中心库同步活动信息时出现异常：", e);
			throw e;
		}finally{
			DbUtil.closeConnection(null, ps, conn);
		}
	}
	
	/**
	 * 保存从中心库同步的活动列表数据
	 * @param eventList 活动列表
	 * @throws Exception 
	 */
	private void saveEvent(List<Event> eventList) throws Exception {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean oldAutoCommit = true;
		try {
			//记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			//关闭自动提交事务
			conn.setAutoCommit(false);
			
			BuyGiftEventService es1 = new BuyGiftEventService();
	    	SwapBuyEventService es2 = new SwapBuyEventService();
	    	MoneyDiscountEventService es3 = new MoneyDiscountEventService();
	    	CountDiscountEventService es4 = new CountDiscountEventService();
	    	ComboEventService es5 = new ComboEventService();
	    	for(Event e : eventList) {
	    		if(e.getType() == 1) {
	    			List<BuyGiftEvent> userLimitBuyGiftEventList = es1.getUsedCount(conn, e.getId());
	    			if(this.isExistEvent(conn, e.getId())) {
	    				es1.deleteEvent(conn, e.getId());
	    			}
	    			//处理有人数限制的买赠活动
	    			for(BuyGiftEvent buyGiftEvent : e.getBuyGiftEventList()) {
	    				for(BuyGiftEvent userLimitBuyGiftEvent : userLimitBuyGiftEventList) {
		    				if(buyGiftEvent.getId()==userLimitBuyGiftEvent.getId() && buyGiftEvent.getProductId()==userLimitBuyGiftEvent.getProductId()) {
		    					buyGiftEvent.setRemainCount(userLimitBuyGiftEvent.getRemainCount());
		    				}
		    			}
	    			}
	    			es1.saveEvent(conn, e);
	    		}else if(e.getType() == 2){
	    			if(this.isExistEvent(conn, e.getId())) {
	    				es2.deleteEvent(conn, e.getId());
	    			}
	    			es2.saveEvent(conn, e);
	    		}else if(e.getType() == 3){
	    			if(this.isExistEvent(conn, e.getId())) {
	    				es3.deleteEvent(conn, e.getId());
	    			}
	    			es3.saveEvent(conn, e);
	    		}else if(e.getType() == 4){
	    			if(this.isExistEvent(conn, e.getId())) {
	    				es4.deleteEvent(conn, e.getId());
	    			}
	    			es4.saveEvent(conn, e);
	    		}else if(e.getType() == 5){
	    			if(this.isExistEvent(conn, e.getId())) {
	    				es5.deleteEvent(conn, e.getId());
	    			}
	    			es5.saveEvent(conn, e);
	    		}
	    	}
        	conn.commit();
		} catch (Exception e) {
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("保存从中心库同步的活动列表数据时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(rs, ps, conn, oldAutoCommit);
		}
	}
	
	/**
	 * 判断该活动是否存在
	 * @param conn 本方法不处理事务
	 * @param eventId 活动id
	 * @throws Exception 
	 */
	private boolean isExistEvent(Connection conn, int eventId) throws Exception {
		boolean isExist = true;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("select id from event where id=?");
			ps.setInt(1, eventId);
			rs = ps.executeQuery();
			isExist = rs.next();
		} catch (Exception e) {
			log.error("判断该活动是否存在时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(rs, ps, null);
		}
		return isExist;
	}
	
	/**
	 * 获取可用的活动列表数据
	 * @return
	 * @throws Exception 
	 */
	public List<Event> getUseableEventList() throws Exception {
		List<Event> eventList = new ArrayList<Event>();
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			//查询需要同步的活动
			List<Event> simpleEventList = new ArrayList<Event>();
			ps = conn.prepareStatement("SELECT e.id,e.type from `event` e where e.use_status=2 and e.start_time<? and e.end_time>?");
			Timestamp now = new Timestamp(new Date().getTime());
			ps.setTimestamp(1, now);
			ps.setTimestamp(2, now);
			rs = ps.executeQuery();
			Event event = null;
	    	while(rs.next()) {
	    		event = new Event();
	    		event.setId(rs.getInt("id"));
	    		event.setType(rs.getInt("type"));
	    		simpleEventList.add(event);
	    	}
	    	
	    	//获取所有活动的详细信息
	    	BuyGiftEventService es1 = new BuyGiftEventService();
	    	SwapBuyEventService es2 = new SwapBuyEventService();
	    	MoneyDiscountEventService es3 = new MoneyDiscountEventService();
	    	CountDiscountEventService es4 = new CountDiscountEventService();
	    	ComboEventService es5 = new ComboEventService();
	    	for(Event e : simpleEventList) {
	    		if(e.getType() == 1) {
	    			e = es1.getDetailEvent(e.getId());
	    		}else if(e.getType() == 2){
	    			e = es2.getDetailEvent(e.getId());
	    		}else if(e.getType() == 3){
	    			e = es3.getDetailEvent(e.getId());
	    		}else if(e.getType() == 4){
	    			e = es4.getDetailEvent(e.getId());
	    		}else if(e.getType() == 5){
	    			e = es5.getDetailEvent(e.getId());
	    		}
	    		eventList.add(e);
	    	}
		}catch(Exception e){
			log.error("获取可用的活动列表数据时出现异常：", e);
			throw e;
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return eventList;
	}

	/**
	 * POS机获取活动列表数据
	 * @return
	 */
	public String posGetEventList() {
		StringBuilder result = new StringBuilder();
		try {
			//获取可用的活动列表数据
			List<Event> allEventList = this.getUseableEventList();
			
			//存放单对象
			List<Event> eventList = new ArrayList<Event>();
			List<BuyGiftEvent> buyGiftEventList = new ArrayList<BuyGiftEvent>();
			List<BuyGiftProduct> buyGiftProductList = new ArrayList<BuyGiftProduct>();
			List<SwapBuyEvent> swapBuyEventList = new ArrayList<SwapBuyEvent>();
			List<SwapBuyProduct> swapBuyProductList = new ArrayList<SwapBuyProduct>();
			List<ComboEvent> comboEventList = new ArrayList<ComboEvent>();
			List<ComboProduct> comboProductList = new ArrayList<ComboProduct>();
			List<CountDiscountEvent> countDiscountEventList = new ArrayList<CountDiscountEvent>();
			List<MoneyDiscountEvent> moneyDiscountEventList = new ArrayList<MoneyDiscountEvent>();
			
			for(Event event : allEventList) {
				//买赠
				for(BuyGiftEvent buyGiftEvent : event.getBuyGiftEventList()) {
					for(BuyGiftProduct buyGiftProduct : buyGiftEvent.getGiftList()) {
						buyGiftProduct.setGiftProduct(null);
						buyGiftProductList.add(buyGiftProduct);
					}
					buyGiftEvent.setProduct(null);
					buyGiftEvent.setGiftList(null);
					buyGiftEventList.add(buyGiftEvent);
				}
				//换购
				for(SwapBuyEvent swapBuyEvent : event.getSwapBuyEventList()) {
					for(SwapBuyProduct swapBuyProduct : swapBuyEvent.getSwapBuyProductList()) {
						swapBuyProduct.setGiftProductName(null);
						swapBuyProductList.add(swapBuyProduct);
					}
					swapBuyEvent.setSwapBuyProductList(null);
					swapBuyEventList.add(swapBuyEvent);
				}
				//套餐
				for(ComboEvent comboEvent : event.getComboEventList()) {
					for(ComboProduct comboProduct : comboEvent.getComboProductList()) {
						comboProduct.setProduct(null);
						comboProductList.add(comboProduct);
					}
					comboEvent.setComboProductList(null);
					comboEventList.add(comboEvent);
				}
				//数量折扣
				for(CountDiscountEvent countDiscountEvent : event.getCountDiscountEventList()) {
					countDiscountEvent.setProduct(null);
					countDiscountEventList.add(countDiscountEvent);
				}
				//金额折扣
				moneyDiscountEventList.addAll(event.getMoneyDiscountEventList());
				
				event.setBuyGiftEventList(null);
				event.setSwapBuyEventList(null);
				event.setComboEventList(null);
				event.setCountDiscountEventList(null);
				event.setMoneyDiscountEventList(null);
				eventList.add(event);
			}
			
			//拼装JSON数据
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			result.append("\"event\":").append(gson.toJson(eventList));
			result.append(",\"buyGiftEvent\":").append(gson.toJson(buyGiftEventList));
			result.append(",\"buyGiftProduct\":").append(gson.toJson(buyGiftProductList));
			result.append(",\"swapBuyEvent\":").append(gson.toJson(swapBuyEventList));
			result.append(",\"swapBuyProduct\":").append(gson.toJson(swapBuyProductList));
			result.append(",\"comboEvent\":").append(gson.toJson(comboEventList));
			result.append(",\"comboProduct\":").append(gson.toJson(comboProductList));
			result.append(",\"countDiscountEvent\":").append(gson.toJson(countDiscountEventList));
			result.append(",\"moneyDiscountEvent\":").append(gson.toJson(moneyDiscountEventList));
		} catch (Exception e) {
			log.error("POS机获取活动列表数据时出现异常：", e);
			result = new StringBuilder("{\"message\":\""+e+"\"}");
		}
		return result.toString();
	}

}
