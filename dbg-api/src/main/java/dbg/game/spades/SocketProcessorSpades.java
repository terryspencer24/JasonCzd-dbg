package dbg.game.spades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import dbg.DbgObject;
import dbg.card.Card;
import dbg.card.Deck;
import dbg.card.PlayableCard;
import dbg.card.Suit;
import dbg.card.Value;
import dbg.game.GameImage;
import dbg.game.GameRuleViolation;
import dbg.game.GameSeat;
import dbg.socket.PlayerMessage;
import dbg.socket.ServerMessage;
import dbg.socket.SocketDestination;
import dbg.socket.SocketMessage;
import dbg.socket.SocketProcessor;

@Component
public class SocketProcessorSpades extends DbgObject implements SocketProcessor<GameSpades> {

	@Override
	public boolean supports(String clz) {
		return "dbg.game.spades.GameSpades".equals(clz);
	}

	@Override
	public List<ServerMessage> process(PlayerMessage message, GameSpades game) {
		List<ServerMessage> messages = new ArrayList<ServerMessage>();
		try {
			GameSeatSpades seat = message.user == null ? null : game.seat(message.user.id);
			switch (message.type) {
			case "board":
				messages.add(broadcastBoard(game, null));
				break;
			case "status":
				messages.add(broadcastState(game, message.user.id));
				messages.add(broadcastHand(game, message.user.id));
				break;
			case "ready":
				GameTurnSpadesReady turnReady = new GameTurnSpadesReady();
				turnReady.userid = message.user.id;
				if (!game.isStarted()) {
					seat.isReady = true;
					if (game.isReadyToStart()) {
						game.start();
					}
					messages.add(0, broadcastSave());
				} else {
					game.submitTurn(turnReady);
					messages.add(0, broadcastSave());
				}
				messages.addAll(broadcastAfterTurn(game, turnReady));
				messages.add(broadcastPost());
				break;
			case "bid":
				GameTurnSpadesBid turnBid = new GameTurnSpadesBid();
				turnBid.userid = message.user.id;
				turnBid.bid = game.createBid(Integer.parseInt(message.message));
				game.submitTurn(turnBid);
				messages.add(0, broadcastSave());
				messages.addAll(broadcastAfterTurn(game, turnBid));
				messages.add(broadcastPost());
				break;
			case "play":
				GameTurnSpadesPlay turnPlay = new GameTurnSpadesPlay();
				turnPlay.userid = message.user.id;
				for (Card card : seat.hand.cards()) {
					if (GameImage.name(card).equals(message.message)) {
						turnPlay.card = card;
					}
				}
				game.submitTurn(turnPlay);
				messages.add(0, broadcastSave());
				messages.addAll(broadcastAfterTurn(game, turnPlay));
				messages.add(broadcastPost());
				break;
			}
		} catch (GameRuleViolation grv) {
			messages.add(broadcastError(message.user.id, grv.getMessage()));
		}
		return messages;
	}

