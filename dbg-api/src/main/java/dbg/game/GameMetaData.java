package dbg.game;

import java.util.List;

public class GameMetaData {

	public String name;

	public String category;

	public String description;

	public String mainimage;

	public String icon;
	
	public List<GameSetting> settings;

	public GameMetaData(String name, String category, String description, String mainimage, String icon, List<GameSetting> settings) {
		this.name = name;
		this.category = category;
		this.description = description;
		this.mainimage = mainimage;
		this.icon = icon;
		this.settings = settings;
	}

}
