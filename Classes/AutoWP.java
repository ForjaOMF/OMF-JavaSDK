package open.movilforum.autowpapi;

/**
 * @file AutoWP.java
 * @version 1.1
 * @author Ibys Technologies S.A.
 * @date   19-05-2008
 * @description Class to send a WapPush message to the user’s own number  
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class AutoWP {
	
	// URL del servidor https que recibe la petición
	private String url = "http://open.movilforum.com/apis/autowap";
	
	public String sendAutoWP (String Login,String Pwd, String URL, String Text){
		
		//Código de respuesta servidor a la petición https
		String httpResponse = "";
		
		try {
			
			// Datos asociados a la petición
			String data = URLEncoder.encode("TME_USER", "UTF-8") + "=" + URLEncoder.encode(Login, "UTF-8");
	        data += "&" + URLEncoder.encode("TME_PASS", "UTF-8") + "=" + URLEncoder.encode(Pwd, "UTF-8");
	        data += "&" + URLEncoder.encode("WAP_Push_URL", "UTF-8") + "=" + URLEncoder.encode(URL, "UTF-8");
	        data += "&" + URLEncoder.encode("WAP_Push_Text", "UTF-8") + "=" + URLEncoder.encode(Text, "UTF-8");
	        
			// URL de la petición
			URL url = new URL(this.url);   
			   
	        // Establece la conexión   
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			
	        // Establece las cabeceras de la petición http
			connection.addRequestProperty("Content-type", "application/x-www-form-urlencoded");       
			connection.addRequestProperty("Accept-Encoding", "gzip, deflate"); 
			connection.addRequestProperty("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*"); 
			connection.addRequestProperty("Connection", "Keep-Alive");
			 
	        // Envia los datos asociados a la petición   
	        connection.setDoOutput(true);
	        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());   
	        wr.write(data);
	        wr.flush();   
	        
	           
	        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){   
	            // Lee el contenido de la respuesta y lo retorna   
	            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));   
	            String         line   = reader.readLine();   
	            while (line != null) {   
	                httpResponse = line.toString();
	                line = reader.readLine();     
	            }   
	            reader.close();   
	        } else {   
	           httpResponse = connection.getResponseMessage();
	        }   
	         
	        
	        // Cierra la conexión   
	        connection.disconnect();   

		} catch (MalformedURLException me) {
			System.out.println("URL erronea");
		} catch (IOException ioe) {
			System.out.println("Error IO: " + ioe.getMessage());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		
		// Retorna el contenido de la respuesta
		return httpResponse;
	}
}