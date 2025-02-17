package tools.refinery.language.web.tests;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

public abstract class WebSocketIntegrationTestClient {
	private static long TIMEOUT_MILLIS = Duration.ofSeconds(1).toMillis();
	
	private boolean finished = false;

	private Object lock = new Object();

	private Throwable error;

	private int closeStatusCode;

	private List<String> responses = new ArrayList<>();
	
	public int getCloseStatusCode() {
		return closeStatusCode;
	}
	
	public List<String> getResponses() {
		return responses;
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		arrangeAndCatchErrors(session);
	}
	
	private void arrangeAndCatchErrors(Session session) {
		try {
			arrange(session, responses.size());
		} catch (Exception e) {
			finishedWithError(e);
		}
	}
	
	protected abstract void arrange(Session session, int responsesReceived) throws IOException; 

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		closeStatusCode = statusCode;
		testFinished();
	}

	@OnWebSocketError
	public void onError(Throwable error) {
		finishedWithError(error);
	}

	@OnWebSocketMessage
	public void onMessage(Session session, String message) {
		responses.add(message);
		arrangeAndCatchErrors(session);
	}

	private void finishedWithError(Throwable t) {
		error = t;
		testFinished();
	}

	private void testFinished() {
		synchronized (lock) {
			finished = true;
			lock.notify();
		}
	}

	public void waitForTestResult() {
		synchronized (lock) {
			if (!finished) {
				try {
					lock.wait(TIMEOUT_MILLIS);
				} catch (InterruptedException e) {
					fail("Unexpected InterruptedException", e);
				}
			}
		}
		if (!finished) {
			fail("Test still not finished after timeout");
		}
		if (error != null) {
			fail("Unexpected exception in websocket thread", error);
		}
	}
}
