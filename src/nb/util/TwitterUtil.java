package nb.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import nb.database.BluemixDB2Connector;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterUtil {	
	final public static String CONSUMER_KEY = "w6E3FlCBCWmL2u2P8BY374Nsb";
	final public static String CONSUMER_SECRET = "QMAX62lY8lgMlfeEbq9jdNKB8ymrmrBL8ITMIIJdaV1HOH4EQg";
	
	//private static MySQLConnector connection = MySQLConnector.getConnector();
	private static BluemixDB2Connector connection = BluemixDB2Connector.getConnector();
		

	public static boolean tweet(String tweet, String username) {
		String []  tns = getTokenAndSecret(username);
		if(tns == null)
			return false;
		String token = tns[0];
		String tokenSecret = tns[1];
		
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
			return true;
		} catch (TwitterException e) {
			e.printStackTrace();
			return false;
		}
	}
		
	private static String[] getTokenAndSecret(String username) {
		String query = "SELECT TOKEN,TOKENSECRET FROM TWITTER_ACCESS WHERE USERNAME='" + username + "'";
		ResultSet result = connection.executeQuery(query);
		String results[] = new String[2];
		try {
			while(result.next()) {
				results[0] = result.getString("TOKEN");
				results[1] = result.getString("TOKENSECRET");
			}
			return results;
		} catch(SQLException e) {			
			e.printStackTrace();
			return null;
		}
	}
}


