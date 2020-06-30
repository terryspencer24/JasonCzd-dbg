package dbg.game.spades;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import dbg.card.Card;
import dbg.card.Deck;
import dbg.card.Suit;
import dbg.card.Value;

public class GameSpadesBotTest {

	// http://www.vogella.com/tutorials/Mockito/article.html
	@Mock
	GameSpades game;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	GameSpadesBot bot = new GameSpadesBot();

	GameSeatSpades seat;

	Long userid;

	List<GameTurnSpades> turns;

	@Before
	public void setup() {
		seat = new GameSeatSpades();
		userid = 1L;
		seat.userid = userid;
		bot = new GameSpadesBot();
		turns = new ArrayList<GameTurnSpades>();
		seat.hand = new Deck();
		Mockito.when(game.getTurns(seat.userid)).thenReturn(turns);
	}

	@Test
	public void testNoMoves() {
		GameTurnSpades myTurn = bot.calculateMove(game, seat);
		Assert.assertNull(myTurn);
	}

	@Test
	public void testReady() {
		GameTurnSpadesReady availableTurn = new GameTurnSpadesReady();
		turns.add(availableTurn);

		GameTurnSpades myTurn = bot.calculateMove(game, seat);
		Assert.assertTrue(myTurn instanceof GameTurnSpadesReady);
		Assert.assertSame(userid, myTurn.userid);
	}

	@Test
	public void testBidSimple() {
		GameTurnSpadesBid availableTurn = new GameTurnSpadesBid();
		turns.add(availableTurn);

		GameTurnSpades myTurn = bot.calculateMove(game, seat);
		Assert.assertTrue(myTurn instanceof GameTurnSpadesBid);
		Assert.assertSame(userid, myTurn.userid);
		Assert.assertEquals(3, ((GameTurnSpadesBid) myTurn).bid.getBooks());
	}

	@Test
	public void testPlayLeadNoSpades() {
		turns.add(new GameTurnSpadesPlay());
		seat.hand.add(new Card(Suit.SPADES, Value.ACE));
		seat.hand.add(new Card(Suit.CLUBS, Value.TEN));
		seat.hand.add(new Card(Suit.HEARTS, Value.TWO));
		Mockito.when(game.getSuitLed(seat)).thenReturn(null);

		GameTurnSpades myTurn = bot.calculateMove(game, seat);
		Assert.assertTrue(myTurn instanceof GameTurnSpadesPlay);
		Assert.assertSame(userid, myTurn.userid);
		Assert.assertTrue(((GameTurnSpadesPlay) myTurn).card.matches(Suit.CLUBS, Value.TEN));
	}

	@Test
	public void testPlayLeadOnlySpades() {
		turns.add(new GameTurnSpadesPlay());
		seat.hand.add(new Card(Suit.SPADES, Value.TWO));
		seat.hand.add(new Card(Suit.SPADES, Value.TEN));
		seat.hand.add(new Card(Suit.SPADES, Value.ACE));
		Mockito.when(game.getSuitLed(seat)).thenReturn(null);

		GameTurnSpades myTurn = bot.calculateMove(game, seat);
		Assert.assertTrue(myTurn instanceof GameTurnSpadesPlay);
		Assert.assertSame(userid, myTurn.userid);
		Assert.assertTrue(((GameTurnSpadesPlay) myTurn).card.matches(Suit.SPADES, Value.ACE));
	}

	@Test
	public void testPlayInSuit() {
		turns.add(new GameTurnSpadesPlay());
		seat.hand.add(new Card(Suit.CLUBS, Value.TEN));
		seat.hand.add(new Card(Suit.CLUBS, Value.ACE));
		seat.hand.add(new Card(Suit.CLUBS, Value.TWO));
		Mockito.when(game.getSuitLed(seat)).thenReturn(Suit.CLUBS);

		GameTurnSpades myTurn = bot.calculateMove(game, seat);
		Assert.assertTrue(myTurn instanceof GameTurnSpadesPlay);
		Assert.assertSame(userid, myTurn.userid);
		Assert.assertTrue(((GameTurnSpadesPlay) myTurn).card.matches(Suit.CLUBS, Value.ACE));
	}

	@Test
	public void testPlayOutOfSuit() {
		turns.add(new GameTurnSpadesPlay());
		seat.hand.add(new Card(Suit.SPADES, Value.TEN));
		seat.hand.add(new Card(Suit.CLUBS, Value.ACE));
		seat.hand.add(new Card(Suit.DIAMONDS, Value.TWO));
		Mockito.when(game.getSuitLed(seat)).thenReturn(Suit.HEARTS);

		GameTurnSpades myTurn = bot.calculateMove(game, seat);
		Assert.assertTrue(myTurn instanceof GameTurnSpadesPlay);
		Assert.assertSame(userid, myTurn.userid);
		Assert.assertTrue(((GameTurnSpadesPlay) myTurn).card.matches(Suit.CLUBS, Value.ACE));
	}

}
