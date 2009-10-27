package open.movilforum.smssenderapi;

/**
 * @file SMSSender.java
 * @version 1.1
 * @author Ibys Technologies S.A.
 * @date   19-05-2008
 * @description Envío de SMS mediante protocolo https  
 */

import java.net.*;
import java.io.*;
import javax.net.ssl.*;

public class SMSSender {
	
	// URL del servidor https que recibe la petición
	private String url = "https://opensms.movistar.es/aplicacionpost/loginEnvio.jsp";
	
	public String SendMessage (String Login,String Pwd, String Dest, String Msg){
		
		//Código de respuesta servidor a la petición https
		String httpResponse = "";
		
		try {
						
			// Datos asociados a la petición
			String data = URLEncoder.encode("TM_ACTION", "UTF-8") + "=" + URLEncoder.encode("AUTHENTICATE", "UTF-8");
	        data += "&" + URLEncoder.encode("TM_LOGIN", "UTF-8") + "=" + URLEncoder.encode(Login, "UTF-8");
	        data += "&" + URLEncoder.encode("TM_PASSWORD", "UTF-8") + "=" + URLEncoder.encode(Pwd, "UTF-8");
	        data += "&" + URLEncoder.encode("to", "UTF-8") + "=" + URLEncoder.encode(Dest, "UTF-8");
	        data += "&" + URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(Msg, "UTF-8");
	        
			// URL de la petición
			URL url = new URL(this.url);   
			   
	        // Establece la conexión   
			HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
			
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