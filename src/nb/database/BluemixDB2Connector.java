package nb.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



import com.ibm.db2.jcc.DB2SimpleDataSource;

public class BluemixDB2Connector {
	
	public static String exceptionString;
	
	private static Connection connection;
	private static BluemixDB2Connector connector;

	
	private BluemixDB2Connector() {
		try {
			DB2SimpleDataSource dataSource = new DB2SimpleDataSource();
			dataSource.setServerName("75.126.155.153");
			dataSource.setPortNumber(50000);
			dataSource.setDatabaseName("BLUDB");
			dataSource.setUser("blu00056");
			dataSource.setPassword ("uHIWsDlCbWAd");
			dataSource.setDriverType(4);
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static BluemixDB2Connector getConnector() {
		if(connector == null) {
			connector = new BluemixDB2Connector();
			return connector;
		}
		return connector;
	}
	
	public boolean executeUpdate(String query) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
			connection.commit();
			return true;
		} catch (SQLException e) {			           
			e.printStackTrace();
			return false;
		}		
	}
	
	public ResultSet executeQuery(String query) {
		try {
			Statement statement = connection.createStatement();			
			return statement.executeQuery(query);				
		} catch (SQLException e) {			
			e.printStackTrace();
			return null;
		}
	}
	
	public PreparedStatement getPreparedStatement(String sql) {
		try {
			return connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void commit() {
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
