package im.bpu.hexachess.dao;

import im.bpu.hexachess.Config;

import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.cj.jdbc.MysqlDataSource;

public class SingleConnection {
	private static final String DEFAULT_DB_URL =
		"jdbc:mysql://localhost:3306/hexachess?serverTimezone=UTC";
	private static final String DEFAULT_DB_USER = "root";
	private static final String DEFAULT_WINDOWS_PASS = "";
	private static final String DEFAULT_LINUX_PASS = "password123";
	private static final String DB_URL = Config.get("DB_URL", DEFAULT_DB_URL);
	private static final String DB_USER = Config.get("DB_USER", DEFAULT_DB_USER);
	private static final String DB_PASS = Config.get("DB_PASS", getPassword());
	private static Connection connect;
	private SingleConnection() throws SQLException {
		MysqlDataSource mysqlDS = new MysqlDataSource();
		mysqlDS.setURL(DB_URL);
		mysqlDS.setUser(DB_USER);
		mysqlDS.setPassword(DB_PASS);
		connect = mysqlDS.getConnection();
	}
	private static String getPassword() {
		String osName = System.getProperty("os.name").toLowerCase();
		boolean isWindows = osName.contains("win");
		return isWindows ? DEFAULT_WINDOWS_PASS : DEFAULT_LINUX_PASS;
	}
	public static synchronized Connection getInstance() throws SQLException {
		if (connect == null || connect.isClosed())
			new SingleConnection();
		return connect;
	}
	public static void close() {
		try {
			if (connect != null && !connect.isClosed())
				connect.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}
}