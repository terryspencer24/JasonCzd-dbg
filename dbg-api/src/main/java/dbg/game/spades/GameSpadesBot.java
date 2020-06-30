package dbg.game.spades;

import java.util.List;

import dbg.card.Card;
import dbg.card.Suit;
import dbg.game.GameBot;

public class GameSpadesBot extends GameBot<GameSpades, GameSeatSpades, GameTurnSpades> {

	SpadesCardRanker ranker = new SpadesCardRanker();

	@Override
	public GameTurnSpades calculateMove(GameSpades game, GameSeatSpades seat) {
		List<GameTurnSpades> turns = game.getTurns(seat.userid);
		GameTurnSpades myTurn = null;
		if (turns.size() == 1) {
			GameTurnSpades turn = turns.get(0);
			if (turn instanceof GameTurnSpadesReady) {
				myTurn = new GameTurnSpadesReady();
				myTurn.userid = seat.userid;
			} else if (turn instanceof GameTurnSpadesBid) {
				SpadesBid bid = new SpadesBid(3, 0);
				myTurn = new GameTurnSpadesBid(seat.userid, bid);
			} else if (turn instanceof GameTurnSpadesPlay) {
				Suit suit = game.getSuitLed(seat);
				if (suit != null) { // following
					List<Card> cardsInSuit = seat.hand.cards(suit);
					if (cardsInSuit.size() > 0) {
						ranker.sortByValue(cardsInSuit);
						myTurn = new GameTurnSpadesPlay(seat.userid, cardsInSuit.get(0));
					} else {
						List<Card> cards = seat.hand.cards();
						ranker.sortByValue(cards);
						myTurn = new GameTurnSpadesPlay(seat.userid, cards.get(0));
					}
				} else { // leading
					List<Card> nonSpades = seat.hand.cardsNo(Suit.SPADES);
					if (nonSpades.size() > 0) { // has non-spades
						ranker.sortByValue(nonSpades);
						myTurn = new GameTurnSpadesPlay(seat.userid, nonSpades.get(0));
					} else { // only spades
						List<Card> allCards = seat.hand.cards();
						ranker.sortByValue(allCards);
						myTurn = new GameTurnSpadesPlay(seat.userid, allCards.get(0));
					}
				}
			}
		}
		return myTurn;
	}

}
