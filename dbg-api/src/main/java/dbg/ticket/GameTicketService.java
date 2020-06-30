package dbg.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import dbg.DbgObject;
import dbg.game.Game;

@Component
public class GameTicketService extends DbgObject {

	private final static Gson GSON = new Gson();

	@Autowired
	GameTicketRepository repo;

	public GameTicket loadFromId(Long ticketid) {
		synchronized (ticketid) {
			try {
				return repo.findById(ticketid).orElseThrow(() -> new RuntimeException());
			} catch (Exception e) {
				logger.error("Error loading ticket " + ticketid, e);
			}
			return null;
		}
	}

	public Game<?, ?> loadGameFromTicket(Long ticketid) {
		synchronized (ticketid) {
			try {
				GameTicket ticket = repo.findById(ticketid).orElseThrow(() -> new RuntimeException());
				return loadGameFromTicket(ticket);
			} catch (Exception e) {
				logger.error("Error loading game from ticket " + ticketid, e);
			}
			return null;
		}
	}

	public Game<?, ?> loadGameFromTicket(GameTicket ticket) {
		synchronized (ticket.id) {
			try {
				Game<?, ?> game = (Game<?, ?>) GSON.fromJson(ticket.gameBlob, Class.forName(ticket.gameClass));
				game.unpack();
				return game;
			} catch (Exception e) {
				logger.error("Error loading game from ticket " + ticket.id, e);
				return null;
			}
		}
	}

	public GameTicket saveGameToTicket(GameTicket ticket, Game<?, ?> game) {
		synchronized (ticket.id) {
			game.pack();
			ticket.gameClass = game.getClass().getName();
			ticket.gameBlob = GSON.toJson(game);
			return repo.save(ticket);
		}
	}

}
