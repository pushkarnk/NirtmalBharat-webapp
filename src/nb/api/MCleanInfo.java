package nb.api;

import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nb.util.CleanInfoUtil;
import nb.util.DropBoxUtil;
import nb.util.ReverseGeocoding;
import nb.util.TwitterUtil;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/mcleaninfo")
//cleaninfo for mobile
public class MCleanInfo {
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
		@FormDataParam("file") InputStream inputStream, @FormDataParam("file") FormDataContentDisposition fileDetail,
		@FormDataParam("username") String username, @FormDataParam("size") String size,
		@FormDataParam("comment") String comment, @FormDataParam("score") String score, 
		@FormDataParam("file") InputStream exifInputStream, @FormDataParam("tweet") String doTweet,
		@FormDataParam("latitude") String latitude, @FormDataParam("longitude") String longitude) {  //pathetic hacks all around!		
		try {
			System.err.println(fileDetail.getFileName() + " " + "username = " + username + " " + "size = " + size);
			String filename = username+System.currentTimeMillis(); 
			DropBoxUtil.storeFile(inputStream, Long.parseLong(size), filename);
			Map<String,String>locationData = ReverseGeocoding.reverseGeocode(Double.parseDouble(latitude), Double.parseDouble(longitude));
			CleanInfoUtil.updateCleanInfoTable(username,locationData,filename,comment,
					Integer.parseInt(score),Double.parseDouble(latitude),Double.parseDouble(longitude));
			if(doTweet.equals("Yes"))
				TwitterUtil.tweet(comment, username);				
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(200).entity(e.toString()).build();
		}
		return Response.status(200).entity("PASS").build(); 
	}
}