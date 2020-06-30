package dbg.card;

import org.junit.Assert;
import org.junit.Test;

public class DeckTest {

	@Test
	public void testCreateDeck() {
		Deck deck = new DeckService().createStandardDeck();
		Assert.assertEquals(52, deck.size());
		for (Suit suit : Suit.values()) {
			for (Value value : Value.values()) {
				Assert.assertTrue(deck.has(suit, value));
			}
		}
	}

	@Test
	public void testShuffle() {
		Deck deck = new DeckService().createStandardDeck();
		StringBuffer ret = new StringBuffer();
		for (Card card : deck.cards()) {
			ret.append(card.toString());
		}
		deck.shuffle();
		StringBuffer ret2 = new StringBuffer();
		for (Card card : deck.cards()) {
			ret2.append(card.toString());
		}
		Assert.assertNotEquals(ret.toString(), ret2.toString());
	}

	@Test
	public void testDraw() {
		Deck deck = new DeckService().createStandardDeck();
		Card card = deck.draw();
		Assert.assertEquals(51, deck.size());
		Assert.assertFalse(deck.has(card.suit, card.value));
		Assert.assertEquals(Suit.SPADES, card.suit);
		Assert.assertEquals(Value.ACE, card.value);
	}

	@Test
	public void testDrawNumber() {
		Deck deck = new DeckService().createStandardDeck();
		Deck hand1 = deck.draw(26);
		Assert.assertEquals(26, deck.size());
		Assert.assertEquals(26, hand1.size());
		Deck hand2 = deck.draw(26);
		Assert.assertEquals(0, deck.size());
		Assert.assertEquals(26, hand2.size());
	}

	@Test(expected = RuntimeException.class)
	public void testDrawNumberTooMuch() {
		Deck deck = new DeckService().createStandardDeck();
		deck.draw(53);
	}

}
