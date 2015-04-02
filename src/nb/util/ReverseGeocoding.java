package nb.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class ReverseGeocoding {

	public static HashMap<String,String> reverseGeocode(double latitude, double longitude)  {
		   HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&sensor=false");
		    HttpClient client = new DefaultHttpClient();
		    HttpResponse response;
		    StringBuilder stringBuilder = new StringBuilder();
		    HashMap<String,String> returnMap = new HashMap<String,String>();
		    try {
		        response = client.execute(httpGet);
		        HttpEntity entity = response.getEntity();
		        InputStream stream = entity.getContent();
		        int b;
		        while ((b = stream.read()) != -1) {
		            stringBuilder.append((char) b);
		        }
		    } catch (ClientProtocolException e) {
		    	e.printStackTrace();
		    } catch (IOException ie) {
		    	ie.printStackTrace();
		    }
		    
		    try {
				JSONObject jsonObject = new JSONObject(stringBuilder.toString());
				JSONArray addressComponents = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
			    for ( int i=0; i< addressComponents.length(); i++) {
			    	JSONObject next = addressComponents.getJSONObject(i);
			    	returnMap.put(next.getJSONArray("types").getString(0), next.getString("long_name"));
			    }
				
			} catch (JSONException e) {
				e.printStackTrace();
			}		    
		    return returnMap;
	}
}
