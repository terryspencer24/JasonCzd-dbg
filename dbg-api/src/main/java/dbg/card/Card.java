package dbg.card;

public class Card {

	public Suit suit;

	public Value value;

	public Card() {

	}

	public Card(Suit suit, Value value) {
		this.suit = suit;
		this.value = value;
	}

	public String toString() {
		return value + " of " + suit;
	}

	public boolean matches(Card card) {
		return matches(card.suit, card.value);
	}

	public boolean matches(Suit suit, Value value) {
		return this.suit == suit && this.value == value;
	}

	public boolean matches(Suit suit) {
		return this.suit == suit;
	}

}
