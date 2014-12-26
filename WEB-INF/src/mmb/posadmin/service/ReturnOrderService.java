package mmb.posadmin.service;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import mmb.posadmin.action.Page;
import mmb.posadmin.domain.Invoice;
import mmb.posadmin.domain.Product;
import mmb.posadmin.domain.ReturnOrder;
import mmb.posadmin.domain.ReturnOrderItem;
import mmb.posadmin.domain.SystemConfig;
import mmb.posadmin.util.HttpURLUtil;
import mmboa.util.Constants;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ReturnOrderService extends BaseService {
	
	private static Logger log = Logger.getLogger(ReturnOrderService.class);
	
	/**
	 * 分页获取退货单列表信息
	 * @param page 分页信息
	 * @param param [startTime:开始时间；endTime:结束时间；returnOrder:退货单对象]
	 * @return
	 */
	public Page<ReturnOrder> getReturnOrderPage(Page<ReturnOrder> page, Map<String, Object> param) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			//查询条件
			StringBuilder condSql = new StringBuilder();
			Timestamp startTime = (Timestamp) param.get("startTime");
			Timestamp endTime = (Timestamp) param.get("endTime");
			ReturnOrder order = (ReturnOrder) param.get("returnOrder");
			if(StringUtils.isNotBlank(order.getOrderNumber())) {
				condSql.append(" and p.order_number like ? ");
			}
			if(StringUtils.isNotBlank(order.getCharger())) {
				condSql.append(" and p.charger like ? ");
			}
			if(order.getUseStatus() != -1) {
				condSql.append(" and p.use_status=? ");
			}
			if(startTime != null) {
				condSql.append(" and p.create_time>=? ");
			}
			if(endTime != null) {
				condSql.append(" and p.create_time<=? ");
			}
			
			//查询总记录数
			int index = 1;
			ps = conn.prepareStatement("select count(p.id) from return_order p where 1=1 " + condSql);
			if(StringUtils.isNotBlank(order.getOrderNumber())) {
				ps.setString(index++, "%"+order.getOrderNumber()+"%");
			}
			if(StringUtils.isNotBlank(order.getCharger())) {
				ps.setString(index++, "%"+order.getCharger()+"%");
			}
			if(order.getUseStatus() != -1) {
				ps.setInt(index++, order.getUseStatus());
			}
			if(startTime != null) {
				ps.setTimestamp(index++, startTime);
			}
			if(endTime != null) {
				ps.setTimestamp(index++, endTime);
			}
			rs = ps.executeQuery();
		    if(rs.next()){
		    	page.setTotalRecords(rs.getInt(1));
		    }
		    
		    //获取列表数据
		    if(page.getTotalRecords() > 0) {
		    	List<ReturnOrder> list = new ArrayList<ReturnOrder>();
		    	ReturnOrder p;
		    	StringBuilder sql = new StringBuilder(50);
		    	sql.append("SELECT p.id,p.order_number,p.charger,p.create_time,p.use_status from return_order p ");
		    	sql.append(" where 1=1 ").append(condSql);
		    	sql.append(" order by p.id desc limit ");
		    	sql.append(page.getFirstResult()).append(",").append(page.getPageCount());
		    	ps = conn.prepareStatement(sql.toString());
		    	index = 1;
		    	if(StringUtils.isNotBlank(order.getOrderNumber())) {
		    		ps.setString(index++, "%"+order.getOrderNumber()+"%");
				}
				if(StringUtils.isNotBlank(order.getCharger())) {
					ps.setString(index++, "%"+order.getCharger()+"%");
				}
				if(order.getUseStatus() != -1) {
					ps.setInt(index++, order.getUseStatus());
				}
				if(startTime != null) {
					ps.setTimestamp(index++, startTime);
				}
				if(endTime != null) {
					ps.setTimestamp(index++, endTime);
				}
		    	rs = ps.executeQuery();
		    	while(rs.next()){
		    		p = new ReturnOrder();
		    		p.setId(rs.getInt("id"));
		    		p.setOrderNumber(rs.getString("order_number"));
		    		p.setCharger(rs.getString("charger"));
		    		p.setCreateTime(rs.getTimestamp("create_time"));
		    		p.setUseStatus(rs.getInt("use_status"));
		    		list.add(p);
		    	}
		    	page.setList(list);
		    }
		}catch(Exception e){
			log.error("分页获取退货单列表信息时出现异常：", e);
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return page;
	}

	/**
	 * 根据退货单id获取退货单对象信息
	 * @param id 退货单id
	 * @return
	 */
	public ReturnOrder getReturnOrderById(int id) {
		return (ReturnOrder) this.getXXX("`id`="+id, "return_order", ReturnOrder.class.getName());
	}
	
	/**
	 * 更新退货单信息
	 * @param returnOrder 退货单对象
	 * @return
	 */
	public boolean updateReturnOrder(ReturnOrder returnOrder) {
		//更新语句
		StringBuilder set = new StringBuilder();
		set.append("`charger`='").append(returnOrder.getCharger()).append("', ");
		set.append("`use_status`=").append(returnOrder.getUseStatus());
		return this.updateXXX(set.toString(), "`id`="+returnOrder.getId(), "return_order");
	}
	
	/**
	 * 删除退货单信息，同时删除该退货单下的所有条目
	 * @param id 退货单id
	 * @return
	 */
	public boolean deleteReturnOrderById(int id) {
		boolean success = false;
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		boolean oldAutoCommit = true;
		try {
			//记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			//关闭自动提交事务
			conn.setAutoCommit(false);
			
			//删除退货单条目数据
			ps = conn.prepareStatement("delete from return_order_item where return_order_id=?");
			ps.setInt(1, id);
			ps.executeUpdate();
			
			//删除退货单数据
			ps = conn.prepareStatement("delete from return_order where id=?");
			ps.setInt(1, id);
			ps.executeUpdate();
			
        	conn.commit();
			success = true;
		} catch (Exception e) {
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("删除退货单时出现异常：", e);
		} finally {
			DbUtil.closeConnection(null, ps, null);
			if(conn != null){
				try {
					//还原事务提交方式
					conn.setAutoCommit(oldAutoCommit);
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		return success;
	}

	/**
	 * 提交退货单
	 * @param id 退货单id
	 */
	public boolean submitReturnOrder(int id) {
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
			
			//更新退货单的状态字段
			ps = conn.prepareStatement("update return_order set use_status=1 where id=?");
			ps.setInt(1, id);
			ps.executeUpdate();
			
			//获取退货单信息
			ReturnOrder returnOrder = this.getReturnOrderById(id);
			
			//获取所有退货单条目
			ReturnOrderItemService sois = new ReturnOrderItemService();
			List<ReturnOrderItem> itemList = sois.getAllItemListByOrderId(id);
			
			//向中心库发送退货信息
			this.submitReturnOrderToCenter(returnOrder, itemList);
			
			//查看进销存前的库存信息，并生成进销存记录
			List<Invoice> invoiceList = new ArrayList<Invoice>();
			Invoice invoice = new Invoice();
			StringBuilder sql = new StringBuilder();
			sql.append("select id,stock from product where id in(");
			for(ReturnOrderItem item : itemList) {
				sql.append(item.getProductId()).append(",");
			}
			sql.deleteCharAt(sql.length()-1).append(")");
        	ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				invoice = new Invoice();
				invoice.setOperType("6"); //6:退货到中心库
				invoice.setSerialNumber(returnOrder.getOrderNumber());
				invoice.setProductId(rs.getInt("id"));
				invoice.setBeforeCount(rs.getInt("stock"));
				invoice.setAfterCount(invoice.getBeforeCount());
				for(ReturnOrderItem item : itemList) {
					if(item.getProductId() == invoice.getProductId()) {
						invoice.setAfterCount(invoice.getAfterCount()-item.getCount()); //处理退货单商品重复的问题
						invoice.setCount(invoice.getCount() + item.getCount());
					}
				}
				invoiceList.add(invoice);
			}
			new InvoiceService().addBatInvoice(conn, invoiceList);
			
			// 更新商品商品库存信息
			int count = 0;
			ps = conn.prepareStatement("update product set stock=stock-? where id=?");
			for(ReturnOrderItem item : itemList) {
				ps.setInt(1, item.getCount());
				ps.setInt(2, item.getProductId());
				ps.addBatch();
				count++;
				//批量执行
				if (count%100==0 || count==itemList.size()) {
					ps.executeBatch();
				}
			}
			
        	conn.commit();
			success = true;
		} catch (Exception e) {
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("提交退货单时出现异常：", e);
		} finally {
			DbUtil.closeConnection(null, ps, conn, oldAutoCommit);
		}
		return success;
	}
	
	/**
	 * 向中心库提交退货单数据
	 * @param returnOrder 退货单
	 * @param itemList 退货单条目
	 * @throws Exception
	 */
	private void submitReturnOrderToCenter(ReturnOrder returnOrder, List<ReturnOrderItem> itemList) throws Exception {
		//拼装JSON字符串
		StringBuilder submitJson = new StringBuilder();
		String shopCode = SystemConfig.getInstance().getShopCode();
		submitJson.append("{\"orderNumber\":\""+returnOrder.getOrderNumber()+"\",\"shop\":{\"code\":\""+shopCode+"\"},\"itemList\":[");
		for(int i=0; i<itemList.size(); i++) {
			ReturnOrderItem item = itemList.get(i);
			submitJson.append(i>0 ? "," : "");
			submitJson.append("{\"productId\":"+item.getProductId()+",\"returnCount\":"+item.getCount()+"}");
		}
		submitJson.append("]}");
		log.debug("submitReturnOrderToCenter data=="+submitJson);
		
		//发送请求并接收返回数据
		String submitReturnOrderToCenterURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("submitReturnOrderToCenterURL");
		String result = HttpURLUtil.getResponseResult(submitReturnOrderToCenterURL, submitJson.toString());
		boolean success = "true".equalsIgnoreCase(result.toString());
		if(!success) {
			throw new Exception("中心库操作失败！");
		}
	}

	/**
	 * 导入退货单
	 * @param excel 退货单文件
	 * @throws Exception 
	 */
	public void importReturnOrder(File excel) throws Exception {
		Workbook book = null;
		try {
			// 获得第一个工作表对象
			book = Workbook.getWorkbook(excel);
			Sheet sheet = book.getSheet(0);

			// 获取负责人
			ReturnOrder order = new ReturnOrder();
			order.setCharger(sheet.getCell(1, 0).getContents());
			
			// 获取订单条目信息
			List<ReturnOrderItem> itemList = new ArrayList<ReturnOrderItem>();
			ReturnOrderItem item;
			Cell[] row;
			System.out.println("sheet.getRows()=="+sheet.getRows());
			for (int i=2; i<sheet.getRows(); i++) {
				row = sheet.getRow(i);
				if(row.length < 2) {
					continue;
				}
				item = new ReturnOrderItem();
				String productName = row[0].getContents();
				if(StringUtils.isBlank(productName)) {
					continue;
				}
				Product product = new Product();
				product.setName(productName);
				item.setProduct(product);
				item.setCount(Integer.parseInt(row[1].getContents()));
				itemList.add(item);
			}
			
			//退货单条目为空或退货单文件格式错误
			if(itemList.isEmpty()) {
				throw new Exception("退货单条目为空或退货单文件格式错误");
			}
			
			//解析订单中的商品信息
			this.parseProduct(itemList);
			
			//过滤系统中无匹配的商品
			for(int i=itemList.size()-1; i>=0; i--) {
				ReturnOrderItem orderItem = itemList.get(i);
				if(orderItem.getProductId() == 0) {
					itemList.remove(i);
				}
			}
			
			//批量保存退货单
			this.batchSaveReturnOrder(order, itemList);
		} catch (Exception e) {
			log.error("导入退货单时出现异常：", e);
			throw e;
		} finally {
			if(book != null) {
				book.close();
			}
		}
	}
	
	/**
	 * 解析订单中的商品信息 <br/>
	 * 根据商品名称获取商品id
	 * @param itemList 退货单条目
	 */
	private void parseProduct(List<ReturnOrderItem> itemList) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 根据商品名称查找商品
			StringBuilder sql = new StringBuilder();
			sql.append("select p.id,p.`name` from product p where p.`name` in(");
			for (int i=0; i<itemList.size(); i++) {
				sql.append("?,");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");
			ps = conn.prepareStatement(sql.toString());
			int index = 1;
			for (ReturnOrderItem item : itemList) {
				ps.setString(index++, item.getProduct().getName());
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				int productId = rs.getInt("id");
				String productName = rs.getString("name");
				for (ReturnOrderItem item : itemList) {
					if (productName.equals(item.getProduct().getName())) {
						item.getProduct().setId(productId);
						item.setProductId(productId);
					}
				}
			}
		} catch (Exception e) {
			log.error("解析订单中的商品信息时出现异常：", e);
		} finally {
			DbUtil.closeConnection(rs, ps, conn);
		}
	}
	
	/**
	 * 批量保存退货单
	 * @param order 退货单对象
	 * @param itemList 退货单条目
	 */
	public void batchSaveReturnOrder(ReturnOrder order, List<ReturnOrderItem> itemList) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean oldAutoCommit = true;
		try {
			//记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			//关闭自动提交事务
			conn.setAutoCommit(false);
			
			//保存退货单
			ps = conn.prepareStatement("insert return_order(order_number,charger,use_status,create_time) values(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, "TH"+String.valueOf(new Date().getTime()).substring(2, 10));
			ps.setString(2, order.getCharger());
			ps.setInt(3, 0);
			ps.setTimestamp(4, new Timestamp(new Date().getTime()));
			ps.executeUpdate();
			
			//获取退货单id
			int orderId = 0;
			rs = ps.getGeneratedKeys();
        	if(rs.next()) {
        		orderId = rs.getInt(1);
        	}
			
			//保存退货单条目
			ps = conn.prepareStatement("insert return_order_item(return_order_id,product_id,count) values(?,?,?)");
			for(ReturnOrderItem item : itemList) {
				ps.setInt(1, orderId);
				ps.setInt(2, item.getProductId());
				ps.setInt(3, item.getCount());
				ps.addBatch();
			}
			ps.executeBatch();
			
        	conn.commit();
		} catch (Exception e) {
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("批量保存退货单时出现异常：", e);
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
	}
	
}
