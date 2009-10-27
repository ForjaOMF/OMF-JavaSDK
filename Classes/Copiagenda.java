package open.movilforum.copiagendaapi;

import java.util.List;
import java.util.ArrayList;
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
public class Copiagenda {   
           
    /**  
     * URL del servidor https que recibe la petici�n de login  
     */  
    public static final String  TargetURLLogin = "https://copiagenda.movistar.es/cp/ps/Main/login/Agenda";   
    
    /**  
     * URL del servidor https que recibe la petici�n de reautenticaci�n 
     */
    public static final String  TargetURLAutthenticate = "https://copiagenda.movistar.es/cp/ps/Main/login/Authenticate";   
    
    /**  
     * URL del servidor https que recibe la petici�n de datos 
     */
    public static final String  TargetURLDatos = "https://copiagenda.movistar.es/cp/ps/PSPab/preferences/ExportContacts?d=movistar.es&c=yes";   
    
	private String login = null;
	private String pwd = null;
	private String urlRedireccion = null;
	private String sCookie = null;
	private String password;
	private String sReautenticacionT = null;
	private List<String> contactList = null;
	
	public Copiagenda() {
		super();
	}
         
    /**
     * @param login
     * @param pwd
     */
    public List<String> RetrieveContacts (String login, String pwd) {
    	this.login = login;
		this.pwd = pwd;
    	
		if(Login()) {
			if(Redireccion()) {
				if(Reautenticacion()) {
					if(! Datos()) {
			    		System.out.println("Error when reading the contact list");   		
			    	}
				}else  {
					System.out.println("Error during the authentication");   
		    	}
	    	}else  {
	    		System.out.println("Error during the redirection");  
	    	}
    		
    	}else  {
    		System.out.println("Username or password not valid"); 
    	}
    		
		/*
    	if(! Login() && ! Redireccion() && ! Reautenticacion() && ! Datos()) {
    		System.out.println("Username or password not valid");  
    		return this.contactList ;
    	}
    	*/
		
    	// devuelve la lista de contactos
    	return this.contactList ;
    }
   
