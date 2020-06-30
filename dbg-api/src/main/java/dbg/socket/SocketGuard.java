package dbg.socket;

import org.springframework.messaging.simp.stomp.StompCommand;

import dbg.security.User;

interface SocketGuard {

	boolean isAuthRequired(StompCommand cmd, String dest);

	boolean isPermitted(String dest, User user);

}