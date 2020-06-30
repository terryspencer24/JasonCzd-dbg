package dbg.game.spades;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dbg.card.Card;
import dbg.card.Suit;
import dbg.game.GameRuleViolation;

public class GameSpadesTest {

	GameSeatSpades gs1, gs2, gs3, gs4;

	Long u1, u2, u3, u4;

	GameSpades game;

	@Before
	public void setup() {
		game = new GameSpades();

		gs1 = new GameSeatSpades();
		u1 = 1L;
		gs1.userid = u1;
		gs1.isHost = true;
		gs1.isReady = true;
		game.addSeat(gs1);

		gs2 = new GameSeatSpades();
		u2 = 2L;
		gs2.userid = u2;
		gs2.isReady = true;
		game.addSeat(gs2);

		gs3 = new GameSeatSpades();
		u3 = 3L;
		gs3.userid = u3;
		gs3.isReady = true;
		game.addSeat(gs3);

		gs4 = new GameSeatSpades();
		u4 = 4L;
		gs4.userid = u4;
		gs4.isReady = true;
		game.addSeat(gs4);
	}
	
	@Test
	public void testPackAndUnpack() {
		game.start();
		Assert.assertSame(gs1, gs3.partner);
		Assert.assertSame(gs2, gs4.partner);
		game.pack();
		Assert.assertNull(gs3.partner);
		Assert.assertNull(gs4.partner);
		game.unpack();
		Assert.assertSame(gs1, gs3.partner);
		Assert.assertSame(gs2, gs4.partner);
	}

	@Test
	public void testStart() {
		Assert.assertFalse(game.isItMyTurn(u1));
		Assert.assertTrue(game.isReadyToStart());
		Assert.assertEquals(0, game.getRound());
		game.start();
		Assert.assertEquals(1, game.getTrick());
		Assert.assertEquals(1, game.getRound());
		Assert.assertEquals(13, gs1.hand.size());
		Assert.assertEquals(13, gs2.hand.size());
		Assert.assertEquals(13, gs3.hand.size());
		Assert.assertEquals(13, gs4.hand.size());
		Assert.assertNull(gs1.bid(game.getRound()));
		Assert.assertTrue(game.isItMyTurn(u1));
		Assert.assertFalse(game.isItMyTurn(u2));
		Assert.assertFalse(game.isItMyTurn(u3));
		Assert.assertFalse(game.isItMyTurn(u4));
		Assert.assertEquals(1, game.getRound());
		List<GameTurnSpades> turns = game.getTurns(u1);
		Assert.assertEquals(1, turns.size());
		Assert.assertTrue(turns.get(0) instanceof GameTurnSpadesBid);
		Assert.assertFalse(game.isRoundOver());
	}

	@Test
	public void testBid() {
		game.start();
		game.submitTurn(createBidTurn(u1, 4, 0));
		Assert.assertEquals(4, gs1.bid(1).getBooks());
		Assert.assertFalse(game.isItMyTurn(u1));
		Assert.assertEquals(0, game.getTurns(u1).size());
	}

	@Test(expected = GameRuleViolation.class)
	public void testBidNotMyTurn() {
		game.start();
		game.submitTurn(createBidTurn(u2, 4, 0));
	}

	@Test(expected = GameRuleViolation.class)
	public void testBidWrongType() {
		game.start();
		game.submitTurn(createPlayTurn(u1, null));
	}

	@Test
	public void testFullRoundOfBids() {
		game.start();
		game.submitTurn(createBidTurn(u1, 4, 0));
		game.submitTurn(createBidTurn(u2, 4, 0));
		game.submitTurn(createBidTurn(u3, 4, 0));
		game.submitTurn(createBidTurn(u4, 4, 0));
		Assert.assertEquals(4, gs1.bid(1).getBooks());
		Assert.assertTrue(game.isItMyTurn(u1));
		Assert.assertEquals(1, game.getTurns(u1).size());
		Assert.assertTrue(game.getTurns(u1).get(0) instanceof GameTurnSpadesPlay);
	}

