package nb.api;

import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nb.util.CleanInfoUtil;
import nb.util.DropBoxUtil;
import nb.util.ExifUtil;
import nb.util.ReverseGeocoding;
import nb.util.TwitterUtil;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;


@Path("/cleaninfo")
public class CleanInfo {
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
		@FormDataParam("file") InputStream inputStream, @FormDataParam("file") FormDataContentDisposition fileDetail,
		@FormDataParam("username") String username, @FormDataParam("size") String size,
		@FormDataParam("comment") String comment, @FormDataParam("score") String score, 
		@FormDataParam("file") InputStream exifInputStream, @FormDataParam("tweet") String doTweet ) {  //cheap hack
		
		try {
			System.err.println(fileDetail.getFileName() + " " + "username = " + username + " " + "size = " + size);
			String filename = username+System.currentTimeMillis(); 
			DropBoxUtil.storeFile(inputStream, Long.parseLong(size), filename);
			ExifUtil.setJPEGImage(exifInputStream);
			Map<String,String>locationData =
					ReverseGeocoding.reverseGeocode(ExifUtil.getLatitude(), ExifUtil.getLongitude());
			CleanInfoUtil.updateCleanInfoTable(username,locationData,filename,comment,
					Integer.parseInt(score),ExifUtil.getLatitude(),ExifUtil.getLongitude());
			if(doTweet.equals("Yes") && TwitterUtil.tweet(comment, username))
				System.err.println("Tweeted");
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(200).entity(e.toString()).build();
		}
		return Response.status(200).entity("PASS").build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnCleanInfo(@QueryParam("name")String name, @QueryParam("type")String type) {
		String responseJSON = CleanInfoUtil.getResults(name,type);
		return Response.status(200).entity(responseJSON).build();
	}
}

