package dbg.socket;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import dbg.security.TokenAuthenticationProvider;
import dbg.security.User;

@Configuration
@EnableWebSocketMessageBroker
public class SocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer
		implements WebSocketMessageBrokerConfigurer {

	@Value("${dbg.websocket.endpoint}") // uri: sock
	private String socketEndpoint;

	@Value("${dbg.websocket.broker}") // outbound: /out
	private String socketBroker;

	@Value("${dbg.websocket.appdest}") // inbound: /inb
	private String socketAppDest;

	@Autowired
	TokenAuthenticationProvider provider;

	@Autowired
	List<SocketGuard> guards;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker(socketBroker);
		config.setApplicationDestinationPrefixes(socketAppDest);
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint(socketEndpoint).setAllowedOrigins("*");
	}

	// === channel and security...

	// INBOUND: /inb/echo << simple inbound echo channel
	// OUTBOUND: /out/echo << simple outbound echo channel

	// INBOUND: /inb/game/{gameid} << game messages from player
	// ^^ must authenticate as a player and be in game

	// OUTBOUND: /out/game/{gameid}/player/{playerid} << game messages to a player
	// ^^ must authenticate as as player and be in game

	// OUTBOUND: /out/game/{gameid}/board << board messages
	// ^^ no security required

	// TODO write integration tests and do CI build for docker
	// TODO take down socket container??

	@Override
	protected void customizeClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
				String dest = (String) headerAccessor.getHeader("simpDestination");
				StompCommand cmd = headerAccessor.getCommand();
				if (guards != null) {
					for (SocketGuard guard : guards) {
						if (guard.isAuthRequired(cmd, dest)) {
							boolean authorized = false;
							User user = getUser(headerAccessor);
							if (user != null) {
								authorized = guard.isPermitted(dest, user);
							}
							if (!authorized) {
								throw new IllegalArgumentException("No permission for topic " + dest);
							}
						}
					}
				}
				return ChannelInterceptor.super.preSend(message, channel);
			}
		});
	}

	private User getUser(StompHeaderAccessor headerAccessor) {
		List<String> header = headerAccessor.getNativeHeader("Authorization");
		if (header != null && header.size() == 1) {
			User user = PlayerMessage.auth(header.get(0));
			return user;
		}
		return null;
	}

	@Override
	protected boolean sameOriginDisabled() {
		return true;
	}

}