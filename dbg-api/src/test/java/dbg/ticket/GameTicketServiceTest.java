package dbg.ticket;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.google.gson.Gson;

import dbg.game.Game;
import dbg.game.spades.GameSpades;

public class GameTicketServiceTest {

	@Mock
	GameTicketRepository repo;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Test
	public void testLoadGameWithoutSeats() {
		GameTicket ticket = new GameTicket();
		ticket.id = 1L;
		ticket.gameBlob = new Gson().toJson(new GameSpades());
		ticket.gameClass = GameSpades.class.getName();

		GameTicketService svc = new GameTicketService();
		svc.repo = repo;
		Mockito.when(repo.findById(1L)).thenReturn(Optional.of(ticket));

		Game<?, ?> g = svc.loadGameFromTicket(1L);
		Assert.assertEquals(0, g.getRound());
	}

	@Test
	public void testSaveGame() {
		GameSpades game = new GameSpades();
		GameTicket ticket = new GameTicket();
		ticket.id = 1L;

		GameTicketService svc = new GameTicketService();
		svc.repo = repo;

		svc.saveGameToTicket(ticket, game);
		Assert.assertEquals(GameSpades.class.getName(), ticket.gameClass);
		Assert.assertEquals(new Gson().toJson(game), ticket.gameBlob);
	}

}
