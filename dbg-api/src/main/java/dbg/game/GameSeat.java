package dbg.game;

/**
 * GameSeat represents the user in a game, and contains common data and logic to
 * be used across all games. The Seat object for individual games would extend
 * this and provide any additional data/logic. As an example, the Seat object
 * for War would also contain their hand, the hidden cards on table, and the
 * visible cards on table.
 */
public abstract class GameSeat {

	/**
	 * If the user is the host of this game.
	 */
	public boolean isHost;

	/**
	 * Whether the player has indicated they are ready or not.
	 */
	public boolean isReady;

	/**
	 * If it is the player's turn.
	 */
	public boolean isTurn;

	/**
	 * If player is winner.
	 */
	public boolean isWinner;

	/**
	 * User id in seat.
	 */
	public Long userid;

	/**
	 * Last time in milliseconds that the user sent a command
	 */
	public Long lastCommand;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GameSeat) {
			return ((GameSeat) obj).userid == userid;
		}
		return false;
	}

	public boolean isBot() {
		return userid < 0;
	}

	public boolean isActive() {
		return isBot() || (lastCommand != null && System.currentTimeMillis() - lastCommand < 300000);
	}

}
