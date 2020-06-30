package dbg.game;

public class GameRuleViolation extends RuntimeException {

	private static final long serialVersionUID = -998736873728338756L;

	public GameRuleViolation(String msg) {
		super(msg);
	}

}
