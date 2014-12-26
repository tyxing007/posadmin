package mmb.posadmin.service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mmb.posadmin.action.Page;
import mmb.posadmin.domain.Product;
import mmb.posadmin.domain.ReceiveOrderItem;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbOperation;

public class ReceiveOrderItemService extends BaseService {
	
	/**
	 * 分页获取收货单条目列表信息
	 * @param page 分页信息
	 * @return
	 */
	public Page<ReceiveOrderItem> getReceiveOrderItemPage(Page<ReceiveOrderItem> page, Map<String, Object> param) {
		DbOperation db = new DbOperation();
		try{
			Integer ReceiveOrderId = (Integer) param.get("receiveOrderId");
			String condSql = " and i.order_id="+ReceiveOrderId;
			//查询总记录数
			ResultSet rs = db.executeQuery("select count(id) from receive_order_item i where 1=1 "+condSql);
		    if(rs.next()){
		    	page.setTotalRecords(rs.getInt(1));
		    }
		    
		    //获取列表数据
		    if(page.getTotalRecords() > 0) {
		    	List<ReceiveOrderItem> list = new ArrayList<ReceiveOrderItem>();
		    	ReceiveOrderItem item;
		    	StringBuilder sql = new StringBuilder(50);
		    	sql.append("SELECT i.id,i.order_id,i.product_id,i.send_count,i.receive_count,p.`name` productName");
		    	sql.append(" from receive_order_item i LEFT JOIN product p on i.product_id=p.id ");
		    	sql.append(" where 1=1 ").append(condSql);
		    	sql.append(" limit ");
		    	sql.append(page.getFirstResult()).append(",").append(page.getPageCount());
		    	rs = db.executeQuery(sql.toString());
		    	while(rs.next()){
		    		item = new ReceiveOrderItem();
		    		item.setId(rs.getInt("id"));
		    		item.setOrderId(rs.getInt("order_id"));
		    		item.setSendCount(rs.getInt("send_count"));
		    		item.setReceiveCount(rs.getInt("receive_count"));
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
			e.printStackTrace();
		}finally{
			db.release();
		}
		return page;
	}
	
	/**
	 * 根据收货单id获取所有收货单条目
	 * @param ReceiveOrderId 收货单id
	 * @return
	 */
	public List<ReceiveOrderItem> getAllItemListByOrderId(int receiveOrderId) {
		DbOperation db = new DbOperation();
		List<ReceiveOrderItem> list = new ArrayList<ReceiveOrderItem>();
		try{
		    //获取列表数据
		    ReceiveOrderItem item;
		    String sql = "SELECT i.id,i.order_id,i.product_id,i.send_count,i.receive_count,p.`name` productName " +
		    		"from receive_order_item i LEFT JOIN product p on i.product_id=p.id where i.order_id="+receiveOrderId;
		    ResultSet rs = db.executeQuery(sql);
		    while(rs.next()){
		    	item = new ReceiveOrderItem();
	    		item.setId(rs.getInt("id"));
	    		item.setOrderId(rs.getInt("order_id"));
	    		item.setSendCount(rs.getInt("send_count"));
	    		item.setReceiveCount(rs.getInt("receive_count"));
	    		//商品信息
	    		Product p = new Product();
	    		p.setId(rs.getInt("product_id"));
	    		p.setName(rs.getString("productName"));
	    		item.setProductId(p.getId());
	    		item.setProduct(p);
	    		list.add(item);
		    }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.release();
		}
		return list;
	}

}
