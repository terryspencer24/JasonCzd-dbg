package dbg.socket;

import java.util.List;

import dbg.game.Game;

public interface SocketProcessor<T extends Game<?, ?>> {

	public boolean supports(String clz);

	public List<ServerMessage> process(PlayerMessage message, T game);

	public List<ServerMessage> postProcess(T game);

}
