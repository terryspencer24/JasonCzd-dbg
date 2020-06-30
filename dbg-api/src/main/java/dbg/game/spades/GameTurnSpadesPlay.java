package dbg.game.spades;

import dbg.card.Card;

public class GameTurnSpadesPlay extends GameTurnSpades {

	public Card card;

	public GameTurnSpadesPlay() {
	}

	public GameTurnSpadesPlay(Long userid, Card card) {
		this.userid = userid;
		this.card = card;
	}

}
