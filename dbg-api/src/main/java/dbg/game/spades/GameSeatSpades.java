package dbg.game.spades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dbg.card.Card;
import dbg.card.Deck;
import dbg.card.Suit;
import dbg.game.GameSeat;

public class GameSeatSpades extends GameSeat {

	public Deck hand;

	public List<SpadesBid> bids;

	public GameSeatSpades partner;

	public Map<Integer, List<Card>> cardsPlayed;

	public boolean trickWinner;

	public void setPartner(GameSeatSpades partner) {
		this.partner = partner;
		partner.partner = this;
	}

	public SpadesBid bid(int round) {
		return bids != null && bids.size() >= round ? bids.get(round - 1) : null;
	}

	public Card cardPlayed(int round, int trick) {
		return cardsPlayed != null && cardsPlayed.get(round) != null && cardsPlayed.get(round).size() >= trick
				? cardsPlayed.get(round).get(trick - 1)
				: null;
	}

	public void bid(SpadesBid bid) {
		if (bids == null) {
			bids = new ArrayList<SpadesBid>();
		}
		bids.add(bid);
	}

	public void cardPlayed(int round, Card cardPlayed) {
		if (cardsPlayed == null) {
			cardsPlayed = new HashMap<Integer, List<Card>>();
		}
		if (cardsPlayed.get(round) == null) {
			cardsPlayed.put(round, new ArrayList<Card>());
		}
		cardsPlayed.get(round).add(cardPlayed);
	}

	public boolean spadesPlayed() {
		return cardsPlayed == null ? false : cardsPlayed.values().stream().filter(cards -> {
			return cards.stream().filter(card -> card.suit == Suit.SPADES).count() > 0;
		}).count() > 0;
	}

	public int getScore() {
		return getScore(bids.size());
	}

	public int getScore(int round) {
		int bags = getTotalBags(round);
		int score = getTotalScore(round) - (100 * (bags / 10));
		return score;
	}

	public int getBags() {
		return getTotalBags(bids.size()) % 10;
	}

	public int getBags(int round) {
		return getTotalBags(round) % 10;
	}

	private int getTotalScore(int round) {
		int ttl = 0;
		for (int i = 0; i < round; i++) {
			int books = bids.get(i).getBooks() + (partner != null ? partner.bids.get(i).getBooks() : 0);
			int won = bids.get(i).getWon() + (partner != null ? partner.bids.get(i).getWon() : 0);
			int bags = won > books ? won - books : 0;
			int score = (books * 10 + bags) * (books > won ? -1 : 1);
			ttl += score;
		}
		return ttl;
	}

	private int getTotalBags(int round) {
		int ttl = 0;
		for (int i = 0; i < round; i++) {
			int books = bids.get(i).getBooks() + (partner != null ? partner.bids.get(i).getBooks() : 0);
			int won = bids.get(i).getWon() + (partner != null ? partner.bids.get(i).getWon() : 0);
			int bags = won > books ? won - books : 0;
			ttl += bags;
		}
		return ttl;
	}

}
