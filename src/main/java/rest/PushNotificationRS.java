package rest;
/*
 *
 * Para a integração com o APNS utilizamos a biblioteca: https://github.com/notnoop/java-apns
 */



import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;

@Path("/push")
public class PushNotificationRS {
	
	public static final List<Register> registers = new ArrayList<Register>();
	
	
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
	
	@GET
	@Path("register/{token}/{name}/{device}/{so}")
	@Produces("application/json; charset=UTF-8")
	public Boolean register(@PathParam("token") String token, @PathParam("name") String name, @PathParam("device") String device, @PathParam("so") String so) {
		System.out.println("Registrando \n \tToken: "+token+"\n \tName: "+name);
		
		boolean newRegister = true;
		for (Register register : registers) {
			if(register.token.trim().equals(token.trim())){
				newRegister = false;
				register.setName(name);
				register.setDevice(device);
				register.setSo(so);
			}
		}
		if(newRegister){
			Register register = new Register();
			register.setToken(token);
			register.setName(name);
			register.setDevice(device);
			register.setSo(so);
		
			registers.add(register);
		}
		
		return new Boolean(true);
		
	}
	
	@GET
	@Path("register/devices")
	@Produces("application/json; charset=UTF-8")
	public List<Register> listDevices() {
		System.out.println("Listando os dispositivos");
				
		return registers;
		
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
	
	public class Register{
		private String token;
		private String name;
		private String device;
		private String so;
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDevice() {
			return device;
		}
		public void setDevice(String device) {
			this.device = device;
		}
		public String getSo() {
			return so;
		}
		public void setSo(String so) {
			this.so = so;
		}
		
	}
	
	
}