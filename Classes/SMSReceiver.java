package open.movilforum.smsreceiverapi;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;

/**
 * @file SMSReceiver.java
 * @version 1.1
 * @author Ibys Technologies S.A.
 * @date   19-05-2008
 * @description Recepción de SMS  
 */

public class SMSReceiver {
    
	private String server;
    private String login;
    private String password;
    Properties props = System.getProperties();
    private Store store = null;
    private Folder activeFolder = null;

      
    /**
     * Conecta con el servidor y devuelve la lista de mensajes de la carpeta inbox
     * 
     * @param String Server Servidor pop3
     * @param String Login Nombre de usuario
     * @param String Password Contraseña
     * @return List Lista de mensajes
     * @throws Exception
     */
    public List<Message> GetSMSList(String Server, String Login, String Password)throws Exception {
          
    	this.server = Server;
    	this.login = Login;
        this.password = Password;
    	
          // Sesión
          Session session = Session.getInstance(props);
          store = session.getStore("pop3");
         
          // Conecta con el servidor
          store.connect(server, login, password);
         
          // Obtener mensajes de la carpeta INBOX
          activeFolder = store.getFolder("INBOX");
        
          // Modo de apertura
          activeFolder.open(Folder.READ_ONLY);
          
          // Lista los mensajes del servidor
          List<Message> mailList = getMails(activeFolder.getMessages());
          
          if (store != null) {
              activeFolder.close(true);
              store.close();
           }
          
         return mailList;
    }
    
    
    
    
    /**
     * Devuelve verdadero si es un mensaje válido
     * 
     * @param message
     * @return boolean 
     */
    public static boolean isValid(Message message){
  	  
  	  boolean retorno = false;
  	  String body = "";

  	  try {
  	        if( message.isMimeType("text/plain") )
  	        {
  	            if((message.getContent().toString().indexOf("Movil:")!=-1) && (message.getContent().toString().indexOf("Texto:")!=-1)){
  	            	retorno = true;
  	            }
  	        } else {
  	        	Multipart mp  = (Multipart)message.getContent();
  	        	InputStream is = mp.getBodyPart(0).getInputStream();
  	        
  	        	StringBuffer sb = new StringBuffer();
  	        	byte[] b = new byte[100000];
  	        	int noChars = is.read(b);
  	        	sb.append(new String(b, 0, noChars));
  	        	body = sb.toString();
  	          
  	          if((body.indexOf("Movil:")!=-1) && (body.indexOf("Texto:")!=-1)){
  	            	retorno = true;
  	          }
  	          
  	        }
  	    }
  	    catch( Exception e)
  	    {
  	        retorno =  false;
  	    }
  	  return retorno;
    }
    
    
    
    /**
     * Obtiene la lista de mensajes
     * @param messages
     * @return List lista de mensajes válidos
     */
    public static List<Message> getMails(Message[] messages) {
        List<Message> list = new ArrayList<Message>();
        for (int i = 0; i < messages.length; i++) {
        	try {
        		if (isValid(messages[i])){
            		list.add(messages[i]);
          	  	}
        	}
        	catch(Exception e){
        		System.out.println("Failed to read the message");
        	}
        }
        
        return list;
      }
    
    /**
     * Lee el cuerpo del mensaje
     * @param message
     * @return String Texto con el cuerpo del mensaje
     */
    public String readBody(Message message) {
    	   
    		String body = "";
    	    
    	    try {
    	        if( message.isMimeType("text/plain") )
    	        {
    	        	Integer inicio = message.getContent().toString().indexOf("Texto:") + 6;
        			Integer fin = inicio + 9;
        			body = message.getContent().toString().substring(inicio, fin);
    	            
    	        }
    	        else
    	        {
    	          Multipart mp  = (Multipart)message.getContent();
    	          InputStream is = mp.getBodyPart(0).getInputStream();
    	        
    	          StringBuffer sb = new StringBuffer();
    	          byte[] b = new byte[100000];
    	          int noChars = is.read(b);
    	          sb.append(new String(b, 0, noChars));
    	          body = sb.toString();
    	          
    	          Integer inicio = body.indexOf("Texto:") + 6;
    	          Integer fin = inicio + 9;    			
    	          body = body.substring(inicio, fin);
    	        }
    	    }
    	    catch( Exception e)
    	    {
    	        body = "Message not available";
    	    }
    	    
    	    return body;
    	  }
    
    
    /**
     * Lee el remitente real del mensaje
     * @param message
     * @return String Remitente del mensaje
     */
    public String readFromServer(Message message) {
   
    	String from = "";

    	try {
    		InternetAddress fromAddress = (InternetAddress) message.getFrom()[0];

    		from = fromAddress.getPersonal() + " (" + fromAddress.getAddress() + ")";
    	} catch (Exception e) {
    		from = "Sender unavailable";
    	}

    	return from;
    }
    
    
    /**
     * Lee el remitente dentro del cuerpo del mensaje
     * @param message
     * @return String Remitente del mensaje
     */
    public String readFrom(Message message) {
   
    	String from = "";

    	try {
    		if( message.isMimeType("text/plain") ) {

    			Integer inicio = message.getContent().toString().indexOf("Movil:") + 6;
    			Integer fin = inicio + 9;
    			from = message.getContent().toString().substring(inicio, fin);
    			
    		} else {
    			Multipart mp  = (Multipart)message.getContent();
    			InputStream is = mp.getBodyPart(0).getInputStream();
    
    			StringBuffer sb = new StringBuffer();
    			byte[] b = new byte[100000];
    			int noChars = is.read(b);
    			sb.append(new String(b, 0, noChars));
    			from = sb.toString();
    			
    			Integer inicio = from.indexOf("Movil:") + 6;
    			Integer fin = inicio + 9;    			
    			from = from.substring(inicio, fin);
    		}
    	} catch( Exception e) {
    		from = "Message not available";
    	}

    	return from;
    }
    
    
    
    /**
     * Lee el asunto del mensaje
     * @param message
     * @return String Asunto del mensaje
     */
    public String readSubject(Message message) {
   
    	String subject = "";

    	try {
    		subject = message.getSubject();
    	} catch (Exception e) {
    		subject = "Subject not available";
    	}
    
    	return subject;
	}
    
    
}
