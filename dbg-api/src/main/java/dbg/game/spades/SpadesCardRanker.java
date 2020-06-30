package dbg.game.spades;

import java.util.Arrays;
import java.util.List;

import dbg.card.Card;
import dbg.card.Suit;
import dbg.card.Value;

public class SpadesCardRanker {

	public static List<Value> VALUES = Arrays.asList(Value.values());

	public GameSeatSpades winner(int round, int trick, int roundStart, GameSeatSpades... seats) {
		GameSeatSpades winner = null;
		if (trick > 1) {
			winner = winner(round, trick - 1, roundStart, seats);
		} else {
			winner = seats[0];
		}
		
		int idx = 0;
		for (int i = 0; i < seats.length; i++) {
			if (seats[i] == winner) {
				idx = i;
				break;
			}
		}
		
		for (int i = 1; i < seats.length; i++) {
			int st = i + idx >= seats.length ? i + idx - seats.length : i + idx;
			if (seats[st].cardPlayed(round, trick).suit == winner.cardPlayed(round, trick).suit) {
				if (VALUES.indexOf(seats[st].cardPlayed(round, trick).value) > VALUES
						.indexOf(winner.cardPlayed(round, trick).value)) {
					winner = seats[st];
				}
			} else if (seats[st].cardPlayed(round, trick).suit == Suit.SPADES) {
				winner = seats[st];
			}
		}
		return winner;
	}

	public GameSeatSpades led(int round, int trick, int roundStart, GameSeatSpades... seats) {
		return trick == 1 ? seats[roundStart == 0 ? 3 : roundStart - 1] : winner(round, trick - 1, roundStart, seats);
	}

	public Suit ledSuit(int round, int trick, int roundStart, GameSeatSpades... seats) {
		GameSeatSpades led = led(round, trick, roundStart, seats);
		Card card = led.cardPlayed(round, trick);
		return card == null ? null : card.suit;
	}

	public void sortByValue(List<Card> cards) {
		cards.sort((c1, c2) -> {
			return new Integer(SpadesCardRanker.VALUES.indexOf(c2.value))
					.compareTo(SpadesCardRanker.VALUES.indexOf(c1.value));
		});
	}

}
