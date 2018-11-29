package de.cnoelle.websockets.client;

import java.net.URI;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;

@Component(
		service= {},
		immediate=true,
		configurationPid=ClientActivator.PID,
		configurationPolicy=ConfigurationPolicy.REQUIRE
)
@Designate(ocd=ClientConfig.class, factory=true)
@ClientConfig
public class ClientActivator {
	
	static final String PID = "de.cnoelle.websockets.Client"; 

	private WsSampleClient client;
	
	@Activate
	protected void activate(ClientConfig config) {
		this.client = new WsSampleClient(URI.create(config.server()), config.updateIntervalSeconds());
	}
	
	@Deactivate
	protected void deactivate() {
		client.shutdown();
	}
	
}