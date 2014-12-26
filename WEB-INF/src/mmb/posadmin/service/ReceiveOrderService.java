package mmb.posadmin.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import mmb.posadmin.action.Page;
import mmb.posadmin.domain.Invoice;
import mmb.posadmin.domain.ReceiveOrder;
import mmb.posadmin.domain.ReceiveOrderItem;
import mmb.posadmin.domain.SystemConfig;
import mmb.posadmin.util.HttpURLUtil;
import mmboa.util.Constants;
import mmboa.util.LogUtil;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.stream.JsonReader;

public class ReceiveOrderService extends BaseService {
	
	private static Logger log = Logger.getLogger(ReceiveOrderService.class);
	
	/**
	 * 分页获取收货单列表信息
	 * @param page 分页信息
	 * @param param [startTime:开始时间；endTime:结束时间；receiveOrder:收货单对象]
	 * @return
	 */
	public Page<ReceiveOrder> getReceiveOrderPage(Page<ReceiveOrder> page, Map<String, Object> param) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 查询条件
			StringBuilder condSql = new StringBuilder();
			Timestamp startTime = (Timestamp) param.get("startTime");
			Timestamp endTime = (Timestamp) param.get("endTime");
			ReceiveOrder order = (ReceiveOrder) param.get("receiveOrder");
			if (StringUtils.isNotBlank(order.getOrderNumber())) {
				condSql.append(" and o.order_number like ? ");
			}
			if (StringUtils.isNotBlank(order.getCharger())) {
				condSql.append(" and o.charger like ? ");
			}
			if (order.getUseStatus() != -1) {
				condSql.append(" and o.use_status=? ");
			}
			if (startTime != null) {
				condSql.append(" and o.create_time>=? ");
			}
			if (endTime != null) {
				condSql.append(" and o.create_time<=? ");
			}

			// 查询总记录数
			int index = 1;
			ps = conn.prepareStatement("select count(o.id) from receive_order o where 1=1 " + condSql);
			if (StringUtils.isNotBlank(order.getOrderNumber())) {
				ps.setString(index++, "%" + order.getOrderNumber() + "%");
			}
			if (StringUtils.isNotBlank(order.getCharger())) {
				ps.setString(index++, "%" + order.getCharger() + "%");
			}
			if (order.getUseStatus() != -1) {
				ps.setInt(index++, order.getUseStatus());
			}
			if (startTime != null) {
				ps.setTimestamp(index++, startTime);
			}
			if (endTime != null) {
				ps.setTimestamp(index++, endTime);
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				page.setTotalRecords(rs.getInt(1));
			}

