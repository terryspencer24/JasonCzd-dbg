package dbg.game.spades;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dbg.card.Card;
import dbg.card.Suit;
import dbg.card.Value;

public class GameSeatSpadesTest {

	GameSeatSpades seat1, seat2;

	@Before
	public void setup() {
		seat1 = new GameSeatSpades();
		seat2 = new GameSeatSpades();
	}

	@Test
	public void testPerfect() {
		seat1.bid(new SpadesBid(5, 5));
		Assert.assertEquals(50, seat1.getScore());
		Assert.assertEquals(0, seat1.getBags());
	}

	@Test
	public void testPerfectEmptyConstructor() {
		SpadesBid bid = new SpadesBid();
		bid.setBooks(5);
		bid.setWon(5);
		seat1.bid(bid);
		Assert.assertEquals(50, seat1.getScore());
		Assert.assertEquals(0, seat1.getBags());
	}

	@Test
	public void testSpecificRound() {
		seat1.bid(new SpadesBid(5, 5));
		Assert.assertEquals(0, seat1.getScore(0));
		Assert.assertEquals(0, seat1.getBags(0));
		Assert.assertEquals(50, seat1.getScore(1));
		Assert.assertEquals(0, seat1.getBags(1));
	}

	@Test
	public void testPerfectWithPartner() {
		seat1.bid(new SpadesBid(5, 5));
		seat2.bid(new SpadesBid(3, 3));
		seat1.setPartner(seat2);
		Assert.assertEquals(80, seat1.getScore());
		Assert.assertEquals(0, seat1.getBags());
		Assert.assertEquals(80, seat2.getScore());
		Assert.assertEquals(0, seat2.getBags());
	}

	@Test
	public void testPerfectWithPartnerMixed() {
		seat1.bid(new SpadesBid(5, 4));
		seat2.bid(new SpadesBid(3, 4));
		seat1.setPartner(seat2);
		Assert.assertEquals(80, seat1.getScore());
		Assert.assertEquals(0, seat1.getBags());
		Assert.assertEquals(80, seat2.getScore());
		Assert.assertEquals(0, seat2.getBags());
	}

	@Test
	public void testUnder() {
		seat1.bid(new SpadesBid(5, 10));
		Assert.assertEquals(55, seat1.getScore());
		Assert.assertEquals(5, seat1.getBags());
	}

	@Test
	public void testUnderWithPartner() {
		seat1.bid(new SpadesBid(5, 10));
		seat2.bid(new SpadesBid(3, 5));
		seat1.setPartner(seat2);
		Assert.assertEquals(87, seat1.getScore());
		Assert.assertEquals(7, seat1.getBags());
	}

	@Test
	public void testOver() {
		seat1.bid(new SpadesBid(5, 4));
		Assert.assertEquals(-50, seat1.getScore());
		Assert.assertEquals(0, seat1.getBags());
	}

	@Test
	public void testOverWithPartnerMixed() {
		seat1.bid(new SpadesBid(5, 6));
		seat2.bid(new SpadesBid(5, 3));
		seat1.setPartner(seat2);
		Assert.assertEquals(-100, seat1.getScore());
		Assert.assertEquals(0, seat1.getBags());
	}

	@Test
	public void testPerfectMultiple() {
		seat1.bid(new SpadesBid(5, 5));
		seat1.bid(new SpadesBid(3, 3));
		Assert.assertEquals(80, seat1.getScore());
		Assert.assertEquals(0, seat1.getBags());
	}

	@Test
	public void testPerfectMultipleWithPartner() {
		seat1.bid(new SpadesBid(5, 5));
		seat1.bid(new SpadesBid(3, 3));
		seat2.bid(new SpadesBid(5, 5));
		seat2.bid(new SpadesBid(3, 3));
		seat1.setPartner(seat2);
		Assert.assertEquals(160, seat1.getScore());
		Assert.assertEquals(0, seat1.getBags());
	}

	@Test
	public void testAlmostBagged() {
		seat1.bid(new SpadesBid(5, 7));
		seat1.bid(new SpadesBid(5, 7));
		seat1.bid(new SpadesBid(5, 7));
		seat1.bid(new SpadesBid(5, 7));
		Assert.assertEquals(208, seat1.getScore());
		Assert.assertEquals(8, seat1.getBags());
	}

	@Test
	public void testBagged() {
		seat1.bid(new SpadesBid(5, 7));
		seat1.bid(new SpadesBid(5, 7));
		seat1.bid(new SpadesBid(5, 7));
		seat1.bid(new SpadesBid(5, 7));
		seat1.bid(new SpadesBid(5, 7));
		Assert.assertEquals(160, seat1.getScore());
		Assert.assertEquals(0, seat1.getBags());
	}

	@Test
	public void testBaggedWithPartner() {
		seat1.bid(new SpadesBid(3, 8));
		seat1.bid(new SpadesBid(3, 3));
		seat2.bid(new SpadesBid(3, 3));
		seat2.bid(new SpadesBid(3, 8));
		seat1.setPartner(seat2);
		Assert.assertEquals(30, seat1.getScore());
		Assert.assertEquals(0, seat1.getBags());
	}

	@Test
	public void testBaggedPlus() {
		seat1.bid(new SpadesBid(5, 7));
		seat1.bid(new SpadesBid(5, 7));
		seat1.bid(new SpadesBid(5, 7));
		seat1.bid(new SpadesBid(5, 7));
		seat1.bid(new SpadesBid(5, 7));
		seat1.bid(new SpadesBid(5, 7));
		Assert.assertEquals(212, seat1.getScore());
		Assert.assertEquals(2, seat1.getBags());
	}

	@Test
	public void testBaggedPlusWithPartner() {
		seat1.bid(new SpadesBid(3, 8));
		seat1.bid(new SpadesBid(3, 4));
		seat2.bid(new SpadesBid(3, 4));
		seat2.bid(new SpadesBid(3, 8));
		seat1.setPartner(seat2);
		Assert.assertEquals(32, seat1.getScore());
		Assert.assertEquals(2, seat1.getBags());
	}
	
	@Test
	public void testSpadesPlayed() {
		seat1.cardsPlayed = new HashMap<>();
		seat1.cardsPlayed.put(0, new ArrayList<>());
		seat1.cardsPlayed.get(0).add(new Card(Suit.CLUBS, Value.ACE));
		Assert.assertEquals(false, seat1.spadesPlayed());
		seat1.cardsPlayed.get(0).add(new Card(Suit.SPADES, Value.ACE));
		Assert.assertEquals(true, seat1.spadesPlayed());
	}

}
