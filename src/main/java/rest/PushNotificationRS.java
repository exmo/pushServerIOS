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
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;

@Path("/push")
public class PushNotificationRS {
	
	
	private static final String PASSWORD = "tecnologia123";
	private static final String CERTIFICADO = "ServerCertificadoEchavePrivada.p12";
	
	private static final String PASSWORD_CORDOVA = "tecnologia123";
	private static final String CERTIFICADO_CORDOVA = "PushCordova.p12";


	@GET
	@Path("/{token}/{msg}")
	@Produces("application/json; charset=UTF-8")
	public ApnsNotification[] push(@PathParam("token") String token, @PathParam("msg") String msg) {
		
		System.out.println("Preparando mensagem \n \tToken: "+token+"\n \tMSG: "+msg);
		
		ApnsService service = getService();
		ApnsService serviceCordova = getServiceCordova();
		
		String payload = APNS.newPayload().alertBody(msg).badge(10).sound("message").build();
		
		System.out.println("Payload: "+payload);
		ApnsNotification n1 = service.push(token, payload); 
		ApnsNotification n2 =serviceCordova.push(token, payload); 
		
		
		
		return new ApnsNotification[]{n1, n2};
		
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
	
	public ApnsService getServiceCordova(){
		
		InputStream path = PushNotificationRS.class.getClassLoader().getResourceAsStream(CERTIFICADO_CORDOVA);
		
		ApnsService service =
			    APNS.newService()
			    .withCert(path, PASSWORD_CORDOVA)
			    .withSandboxDestination()
			    .build();
		return service;
	}
	
}