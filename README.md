pushServerIOS
=============

Demoiselle push server for iOS

Este aplicativo apenas possui uma classe PushNotificationRS que disponibiliza um serviço via URL para submeter notificações push para o aplicativo [pushClientIOS](https://github.com/exmo/pushClientIOS)

Para criar um projeto push é preciso seguir os seguintes passos.

Antes de começar recomendo a leitura deste [post](http://www.raywenderlich.com/3443/apple-push-notification-services-tutorial-part-12), principalmente
o inicio que explica bem o funcionando do Push da Apple.

## Passos para criar um Servidor de Push Notification

### 1 - Criar uma aplicação java EE

Opatamos por criar uma aplicação java [Demoiselle](http://www.frameworkdemoisell.gov.br), mas você pode criar da forma como preferir.

Como criamos com o demoiselle utilizei no pom do projeto o parent demoiselle-servlet-parent, pois desta forma o projeto
já é compatível com JEE 6 e não traz dependências extras...

### 2 - Cria uma classe REST que vai enviar o Push para a Apple.

Criamos a classe PushNotificationRS, que terá o método push:

```java
    @GET
    @Path("/{token}/{msg}")
    @Produces("application/json; charset=UTF-8")
    public String[] push(@PathParam("token") String token, @PathParam("msg") String msg)
        ApnsService service = getService();
		    String payload = APNS.newPayload().alertBody(msg).badge(10).sound("message").build();
		    service.push(token, payload); 
		    return new String[]{"Token: "+token, "MSG: "+msg};
	  }	
```

Este método apenas recebe o token do dispositivo a a mensagem a ser enviada.

A comunicação com o servidor da apple(APNS) se dá através de sockets sobre SSL e as informações trafegam no formato JSON. 
Como não queremos reinventar a roda utilizamos a biblioteca [Java-APNS](https://github.com/notnoop/java-apns), que vai simplificar muito nossos trabalhos.

Omiti o método getService por enquanto, pois ele envolve a proxima etapa que é a obtenção do certificado e chave privada.

### 3 - Obtenção do Certificado e chave privada do servidor.

A conexão é feita sobre SSL, logo teremos que ter um certificado combinado com a apple.

Sigam [os passos fornecidos pela Apple para gerar o certificado](http://developer.apple.com/library/mac/#documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/ProvisioningDevelopment/ProvisioningDevelopment.html#//apple_ref/doc/uid/TP40008194-CH104-SW3)

Após seguir estes passos, você terá um arquivo com extensão .p12 que conterá o certifica e a chave privada. Adicione este arquivo no classpath do seu projeto.
Em nosso caso colocamos um arquivo chamado "ServerCertificadoEchavePrivada.p12"

Agora podemos ver o método getService que apenas abre este arquivo com a senha utilizada em sua criação:

```java
    public ApnsService getService(){
		InputStream path = PushNotificationRS.class.getClassLoader().getResourceAsStream(CERTIFICADO);
		ApnsService service =
			    APNS.newService()
			    .withCert(path, PASSWORD)
			    .withSandboxDestination()
			    .build();
		return service;
	}
```

### 4 - Envie as mensagens e teste em seu dispositivo!

Rode o servidor, rodamos no jboss 7. Em seguida abra o browser e execute uma url como esta:

"http://localhost:8080/push/rest/push/4f2330c0f36336f02aad1dca8e2d453b15bde23dcf49307a89ebfa5d3ee161c6/Minha mensagem push"

### 5 - Infraestrutura

O servidor da apple funciona nas portas 2195 e 2196, por isso o firewall deve liberar o acesso a estas portas.

O ip não é fixo eles tem um balanceamento, por isso, principalmente em produção, deve-se criar regras no firewall liberando o
acesso as urls por faixas de ip.

#### 5.1 -Para produção (gateway.push.apple.com)

Abra as portas 2195 e 2196 no firewal para:

* 17.149.35.0 / 24
* 17.172.238.0 / 24

#### 5.2 - Para desenvolvimento (gateway.sandbox.push.apple.com)

Abra as portas 2195 e 2196 no firewal para:

* 17.149.34.66
* 17.149.34.65
* 17.172.233.65
* 17.172.233.66


## Referências

* [Apple Developer](http://developer.apple.com/library/mac/#documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/Introduction/Introduction.html#//apple_ref/doc/uid/TP40008194-CH1-SW1)
* [raywenderlich : Apple Push Notification Services Tutorial](http://www.raywenderlich.com/3443/apple-push-notification-services-tutorial-part-12)
* [Apple Developer: Certificados! A parte mais difícil](http://developer.apple.com/library/mac/#documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/ProvisioningDevelopment/ProvisioningDevelopment.html#//apple_ref/doc/uid/TP40008194-CH104-SW3)
