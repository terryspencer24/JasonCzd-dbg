package dbg.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dbg.game.Game;
import dbg.game.GameMetaData;

@Component
public class GameCatalog {

	@Autowired(required = false)
	List<Game<?, ?>> catalog2;

	@PostConstruct
	public void setup() {
		if (catalog2 == null || catalog2.isEmpty()) {
			catalog2 = new ArrayList<>();
		}
	}

	public List<GameMetaData> getCatalog() {
		return catalog2.stream().map(g -> g.getMetaData()).collect(Collectors.toList());
	}

	public String getClz(String identifier) {
		return catalog2.stream().filter(g -> g.getMetaData().name.equals(identifier)).map(g -> g.getClass().getName())
				.findFirst().orElse(null);
	}

	public String getName(String clz) {
		return catalog2.stream().filter(g -> g.getClass().getName().equals(clz)).map(g -> g.getMetaData().name)
				.findFirst().orElse(null);
	}

}
