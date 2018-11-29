package de.cnoelle.websockets.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

class WsSampleServer extends WebSocketServer {
	
	private final ScheduledExecutorService exec;

	public WsSampleServer(final InetSocketAddress address, final long updateIntervalSeconds) {
		super(address);
		if (updateIntervalSeconds > 0) {
			this.exec = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "websockets-sampleclient"));
			final AtomicInteger cnt = new AtomicInteger(0);
			exec.scheduleWithFixedDelay(() -> broadcast("Hi client " + cnt.getAndIncrement()), updateIntervalSeconds, updateIntervalSeconds, TimeUnit.SECONDS);
		} else
			this.exec = null;
	}
	
	@Override
	public void stop() throws IOException, InterruptedException {
		if (exec != null) {
			try {
				exec.shutdownNow();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		super.stop();
	}
	
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("Server received close " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		System.out.println("Server received message from "	+ conn.getRemoteSocketAddress() + ": " + message);
	}

	@Override
	public void onMessage( WebSocket conn, ByteBuffer message ) {
		System.out.println("Server received ByteBuffer from "	+ conn.getRemoteSocketAddress() + ": " + StandardCharsets.UTF_8.decode(message));
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.err.println("Server: an error occured on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
	}
	
	@Override
	public void onStart() {
		System.out.println("Server started successfully");
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println("Client handshake from " + conn.getRemoteSocketAddress() + ": " + handshake );
	}
	
	
}