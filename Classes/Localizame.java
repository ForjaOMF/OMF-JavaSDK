package open.movilforum.localizameapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.httpclient.*;   
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
	  
/**  
 *     
 * @author   
 */  
public class Localizame {	           
    /**  
     * URL del servidor http que recibe la petición de login  
     */  
    public static final String  TargetURLLogin = "https://www.localizame.movistar.es/login.do";   
    
    /**  
     * URL del servidor http que recibe la petición de nuevo usuario 
     */
    public static final String  URLNuevoUsuario = "https://www.localizame.movistar.es/nuevousuario.do";   
    
    /**  
     * URL del servidor http que recibe la petición de búsqueda 
     */
    public static final String  URLBuscar =  "https://www.localizame.movistar.es/buscar.do";    
    
    /**  
     * URL del servidor http que recibe la petición de nuevo localizador 
     */
    public static final String  URLNuevoLocalizador = "https://www.localizame.movistar.es/insertalocalizador.do";   
    
    /**  
     * URL del servidor http que recibe la petición para borrar localizador 
     */
    public static final String  URLBorraLocalizador = "https://www.localizame.movistar.es/borralocalizador.do";   
    
    /**  
     * URL del servidor http que recibe la petición para borrar localizador 
     */
    public static final String  URLLogout = "https://www.localizame.movistar.es/logout.do";   
    
    
	private String login = null;
	private String pwd = null;
	private String sCookie = null;
         
