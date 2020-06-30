package dbg.card;

import org.springframework.stereotype.Component;

@Component
public class DeckService {

	public Deck createStandardDeck() {
		Deck deck = new Deck();
		for (Suit suit : Suit.values()) {
			for (Value value : Value.values()) {
				Card card = new Card();
				card.suit = suit;
				card.value = value;
				deck.add(card);
			}
		}
		return deck;
	}

}