			// 获取列表数据
			if (page.getTotalRecords() > 0) {
				List<ReceiveOrder> list = new ArrayList<ReceiveOrder>();
				ReceiveOrder ro;
				StringBuilder sql = new StringBuilder(50);
				sql.append("select o.id,o.order_number,o.charger,o.use_status,o.create_time from receive_order o");
				sql.append(" where 1=1 ").append(condSql);
				sql.append(" order by o.id desc limit ");
				sql.append(page.getFirstResult()).append(",").append(page.getPageCount());
				ps = conn.prepareStatement(sql.toString());
				index = 1;
				if (StringUtils.isNotBlank(order.getOrderNumber())) {
					ps.setString(index++, "%" + order.getOrderNumber() + "%");
				}
				if (StringUtils.isNotBlank(order.getCharger())) {
					ps.setString(index++, "%" + order.getCharger() + "%");
				}
				if (order.getUseStatus() != -1) {
					ps.setInt(index++, order.getUseStatus());
				}
				if (startTime != null) {
					ps.setTimestamp(index++, startTime);
				}
				if (endTime != null) {
					ps.setTimestamp(index++, endTime);
				}
				rs = ps.executeQuery();
				while (rs.next()) {
					ro = new ReceiveOrder();
					ro.setId(rs.getInt("id"));
					ro.setOrderNumber(rs.getString("order_number"));
					ro.setCharger(rs.getString("charger"));
					ro.setCreateTime(rs.getTimestamp("create_time"));
					ro.setUseStatus(rs.getInt("use_status"));
					list.add(ro);
				}
				page.setList(list);
			}
		} catch (Exception e) {
			log.error("分页获取采购单列表信息时出现异常：", e);
		} finally {
			DbUtil.closeConnection(rs, ps, conn);
		}
		return page;
	}

	/**
	 * 根据收货单id获取收货单对象信息
	 * @param id 收货单id
	 * @return
	 */
	public ReceiveOrder getReceiveOrderById(int id) {
		return (ReceiveOrder) this.getXXX("`id`="+id, "receive_order", ReceiveOrder.class.getName());
	}
	
	/**
	 * 保存从中心库同步的发货单信息
	 * @param json
	 * @return 是否保存成功
	 */
	private boolean saveSendOrder(String json) {
		boolean success = false;
		//解析json字符串
		List<ReceiveOrder> orderList = this.doSendOrderJson(json);
		
		//没有需要同步的发货单
		if(orderList==null || orderList.isEmpty()) {
			return true;
		}
		
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		boolean oldAutoCommit = true;
		try {
			//记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			//关闭自动提交事务
			conn.setAutoCommit(false);
			
			for(ReceiveOrder order : orderList) {
				//保存收货单信息
				ps = conn.prepareStatement("insert into receive_order(`send_order_id`,`create_time`,`use_status`,order_number) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, order.getSendOrderId());
				ps.setTimestamp(2, new Timestamp(new Date().getTime()));
				ps.setInt(3, 0);
				ps.setString(4,"SH".concat( order.getOrderNumber().substring(2,10)));
				ps.executeUpdate();
				
				//获取订单id
				int orderId = 0;
				ResultSet rs = ps.getGeneratedKeys();
				if(rs.next()) {
					orderId = rs.getInt(1);
				}
				
				//批量保存收货单条目
				ps = conn.prepareStatement("insert into receive_order_item(`order_id`,`product_id`,`send_count`) values (?,?,?)");
				for(ReceiveOrderItem item : order.getItemList()) {
					ps.setInt(1, orderId);
					ps.setInt(2, item.getProductId());
					ps.setInt(3, item.getSendCount());
					ps.addBatch();
				}
				
				ps.executeBatch();
			}
			
			//修改中心库的发货单的状态
			List<Integer> orderIdList = new ArrayList<Integer>();
			for(ReceiveOrder order : orderList) {
				orderIdList.add(order.getSendOrderId());
			}
			this.syncSendOrderStatusFromPoscenter(orderIdList);
			
        	conn.commit();
			success = true;
		} catch (Exception e) {
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			LogUtil.logAccess("保存从中心库同步的发货单信息时出现错误："+e.getMessage());
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
	 * 解析poscenter发送的json格式的发货单数据
	 * @param json
	 * @return
	 */
	public List<ReceiveOrder> doSendOrderJson(String json) {
		List<ReceiveOrder> orderList = new ArrayList<ReceiveOrder>();
		ReceiveOrder order = new ReceiveOrder();
		ReceiveOrderItem item;
		try {
			JsonReader jr = new JsonReader(new StringReader(json));
			jr.beginObject();
			String attrName = null;
			while (jr.hasNext()) {
				attrName = jr.nextName();
				if ("sendOrderList".equals(attrName)) {
					jr.beginArray();
					while (jr.hasNext()) {
						jr.beginObject();
						//收货单
						order = new ReceiveOrder();
						while (jr.hasNext()) {
							attrName = jr.nextName();
							if ("sendOrderId".equals(attrName)) { //发货单id
								order.setSendOrderId(jr.nextInt());
							} else if ("orderNumber".equals(attrName)) { //发货单号
								order.setOrderNumber(jr.nextString());
							} else if ("items".equals(attrName)) {
								jr.beginArray();
								while (jr.hasNext()) {
									item = new ReceiveOrderItem();
									jr.beginObject();
									while (jr.hasNext()) {
										attrName = jr.nextName();
										if ("id".equals(attrName)) { //条目id
											item.setId(jr.nextInt());
										} else if ("productId".equals(attrName)) { //商品id
											item.setProductId(jr.nextInt());
										} else if ("count".equals(attrName)) { //商品数量
											item.setSendCount(jr.nextInt());
										}
									}
									jr.endObject();
									order.getItemList().add(item);
								}
								jr.endArray();
							}
						}
						orderList.add(order);
						jr.endObject();
					}
					jr.endArray();
				}
			}
			jr.endObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderList;
	}

	/**
	 * 确认收货
	 * @param receiveOrder 收货单
	 * @param itemList 收货单条目
	 * @throws Exception 
	 */
	public void confirmReceive(ReceiveOrder receiveOrder, List<ReceiveOrderItem> itemList) throws Exception {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean oldAutoCommit = true;
		try {
			//记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			//关闭自动提交事务
			conn.setAutoCommit(false);
			
			//保存收货单信息
			ps = conn.prepareStatement("update receive_order set order_number=?, charger=?,use_status=? where id=?");
			ps.setString(1, receiveOrder.getOrderNumber());
			ps.setString(2, receiveOrder.getCharger());
			ps.setInt(3, 1);
			ps.setInt(4, receiveOrder.getId());
			ps.executeUpdate();
			
			//批量保存收货单条目的实际收货量
        	ps = conn.prepareStatement("update receive_order_item set receive_count=? where id=?");
        	for(ReceiveOrderItem item : itemList) {
        		ps.setInt(1, item.getReceiveCount());
        		ps.setInt(2, item.getId());
        		ps.addBatch();
        	}
        	ps.executeBatch();
			
			//查看确认收货前的库存信息，生成进销存记录
			List<Invoice> invoiceList = new ArrayList<Invoice>();
			Invoice invoice = new Invoice();
			StringBuilder sql = new StringBuilder();
			sql.append("select id,stock from product where id in(");
			for(ReceiveOrderItem item : itemList) {
				sql.append(item.getProductId()).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(")");
        	ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				invoice = new Invoice();
				invoice.setOperType("0");
				invoice.setSerialNumber(receiveOrder.getOrderNumber());
				invoice.setProductId(rs.getInt("id"));
				invoice.setBeforeCount(rs.getInt("stock"));
				invoice.setAfterCount(invoice.getBeforeCount());
				for(ReceiveOrderItem item : itemList) {
					if(item.getProductId() == invoice.getProductId()) {
						invoice.setAfterCount(invoice.getAfterCount() + item.getReceiveCount()); //处理收货单商品重复的问题
						invoice.setCount(invoice.getCount() + item.getReceiveCount());
					}
				}
				invoiceList.add(invoice);
			}
			new InvoiceService().addBatInvoice(conn, invoiceList);
			
        	//更新商品库存信息
			ps = conn.prepareStatement("update product set stock=stock+? where id=?");
			for(ReceiveOrderItem item : itemList) {
				ps.setInt(1, item.getReceiveCount());
				ps.setInt(2, item.getProductId());
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
			log.error("确认收货时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(rs, ps, null);
			if(conn != null){
				try {
					//还原事务提交方式
					conn.setAutoCommit(oldAutoCommit);
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	/**
	 * 从中心库同步发货单信息
	 * @throws Exception
	 */
	public void syncSendOrderFromPoscenter() throws Exception {
		//请求数据
		String shopCode = SystemConfig.getInstance().getShopCode();
		String requestContent = "{\"shopCode\":\""+shopCode+"\"}";
		
		//发送请求并接收返回结果
		String syncPoscenterSendOrderURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("syncPoscenterSendOrderURL");
		String json = HttpURLUtil.getResponseResult(syncPoscenterSendOrderURL, requestContent);
		
		//保存从中心库同步的发货单信息
		this.saveSendOrder(json);
	}
	
	/**
	 * 修改中心库发货单状态信息
	 * @param orderIdList 中心库发货单id
	 * @throws Exception
	 */
	private void syncSendOrderStatusFromPoscenter(List<Integer> orderIdList) throws Exception {
		//拼装JSON字符串
		StringBuilder json = new StringBuilder();
		json.append("{\"sendOrderIdList\":[");
		for(int i=0; i<orderIdList.size(); i++) {
			Integer orderId = orderIdList.get(i);
			json.append(i>0 ? "," : "");
			json.append("{\"sendOrderId\":"+orderId+"}");
		}
		json.append("]}");
		
		//发送请求并接收返回结果
		String syncPoscenterSendOrderStatusURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("syncPoscenterSendOrderStatusURL");
		String result = HttpURLUtil.getResponseResult(syncPoscenterSendOrderStatusURL, json.toString());
		
		boolean success = "true".equalsIgnoreCase(result);
		if(!success) {
			throw new Exception("修改中心库发货单状态信息时出现异常！");
		}
	}

	/**
	 * 导出Excel收货单
	 * @param order 收货单对象
	 * @return excel文件字节数据
	 */
	public byte[] exportExcel(ReceiveOrder order) {
		byte[] data = null;
		
		//获取所有收货单条目信息
		ReceiveOrderItemService itemService = new ReceiveOrderItemService();
		List<ReceiveOrderItem> allItemList = itemService.getAllItemListByOrderId(order.getId());
		
		WritableWorkbook book = null;
		ByteArrayOutputStream baos = null;
		try {
			//把工作薄保存到内存中
			baos = new ByteArrayOutputStream();
			book = Workbook.createWorkbook(baos);
			WritableSheet sheet = book.createSheet("收货单信息", 0);
			
			//设置列宽
			sheet.setColumnView(0, 15);
			sheet.setColumnView(1, 35);
			sheet.setColumnView(2, 15);
			sheet.setColumnView(3, 15);
			
			//采购单信息
			WritableCellFormat format = new WritableCellFormat();
			format.setAlignment(Alignment.RIGHT);
			sheet.addCell(new Label(0, 0, "收货单号：", format));
			sheet.addCell(new Label(0, 1, "负责人：", format));
			sheet.addCell(new Label(0, 2, "创建时间：", format));
			sheet.addCell(new Label(1, 0, order.getOrderNumber()));
			sheet.addCell(new Label(1, 1, order.getCharger()));
			sheet.addCell(new Label(1, 2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreateTime())));
			
			//条目信息
			WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD); 
			format = new WritableCellFormat(font);
			format.setAlignment(Alignment.CENTRE);
			sheet.addCell(new Label(0, 3, "序号", format));
			sheet.addCell(new Label(1, 3, "商品名称", format));
			sheet.addCell(new Label(2, 3, "发货数量", format));
			sheet.addCell(new Label(3, 3, "实收数量", format));
			format = new WritableCellFormat();
			format.setAlignment(Alignment.CENTRE);
			for(int i=0; i<allItemList.size(); i++) {
				ReceiveOrderItem item = allItemList.get(i);
				sheet.addCell(new jxl.write.Number(0, 4+i, i+1, format));
				sheet.addCell(new Label(1, 4+i, item.getProduct().getName()));
				sheet.addCell(new jxl.write.Number(2, 4+i, item.getSendCount()));
				sheet.addCell(new jxl.write.Number(3, 4+i, item.getReceiveCount()));
			}
			
			//写入数据并关闭文件
			book.write();
			book.close();
			
			//转化为字节数组
			data = baos.toByteArray();
		} catch (Exception e) {
			log.error("导出Excel收货单时出现异常：", e);
		} finally {
			if(baos != null) {
				try {
					baos.close();
					baos = null;
				} catch (IOException e) {}
			}
		}
		
		return data;
	}
	
}
