package mmb.posadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import mmb.posadmin.domain.CashStream;
import mmboa.util.db.BaseService;

public class CashStreamService extends BaseService {

	/**
	 * 保存现金流数据
	 * @param conn 数据库连接对象，此方法中不处理事务
	 * @param cashStream 现金流对象
	 * @throws SQLException
	 */
	public void saveCashStream(Connection conn, CashStream cashStream) throws SQLException {
		String sql = "insert cash_stream(serial_number,pos_code,cashier_id,cash,get_time) values(?,?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, cashStream.getSerialNumber());
		ps.setString(2, cashStream.getPosCode());
		ps.setInt(3, cashStream.getCashierId());
		ps.setDouble(4, cashStream.getCash());
		ps.setTimestamp(5, new Timestamp(new Date().getTime()));
		ps.executeUpdate();
		ps.close();
	}
}
