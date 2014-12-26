package mmb.posadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mmb.posadmin.action.Page;
import mmb.posadmin.domain.PriceCard;
import mmb.posadmin.domain.PriceCardCharge;
import mmb.posadmin.domain.SyncPriceCardInfo;
import mmb.posadmin.domain.SystemConfig;
import mmb.posadmin.util.HttpURLUtil;
import mmboa.util.Constants;
import mmboa.util.Secure;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;


public class PriceCardService extends BaseService {
	
	private static Logger log = Logger.getLogger(PriceCardService.class);

	/**
	 * 获取红蓝卡列表信息
	 * @param page 分页信息
	 * @return
	 */
	public Page<PriceCard> getPriceCardList(Page<PriceCard> page,Map<String,Object> param){
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			int state = (Integer)param.get("state");
			String priceCardId = (String)param.get("pricecardid");
			int type = (Integer)param.get("type");
			String clerkName = (String)param.get("clerkname");
			
			StringBuilder sb = new StringBuilder(100);
			sb.append("select count(1) from price_card pc where 1 = 1 ");
		
			if(state != 0 ){
				sb.append(" and pc.state = ? ");
			}
			if(type != 0 ){
				sb.append(" and pc.type = ? ");
			}
			if(StringUtils.isNotBlank(priceCardId)){
				sb.append(" and pc.id like ? ");
			}
			if(StringUtils.isNotBlank(clerkName)){
				sb.append(" and pc.clerk_name like  ? ");
			}
			
			ps = conn.prepareStatement(sb.toString());
			int index = 1 ;
			if(state != 0 ){
				ps.setInt(index++, state);
			}
			if(type != 0 ){
				ps.setInt(index++, type);
			}
			if(StringUtils.isNotBlank(priceCardId)){
				ps.setString(index++, "%"+priceCardId+"%");
			}
			if(StringUtils.isNotBlank(clerkName)){
				ps.setString(index++, "%"+clerkName+"%");
			}
			
			rs = ps.executeQuery();
			if(rs.next()){
			    	page.setTotalRecords(rs.getInt(1));
			}
			//如果没有记录，则直接返回
			if(page.getTotalRecords() <= 0) {
				return page;
			}
			
			List<PriceCard>  list = new ArrayList<PriceCard>();
			PriceCard priceCard = null;
			sb.delete(0, sb.length()-1);
			
			sb.append("select pc.id,pc.type,pc.point,pc.clerk_name,pc.open_time,pc.state,pc.type FROM price_card pc where 1 = 1 ");
			if(state != 0 ){
				sb.append(" and pc.state = ? ");
			}
			if(type != 0 ){
				sb.append(" and pc.type = ? ");
			}
			if(StringUtils.isNotBlank(priceCardId)){
				sb.append(" and pc.id like ? ");
			}
			if(StringUtils.isNotBlank(clerkName)){
				sb.append(" and pc.clerk_name like  ? ");
			}
			sb.append("order by id desc limit ");
			sb.append(page.getFirstResult()).append(",").append(page.getPageCount());
			ps = conn.prepareStatement(sb.toString());
			index = 1;
			if(state != 0 ){
				ps.setInt(index++, state);
			}
			if(type != 0 ){
				ps.setInt(index++, type);
			}
			if(StringUtils.isNotBlank(priceCardId)){
				ps.setString(index++, "%"+priceCardId+"%");
			}     
			if(StringUtils.isNotBlank(clerkName)){
				ps.setString(index++, "%"+clerkName+"%");
			}
			
			rs = ps.executeQuery();
			while(rs.next()){
				priceCard = new PriceCard();
				priceCard.setId(rs.getString("id"));
				priceCard.setClerkName(rs.getString("clerk_name"));
				priceCard.setOpenTime(rs.getTimestamp("open_time"));
				priceCard.setState(rs.getInt("state"));
				priceCard.setType(rs.getInt("type"));
				priceCard.setPoint(rs.getInt("point"));
			    list.add(priceCard);
			}
			page.setList(list);
		}catch(Exception e){
			log.error("获取红蓝卡分页信息出错！", e);
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return page;
  }

