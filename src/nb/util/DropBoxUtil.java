package nb.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;

public class DropBoxUtil {
	
	final public static String APP_KEY 	 = "fkrp0c78hcgrufe";
	final public static String APP_SECRET 	 = "q4k7jalm9bq4kpa";
	final public static String ACCESS_TOKEN = "DNgx_edczdsAAAAAAAABswYLoG-m9n3to7bC0HsVgqJYaDUSYLebJSdqsG13tyx2";
	
	public static void storeFile(InputStream inputStream, long size, String filename) {
		DbxRequestConfig requestConfig = new DbxRequestConfig("NirmalBharat", Locale.getDefault().toString());
		DbxClient client = new DbxClient(requestConfig, ACCESS_TOKEN);
	    
		try {
			client.uploadFile("/" + filename, DbxWriteMode.add(), size, inputStream);
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    finally {
	        try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }	
	}
	
	public static String getPublicURL(String path) {
		DbxRequestConfig requestConfig = new DbxRequestConfig("NirmalBharat", Locale.getDefault().toString());
		DbxClient client = new DbxClient(requestConfig, ACCESS_TOKEN);
		try {
			return client.createTemporaryDirectUrl("/"+path).url;
		} catch (DbxException e) {
			e.printStackTrace();
			return "null";
		}

	}
}
