package mmb.posadmin.service;

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

import mmb.posadmin.action.Page;
import mmb.posadmin.domain.AdjustPriceBill;
import mmb.posadmin.domain.Product;
import mmb.posadmin.domain.SystemConfig;
import mmb.posadmin.util.HttpURLUtil;
import mmboa.util.Constants;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbOperation;
import mmboa.util.db.DbUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class AdjustPriceBillService extends BaseService {
	
	private static Logger log = Logger.getLogger(AdjustPriceBillService.class);
	
	/**
	 * 分页获取调价单列表信息
	 * @param page 分页信息
	 * @param param 查询条件[billNumber:调价单号；productName:商品名称；auditStatus:审核状态；useStatus:使用状态]
	 * @return
	 */
	public Page<AdjustPriceBill> getAdjustPriceBillPage(Page<AdjustPriceBill> page, Map<String, Object> param) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 查询条件
			String billNumber = (String) param.get("billNumber");
			String productName = (String) param.get("productName");
			int auditStatus = (Integer) param.get("auditStatus");
			int useStatus = (Integer) param.get("useStatus");
			StringBuffer condSql = new StringBuffer();
			if (StringUtils.isNotBlank(billNumber)) {
				condSql.append(" and b.`bill_number` like ? ");
			}
			if (StringUtils.isNotBlank(productName)) {
				condSql.append(" and p.`name` like ? ");
			}
			if (auditStatus != 0) {
				condSql.append(" and b.audit_status = ? ");
			}
			if (useStatus != 0) {
				condSql.append(" and b.use_status = ? ");
			}

			// 查询总记录数
			int index = 1;
			ps = conn.prepareStatement("select count(b.id) from adjust_price_bill b join product p on b.product_id=p.id where 1=1 " + condSql);
			if (StringUtils.isNotBlank(billNumber)) {
				ps.setString(index++, "%" + billNumber + "%");
			}
			if (StringUtils.isNotBlank(productName)) {
				ps.setString(index++, "%" + productName + "%");
			}
			if (auditStatus != 0) {
				ps.setInt(index++, auditStatus);
			}
			if (useStatus != 0) {
				ps.setInt(index++, useStatus);
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				page.setTotalRecords(rs.getInt(1));
			}

			// 查询列表数据
			if (page.getTotalRecords() > 0) {
				StringBuilder sql = new StringBuilder(50);
				sql.append("select b.id,b.product_id,b.bill_number,b.target_price,b.audit_status,b.use_status,b.create_time,p.name,p.bar_code,p.sale_price,p.limit_price,p.lock_price");
				sql.append(" from adjust_price_bill b join product p on b.product_id=p.id");
				sql.append(" where 1=1 ").append(condSql);
				sql.append(" order by b.id desc limit ");
				sql.append(page.getFirstResult()).append(",").append(page.getPageCount());
				ps = conn.prepareStatement(sql.toString());
				index = 1;
				if (StringUtils.isNotBlank(billNumber)) {
					ps.setString(index++, "%" + billNumber + "%");
				}
				if (StringUtils.isNotBlank(productName)) {
					ps.setString(index++, "%" + productName + "%");
				}
				if (auditStatus != 0) {
					ps.setInt(index++, auditStatus);
				}
				if (useStatus != 0) {
					ps.setInt(index++, useStatus);
				}
				rs = ps.executeQuery();
				List<AdjustPriceBill> list = new ArrayList<AdjustPriceBill>();
				AdjustPriceBill bill;
				while (rs.next()) {
					bill = new AdjustPriceBill();
					bill.setId(rs.getInt("id"));
					bill.setBillNumber(rs.getString("bill_number"));
					bill.setTargetPrice(rs.getDouble("target_price"));
					bill.setAuditStatus(rs.getInt("audit_status"));
					bill.setUseStatus(rs.getInt("use_status"));
					bill.setCreateTime(rs.getTimestamp("create_time"));
					Product p = new Product();
					p.setId(rs.getInt("product_id"));
					p.setName(rs.getString("name"));
					p.setBarCode(rs.getString("bar_code"));
					p.setSalePrice(rs.getDouble("sale_price"));
					p.setLimitPrice(rs.getDouble("limit_price"));
			    	p.setLockPrice(rs.getDouble("lock_price"));
					bill.setProductId(p.getId());
					bill.setProduct(p);
					list.add(bill);
				}
				page.setList(list);
			}
		} catch (Exception e) {
			log.error("分页获取调价单列表信息时出现异常：", e);
		} finally {
			DbUtil.closeConnection(rs, ps, conn);
		}
		return page;
	}

	/**
	 * 根据调价单id获取调价单对象信息
	 * @param id 调价单id
	 * @return
	 */
	public AdjustPriceBill getAdjustPriceBillById(int id) {
		return (AdjustPriceBill) this.getXXX("`id`="+id, "adjust_price_bill", AdjustPriceBill.class.getName());
	}
	
	/**
	 * 根据调价单号获取调价单对象信息
	 * @param billNumber 调价单号
	 * @return
	 * @throws Exception 
	 */
	public AdjustPriceBill getAdjustPriceBillByBillNumber(String billNumber) throws Exception {
		AdjustPriceBill bill = null;
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("select b.id,b.product_id,b.bill_number,b.target_price,b.audit_status,b.use_status,b.create_time from adjust_price_bill b where b.bill_number=?");
			ps.setString(1, billNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				bill = new AdjustPriceBill();
				bill.setId(rs.getInt("id"));
				bill.setProductId(rs.getInt("product_id"));
				bill.setBillNumber(rs.getString("bill_number"));
				bill.setTargetPrice(rs.getDouble("target_price"));
				bill.setAuditStatus(rs.getInt("audit_status"));
				bill.setUseStatus(rs.getInt("use_status"));
				bill.setCreateTime(rs.getTimestamp("create_time"));
			}
		} catch (Exception e) {
			log.error("根据调价单号获取调价单对象信息时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(rs, ps, conn);
		}
		return bill;
	}
	
	/**
	 * 根据调价单id获取调价单详细信息（包括商品信息）
	 * @param id 调价单id
	 * @return
	 */
	public AdjustPriceBill getDetailById(int id) {
		DbOperation db = new DbOperation();
		AdjustPriceBill bill = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select b.id,b.product_id,b.bill_number,b.target_price,b.audit_status,b.use_status,b.create_time,p.name");
			sql.append(" from adjust_price_bill b join product p on b.product_id=p.id");
			sql.append(" where b.id=").append(id);
			ResultSet rs = db.executeQuery(sql.toString());
		    if(rs.next()){
		    	bill = new AdjustPriceBill();
				bill.setId(rs.getInt("id"));
				bill.setBillNumber(rs.getString("bill_number"));
				bill.setTargetPrice(rs.getDouble("target_price"));
				bill.setAuditStatus(rs.getInt("audit_status"));
				bill.setUseStatus(rs.getInt("use_status"));
				bill.setCreateTime(rs.getTimestamp("create_time"));
				Product p = new Product();
				p.setId(rs.getInt("product_id"));
				p.setName(rs.getString("name"));
				bill.setProductId(p.getId());
				bill.setProduct(p);
		    }
		} catch (Exception e) {
			log.error("根据调价单id获取调价单详细信息时出现异常：", e);
		} finally {
			db.release();
		}
		return bill;
	}
	
	/**
	 * 保存调价单信息
	 * @param adjustPriceBill 调价单信息
	 * @throws Exception 
	 */
	public void saveAdjustPriceBill(AdjustPriceBill adjustPriceBill) throws Exception {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean oldAutoCommit = true;
		try {
			//记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			//关闭自动提交事务
			conn.setAutoCommit(false);
			
			//生成调价单号
			String billNumber = "";
			ps = conn.prepareStatement("select count(id) from adjust_price_bill");
			rs = ps.executeQuery();
			if(rs.next()) {
				billNumber = String.valueOf(rs.getInt(1) + 1);
			}
			billNumber = StringUtils.leftPad(billNumber, 6, "0");
			System.out.println(billNumber);
			
			//保存
			ps = conn.prepareStatement("insert adjust_price_bill(product_id,bill_number,target_price,use_status,audit_status,create_time) values(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, adjustPriceBill.getProductId());
			ps.setString(2, billNumber);
			ps.setDouble(3, adjustPriceBill.getTargetPrice());
			ps.setInt(4, adjustPriceBill.getUseStatus());
			ps.setInt(5, adjustPriceBill.getAuditStatus());
			ps.setTimestamp(6, new Timestamp(new Date().getTime()));
			ps.executeUpdate();
			
			if(adjustPriceBill.getUseStatus() == 2) {
				//获取id
				rs = ps.getGeneratedKeys();
				if(rs.next()) {
					adjustPriceBill.setId(rs.getInt(1));
				}
				
				//向中心库提交调价单
				this.submitAdjustPriceBillToCenter(conn, adjustPriceBill.getId());
			}
			
			conn.commit();
		} catch (Exception e) {
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {}
			log.error("保存调价单信息时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(rs, ps, conn, oldAutoCommit);
		}
	}
	
	/**
	 * 修改调价单信息
	 * @param adjustPriceBill 调价单信息
	 * @throws Exception 
	 */
	public void updateAdjustPriceBill(AdjustPriceBill adjustPriceBill) throws Exception {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		boolean oldAutoCommit = true;
		try {
			//记录原来事务提交的方式
			oldAutoCommit = conn.getAutoCommit();
			//关闭自动提交事务
			conn.setAutoCommit(false);
			
			//更新
			ps = conn.prepareStatement("update adjust_price_bill set product_id=?,target_price=?,use_status=?,audit_status=? where id=?");
			ps.setInt(1, adjustPriceBill.getProductId());
			ps.setDouble(2, adjustPriceBill.getTargetPrice());
			ps.setInt(3, adjustPriceBill.getUseStatus());
			ps.setInt(4, adjustPriceBill.getAuditStatus());
			ps.setInt(5, adjustPriceBill.getId());
			ps.executeUpdate();
			
			//向中心库提交调价单
			this.submitAdjustPriceBillToCenter(conn, adjustPriceBill.getId());
			
			conn.commit();
		} catch (Exception e) {
			try {
				//回滚事务
				conn.rollback();
			} catch (SQLException e1) {}
			log.error("修改调价单信息时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(null, ps, conn, oldAutoCommit);
		}
	}
	
	
	/**
	 * 向中心库提交调价单
	 * @param conn 数据库连接对象，此方法不控制事务
	 * @param id 调价单id
	 * @throws Exception 
	 */
	private void submitAdjustPriceBillToCenter(Connection conn, int id) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			//获取调价单信息
			AdjustPriceBill bill = null;
			ps = conn.prepareStatement("select b.id,b.product_id,b.bill_number,b.target_price,b.audit_status,b.use_status,b.create_time from adjust_price_bill b where b.id=?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
		    if(rs.next()){
		    	bill = new AdjustPriceBill();
				bill.setId(rs.getInt("id"));
				bill.setProductId(rs.getInt("product_id"));
				bill.setBillNumber(rs.getString("bill_number"));
				bill.setTargetPrice(rs.getDouble("target_price"));
				bill.setAuditStatus(rs.getInt("audit_status"));
				bill.setUseStatus(rs.getInt("use_status"));
				bill.setCreateTime(rs.getTimestamp("create_time"));
		    }
		    
		    //拼装JSON字符串
			String shopCode = SystemConfig.getInstance().getShopCode();
			bill.setShopCode(shopCode);
			String submitJson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(bill);
			log.debug("submitAdjustPriceBillToCenter data=="+submitJson);
			
			//发送请求并接收返回数据
			String submitAdjustPriceBillToCenterURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("submitAdjustPriceBillToCenterURL");
			String result = HttpURLUtil.getResponseResult(submitAdjustPriceBillToCenterURL, submitJson);
			boolean success = "true".equalsIgnoreCase(result.toString());
			if(!success) {
				throw new Exception("中心库操作失败！");
			}
		} catch (Exception e) {
			log.error("向中心库提交调价单时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(rs, ps, null);
		}
	}
	
	/**
	 * 删除调价单信息
	 * @param id 调价单id
	 * @return
	 */
	public boolean deleteById(int id) {
		return this.deleteXXX("`id`="+id, "adjust_price_bill");
	}

	/**
	 * 从中心库同步调价单的审核状态
	 * @throws Exception 
	 */
	public void syncAdjustPriceBill() throws Exception {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			//获取所有待审核调价单的id
			List<AdjustPriceBill> billList = new ArrayList<AdjustPriceBill>();
			String shopCode = SystemConfig.getInstance().getShopCode();
			ps = conn.prepareStatement("select id from `adjust_price_bill` where use_status=2 and audit_status=1");
			rs = ps.executeQuery();
			AdjustPriceBill bill = null;
	    	while(rs.next()){
	    		bill = new AdjustPriceBill();
				bill.setId(rs.getInt("id"));
				bill.setShopCode(shopCode);
				billList.add(bill);
	    	}
	    	
	    	//没有需要同步的调价单
	    	if(billList.isEmpty()) {
	    		return ;
	    	}
	    	
	    	//请求数据
	    	String content = new Gson().toJson(billList);
	    	log.info("request data=="+content);
	    	
			//发送请求并获取返回结果
			String syncAdjustPriceBillURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("syncAdjustPriceBillURL");
			String json = HttpURLUtil.getResponseResult(syncAdjustPriceBillURL, content);
			log.info("back data=="+json);
			
			//解析JSON数据
			billList = new Gson().fromJson(json, new TypeToken<List<AdjustPriceBill>>(){}.getType());
			
			//更新调价单的审核状态
			if(!billList.isEmpty()) {
				this.updateAuditStatus(billList);
			}
		}catch(Exception e){
			log.error("从中心库同步调价单的审核状态时出现异常：", e);
			throw e;
		}finally{
			DbUtil.closeConnection(null, ps, conn);
		}
	}

	/**
	 * 更新调价单的审核状态
	 * @param billList 调价单列表
	 * @throws Exception 
	 */
	private void updateAuditStatus(List<AdjustPriceBill> billList) throws Exception {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("update adjust_price_bill set audit_status=? where id=?");
			for(AdjustPriceBill bill : billList) {
				ps.setInt(1, bill.getAuditStatus());
				ps.setInt(2, bill.getId());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (Exception e) {
			log.error("更新调价单的审核状态时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(null, ps, conn);
		}
	}

	/**
	 * POS机获取调价单
	 * @param json POS机传递的JSON数据
	 * @return 操作结果JSON
	 */
	public String posGetAdjustPriceBill(String json) {
		String result = "";
		try {
			//解析JSON数据
			Gson gson = new Gson();
			AdjustPriceBill bill = gson.fromJson(json, AdjustPriceBill.class);
			
			//获取调价单
			bill = this.getAdjustPriceBillByBillNumber(bill.getBillNumber());
			
			//返回JSON字符串
			if(bill != null) {
				result = gson.toJson(bill);
			}
		} catch (Exception e) {
			log.error("POS机获取调价单时出现异常：", e);
			result = "{\"message\":\"POS机获取调价单时出现异常："+e+"\"}";
		}
		return result;
	}

}
