package dbg;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.ConnectionLostException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import io.restassured.http.Headers;
import io.restassured.response.Response;
import net.jodah.concurrentunit.Waiter;

public class SocketTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private String url;

	private WebSocketStompClient stompClient;

	@Before
	public void setup() {
		this.url = "ws://" + TestUtil.setupBaseURL() + "/sock";
		WebSocketClient client = new StandardWebSocketClient();
		stompClient = new WebSocketStompClient(client);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
	}

	@Test
	public void testEcho() throws TimeoutException, InterruptedException {
		final Waiter waiter = new Waiter();
		stompClient.connect(url, new SocketTestStompSessionHandler(waiter) {

			@Override
			public void handleFrame(StompHeaders session, Object payload) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> msg = (HashMap<String, String>) payload;
				logger.info("Received message from server :: " + payload);
				waiter.assertEquals("echo", msg.get("type"));
				waiter.resume();
			}

			@Override
			public void afterConnected(StompSession session, StompHeaders arg1) {
				logger.info("Subscribing to echo topic");
				session.subscribe("/out/echo", this);
				HashMap<String, Object> pm = new HashMap<>();
				pm.put("message", "xyz");
				logger.info("Sending test message");
				session.send("/inb/echo", pm);
			}

		});
		waiter.await(5000);
	}

	@Test
	public void testUnauthConnect() throws TimeoutException, InterruptedException {
		final Waiter waiter = new Waiter();
		stompClient.connect(url, new SocketTestStompSessionHandler(waiter) {

			@Override
			public void handleFrame(StompHeaders session, Object payload) {
			}

			@Override
			public void afterConnected(StompSession session, StompHeaders arg1) {
				logger.info("Subscribing to secured topic");
				session.subscribe("/out/game/12345/player/1", this);
			}

			@Override
			public void handleTransportError(StompSession arg0, Throwable arg1) {
				waiter.assertTrue(arg1 instanceof ConnectionLostException);
				waiter.resume();
			}

		});
		waiter.await(5000);
	}

	@Test
	public void testAuthConnect() throws TimeoutException, InterruptedException {
		String auth = UserIntTest.tokenValue();
		final Waiter waiter = new Waiter();
		stompClient.connect(url, new SocketTestStompSessionHandler(waiter) {

			@Override
			public void handleFrame(StompHeaders session, Object payload) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> msg = (HashMap<String, Object>) payload;
				logger.info("Received message from server :: " + payload);
				waiter.assertEquals("echo", msg.get("type"));
				@SuppressWarnings("unchecked")
				HashMap<String, String> msg2 = (HashMap<String, String>) msg.get("message");
				waiter.assertEquals("auth echo works", msg2.get("message"));
				waiter.resume();
			}

			@Override
			public void afterConnected(StompSession session, StompHeaders arg1) {
				logger.info("Subscribing to secured topic");
				StompHeaders headers = new StompHeaders();
				headers.setDestination("/out/game/12345/player/1");
				headers.set("Authorization", auth);
				session.subscribe(headers, this);

				HashMap<String, Object> pm = new HashMap<>();
				pm.put("body", "/out/game/12345/player/1");
				pm.put("message", "auth echo works");
				session.send("/inb/echo", pm);
			}

		});
		waiter.await(5000);
	}

	@Test
	public void testUnauthInbound() throws TimeoutException, InterruptedException {
		final Waiter waiter = new Waiter();
		stompClient.connect(url, new SocketTestStompSessionHandler(waiter) {

			@Override
			public void handleFrame(StompHeaders session, Object payload) {
			}

			@Override
			public void afterConnected(StompSession session, StompHeaders arg1) {
				logger.info("Sending message to secure game topic");
				HashMap<String, Object> pm = new HashMap<>();
				pm.put("message", "type");
				pm.put("body", "whatever");
				session.send("/inb/game/12345", pm);
			}

			@Override
			public void handleTransportError(StompSession arg0, Throwable arg1) {
				waiter.assertTrue(arg1 instanceof ConnectionLostException);
				waiter.resume();
			}

		});
		waiter.await(5000);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testAuthInbound() throws TimeoutException, InterruptedException {
		Headers headers = UserIntTest.token();
		String auth = headers.get("Authorization").getValue();

		// create ticket
		Response response = TicketIntTest.createTicket(headers);
		// add invite and upate
		HashMap map = response.as(HashMap.class);
		List<HashMap> invites = new ArrayList();
		HashMap invite = new HashMap();
		invite.put("id", 1L);
		invites.add(invite);
		map.put("invites", invites);
		response = TicketIntTest.updateTicket(headers, map);
		// grab ticket id
		Long ticketid = response.jsonPath().getLong("id");

		final Waiter waiter = new Waiter();
		stompClient.connect(url, new SocketTestStompSessionHandler(waiter) {

			@Override
			public void handleFrame(StompHeaders session, Object payload) {
				HashMap<String, Object> msg = (HashMap<String, Object>) payload;
				logger.info("Received message from server :: " + payload);
				waiter.assertEquals("echo", msg.get("type"));
				HashMap<String, String> echoedMsg = (HashMap<String, String>) msg.get("message");
				waiter.assertEquals("whatever", echoedMsg.get("body"));
				waiter.resume();
			}

			@Override
			public void afterConnected(StompSession session, StompHeaders arg1) {
				session.subscribe("/out/echo", this);

				logger.info("Sending message to secure game topic");
				StompHeaders headers = new StompHeaders();
				headers.setDestination("/inb/game/" + ticketid);
				headers.set("Authorization", auth);
				logger.info("auth token is " + auth);

				HashMap<String, Object> pm = new HashMap<>();
				pm.put("message", "echo");
				pm.put("body", "whatever");
				session.send(headers, pm);
			}

		});
		waiter.await(5000);

		// now try to connect to game/player topic
		stompClient.connect(url, new SocketTestStompSessionHandler(waiter) {

			@Override
			public void handleFrame(StompHeaders session, Object payload) {
			}

			@Override
			public void afterConnected(StompSession session, StompHeaders arg1) {
				logger.info("Connecting to secure game+player topic with bad player");
				StompHeaders headers = new StompHeaders();
				headers.setDestination("/out/game/" + ticketid + "/player/2");
				headers.set("Authorization", auth);
				logger.info("auth token is " + auth);
				session.subscribe(headers, this);
			}

			@Override
			public void handleTransportError(StompSession arg0, Throwable arg1) {
				waiter.assertTrue(arg1 instanceof ConnectionLostException);
				waiter.resume();
			}

		});
		waiter.await(5000);

		// now try to connect to game/player topic
		
		ThreadPoolTaskScheduler tpts = new ThreadPoolTaskScheduler();
		tpts.afterPropertiesSet();
		stompClient.setTaskScheduler(tpts);
		stompClient.connect(url, new SocketTestStompSessionHandler(waiter) {

			@Override
			public void handleFrame(StompHeaders session, Object payload) {
			}

			@Override
			public void afterConnected(StompSession session, StompHeaders arg1) {
				logger.info("Connecting to secure game+player topic");
				StompHeaders headers = new StompHeaders();
				headers.setDestination("/out/game/" + ticketid + "/player/1");
				headers.set("Authorization", auth);
				headers.setReceiptId("ABCD");
				session.setAutoReceipt(true);
				session.subscribe(headers, this).addReceiptTask(() -> {
					System.out.println("resuming...");
					waiter.resume();
				});
			}

		});
		waiter.await(5000);
	}

}

abstract class SocketTestStompSessionHandler implements StompSessionHandler {

	private Waiter waiter;

	SocketTestStompSessionHandler(Waiter waiter) {
		this.waiter = waiter;
	}

	@Override
	public Type getPayloadType(StompHeaders arg0) {
		return HashMap.class;
	}

	@Override
	public void handleException(StompSession arg0, StompCommand arg1, StompHeaders arg2, byte[] arg3, Throwable arg4) {
		waiter.fail(arg4);
	}

	@Override
	public void handleTransportError(StompSession arg0, Throwable arg1) {
		waiter.fail(arg1);
	}

}