package dbg.card;

import org.junit.Assert;
import org.junit.Test;

public class CardTest {

	@Test
	public void testEquals() {
		Card card1 = new Card(Suit.CLUBS, Value.ACE);
		Card card2 = new Card(Suit.CLUBS, Value.ACE);
		Assert.assertTrue(card1.matches(card2));
		Assert.assertFalse(card1.equals(card2));
		Assert.assertNotEquals(card1.hashCode(), card2.hashCode());
		Assert.assertEquals(card1.hashCode(), card1.hashCode());
	}
	
	@Test
	public void testAddToBottom() {
		Card card1 = new Card(Suit.CLUBS, Value.ACE);
		Deck deck1 = new Deck();
		deck1.add(card1);
		Card card2 = new Card(Suit.CLUBS, Value.KING);
		Card card3 = new Card(Suit.CLUBS, Value.QUEEN);
		Deck deck2 = new Deck();
		deck2.add(card2);
		deck2.add(card3);
		deck1.addToBottom(deck2);
		Assert.assertSame(card3, deck1.cardAt(0));
		Assert.assertSame(card2, deck1.cardAt(1));
		Assert.assertSame(card1, deck1.cardAt(2));
	}
	
	@Test
	public void testDrawAll() {
		Card card1 = new Card(Suit.CLUBS, Value.ACE);
		Card card2 = new Card(Suit.CLUBS, Value.KING);
		Card card3 = new Card(Suit.CLUBS, Value.QUEEN);
		Deck deck1 = new Deck();
		deck1.add(card1);
		deck1.add(card2);
		deck1.add(card3);
		Deck deck2 = deck1.drawAll();
		Assert.assertEquals(0, deck1.size());
		Assert.assertSame(card1, deck2.cardAt(2));
		Assert.assertSame(card2, deck2.cardAt(1));
		Assert.assertSame(card3, deck2.cardAt(0));
	}

	@Test
	public void testNotEquals() {
		Card card1 = new Card(Suit.CLUBS, Value.ACE);
		Card card2 = new Card(Suit.DIAMONDS, Value.ACE);
		Assert.assertFalse(card1.equals(card2));
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testNotEqualsNotSame() {
		Card card1 = new Card(Suit.CLUBS, Value.ACE);
		Assert.assertFalse(card1.equals("test"));
	}

}