	@Test
	public void testPlay() {
		game.start();
		game.submitTurn(createBidTurn(u1, 4, 0));
		game.submitTurn(createBidTurn(u2, 4, 0));
		game.submitTurn(createBidTurn(u3, 4, 0));
		game.submitTurn(createBidTurn(u4, 4, 0));
		Card card = gs1.hand.cardOnTop();
		card.suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u1, card));
		Assert.assertSame(card, gs1.cardPlayed(1, 1));
		Assert.assertEquals(12, gs1.hand.size());
		Assert.assertFalse(gs1.hand.has(card));
	}

	@Test
	public void testPlayNoSuitLed() {
		game.start();
		game.submitTurn(createBidTurn(u1, 4, 0));
		game.submitTurn(createBidTurn(u2, 4, 0));
		game.submitTurn(createBidTurn(u3, 4, 0));
		game.submitTurn(createBidTurn(u4, 4, 0));
		Card card = gs1.hand.cardOnTop();
		card.suit = Suit.CLUBS;
		Assert.assertNull(game.getSuitLed(gs2));
		game.submitTurn(createPlayTurn(u1, card));
		Assert.assertEquals(Suit.CLUBS, game.getSuitLed(gs2));
	}

	@Test(expected = GameRuleViolation.class)
	public void testPlayWrongType() {
		game.start();
		game.submitTurn(createBidTurn(u1, 4, 0));
		game.submitTurn(createBidTurn(u2, 4, 0));
		game.submitTurn(createBidTurn(u3, 4, 0));
		game.submitTurn(createBidTurn(u4, 4, 0));
		game.submitTurn(createBidTurn(u1, 4, 0));
	}

	@Test(expected = GameRuleViolation.class)
	public void testPlayDontHaveCard() {
		game.start();
		game.submitTurn(createBidTurn(u1, 4, 0));
		game.submitTurn(createBidTurn(u2, 4, 0));
		game.submitTurn(createBidTurn(u3, 4, 0));
		game.submitTurn(createBidTurn(u4, 4, 0));
		game.submitTurn(createPlayTurn(u1, gs2.hand.cardOnTop()));
	}

	@Test(expected = GameRuleViolation.class)
	public void testPlayNotFollowSuit() {
		game.start();
		game.submitTurn(createBidTurn(u1, 4, 0));
		game.submitTurn(createBidTurn(u2, 4, 0));
		game.submitTurn(createBidTurn(u3, 4, 0));
		game.submitTurn(createBidTurn(u4, 4, 0));
		Card card1 = gs1.hand.cardOnTop();
		card1.suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u1, card1));
		gs2.hand.cards().forEach(card -> card.suit = Suit.CLUBS);
		Card card2 = gs2.hand.cardOnTop();
		card2.suit = Suit.HEARTS;
		game.submitTurn(createPlayTurn(u2, card2));
	}

	@Test
	public void testPlayNotFollowSuitButDontHaveSuitAnymore() {
		game.start();
		game.submitTurn(createBidTurn(u1, 4, 0));
		game.submitTurn(createBidTurn(u2, 4, 0));
		game.submitTurn(createBidTurn(u3, 4, 0));
		game.submitTurn(createBidTurn(u4, 4, 0));
		Card card1 = gs1.hand.cardOnTop();
		card1.suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u1, card1));
		Card card2 = gs2.hand.cardOnTop();
		card2.suit = Suit.HEARTS;
		gs2.hand.cards().forEach(card -> card.suit = Suit.HEARTS);
		game.submitTurn(createPlayTurn(u2, card2));
		Assert.assertSame(card2, gs2.cardPlayed(1, 1));
	}

	@Test
	public void testPlaySpadesNotFollowSuitButDontHaveSuitAnymore() {
		game.start();
		game.submitTurn(createBidTurn(u1, 4, 0));
		game.submitTurn(createBidTurn(u2, 4, 0));
		game.submitTurn(createBidTurn(u3, 4, 0));
		game.submitTurn(createBidTurn(u4, 4, 0));
		Card card1 = gs1.hand.cardOnTop();
		card1.suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u1, card1));
		gs2.hand.cards().forEach(card -> card.suit = Suit.HEARTS);
		Card card2 = gs2.hand.cardOnTop();
		card2.suit = Suit.SPADES;
		game.submitTurn(createPlayTurn(u2, card2));
		Assert.assertSame(card2, gs2.cardPlayed(1, 1));
	}

	@Test(expected = GameRuleViolation.class)
	public void testPlayCannotLeadSpadesIfNotBroken() {
		game.start();
		game.submitTurn(createBidTurn(u1, 4, 0));
		game.submitTurn(createBidTurn(u2, 4, 0));
		game.submitTurn(createBidTurn(u3, 4, 0));
		game.submitTurn(createBidTurn(u4, 4, 0));
		Card card1 = gs1.hand.cardOnTop();
		card1.suit = Suit.SPADES;
		game.submitTurn(createPlayTurn(u1, card1));
	}

	@Test
	public void testPlayCanLeadSpadesIfNotBrokenAndOnlySpadesLeft() {
		game.start();
		game.submitTurn(createBidTurn(u1, 4, 0));
		game.submitTurn(createBidTurn(u2, 4, 0));
		game.submitTurn(createBidTurn(u3, 4, 0));
		game.submitTurn(createBidTurn(u4, 4, 0));
		Card card1 = gs1.hand.cardOnTop();
		gs1.hand.cards().forEach(card -> card.suit = Suit.SPADES);
		card1.suit = Suit.SPADES;
		Assert.assertEquals(13, gs1.hand.size());
		game.submitTurn(createPlayTurn(u1, card1));
		Assert.assertSame(card1, gs1.cardPlayed(1, 1));
		Assert.assertEquals(12, gs1.hand.size());
		Assert.assertFalse(gs1.hand.has(card1));
	}

	@Test
	public void testPlayFullRound() {
		game.ranker = new SpadesCardRanker() {
			@Override
			public GameSeatSpades winner(int round, int trick, int roundStart, GameSeatSpades... seats) {
				return seats[0];
			}
		};
		game.start();
		game.submitTurn(createBidTurn(u1, 4, 0));
		game.submitTurn(createBidTurn(u2, 4, 0));
		game.submitTurn(createBidTurn(u3, 4, 0));
		game.submitTurn(createBidTurn(u4, 4, 0));
		gs1.hand.cardOnTop().suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u1, gs1.hand.cardOnTop()));
		gs2.hand.cardOnTop().suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u2, gs2.hand.cardOnTop()));
		gs3.hand.cardOnTop().suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u3, gs3.hand.cardOnTop()));
		gs4.hand.cardOnTop().suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u4, gs4.hand.cardOnTop()));

		Assert.assertEquals(1L, gs1.bid(1).getWon());
		Assert.assertTrue(gs1.trickWinner);
	}

	@Test
	public void testAdvanceToNextTrick() {
		game.ranker = new SpadesCardRanker() {
			@Override
			public GameSeatSpades winner(int rond, int trick, int roundStart, GameSeatSpades... seats) {
				return seats[2];
			}
		};
		game.start();
		game.submitTurn(createBidTurn(u1, 4, 0));
		game.submitTurn(createBidTurn(u2, 4, 0));
		game.submitTurn(createBidTurn(u3, 4, 0));
		game.submitTurn(createBidTurn(u4, 4, 0));
		gs1.hand.cardOnTop().suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u1, gs1.hand.cardOnTop()));
		gs2.hand.cardOnTop().suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u2, gs2.hand.cardOnTop()));
		gs3.hand.cardOnTop().suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u3, gs3.hand.cardOnTop()));
		gs4.hand.cardOnTop().suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u4, gs4.hand.cardOnTop()));

		Assert.assertTrue(game.isItMyTurn(u1));
		Assert.assertFalse(gs1.isReady);
		Assert.assertEquals(1, game.getTurns(u1).size());
		Assert.assertTrue(game.getTurns(u1).get(0) instanceof GameTurnSpadesReady);
		game.submitTurn(createReadyTurn(u1));
		Assert.assertTrue(gs1.isReady);
		Assert.assertFalse(game.isItMyTurn(u1));

		Assert.assertTrue(game.isItMyTurn(u2));
		Assert.assertEquals(1, game.getTurns(u2).size());
		Assert.assertTrue(game.getTurns(u2).get(0) instanceof GameTurnSpadesReady);
		game.submitTurn(createReadyTurn(u2));
		Assert.assertTrue(gs2.isReady);
		Assert.assertFalse(game.isItMyTurn(u2));

		Assert.assertTrue(game.isItMyTurn(u3));
		Assert.assertEquals(1, game.getTurns(u3).size());
		Assert.assertTrue(game.getTurns(u3).get(0) instanceof GameTurnSpadesReady);
		game.submitTurn(createReadyTurn(u3));
		Assert.assertTrue(gs3.isReady);
		Assert.assertFalse(game.isItMyTurn(u3));

		Assert.assertTrue(game.isItMyTurn(u4));
		Assert.assertEquals(1, game.getTurns(u4).size());
		Assert.assertTrue(game.getTurns(u4).get(0) instanceof GameTurnSpadesReady);
		game.submitTurn(createReadyTurn(u4));
		Assert.assertTrue(gs4.isReady);
		Assert.assertFalse(game.isItMyTurn(u4));

		Assert.assertNull(gs1.cardPlayed(1, 2));
		Assert.assertNull(gs2.cardPlayed(1, 2));
		Assert.assertNull(gs3.cardPlayed(1, 2));
		Assert.assertNull(gs4.cardPlayed(1, 2));
		Assert.assertFalse(gs1.trickWinner);
		Assert.assertFalse(gs2.trickWinner);
		Assert.assertFalse(gs3.trickWinner);
		Assert.assertFalse(gs4.trickWinner);
		Assert.assertFalse(gs1.isTurn);
		Assert.assertFalse(gs2.isTurn);
		Assert.assertTrue(gs3.isTurn);
		Assert.assertFalse(gs4.isTurn);
		Assert.assertEquals(2, game.getTrick());
	}

	@Test(expected = GameRuleViolation.class)
	public void testAdvanceWrongType() {
		game.start();
		game.submitTurn(createBidTurn(u1, 4, 0));
		game.submitTurn(createBidTurn(u2, 4, 0));
		game.submitTurn(createBidTurn(u3, 4, 0));
		game.submitTurn(createBidTurn(u4, 4, 0));
		gs1.hand.cardOnTop().suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u1, gs1.hand.cardOnTop()));
		gs2.hand.cardOnTop().suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u2, gs2.hand.cardOnTop()));
		gs3.hand.cardOnTop().suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u3, gs3.hand.cardOnTop()));
		gs4.hand.cardOnTop().suit = Suit.CLUBS;
		game.submitTurn(createPlayTurn(u4, gs4.hand.cardOnTop()));
		game.submitTurn(createBidTurn(u1, 4, 0));
	}

	@Test
	public void testAdvanceToEndOfRound() {
		game.ranker = new SpadesCardRanker() {
			@Override
			public GameSeatSpades winner(int round, int trick, int roundStart, GameSeatSpades... seats) {
				return seats[0];
			}
		};
		game.start();

		game.submitTurn(createBidTurn(u1, 4, 0));
		game.submitTurn(createBidTurn(u2, 4, 0));
		game.submitTurn(createBidTurn(u3, 4, 0));
		game.submitTurn(createBidTurn(u4, 4, 0));
		for (int i = 0; i < 13; i++) {
			gs1.hand.cardOnTop().suit = Suit.CLUBS;
			game.submitTurn(createPlayTurn(u1, gs1.hand.cardOnTop()));
			gs2.hand.cardOnTop().suit = Suit.CLUBS;
			game.submitTurn(createPlayTurn(u2, gs2.hand.cardOnTop()));
			gs3.hand.cardOnTop().suit = Suit.CLUBS;
			game.submitTurn(createPlayTurn(u3, gs3.hand.cardOnTop()));
			gs4.hand.cardOnTop().suit = Suit.CLUBS;
			game.submitTurn(createPlayTurn(u4, gs4.hand.cardOnTop()));
			game.submitTurn(createReadyTurn(u1));
			game.submitTurn(createReadyTurn(u2));
			game.submitTurn(createReadyTurn(u3));
			game.submitTurn(createReadyTurn(u4));
		}
		Assert.assertFalse(game.isItMyTurn(u1));
		Assert.assertTrue(game.isItMyTurn(u2));
		Assert.assertFalse(game.isItMyTurn(u3));
		Assert.assertFalse(game.isItMyTurn(u4));
		Assert.assertEquals(13, gs1.hand.size());
		Assert.assertEquals(13, gs2.hand.size());
		Assert.assertEquals(13, gs3.hand.size());
		Assert.assertEquals(13, gs4.hand.size());
		Assert.assertEquals(13, gs1.bids.get(0).getWon());
		Assert.assertEquals(0, gs2.bids.get(0).getWon());
		Assert.assertEquals(0, gs3.bids.get(0).getWon());
		Assert.assertEquals(0, gs4.bids.get(0).getWon());
	}

	@Test
	public void testLoopAllTheWayAround() {
		game.start();

		List<GameSeatSpades> seats = new ArrayList<GameSeatSpades>();
		seats.add(gs1);
		seats.add(gs2);
		seats.add(gs3);
		seats.add(gs4);
		game.ranker = new SpadesCardRanker() {
			@Override
			public GameSeatSpades winner(int round, int trick, int roundStart, GameSeatSpades... s) {
				return seats.get(0);
			}
		};
		for (int j = 0; j < 4; j++) {
			seats.forEach(s -> game.submitTurn(createBidTurn(s.userid, 4, 0)));
			for (int i = 0; i < 13; i++) {
				seats.forEach(s -> {
					s.hand.cardOnTop().suit = Suit.CLUBS;
					game.submitTurn(createPlayTurn(s.userid, s.hand.cardOnTop()));
				});
				seats.forEach(s -> game.submitTurn(createReadyTurn(s.userid)));
			}
			seats.add(seats.remove(0));
		}
		Assert.assertTrue(game.isItMyTurn(u1));
		Assert.assertFalse(game.isItMyTurn(u2));
		Assert.assertFalse(game.isItMyTurn(u3));
		Assert.assertFalse(game.isItMyTurn(u4));
	}

	@Test
	public void testLoopUntilOver() {
		game.start();

		List<GameSeatSpades> seats = new ArrayList<GameSeatSpades>();
		seats.add(gs1);
		seats.add(gs2);
		seats.add(gs3);
		seats.add(gs4);
		game.ranker = new SpadesCardRanker() {
			@Override
			public GameSeatSpades winner(int round, int trick, int roundStart, GameSeatSpades... s) {
				return seats.get(0);
			}
		};
		for (int j = 0; j < 5; j++) {
			for (int x = 0; x < seats.size(); x++) {
				game.submitTurn(createBidTurn(seats.get(x).userid, x == 0 ? 13 : 0, 0));
			}
			for (int i = 0; i < 13; i++) {
				seats.forEach(s -> {
					s.hand.cardOnTop().suit = Suit.CLUBS;
					game.submitTurn(createPlayTurn(s.userid, s.hand.cardOnTop()));
				});
				seats.forEach(s -> game.submitTurn(createReadyTurn(s.userid)));
			}
			seats.add(seats.remove(0));
		}
		Assert.assertTrue(game.isGameOver());
	}

	@Test(expected = GameRuleViolation.class)
	public void testBidNilNope() {
		game.createBid(0);
	}

	@Test
	public void testBidNilYep() {
		game.setNilAllowed(true);
		game.createBid(0);
	}

	@Test(expected = GameRuleViolation.class)
	public void testBid14() {
		game.createBid(14);
	}

	@Test
	public void testBidOk() {
		SpadesBid bid = game.createBid(5);
		Assert.assertEquals(5, bid.getBooks());
		Assert.assertEquals(0, bid.getWon());
	}

	private GameTurnSpadesBid createBidTurn(Long u, Integer books, int won) {
		SpadesBid bid = new SpadesBid(books, won);
		GameTurnSpadesBid turn = new GameTurnSpadesBid();
		turn.userid = u;
		turn.bid = bid;
		return turn;
	}

	private GameTurnSpadesPlay createPlayTurn(Long u, Card card) {
		GameTurnSpadesPlay turn = new GameTurnSpadesPlay();
		turn.userid = u;
		turn.card = card;
		return turn;
	}

	private GameTurnSpadesReady createReadyTurn(Long u) {
		GameTurnSpadesReady turn = new GameTurnSpadesReady();
		turn.userid = u;
		return turn;
	}

}
