package dbg.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.stereotype.Component;

import dbg.security.User;
import dbg.ticket.GameTicket;
import dbg.ticket.GameTicketRepository;

@Component
public class SocketGuardInboundGame implements SocketGuard {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	GameTicketRepository repo;

	@Override
	public boolean isAuthRequired(StompCommand cmd, String dest) {
		boolean auth = StompCommand.SEND.equals(cmd) && dest != null && dest.contains("/inb/game/")
				&& dest.contains("/player/");
		return auth;
	}

	@Override
	public boolean isPermitted(String dest, User user) {
		try {
			String playerId = dest.substring(dest.lastIndexOf('/') + 1);
			if (user.id.equals(new Long(playerId))) {
				String gameId = dest.substring(dest.indexOf("/game/") + 6, dest.indexOf("/player/"));
				GameTicket ticket = repo.findById(Long.parseLong(gameId))
						.orElseThrow(() -> new RuntimeException("No game ticket found for provided id of " + gameId));
				return ticket.invites.contains(user);
			}
		} catch (Exception e) {
			logger.error(dest + " and " + user.id, e);
		}
		return false;
	}

}
