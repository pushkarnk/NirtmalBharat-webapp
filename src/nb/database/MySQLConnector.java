package nb.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnector {
	
	private static Connection connection;
	private static MySQLConnector connector;
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("MySQL Driver Not Found.");
			e.printStackTrace();
		}
	
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nirmalbharat","root", "passw0rd");			
		} catch ( SQLException e ) {
			System.err.println ( "Connection failed.");
			e.printStackTrace();
		}
	}
		
	private MySQLConnector() {
		
	}
	
	public static MySQLConnector getConnector() {
		if(connector == null) {
			connector = new MySQLConnector();
			return connector;
		}
		return connector;
	}
	
	public boolean executeUpdate(String query) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
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
	
	public int executePreparedUpdate(PreparedStatement ps) {
		try {
			return ps.executeUpdate();
		} catch (SQLException e) {			
			e.printStackTrace();
			return 0;
		}
	}
	
	

}
