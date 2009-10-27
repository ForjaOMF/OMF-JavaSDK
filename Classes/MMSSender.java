package open.movilforum.mmssenderapi;

import java.io.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;

public class MMSSender {
	
	/**  
     * URL del servidor http que envía una redirección a un servidor numerado  
     */  
    public static final String  URLInicio = "https://www.multimedia.movistar.es";   
    
    /**  
     * URL del servidor http que recibe la petición de login  
     */  
    public static final String  URLLogin = "https://www.multimedia.movistar.es/do/dologin"; 
    
    /**  
     * URL del servidor http que recibe la petición de creación del mensaje 
     */
    public static final String  URLCreate = "https://www.multimedia.movistar.es/do/multimedia/create?l=sp-SP&v=mensajeria";
    
    /**  
     * URL del servidor http que recibe la petición de envío del mensaje 
     */
    public static final String  URLSend = "https://www.multimedia.movistar.es/do/multimedia/send?l=sp-SP&v=mensajeria";
    										
	private String login = null;
	private String pwd = null;
	/**
	 * Identificador del usuario devuelto por el servicio 
	 */
	private String sCookieSKF = null;
	/**
	 * Cookie de sesión JSESSIONID 
	 */
	private String SessionId = null;
	/**
	 * Servidor al que nos redirige en la primera petición 
	 */
	private String urlServer = null; 
	
	
	public MMSSender() {
		super();
	}
         
	/**
	 * @param login User's telephone number
	 * @param pwd User's password
	 * @return sessionId contained in JSESSIONID cookie
	 */
	public String Login (String login, String pwd) {
		this.login = login;
		this.pwd = pwd;
		String sessionId = "";
		
		if (Inicio () && SendLogin() && Create() ) {
			sessionId = this.SessionId;
		}
		
		return sessionId;
	}
   