	/**
	 * 通过调价卡号获取调价卡对象
	 * @param priceCardId
	 * @return
	 */
	public PriceCard getPriceCardById(String priceCardId){
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		PriceCard priceCard = null ;
		try{
			ps = conn.prepareStatement("select pc.id,pc.password,pc.type,pc.point,pc.clerk_name,pc.open_time,pc.state,pc.type FROM price_card pc where pc.id = ?");
			ps.setString(1, priceCardId);
			rs = ps.executeQuery();
			if(rs.next()){
				priceCard = new PriceCard();
				priceCard.setId(rs.getString("id"));
				priceCard.setClerkName(rs.getString("clerk_name"));
				priceCard.setOpenTime(rs.getTimestamp("open_time"));
				priceCard.setState(rs.getInt("state"));
				priceCard.setType(rs.getInt("type"));
				priceCard.setPoint(rs.getInt("point"));
				priceCard.setPassword(rs.getString("password"));
			}
			
		}catch(Exception e){
			log.error("获取红蓝卡信息出错！", e);
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return priceCard;
	}

	/**
	 * 从中心库同步红蓝卡信息到本地数据库
	 * @throws Exception 
	 */
	public void syncPriceCardInfoFromCenter() throws Exception {
		Connection conn = DbUtil.getConnection();
		boolean autoCommit = false;
		try {
			
			 //设置事务提交方式
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			//发送请求并接收返回结果
			String syncPoscenterProductURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("syncPoscenterPriceCardInfoURL");
			String shopCode = SystemConfig.getInstance().getShopCode();
			StringBuilder requestJson = new StringBuilder("{\"shopCode\":\"");
			requestJson.append(shopCode).append("\"}");
			String json = HttpURLUtil.getResponseResult(syncPoscenterProductURL,requestJson.toString());
			
			Gson gson = new Gson();
		    gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			SyncPriceCardInfo spci = gson.fromJson(json,SyncPriceCardInfo.class);
			
		
		   
			//同步状态信息
			StringBuilder syncStatus = new StringBuilder();
			
			//获取中心库新增和更新的红蓝卡列表数据
			List<List<PriceCard>> list = getBatList(getAllPriceCardId(), spci.getPriceCardList());
			//批量插入和更新红蓝卡信息
			boolean syncCardInfo = this.batchInsertUpdate(list.get(0), list.get(1),conn);
			if(!syncCardInfo){
				syncStatus.append("同步红蓝卡信息异常");
				throw new Exception("同步红蓝卡信息异常");
			}
			
			//同步中心库红蓝卡充值记录
			Map<String,Double> points = new HashMap<String,Double>();
			boolean syncCharge = false;
			PriceCardChargeService pccs = new PriceCardChargeService();
			Set<String> priceCardIds = new HashSet<String>();
			 for(PriceCardCharge pcc : spci.getPriceCardChargeList()){
				 if(priceCardIds.contains(pcc.getPriceCardId())){
					 continue;
				 }else{
					 priceCardIds.add(pcc.getPriceCardId());
				 }
			 }
			if(priceCardIds.size() == 0 ){
				syncCharge = true;
			}else{
				points = getPriceCardPointMap(priceCardIds);
				for(PriceCardCharge pcc : spci.getPriceCardChargeList()){
					Double totalCash = points.get(pcc.priceCardId);
					//当同步的是新卡时   原先的Map里面不存在新卡的余额   设值为0
					if(totalCash == null){
						totalCash = 0.00;
					}
					pcc.setTotalCash(totalCash+pcc.consumeCash);
					points.put(pcc.getPriceCardId(), pcc.getTotalCash());
				}
				syncCharge = pccs.InsertChargeRecord(spci.getPriceCardChargeList(),points, conn);
			}
			
			if(!syncCharge){
				syncStatus.append("同步红蓝卡充值记录异常,");
				throw new Exception("同步红蓝卡信息异常");
			}
			
			if(syncCharge && syncCardInfo){
				syncStatus.append("success");
				//同步成功，通知中心库删除临时表信息
				StringBuilder syncDelTempjson = new StringBuilder();
				syncDelTempjson.append("{\"shopCode\":\"").append(shopCode).append("\",");
				syncDelTempjson.append("\"pricePointList\":[");
				if(points.keySet().size() > 0){
					for(String key : points.keySet()){
						syncDelTempjson.append("{\"").append("priceCardId").append("\":\"").append(key).append("\",");
						syncDelTempjson.append("\"").append("point").append("\":").append(points.get(key)).append("},");
					}
					syncDelTempjson.deleteCharAt(syncDelTempjson.length() - 1);
				}
				syncDelTempjson.append("],");
				syncDelTempjson.append("\"status\":\"").append(syncStatus).append("\"}");
				String syncPoscenterDelTempPriceReChargeURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("syncPoscenterDelTempPriceReChargeURL");
				String delStatus = HttpURLUtil.getResponseResult(syncPoscenterDelTempPriceReChargeURL,syncDelTempjson.toString());
                if("true".equals(delStatus)){
                	conn.commit();
                }else{
                	conn.rollback();
                	log.error("中心库删除临时表出错，请重新同步！");
                }
			}
		} catch (Exception e) {
			log.error("从中心库同步红蓝卡信息时出现异常：", e);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw e;
		}finally{
			DbUtil.closeConnection(null, null, conn, autoCommit);
		}
		
	}
	
	//获取制定卡号的红蓝卡余额Map信息
	public Map<String,Double> getPriceCardPointMap(Set<String> ids){
		   Map<String,Double> temp = new HashMap<String,Double>();
			Connection conn = DbUtil.getConnection();
			PreparedStatement ps = null;
			ResultSet rs = null;
			try{
				StringBuilder sb = new StringBuilder();
				sb.append("select pc.id,pc.point FROM price_card pc where pc.id in ( ");
				for(String id : ids){
					sb.append("'"+id+"'");
					sb.append(",");
				}
				sb.deleteCharAt(sb.length()-1);
				sb.append(" )");
				ps = conn.prepareStatement(sb.toString());
				rs = ps.executeQuery();
				while(rs.next()){
					temp.put(rs.getString("id"), rs.getDouble("point"));
				}
			}catch(Exception e){
				log.error("获取红蓝卡余额Map信息出错！", e);
			}finally{
				DbUtil.closeConnection(rs, ps, conn);
			}
		   return temp;
	}
	
	
	/**
	 * 更新中心库红蓝卡信息
	 * @param insertlist
	 * @param updatelist
	 * @param conn
	 * @return
	 */
	private boolean  batchInsertUpdate(List<PriceCard> insertlist, List<PriceCard> updatelist,Connection conn) {
		boolean success = true ;
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement("insert into price_card(id,clerk_name,open_time,type,state,supplier_id,password,point) values (?,?,?,?,?,?,?,?)");
			for(PriceCard pc : insertlist){
				ps.setString(1, pc.getId());
				ps.setString(2, pc.getClerkName());
				ps.setTimestamp(3, pc.getOpenTime());
				ps.setInt(4, pc.getType());
				ps.setInt(5, pc.getState());
				ps.setInt(6, pc.getSupplierId());
				ps.setString(7, pc.getPassword());
				ps.setDouble(8, pc.getPoint());
				ps.addBatch();
			}
			ps.executeBatch();
			
			ps = conn.prepareStatement("update price_card pc set pc.clerk_name = ? , pc.state = ? , pc.supplier_id = ? , pc.password = ?  where pc.id = ? and pc.type = ? ");
			int count = 0;
			for(PriceCard pc : updatelist){
				ps.setString(1, pc.getClerkName());
				ps.setInt(2, pc.getState());
				ps.setInt(3, pc.getSupplierId());
				ps.setString(4, pc.getPassword());
				ps.setString(5, pc.getId());
				ps.setInt(6, pc.getType());
				ps.addBatch();
				count++ ;
				if(count % 100 == 0 || count == updatelist.size()){
					ps.executeBatch();
				}
				
			}
			
		}catch(Exception e){
			log.error("更新和插入红蓝卡信息出错！", e);
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
	 * 获取本地所有的红蓝卡的id
	 * @return
	 */
	public List<String> getAllPriceCardId(){
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();
		try{
			ps = conn.prepareStatement("select pc.id from price_card pc ");
			rs = ps.executeQuery();
			while(rs.next()){
				String idstr = rs.getString("id");
				list.add(idstr);
			}
		}catch(Exception e){
			log.error("获取红蓝卡的卡号出错！",e);
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return list;
	}
	
	
	//返回list集合  0 ：是中心库新增的  1：中心库要修改的
	public  List<List<PriceCard>>  getBatList(List<String> localPriceCardId , List<PriceCard> center){
		List<List<PriceCard>> tmp = new ArrayList<List<PriceCard>>();
		List<PriceCard> list0 = new ArrayList<PriceCard>();
		List<PriceCard> list1 = new ArrayList<PriceCard>();
		Set<String> s1 = new HashSet<String>();
		Set<String> s2 = new HashSet<String>();
		//将中心库的数据id加入set集合
		for(int i = 0; i< center.size();i++){
			s1.add(center.get(i).getId());
		}
		   
		for(int i = 0; i< localPriceCardId.size();i++){
			   s2.add(localPriceCardId.get(i));
		}
		   //如果有新增红蓝卡数据，获取新增红蓝卡数据
		   if(s2.size() < s1.size()){
			   s1.removeAll(s2);
			   //将新增红蓝卡数据入list0
			   for(String j : s1){
				   for(int k = 0 ; k < center.size();k++){
					   if(center.get(k).id.equals(j)){
						   list0.add(center.get(k));
					   }
				   }
			   }
		   }   
			   //将原有红蓝卡数据入list1
			   for(String ss : s2){
				   for(int k = 0 ; k < center.size();k++){
					   if(center.get(k).id.equals(ss)){
						   list1.add(center.get(k));
					   }
				   }
			   }
		   tmp.add(list0);
		   tmp.add(list1);
		   return tmp;
	}

	/**
	 * POS机获取调价卡
	 * @param json POS机传递的JSON数据
	 * @return 操作结果JSON
	 */
	public String posGetPriceCard(String json) {
		String result = "";
		try {
			//解析JSON数据
			Gson gson = new Gson();
			PriceCard priceCard = gson.fromJson(json, PriceCard.class);
			
			//获取调价卡
			PriceCard myCard = this.getPriceCardById(priceCard.getId());
			
			//返回JSON字符串
			if(myCard != null) {
				if(myCard.getPassword().equals(Secure.encryptPwd(priceCard.getPassword()))) {
					result = gson.toJson(myCard);
				} else {
					result = "{\"message\":\"密码不正确\"}";
				}
			}
		} catch (Exception e) {
			log.error("POS机获取调价卡时出现异常：", e);
			result = "{\"message\":\"POS机获取调价卡时出现异常："+e+"\"}";
		}
		return result;
	}
	

}
