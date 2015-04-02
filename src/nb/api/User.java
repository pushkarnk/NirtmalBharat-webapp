package nb.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import nb.database.BluemixDB2Connector;

@Path("/useradd")
public class User {
	
	private static BluemixDB2Connector connection = BluemixDB2Connector.getConnector();
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addUser(@FormParam("username") String username, @FormParam("emailid") String emailID) {		
		System.err.println("Received request");
		String response;
		if (connection.executeUpdate(buildQuery(username,emailID)))
			response = "DONE";
		else 
			response = "ERROR";
		return Response.status(200).entity(response).build();
	}
	
	private String buildQuery(String uname, String email) {
		String query = "INSERT INTO NBUSER VALUES " 
				     + "( '" + uname + "', '" + email + "' )";
		return query;
	}
}
