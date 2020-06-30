package dbg.game;

@SuppressWarnings("rawtypes")
public abstract class GameBot<G extends Game, S extends GameSeat, T extends GameTurn> {

	public abstract T calculateMove(G game, S seat);

}
