package dbg.game.spades;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dbg.card.Card;
import dbg.card.Suit;
import dbg.card.Value;

public class SpadesCardRankerTest {

	SpadesCardRanker r;
	GameSeatSpades s1, s2, s3, s4;

	@Before
	public void setup() {
		r = new SpadesCardRanker();
		s1 = new GameSeatSpades();
		s1.userid = 1L;
		s2 = new GameSeatSpades();
		s2.userid = 2L;
		s3 = new GameSeatSpades();
		s3.userid = 3L;
		s4 = new GameSeatSpades();
		s4.userid = 4L;
	}

	@Test
	public void testSameSuit() {
		s1.cardPlayed(1, new Card(Suit.DIAMONDS, Value.ACE));
		s2.cardPlayed(1, new Card(Suit.DIAMONDS, Value.KING));
		s3.cardPlayed(1, new Card(Suit.DIAMONDS, Value.QUEEN));
		s4.cardPlayed(1, new Card(Suit.DIAMONDS, Value.JACK));
		Assert.assertSame(s1, r.winner(1, 1, 0, s1, s2, s3, s4));
		Assert.assertSame(s1, r.led(1, 1, 1, s1, s2, s3, s4));

		s1.cardPlayed(1, new Card(Suit.DIAMONDS, Value.KING));
		s2.cardPlayed(1, new Card(Suit.DIAMONDS, Value.ACE));
		s3.cardPlayed(1, new Card(Suit.DIAMONDS, Value.QUEEN));
		s4.cardPlayed(1, new Card(Suit.DIAMONDS, Value.JACK));
		Assert.assertSame(s2, r.winner(1, 2, 0, s1, s2, s3, s4));
		Assert.assertSame(s1, r.led(1, 2, 1, s1, s2, s3, s4));

		s1.cardPlayed(1, new Card(Suit.DIAMONDS, Value.QUEEN));
		s2.cardPlayed(1, new Card(Suit.DIAMONDS, Value.KING));
		s3.cardPlayed(1, new Card(Suit.DIAMONDS, Value.ACE));
		s4.cardPlayed(1, new Card(Suit.DIAMONDS, Value.JACK));
		Assert.assertSame(s3, r.winner(1, 3, 0, s1, s2, s3, s4));
		Assert.assertSame(s2, r.led(1, 3, 1, s1, s2, s3, s4));

		s1.cardPlayed(1, new Card(Suit.DIAMONDS, Value.JACK));
		s2.cardPlayed(1, new Card(Suit.DIAMONDS, Value.QUEEN));
		s3.cardPlayed(1, new Card(Suit.DIAMONDS, Value.KING));
		s4.cardPlayed(1, new Card(Suit.DIAMONDS, Value.ACE));
		Assert.assertSame(s4, r.winner(1, 4, 0, s1, s2, s3, s4));
		Assert.assertSame(s3, r.led(1, 4, 1, s1, s2, s3, s4));
	}

	@Test
	public void testHigherOffSuit() {
		s1.cardPlayed(1, new Card(Suit.DIAMONDS, Value.KING));
		s2.cardPlayed(1, new Card(Suit.HEARTS, Value.ACE));
		s3.cardPlayed(1, new Card(Suit.DIAMONDS, Value.QUEEN));
		s4.cardPlayed(1, new Card(Suit.DIAMONDS, Value.JACK));
		Assert.assertSame(s1, r.winner(1, 1, 0, s1, s2, s3, s4));
	}

	@Test
	public void testHigherOffSuitMultipleRounds() {
		s1.cardPlayed(1, new Card(Suit.DIAMONDS, Value.KING));
		s2.cardPlayed(1, new Card(Suit.DIAMONDS, Value.ACE));
		s3.cardPlayed(1, new Card(Suit.DIAMONDS, Value.QUEEN));
		s4.cardPlayed(1, new Card(Suit.DIAMONDS, Value.JACK));
		s1.cardPlayed(1, new Card(Suit.HEARTS, Value.KING));
		s2.cardPlayed(1, new Card(Suit.DIAMONDS, Value.TEN));
		s3.cardPlayed(1, new Card(Suit.DIAMONDS, Value.NINE));
		s4.cardPlayed(1, new Card(Suit.DIAMONDS, Value.EIGHT));
		Assert.assertSame(s2, r.winner(1, 2, 0, s1, s2, s3, s4));
		Assert.assertSame(s2, r.led(1, 2, 1, s1, s2, s3, s4));
		Assert.assertSame(s1, r.led(1, 1, 1, s1, s2, s3, s4));
	}

	@Test
	public void testHigherOffSuitMultipleRoundsTwo() {
		s1.cardPlayed(1, new Card(Suit.DIAMONDS, Value.KING));
		s2.cardPlayed(1, new Card(Suit.DIAMONDS, Value.ACE));
		s3.cardPlayed(1, new Card(Suit.DIAMONDS, Value.QUEEN));
		s4.cardPlayed(1, new Card(Suit.DIAMONDS, Value.JACK));
		Assert.assertSame(s1, r.led(1, 1, 1, s1, s2, s3, s4));
		Assert.assertSame(s2, r.winner(1, 1, 1, s1, s2, s3, s4));
		Assert.assertSame(s2, r.led(1, 2, 1, s1, s2, s3, s4));
		
		s2.cardPlayed(1, new Card(Suit.CLUBS, Value.TWO));
		s3.cardPlayed(1, new Card(Suit.CLUBS, Value.THREE));
		s4.cardPlayed(1, new Card(Suit.DIAMONDS, Value.SEVEN));
		s1.cardPlayed(1, new Card(Suit.CLUBS, Value.SEVEN));
		Assert.assertSame(s1, r.winner(1, 2, 1, s1, s2, s3, s4));
	}

	@Test
	public void testCutWithSpades() {
		s1.cardPlayed(1, new Card(Suit.DIAMONDS, Value.ACE));
		s2.cardPlayed(1, new Card(Suit.DIAMONDS, Value.KING));
		s3.cardPlayed(1, new Card(Suit.DIAMONDS, Value.QUEEN));
		s4.cardPlayed(1, new Card(Suit.SPADES, Value.TWO));
		Assert.assertSame(s4, r.winner(1, 1, 0, s1, s2, s3, s4));
	}

	@Test
	public void testLeadWithSpades() {
		s1.cardPlayed(1, new Card(Suit.SPADES, Value.TEN));
		s2.cardPlayed(1, new Card(Suit.SPADES, Value.KING));
		s3.cardPlayed(1, new Card(Suit.SPADES, Value.ACE));
		s4.cardPlayed(1, new Card(Suit.SPADES, Value.TWO));
		Assert.assertSame(s3, r.winner(1, 1, 0, s1, s2, s3, s4));
	}

	@Test
	public void testHigherCardButNotSpades() {
		s1.cardPlayed(1, new Card(Suit.SPADES, Value.TEN));
		s2.cardPlayed(1, new Card(Suit.DIAMONDS, Value.ACE));
		s3.cardPlayed(1, new Card(Suit.SPADES, Value.NINE));
		s4.cardPlayed(1, new Card(Suit.SPADES, Value.TWO));
		Assert.assertSame(s1, r.winner(1, 1, 0, s1, s2, s3, s4));
	}

}