	@Override
	public List<ServerMessage> postProcess(GameSpades game) {
		List<ServerMessage> messages = new ArrayList<ServerMessage>();
		for (GameSeatSpades seat : game.getSeats()) {
			if (seat.isBot()) {
				GameSpadesBot bot = new GameSpadesBot();
				seat = game.seat(seat.userid);
				if (game.isItMyTurn(seat.userid)) {
					try {
						GameTurnSpades turn = bot.calculateMove(game, seat);
						logger.trace("Bot " + seat.userid + " is executing turn " + turn.getClass().getName());
						if (!(turn instanceof GameTurnSpadesReady)) {
							logger.trace("Pausing for realism!");
							Thread.sleep(1000); // simulate pause for bots
						}
						game.submitTurn(turn);
						logger.trace("Turn submitted for " + seat.userid + " with " + turn.getClass().getName());
						messages.add(0, broadcastSave());
						messages.addAll(broadcastAfterTurn(game, turn));
						messages.add(broadcastPost());
						return messages;
					} catch (GameRuleViolation grv) {
						grv.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return messages;
	}

	private ServerMessage broadcastError(Long userid, String msg) {
		ServerMessage message = new ServerMessage();
		message.destination = SocketDestination.PLAYER;
		message.userid = userid;
		message.message = new SocketMessage("error", msg);
		return message;
	}

	private ServerMessage broadcastSave() {
		ServerMessage message = new ServerMessage();
		message.destination = SocketDestination.SAVE;
		return message;
	}

	private ServerMessage broadcastPost() {
		ServerMessage message = new ServerMessage();
		message.destination = SocketDestination.POST;
		return message;
	}

	private List<ServerMessage> broadcastAfterTurn(GameSpades game, GameTurnSpades turn) {
		List<ServerMessage> messages = new ArrayList<ServerMessage>();
		if (turn instanceof GameTurnSpadesReady) {
			messages.addAll(broadcastStates(game));
			messages.addAll(broadcastHands(game));

			// // if there are no bids for this round, hands must be sent
			// int bidCount = 0;
			// for (GameSeatSpades seat : game.getSeats()) {
			// bidCount += seat.bid(game.getRound()) == null ? 0 : 1;
			// }
			// logger.trace("bidCount is " + bidCount + " for round " + game.getRound());
			// if (bidCount == 0) {
			// messages.addAll(broadcastHands(game));
			// }

		} else if (turn instanceof GameTurnSpadesBid) {
			messages.add(broadcastState(game, turn.userid));
			for (GameSeat seat : game.getSeats()) {
				if (seat.isTurn) {
					messages.add(broadcastState(game, seat.userid));
				}
			}
		} else if (turn instanceof GameTurnSpadesPlay) {
			messages.add(broadcastState(game, turn.userid));
			int cardCount = 0;
			for (GameSeatSpades seat : game.getSeats()) {
				cardCount += seat.cardPlayed(game.getRound(), game.getTrick()) == null ? 0 : 1;
				if (seat.isTurn) {
					messages.add(broadcastState(game, seat.userid));
				}
			}
			logger.trace("Card count in this trick is " + cardCount);
			if (cardCount == 1) {
				messages.addAll(broadcastHands(game));
			} else {
				messages.add(broadcastHand(game, turn.userid));
			}
		}
		messages.add(broadcastBoard(game, turn));
		return messages;
	}

	private List<ServerMessage> broadcastStates(GameSpades game) {
		List<ServerMessage> messages = new ArrayList<ServerMessage>();
		for (Long userid : game.getSeats().stream().filter(s -> !s.isBot()).map(s -> s.userid)
				.collect(Collectors.toList())) {
			messages.add(broadcastState(game, userid));
		}
		messages.add(broadcastBoard(game, null));
		return messages;
	}

	private ServerMessage broadcastState(GameSpades game, Long userid) {
		ServerMessage message = new ServerMessage();
		message.destination = SocketDestination.PLAYER;
		message.userid = userid;
		GameSeat seat = game.seat(userid);
		logger.trace("Looking at player " + userid);
		if (!game.isStarted()) {
			message.message = new SocketMessage("state", seat == null || seat.isReady ? "wait" : "ready");
		} else if (game.isItMyTurn(userid)) {
			List<GameTurnSpades> turns = game.getTurns(userid);
			String state = "bid";
			if (turns.get(0) instanceof GameTurnSpadesPlay) {
				state = "play";
			} else if (turns.get(0) instanceof GameTurnSpadesReady) {
				state = "ready";
			}
			logger.trace(">>> It is my turn, and my state is " + state);
			message.destination = SocketDestination.PLAYER;
			message.message = new SocketMessage("state", state);
		} else {
			logger.trace(">>> It is not my turn, and my state will be wait");
			message.message = new SocketMessage("state", "wait");
		}
		return message;
	}

	private List<ServerMessage> broadcastHands(GameSpades game) {
		List<ServerMessage> messages = new ArrayList<ServerMessage>();
		for (Long userid : game.getSeats().stream().filter(s -> !s.isBot()).map(s -> s.userid)
				.collect(Collectors.toList())) {
			messages.add(broadcastHand(game, userid));
		}
		messages.add(broadcastBoard(game, null));
		return messages;
	}

	private ServerMessage broadcastHand(GameSpades game, Long userid) {
		ServerMessage message = new ServerMessage();
		GameSeatSpades seat = game.seat(userid);
		Deck deck = seat.hand;
		List<Suit> suits = Arrays.asList(Suit.values());
		List<Value> values = Arrays.asList(Value.values());
		if (deck != null) {
			deck.sort(new Comparator<Card>() {
				@Override
				public int compare(Card o1, Card o2) {
					int s1 = suits.indexOf(o1.suit);
					int s2 = suits.indexOf(o2.suit);
					int v1 = values.indexOf(o1.value);
					int v2 = values.indexOf(o2.value);
					if (s1 > s2)
						return 1;
					else if (s2 > s1)
						return -1;
					else if (v1 > v2)
						return 1;
					else if (v2 > v1)
						return -1;
					else
						return 0;
				}
			});
			List<PlayableCard> cards = new ArrayList<PlayableCard>();
			deck.cards().forEach(c -> {
				PlayableCard card = new PlayableCard();
				card.name = GameImage.name(c);
				card.valid = true;
				try {
					logger.trace("Evaluating playing " + c.toString() + " " + seat.userid);
					game.validateTurn(new GameTurnSpadesPlay(seat.userid, c));
					logger.trace("Validation OK");
				} catch (GameRuleViolation e) {
					logger.trace("Validation failed", e);
					card.valid = false;
				}
				cards.add(card);
			});
			message.destination = SocketDestination.PLAYER;
			message.userid = userid;
			message.message = new SocketMessage("hand", cards);
		}
		return message;
	}

	private ServerMessage broadcastBoard(GameSpades game, GameTurnSpades turn) {
		ServerMessage message = new ServerMessage();
		message.destination = SocketDestination.BOARD;
		message.message = new SocketMessage("update", "update");

		GameBoardSpadesState board = new GameBoardSpadesState();
		List<GameSeatSpades> seats = game.getSeats();
		boolean isPlay = turn != null && turn instanceof GameTurnSpadesPlay;
		for (int i = 0; i < seats.size(); i++) {
			GameSeatSpades seat = seats.get(i);
			
			board.lastCommands.add(seat.isActive());
			
			if (seat.trickWinner) {
				board.trickWinner = i;
			}

			Card card = seat.cardPlayed(game.getRound(), game.getTrick());
			board.cards.add(card == null ? null : GameImage.name(card));
			SpadesBid bid = seat.bid(game.getRound());
			board.bids.add(bid);
			
			if (isPlay && turn.userid.equals(seat.userid)) {
				board.animate = i;
			}
		}
		
		message.message = new SocketMessage("update", board);

		return message;
	}

}
