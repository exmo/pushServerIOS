package rest;
/*
 *
 * Para a integração com o APNS utilizamos a biblioteca: https://github.com/notnoop/java-apns
 */



import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

@Path("/push/cordova")
public class PushNotificationCordovaRS {
	
	
	private static final String PASSWORD = "tecnologia123";
	private static final String CERTIFICADO = "PushCordova.p12";


	@GET
	@Path("/{token}/{msg}")
	@Produces("application/json; charset=UTF-8")
	public String[] push(@PathParam("token") String token, @PathParam("msg") String msg) {
		
		System.out.println("Preparando mensagem \n \tToken: "+token+"\n \tMSG: "+msg);
		
		ApnsService service = getService();
		
		String payload = APNS.newPayload().alertBody(msg).sound("message").build();
		
		System.out.println("Payload: "+payload);
		service.push(token, payload); 
		
		return new String[]{"Token: "+token, "MSG: "+msg};
		
	}		
	
	
	public ApnsService getService(){
		
		InputStream path = PushNotificationCordovaRS.class.getClassLoader().getResourceAsStream(CERTIFICADO);
		
		ApnsService service =
			    APNS.newService()
			    .withCert(path, PASSWORD)
			    .withSandboxDestination()
			    .build();
		return service;
	}
	
}