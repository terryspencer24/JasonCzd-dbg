package dbg.game.spades;

import java.util.ArrayList;
import java.util.List;

public class GameBoardSpadesState {
	
	public List<Boolean> lastCommands = new ArrayList<>();
	
	public List<String> cards = new ArrayList<>();
	
	public List<SpadesBid> bids = new ArrayList<>();
	
	public Integer trickWinner;

	public Integer animate;
	
}
