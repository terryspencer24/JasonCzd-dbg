package dbg.game.spades;

import java.util.ArrayList;
import java.util.Arrays;

import dbg.game.GameMetaData;
import dbg.game.GameSetting;
import dbg.game.GameSettingType;

public class GameMetaDataSpades extends GameMetaData {
	
	public final static String SPADES_SCORE = "SPADES_SCORE";
	
	public final static String SPADES_NIL = "SPADES_NIL";

	public GameMetaDataSpades() {
		super("Spades", "Card Game",
				"Spades is a trick-taking card game devised in the United States in the 1930s. The object is to take at least the number of tricks (or books) that were bid before play of the hand began. Spades is a descendant of the Whist family of card games, which also includes Bridge and Hearts. Its major difference as compared to other Whist variants is that, instead of trump being decided by the highest bidder or at random, the Spade suit always trumps, hence the name.",
				"images/cards/AS.jpg", "images/cards/AS.png", Arrays.asList(
						new GameSetting(SPADES_SCORE, "Score", "The score to play to", GameSettingType.Number, new ArrayList<String>(), "300"),
						new GameSetting(SPADES_NIL, "Nil Bidding", "Allow nil bidding", GameSettingType.Boolean, new ArrayList<String>(), "false")
				));
	}

}
