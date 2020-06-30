package dbg.catalog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dbg.game.GameMetaData;

@RestController
@RequestMapping("/api/catalog")
public class GameCatalogController {

	@Autowired
	GameCatalog catalog;

	@GetMapping
	public List<GameMetaData> catalog() {
		return catalog.getCatalog();
	}

}
