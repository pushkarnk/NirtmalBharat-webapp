package nb.api;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import nb.database.BluemixDB2Connector;
import nb.util.TwitterUtil;

@Path("/tweet")
public class Tweeter {

	private String token;
	private String tokenSecret;
	//private static MySQLConnector connection = MySQLConnector.getConnector();
	private static BluemixDB2Connector connection = BluemixDB2Connector.getConnector();
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String postTweet(@FormParam("tweet") String tweet, @FormParam("username") String username) {
		if(token == null || tokenSecret == null)
			getTokenAndSecret(username);
		
		if(token == null || tokenSecret == null ) {
			return "FAIL";
		}
		
	    ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	      .setOAuthConsumerKey(TwitterUtil.CONSUMER_KEY)
	      .setOAuthConsumerSecret(TwitterUtil.CONSUMER_SECRET)
	      .setOAuthAccessToken(token)
	      .setOAuthAccessTokenSecret(tokenSecret);
	    TwitterFactory tf = new TwitterFactory(cb.build());
	    Twitter twitter = tf.getInstance();
	    try {
			twitter.updateStatus(tweet);
			return "PASS";
		} catch (TwitterException e) {
			e.printStackTrace();
			return e.toString();
		}
	}
	
	private void getTokenAndSecret(String username) {
		String query = "SELECT TOKEN,TOKENSECRET FROM TWITTER_ACCESS WHERE USERNAME='" + username + "'";
		ResultSet result = connection.executeQuery(query);
		try {
			while(result.next()) {
				token = result.getString("TOKEN");
				tokenSecret = result.getString("TOKENSECRET");
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
