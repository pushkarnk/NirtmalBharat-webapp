package nb.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nb.database.BluemixDB2Connector;
import nb.util.TwitterUtil;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

@Path("/twauth")
public class TwitterOAuth {	
	
	//private static MySQLConnector connection = MySQLConnector.getConnector();
	private static BluemixDB2Connector connection = BluemixDB2Connector.getConnector();
	private static RequestToken requestToken;
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String returnOAuthURL() {
		
	    try {
	    	ConfigurationBuilder builder = new ConfigurationBuilder();
	    	builder.setOAuthConsumerKey(TwitterUtil.CONSUMER_KEY);
	    	builder.setOAuthConsumerSecret(TwitterUtil.CONSUMER_SECRET);
	    	Configuration configuration = builder.build();
	    	TwitterFactory factory = new TwitterFactory(configuration);
	    	Twitter twitter = factory.getInstance();
	    	requestToken = twitter.getOAuthRequestToken();
			return requestToken.getAuthorizationURL();
		} catch (TwitterException e) {
			e.printStackTrace();
			return "OAuth Error";
		}	  		   
	}
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String registerUser(@FormParam("pin") String pin, @FormParam("username") String username)  {
		System.err.println("register user");
		ConfigurationBuilder builder = new ConfigurationBuilder();
    	builder.setOAuthConsumerKey(TwitterUtil.CONSUMER_KEY);
    	builder.setOAuthConsumerSecret(TwitterUtil.CONSUMER_SECRET);
    	Configuration configuration = builder.build();
    	TwitterFactory factory = new TwitterFactory(configuration);
    	Twitter twitter = factory.getInstance();
		try {
			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, pin);
			connection.executeUpdate(buildQuery(accessToken,username));		
			return "PASS";
		} catch (TwitterException e) {
			e.printStackTrace();
			return "FAIL";
		}
	}
	
	private String buildQuery ( AccessToken accessToken, String username ) {
		String query = "INSERT INTO TWITTER_ACCESS VALUES"
				       + "( '" + username + "',"
				       + "'" + accessToken.getToken() + "',"
				       + "'" + accessToken.getTokenSecret() + "')";
		return query;
	}
}
