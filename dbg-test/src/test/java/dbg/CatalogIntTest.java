package dbg;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.restassured.response.Response;

public class CatalogIntTest {

	@Before
	public void setUp() {
		TestUtil.setupBaseURL();
	}

	@Test
	public void testCatalog() throws InterruptedException {
		Response response = TestUtil.get("/api/catalog", UserIntTest.token());
		Assert.assertEquals(1, response.jsonPath().getList("$").size());
		Assert.assertEquals("Spades", response.jsonPath().getString("[0]['name']"));
		Assert.assertEquals("Card Game", response.jsonPath().getString("[0]['category']"));
	}

}
