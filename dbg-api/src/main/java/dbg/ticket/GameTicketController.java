package dbg.ticket;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dbg.DbgObject;
import dbg.catalog.GameCatalog;
import dbg.game.Game;
import dbg.game.GameSeat;
import dbg.security.User;
import dbg.security.UserController;
import dbg.security.UserRepository;

@RestController
@RequestMapping("/api/tickets")
public class GameTicketController extends DbgObject {

	@Autowired
	GameCatalog catalog;

	@Autowired
	GameTicketService svc;

	@Autowired
	GameTicketRepository repo;

	@Autowired
	UserRepository usr;

	@GetMapping
	public List<GameTicket> tickets(@RequestParam("sort") String sort) {
		List<GameTicket> tickets = repo.findDistinctByUserOrInvites(UserController.getCurrentUser(),
				UserController.getCurrentUser());
		if (sort != null && "newest".equals(sort)) {
			tickets.sort((t1, t2) -> {
				return t2.startdate.compareTo(t1.startdate);
			});
		}
		return tickets;
	}

	@PostMapping
	public GameTicket post(@RequestBody GameTicket newTicket) {
		try {
			String clz = catalog.getClz(newTicket.gameName);
			Game<?, ?> game = (Game<?, ?>) Class.forName(clz).newInstance();
			GameTicket ticket = new GameTicket();
			ticket.gameClass = clz;
			ticket.gameName = this.catalog.getName(clz);
			ticket.user = UserController.getCurrentUser();
			ticket.startdate = Calendar.getInstance();
			ticket = repo.save(ticket);
			return svc.saveGameToTicket(ticket, game);
		} catch (Exception e) {
			logger.error("Failure", e);
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	@PutMapping
	public GameTicket put(@RequestBody GameTicket ticket) {
		GameTicket saved = repo.findById(ticket.id).orElse(null);
		if (saved != null) {
			saved.invites = ticket.invites;
			if (ticket.started && !saved.started) {
				Game game = svc.loadGameFromTicket(saved);
				game.applySettings(ticket.settings);
				int botcnt = 0;
				for (User invite : saved.invites) {
					long id = invite.id;
					if (invite.isBot()) {
						botcnt--;
						id = botcnt;
					}
					game.addSeat(id);
				}
				GameSeat seat = (GameSeat) game.getSeats().get(0);
				seat.isHost = true;
				svc.saveGameToTicket(saved, game);
			}
			saved.started = ticket.started;

			saved = repo.save(saved);
		}
		return saved;
	}

	@DeleteMapping("/{id}")
	public boolean delete(@PathVariable Long id) {
		GameTicket saved = repo.findById(id).orElse(null);
		if (saved != null && saved.ownedBy(UserController.getCurrentUser())) {
			repo.delete(saved);
			return true;
		}
		return false;
	}

}
