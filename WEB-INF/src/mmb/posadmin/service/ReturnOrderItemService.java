package mmb.posadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mmb.posadmin.action.Page;
import mmb.posadmin.domain.Product;
import mmb.posadmin.domain.ReturnOrderItem;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbOperation;
import mmboa.util.db.DbUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ReturnOrderItemService extends BaseService {
	
	private static Logger log = Logger.getLogger(ReturnOrderItemService.class);
	
	/**
	 * 分页获取退货单条目列表信息
	 * @param page 分页信息
	 * @param param 查询参数[productName:商品名称；returnOrderId:退货单id]
	 * @return
	 */
	public Page<ReturnOrderItem> getReturnOrderItemPage(Page<ReturnOrderItem> page, Map<String, Object> param) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			//查询条件
			StringBuilder condSql = new StringBuilder();
			Integer returnOrderId = (Integer) param.get("returnOrderId");
			String productName = (String) param.get("productName");
			condSql.append(" and i.return_order_id="+returnOrderId);
			if(StringUtils.isNotBlank(productName)) {
				condSql.append(" and p.`name` like ? ");
			}
			
			//查询总记录数
			int index = 1;
			ps = conn.prepareStatement("select count(i.id) from return_order_item i LEFT JOIN product p on i.product_id=p.id where 1=1 "+condSql);
			if(StringUtils.isNotBlank(productName)) {
				ps.setString(index++, "%"+productName+"%");
			}
			rs = ps.executeQuery();
			if(rs.next()){
		    	page.setTotalRecords(rs.getInt(1));
		    }
			
		    //获取列表数据
		    if(page.getTotalRecords() > 0) {
		    	List<ReturnOrderItem> list = new ArrayList<ReturnOrderItem>();
		    	ReturnOrderItem item;
		    	StringBuilder sql = new StringBuilder(50);
		    	sql.append("SELECT i.id,i.return_order_id,i.product_id,i.count,p.`name` productName");
		    	sql.append(" from return_order_item i LEFT JOIN product p on i.product_id=p.id ");
		    	sql.append(" where 1=1 ").append(condSql);
		    	sql.append(" limit ");
		    	sql.append(page.getFirstResult()).append(",").append(page.getPageCount());
		    	ps = conn.prepareStatement(sql.toString());
		    	index = 1;
				if(StringUtils.isNotBlank(productName)) {
					ps.setString(index++, "%"+productName+"%");
				}
		    	rs = ps.executeQuery();
		    	while(rs.next()){
		    		item = new ReturnOrderItem();
		    		item.setId(rs.getInt("id"));
		    		item.setReturnOrderId(rs.getInt("return_order_id"));
		    		item.setCount(rs.getInt("count"));
		    		//商品信息
		    		Product p = new Product();
		    		p.setId(rs.getInt("product_id"));
		    		p.setName(rs.getString("productName"));
		    		item.setProductId(p.getId());
		    		item.setProduct(p);
		    		list.add(item);
		    	}
		    	page.setList(list);
		    }
		}catch(Exception e){
			log.error("分页获取退货单条目列表信息时出现异常：", e);
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return page;
	}
	
	/**
	 * 根据退货单id获取所有退货单条目
	 * @param returnOrderId 退货单id
	 * @return
	 */
	public List<ReturnOrderItem> getAllItemListByOrderId(int returnOrderId) {
		DbOperation db = new DbOperation();
		List<ReturnOrderItem> list = new ArrayList<ReturnOrderItem>();
		try{
		    //获取列表数据
		    ReturnOrderItem item;
		    String sql = "SELECT i.id,i.return_order_id,i.product_id,i.count from return_order_item i where i.return_order_id="+returnOrderId;
		    ResultSet rs = db.executeQuery(sql);
		    while(rs.next()){
		    	item = new ReturnOrderItem();
		    	item.setId(rs.getInt("id"));
		    	item.setReturnOrderId(rs.getInt("return_order_id"));
		    	item.setCount(rs.getInt("count"));
		    	item.setProductId(rs.getInt("product_id"));
		    	list.add(item);
		    }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.release();
		}
		return list;
	}

	/**
	 * 根据退货单条目id获取退货单条目对象信息
	 * @param id 退货单条目id
	 * @return
	 */
	public ReturnOrderItem getReturnOrderItemById(int id) {
		return (ReturnOrderItem) this.getXXX("`id`="+id, "return_order_item", ReturnOrderItem.class.getName());
	}
	
	/**
	 * 根据退货单条目id获取退货单条目详细信息（包括商品信息）
	 * @param id 退货单条目id
	 * @return
	 */
	public ReturnOrderItem getDetailById(int id) {
		DbOperation db = new DbOperation();
		ReturnOrderItem item = null;
		try{
			String sql = "SELECT i.id,i.return_order_id,i.product_id,i.count,p.`name` productName" +
					" from return_order_item i LEFT JOIN product p on i.product_id=p.id where i.id="+id;
			ResultSet rs = db.executeQuery(sql);
		    if(rs.next()){
		    	item = new ReturnOrderItem();
	    		item.setId(rs.getInt("id"));
	    		item.setReturnOrderId(rs.getInt("return_order_id"));
	    		item.setCount(rs.getInt("count"));
	    		//商品信息
	    		Product p = new Product();
	    		p.setId(rs.getInt("product_id"));
	    		p.setName(rs.getString("productName"));
	    		item.setProductId(p.getId());
	    		item.setProduct(p);
		    }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.release();
		}
		return item;
	}
	
	/**
	 * 更新退货单条目信息
	 * @param returnOrderItem 退货单条目对象
	 * @return
	 */
	public boolean updateReturnOrderItem(ReturnOrderItem returnOrderItem) {
		//更新语句
		StringBuilder set = new StringBuilder();
		set.append("`product_id`=").append(returnOrderItem.getProductId()).append(", ");
		set.append("`count`=").append(returnOrderItem.getCount());
		return this.updateXXX(set.toString(), "`id`="+returnOrderItem.getId(), "return_order_item");
	}
	
	/**
	 * 删除退货单条目信息
	 * @param id 退货单条目id
	 * @return
	 */
	public boolean deleteItemById(int id) {
		return this.deleteXXX("`id`="+id, "return_order_item");
	}

}