	/**
	 * Solicita la página raiz y recibe una redirección
	 * 
	 * @return boolean
	 */
	public boolean Inicio () {
    	    	
    	HttpClient      httpClient = null;   
        //HttpMethodBase  request = null;   
        int             status = 0;   
        boolean retorno = true;
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        
        // petición mediante método Get
        GetMethod request = new GetMethod(MMSSender.URLInicio);
        
        // no realiza automáticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));   
               
            // añade las cabeceras a la petición
           
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("UA-CPU", "x86");
            request.addRequestHeader("Accept-Encoding", "gzip, deflate");
            request.addRequestHeader("Host", "www.multimedia.movistar.es");
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
            request.addRequestHeader("Cache-Control", "no-cache");
                        
            // lee el código de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
            
            // si la petición se ha realizado satisfactoriamente   
            if (status == HttpStatus.SC_MOVED_TEMPORARILY) {
            	
            	Header[] headers = request.getResponseHeaders("Set-Cookie");
                String sCookie = null;
                // extrae las cookies necesarias para la redirección
                for (int i = 0; i < headers.length; i++) {
                	if (headers[i].getValue().contains("JSESSIONID=")) {
                		sCookie = headers[i].getValue().substring(11, headers[i].getValue().indexOf(";"));
                	}
                }
                // almacena cookie de sesión en variable miembro
                this.SessionId = sCookie;            	
                // extrae la url de redirección a un servidor numerado
                this.urlServer = request.getResponseHeader("Location").getValue();
                
            } else {
            	retorno = false;
            }
            
        } catch (Exception ex) {   
            System.err.println( ex.getMessage());   
        } finally {   
            // libera la conexión   
            request.releaseConnection();   
        }   
        // devuelve true si se obtiene la URL del servidor numerado
        return retorno;
    }
	
	/**
	 * Envío de datos de login y recepción de nuevas cookies
	 * 
	 * @return
	 */
	public boolean SendLogin () {
		    	
    	HttpClient      httpClient = null;   
        HttpMethodBase  request = null;   
        int             status = 0;   
        boolean retorno = false;
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        
        // petición mediante método Get
        request = new PostMethod(MMSSender.URLLogin);
        
        // no realiza automáticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        // añade los parámetros a la petición    
        ((PostMethod) request).addParameter("TM_ACTION", "LOGIN");
        ((PostMethod) request).addParameter("variant", "mensajeria");
        ((PostMethod) request).addParameter("locale", "sp-SP");
        ((PostMethod) request).addParameter("client", "html-msie-7-winxp");
        ((PostMethod) request).addParameter("directMessageView", "");
        ((PostMethod) request).addParameter("uid", "");
        ((PostMethod) request).addParameter("uidl", "");
        ((PostMethod) request).addParameter("folder", "");
        ((PostMethod) request).addParameter("remoteAccountUID", "");
        ((PostMethod) request).addParameter("login", "1");
        ((PostMethod) request).addParameter("TM_LOGIN", this.login);
        ((PostMethod) request).addParameter("TM_PASSWORD", this.pwd);
        
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));   
            
            // añade las cabeceras a la petición
            request.addRequestHeader("Referer", this.urlServer);
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            request.addRequestHeader("UA-CPU", "x86");
            request.addRequestHeader("Accept-Encoding", "gzip, deflate");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
            request.addRequestHeader("Host", "multimedia.movistar.es");
            //request.addRequestHeader("Content-Length", "174");
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("Cache-Control", "no-cache");
            request.addRequestHeader("Cookie", "JSESSIONID="+this.SessionId);
                       
            // lee el código de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la petición se ha realizado satisfactoriamente   
            if (status == HttpStatus.SC_MOVED_TEMPORARILY) {   
  
                Header[] headers = request.getResponseHeaders("Set-Cookie");
                String sCookie = null;
                // extrae las cookies necesarias para la redirección
                for (int i = 0; i < headers.length; i++) {
                	if (headers[i].getValue().contains("skf=")) {
                		sCookie = headers[i].getValue().substring(0, headers[i].getValue().indexOf(";") );
                	}
                }
                this.sCookieSKF = sCookie;
                retorno = true;
            }
            
        } catch (Exception ex) {   
            System.err.println( ex.getMessage());   
        } finally {   
            // libera la conexión   
            request.releaseConnection();   
        }   
        // devuelve true si login y password son correctos
        return retorno;
	}
    	

	/**
	 * Descarga de página y creación del mensaje obviando respuesta
	 * 
	 * @return
	 */
	public boolean Create() {
    	HttpClient      httpClient = null;   
        HttpMethodBase  request = null;   
        int             status = 0;   
        boolean retorno = true;
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        
        // petición mediante método GET
        request = new GetMethod(MMSSender.URLCreate);
        
        // no realiza automáticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));   
            
            // añade las cabeceras a la petición   
            request.addRequestHeader("Referer", "http://multimedia.movistar.es/do/messages/inbox");       
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("UA-CPU", "x86"); 
            request.addRequestHeader("Accept-Encoding", "gzip, deflate");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
            request.addRequestHeader("Host", "multimedia.movistar.es");
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("Cookie", "activeLogin=true; JSESSIONID=" + this.SessionId + "; " + this.sCookieSKF);
            
            // lee el código de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la petición se ha realizado satisfactoriamente   
            if (status == HttpStatus.SC_MOVED_TEMPORARILY) {
            	retorno = true;
            } else {
            	retorno = false;
            }
            
        } catch (Exception ex){   
            System.err.println("Error\t: " + ex.getMessage());   
        } finally {   
            // libera la conexión  
            request.releaseConnection();   
        }   
        
        return retorno;
	}
    
    
    public boolean InsertImage (String csObjPath) {
    	HttpClient      httpClient = null;   
        int             status = 0; 
        boolean retorno = true;
        
        // variables para la construcción del mensaje
        String sData = "";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "---------------------------7d811c60180";
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        
        // petición mediante método POST
        PostMethod request = new PostMethod("https://www.multimedia.movistar.es/do/multimedia/uploadEnd");
        
        // no realiza automáticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        try {
        	
			String sContent = "";
			String sFichero = csObjPath;
			File fichero = new File(sFichero);
			
			// Obtiene la extensión del archivo			
			String ext = fichero.getName().substring(fichero.getName().lastIndexOf(".")+1);			
			String tipo = getContentType(ext,"imagen");
			if (tipo == null) {
				System.out.println("Extension not allowed: " + ext);
			}

			System.out.println("Inserting image...");
			
			// añade los parámetros a la petición 
	        String sContentDisposition =  twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"file\"; filename=\"" + csObjPath + "\"" + lineEnd;
	        String sContentType =  "Content-Type: " + tipo + lineEnd + lineEnd;
	        String sEndRequest =  lineEnd + twoHyphens + boundary + twoHyphens + lineEnd;
			
			// Obtiene el tamaño del fichero a enviar
			int tamFichero = (int)fichero.length();
			FileInputStream fis = new FileInputStream(fichero);  
			byte[] buffer = new byte[tamFichero];
			fis.read(buffer);  
			sContent = new String(buffer,0);
			fis.close();
			
            sData = sContentDisposition + sContentType + sContent + sEndRequest;
        	
            ((PostMethod) request).setRequestBody(sData);
            
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));   
             
            // añade las cabeceras a la petición
            request.addRequestHeader("Referer", "http://multimedia.movistar.es/do/multimedia/upload?l=sp-SP&v=mensajeria");
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("Content-Type", "multipart/form-data; boundary=---------------------------7d811c60180");
            request.addRequestHeader("UA-CPU", "x86");
            request.addRequestHeader("Accept-Encoding", "gzip, deflate");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
            request.addRequestHeader("Host", "multimedia.movistar.es");
            request.addRequestHeader("Content-Length", "" + sData.length() + "");
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("Cache-Control", "no-cache");
            request.addRequestHeader("Cookie", "activeLogin=true; JSESSIONID=" + this.SessionId + "; " + this.sCookieSKF);
            
       	                 
            // lee el código de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la respuesta no es satisfactoria  
            if (status != HttpStatus.SC_OK) {
            	// Se ha producido un error 
            	retorno = false;
            }
            
        } catch (FileNotFoundException fnfex){
        	// Archivo no encontrado
               
        } catch (IOException ioe) {  
   			 // Error al leer   
   		
        } catch (Exception ex){   
            // Error
        	
        } finally {   
            // libera la conexión   
            request.releaseConnection();   
        }   
        
        return retorno;
    }
    
    public boolean InsertAudio (String csObjPath) {
    	HttpClient      httpClient = null;   
        int             status = 0; 
        boolean retorno = true;
        
     // variables para la construcción del mensaje
        String sData = "";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "---------------------------7d811c60180";
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        
        // petición mediante método POST
        PostMethod request = new PostMethod("https://www.multimedia.movistar.es/do/multimedia/uploadEnd");
        
        // no realiza automáticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        try { 
        	
        	String sContent = "";
			String sFichero = csObjPath;
			File fichero = new File(sFichero);
			
			// Obtiene la extensión del archivo			
			String ext = fichero.getName().substring(fichero.getName().lastIndexOf(".")+1);			
			String tipo = getContentType(ext,"audio");
			if (tipo == null) {
				System.out.println("Extension not allowed: " + ext);
			}
			
			System.out.println("Inserting audio...");
			
			// Añade los parámetros a la petición 
	        String sContentDisposition =  twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"file\"; filename=\"" + csObjPath + "\"" + lineEnd;
	        String sContentType =  "Content-Type: " + tipo + lineEnd + lineEnd;
	        String sEndRequest =  lineEnd + twoHyphens + boundary + twoHyphens + lineEnd;
	        
			// Obtiene el tamaño del fichero a enviar
			int tamFichero = (int)fichero.length();
			FileInputStream fis = new FileInputStream(fichero);  
			byte[] buffer = new byte[tamFichero];
			fis.read(buffer);  
			sContent = new String(buffer,0);
			fis.close();
			
            sData = sContentDisposition + sContentType + sContent + sEndRequest;
        	
            ((PostMethod) request).setRequestBody(sData);
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));   
             
            // añade las cabeceras a la petición
            request.addRequestHeader("Referer", "http://multimedia.movistar.es/do/multimedia/upload?l=sp-SP&v=mensajeria");
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("Content-Type", "multipart/form-data; boundary=---------------------------7d811c60180");
            request.addRequestHeader("UA-CPU", "x86");
            request.addRequestHeader("Accept-Encoding", "gzip, deflate");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
            request.addRequestHeader("Host", "multimedia.movistar.es");
            request.addRequestHeader("Content-Length", "" + sData.length() + "");
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("Cache-Control", "no-cache");
            request.addRequestHeader("Cookie", "activeLogin=true; JSESSIONID=" + this.SessionId + "; " + this.sCookieSKF);
            
            // lee el código de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la respuesta no es satisfactoria   
            if (status != HttpStatus.SC_OK) {
            	// Se ha producido un error
            	retorno = false;
            } 
            
        } catch (FileNotFoundException fnfex){
        	// Archivo no encontrado
               
        } catch (IOException ioe) {  
   			 // Error al leer   
   		
        } catch (Exception ex){   
            // Error
        	
        } finally {   
            // libera la conexión   
            request.releaseConnection();   
        }   
        
        return retorno;
    }
    
    public boolean InsertVideo (String csObjPath) {
    	HttpClient      httpClient = null;   
        int             status = 0; 
        boolean retorno = true;
         
        // variables para la construcción del mensaje
        String sData = "";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "---------------------------7d811c60180";
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        
        // petición mediante método POST
        PostMethod request = new PostMethod("https://www.multimedia.movistar.es/do/multimedia/uploadEnd");
        
        // no realiza automáticamente el seguimiento de las redirecciones        
        request.setFollowRedirects(false); 
        
        try { 
        	String sContent = "";
			String sFichero = csObjPath;
			File fichero = new File(sFichero);
			
			// Obtiene la extensión del archivo			
			String ext = fichero.getName().substring(fichero.getName().lastIndexOf(".")+1);			
			String tipo = getContentType(ext,"video");
			if (tipo == null) {
				System.out.println("Extension not allowed: " + ext);
			}
			
			System.out.println("Inserting video...");
	
			// añade los parámetros a la petición 
	        String sContentDisposition =  twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"file\"; filename=\"" + csObjPath + "\"" + lineEnd;
	        String sContentType =  "Content-Type: " + tipo + lineEnd + lineEnd;
	        String sEndRequest =  lineEnd + twoHyphens + boundary + twoHyphens + lineEnd;
	        
			// Obtiene el tamaño del fichero a enviar
			int tamFichero = (int)fichero.length();
			FileInputStream fis = new FileInputStream(fichero);  
			byte[] buffer = new byte[tamFichero];
			fis.read(buffer);  
			sContent = new String(buffer,0);
			fis.close();
			
            sData = sContentDisposition + sContentType + sContent + sEndRequest;
        	
            ((PostMethod) request).setRequestBody(sData);
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));   
             
            // añade las cabeceras a la petición
            request.addRequestHeader("Referer", "http://multimedia.movistar.es/do/multimedia/upload?l=sp-SP&v=mensajeria");
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("Content-Type", "multipart/form-data; boundary=---------------------------7d811c60180");
            request.addRequestHeader("UA-CPU", "x86");
            request.addRequestHeader("Accept-Encoding", "gzip, deflate");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
            request.addRequestHeader("Host", "multimedia.movistar.es");
            request.addRequestHeader("Content-Length", "" + sData.length() + "");
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("Cache-Control", "no-cache");
            request.addRequestHeader("Cookie", "activeLogin=true; JSESSIONID=" + this.SessionId + "; " + this.sCookieSKF);
            
            // lee el código de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la respuesta no es satisfactoria   
            if (status != HttpStatus.SC_OK) {
            	// Se ha producido un error
            	retorno = false; 
            }
            
        } catch (FileNotFoundException fnfex){
        	// Archivo no encontrado
               
        } catch (IOException ioe) {  
   			 // Error al leer   
   		
        } catch (Exception ex){   
            // Error
        	
        } finally {   
            // libera la conexión   
            request.releaseConnection();   
        }   
        
        return retorno;
    }
    
    public boolean SendMessage (String Subject, String Dest, String Msg) {
    	HttpClient      httpClient = null;   
        int             status = 0; 
        boolean retorno = true;
                
        // variables para la construcción del mensaje
        String sData = "";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "---------------------------7d811c60180";
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
                      
        // petición mediante método POST
        PostMethod request = new PostMethod(MMSSender.URLSend);
               
        // no realiza automáticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        // añade los parámetros a la petición 
       String sBaseFolder =  lineEnd + twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"basefolder\"" + lineEnd + lineEnd + lineEnd;
       String sFolder =  twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"folder\"" + lineEnd + lineEnd + lineEnd;
       String sid =  twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"id\"" + lineEnd + lineEnd + lineEnd;
       String sPublic =  twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"public\"" + lineEnd + lineEnd + lineEnd;
       String sName =  twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"name\"" + lineEnd + lineEnd + lineEnd;
       String sUrl =  twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"url\"" + lineEnd + lineEnd + lineEnd;
       String sOwner =  twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"owner\"" + lineEnd + lineEnd + lineEnd;
       String sDeferredDate =  twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"deferredDate\"" + lineEnd + lineEnd + lineEnd;
       String sRequestReturnReceipt =  twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"requestReturnReceipt\"" + lineEnd + lineEnd + lineEnd;
       String sTo =  twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"to\"" + lineEnd + lineEnd + Dest+ lineEnd;
       String sSubject =  twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"subject\"" + lineEnd + lineEnd + Subject+ lineEnd;
       String sText =  twoHyphens + boundary + lineEnd + "Content-Disposition: form-data; name=\"text\"" + lineEnd + lineEnd + Msg+ lineEnd;
       String sEndRequest =  twoHyphens + boundary + twoHyphens + lineEnd;       
       
       sData = sBaseFolder + sFolder + sid + sPublic + sName + sUrl + sOwner + sDeferredDate + sRequestReturnReceipt + sTo + sSubject + sText + sEndRequest;
              
       ((PostMethod) request).setRequestBody(sData); 
       
                             
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));   
            
            // añade las cabeceras a la petición
            request.addRequestHeader("Referer", "http://multimedia.movistar.es/do/multimedia/show");
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("Content-Type", "multipart/form-data; boundary=---------------------------7d811c60180");
            request.addRequestHeader("UA-CPU", "x86");
            request.addRequestHeader("Accept-Encoding", "gzip, deflate");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
            request.addRequestHeader("Host", "multimedia.movistar.es");
            request.addRequestHeader("Content-Length", "" + sData.length() + "");
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("Cache-Control", "no-cache");
            request.addRequestHeader("Cookie", "activeLogin=true; JSESSIONID=" + this.SessionId + "; " + this.sCookieSKF);
          
            // lee el código de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
             
            // petición realizada satisfactoriamente   
            if (status == HttpStatus.SC_OK) {
            	
               // Declaración de variables locales
               BufferedReader reader = null; 
               String line = null;
               String respuesta = "";
               
               // recupera la respuesta del servidor
	           	reader = new BufferedReader(new InputStreamReader(request.getResponseBodyAsStream(), request.getResponseCharSet()));   
	            line   = reader.readLine();
	            while (line != null) {   
		                respuesta += line+"\n";
		                line = reader.readLine();
	            }
	            if(respuesta.indexOf("Tu mensaje ha sido enviado")!=-1) {
	            	System.out.println("Your message has been sent");
	            }
            
            } else {
            	// recibe una redirección 
            	retorno = false;
            }
            
            
        } catch (Exception ex){   
            System.err.println("Error\t: " + ex.getMessage());   
               
            ex.printStackTrace();   
        } finally {   
            // libera la conexión   
            request.releaseConnection();   
        }   
        
        return retorno;
    	
    }
    
    public boolean Logout () {    	
    	HttpClient      httpClient = null;   
        HttpMethodBase  request = null;   
        int             status = 0;   
        boolean retorno = false;
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
         
        // petición mediante método Get
        request = new PostMethod("https://www.multimedia.movistar.es/do/logout?l=sp-SP&v=mensajeria");
        
        // no realiza automáticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        // añade los parámetros a la petición    
        ((PostMethod) request).addParameter("TM_ACTION", "LOGOUT");
        
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));   
                      
            // añade las cabeceras a la petición
            request.addRequestHeader("Referer", "http://multimedia.movistar.es/do/messages/inbox");
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            request.addRequestHeader("UA-CPU", "x86");
            request.addRequestHeader("Accept-Encoding", "gzip, deflate");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
            request.addRequestHeader("Host", "multimedia.movistar.es");
            request.addRequestHeader("Content-Length", "16");
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("Cache-Control", "no-cache");
            request.addRequestHeader("Cookie", "activeLogin=true; JSESSIONID=" + this.SessionId + "; " + this.sCookieSKF);
                       
            // lee el código de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la petición se ha realizado satisfactoriamente   
            if (status == HttpStatus.SC_MOVED_TEMPORARILY) {   
            	// se ha realizado logout correctamente
                retorno = true;
            } else {
            	retorno = false;
            }
            
        } catch (Exception ex) {   
            System.err.println( ex.getMessage());   
        } finally {   
            // libera la conexión   
            request.releaseConnection();   
        }   
        // devuelve true si login y password son correctos
        return retorno;
	}
    
    
    public String getContentType (String ext, String tipo) {
    	String contentType = "";    	
    	HashMap<String,String> contentTypes = new HashMap<String,String>();
    	if(tipo== "imagen") {
    		// Tipos para imagen
    		contentTypes.put("gif","image/gif");
    		contentTypes.put("jpg","image/pjpeg");
    		contentTypes.put("jpeg","image/pjpeg");
    		contentTypes.put("png","image/x-png");
    		contentTypes.put("bmp","image/bmp");    		
    	}else if (tipo== "audio") {
    		// Tipos para audio
    		contentTypes.put("mid","audio/mid");
    		contentTypes.put("wav","audio/wav");
    		contentTypes.put("mp3","audio/mpeg");
    	}else {
    		// Tipos para video
    		contentTypes.put("avi","video/avi");
    		contentTypes.put("asf","video/x-ms-asf");
    		contentTypes.put("mpg","video/mpeg");
    		contentTypes.put("mpeg","video/mpeg");
    		contentTypes.put("wmv","video/x-ms-wmv");
    	}
    	
    	contentType = contentTypes.get(ext.toLowerCase());    	
    	
    	return contentType;
    }
    
    
}
