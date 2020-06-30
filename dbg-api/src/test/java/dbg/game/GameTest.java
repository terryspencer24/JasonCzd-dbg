package dbg.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GameTest {

	GameSeat gs1, gs2, gs3;

	Long user1, user2, user3;

	Game<GameSeat, GameTurn> game;

	@Before
	public void setup() {
		gs1 = new GameSeat() {
		};
		user1 = 1L;
		gs1.userid = user1;
		gs2 = new GameSeat() {
		};
		user2 = 2L;
		gs2.userid = user2;
		gs3 = new GameSeat() {
		};
		user3 = 3L;
		gs3.userid = user3;
		game = new Game<GameSeat, GameTurn>() {

			List<GameSeat> seats = new ArrayList<GameSeat>();
			
			@Override
			public void applySettings(Map<String, String> settings) {
			}
			
			@Override
			protected List<GameSeat> seats() {
				return seats;
			}
			
			@Override
			public GameImage getGameImage() {
				return null;
			}

			@Override
			public void addSeat(Long id) {
			}

			@Override
			public int getMinNumberOfPlayers() {
				return 2;
			}

			@Override
			public int getMaxNumberOfPlayers() {
				return 2;
			}

			@Override
			public void initialize() {
			}

			@Override
			public List<GameTurn> getTurns(Long userid) {
				return null;
			}

			@Override
			protected void executeTurn(GameTurn turn) {
			}

			@Override
			public boolean isRoundOver() {
				return false;
			}

			@Override
			protected void completeRound() {
			}

			@Override
			protected void validateTurn(GameTurn turn) {
			}

			@Override
			public GameMetaData getMetaData() {
				return null;
			}

		};
	}

	@Test
	public void testSeatAdd() {
		gs1.isHost = true;
		game.addSeat(gs1);
		Assert.assertEquals(1, game.getSeats().size());
	}

	@Test(expected = GameRuleViolation.class)
	public void testSeatAddDuplicatePlayer() {
		game.addSeat(gs1);
		game.addSeat(gs1);
	}

	@Test(expected = GameRuleViolation.class)
	public void testSeatAddDuplicateHosts() {
		gs1.isHost = true;
		game.addSeat(gs1);
		gs2.isHost = true;
		game.addSeat(gs2);
	}

	@Test
	public void testSeatAddMultiple() {
		game.addSeat(gs1);
		game.addSeat(gs2);
		Assert.assertEquals(2, game.getSeats().size());
		Assert.assertSame(gs1, game.getSeats().get(0));
		Assert.assertSame(gs2, game.getSeats().get(1));
	}

	@Test
	public void testGetHost() {
		gs1.isHost = true;
		game.addSeat(gs1);
		game.addSeat(gs2);
		Assert.assertSame(gs1.userid, game.getHost());
	}

	@Test
	public void testGetNoHost() {
		game.addSeat(gs1);
		game.addSeat(gs2);
		Assert.assertNull(game.getHost());
	}

	@Test
	public void testGetHostNull() {
		gs1.isHost = true;
		game.addSeat(gs1);
		Assert.assertEquals(1, game.getSeats().size());
	}

	@Test(expected = GameRuleViolation.class)
	public void testMaximumNumberOfPlayers() {
		game.addSeat(gs1);
		game.addSeat(gs2);
		game.addSeat(gs3);
	}

	@Test
	public void testNotReadyTooFewPlayers() {
		Assert.assertFalse(game.isReadyToStart());
	}

	@Test
	public void testNotReadyNoHost() {
		game.addSeat(gs1);
		game.addSeat(gs2);
		Assert.assertFalse(game.isReadyToStart());
	}

	@Test
	public void testNotReadyPlayersNotReady() {
		gs1.isHost = true;
		game.addSeat(gs1);
		game.addSeat(gs2);
		Assert.assertFalse(game.isReadyToStart());
	}

	@Test
	public void testReady() {
		gs1.isHost = true;
		gs1.isReady = true;
		game.addSeat(gs1);
		gs2.isReady = true;
		game.addSeat(gs2);
		Assert.assertTrue(game.isReadyToStart());
	}

	@Test
	public void testStart() {
		gs1.isHost = true;
		gs1.isReady = true;
		game.addSeat(gs1);
		gs2.isReady = true;
		game.addSeat(gs2);
		game.start();
		Assert.assertTrue(game.isStarted());
		Assert.assertEquals(1, game.getRound());
		game.start();
		Assert.assertTrue(game.isStarted());
		Assert.assertEquals(1, game.getRound());
	}

	@Test
	public void testOver() {
		Assert.assertFalse(game.isGameOver());
		gs1.isHost = true;
		gs1.isReady = true;
		game.addSeat(gs1);
		gs2.isReady = true;
		game.addSeat(gs2);
		game.start();
		Assert.assertFalse(game.isGameOver());
		gs1.isWinner = true;
		Assert.assertTrue(game.isGameOver());
	}

	@Test
	public void testTurn() {
		gs1.isHost = true;
		gs1.isReady = true;
		game.addSeat(gs1);
		gs2.isReady = true;
		game.addSeat(gs2);
		game.start();
		GameTurn turn = new GameTurn() {

		};
		turn.userid = user1;
		gs1.isTurn = true;
		game.submitTurn(turn);
		Assert.assertTrue(gs1.isTurn);
	}

	@Test(expected = GameRuleViolation.class)
	public void testTurnUserNotFound() {
		gs1.isHost = true;
		gs1.isReady = true;
		game.addSeat(gs1);
		gs2.isReady = true;
		game.addSeat(gs2);
		game.start();
		GameTurn turn = new GameTurn() {

		};
		turn.userid = user3;
		game.submitTurn(turn);
	}

	@Test(expected = GameRuleViolation.class)
	public void testTurnGameNotStarted() {
		gs1.isHost = true;
		gs1.isReady = true;
		game.addSeat(gs1);
		GameTurn turn = new GameTurn() {

		};
		turn.userid = user1;
		game.submitTurn(turn);
	}

	@Test(expected = GameRuleViolation.class)
	public void testTurnNotMyTurn() {
		gs1.isHost = true;
		gs1.isReady = true;
		game.addSeat(gs1);
		gs2.isReady = true;
		game.addSeat(gs2);
		game.start();
		GameTurn turn = new GameTurn() {

		};
		turn.userid = user1;
		game.submitTurn(turn);
	}

}
