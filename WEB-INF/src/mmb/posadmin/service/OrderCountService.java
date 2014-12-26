package mmb.posadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mmb.posadmin.action.Page;
import mmb.posadmin.domain.Counter;
import mmb.posadmin.domain.SaleCounter;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbUtil;

public class OrderCountService extends BaseService {
    
	//有bug 当查询时间内没有销售id时  sql语句会出bug
	public Page<SaleCounter> doCountSaleOrder(Page<SaleCounter> page,
			String start, String end) {
		List<SaleCounter> tmp = new ArrayList<SaleCounter>();
		// List<SaledOrder> salist = new ArrayList<SaledOrder>();
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		SaleCounter sc = null;
		try {
			ps = conn
					.prepareStatement("select id from saled_order where saled_time >= ? and saled_time <= ?");
			ps.setString(1, start);
			ps.setString(2, end);
			rs = ps.executeQuery();
			StringBuilder sb = new StringBuilder(500);
			sb.append("(");
			while (rs.next()) {
				if (sb.length() == 1) {
					sb.append(rs.getInt("id"));
				} else {
					sb.append("," + rs.getInt("id"));
				}
			}
			sb.append(")");
			page = getPageFullValues(page, sb.toString());

			StringBuilder sql = new StringBuilder(100);
			sql.append("select sp.product_id ,p.name,p.bar_code,sp.saled_order_id ,sum(sp.count),SUM(sp.count*sp.pre_price),p.stock from saled_order_product sp,product p where sp.product_id = p.id and sp.saled_order_id in "
					+ sb.toString() + " GROUP BY sp.product_id");
			sql.append(" limit ");
			sql.append(page.getFirstResult()).append(",").append(page.getPageCount());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				sc = new SaleCounter();
				sc.setBarCode(rs.getString(3));
				sc.setCount(rs.getInt(5));
				sc.setProductId(rs.getInt(1));
				sc.setProductName(rs.getString(2));
				sc.setTotalSales(rs.getDouble(6));
				sc.setStock(rs.getInt(7));
				tmp.add(sc);
			}
			page.setList(tmp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		return page;
	}

	/**
	 * 获取当前分页的信息
	 * 
	 * @param page
	 * @return
	 */
	public Page<SaleCounter> getPageFullValues(Page<SaleCounter> page,
			String condition) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn
					.prepareStatement("select count(1) from ( select sp.product_id ,p.name,p.bar_code,sp.saled_order_id ,sum(sp.count),SUM(sp.count*sp.pre_price) from saled_order_product sp,product p where sp.product_id = p.id and sp.saled_order_id in "
							+ condition + " GROUP BY sp.product_id ) tt");
			rs = ps.executeQuery();
			if (rs.next()) {
				page.setTotalRecords(rs.getInt(1));
			}
			return page;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return page;
	}

	/**
	 * 统计常用信息
	 * 
	 * @param time
	 * @return
	 */
	public Counter getCounter(String time) {
		Counter c = new Counter();
		Connection conn = DbUtil.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String time1 = time +" 00:00:00";
		String time2 = time +" 24:59:59";
		try {
			ps = conn
					.prepareStatement("select sum(price),count(1) from saled_order where saled_time >= ? and saled_time <= ? and order_type = 0");
			ps.setString(1, time1);
			ps.setString(2, time2);
			rs = ps.executeQuery();
			if (rs.next()) {
				c.setTotalSales(rs.getDouble(1));
				c.setTotalSaleOrders(rs.getInt(2));
			}

			ps = conn
					.prepareStatement("select sum(price),count(1) from saled_order where saled_time >= ? and saled_time <= ? and order_type = 1");
			ps.setString(1, time1);
			ps.setString(2, time2);
			rs = ps.executeQuery();
			if (rs.next()) {
				c.setTotalBackSales(rs.getDouble(1));
				c.setTotalBackSaleOrders(rs.getInt(2));
			}

			ps = conn
					.prepareStatement("select sum(price) from lease_order where create_time >= ? and create_time <= ? ");
			ps.setString(1, time1);
			ps.setString(2, time2);
			rs = ps.executeQuery();
			if (rs.next()) {
				c.setTotalLeaseCash(rs.getDouble(1));
			}

			ps = conn
					.prepareStatement("select count(1),sum(deposit) from lease_order where create_time >= ? and create_time <= ?  and order_type = 0");
			ps.setString(1, time1);
			ps.setString(2, time2);
			rs = ps.executeQuery();
			if (rs.next()) {
				c.setTotalLeaseOrders(rs.getInt(1));
				c.setTotalDeposit(rs.getDouble(2));
			}

			ps = conn
					.prepareStatement("select count(1),sum(deposit) from lease_order where create_time >= ? and create_time <= ?  and order_type = 1");
			ps.setString(1, time1);
			ps.setString(2, time2);
			rs = ps.executeQuery();
			if (rs.next()) {
				c.setTotalBackLeaseOrders(rs.getInt(1));
				c.setTotalBackDeposit(rs.getDouble(2));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		return c;
	}

}
