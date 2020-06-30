package dbg.game.spades;

public class GameTurnSpadesBid extends GameTurnSpades {

	public SpadesBid bid;

	public GameTurnSpadesBid() {

	}

	public GameTurnSpadesBid(Long userid, SpadesBid bid) {
		this.userid = userid;
		this.bid = bid;
	}

}