    public boolean Login (String login, String pwd) {
    	this.login = login;
		this.pwd = pwd;
    	
    	HttpClient      httpClient = null;   
        HttpMethodBase  request = null;   
        int             status = 0;   
        boolean retorno = true;
        
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        
        // petición mediante método Post
        request = new PostMethod(Localizame.TargetURLLogin);
        
        // no realiza automáticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        // añade los parámetros a la petición    
        ((PostMethod) request).addParameter("usuario", this.login); 
        ((PostMethod) request).addParameter("clave", this.pwd);
        ((PostMethod) request).addParameter("submit.x", "36");
        ((PostMethod) request).addParameter("submit.y", "6");
        
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));   
               
            // añade las cabeceras a la petición   
            request.addRequestHeader("Content-type", "application/x-www-form-urlencoded");       
            request.addRequestHeader("Host", "www.localizame.movistar.es");
            request.addRequestHeader("Accept-Encoding", "identity");
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, application/x-shockwave-flash, */*"); 
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0; .NET CLR 2.0.50727)");
            
             // lee el código de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la petición se ha realizado satisfactoriamente   
            if (status == HttpStatus.SC_OK) {   

            	Header[] headers = request.getResponseHeaders("Set-Cookie");
                String sCookie = null;
                // extrae las cookies necesarias
                for (int i = 0; i < headers.length; i++) {
                	sCookie = headers[i].getValue();
                }
                this.sCookie = sCookie;
                // llamada a NuevoUsuario
                retorno = NuevoUsuario();
                
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

    public boolean NuevoUsuario() {
    	HttpClient      httpClient = null;   
        HttpMethodBase  request = null;   
        int             status = 0;   
        boolean retorno = true;
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        
        // petición mediante método GET
        request = new GetMethod(Localizame.URLNuevoUsuario);
        
        // no realiza automáticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,    
                                new DefaultHttpMethodRetryHandler(3, true));   
               
            // añade las cabeceras a la petición 
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("Host", "www.localizame.movistar.es");
            request.addRequestHeader("Accept-Encoding", "identity");
            request.addRequestHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, application/x-shockwave-flash, */*"); 
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0; .NET CLR 2.0.50727)");
            request.addRequestHeader("Referer", Localizame.TargetURLLogin);
            request.addRequestHeader("Cookie", this.sCookie);
            
            // lee el código de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la respuesta no es satisfactoria   
            if (status != HttpStatus.SC_OK) {
            	retorno = false;
            }
            
        } catch (Exception ex){   
            //System.err.println("Error\t: " + ex.getMessage());   
        } finally {   
            // libera la conexión  
            request.releaseConnection();   
        }   
        
        return retorno;
	}
    
    
    public String Locate (String sTelefono) {
    	HttpClient httpClient = null;   
        HttpMethodBase request = null;   
        int status = 0;   
        BufferedReader reader = null; 
        String line = null;
        String respuesta = "";
        String localizacion = "";
        boolean retorno = true;
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        String sUrl = Localizame.URLBuscar;
        
        // petición mediante método POST
        request = new PostMethod(sUrl);
        
        // no realiza automáticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        // añade los parámetros a la petición    
        ((PostMethod) request).addParameter("telefono", sTelefono); 
        
        
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));   
               
            // añade las cabeceras a la petición 
            request.addRequestHeader("Content-type", "application/x-www-form-urlencoded");
            request.addRequestHeader("Host", "www.localizame.movistar.es");
            request.addRequestHeader("Accept-Encoding", "identity");
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, application/x-shockwave-flash, */*"); 
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0; .NET CLR 2.0.50727)");
            request.addRequestHeader("Cookie", this.sCookie);
            
            // lee el código de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la petición se ha realizado satisfactoriamente   
            if (status == HttpStatus.SC_OK) {
            	
            	// recupera la respuesta del servidor
            	reader = new BufferedReader(new InputStreamReader(request.getResponseBodyAsStream(), request.getResponseCharSet()));   
	            line   = reader.readLine(); 
	            while (line != null) {   
		                respuesta += line;
		                line = reader.readLine();
	            }
            	Integer iInicioRes = respuesta.indexOf(sTelefono);
            	Integer iFinRes = respuesta.indexOf("metros",iInicioRes);
            	// recupera el texto
            	if((iInicioRes > 0) && (iFinRes>0)) {
            		localizacion = respuesta.substring(iInicioRes, iFinRes);
            		//System.out.println("Encontrado?: " + localizacion);	
            	} else {
            		localizacion = "The number has not been located.";
            		//System.out.println("The number has not been located.");
            		retorno = false;
            	}
            } else {
            	retorno = false;
            }
            
        } catch (Exception ex) {   
            //System.err.println("Error\t: " + ex.getMessage());   
        } finally {   
            // libera la conexión   
            request.releaseConnection();   
        }   
        
        return localizacion;
    }
    
    
    public boolean Authorize(String sTelefono) {
    	HttpClient      httpClient = null;   
        HttpMethodBase  request = null;   
        int             status = 0;   
        boolean retorno = true;
        String sUrl = Localizame.URLNuevoLocalizador + "?telefono=" + sTelefono + "&submit.x=40&submit.y=5";
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        
        // petición mediante método GET
        request = new GetMethod(sUrl);
        
        // no realiza automáticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,    
                                new DefaultHttpMethodRetryHandler(3, true));   
               
            // añade las cabeceras a la petición 
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("Host", "www.localizame.movistar.es");
            request.addRequestHeader("Accept-Encoding", "identity");
            request.addRequestHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, application/x-shockwave-flash, */*"); 
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("Referer", "http://www.localizame.movistar.es/buscalocalizadorespermisos.do");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0; .NET CLR 2.0.50727)");
            request.addRequestHeader("Cookie", this.sCookie);
            
            // lee el código de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la petición se ha realizado satisfactoriamente   
            if (status == HttpStatus.SC_OK) {
            	// acceso correcto
            	System.out.println("Number authorized: " + sTelefono);
            } else {
            	retorno = false;
            }
            
        } catch (Exception ex){   
            //System.err.println("Error\t: " + ex.getMessage());   
        } finally {   
            // libera la conexión  
            request.releaseConnection();   
        }   
        
        return retorno;
	}
    
    
    public boolean Unauthorize(String sTelefono) {
    	HttpClient      httpClient = null;   
        HttpMethodBase  request = null;   
        int             status = 0;   
        boolean retorno = true;
        String sUrl = Localizame.URLBorraLocalizador + "?telefono=" + sTelefono + "&submit.x=44&submit.y=8";
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        
        // petición mediante método GET
        request = new GetMethod(sUrl);
        
        // no realiza automáticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,    
                                new DefaultHttpMethodRetryHandler(3, true));   
               
            // añade las cabeceras a la petición 
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("Host", "www.localizame.movistar.es");
            request.addRequestHeader("Accept-Encoding", "identity");
            request.addRequestHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, application/x-shockwave-flash, */*"); 
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("Referer", "http://www.localizame.movistar.es/buscalocalizadorespermisos.do");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0; .NET CLR 2.0.50727)");
            request.addRequestHeader("Cookie", this.sCookie);
            
            // lee el código de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la petición se ha realizado satisfactoriamente   
            if (status == HttpStatus.SC_OK) {
            	// acceso correcto
            	System.out.println("Number unauthorized: " + sTelefono);
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
    
    public boolean Logout() {
    	HttpClient      httpClient = null;   
        HttpMethodBase  request = null;   
        int             status = 0;   
        boolean retorno = true;
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        
        // petición mediante método GET
        request = new GetMethod(Localizame.URLLogout);
        
        // no realiza automáticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,    
                                new DefaultHttpMethodRetryHandler(3, true));   
               
            // añade las cabeceras a la petición 
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("Host", "www.localizame.movistar.es");
            request.addRequestHeader("Accept-Encoding", "identity");
            request.addRequestHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, application/x-shockwave-flash, */*"); 
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0; .NET CLR 2.0.50727)");
            request.addRequestHeader("Cookie", this.sCookie);
            
            // lee el código de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la petición se ha realizado satisfactoriamente   
            if (status == HttpStatus.SC_OK) {
            	// acceso correcto
            	System.out.println("Logout");
            } else {
            	retorno = false;
            }
            
        } catch (Exception ex){   
            //System.err.println("Error\t: " + ex.getMessage());   
        } finally {   
            // libera la conexión  
            request.releaseConnection();   
        }   
        
        return retorno;
	}

}
