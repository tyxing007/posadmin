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
import mmb.posadmin.domain.PriceCardCharge;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class PriceCardChargeService extends BaseService{
	private static Logger log = Logger.getLogger(PriceCardChargeService.class);
	
	//同步中心库插入红蓝卡充值记录
	public boolean InsertChargeRecord(List<PriceCardCharge> list,Map<String,Double> points,Connection conn){
	    boolean success = true;
	    PreparedStatement ps = null;
	    try{
	 
	    	ps = conn.prepareStatement("insert into price_card_charge(price_card_id,card_type,charge_time,consume_type,consume_cash,total_cash) values(?,?,?,?,?,?)");
	    	int count = 0;
	    	for(PriceCardCharge pcc : list){
	    		ps.setString(1, pcc.getPriceCardId());
	    		ps.setInt(2, pcc.getCardType());
	    		ps.setTimestamp(3,new Timestamp(new Date().getTime()));
	    		//中心库只有充值记录
	    		ps.setInt(4, 1);
	    		ps.setDouble(5, pcc.getConsumeCash());
	    		ps.setDouble(6, pcc.getTotalCash());
	    		ps.addBatch();
	    		count++;
	    		if( count % 100 == 0 || count == list.size() ){
	    			ps.executeBatch();
	    		}
	    	}
	    	
	    	ps = conn.prepareStatement("update price_card pc set pc.point = ? where pc.id = ?");
	    	for(String key : points.keySet()){
	    		ps.setDouble(1, points.get(key));
	    		ps.setString(2, key);
	    		ps.addBatch();
	    	}
	    	ps.executeBatch();
	    	
	    
	    }catch(Exception e){
	    	log.error("批量更新充值记录出错！", e);
	    	success = false;
	    }finally{
	    	try {
				ps.close();
			} catch (SQLException e) {
				log.error("关闭预处理出错！", e);
			}
	    }
		
		return success;
	}

	/**
	 * 批量保存红蓝卡收支明细记录
	 * @param conn 数据库连接对象，此方法中不处理事务
	 * @param inoutList 红蓝卡收支明细记录列表
	 * @throws SQLException 
	 */
	public void addBatPriceCardCharge(Connection conn, List<PriceCardCharge> inoutList) throws SQLException {
		PreparedStatement ps = null;
		try {
			String sql = "insert into price_card_charge(`price_card_id`,order_id,`charge_time`,`consume_type`,`consume_cash`,total_cash) values (?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			for (PriceCardCharge inout : inoutList) {
				ps.setString(1, inout.getPriceCardId());
				ps.setInt(2, inout.getOrderId());
				ps.setTimestamp(3, new Timestamp(new Date().getTime()));
				ps.setInt(4, inout.getConsumeType());
				ps.setDouble(5, inout.getConsumeCash());
				ps.setDouble(6, inout.getTotalCash());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			log.error("批量保存红蓝卡收支明细记录时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(null, ps, null);
		}
	}
	
	
	public Page<PriceCardCharge> getPriceCardChargeList(Page<PriceCardCharge> page,Map<String, Object> param) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			int consumeType = (Integer)param.get("consumetype");
			String chargeTime = (String)param.get("chargetime");
			String priceCardId = (String)param.get("pricecardid");
			
			StringBuilder sb = new StringBuilder(100);
			sb.append("select count(1) from price_card_charge pcc where 1 = 1 ");
		
			if(StringUtils.isNotBlank(priceCardId)){
				sb.append(" and pcc.price_card_id  =  ? ");
			}
			if(consumeType != 0 ){
				sb.append(" and pcc.consume_type  =  ? ");
			}
			if(StringUtils.isNotBlank(chargeTime)){
				sb.append(" and pcc.charge_time >=  ? ");
			}
			
			ps = conn.prepareStatement(sb.toString());
			int index = 1 ;
			if(StringUtils.isNotBlank(priceCardId)){
				ps.setString(index++, priceCardId);
			}
			if(consumeType != 0){
				ps.setInt(index++, consumeType);
			}
			if(StringUtils.isNotBlank(chargeTime)){
				ps.setString(index++, chargeTime);
			}
			
			rs = ps.executeQuery();
			if(rs.next()){
			    	page.setTotalRecords(rs.getInt(1));
			}
			//如果没有记录，则直接返回
			if(page.getTotalRecords() <= 0) {
				return page;
			}
			
			List<PriceCardCharge>  list = new ArrayList<PriceCardCharge>();
			PriceCardCharge priceCardCharge = null;
			sb.delete(0, sb.length()-1);
			
			sb.append("select pcc.id,pcc.price_card_id,pcc.order_id,pcc.card_type,pcc.charge_time,pcc.consume_type,pcc.consume_cash,pcc.total_cash FROM price_card_charge pcc  where 1 = 1 ");

			if(StringUtils.isNotBlank(priceCardId)){
				sb.append(" and pcc.price_card_id  =  ? ");
			}
			if(consumeType != 0 ){
				sb.append(" and pcc.consume_type  =  ? ");
			}
			if(StringUtils.isNotBlank(chargeTime)){
				sb.append(" and pcc.charge_time >=  ? ");
			}
			sb.append("order by id desc limit ");
			sb.append(page.getFirstResult()).append(",").append(page.getPageCount());
			ps = conn.prepareStatement(sb.toString());
			index = 1;
			if(StringUtils.isNotBlank(priceCardId)){
				ps.setString(index++, priceCardId);
			}
			if(consumeType != 0){
				ps.setInt(index++, consumeType);
			}
			if(StringUtils.isNotBlank(chargeTime)){
				ps.setString(index++, chargeTime);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				priceCardCharge = new PriceCardCharge();
				priceCardCharge.setId(rs.getInt("id"));
				priceCardCharge.setPriceCardId(rs.getString("price_card_id"));
				priceCardCharge.setTotalCash(rs.getDouble("total_cash"));
				priceCardCharge.setCardType(rs.getInt("card_type"));
				priceCardCharge.setChargeTime(rs.getTimestamp("charge_time"));
				priceCardCharge.setConsumeCash(rs.getDouble("consume_cash"));
				priceCardCharge.setConsumeType(rs.getInt("consume_type"));
			    list.add(priceCardCharge);
			}
			page.setList(list);
		}catch(Exception e){
			log.error("获取红蓝卡消费分页信息出错！", e);
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return page;

	}

}
