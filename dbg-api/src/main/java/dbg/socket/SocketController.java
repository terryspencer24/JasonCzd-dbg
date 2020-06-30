package dbg.socket;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import dbg.DbgObject;
import dbg.game.Game;
import dbg.game.GameSeat;
import dbg.security.UserRepository;
import dbg.ticket.GameTicket;
import dbg.ticket.GameTicketService;

@Controller
public class SocketController extends DbgObject {

	final static String SOCKET_BROKER = "/out"; // TODO this is also defined in properties as socket broker

	final static String DEST_OUT_ECHO = SOCKET_BROKER + "/echo";

	@Autowired
	SimpMessagingTemplate smt;

	@Autowired
	GameTicketService svcTicket;

	@Autowired
	List<SocketProcessor> processors;

	@Autowired
	UserRepository repoUser;

	@MessageMapping("/echo")
	public void echo(PlayerMessage msg) {
		logger.info("Received echo msg: " + msg.type);
		sendMessage(DEST_OUT_ECHO, "echo", msg);
	}
	
	@MessageMapping("/game/{ticketId}/board")
	public void gameMessageFromBoard(@DestinationVariable("ticketId") String ticketId, PlayerMessage message) throws Exception {
		logger.info("Incoming message on /game/" + ticketId + "/board :: type: " + message.type
				+ "; message: " + message.message);
		synchronized (SocketLock.lock(ticketId)) {
			Long tId = Long.parseLong(ticketId);
			GameTicket ticket = svcTicket.loadFromId(tId);
			processors.stream().filter(p -> p.supports(ticket.gameClass)).findFirst().ifPresent(p -> {
				Game<?, ?> game = svcTicket.loadGameFromTicket(ticket);
				List<ServerMessage> serverMessages = p.process(message, game);
				while (processMessages(ticket, game, serverMessages)) {
					game = svcTicket.loadGameFromTicket(tId);
					serverMessages = p.postProcess(game);
				}
			});
		}
	}

	@MessageMapping("/game/{ticketId}/player/{playerId}")
	public void gameMessageFromPlayer(@DestinationVariable("ticketId") String ticketId,
			@DestinationVariable("playerId") String playerId, PlayerMessage message) throws Exception {
		logger.info("Incoming message on /game/" + ticketId + "/player/" + playerId + " :: type: " + message.type
				+ "; message: " + message.message);
		message.user = repoUser.findById(Long.parseLong(playerId)).get();
		
		synchronized (SocketLock.lock(ticketId)) {
			Long tId = Long.parseLong(ticketId);
			GameTicket ticket = svcTicket.loadFromId(tId);
			processors.stream().filter(p -> p.supports(ticket.gameClass)).findFirst().ifPresent(p -> {
				Game<?, ?> game = svcTicket.loadGameFromTicket(ticket);
				List<ServerMessage> serverMessages = p.process(message, game);
				touchSeat(game, playerId, serverMessages);
				while (processMessages(ticket, game, serverMessages)) {
					game = svcTicket.loadGameFromTicket(tId);
					serverMessages = p.postProcess(game);
				}
			});
		}
	}

	private void touchSeat(Game<?, ?> game, String playerId, List<ServerMessage> serverMessages) {
		GameSeat seat = game.seat(Long.parseLong(playerId));
		if (seat != null) {
			boolean starting = seat.isActive();
			seat.lastCommand = System.currentTimeMillis();
			serverMessages.add(ServerMessage.newSave());
			if (starting != seat.isActive()) {
				// TODO trigger board update when someone comes on or off line
				//serverMessages.add(serverMessage.newBoard());
			}
		}
	}

	private boolean processMessages(GameTicket ticket, Game<?, ?> game, List<ServerMessage> serverMessages) {
		boolean ret = false, saved = false;
		if (serverMessages != null) {
			for (ServerMessage sm : serverMessages) {
				if (sm.destination != null) {
					switch (sm.destination) {
					case BOARD:
						sendMessage(getBoardTopic(ticket.id), sm.message);
						break;
					case PLAYER:
						sendMessage(getPlayerTopic(ticket.id, sm.userid), sm.message);
						break;
					case SAVE:
						if (!saved) {
							svcTicket.saveGameToTicket(ticket, game);
							saved = true;
						}
						break;
					case POST:
						ret = true;
						break;
					}
				}
			}
		}
		return ret;
	}

	private String getPlayerTopic(Long ticketId, Long playerId) {
		return SOCKET_BROKER + "/game/" + ticketId + "/player/" + playerId;
	}

	private String getBoardTopic(Long ticketId) {
		return SOCKET_BROKER + "/game/" + ticketId + "/board";
	}

	private void sendMessage(String topic, String type, Object message) {
		sendMessage(topic, new SocketMessage(type, message));
	}

	private void sendMessage(String topic, SocketMessage message) {
		logger.info("Outgoing message to " + topic + " :: type: " + message.type + "; message: " + message.message);
		smt.convertAndSend(topic, message);
	}

}
