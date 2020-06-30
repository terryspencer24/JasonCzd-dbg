package dbg.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Deck {

	private List<Card> cards = new ArrayList<Card>();

	public void add(Card card) {
		cards.add(card);
	}
	
	public void addToBottom(Deck cards) {
		for (Card card : cards.cards) {
			this.cards.add(0, card);
		}
	}

	public int size() {
		return cards.size();
	}

	public boolean has(Suit suit) {
		for (Card card : cards) {
			if (card.matches(suit)) {
				return true;
			}
		}
		return false;
	}

	public boolean has(Suit suit, Value value) {
		for (Card card : cards) {
			if (card.matches(suit, value)) {
				return true;
			}
		}
		return false;
	}

	public boolean has(Card card) {
		return cards.contains(card);
	}

	@SuppressWarnings("unchecked")
	public List<Card> cards() {
		return (List<Card>) ((ArrayList<Card>) cards).clone();
	}

	public Deck shuffle() {
		Collections.shuffle(cards);
		return this;
	}

	public Card draw() {
		if (cards.size() == 0) {
			throw new RuntimeException("No cards left to draw");
		}
		Card draw = cards.remove(cards.size() - 1);
		return draw;
	}

	public Deck draw(int cnt) {
		Deck deck = new Deck();
		for (int i = 0; i < cnt; i++) {
			deck.add(draw());
		}
		return deck;
	}

	public Card draw(Card card) {
		if (cards.remove(card)) {
			return card;
		} else {
			return null;
		}
	}

	public Deck drawAll() {
		return draw(cards.size());
	}

	public Card cardAt(int idx) {
		return cards.get(idx);
	}

	public Card cardOnTop() {
		return cardAt(cards.size() - 1);
	}

	public List<Card> cards(Suit suit) {
		return StreamSupport.stream(cards.spliterator(), false).filter(card -> card.suit == suit)
				.collect(Collectors.toList());
	}

	public List<Card> cardsNo(Suit suit) {
		return StreamSupport.stream(cards.spliterator(), false).filter(card -> card.suit != suit)
				.collect(Collectors.toList());
	}

	public void sort(Comparator<Card> c) {
		cards.sort(c);
	}

}