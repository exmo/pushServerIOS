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

@Path("/push")
public class PushNotificationRS {
	
	
	private static final String PASSWORD = "tecnologia123";
	private static final String CERTIFICADO = "ServerCertificadoEchavePrivada.p12";


	@GET
	@Path("/{token}/{msg}")
	@Produces("application/json; charset=UTF-8")
	public String[] push(@PathParam("token") String token, @PathParam("msg") String msg) {
		
		System.out.println("Token: "+token+"\n MSG: "+msg);
		
		ApnsService service = getService();
		
		String payload = APNS.newPayload().alertBody(msg).badge(10).sound("message").build();
		service.push(token, payload); 
		
		return new String[]{"Token: "+token, "MSG: "+msg};
		
	}		
	
	
	public ApnsService getService(){
		
		InputStream path = PushNotificationRS.class.getClassLoader().getResourceAsStream(CERTIFICADO);
		
		ApnsService service =
			    APNS.newService()
			    .withCert(path, PASSWORD)
			    .withSandboxDestination()
			    .build();
		return service;
	}
	
}