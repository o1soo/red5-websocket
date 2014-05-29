package org.red5.net.websocket;

import org.red5.server.Server;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.plugin.Red5Plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocketPlugin
 * <pre>
 * This program will be called by red5 PluginLauncher
 * and hold the application Context or Application Adapter
 * </pre>
 * @author Toda Takahiko
 */
public class WebSocketPlugin extends Red5Plugin {

	private Logger log = LoggerFactory.getLogger(WebSocketPlugin.class);

	private WebSocketScopeManager manager = new WebSocketScopeManager();

	public WebSocketPlugin() {
		log.info("WebSocketPlugin ctor");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doStart() throws Exception {
		super.doStart();
		log.info("WebSocketPlugin start");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "WebSocketPlugin";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Server getServer() {
		return super.getServer();
	}

	public WebSocketScopeManager getManager() {
		return manager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplication(MultiThreadedApplicationAdapter application) {
		log.info("WebSocketPlugin application: {}", application);
		manager.addApplication(application.getName());
		super.setApplication(application);
	}
	
}
