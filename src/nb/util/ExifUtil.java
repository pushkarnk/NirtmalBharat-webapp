package nb.util;

import java.io.InputStream;
import javaxt.io.Image;

public class ExifUtil {

	private static Image image;
	private static double[] gpsCoordinates;
	public static void setJPEGImage(InputStream imageStream) {
		image = new Image(imageStream);
		gpsCoordinates = image.getGPSCoordinate(); 
	}
	
	public static double getLatitude() {
		return gpsCoordinates[1];
	}
	
	public static double getLongitude() {
		return gpsCoordinates[0];
	}
	
}
