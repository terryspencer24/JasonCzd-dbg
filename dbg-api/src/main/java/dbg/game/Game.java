package dbg.game;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Generic game settings that would be common across all/most games. Individual
 * games would extend this class and would provide any game-level
 * settings/data/logic that are specific to the game.
 */
public abstract class Game<S extends GameSeat, T extends GameTurn> {

	/**
	 * Whether the game has started or not.
	 */
	private boolean isStarted;

	/**
	 * Round the game is currently on.
	 */
	private int round;

	/**
	 * The seats for this game (users + data + logic).
	 */
	public List<S> getSeats() {
		return Collections.unmodifiableList(seats());
	}

	public boolean isSeated(Long userid) {
		for (GameSeat seat : getSeats()) {
			if (seat.userid.equals(userid)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the host for this game.
	 */
	public Long getHost() {
		S seat = seats().stream().filter(s -> s.isHost).findFirst().orElse(null);
		return seat == null ? null : seat.userid;
	}

	/**
	 * Adds game seat to this game.
	 */
	public void addSeat(S seat) {
		if (seats().stream().filter(s -> s.userid == seat.userid).count() > 0) {
			throw new GameRuleViolation("Player " + seat.userid + " is already playing in this game.");
		} else if (seat.isHost && seats().stream().filter(s -> s.isHost).count() > 0) {
			throw new GameRuleViolation("There is already a host playing in this game.");
		} else if (seats().size() == getMaxNumberOfPlayers()) {
			throw new GameRuleViolation("Maximum number of players for this game reached.");
		}
		seats().add(seat);
	}

	/**
	 * Checks game to determine if the game is ready to start.
	 */
	public boolean isReadyToStart() {
		boolean countGood = seats().size() >= getMinNumberOfPlayers();
		boolean oneHost = getHost() != null;
		boolean playersReady = playersReady();
		boolean notStarted = !isStarted;
		return countGood && oneHost && playersReady && notStarted;
	}

	public boolean playersReady() {
		boolean playersReady = seats().stream().filter(seat -> seat.isReady).count() == seats().size();
		return playersReady;
	}

	/**
	 * Is it user's turn
	 */
	public boolean isItMyTurn(Long userid) {
		GameSeat seat = seat(userid);
		return seat.isTurn;
	}

	/**
	 * Return user who's turn it is
	 */
	public GameSeat getCurrentTurn() {
		return seats().stream().filter(seat -> seat.isReady).findFirst().orElse(null);
	}

	/**
	 * Is game started.
	 */
	public boolean isStarted() {
		return isStarted;
	}

	/**
	 * Current round.
	 */
	public int getRound() {
		return round;
	}

	/**
	 * Increments to next round.
	 */
	public void incrementRound() {
		round++;
	}

	/**
	 * Starts the game.
	 */
	public void start() {
		if (isReadyToStart()) {
			initialize();
			isStarted = true;
			round = 1;
		}
	}

	/**
	 * Submits user turn if valid.
	 */
	public void submitTurn(T turn) {
		GameSeat seat = seat(turn.userid);
		if (seat == null) {
			throw new GameRuleViolation("User " + turn.userid + " not found");
		} else if (!seat.isTurn) {
			throw new GameRuleViolation("Not user " + turn.userid + " turn");
		}
		executeTurn(turn);
	}

	/**
	 * Returns seat (if found) for provided user)
	 */
	public S seat(Long userid) {
		return seats().stream().filter(seat -> seat.userid == userid).findFirst().orElse(null);
	}

	/**
	 * If game is over.
	 */
	public boolean isGameOver() {
		if (isStarted) {
			return seats().stream().filter(seat -> seat.isWinner).count() > 0;
		}
		return false;
	}

	/**
	 * If there is any work required prior to persisting, do it here
	 */
	public void pack() {
	}

	/**
	 * If there is any work required after persist retrieval, do it here
	 */
	public void unpack() {
	}

	public abstract void applySettings(Map<String, String> settings);

	public abstract GameMetaData getMetaData();

	public abstract GameImage getGameImage();

	public abstract int getMinNumberOfPlayers();

	public abstract int getMaxNumberOfPlayers();

	public abstract void addSeat(Long id);

	public abstract List<T> getTurns(Long userid);

	public abstract boolean isRoundOver();

	protected abstract void initialize();

	protected abstract List<S> seats();

	protected abstract void executeTurn(T turn);

	protected abstract void validateTurn(T turn);

	protected abstract void completeRound();

}
