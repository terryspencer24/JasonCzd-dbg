package dbg.socket;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.stereotype.Component;

import dbg.security.User;

@Component
public class SocketGuardOutboundGamePlayer implements SocketGuard {

	@Override
	public boolean isAuthRequired(StompCommand cmd, String dest) {
		boolean auth = StompCommand.SUBSCRIBE.equals(cmd) && dest != null && dest.contains("/player/");
		return auth;
	}

	@Override
	public boolean isPermitted(String dest, User user) {
		boolean permitted = false;
		String id = dest.substring(dest.lastIndexOf('/') + 1);
		if (user.id.equals(new Long(id))) {
			permitted = true;
		}
		return permitted;
	}

}
