package dbg.game;

import java.util.List;

public class GameSetting {
	
	public String key;

	public String name;
	
	public String description;
	
	public GameSettingType type;
	
	public List<String> allowedValues;
	
	public String value;
	
	public GameSetting(String key, String name, String description, GameSettingType type, List<String> allowedValues, String value) {
		this.key = key;
		this.name = name;
		this.description = description;
		this.type = type;
		this.allowedValues = allowedValues;
		this.value = value;
	}
	
}
