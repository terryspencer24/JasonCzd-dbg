package dbg;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.restassured.response.Response;

public class TestIt {

	@Before
	public void setUp() {
		TestUtil.setupBaseURL();
	}

	@Test
	public void testApi() throws InterruptedException {
		Response response = TestUtil.get("/");
		Assert.assertEquals("Digital Game Board", response.jsonPath().getString("msg"));
	}

}
