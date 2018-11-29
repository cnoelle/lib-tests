package de.cnoelle.websockets.client;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * A client that periodically reconnects in case the server is not available or closes 
 * the connection.
 */
class WsSampleClient extends WebSocketClient {
	
	private final long updateIntervalSeconds;
	private final ScheduledExecutorService exec;
	private final AtomicInteger cnt = new AtomicInteger(0);
	private volatile CompletableFuture<ServerHandshake> connection; 
	private volatile ScheduledFuture<?> sayHi;
	private final AtomicInteger failureCnt = new AtomicInteger(0);

	public WsSampleClient(URI serverUri, long updateIntervalSeconds) {
		super(serverUri);
		this.updateIntervalSeconds = updateIntervalSeconds;
		this.exec = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "websockets-sampleclient"));
		if (updateIntervalSeconds > 0) {
			exec.scheduleWithFixedDelay(() -> send("Hi server " + cnt.getAndIncrement()), updateIntervalSeconds, updateIntervalSeconds, TimeUnit.SECONDS);
		}
		reconnectInternal(false);
	}
	
	private void reconnectInternal(boolean needsReset) {
		if (this.exec.isShutdown())
			return;
		connection = new CompletableFuture<ServerHandshake>();
		connection.thenRun(this::sayHi);
		this.exec.schedule(needsReset ? this::reconnect : this::connect, Math.min(1 + failureCnt.getAndIncrement() * 5, 300), TimeUnit.SECONDS);
	}
	
	private void sayHi() {
		this.sayHi = exec.scheduleWithFixedDelay(() -> send("Hi server " + cnt.getAndIncrement()), updateIntervalSeconds, updateIntervalSeconds, TimeUnit.SECONDS);
	}
	
	public void shutdown() {
		try {
			exec.shutdownNow();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		final CompletableFuture<?> connection = this.connection;
		if (connection != null)
			connection.cancel(true);
		this.connection = null;
		super.close();
	}
	
	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("Server handshake " + handshakedata.getHttpStatus() + ": " + handshakedata.getHttpStatusMessage());
		connection.complete(handshakedata);
		failureCnt.set(0);
	}

	@Override
	public void onMessage(String message) {
		System.out.println("Client received message " + message);
	}
	
	@Override
	public void onMessage(ByteBuffer message) {
		System.out.println("Client received ByteBuffer " + StandardCharsets.UTF_8.decode(message));
	}
	
	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("Client received close message " + code + ": " + reason + ", remote: " + remote);
		final CompletableFuture<?> connection = this.connection;
		if (connection != null)
			connection.completeExceptionally(new RuntimeException("Connection closed: " + code + ": " + reason));
		final ScheduledFuture<?> sayHi = this.sayHi;
		if (sayHi != null)
			sayHi.cancel(true);
		this.connection = null;
		this.sayHi = null;
		reconnectInternal(true);
	}

	@Override
	public void onError(Exception ex) {
		System.out.println("Client received error " + ex);
	}
	
	
}