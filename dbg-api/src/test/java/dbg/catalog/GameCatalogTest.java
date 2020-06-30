package dbg.catalog;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import dbg.game.spades.GameSpades;

public class GameCatalogTest {

	@Test
	public void test() {
		GameCatalog catalog = new GameCatalog();
		catalog.catalog2 = new ArrayList<>();
		catalog.catalog2.add(new GameSpades());
		Assert.assertEquals("dbg.game.spades.GameSpades", catalog.getClz("Spades"));
		Assert.assertEquals("Spades", catalog.getName("dbg.game.spades.GameSpades"));
	}

}
