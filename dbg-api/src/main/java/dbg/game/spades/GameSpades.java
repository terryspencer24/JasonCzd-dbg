package dbg.game.spades;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import dbg.BeanUtil;
import dbg.card.Card;
import dbg.card.Deck;
import dbg.card.DeckService;
import dbg.card.Suit;
import dbg.game.Game;
import dbg.game.GameImage;
import dbg.game.GameMetaData;
import dbg.game.GameRuleViolation;

@Component
public class GameSpades extends Game<GameSeatSpades, GameTurnSpades> {

	private List<GameSeatSpades> seats = new ArrayList<GameSeatSpades>();

	/**
	 * Player who started the current round (so next round the next player will
	 * start)
	 */
	private int roundStart;

	/**
	 * Current trick number
	 */
	private int trickNumber;

	/**
	 * Score when game is over
	 */
	private int scoreToPlayTo = 300;

	/**
	 * Whether players can bid 0 books
	 */
	private boolean supportsNil = false;

	SpadesCardRanker ranker = new SpadesCardRanker();

	@Override
	public void applySettings(Map<String, String> settings) {
		if (settings != null) {
			if (settings.containsKey(GameMetaDataSpades.SPADES_SCORE)) {
				String value = settings.get(GameMetaDataSpades.SPADES_SCORE);
				try {
					scoreToPlayTo = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			if (settings.containsKey(GameMetaDataSpades.SPADES_NIL)) {
				String value = settings.get(GameMetaDataSpades.SPADES_NIL);
				supportsNil = Boolean.parseBoolean(value);
			}
		}
	}

	@Override
	protected List<GameSeatSpades> seats() {
		return seats;
	}

	@Override
	public GameImage<GameSpades> getGameImage() {
		return new GameImageSpades();
	}

	@Override
	public int getMinNumberOfPlayers() {
		return 4;
	}

	@Override
	public int getMaxNumberOfPlayers() {
		return 4;
	}

	@Override
	public void addSeat(Long id) {
		GameSeatSpades seat = new GameSeatSpades();
		seat.userid = id;
		if (seat.isBot()) {
			seat.isReady = true;
		}
		addSeat(seat);
	}

	public void setNilAllowed(boolean supportsNil) {
		this.supportsNil = supportsNil;
	}

	public int getTrick() {
		return trickNumber;
	}

	@Override
	protected void initialize() {
		startNewRound();
		setupPartners();
	}

	private void setupPartners() {
		seats.get(0).setPartner(seats.get(2));
		seats.get(1).setPartner(seats.get(3));
	}

	private void startNewRound() {
		Deck deck = BeanUtil.getBean(DeckService.class).createStandardDeck();
		deck.shuffle();
		int cardCount = deck.size() / seats.size();
		seats.forEach(seat -> {
			seat.hand = deck.draw(cardCount);
			seat.isTurn = false;
			seat.trickWinner = false;
		});
		seats.get(roundStart).isTurn = true;
		roundStart = roundStart < seats.size() - 1 ? roundStart + 1 : 0;
		trickNumber = 1;
		incrementRound();
	}

	@Override
	public List<GameTurnSpades> getTurns(Long userid) {
		List<GameTurnSpades> ret = new ArrayList<GameTurnSpades>();
		if (isItMyTurn(userid)) {
			GameSeatSpades seat = seat(userid);
			if (seat.bid(getRound()) == null) {
				ret.add(new GameTurnSpadesBid());
			} else if (seat.cardPlayed(getRound(), trickNumber) == null) {
				ret.add(new GameTurnSpadesPlay());
			} else {
				ret.add(new GameTurnSpadesReady());
			}
		}
		return ret;
	}

	@Override
	protected void executeTurn(GameTurnSpades turn) {
		GameSeatSpades seat = seat(turn.userid);
		if (bidding()) {
			if (turn instanceof GameTurnSpadesBid) {
				GameTurnSpadesBid bid = (GameTurnSpadesBid) turn;
				seat.bid(bid.bid);
				flipTurnFlag(true);
			} else {
				throw new GameRuleViolation("Bid is required");
			}
		} else if (tricking()) {
			if (turn instanceof GameTurnSpadesPlay) {
				GameTurnSpadesPlay play = (GameTurnSpadesPlay) turn;
				validateTurn(turn);
				Card card = seat.hand.draw(play.card);
				seat.cardPlayed(getRound(), card);

				if (tricking()) {
					flipTurnFlag(true);
				} else {
					GameSeatSpades winner = ranker.winner(getRound(), trickNumber, roundStart,
							seats.toArray(new GameSeatSpades[0]));
					winner.bid(getRound()).incrementWon();
					winner.trickWinner = true;
					seats.forEach(s -> {
						s.isReady = false;
						s.isTurn = true;
					});
				}
			} else {
				throw new GameRuleViolation("Play is required now");
			}
		} else {
			if (turn instanceof GameTurnSpadesReady) {
				seat.isTurn = false;
				seat.isReady = true;
				if (playersReady()) {
					boolean roundOver = isRoundOver();
					seats.forEach(s -> {
						s.isTurn = roundOver ? false : s.trickWinner;
						s.trickWinner = false;
					});
					trickNumber++;
					if (roundOver) {
						completeRound();
					}
				}
			} else {
				throw new GameRuleViolation("Ready is required first");
			}
		}
	}

	@Override
	public void validateTurn(GameTurnSpades turn) {
		if (turn instanceof GameTurnSpadesPlay) {
			GameTurnSpadesPlay playturn = (GameTurnSpadesPlay) turn;
			GameSeatSpades seat = seat(turn.userid);
			if (!seat.hand.has(playturn.card)) {
				throw new GameRuleViolation("That card does not exist in hand");
			} else {
				Suit suitLed = ranker.ledSuit(getRound(), trickNumber, roundStart,
						seats.toArray(new GameSeatSpades[0]));
				if (suitLed != null) {
					if (playturn.card.suit != suitLed && seat.hand.has(suitLed)) {
						throw new GameRuleViolation("Suit of lead card must be followed");
					}
				} else if (playturn.card.suit == Suit.SPADES && !spadesBroken()
						&& seat.hand.cardsNo(Suit.SPADES).size() != 0) {
					throw new GameRuleViolation("Cannot lead with spades until spades have been played");
				}
			}
		}
	}

	private boolean spadesBroken() {
		return seats.stream().filter(seat -> seat.spadesPlayed()).count() > 0;
	}

	private boolean bidding() {
		return seats.stream().filter(s -> s.bid(getRound()) == null).count() > 0;
	}

	private boolean tricking() {
		long cnt = seats.stream().filter(s -> s.cardPlayed(getRound(), trickNumber) != null).count();
		return cnt < seats.size();
	}

	private void flipTurnFlag(boolean flipNext) {
		for (int i = 0; i < seats.size() - 1; i++) {
			if (seats.get(i).isTurn) {
				seats.get(i).isTurn = false;
				seats.get(i + 1).isTurn = flipNext;
				return;
			}
		}
		seats.get(seats.size() - 1).isTurn = false;
		seats.get(0).isTurn = flipNext;
	}

	@Override
	public boolean isRoundOver() {
		return seats.stream().filter(s -> s.hand.size() == 0).count() == seats.size();
	}

	@Override
	protected void completeRound() {
		seats.forEach(s -> {
			if (s.getScore() >= scoreToPlayTo) {
				s.isWinner = true;
			}
		});
		if (!isGameOver()) {
			startNewRound();
		}
	}

	public SpadesBid createBid(int books) {
		if (books == 0 && !supportsNil) {
			throw new GameRuleViolation("Cannot bid nil in this game");
		} else if (books > 13) {
			throw new GameRuleViolation("Cannot bid more than 13 books");
		}
		return new SpadesBid(books, 0);
	}

	public Suit getSuitLed(GameSeatSpades seat) {
		return ranker.ledSuit(getRound(), trickNumber, roundStart, seats.toArray(new GameSeatSpades[0]));
	}

	/**
	 * To prevent circular dependency issue with serialization
	 */
	@Override
	public void pack() {
		super.pack();
		if (seats.size() >= 3) {
			seats.get(2).partner = null;
		}
		if (seats.size() == 4) {
			seats.get(3).partner = null;
		}
	}

	/**
	 * To prevent circular dependency issue with serialization
	 */
	@Override
	public void unpack() {
		super.unpack();
		if (seats.size() >= 3) {
			seats.get(0).setPartner(seats.get(2));
		}
		if (seats.size() == 4) {
			seats.get(1).setPartner(seats.get(3));
		}
	}

	@Override
	public GameMetaData getMetaData() {
		return new GameMetaDataSpades();
	}

}
