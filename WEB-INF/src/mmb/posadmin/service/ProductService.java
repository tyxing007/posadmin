package mmb.posadmin.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import mmb.posadmin.action.Page;
import mmb.posadmin.domain.Product;
import mmb.posadmin.domain.SystemConfig;
import mmb.posadmin.util.HttpURLUtil;
import mmb.posadmin.util.SaleTagUtil;
import mmboa.util.Constants;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbOperation;
import mmboa.util.db.DbUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ProductService extends BaseService {
	
	private static Logger log = Logger.getLogger(ProductService.class);
	
	/**
	 * 分页获取商品列表信息
	 * @param page 分页信息
	 * @param param 查询参数[barCode:条形码；productName:商品名称]
	 * @return
	 */
	public Page<Product> getProductPage(Page<Product> page, Map<String, Object> param) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			//查询条件
			StringBuilder condSql = new StringBuilder();
			String barCode = (String) param.get("barCode");
			String productName = (String) param.get("productName");
			if(StringUtils.isNotBlank(barCode)) {
				condSql.append(" and p.`bar_code` like ? ");
			}
			if(StringUtils.isNotBlank(productName)) {
				condSql.append(" and p.`name` like ? ");
			}
			
			//查询总记录数
			int index = 1;
			StringBuilder countSql = new StringBuilder();
			countSql.append("select count(p.id) from product p where p.is_delete=0").append(condSql);
			ps = conn.prepareStatement(countSql.toString());
			if(StringUtils.isNotBlank(barCode)) {
				ps.setString(index++, "%"+barCode+"%");
			}
			if(StringUtils.isNotBlank(productName)) {
				ps.setString(index++, "%"+productName+"%");
			}
			rs = ps.executeQuery();
			if(rs.next()){
		    	page.setTotalRecords(rs.getInt(1));
		    }
			
		    //查询列表数据
		    if(page.getTotalRecords() > 0) {
		    	StringBuilder sql = new StringBuilder(50);
		    	sql.append("select p.id,p.bar_code,p.name,p.lease_price,p.month_lease_price,p.sale_price,p.limit_price,p.lock_price,p.red_lines,p.blue_lines,p.deposit,p.stock from product p");
		    	sql.append(" where p.is_delete=0 ").append(condSql);
		    	sql.append(" order by p.id desc limit ");
		    	sql.append(page.getFirstResult()).append(",").append(page.getPageCount());
		    	ps = conn.prepareStatement(sql.toString());
		    	index = 1;
		    	if(StringUtils.isNotBlank(barCode)) {
					ps.setString(index++, "%"+barCode+"%");
				}
				if(StringUtils.isNotBlank(productName)) {
					ps.setString(index++, "%"+productName+"%");
				}
		    	rs = ps.executeQuery();
		    	List<Product> list = new ArrayList<Product>();
		    	Product p;
		    	while(rs.next()){
		    		p = new Product();
					p.setId(rs.getInt("id"));
					p.setBarCode(rs.getString("bar_code"));
					p.setName(rs.getString("name"));
					p.setLeasePrice(rs.getDouble("lease_price"));
					p.setMonthLeasePrice(rs.getDouble("month_lease_price"));
					p.setSalePrice(rs.getDouble("sale_price"));
					p.setLimitPrice(rs.getDouble("limit_price"));
			    	p.setLockPrice(rs.getDouble("lock_price"));
			    	p.setRedLines(rs.getDouble("red_lines"));
			    	p.setBlueLines(rs.getDouble("blue_lines"));
					p.setDeposit(rs.getDouble("deposit"));
					p.setStock(rs.getInt("stock"));
					list.add(p);
		    	}
		    	page.setList(list);
		    }
		}catch(Exception e){
			log.error("分页获取商品列表信息时出现异常：", e);
		}finally{
			DbUtil.closeConnection(rs, ps, conn);
		}
		return page;
	}

	/**
	 * 根据商品id获取商品对象信息
	 * @param id 商品id
	 * @return
	 */
	public Product getProductById(int id) {
		return (Product) this.getXXX("`id`="+id, "product", Product.class.getName());
	}
	
	/**
	 * 更新商品信息
	 * @param product 商品信息
	 * @throws Exception 
	 */
	public void updateProduct(Product product) throws Exception {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("update product set name=?,lease_price=?,month_lease_price=?,sale_price=?,deposit=?,limit_price=?,lock_price=?,red_lines=?,blue_lines=? where id=?");
			ps.setString(1, product.getName());
			ps.setDouble(2, product.getLeasePrice());
			ps.setDouble(3, product.getMonthLeasePrice());
			ps.setDouble(4, product.getSalePrice());
			ps.setDouble(5, product.getDeposit());
			ps.setDouble(6, product.getLimitPrice());
			ps.setDouble(7, product.getLockPrice());
			ps.setDouble(8, product.getRedLines());
			ps.setDouble(9, product.getBlueLines());
			ps.setInt(10, product.getId());
			ps.executeUpdate();
		} catch (Exception e) {
			log.error("修改商品信息时出现异常：", e);
			throw e;
		} finally {
			DbUtil.closeConnection(null, ps, conn);
		}
	}
	
	/**
	 * 从中心库同步商品信息
	 * @throws Exception 
	 */
	public void syncProductDataFromPoscenter() throws Exception {
		try {
			//发送请求并接收返回结果
			String syncPoscenterProductURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("syncPoscenterProductURL");
			String json = HttpURLUtil.getResponseResult(syncPoscenterProductURL);
			
			//解析字符串
			List<Product> productList = new Gson().fromJson(json, new TypeToken<List<Product>>(){}.getType());
			
			//获取中心库新增和更新的商品列表数据
			List<List<Product>> list = getBatList(getAllProductListId(), productList);
			
			//批量插入和更新商品
			this.batchInsertUpdate(list.get(0), list.get(1));
		} catch (Exception e) {
			log.error("从中心库同步商品信息时出现异常：", e);
			throw e;
		}
	}
	
	//返回list集合  0 ：是中心库新增的  1：中心库要修改的
	public  List<List<Product>>  getBatList(List<Integer> src , List<Product> center){
		List<List<Product>> tmp = new ArrayList<List<Product>>();
		List<Product> list0 = new ArrayList<Product>();
		List<Product> list1 = new ArrayList<Product>();
		Set<Integer> s1 = new HashSet<Integer>();
		Set<Integer> s2 = new HashSet<Integer>();
		//将中心库的数据id加入set集合
		for(int i = 0; i< center.size();i++){
			s1.add(center.get(i).id);
		}
		   
		   for(int i = 0; i< src.size();i++){
			   s2.add(src.get(i));
		   }
		   //如果有新增货物数据，获取新增货物数据
		   if(s2.size() < s1.size()){
			   s1.removeAll(s2);
			   //将新增货物数据入list0
			   for(int j : s1){
				   for(int k = 0 ; k < center.size();k++){
					   if(center.get(k).id == j){
						   list0.add(center.get(k));
					   }
				   }
			   }
		   }   
		  
			   //将原有货物数据入list1
			   for(int ss : s2){
				   for(int k = 0 ; k < center.size();k++){
					   if(center.get(k).id == ss){
						   list1.add(center.get(k));
					   }
				   }
			   }
		   tmp.add(list0);
		   tmp.add(list1);
		   return tmp;
	}
	
	
	/**
	 * 获取店面本地的id list表
	 * @return
	 */
	public List<Integer> getAllProductListId(){
		List<Integer> list = new ArrayList<Integer>();
		DbOperation db = new DbOperation();
		try{
			StringBuilder sql = new StringBuilder(50);
			sql.append("select id from product");
			ResultSet rs = db.executeQuery(sql.toString());
			while(rs.next()){
				list.add(rs.getInt(1));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.release(db);
		}
		return list;
	}
	
	/**
	 * 批量插入和更新商品
	 * @param insertProductList 新增商品列表
	 * @param updateProductList 更新商品列表
	 * @return
	 */
	public boolean batchInsertUpdate(List<Product> insertProductList, List<Product> updateProductList) {
		boolean success = false;
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		boolean oldAutoCommit = true;
		try {
			//记录原来事物提交的方式
			oldAutoCommit = conn.getAutoCommit(); 
			//关闭自动提交事务
			conn.setAutoCommit(false);
			
			//批量插入
			if(insertProductList!=null && !insertProductList.isEmpty()) {
				int count = 0;
				ps = conn.prepareStatement("insert into product(`bar_code`,`name`,`lease_price`,`month_lease_price`,`sale_price`,`deposit`,`stock`,`is_delete`,`id`,goods_class_id,limit_price,lock_price,red_lines,blue_lines) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				for(Product product : insertProductList) {
					ps.setString(1, product.getBarCode());
					ps.setString(2, product.getName());
					ps.setDouble(3, product.getLeasePrice());
					ps.setDouble(4, product.getMonthLeasePrice());
					ps.setDouble(5, product.getSalePrice());
					ps.setDouble(6, product.getDeposit());
					ps.setInt(7, 0); //从中心库同步商品时不同步库存信息
					ps.setInt(8, product.getIsDelete());
					ps.setInt(9, product.getId());
					ps.setString(10, product.getGoodsClassId());
					ps.setDouble(11, product.getLimitPrice());
					ps.setDouble(12, product.getLockPrice());
					ps.setDouble(13, product.getRedLines());
					ps.setDouble(14, product.getBlueLines());
					ps.addBatch();
					count++;
					
					//批量执行
					if (count%100==0 || count==insertProductList.size()) {
						ps.executeBatch();
						conn.commit();
					}
				}
			}
			
			//批量更新
			if(updateProductList!=null && !updateProductList.isEmpty()) {
				int count = 0;
				ps = conn.prepareStatement("update product set `bar_code`=?,`name`=?,`lease_price`=?,`month_lease_price`=?,`sale_price`=?,`deposit`=?,`is_delete`=?,goods_class_id=?,limit_price=?,lock_price=?,red_lines=?,blue_lines=? where `id`=?"); //`stock`=?,
				for(Product product : updateProductList) {
					ps.setString(1, product.getBarCode());
					ps.setString(2, product.getName());
					ps.setDouble(3, product.getLeasePrice());
					ps.setDouble(4, product.getMonthLeasePrice());
					ps.setDouble(5, product.getSalePrice());
					ps.setDouble(6, product.getDeposit());
					ps.setInt(7, product.getIsDelete());
					ps.setString(8, product.getGoodsClassId());
					ps.setDouble(9, product.getLimitPrice());
					ps.setDouble(10, product.getLockPrice());
					ps.setDouble(11, product.getRedLines());
					ps.setDouble(12, product.getBlueLines());
					ps.setInt(13, product.getId());
					ps.addBatch();
					count++;
					
					//批量执行
					if (count%100==0 || count==updateProductList.size()) {
						ps.executeBatch();
						conn.commit();
					}
				}
			}
			
			success = true;
		} catch (Exception e) {
			log.error("批量插入和更新商品时出现异常：", e);
		} finally {
			DbUtil.closeConnection(null, ps, conn, oldAutoCommit);
		}
		return success;
	}
	
	/**
	 * 获取JSON格式的所有商品数据
	 * @return
	 */
	public String getAllProductJson(){
		StringBuilder temp = new StringBuilder(1000);
		DbOperation db = new DbOperation();
		try{
			ResultSet rs = db.executeQuery("select id,bar_code,name,lease_price,month_lease_price,sale_price,limit_price,lock_price,red_lines,blue_lines,deposit,stock,is_delete from product");
			temp.append("\"product\":[");
			while(rs.next()){
				temp.append("{\"id\":").append(rs.getInt("id")).append(",");
				temp.append("\"bar_code\":\"").append(rs.getString("bar_code").replaceAll("\"", "\\\\\"")).append("\",");
				temp.append("\"name\":\"").append(rs.getString("name").replaceAll("\"", "\\\\\"")).append("\",");
				temp.append("\"lease_price\":").append(rs.getDouble("lease_price")).append(",");
				temp.append("\"month_lease_price\":").append(rs.getDouble("month_lease_price")).append(",");
				temp.append("\"sale_price\":").append(rs.getDouble("sale_price")).append(",");
				temp.append("\"limitPrice\":").append(rs.getDouble("limit_price")).append(",");
				temp.append("\"lockPrice\":").append(rs.getDouble("lock_price")).append(",");
				temp.append("\"redLines\":").append(rs.getDouble("red_lines")).append(",");
				temp.append("\"blueLines\":").append(rs.getDouble("blue_lines")).append(",");
				temp.append("\"deposit\":").append(rs.getDouble("deposit")).append(",");
				temp.append("\"stock\":").append(rs.getInt("stock")).append(",");
				temp.append("\"is_delete\":").append(rs.getInt("is_delete")).append("},");
				if(rs.isLast()){
					temp.deleteCharAt(temp.length()-1);
				}
			}
			temp.append("]");
		}catch(Exception e){
			log.error("获取JSON格式的所有商品数据时出现异常：", e);
		}finally{
			this.release(db);
		}
		return temp.toString();
	}

	/**
	 * 导出Excel商品列表
	 */
	public byte[] exportExcel() {
		byte[] data = null;
		
		//获取所有商品信息
		Page<Product> page = new Page<Product>(1, Integer.MAX_VALUE);
		page = this.getProductPage(page, new HashMap<String, Object>());
		
		WritableWorkbook book = null;
		ByteArrayOutputStream baos = null;
		try {
			//把工作薄保存到内存中
			baos = new ByteArrayOutputStream();
			book = Workbook.createWorkbook(baos);
			WritableSheet sheet = book.createSheet("商品列表信息", 0);
			
			//设置列宽
			sheet.setColumnView(0, 16);
			sheet.setColumnView(1, 35);
			sheet.setColumnView(2, 15);
			sheet.setColumnView(3, 15);
			sheet.setColumnView(4, 15);
			sheet.setColumnView(5, 10);
			sheet.setColumnView(6, 10);
			
			//商品列表信息
			WritableCellFormat format = new WritableCellFormat();
			WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD); 
			format = new WritableCellFormat(font);
			format.setAlignment(Alignment.CENTRE);
			sheet.addCell(new Label(0, 0, "条形码", format));
			sheet.addCell(new Label(1, 0, "商品名称", format));
			sheet.addCell(new Label(2, 0, "销售价格", format));
			sheet.addCell(new Label(3, 0, "日租赁价格", format));
			sheet.addCell(new Label(4, 0, "包月租赁价格", format));
			sheet.addCell(new Label(5, 0, "押金", format));
			sheet.addCell(new Label(6, 0, "库存", format));
			format = new WritableCellFormat();
			format.setAlignment(Alignment.CENTRE);
			for(int i=0; i<page.getList().size(); i++) {
				Product product = page.getList().get(i);
				sheet.addCell(new Label(0, 1+i, product.getBarCode(), format));
				sheet.addCell(new Label(1, 1+i, product.getName()));
				sheet.addCell(new jxl.write.Number(2, 1+i, product.getSalePrice()));
				sheet.addCell(new jxl.write.Number(3, 1+i, product.getLeasePrice()));
				sheet.addCell(new jxl.write.Number(4, 1+i, product.getMonthLeasePrice()));
				sheet.addCell(new jxl.write.Number(5, 1+i, product.getDeposit()));
				sheet.addCell(new jxl.write.Number(6, 1+i, product.getStock()));
			}
			
			// 写入数据并关闭文件
			book.write();
			book.close();
			
			//转化为字节数组
			data = baos.toByteArray();
		} catch (Exception e) {
			log.error("导出Excel商品列表时出现异常：", e);
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

	/**
	 * 把库存信息提交到中心库
	 * @return
	 */
	public String submitShopStockToCenter() {
		String result = null;
		DbOperation db = new DbOperation();
		try {
			//组装店面商品库存信息
			String shopCode = SystemConfig.getInstance().getShopCode();
			StringBuilder submitJson = new StringBuilder();
			submitJson.append("[");
			ResultSet rs = db.executeQuery("select p.id,p.stock from product p where p.is_delete=0 ");
			while(rs.next()){
				if(!rs.isFirst()) {
					submitJson.append(",");
				}
				submitJson.append("{\"shop\":{\"code\":\""+shopCode+"\"},");
				submitJson.append("\"productId\":"+rs.getInt("id")+",");
				submitJson.append("\"stock\":"+rs.getInt("stock"));
				submitJson.append("}");
	    	}
			submitJson.append("]");
			log.debug("submitShopStockToCenter data=="+submitJson);
			
			//发送请求并接收返回数据
			String submitShopStockToCenterURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("submitShopStockToCenterURL");
			result = HttpURLUtil.getResponseResult(submitShopStockToCenterURL, submitJson.toString());
		} catch (Exception e) {
			log.error("把库存信息提交到中心库时出现异常：", e);
			result = "操作失败：" + e.getMessage();
		} finally {
			db.release();
		}
		return result;
	}
	
	/**
	 * 获取商品销售标签图片路径
	 * @param product 商品对象
	 * @return 图片相对路径
	 * @throws Exception 
	 */
	public String getSaleTagImgPath(Product product) throws Exception {
		return SaleTagUtil.createSaleTagImg(product);
	}
	
}