    public boolean Login () {
    	HttpClient      httpClient = null;   
        HttpMethodBase  request = null;   
        int             status = 0;   
        boolean retorno = true;
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        
        // petici�n mediante m�todo Post
        request = new PostMethod(Copiagenda.TargetURLLogin);
        
        // no realiza autom�ticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        // a�ade los par�metros a la petici�n    
        ((PostMethod) request).addParameter("TM_ACTION", "LOGIN"); 
        ((PostMethod) request).addParameter("TM_LOGIN", this.login);
        ((PostMethod) request).addParameter("TM_PASSWORD", this.pwd);
        
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));   
               
            // a�ade las cabeceras a la petici�n   
            request.addRequestHeader("Content-type", "application/x-www-form-urlencoded");       
            request.addRequestHeader("Accept-Encoding", "gzip, deflate");
            request.addRequestHeader("Host", "copiagenda.movistar.es"); 
            request.addRequestHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*"); 
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0; .NET CLR 2.0.50727)");
            
            // lee el c�digo de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la petici�n se ha realizado satisfactoriamente   
            if (status == HttpStatus.SC_MOVED_TEMPORARILY) {   

                Header[] headers = request.getResponseHeaders("Set-Cookie");
                String sCookie = null;
                // extrae las cookies necesarias para la redirecci�n
                for (int i = 0; i < headers.length; i++) {
                	if (headers[i].getValue().contains("s=")) {
                		sCookie = headers[i].getValue().substring(0, headers[i].getValue().indexOf(";") + 2);
                	}
                	if (headers[i].getValue().contains("skf=")) {
                		sCookie += headers[i].getValue().substring(0, headers[i].getValue().indexOf(";"));
                	}
                }
                
                this.sCookie = sCookie;
                // extrae la url de redirecci�n
                this.urlRedireccion = request.getResponseHeader("Location").getValue();
                
            } else {
            	retorno = false;
            }
            
        } catch (Exception ex) {   
            System.err.println( ex.getMessage());   
        } finally {   
            // libera la conexi�n   
            request.releaseConnection();   
        }   
        // devuelve true si login y password son correctos
        return retorno;
    }

    public boolean Redireccion() {
    	HttpClient      httpClient = null;   
        HttpMethodBase  request = null;   
        int             status = 0;   
        boolean retorno = true;
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        
        // petici�n mediante m�todo GET
        request = new GetMethod(this.urlRedireccion);
        
        // no realiza autom�ticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,    
                                new DefaultHttpMethodRetryHandler(3, true));   
               
            // a�ade las cabeceras a la petici�n   
            request.addRequestHeader("Content-type", "application/x-www-form-urlencoded");       
            request.addRequestHeader("Accept-Encoding", "gzip, deflate");
            request.addRequestHeader("Host", "copiagenda.movistar.es"); 
            request.addRequestHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*"); 
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0; .NET CLR 2.0.50727)");
            request.addRequestHeader("Cookie", this.sCookie);
            
            // lee el c�digo de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la petici�n se ha realizado satisfactoriamente   
            if (status == HttpStatus.SC_OK) {
            	
               String respuesta= request.getResponseBodyAsString();
               Integer iInicioPw = respuesta.indexOf("name=\"password\" value=");
               iInicioPw = respuesta.indexOf("value=",iInicioPw)+6;
               Integer iFinPw = respuesta.indexOf(">",iInicioPw);
               // recupera el password para la posterior reautenticaci�n
               this.password= respuesta.substring(iInicioPw, iFinPw);
            } else {
            	retorno = false;
            }
            
        } catch (Exception ex){   
            System.err.println("Error\t: " + ex.getMessage());   
        } finally {   
            // libera la conexi�n  
            request.releaseConnection();   
        }   
        
        return retorno;
	}
    
    
    public boolean Reautenticacion () {
    	HttpClient      httpClient = null;   
        HttpMethodBase  request = null;   
        int             status = 0; 
        boolean retorno = true;
                
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        
        // petici�n mediante m�todo POST
        request = new PostMethod(Copiagenda.TargetURLAutthenticate);
        
        // no realiza autom�ticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        // a�ade los par�metros a la petici�n    
        ((PostMethod) request).addParameter("password", this.password); 
        ((PostMethod) request).addParameter("u", this.login);
        ((PostMethod) request).addParameter("d", "movistar.es");
        
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));   
               
            // a�ade las cabeceras a la petici�n   
            request.addRequestHeader("Content-type", "application/x-www-form-urlencoded");       
            request.addRequestHeader("Accept-Encoding", "gzip, deflate");
            request.addRequestHeader("Host", "copiagenda.movistar.es");
            request.addRequestHeader("Referer", this.urlRedireccion);
            //request.addRequestHeader("Referer", "https://copiagenda.movistar.es/cp/ps/Main/login/Verificacion?d=movistar.es"); 
            request.addRequestHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*"); 
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0; .NET CLR 2.0.50727)");
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("Cache-Control", "no-cache");
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("Cookie", this.sCookie);
            
            // lee el c�digo de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la petici�n se ha realizado satisfactoriamente   
            if (status == HttpStatus.SC_OK) {
            	// recupera la respuesta del servidor 
               String respuesta= request.getResponseBodyAsString();
               Integer iInicioT = respuesta.indexOf("&t=")+3;
               Integer iFinT = respuesta.indexOf("\"",iInicioT);
               // recupera el password para la posterior reautenticaci�n
               this.sReautenticacionT = respuesta.substring(iInicioT, iFinT);
               //devuelve estado 200=OK pero con errores
               if((iFinT - iInicioT) > 6) retorno = false;
            } else {
            	retorno = false;
            }
            
            
        } catch (Exception ex){   
            System.err.println("Error\t: " + ex.getMessage());   
               
            ex.printStackTrace();   
        } finally {   
            // libera la conexi�n   
            request.releaseConnection();   
        }   
        
        return retorno;
    }
    
    public boolean Datos () {
    	HttpClient httpClient = null;   
        HttpMethodBase request = null;   
        int status = 0;   
        BufferedReader reader = null; 
        String line = null;
        String respuesta = "";
        boolean retorno = true;
        
        // instancia del objeto httpClient  
        httpClient = new HttpClient();
        String sUrl = Copiagenda.TargetURLDatos + "&u=" + this.login + "&t=" + this.sReautenticacionT;
        
        // petici�n mediante m�todo POST
        request = new PostMethod(sUrl);
        
        // no realiza autom�ticamente el seguimiento de las redirecciones
        request.setFollowRedirects(false); 
        
        // a�ade los par�metros a la petici�n    
        ((PostMethod) request).addParameter("FileFormat", "TEXT"); 
        ((PostMethod) request).addParameter("charset", "8859_1");
        ((PostMethod) request).addParameter("delimiter", "TAB");
        
        try {   
               
            // realiza 3 reintentos en caso de errores.   
            request.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));   
               
            // a�ade las cabeceras a la petici�n   
            request.addRequestHeader("Content-type", "application/x-www-form-urlencoded");       
            request.addRequestHeader("Accept-Encoding", "gzip, deflate");
            request.addRequestHeader("Host", "copiagenda.movistar.es");
            request.addRequestHeader("Referer", this.urlRedireccion);
            //request.addRequestHeader("Referer", "https://copiagenda.movistar.es/cp/ps/Main/login/Verificacion?d=movistar.es"); 
            request.addRequestHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*"); 
            request.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; Media Center PC 4.0; .NET CLR 2.0.50727)");
            request.addRequestHeader("Accept-Language", "es");
            request.addRequestHeader("Cache-Control", "no-cache");
            request.addRequestHeader("Connection", "Keep-Alive");
            request.addRequestHeader("Cookie", this.sCookie);
            
            // lee el c�digo de la respuesta HTTP que devuelve el servidor   
            status = httpClient.executeMethod(request);   
               
            // si la petici�n se ha realizado satisfactoriamente   
            if (status == HttpStatus.SC_OK) {
            	// recupera la respuesta del servidor
            	reader = new BufferedReader(new InputStreamReader(request.getResponseBodyAsStream(), request.getResponseCharSet()));   
	            line   = reader.readLine(); 
	            
	            while (line != null) {   
		                respuesta += line+"\n";
		                line = reader.readLine();
	            } 
	            String sListaContactos [] = respuesta.split("\n\n");
                String lista [] =sListaContactos[0].split("\n");
                
            	List<String> Contactos = new ArrayList<String>(); 
                for (int i = 0; i < lista.length; i++) {
                	try {
                		Contactos.add(lista[i]);		                		
                	}
                	catch(Exception e){
                		System.out.println("Failed to read the contact list");
                	}
                }
                
               // devuelve un array con los contactos
               this.contactList = Contactos;
               //devuelve estado 200=OK pero con errores
               if (lista.length < 2) retorno = false;
            } else {
            	retorno = false;
            }
            
        } catch (Exception ex) {   
            System.err.println("Error\t: " + ex.getMessage());   
               
            ex.printStackTrace();   
        } finally {   
            // libera la conexi�n   
            request.releaseConnection();   
        }   
        
        return retorno;
    }
    
    public String SearchByName(String nombre,List<String> contactList) {
    	String contacto = null;
    	
    	if (contactList != null && contactList.size()> 1) {
    		
    		for (int i = 0; i < contactList.size(); i++) {
            	try {
            		
            		String sContacto [] = contactList.get(i).replaceAll("\"", "").split(",");
            		if(sContacto[3].equalsIgnoreCase(nombre)) {
            			contacto = contactList.get(i).toString();
            		}
            	}
            	catch(Exception e){
            		System.out.println("Failed to read the contact list");
            	}
            }
    	}
    	
    	return contacto;
    }
} 
