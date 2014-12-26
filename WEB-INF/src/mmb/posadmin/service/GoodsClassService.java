package mmb.posadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import mmb.posadmin.domain.GoodsClass;
import mmb.posadmin.util.HttpURLUtil;
import mmboa.util.Constants;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbOperation;
import mmboa.util.db.DbUtil;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class GoodsClassService extends BaseService {
	
	private static Logger log = Logger.getLogger(GoodsClassService.class);

	/**
	 * 从中心库同步商品分类信息
	 * @throws Exception 
	 */
	public void syncGoodsClassDataFromPoscenter() throws Exception {
		try {
			//发送请求并接收返回结果
			String syncPoscenterGoodsClassURL = Constants.config.getProperty("centerServerURL") + Constants.config.getProperty("syncPoscenterGoodsClassURL");
			String json = HttpURLUtil.getResponseResult(syncPoscenterGoodsClassURL);
			
			//解析字符串
			List<GoodsClass> goodsClassList = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(json, new TypeToken<List<GoodsClass>>(){}.getType());
			
			//获取店面本地的所以商品分类id
			List<String> allGoodsClassIdList = this.getAllGoodsClassIdList();
			
			// 获取中心库新增和更新的商品列表数据
			List<List<GoodsClass>> list = getBatList(allGoodsClassIdList, goodsClassList);
			
			// 批量插入和更新商品
			this.batchInsertUpdate(list.get(0), list.get(1));
		} catch (Exception e) {
			log.error("从中心库同步商品分类信息时出现异常：", e);
		}
	}

	/**
	 * 获取店面本地的所以商品分类id
	 * @return
	 */
	public List<String> getAllGoodsClassIdList() {
		List<String> list = new ArrayList<String>();
		DbOperation db = new DbOperation();
		try {
			StringBuilder sql = new StringBuilder(50);
			sql.append("select id from goods_class");
			ResultSet rs = db.executeQuery(sql.toString());
			while (rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(db);
		}
		return list;
	}

	// 返回list集合 0 ：是中心库新增的 1：中心库要修改的
	public List<List<GoodsClass>> getBatList(List<String> src, List<GoodsClass> center) {
		List<List<GoodsClass>> tmp = new ArrayList<List<GoodsClass>>();
		List<GoodsClass> list0 = new ArrayList<GoodsClass>();
		List<GoodsClass> list1 = new ArrayList<GoodsClass>();
		Set<String> s1 = new HashSet<String>();
		Set<String> s2 = new HashSet<String>();
		// 将中心库的数据id加入set集合
		for (int i = 0; i < center.size(); i++) {
			s1.add(center.get(i).id);
		}

		for (int i = 0; i < src.size(); i++) {
			s2.add(src.get(i));
		}
		// 如果有新增货物数据，获取新增货物数据
		if (s2.size() < s1.size()) {
			s1.removeAll(s2);
			// 将新增货物数据入list0
			for (String j : s1) {
				for (int k = 0; k < center.size(); k++) {
					if (j.equals(center.get(k).id)) {
						list0.add(center.get(k));
					}
				}
			}
		}

		// 将原有货物数据入list1
		for (String ss : s2) {
			for (int k = 0; k < center.size(); k++) {
				if (ss.equals(center.get(k).id)) {
					list1.add(center.get(k));
				}
			}
		}
		tmp.add(list0);
		tmp.add(list1);
		return tmp;
	}

	/**
	 * 批量插入和更新商品分类
	 * @param insertGoodsClassList 新增商品分类列表
	 * @param updateGoodsClassList 更新商品分类列表
	 * @return
	 */
	public boolean batchInsertUpdate(List<GoodsClass> insertGoodsClassList, List<GoodsClass> updateGoodsClassList) {
		boolean success = false;
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		boolean oldAutoCommit = true;
		try {
			// 记录原来事物提交的方式
			oldAutoCommit = conn.getAutoCommit();
			// 关闭自动提交事务
			conn.setAutoCommit(false);

			// 批量插入
			if (insertGoodsClassList != null && !insertGoodsClassList.isEmpty()) {
				int count = 0;
				ps = conn.prepareStatement("insert into goods_class(`id`,`parent_id`,`name`,`desc`,`tree_level`,`is_leaf`,`create_time`) values (?,?,?,?,?,?,?)");
				for (GoodsClass gc : insertGoodsClassList) {
					ps.setString(1, gc.getId());
					ps.setString(2, gc.getParentId());
					ps.setString(3, gc.getName());
					ps.setString(4, gc.getDesc());
					ps.setInt(5, gc.getTreeLevel());
					ps.setInt(6, gc.getIsLeaf());
					ps.setTimestamp(7, gc.getCreateTime());
					ps.addBatch();
					count++;

					// 批量执行
					if (count % 100 == 0 || count == insertGoodsClassList.size()) {
						ps.executeBatch();
						conn.commit();
					}
				}
			}

			// 批量更新
			if (updateGoodsClassList != null && !updateGoodsClassList.isEmpty()) {
				int count = 0;
				ps = conn.prepareStatement("update goods_class set `parent_id`=?,`name`=?,`desc`=?,`tree_level`=?,`is_leaf`=?,`create_time`=? where `id`=?"); // `stock`=?,
				for (GoodsClass gc : updateGoodsClassList) {
					ps.setString(1, gc.getParentId());
					ps.setString(2, gc.getName());
					ps.setString(3, gc.getDesc());
					ps.setInt(4, gc.getTreeLevel());
					ps.setInt(5, gc.getIsLeaf());
					ps.setTimestamp(6, gc.getCreateTime());
					ps.setString(7, gc.getId());
					ps.addBatch();
					count++;

					// 批量执行
					if (count % 100 == 0 || count == updateGoodsClassList.size()) {
						ps.executeBatch();
						conn.commit();
					}
				}
			}

			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.closeConnection(null, ps, null);
			if (conn != null) {
				try {
					// 还原事物提交方式
					conn.setAutoCommit(oldAutoCommit);
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

		return success;
	}

}
