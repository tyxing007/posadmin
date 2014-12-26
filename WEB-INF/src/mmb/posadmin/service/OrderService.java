package mmb.posadmin.service;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mmb.posadmin.action.Page;
import mmb.posadmin.domain.BuyGiftEvent;
import mmb.posadmin.domain.CashStream;
import mmb.posadmin.domain.Invoice;
import mmb.posadmin.domain.LeaseOrder;
import mmb.posadmin.domain.LeaseOrderProduct;
import mmb.posadmin.domain.Member;
import mmb.posadmin.domain.MemberAccount;
import mmb.posadmin.domain.MemberAccountDetail;
import mmb.posadmin.domain.MemberLease;
import mmb.posadmin.domain.MemberScore;
import mmb.posadmin.domain.MoneyDestination;
import mmb.posadmin.domain.MoneySource;
import mmb.posadmin.domain.PriceCardCharge;
import mmb.posadmin.domain.Product;
import mmb.posadmin.domain.SaledOrder;
import mmb.posadmin.domain.SaledOrderEvent;
import mmb.posadmin.domain.SaledOrderProduct;
import mmb.posadmin.domain.SystemConfig;
import mmb.posadmin.util.HttpURLUtil;
import mmboa.util.Constants;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbOperation;
import mmboa.util.db.DbUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class OrderService extends BaseService {
	private static Logger log = Logger.getLogger(OrderService.class);
	
	/**
	 * 获取销售订单
	 * @param id 销售订单id
	 * @return
	 */
	public SaledOrder getSaledOrder(int id){
		return (SaledOrder)this.getXXX("`id`="+id, "saled_order", SaledOrder.class.getName());
	}
	
	/**
	 * 分页获取租赁订单信息
	 * @param page 分页信息
	 * @param param 查询参数[orderId:订单id；orderType:订单类型；memberName:会员姓名]
	 * @return
	 */
	public Page<LeaseOrder> getLeaseOrderPage(Page<LeaseOrder> page, Map<String, Object> param) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			//查询条件
			StringBuilder condSql = new StringBuilder();
			int orderId = (Integer)param.get("orderId");
			int orderType = (Integer)param.get("orderType");
			String memberName = (String)param.get("memberName");
			condSql.append(" and lo.order_type="+orderType);
			if(orderId != 0) {
				condSql.append(" and lo.id = ? ");
			}
			if(StringUtils.isNotBlank(memberName)) {
				condSql.append(" and m.name like ? ");
			}
			
			//查询总记录数
			int index = 1;
			ps = conn.prepareStatement("select count(lo.id) from lease_order lo left join member m on lo.member_id=m.id where 1=1 "+condSql);
			if(orderId != 0) {
				ps.setInt(index++, orderId);
			}
			if(StringUtils.isNotBlank(memberName)) {
				ps.setString(index++, "%"+memberName+"%");
			}
			rs = ps.executeQuery();
			if(rs.next()){
		    	page.setTotalRecords(rs.getInt(1));
		    }
			
			//获取列表数据
			if(page.getTotalRecords() > 0) {
				//获取每页显示的订单id列表
				List<Integer> orderIdList = new ArrayList<Integer>();
				StringBuilder sql = new StringBuilder();
				sql.append(" select lo.id orderId from lease_order lo left join member m on lo.member_id=m.id");
				sql.append(" where 1=1 ").append(condSql);
				sql.append(" order by lo.id desc ");
				sql.append(" limit "+page.getFirstResult()).append(",").append(page.getPageCount());
				ps = conn.prepareStatement(sql.toString());
		    	index = 1;
		    	if(orderId != 0) {
					ps.setInt(index++, orderId);
				}
		    	if(StringUtils.isNotBlank(memberName)) {
					ps.setString(index++, "%"+memberName+"%");
				}
		    	rs = ps.executeQuery();
				while(rs.next()){
					orderIdList.add(rs.getInt("orderId"));
				}
				
				//获取订单和订单商品信息
				List<LeaseOrder> leaseOrderList = this.getLeaseOrderList(orderIdList);
				page.setList(leaseOrderList);
			}
		}catch(Exception e){
			log.error("分页获取租赁订单信息时出现异常：", e);
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return page;
	}
	
	/**
	 * 根据订单ids获取租赁订单和订单商品详细信息
	 * @param orderIdList 订单id列表
	 * @return
	 */
	public List<LeaseOrder> getLeaseOrderList(List<Integer> orderIdList) {
		List<LeaseOrder> leaseOrderList = new ArrayList<LeaseOrder>();
		DbOperation db = new DbOperation();
		try{
			//查询sql
			StringBuilder sql = new StringBuilder();
			sql.append("select lo.id orderId,lo.serial_number,lo.member_id,m.`name` memberName,lo.price,lo.deposit,lo.order_type,lop.id,lop.count,lop.pre_price,lop.per_deposit,lop.start_time,lop.end_time,lop.time_length,lop.lease_style, lop.product_id,p.`name` productName");
			sql.append(" from lease_order lo");
			sql.append(" join lease_order_product lop on lo.id=lop.lease_order_id");
			sql.append(" join member m on lo.member_id=m.id");
			sql.append(" join product p on lop.product_id=p.id");
			sql.append(" where lo.id in (");
			for(Integer id : orderIdList) {
				sql.append(id).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(") ");
			sql.append(" order by lo.id desc");
			
			ResultSet rs = db.executeQuery(sql.toString());
			LeaseOrder leaseOrder = new LeaseOrder();
			int currentOrderId = -1;
			while(rs.next()){
				int orderId = rs.getInt("orderId");
				//新订单
				if(currentOrderId != orderId) {
					currentOrderId = orderId;
					//订单信息
					leaseOrder = new LeaseOrder();
					leaseOrder.setId(orderId);
					leaseOrder.setSerialNumber(rs.getString("serial_number"));
					leaseOrder.setMemberId(rs.getString("member_id"));
					leaseOrder.setOrderType(rs.getInt("order_type"));
					leaseOrder.setPrice(rs.getDouble("price"));
					leaseOrder.setDeposit(rs.getDouble("deposit"));
					//会员信息
					Member member = new Member();
					member.setName(rs.getString("memberName"));
					leaseOrder.setMember(member);
					leaseOrderList.add(leaseOrder);
				}
				//订单商品信息
				LeaseOrderProduct op = new LeaseOrderProduct();
				op.setId(rs.getInt("id"));
				op.setProductId(rs.getInt("product_id"));
				op.setCount(rs.getInt("count"));
				op.setPrePrice(rs.getDouble("pre_price"));
				op.setPerDeposit(rs.getDouble("per_deposit"));
				op.setStartTime(rs.getTimestamp("start_time"));
				op.setEndTime(rs.getTimestamp("end_time"));
				op.setTimeLength(rs.getDouble("time_length"));
				op.setLeaseStyle(rs.getInt("lease_style"));
				//商品信息
				Product product = new Product();
				product.setName(rs.getString("productName"));
				op.setProduct(product);
				leaseOrder.getProductList().add(op);
			}
		}catch(Exception e){
			log.error("根据订单ids获取租赁订单和订单商品详细信息时出现异常：", e);
		}finally{
			this.release(db);
		}
		return leaseOrderList;
	}
	
	/**
	 * 分页获取销售订单信息
	 * @param page 分页信息
	 * @param param 查询参数[orderId:订单id；orderType:订单类型；memberName:会员姓名；serialNumber:订单号；payMethod:支付方式；swipCardNumber:刷卡流水号]
	 * @return
	 */
	public Page<SaledOrder> getSaledOrderPage(Page<SaledOrder> page, Map<String, Object> param) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			//查询条件
			StringBuilder condSql = new StringBuilder();
			int orderId = (Integer)param.get("orderId");
			int orderType = (Integer)param.get("orderType");
			String serialNumber = (String)param.get("serialNumber");
			String memberName = (String)param.get("memberName");
			Integer payMethod = (Integer)param.get("payMethod");
			String swipCardNumber = (String)param.get("swipCardNumber");
			condSql.append(" where so.order_type="+orderType);
			if(orderId != 0) {
				condSql.append(" and so.id = ? ");
			}
			if(StringUtils.isNotBlank(serialNumber)) {
				condSql.append(" and so.serial_number like ? ");
			}
			if(StringUtils.isNotBlank(memberName)) {
				condSql.append(" and m.name like ? ");
			}
			
			//订单金钱来源明细查询条件
			StringBuilder msSql = new StringBuilder();
			if((payMethod!=null && payMethod!=0) || StringUtils.isNotBlank(swipCardNumber)) {
				msSql.append(" join money_source ms on so.id=ms.order_id");
				if(payMethod!=null && payMethod!=0) {
					msSql.append(" and ms.type = ? ");
				}
				if(StringUtils.isNotBlank(swipCardNumber)) {
					msSql.append(" and ms.swip_card_number like ? ");
				}
			}
			
			//查询总记录数
			int index = 1;
			ps = conn.prepareStatement("select count(so.id) from saled_order so left join member m on so.member_id=m.id "+msSql+condSql);
			if((payMethod!=null && payMethod!=0) || StringUtils.isNotBlank(swipCardNumber)) {
				if(payMethod!=null && payMethod!=0) {
					ps.setInt(index++, payMethod);
				}
				if(StringUtils.isNotBlank(swipCardNumber)) {
					ps.setString(index++, "%"+swipCardNumber+"%");
				}
			}
			if(orderId != 0) {
				ps.setInt(index++, orderId);
			}
			if(StringUtils.isNotBlank(serialNumber)) {
				ps.setString(index++, "%"+serialNumber+"%");
			}
			if(StringUtils.isNotBlank(memberName)) {
				ps.setString(index++, "%"+memberName+"%");
			}
			rs = ps.executeQuery();
			if(rs.next()){
		    	page.setTotalRecords(rs.getInt(1));
		    }
			
			//获取列表数据
			if(page.getTotalRecords() > 0) {
				//获取每页显示的订单id列表
				List<Integer> orderIdList = new ArrayList<Integer>();
				StringBuilder sql = new StringBuilder();
				sql.append(" select so.id orderId from saled_order so left join member m on so.member_id=m.id ");
				sql.append(msSql).append(condSql);
				sql.append(" order by so.id desc ");
				sql.append(" limit "+page.getFirstResult()).append(",").append(page.getPageCount());
				ps = conn.prepareStatement(sql.toString());
		    	index = 1;
		    	if((payMethod!=null && payMethod!=0) || StringUtils.isNotBlank(swipCardNumber)) {
					if(payMethod!=null && payMethod!=0) {
						ps.setInt(index++, payMethod);
					}
					if(StringUtils.isNotBlank(swipCardNumber)) {
						ps.setString(index++, "%"+swipCardNumber+"%");
					}
				}
		    	if(orderId != 0) {
					ps.setInt(index++, orderId);
				}
		    	if(StringUtils.isNotBlank(serialNumber)) {
					ps.setString(index++, "%"+serialNumber+"%");
				}
				if(StringUtils.isNotBlank(memberName)) {
					ps.setString(index++, "%"+memberName+"%");
				}
		    	rs = ps.executeQuery();
				while(rs.next()){
					orderIdList.add(rs.getInt("orderId"));
				}
				
				//获取订单和订单商品信息
				List<SaledOrder> orderList = this.getSaledOrderList(orderIdList);
				page.setList(orderList);
			}
		}catch(Exception e){
			log.error("分页获取销售订单信息时出现异常：", e);
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return page;
	}
	
	/**
	 * 根据订单ids获取销售订单和订单商品详细信息
	 * @param orderIdList 订单id列表
	 * @return
	 */
	public List<SaledOrder> getSaledOrderList(List<Integer> orderIdList) {
		List<SaledOrder> orderList = new ArrayList<SaledOrder>();
		if(orderIdList==null || orderIdList.isEmpty()) {
			return orderList;
		}
		
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			//查询sql
			StringBuilder sql = new StringBuilder();
			sql.append("select lo.id orderId,lo.serial_number,lo.member_id,m.`name` memberName,lo.price,lo.order_type,lo.saled_time,lop.id,lop.count,lop.pre_price,lop.event_remark,lop.product_id,p.`name` productName ");
			sql.append(" from saled_order lo");
			sql.append(" join saled_order_product lop on lo.id=lop.saled_order_id");
			sql.append(" left join member m on lo.member_id=m.id");
			sql.append(" join product p on lop.product_id=p.id");
			sql.append(" where lo.id in (");
			for(Integer id : orderIdList) {
				sql.append(id).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(") ");
			sql.append(" order by lo.id desc ");
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			SaledOrder saledOrder = new SaledOrder();
			int currentOrderId = -1;
			while(rs.next()){
				int orderId = rs.getInt("orderId");
				//新订单
				if(currentOrderId != orderId) {
					currentOrderId = orderId;
					//订单信息
					saledOrder = new SaledOrder();
					saledOrder.setId(orderId);
					saledOrder.setSerialNumber(rs.getString("serial_number"));
					saledOrder.setMemberId(rs.getString("member_id"));
					saledOrder.setOrderType(rs.getInt("order_type"));
					saledOrder.setPrice(rs.getDouble("price"));
					saledOrder.setSaledTime(rs.getTimestamp("saled_time"));
					//会员信息
					Member member = new Member();
					member.setName(rs.getString("memberName"));
					saledOrder.setMember(member);
					orderList.add(saledOrder);
				}
				//订单商品信息
				SaledOrderProduct op = new SaledOrderProduct();
				op.setId(rs.getInt("id"));
				op.setProductId(rs.getInt("product_id"));
				op.setCount(rs.getInt("count"));
				op.setPrePrice(rs.getDouble("pre_price"));
				op.setEventRemark(rs.getString("event_remark"));
				//商品信息
				Product product = new Product();
				product.setName(rs.getString("productName"));
				op.setProduct(product);
				saledOrder.getProductList().add(op);
			}
			
			//获取金钱来源的List
			sql = new StringBuilder();
			sql.append("select ms.money,ms.type,ms.order_id,ms.swip_card_number from money_source ms where ms.order_id in(");
			for(Integer id : orderIdList) {
				sql.append(id).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(") ");
			sql.append(" order by ms.type ");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			MoneySource ms = null;
			while(rs.next()){
				int orderId = rs.getInt("order_id");
				ms = new MoneySource();
				ms.setMoney(rs.getDouble("money"));
				ms.setOrderId(orderId);
				ms.setType(rs.getInt("type"));
				ms.setSwipCardNumber(rs.getString("swip_card_number"));
				for(SaledOrder order : orderList) {
					if(orderId == order.getId()) {
						order.getMoneySourceList().add(ms);
					}
				}
			}
			
			//获取金钱去向列表
			sql = new StringBuilder();
			sql.append("select md.id,md.order_id,md.type,md.money from money_destination md where md.order_id in(");
			for(Integer id : orderIdList) {
				sql.append(id).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(") ");
			sql.append(" order by md.type ");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			MoneyDestination md = null;
			while(rs.next()){
				int orderId = rs.getInt("order_id");
				md = new MoneyDestination();
				md.setId(rs.getInt("id"));
				md.setMoney(rs.getDouble("money"));
				md.setOrderId(orderId);
				md.setType(rs.getInt("type"));
				for(SaledOrder order : orderList) {
					if(orderId == order.getId()) {
						order.getMoneyDestinationList().add(md);
					}
				}
			}
			
			//获取活动信息
			Map<Integer,List<SaledOrderEvent>> tmpmap = new HashMap<Integer,List<SaledOrderEvent>>();
			sql = new StringBuilder();
			sql.append("select soe.id,soe.saled_order_id,soe.event_id,soe.detail_event_id,soe.ext_id from saled_order_event soe where soe.saled_order_id in(");
			for(Integer id : orderIdList) {
				sql.append(id).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(") ");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			List<SaledOrderEvent> tmplist = null;
			SaledOrderEvent tmpsoe = null;
			while(rs.next()){
				tmpsoe = new SaledOrderEvent();
				tmpsoe.setId(rs.getInt("id"));
				tmpsoe.setSaledOrderId(rs.getInt("saled_order_id"));
				tmpsoe.setEventId(rs.getInt("event_id"));
				tmpsoe.setDetailEventId(rs.getInt("detail_event_id"));
				tmpsoe.setExtId(rs.getInt("ext_id"));
				if(tmpmap.containsKey(rs.getInt("saled_order_id"))){
					tmpmap.get(rs.getInt("saled_order_id")).add(tmpsoe);
				}else{
					tmplist = new ArrayList<SaledOrderEvent>();
					tmplist.add(tmpsoe);
					tmpmap.put(rs.getInt("saled_order_id"), tmplist);
				}
			}
			for(SaledOrder order : orderList) {
			    order.setSaledOrderEventList(tmpmap.get(order.getId()));
			}
		}catch(Exception e){
			log.error("根据订单ids获取销售订单和订单商品详细信息时出现异常：", e);
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return orderList;
	}

	/**
	 * 解析json格式的租赁订单数据
	 * @param json
	 * @return 返回流水信息，temp_01 :0 代表订单流水信息 1--max代表订单商品信息 temp_02 代表会员租赁信息
	 */
	public List<ArrayList<Object>> doLeaseJson(String json) {
		List<ArrayList<Object>> temp = new ArrayList<ArrayList<Object>>();
	    //流水信息
		ArrayList<Object> temp_01 = new ArrayList<Object>();
		//会员租赁信息
		ArrayList<Object> temp_02 = new ArrayList<Object>();
		LeaseOrderProduct lop;
		LeaseOrder lo = new LeaseOrder();
		MemberLease ml;
		try {
			StringReader sr = new StringReader(json);
			JsonReader jr = new JsonReader(sr);
			jr.beginObject();
			String attrName = null;
			while (jr.hasNext()) {
				attrName = jr.nextName();
				if ("memberId".equals(attrName)) {
					lo.setMemberId(jr.nextString());
				} else if ("serialId".equals(attrName)) {
					lo.setSerialNumber(jr.nextString());
				} else if ("price".equals(attrName)) { //订单总金额
					lo.setPrice(jr.nextDouble());
				} else if ("deposit".equals(attrName)) { //订单总押金
					lo.setDeposit(jr.nextDouble());
				} else if ("cash".equals(attrName)) { //用户缴纳的现金
					lo.setCash(jr.nextDouble());
				} else if ("posCode".equals(attrName)) { //pos机编号
					lo.setPosCode(jr.nextString());
				} else if ("cashierId".equals(attrName)) { //收银员id
					lo.setCashierId(jr.nextInt());
				} else if ("products".equals(attrName)) {
					jr.beginArray();
					while (jr.hasNext()) {
						lop = new LeaseOrderProduct();
						jr.beginObject();
						while (jr.hasNext()) {
							attrName = jr.nextName();
							if ("id".equals(attrName)) { //商品id
								lop.setProductId(jr.nextInt());
							} else if ("count".equals(attrName)) { //商品数量
								lop.setCount(jr.nextInt());
							} else if ("prePrice".equals(attrName)) { //单价
								lop.setPrePrice(jr.nextDouble());
							} else if ("perDeposit".equals(attrName)) { //单押金
								lop.setPerDeposit(jr.nextDouble());
							} else if ("startTime".equals(attrName)) { //租赁开始时间
								lop.setStartTime(new Timestamp(jr.nextLong()));
							} else if ("endTime".equals(attrName)) { //租赁结束时间
								lop.setEndTime(new Timestamp(jr.nextLong()));
							} else if ("timeLength".equals(attrName)) { //租赁时长
								lop.setTimeLength(jr.nextDouble());
							} else if ("leaseStyle".equals(attrName)) { //租赁方式[0:按日租赁；1:包月租赁]
								lop.setLeaseStyle(jr.nextInt());
							}
						}
						jr.endObject();
						temp_01.add(lop);
					}
					jr.endArray();
				}else if("memlease".equals(attrName)){
					jr.beginArray();
					while (jr.hasNext()) {
						ml = new MemberLease();
						jr.beginObject();
						while (jr.hasNext()) {
							attrName = jr.nextName();
							if ("id".equals(attrName)) { //租赁表id
								ml.setId(jr.nextInt());
							} else if ("memberId".equals(attrName)) { //会员id
								ml.setMemberId(jr.nextString());
							} else if ("productId".equals(attrName)) { //商品id
								ml.setProductId(jr.nextInt());
							} else if ("startTime".equals(attrName)) { //租赁开始时间
								ml.setStartTime(new Timestamp(jr.nextLong()));
							} else if ("endTime".equals(attrName)) { //租赁结束时间
								ml.setEndTime(new Timestamp(jr.nextLong()));
							} else if ("serialNumber".equals(attrName)) { //租赁订单流失号
								ml.setSerialNumber(jr.nextString());
							} else if ("type".equals(attrName)) { //租赁方式[0:按日租赁；1:包月租赁]
								ml.setType(jr.nextInt());
							} 
						}
						jr.endObject();
						temp_02.add(ml);
					}
					jr.endArray();
					
				}
			}
			temp_01.add(0, lo);
			temp.add(temp_01);
			temp.add(temp_02);
			jr.endObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}
	
	/**
	 * 保存销售订单信息
	 * @param saledOrder 销售订单对象
	 * @throws Exception 
	 */
	public void saveSaledOrder(SaledOrder saledOrder) throws Exception {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean oldAutoCommit = true;
		try {
			//记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			//关闭自动提交事务
			conn.setAutoCommit(false);
			
			//销售订单条目
			List<SaledOrderProduct> saledOrderProductList = saledOrder.getProductList();
			
			//订单金钱来源列表数据
			List<MoneySource> moneySourceList = saledOrder.getMoneySourceList();
			double cash = 0; //现金
			double memberCard = 0; //会员卡
			double bankCard = 0; //银行卡
			for (MoneySource ms : moneySourceList) {
				if(ms.getType() == 1) {
					cash = ms.getMoney();
				} else if(ms.getType() == 2) {
					memberCard = ms.getMoney();
				} else if(ms.getType() == 3) {
					bankCard = ms.getMoney();
				}
			}
			
			//保存销售订单信息
			ps = conn.prepareStatement("insert into saled_order(`serial_number`,`member_id`,`price`,`saled_time`,pos_code,cashier_id,order_type,score) values (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, saledOrder.getSerialNumber());
			ps.setString(2, saledOrder.getMemberId());
			ps.setDouble(3, saledOrder.getPrice());
			ps.setTimestamp(4, new Timestamp(new Date().getTime()));
			ps.setString(5, saledOrder.getPosCode());
			ps.setInt(6, saledOrder.getCashierId());
			ps.setInt(7, 0); //订单类型[0:购买订单；1:退货订单]
			ps.setInt(8, saledOrder.getScore());
			ps.executeUpdate();
			
			//获取订单id
			int orderId = 0;
			rs = ps.getGeneratedKeys();
        	if(rs.next()) {
        		orderId = rs.getInt(1);
        	}
        	
        	//批量保存销售订单商品列表数据
        	ps = conn.prepareStatement("insert into saled_order_product(`product_id`,`saled_order_id`,`count`,`pre_price`,event_remark) values (?,?,?,?,?)");
        	for (SaledOrderProduct op : saledOrderProductList) {
				ps.setInt(1, op.getProductId());
				ps.setInt(2, orderId);
				ps.setInt(3, op.getCount());
				ps.setDouble(4, op.getPrePrice());
				ps.setString(5, op.getEventRemark());
				ps.addBatch();
			}
        	ps.executeBatch();
        	
        	//保存红蓝卡的收支明细
        	Map<String, Double> cardMap = new HashMap<String, Double>();
        	for (SaledOrderProduct op : saledOrderProductList) {
        		if(StringUtils.isNotBlank(op.getRedId())) {
        			if(cardMap.containsKey(op.getRedId())) {
        				cardMap.put(op.getRedId(), cardMap.get(op.getRedId())+op.getRedPoint());
        			} else {
        				cardMap.put(op.getRedId(), op.getRedPoint());
        			}
        		}
        		if(StringUtils.isNotBlank(op.getBlueId())) {
        			if(cardMap.containsKey(op.getBlueId())) {
        				cardMap.put(op.getBlueId(), cardMap.get(op.getBlueId())+op.getBluePoint());
        			} else {
        				cardMap.put(op.getBlueId(), op.getBluePoint());
        			}
        		}
        	}
        	if(!cardMap.isEmpty()) {
        		//获取销售前的红蓝卡的余额
        		List<PriceCardCharge> inoutList = new ArrayList<PriceCardCharge>();
        		for(String id : cardMap.keySet()) {
        			PriceCardCharge inout = new PriceCardCharge();
        			inout.setPriceCardId(id);
        			inout.setOrderId(orderId);
        			inout.setConsumeType(2); //消费
        			inout.setConsumeCash(cardMap.get(id));
        			inoutList.add(inout);
        		}
        		StringBuilder sql = new StringBuilder();
    			sql.append("select id,point from price_card where id in(");
    			for(PriceCardCharge inout : inoutList) {
    				sql.append("'").append(inout.getPriceCardId()).append("',");
    			}
    			sql.deleteCharAt(sql.length()-1).append(")");
    			ps = conn.prepareStatement(sql.toString());
    			rs = ps.executeQuery();
    			while (rs.next()) {
    				String id = rs.getString("id");
    				double point = rs.getDouble("point");
    				for(PriceCardCharge inout : inoutList) {
    					if(id.equals(inout.getPriceCardId())) {
    						inout.setTotalCash(point - inout.getConsumeCash());
    					}
    				}
    			}
    			new PriceCardChargeService().addBatPriceCardCharge(conn, inoutList);
    			
    			//修改红蓝卡的余额
    			ps = conn.prepareStatement("update price_card set point = point - ? where id = ?");
    			for(PriceCardCharge inout : inoutList) {
    				ps.setDouble(1, inout.getConsumeCash());
    				ps.setString(2, inout.getPriceCardId());
    				ps.addBatch();
    			}
    			ps.executeBatch();
        	}
        	
        	//批量保存订单金钱来源列表数据
        	ps = conn.prepareStatement("insert into money_source(`order_id`,`type`,`money`,`swip_card_number`,withdraw_money) values (?,?,?,?,?)");
        	for (MoneySource ms : moneySourceList) {
				ps.setInt(1, orderId);
				ps.setInt(2, ms.getType());
				ps.setDouble(3, ms.getMoney());
				ps.setString(4, ms.getSwipCardNumber());
				ps.setDouble(5, ms.getMoney());
				ps.addBatch();
			}
        	ps.executeBatch();
			
			//处理会员账户信息
			if(StringUtils.isNotBlank(saledOrder.getMemberId())) {
				//获取会员账户信息
				MemberAccount ma = new MemberAccount();
				ps = conn.prepareStatement("select id,available_balance,consumption,freeze_balance,score from member_account where id=?");
				ps.setString(1, saledOrder.getMemberId());
				rs = ps.executeQuery();
				if(rs.next()) {
					ma.setId(rs.getString("id"));
					ma.setAvailableBalance(rs.getDouble("available_balance"));
					ma.setConsumption(rs.getDouble("consumption"));
					ma.setFreezeBalance(rs.getDouble("freeze_balance"));
					ma.setScore(rs.getInt("score"));
				}
				
				//追加会员消费金额
				ma.setConsumption(ma.getConsumption() + cash + bankCard + memberCard);
				
				if(memberCard > 0) {
					//记录会员收支明细
					MemberAccountDetail detail = new MemberAccountDetail();
					detail.setMemberId(saledOrder.getMemberId());
					detail.setOrderId(orderId);
					detail.setType(3); //购买
					detail.setPay(-memberCard);
					detail.setBalance(ma.getAvailableBalance() - memberCard);
					new MemberAccountDetailService().saveMemberAccountDetail(conn, detail);
					
					//扣除消费金额
					ma.setAvailableBalance(ma.getAvailableBalance() - memberCard);
				}
				
				//记录会员积分信息
				if(saledOrder.score > 0){
					//记录会员积分
					ma.setScore(ma.getScore() + saledOrder.getScore());
					
					//生成会员积分记录
					MemberScore memScore = new MemberScore();
					memScore.setMemberId(saledOrder.getMemberId());
					memScore.setOrderId(orderId);
					memScore.setType(1);
					memScore.setCurrentScore(ma.getScore());
					memScore.setAddScore(saledOrder.score);
					memScore.setCreateTime(new Timestamp(new Date().getTime()));
					new MemberScoreService().addMemberScore(conn, memScore);
				}
				
				//更新会员账户信息
				new MemberAccountService().updateMemberAccount(conn, ma);
			}
			
			//保存现金流
			if(cash > 0) {
				CashStream cashStream = new CashStream();
				cashStream.setSerialNumber(saledOrder.getSerialNumber());
				cashStream.setPosCode(saledOrder.getPosCode());
				cashStream.setCashierId(saledOrder.getCashierId());
				cashStream.setCash(cash);
				new CashStreamService().saveCashStream(conn, cashStream);
			}
			
        	//获取销售前的库存信息并生成进销存记录
			List<Invoice> invoiceList = new ArrayList<Invoice>();
			Invoice invoice = new Invoice();
			StringBuilder sql = new StringBuilder();
			sql.append("select id,stock from product where id in(");
			for(SaledOrderProduct op : saledOrderProductList) {
				sql.append(op.getProductId()).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(")");
        	ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				invoice = new Invoice();
				invoice.setOperType("1");
				invoice.setSerialNumber(saledOrder.getSerialNumber());
				invoice.setProductId(rs.getInt("id"));
				invoice.setBeforeCount(rs.getInt("stock"));
				invoice.setAfterCount(invoice.getBeforeCount()); //处理多条订单条目中存在相同商品的问题
				for(SaledOrderProduct op : saledOrderProductList) {
					if(op.getProductId() == invoice.getProductId()) {
						invoice.setAfterCount(invoice.getAfterCount() - op.getCount());
						invoice.setCount(invoice.getCount() + op.getCount());
					}
				}
				invoiceList.add(invoice);
			}
			new InvoiceService().addBatInvoice(conn, invoiceList);
			
			//更新商品库存信息
			ps = conn.prepareStatement("update product set stock = stock - ? where id = ?");
			for(SaledOrderProduct op : saledOrderProductList) {
				ps.setInt(1, op.getCount());
				ps.setInt(2, op.getProductId());
				ps.addBatch();
			}
			ps.executeBatch();
			
			//若该销售订单有活动，存储该活动
			if(!saledOrder.getSaledOrderEventList().isEmpty()){
				ps = conn.prepareStatement("insert into saled_order_event(saled_order_id,event_id,detail_event_id,ext_id) values(?,?,?,?)");
				for(SaledOrderEvent soe : saledOrder.getSaledOrderEventList()){
					ps.setInt(1, orderId);
					ps.setInt(2, soe.getEventId());
					ps.setInt(3, soe.getDetailEventId());
					ps.setInt(4, soe.getExtId());
					ps.addBatch();
				}
				ps.executeBatch();
				
				//处理有人数限制的买赠活动
				this.doUserLimitBuyGiftEvent(conn, saledOrder);
			}
			
        	conn.commit();
		} catch (Exception e) {
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {}
			log.error("保存销售订单信息时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(rs, ps, conn, oldAutoCommit);
		}
	}
	
	/**
	 * 处理有人数限制的买赠活动
	 * <br>更新已用份数
	 * @param conn 此方法不处理事务
	 * @param saledOrder 销售订单对象
	 * @throws Exception 
	 */
	private void doUserLimitBuyGiftEvent(Connection conn, SaledOrder saledOrder) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			//从订单商品中挑选出参与买赠活动的商品
			Map<Integer, Integer> pidCountMap = new HashMap<Integer, Integer>();
			for(SaledOrderProduct op : saledOrder.getProductList()) {
				if("买赠".equals(op.getEventRemark())) {
					if(pidCountMap.containsKey(op.getProductId())) {
						pidCountMap.put(op.getProductId(), pidCountMap.get(op.getProductId())+op.getCount());
					} else {
						pidCountMap.put(op.getProductId(), op.getCount());
					}
				}
			}
			if(pidCountMap.isEmpty()) {
				return ;
			}
			
			//获取销售订单参与的买赠活动
			Integer eventId = null;
			StringBuilder sql = new StringBuilder();
			sql.append("select e.id from event e where e.type=1 and e.id in(");
			for(SaledOrderEvent soe : saledOrder.getSaledOrderEventList()) {
				sql.append(soe.getEventId()).append(",");
			}
			sql.deleteCharAt(sql.length()-1).append(")");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				eventId = rs.getInt("id");
			}
			
			//获取买赠详情活动的ID列表
			Set<Integer> buyGiftEventIdSet = new HashSet<Integer>();
			for(SaledOrderEvent soe : saledOrder.getSaledOrderEventList()) {
				if(eventId == soe.getEventId()) {
					buyGiftEventIdSet.add(soe.getDetailEventId());
				}
			}
			
			//获取买赠详情活动列表
			List<BuyGiftEvent> buyGiftEventList = new ArrayList<BuyGiftEvent>();
			BuyGiftEvent buyGiftEvent = null;
			sql = new StringBuilder();
			sql.append("select e.id,e.product_id,e.product_count from buy_gift_event e where e.user_limit!=0 and e.id in(");
			for(Integer id : buyGiftEventIdSet) {
				sql.append(id).append(",");
			}
			sql.deleteCharAt(sql.length()-1).append(")");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				buyGiftEvent = new BuyGiftEvent();
				buyGiftEvent.setId(rs.getInt("id"));
				buyGiftEvent.setProductId(rs.getInt("product_id"));
				buyGiftEvent.setProductCount(rs.getInt("product_count"));
				buyGiftEventList.add(buyGiftEvent);
			}
			
			if(!buyGiftEventList.isEmpty()) {
				//计算买赠活动的使用份数
				Map<Integer, Integer> buyGiftEventCountMap = new HashMap<Integer, Integer>();
				for(BuyGiftEvent bgEvent : buyGiftEventList) {
					Integer productId = bgEvent.getProductId();
					Integer productCount = bgEvent.getProductCount();
					Integer realCount = pidCountMap.get(productId);
					buyGiftEventCountMap.put(bgEvent.getId(), realCount/productCount);
				}
				
				//更新买赠活动‘已用份数’字段
				sql = new StringBuilder();
				sql.append("update buy_gift_event bge set bge.remain_count = bge.remain_count+? where id = ?");
				ps = conn.prepareStatement(sql.toString());
				for(Integer buyGiftEventId : buyGiftEventCountMap.keySet()) {
					ps.setInt(1, buyGiftEventCountMap.get(buyGiftEventId));
					ps.setInt(2, buyGiftEventId);
					ps.addBatch();
				}
				ps.executeBatch();
			}
		} catch (Exception e) {
			log.error("保存销售订单~处理有人数限制的买赠活动时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(rs, ps, null);
		}
	}
	
	/**
	 * 保存租赁订单信息
	 * @param leaseOrder 租赁订单对象
	 * @param leaseOrderProductList 租赁订单商品列表数据
	 * @param memberLeaseList 会员租赁信息
	 * @return 是否保存成功
	 */
	public boolean saveLeaseOrder(LeaseOrder leaseOrder, List<LeaseOrderProduct> leaseOrderProductList, List<MemberLease> memberLeaseList) {
		boolean success = false;
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean oldAutoCommit = true;
		try {
			//记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			//关闭自动提交事务
			conn.setAutoCommit(false);
			
			//保存或更新会员租赁信息
			MemberLeaseService mls = new MemberLeaseService();
			mls.saveOrUpdateMemberLease(conn, memberLeaseList);
			
			//保存租赁订单信息
			ps = conn.prepareStatement("insert into lease_order(`serial_number`,`member_id`,`price`,`deposit`,`order_type`,create_time,pos_code,cashier_id) values (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, leaseOrder.getSerialNumber());
			ps.setString(2, leaseOrder.getMemberId());
			ps.setDouble(3, leaseOrder.getPrice());
			ps.setDouble(4, leaseOrder.getDeposit());
			ps.setInt(5, 0); //订单类型[0:租赁订单；1:还租订单]
			ps.setTimestamp(6, new Timestamp(new Date().getTime()));
			ps.setString(7, leaseOrder.getPosCode());
			ps.setInt(8, leaseOrder.getCashierId());
			ps.executeUpdate();
			
			//获取订单id
			int orderId = 0;
			rs = ps.getGeneratedKeys();
        	if(rs.next()) {
        		orderId = rs.getInt(1);
        	}
        	
        	//批量保存租赁订单商品列表数据
        	ps = conn.prepareStatement("insert into lease_order_product(`product_id`,`lease_order_id`,`count`,`pre_price`,`per_deposit`,`start_time`,`end_time`,`time_length`,`lease_style`) values (?,?,?,?,?,?,?,?,?)");
        	for(LeaseOrderProduct op : leaseOrderProductList) {
        		ps.setInt(1, op.getProductId());
        		ps.setInt(2, orderId);
        		ps.setInt(3, op.getCount());
        		ps.setDouble(4, op.getPrePrice());
        		ps.setDouble(5, op.getPerDeposit());
        		ps.setTimestamp(6, op.getStartTime());
        		ps.setTimestamp(7, op.getEndTime());
        		ps.setDouble(8, op.getTimeLength());
        		ps.setInt(9, op.getLeaseStyle());
        		ps.addBatch();
        	}
        	ps.executeBatch();
        	
        	//获取租赁前的库存信息并生成进销存记录
			List<Invoice> invoiceList = new ArrayList<Invoice>();
			Invoice invoice = new Invoice();
			StringBuilder sql = new StringBuilder();
			sql.append("select id,stock from product where id in(");
			for(LeaseOrderProduct op : leaseOrderProductList) {
				sql.append(op.getProductId()).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(")");
        	ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				invoice = new Invoice();
				invoice.setOperType("3"); //3:租赁
				invoice.setSerialNumber(leaseOrder.getSerialNumber());
				invoice.setProductId(rs.getInt("id"));
				invoice.setBeforeCount(rs.getInt("stock"));
				invoice.setAfterCount(invoice.getBeforeCount()); //处理多条订单条目中存在相同商品的问题
				for(LeaseOrderProduct op : leaseOrderProductList) {
					if(op.getProductId() == invoice.getProductId()) {
						invoice.setAfterCount(invoice.getAfterCount() - op.getCount());
						invoice.setCount(invoice.getCount() + op.getCount());
					}
				}
				invoiceList.add(invoice);
			}
			new InvoiceService().addBatInvoice(conn, invoiceList);
			
			//更新商品库存信息
			ps = conn.prepareStatement("update product set stock = stock - ? where id = ?");
			for(LeaseOrderProduct op : leaseOrderProductList) {
				ps.setInt(1, op.getCount());
				ps.setInt(2, op.getProductId());
				ps.addBatch();
			}
			ps.executeBatch();
        	
			//为会员充值和冻结会员金额
			if(StringUtils.isNotBlank(leaseOrder.getMemberId())) {
				MemberService ms = new MemberService();
				if(leaseOrder.getCash()>0) {
					//为会员充值
					ms.recharge(conn, leaseOrder.getMemberId(), leaseOrder.getCash());
					
					//保存现金流
					CashStreamService cs = new CashStreamService();
					CashStream cashStream = new CashStream();
					cashStream.setSerialNumber(leaseOrder.getSerialNumber());
					cashStream.setPosCode(leaseOrder.getPosCode());
					cashStream.setCashierId(leaseOrder.getCashierId());
					cashStream.setCash(leaseOrder.getCash());
					cs.saveCashStream(conn, cashStream);
				}
				//冻结会员金额
				double freezeMoney = leaseOrder.getPrice() + leaseOrder.getDeposit();
				ms.freezeMemberAccount(conn, leaseOrder.getMemberId(), freezeMoney);
			}
			
			//提交事务
			conn.commit();
			success = true;
		} catch (Exception e) {
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("保存租赁订单信息时出现错误：", e);
		} finally {
			DbUtil.closeConnection(rs, ps, conn, oldAutoCommit);
		}
		return success;
	}
	
	/**
	 * 退货
	 * @param json POS机传递的JSON数据
	 * @return 操作结果
	 */
	public String backSaledOrder(String json) {
		String result = "success";
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean oldAutoCommit = true;
		try {
			//记录原来事物提交的方式
			oldAutoCommit = conn.getAutoCommit();
			//关闭自动提交事务
			conn.setAutoCommit(false);
			
			//解析JSON字符串
			SaledOrder saledOrder = new Gson().fromJson(json, SaledOrder.class);
			
			//保存 退货订单信息
			ps = conn.prepareStatement("insert into saled_order(`serial_number`,`member_id`,`price`,`saled_time`,pos_code,cashier_id,order_type,score) values (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, saledOrder.getSerialNumber());
			ps.setString(2, saledOrder.getMemberId());
			ps.setDouble(3, saledOrder.getPrice());
			ps.setTimestamp(4, new Timestamp(new Date().getTime()));
			ps.setString(5, saledOrder.getPosCode());
			ps.setInt(6, saledOrder.getCashierId());
			ps.setInt(7, 1); //订单类型[0:购买订单；1:退货订单]
			ps.setInt(8, saledOrder.getScore());
			ps.executeUpdate();
			
			//获取订单id
			int orderId = 0;
			rs = ps.getGeneratedKeys();
        	if(rs.next()) {
        		orderId = rs.getInt(1);
        	}
        	
        	//保存退货订单商品列表数据
			ps = conn.prepareStatement("insert into saled_order_product(`saled_order_id`,`product_id`,`count`,`pre_price`,event_remark) values (?,?,?,?,?)");
			for(SaledOrderProduct saledOrderProduct : saledOrder.getProductList()) {
				ps.setInt(1, orderId);
				ps.setInt(2, saledOrderProduct.getProductId());
				ps.setInt(3, saledOrderProduct.getCount());
				ps.setDouble(4, saledOrderProduct.getPrePrice());
				ps.setString(5, saledOrderProduct.getEventRemark());
				ps.addBatch();
			}
			ps.executeBatch();
			
        	//更新现金来源表
        	ps = conn.prepareStatement("update money_source ms set ms.withdraw_money = ? where ms.id = ? ");
        	for(MoneySource ms : saledOrder.getMoneySourceList()){
        		ps.setDouble(1, ms.getWithdrawMoney());
        		ps.setInt(2, ms.getId());
        		ps.addBatch();
        	}
            ps.executeBatch();
            
            //记录现金去向表
            ps = conn.prepareStatement("insert into money_destination(order_id,type,money) values(?,?,?)");
            for(MoneyDestination md : saledOrder.getMoneyDestinationList()){
            	ps.setInt(1, orderId);
            	ps.setInt(2, md.getType());
            	ps.setDouble(3, md.getMoney());
            	ps.addBatch();
            }
            ps.executeBatch();
            
            //查看退货前的库存信息，生成进销存记录
			List<Invoice> invoiceList = new ArrayList<Invoice>();
			Invoice invoice = new Invoice();
			StringBuilder sql = new StringBuilder();
			sql.append("select id,stock from product where id in(");
			for (SaledOrderProduct saledOrderProduct : saledOrder.getProductList()) {
				sql.append(saledOrderProduct.getProductId()).append(",");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				invoice = new Invoice();
				invoice.setOperType("2"); //2:退货
				invoice.setSerialNumber(saledOrder.getSerialNumber());
				invoice.setProductId(rs.getInt("id"));
				invoice.setBeforeCount(rs.getInt("stock"));
				invoice.setAfterCount(invoice.getBeforeCount());
				for (SaledOrderProduct saledOrderProduct : saledOrder.getProductList()) {
					if (saledOrderProduct.getProductId() == invoice.getProductId()) {
						invoice.setAfterCount(invoice.getAfterCount() + saledOrderProduct.getCount()); //处理订单商品重新的情况
						invoice.setCount(invoice.getCount() + saledOrderProduct.getCount());
					}
				}
				invoiceList.add(invoice);
			}
			new InvoiceService().addBatInvoice(conn, invoiceList);
			
			//更新商品库存信息
			ps = conn.prepareStatement("update product set stock=stock+? where id=?");
			for(SaledOrderProduct saledOrderProduct : saledOrder.getProductList()) {
				ps.setInt(1, saledOrderProduct.getCount());
				ps.setInt(2, saledOrderProduct.getProductId());
				ps.addBatch();
			}
			ps.executeBatch();
			
			//订单返款去向列表数据
			double cash = 0; //现金
			double memberCard = 0; //会员卡
			double bankCard = 0; //银行卡
			for (MoneyDestination md : saledOrder.getMoneyDestinationList()) {
				if(md.getType() == 1) {
					cash = md.getMoney();
				} else if(md.getType() == 2) {
					memberCard = md.getMoney();
				} else if(md.getType() == 3) {
					bankCard = md.getMoney();
				}
			}
			
			//会员操作
			if(StringUtils.isNotBlank(saledOrder.getMemberId())) {
				//获取会员账户信息
				MemberAccount ma = new MemberAccount();
				ps = conn.prepareStatement("select id,available_balance,consumption,freeze_balance,score from member_account where id=?");
				ps.setString(1, saledOrder.getMemberId());
				rs = ps.executeQuery();
				if(rs.next()) {
					ma.setId(rs.getString("id"));
					ma.setAvailableBalance(rs.getDouble("available_balance"));
					ma.setConsumption(rs.getDouble("consumption"));
					ma.setFreezeBalance(rs.getDouble("freeze_balance"));
					ma.setScore(rs.getInt("score"));
				}
				
				//记录会员积分信息
				if(saledOrder.score > 0) {
					//记录会员积分
					ma.setScore(ma.getScore() - saledOrder.getScore());

					// 生成会员积分记录
					MemberScore memScore = new MemberScore();
					memScore.setMemberId(saledOrder.getMemberId());
					memScore.setOrderId(orderId);
					memScore.setType(2);
					memScore.setCurrentScore(ma.getScore());
					memScore.setMinusScore(-saledOrder.score);
					memScore.setCreateTime(new Timestamp(new Date().getTime()));
					new MemberScoreService().addMemberScore(conn, memScore);
				}
				
				//退款到会员卡，记录会员收支明细
				if(memberCard > 0) {
					//记录会员收支明细
					MemberAccountDetail detail = new MemberAccountDetail();
					detail.setMemberId(saledOrder.getMemberId());
					detail.setOrderId(orderId);
					detail.setType(5); //退货
					detail.setIncome(memberCard);
					detail.setBalance(ma.getAvailableBalance() + memberCard);
					new MemberAccountDetailService().saveMemberAccountDetail(conn, detail);
					
					//退款到会员卡
					ma.setAvailableBalance(ma.getAvailableBalance() + memberCard);
				}
				
				//减去总消费金额
				ma.setConsumption(ma.getConsumption() - cash - memberCard - bankCard);
				
				//更新会员账户信息
				new MemberAccountService().updateMemberAccount(conn, ma);
			}
        	
			//保存现金流
			if(cash > 0) {
				CashStream cashStream = new CashStream();
				cashStream.setSerialNumber(saledOrder.getSerialNumber());
				cashStream.setPosCode(saledOrder.getPosCode());
				cashStream.setCashierId(saledOrder.getCashierId());
				cashStream.setCash(-cash);
				new CashStreamService().saveCashStream(conn, cashStream);
			}
			
			conn.commit();
		} catch (Exception e) {
			try {
				//回滚事物
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("退货时出现异常：", e);
			result = e.toString();
		} finally {
			DbUtil.closeConnection(rs, ps, conn, oldAutoCommit);
		}
		return "{\"message\":\""+result+"\"}";
	}

	/**
	 * 处理归还租赁商品操作
	 * @param json pos机传递的json数据
	 * @return 操作是否成功
	 */
	public boolean returnLeaseProduct(String json) {
		boolean success = false;
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean oldAutoCommit = true;
		try {
			//解析POS机传递的JSON数据
			List<Object> list = this.doReturnLeaseJson(json);
			LeaseOrder leaseOrder = (LeaseOrder) list.get(0);
			LeaseOrderProduct op = (LeaseOrderProduct) list.get(1);
			List<MemberLease> memberleaselist = new ArrayList<MemberLease>();
			for (int i = 2; i < list.size(); i++) {
				memberleaselist.add((MemberLease) list.get(i));
			}
			
			//记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			//关闭自动提交事务
			conn.setAutoCommit(false);
			
			//更新会员租赁信息表的数据
			MemberLeaseService mls = new MemberLeaseService();
			mls.saveOrUpdateMemberLease(conn, memberleaselist);
			
			//保存还租订单信息
			ps = conn.prepareStatement("insert into lease_order(`serial_number`,`member_id`,`price`,`deposit`,`order_type`,create_time,pos_code,cashier_id) values (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, leaseOrder.getSerialNumber());
			ps.setString(2, leaseOrder.getMemberId());
			ps.setDouble(3, leaseOrder.getPrice());
			ps.setDouble(4, leaseOrder.getDeposit());
			ps.setInt(5, 1); //订单类型[0:租赁订单；1:还租订单]
			ps.setTimestamp(6, new Timestamp(new Date().getTime()));
			ps.setString(7, leaseOrder.getPosCode());
			ps.setInt(8, leaseOrder.getCashierId());
			ps.executeUpdate();
			
			//获取订单id
			int orderId = 0;
			rs = ps.getGeneratedKeys();
        	if(rs.next()) {
        		orderId = rs.getInt(1);
        	}
			
			//保存还租订单商品
        	ps = conn.prepareStatement("insert into lease_order_product(`product_id`,`lease_order_id`,`count`,`pre_price`,`per_deposit`,`start_time`,`end_time`,`time_length`,`lease_style`) values (?,?,?,?,?,?,?,?,?)");
        	ps.setInt(1, op.getProductId());
        	ps.setInt(2, orderId);
        	ps.setInt(3, op.getCount());
        	ps.setDouble(4, op.getPrePrice());
        	ps.setDouble(5, op.getPerDeposit());
        	ps.setTimestamp(6, op.getStartTime());
        	ps.setTimestamp(7, op.getEndTime());
        	ps.setDouble(8, op.getTimeLength());
        	ps.setInt(9, op.getLeaseStyle());
        	ps.executeUpdate();
        	
        	//记录会员收支明细
			MemberAccount ma = new MemberAccountService().getMemberAccountById(leaseOrder.getMemberId());
			MemberAccountDetail detail = new MemberAccountDetail();
			detail.setMemberId(leaseOrder.getMemberId());
			detail.setOrderId(orderId);
			detail.setType(4); //租赁
			detail.setPay(-leaseOrder.getPrice());
			detail.setBalance(ma.getAvailableBalance() - leaseOrder.getPrice());
			new MemberAccountDetailService().saveMemberAccountDetail(conn, detail);
			
			//处理会员金额
			ps = conn.prepareStatement("update member_account set available_balance = available_balance + ?,freeze_balance = freeze_balance - (? + ?),consumption = consumption + ? where id = ?");
			ps.setDouble(1, leaseOrder.getDeposit());
			ps.setDouble(2, leaseOrder.getPrice());
			ps.setDouble(3,leaseOrder.getDeposit());
			ps.setDouble(4,leaseOrder.getPrice());
			ps.setString(5, leaseOrder.getMemberId());
			ps.executeUpdate();
			
			// 查看还租前的库存信息，生成进销存记录
			Invoice in = new Invoice();
			ps = conn.prepareStatement("select stock from product where id = ?");
			ps.setInt(1, op.getProductId());
			rs = ps.executeQuery();
			if (rs.next()) {
				in.setBeforeCount(rs.getInt("stock"));
				in.setCount(op.getCount());
				in.setAfterCount(rs.getInt("stock") + op.getCount());
			}
			in.setProductId(op.getProductId());
			in.setOperType("4");
			in.setSerialNumber(leaseOrder.getSerialNumber());
			new InvoiceService().addInvoice(conn, in);
			
			//处理库存
			ps = conn.prepareStatement("update product set stock = stock + ? where id = ?");
			ps.setInt(1, op.getCount());
			ps.setInt(2, op.getProductId());
			ps.executeUpdate();
			
			//提交事务
			conn.commit();
			success = true;
		} catch (Exception e) {
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("保存还租订单信息时出现错误：", e);
		} finally {
			DbUtil.closeConnection(rs, ps, null);
			if(conn != null){
				try {
					//还原事务提交方式
					conn.setAutoCommit(oldAutoCommit);
					conn.close();
				} catch (SQLException e) {}
			}
		}
		return success;
	}
	
	/**
	 * 解析pos机传递的还租json数据
	 * @param json pos机传递的json数据
	 * @return 0=LeaseOrder; 1=LeaseOrderProduct; 2--max  memberLease
	 */
	private List<Object> doReturnLeaseJson(String json) {
		List<Object> temp = new ArrayList<Object>();
		LeaseOrder lo = new LeaseOrder();
		LeaseOrderProduct lop = new LeaseOrderProduct();
		MemberLease ml;
		System.out.println(json);
		try {
			JsonReader jr = new JsonReader(new StringReader(json));
			jr.beginObject();
			String attrName = null;
			while (jr.hasNext()) {
				attrName = jr.nextName();
				if ("memberId".equals(attrName)) { //会员id
					lo.setMemberId(jr.nextString());
				} else if ("posCode".equals(attrName)) { //pos机编号
					lo.setPosCode(jr.nextString());
				} else if ("cashierId".equals(attrName)) { //收银员id
					lo.setCashierId(jr.nextInt());
				} else if ("serialId".equals(attrName)) { //订单流水号
					lo.setSerialNumber(jr.nextString());
				} else if ("price".equals(attrName)) { //订单总金额
					lo.setPrice(jr.nextDouble());
				}else if("deposit".equals(attrName)){ //订单总押金
					lo.setDeposit(jr.nextDouble());
				} else if ("productId".equals(attrName)) { //商品id
					lop.setProductId(jr.nextInt());
				} else if ("count".equals(attrName)) { //商品数量
				    lop.setCount(jr.nextInt());
				} else if ("prePrice".equals(attrName)) { //单价(非包月：每个商品每日的租赁价格；包月：每个商品每月的租赁价格)
				    lop.setPrePrice(jr.nextDouble());
				} else if ("perDeposit".equals(attrName)) { //单押金(非包月：每个商品每日的押金；包月：每个商品每月的押金)
				    lop.setPerDeposit(jr.nextDouble());
				} else if ("startTime".equals(attrName)) { //租赁开始时间（long毫秒数）
				    lop.setStartTime(new Timestamp(jr.nextLong()));
				} else if ("endTime".equals(attrName)) { //租赁结束时间（long毫秒数）
					lop.setEndTime(new Timestamp(jr.nextLong()));
				} else if ("timeLength".equals(attrName)) { //租赁时长(非包月：单位为天；包月：单位为月)
				    lop.setTimeLength(jr.nextDouble());
				} else if ("leaseStyle".equals(attrName)) { //租赁方式[0:按日租赁；1:包月租赁]
				    lop.setLeaseStyle(jr.nextInt());
				}else if("memlease".equals(attrName)){
					jr.beginArray();
					int i = 2;
					while (jr.hasNext()) {
						ml = new MemberLease();
						jr.beginObject();
						while (jr.hasNext()) {
							attrName = jr.nextName();
							if ("id".equals(attrName)) { //租赁表id
								ml.setId(jr.nextInt());
							} else if ("memberId".equals(attrName)) { //会员id
								ml.setMemberId(jr.nextString());
							} else if ("productId".equals(attrName)) { //商品id
								ml.setProductId(jr.nextInt());
							} else if ("startTime".equals(attrName)) { //租赁开始时间
								ml.setStartTime(new Timestamp(jr.nextLong()));
							} else if ("endTime".equals(attrName)) { //租赁结束时间
								ml.setEndTime(new Timestamp(jr.nextLong()));
							} else if ("serialNumber".equals(attrName)) { //租赁订单流失号
								ml.setSerialNumber(jr.nextString());
							} else if ("type".equals(attrName)) { //租赁方式[0:按日租赁；1:包月租赁]
								ml.setType(jr.nextInt());
							} 
						}
						jr.endObject();
						temp.add(ml);
						i++;
					}
					jr.endArray();
					
				}
			}
			jr.endObject();
			temp.add(0,lo);
			temp.add(1,lop);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return temp;
	}

	/**
	 * 获取销售商品信息
	 * @param json POS机传递的JSON数据
	 * @return JSON格式的查询结果
	 */
	public String getSaledProduct(String json) {
		StringBuilder jsonResult = new StringBuilder();
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			//解析POS机传递的JSON数据
			Map<String, Object> param = this.doGetProductJson(json);
			String barCode = (String) param.get("barCode"); //商品条形码(可选)
			String memberId = (String) param.get("memberId"); //会员id(可选)
			String serialNumber = (String) param.get("serialNumber"); //订单流水号
			
			//获取销售订单信息
			SaledOrder order = new SaledOrder();
			StringBuilder sql = new StringBuilder();
			sql.append("select d.id,d.serial_number,d.price,d.score from saled_order d where d.serial_number = ?");
			if(StringUtils.isNotBlank(memberId)) {
				sql.append(" and d.member_id=?");
			}
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, serialNumber);
			if(StringUtils.isNotBlank(memberId)) {
				ps.setString(2, memberId);
			}
			rs = ps.executeQuery();
			if(rs.next()) {
				order.setId(rs.getInt("id"));
				order.setSerialNumber(rs.getString("serial_number"));
				order.setPrice(rs.getDouble("price"));
				order.setScore(rs.getInt("score"));
			}
			
			//获取订单商品
			sql = new StringBuilder();
			sql.append("select t.id,t.product_id,t.count,t.pre_price,t.event_remark,p.name,p.bar_code from saled_order_product t join product p on t.product_id=p.id ");
			sql.append(" where t.saled_order_id=?");
			if(StringUtils.isNotBlank(barCode)) {
				sql.append(" and p.bar_code=?");
			}
			ps = conn.prepareStatement(sql.toString());
			ps.setInt(1, order.getId());
			if(StringUtils.isNotBlank(barCode)) {
				ps.setString(2, barCode);
			}
			rs = ps.executeQuery();
			SaledOrderProduct saledOrderProduct;
			while (rs.next()) {
				saledOrderProduct = new SaledOrderProduct();
				saledOrderProduct.setSaledOrderId(order.getId());
				saledOrderProduct.setId(rs.getInt("id"));
				saledOrderProduct.setCount(rs.getInt("count"));
				saledOrderProduct.setPrePrice(rs.getDouble("pre_price"));
				saledOrderProduct.setEventRemark(rs.getString("event_remark"));
				saledOrderProduct.setProductName(rs.getString("name"));
				saledOrderProduct.setProductId(rs.getInt("product_id"));
				saledOrderProduct.setBarCode(rs.getString("bar_code"));
				order.getProductList().add(saledOrderProduct);
			}
			
			//获取订单金额来源
			ps = conn.prepareStatement("select ms.id,ms.order_id,ms.type,ms.money,ms.withdraw_money,ms.swip_card_number from money_source ms where ms.order_id=?");
			ps.setInt(1, order.getId());
			rs = ps.executeQuery();
			MoneySource moneySource;
			while(rs.next()){
				moneySource = new MoneySource();
				moneySource.setId(rs.getInt("id"));
				moneySource.setOrderId(rs.getInt("order_id"));
				moneySource.setType(rs.getInt("type"));
				moneySource.setMoney(rs.getDouble("money"));
				moneySource.setWithdrawMoney(rs.getDouble("withdraw_money"));
				moneySource.setSwipCardNumber(rs.getString("swip_card_number"));
				order.getMoneySourceList().add(moneySource);
			}
			
			//拼装JSON字符串
			Gson gson = new Gson();
			jsonResult.append("{\"serialId\":\"").append(order.getSerialNumber()).append("\",");
			jsonResult.append("\"price\":").append(order.getPrice()).append(",");
			jsonResult.append("\"score\":").append(order.getScore()).append(",");
			jsonResult.append("\"products\":").append(gson.toJson(order.getProductList())).append(",");
			jsonResult.append("\"moneySource\":").append(gson.toJson(order.getMoneySourceList()));
			jsonResult.append("}");
			log.info("getSaledProduct back data=="+jsonResult);
		} catch (Exception e) {
			log.error("获取销售商品信息时出现异常：", e);
		} finally {
			DbUtil.closeConnection(rs, ps, conn);
		}
		return jsonResult.toString();
	}
	
	/**
	 * 查询租赁商品信息
	 * @param json pos机传递的json数据
	 * @return json格式的查询结果
	 */
	public String getLeaseProduct(String json) {
		String jsonResult = "";
		// 解析pos机传递的json数据
		Map<String, Object> param = this.doGetProductJson(json);
		String barCode = (String) param.get("barCode"); // 商品条形码
		String memberId = (String) param.get("memberId"); // 会员id
		String serialNumber = (String) param.get("serialNumber"); // 订单流水号
		
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder json_sb = new StringBuilder(50);
		try {
			json_sb.append("{\"products\":[");

			ps = conn.prepareStatement("select d.id,d.serial_number,s.id,t.count,t.pre_price,t.per_deposit,t.start_time,t.end_time,t.time_length,t.lease_style,s.lease_price from lease_order d, lease_order_product t, product s where t.product_id = s.id and s.bar_code = ? and t.lease_order_id = d.id and d.serial_number = ? and d.member_id = ? ");
			ps.setString(1, barCode);
			ps.setString(2, serialNumber);
			ps.setString(3, memberId);
			rs = ps.executeQuery();
			while (rs.next()) {
				json_sb.append("{\"serialId\":\"" + rs.getString(2) + "\",");
				json_sb.append("\"productId\":" + rs.getInt(3) + ",");
				json_sb.append("\"count\":" + rs.getInt(4) + ",");
				json_sb.append("\"prePrice\":" + rs.getDouble(5) + ",");
				json_sb.append("\"perDeposit\":"+rs.getDouble(6) + ",");
				json_sb.append("\"startTime\":"+rs.getTimestamp(7).getTime()+",");
				json_sb.append("\"endTime\":"+rs.getTimestamp(8).getTime()+",");
				json_sb.append("\"timeLength\":"+rs.getInt(9)+",");
				json_sb.append("\"leaseStyle\":"+rs.getInt(10)+",");
				json_sb.append("\"todayLeasePrice\":"+rs.getDouble(11)+"},");
			}
			if(json_sb.length() > 13){
			   json_sb.delete(json_sb.length() - 1, json_sb.length());
			}
			json_sb.append("]}");
			jsonResult = json_sb.toString();
			System.out.println(jsonResult);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

				}
			}
		}

		
	
		return jsonResult;
	}
	
	/**
	 * 解析POS机传递的[查询销售商品信息]或[查询租赁商品信息]接口的数据
	 * @param json POS机传递的JSON数据
	 * @return barCode:商品条形码; memberId:会员id; serialNumber:订单流水号
	 */
	private Map<String, Object> doGetProductJson(String json) {
		Map<String, Object> param = new HashMap<String, Object>();
		String barCode = null; //商品条形码
		String memberId = null; //会员id
		String serialNumber = null; //订单流水号
		try {
			JsonReader jr = new JsonReader(new StringReader(json));
			jr.beginObject();
			String attrName = null;
			while (jr.hasNext()) {
				attrName = jr.nextName();
				if ("barCode".equals(attrName)) {
					barCode = jr.nextString();
				} else if ("memberId".equals(attrName)) {
					memberId = jr.nextString();
				} else if ("serialId".equals(attrName)) {
					serialNumber = jr.nextString();
				}
			}
			jr.endObject();
		} catch (Exception e) {
			log.error("解析POS机传递的[查询销售商品信息]或[查询租赁商品信息]接口的数据时出现异常：", e);
		} 
		param.put("barCode", barCode);
		param.put("memberId", memberId);
		param.put("serialNumber", serialNumber);
		return param;
	}
	
	/**
	 * 生成本地租赁记录json信息同步到中心库
	 * @param leaseOrderLastSubmitTime 租赁订单上次提交时间
	 * @return
	 */
	private String doCreateLeaseJsonToCenter(Timestamp leaseOrderLastSubmitTime){
		StringBuilder leasestr = new StringBuilder();
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		try{
			//获取店面编号
			String shopCode = SystemConfig.getInstance().getShopCode();
			
			ps = conn.prepareStatement("select lo.cashier_id,lo.create_time,lo.deposit,lo.id,lo.member_id,lo.order_type,lo.pos_code,lo.price,lo.serial_number from lease_order lo where lo.create_time >?");
			ps.setTimestamp(1, leaseOrderLastSubmitTime);
			rs = ps.executeQuery();
			leasestr.append("[");
			while(rs.next()){
				leasestr.append("{\"leaseOrderId\":").append(rs.getInt("id")).append(",");
				leasestr.append("\"serialNumber\":\"").append(rs.getString("serial_number")).append("\",");
				leasestr.append("\"posCode\":\"").append(rs.getString("pos_code")).append("\",");
				leasestr.append("\"cashierId\":").append(rs.getInt("cashier_id")).append(",");
				leasestr.append("\"memberId\":\"").append(rs.getString("member_id")).append("\",");
				leasestr.append("\"price\":").append(rs.getDouble("price")).append(",");
				leasestr.append("\"deposit\":").append(rs.getDouble("deposit")).append(",");
				leasestr.append("\"orderType\":").append(rs.getInt("order_type")).append(",");
				leasestr.append("\"createTime\":\"").append(sdf.format(rs.getTimestamp("create_time"))).append("\",");
				leasestr.append("\"shopCode\":\"").append(shopCode).append("\",");
				ps2 = conn.prepareStatement("select lop.count,lop.end_time,lop.lease_order_id,lop.lease_style,lop.per_deposit,lop.pre_price,lop.product_id,lop.start_time,lop.time_length from lease_order_product lop where lop.lease_order_id = "+rs.getInt("id"));
				rs2 =ps2.executeQuery();
				leasestr.append("\"shopLeaseOrderProduct\":[");
				while(rs2.next()){
					leasestr.append("{\"productId\":").append(rs2.getInt("product_id")).append(",");
					leasestr.append("\"leaseOrderId\":").append(rs2.getInt("lease_order_id")).append(",");
					leasestr.append("\"count\":").append(rs2.getInt("count")).append(",");
					leasestr.append("\"prePrice\":").append(rs2.getDouble("pre_price")).append(",");
					leasestr.append("\"perDeposit\":").append(rs2.getDouble("per_deposit")).append(",");
					if(rs2.getTimestamp("start_time") != null){
						leasestr.append("\"startTime\":\"").append(sdf.format(rs2.getTimestamp("start_time"))).append("\",");
					}
					if(rs2.getTimestamp("end_time") != null){
						leasestr.append("\"endTime\":\"").append(sdf.format(rs2.getTimestamp("end_time"))).append("\",");
					}
					leasestr.append("\"timeLength\":").append(rs2.getDouble("time_length")).append(",");
					leasestr.append("\"leaseStyle\":").append(rs2.getInt("lease_style")).append(",");
					leasestr.append("\"shopCode\":\"").append(shopCode).append("\"},");
					if(rs2.isLast()){
						leasestr.deleteCharAt(leasestr.length()-1);
					}
				}
				leasestr.append("]");
				leasestr.append("},");
				if(rs.isLast()){
					leasestr.deleteCharAt(leasestr.length()-1);
				}
			}
			leasestr.append("]");
		}catch(Exception e){
			log.error("生成本地租赁记录json信息同步到中心库时出现异常：", e);
		}finally{
			DbUtil.closeConnection(rs, ps, null);
			DbUtil.closeConnection(rs2, ps2, conn);
		}
		return leasestr.toString();
	}
	
	
	/**
	 * 生成本地租赁记录json信息同步到中心库
	 * @param saledOrderLastSubmitTime 销售订单上次提交时间
	 * @return
	 */
	private String doCreateSaledJsonToCenter(Timestamp saledOrderLastSubmitTime){
		StringBuilder  salestr = new StringBuilder();
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		try{
			//获取店面编号
			String shopCode = SystemConfig.getInstance().getShopCode();
			
			ps = conn.prepareStatement("select so.cashier_id,so.id,so.member_id,so.order_type,so.pos_code,so.price,so.saled_time,so.serial_number from saled_order so where so.saled_time > ?");
			ps.setTimestamp(1, saledOrderLastSubmitTime);
			rs = ps.executeQuery();
			salestr.append("[");
			while(rs.next()){
				salestr.append("{\"saledOrderId\":").append(rs.getInt("id")).append(",");
				salestr.append("\"serialNumber\":\"").append(rs.getString("serial_number")).append("\",");
				salestr.append("\"posCode\":\"").append(rs.getString("pos_code")).append("\",");
				salestr.append("\"cashierId\":").append(rs.getInt("cashier_id")).append(",");
				salestr.append("\"memberId\":\"").append(rs.getString("member_id")).append("\",");
				salestr.append("\"price\":").append(rs.getDouble("price")).append(",");
				salestr.append("\"orderType\":").append(rs.getInt("order_type")).append(",");
				salestr.append("\"saledTime\":\"").append(sdf.format(rs.getTimestamp("saled_time"))).append("\",");
				salestr.append("\"shopCode\":\"").append(shopCode).append("\",");
				ps2 = conn.prepareStatement("SELECT sop.count,sop.pre_price,sop.product_id,sop.saled_order_id,sop.event_remark from saled_order_product sop where sop.saled_order_id = "+rs.getInt("id"));
				rs2 =ps2.executeQuery();
				salestr.append("\"shopSaledOrderProduct\":[");
				while(rs2.next()){
					salestr.append("{\"productId\":").append(rs2.getInt("product_id")).append(",");
					salestr.append("\"saledOrderId\":").append(rs2.getInt("saled_order_id")).append(",");
					salestr.append("\"count\":").append(rs2.getInt("count")).append(",");
					salestr.append("\"eventRemark\":\"").append(rs2.getString("event_remark")).append("\",");
					salestr.append("\"prePrice\":").append(rs2.getDouble("pre_price")).append(",");
					salestr.append("\"shopCode\":\"").append(shopCode).append("\"},");
					if(rs2.isLast()){
						salestr.deleteCharAt(salestr.length()-1);
					}
				}
				salestr.append("],");
				ps2 = conn.prepareStatement("select ms.money,ms.order_id,ms.swip_card_number,ms.type from money_source ms  where ms.order_id = "+rs.getInt("id"));
				rs2 = ps2.executeQuery();
				salestr.append("\"shopMoneySource\":[");
				while(rs2.next()){
					if(!rs2.isFirst()) {
						salestr.append(",");
					}
					salestr.append("{\"money\":").append(rs2.getDouble("money")).append(",");
					salestr.append("\"swipCardNumber\":\"").append(rs2.getString("swip_card_number")).append("\",");
					salestr.append("\"orderId\":").append(rs2.getInt("order_id")).append(",");
					salestr.append("\"type\":").append(rs2.getInt("type")).append("}");
				}
				salestr.append("],");
				//退款去向列表
				ps2 = conn.prepareStatement("select md.money,md.order_id,md.type from money_destination md where md.order_id = "+rs.getInt("id"));
				rs2 = ps2.executeQuery();
				salestr.append("\"shopMoneyDestinationList\":[");
				while(rs2.next()){
					if(!rs2.isFirst()) {
						salestr.append(",");
					}
					salestr.append("{\"money\":").append(rs2.getDouble("money")).append(",");
					salestr.append("\"orderId\":").append(rs2.getInt("order_id")).append(",");
					salestr.append("\"type\":").append(rs2.getInt("type")).append("}");
				}
				salestr.append("],");
				salestr.append("\"shopSaledOrderEventList\":[");
				//销售订单参与活动列表
				ps2 = conn.prepareStatement("select soe.id,soe.saled_order_id,soe.detail_event_id,soe.event_id,soe.ext_id from saled_order_event soe where soe.saled_order_id = ? ");
				ps2.setInt(1, rs.getInt("id"));
				rs2 = ps2.executeQuery();
				while(rs2.next()){
					if(!rs2.isFirst()){
						salestr.append(",");
					}
					salestr.append("{\"shopEventId\":").append(rs2.getInt("id")).append(",");
					salestr.append("\"saledOrderId\":").append(rs2.getInt("saled_order_id")).append(",");
					salestr.append("\"eventId\":").append(rs2.getInt("event_id")).append(",");
					salestr.append("\"detailEventId\":").append(rs2.getInt("detail_event_id")).append(",");
					salestr.append("\"extId\":").append(rs2.getInt("ext_id")).append("}");
				}
				salestr.append("]");
				salestr.append("},");
				if(rs.isLast()){
					salestr.deleteCharAt(salestr.length()-1);
				}
			}
			salestr.append("]");
		}catch(Exception e){
			log.error("生成本地租赁记录json信息同步到中心库时出现异常：", e);
		}finally{
			DbUtil.closeConnection(rs, ps, null);
			DbUtil.closeConnection(rs2, ps2, conn);
		}
		return salestr.toString();
	}
	
	/**
	 * 向中心库同步租赁订单信息
	 * @throws Exception 
	 */
	public void syncLeaseOrderInfoToCenter() throws Exception {
		try {
			//获取租赁订单上次提交时间
			SystemConfig systemConfig = SystemConfig.getInstance();
			Timestamp leaseOrderLastSubmitTime = systemConfig.getLeaseOrderLastSubmitTime();
			
			//提交数据
			String json = this.doCreateLeaseJsonToCenter(leaseOrderLastSubmitTime);
			log.info("syncLeaseOrderInfoToCenter data=="+json);
			
			//没有需要提交的数据
			if("[]".equals(json)) {
				return ;
			}
			
			// 获取HTTP请求的响应数据
			String syncPoscenterLeaseOrderURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("syncPoscenterLeaseOrderURL");
			String backJson = HttpURLUtil.getResponseResult(syncPoscenterLeaseOrderURL, json);
			
			//修改同步时间
			if("ok".equals(backJson)){
				systemConfig.setLeaseOrderLastSubmitTime(new Timestamp(new Date().getTime()));
				new SystemConfigService().updateSystemConfig(systemConfig);
			}
		} catch (Exception e) {
			log.error("向中心库同步租赁订单信息异常：", e);
			throw e;
		}
	}

	/**
	 * 向中心库提交销售订单信息
	 * @throws Exception 
	 */
	public void syncSaledOrderInfoToCenter() throws Exception {
		try {
			//获取销售订单上次提交时间
			SystemConfig systemConfig = SystemConfig.getInstance();
			Timestamp saledOrderLastSubmitTime = systemConfig.getSaledOrderLastSubmitTime();
			
			//提交数据
			String json = this.doCreateSaledJsonToCenter(saledOrderLastSubmitTime);
			log.info("syncSaledOrderInfoToCenter data=="+json);
			
			//没有需要提交的数据
			if("[]".equals(json)) {
				return ;
			}
			
			// 获取HTTP请求的响应数据
			String syncPoscenterSaledOrderURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("syncPoscenterSaledOrderURL");
			String backJson = HttpURLUtil.getResponseResult(syncPoscenterSaledOrderURL, json);
			
			//修改同步时间
			if("ok".equals(backJson)){
				systemConfig.setSaledOrderLastSubmitTime(new Timestamp(new Date().getTime()));
				new SystemConfigService().updateSystemConfig(systemConfig);
			}
		} catch (Exception e) {
			log.error("向中心库同步销售订单信息异常：", e);
			throw e;
		}
	}

}
