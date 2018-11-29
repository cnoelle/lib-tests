package de.cnoelle.websockets.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;

@Component(
		service= {},
		immediate=true,
		configurationPid=ServerActivator.PID,
		configurationPolicy=ConfigurationPolicy.REQUIRE
)
@Designate(ocd=ServerConfig.class)
@ServerConfig
public class ServerActivator {
	
	static final String PID = "de.cnoelle.websockets.Server"; 

	private WsSampleServer server;
	
	@Activate
	protected void activate(ServerConfig config) {
		this.server = new WsSampleServer(new InetSocketAddress(config.host(), config.port()), config.updateIntervalSeconds());
		server.start();;
	}
	
	@Deactivate
	protected void deactivate() throws IOException, InterruptedException {
		server.stop();
	}
	
}