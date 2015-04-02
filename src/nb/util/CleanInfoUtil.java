package nb.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import nb.database.BluemixDB2Connector;

public class CleanInfoUtil {
	
	private static BluemixDB2Connector connector = BluemixDB2Connector.getConnector();
	
	public static void updateCleanInfoTable(String username, Map<String,String> locationData, 
			String filename, String comment, int score, double latitude, double longitude) {
		PreparedStatement preparedSQL = connector.getPreparedStatement(insertStatement());
		try {
			preparedSQL.setString(1, username);
			preparedSQL.setString(2, new Double(latitude).toString());
			preparedSQL.setString(3, new Double(longitude).toString());	
			preparedSQL.setString(4, otherIfNull(locationData.get("route")));
			preparedSQL.setString(5, otherIfNull(locationData.get("neighborhood")));
			preparedSQL.setString(6, otherIfNull(locationData.get("sublocality_level_1")));
			preparedSQL.setString(7, otherIfNull(locationData.get("locality")));
			preparedSQL.setString(8, otherIfNull(locationData.get("administrative_area_level_2")));
			preparedSQL.setString(9, otherIfNull(locationData.get("administrative_area_level_1")));
			preparedSQL.setString(10, otherIfNull(locationData.get("postal_code")));
			preparedSQL.setInt(11,score);
			preparedSQL.setString(12, filename);
			preparedSQL.setString(13, comment);
			preparedSQL.setTimestamp(14, getCurrentTimestamp());			
			preparedSQL.executeUpdate();
			connector.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static String otherIfNull(String s) {
		return s==null ? "Other" : s;
	}
	
	private static Timestamp getCurrentTimestamp() {
		Date today = new Date();
		return new Timestamp(today.getTime());
	}
	
	private static String insertStatement() {
		String sql= "INSERT INTO CLEANINFO" +
					"(" +
					"USERNAME," +
					"LATITUDE," +
					"LONGITUDE," +
					"ROUTE," +
					"NEIGHBORHOOD," +
					"SUB_LOCALITY_LEVEL_1," +
					"LOCALITY," +
					"ADMINISTRATIVE_AREA_LEVEL_2," +
					"ADMINISTRATIVE_AREA_LEVEL_1," +
					"POSTAL_CODE," +
					"SCORE," +
					"DBX_FILENAME," +
					"COMMENT," +
					"TIMESTAMP" +
					")VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		return sql;
	}

	
	public static String getResults(String name, String type) {
		if(type.equals("city"))
			type = "locality";
		String query = buildQuery(name,type);
		return buildJSON(connector.executeQuery(query));
	}
	
	private static String buildQuery(String name, String type) {
		String query = "SELECT LATITUDE,LONGITUDE,DBX_FILENAME,SCORE,ROUTE,NEIGHBORHOOD,SUB_LOCALITY_LEVEL_1,LOCALITY,POSTAL_CODE FROM CLEANINFO WHERE " 
				        + type + "=" + "'" + name + "'";
		return query;
	}
	
	private static String buildAddress(ResultSet set) {
		StringBuilder address = new StringBuilder();
		try {
			String route = set.getString("ROUTE");
			if(!route.equalsIgnoreCase("Other"))
				address.append(route);
			String neighborhood = set.getString("NEIGHBORHOOD");
			if(!neighborhood.equalsIgnoreCase("Other"))
				address.append(","+neighborhood);
			String sublocal = set.getString("SUB_LOCALITY_LEVEL_1");
			if(!sublocal.equalsIgnoreCase("Other"))
				address.append(","+sublocal);
			String city = set.getString("LOCALITY");
			if(!city.equalsIgnoreCase("Other"))
				address.append(","+city);		
			String postal = set.getString("POSTAL_CODE");
			if(!postal.equalsIgnoreCase("Other"))
				address.append(","+postal);					
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return address.toString();
	}
	
	private static  String buildJSON(ResultSet rs) {
		StringBuilder result = new StringBuilder();
		result.append("{\"results\":[");
		try {			
			//thanks to the comma-complexity
			if(rs.next()) {
				result.append("{");
				result.append("\"lat\":\"" + rs.getString("LATITUDE") + "\",");
				result.append("\"long\":\"" + rs.getString("LONGITUDE") + "\",");
				result.append("\"img\":\"" + DropBoxUtil.getPublicURL(rs.getString("DBX_FILENAME")) + "\",");
				result.append("\"score\":\"" + rs.getInt("SCORE") + "\",");
				result.append("\"address\":\"" + buildAddress(rs) + "\"");
				result.append("}");
			}
			while(rs.next()) {
				result.append(",{");
				result.append("\"lat\":\"" + rs.getString("LATITUDE") + "\",");
				result.append("\"long\":\"" + rs.getString("LONGITUDE") + "\",");
				result.append("\"img\":\"" + DropBoxUtil.getPublicURL(rs.getString("DBX_FILENAME")) + "\",");
				result.append("\"score\":\"" + rs.getInt("SCORE") + "\",");
				result.append("\"address\":\"" + buildAddress(rs) + "\"");
				result.append("}");				
			}
		result.append("]}");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
}
