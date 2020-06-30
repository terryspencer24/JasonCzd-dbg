package dbg;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.restassured.http.Headers;
import io.restassured.response.Response;

public class TicketIntTest {

	@Before
	public void setUp() {
		TestUtil.setupBaseURL();
	}

	@Test
	public void testCatalog() throws InterruptedException {
		Headers headers = UserIntTest.token();
		Response response = TestUtil.get("/api/tickets", headers);
		Assert.assertEquals(0, response.jsonPath().getList("$").size());

		response = createTicket(headers);
		Long id = response.jsonPath().getLong("'id'");

		response = TestUtil.get("/api/tickets", headers);
		Assert.assertEquals(1, response.jsonPath().getList("$").size());
		Assert.assertEquals(id.longValue(), response.jsonPath().getLong("[0]['id']"));

		response = TestUtil.delete("/api/tickets/" + id, headers);
		Assert.assertEquals("true", response.asString());

		response = TestUtil.delete("/api/tickets/" + id, headers);
		Assert.assertEquals("false", response.asString());

		response = TestUtil.get("/api/tickets", headers);
		Assert.assertEquals(0, response.jsonPath().getList("$").size());
	}

	public static Response createTicket(Headers headers) {
		return TestUtil.post("/api/tickets", "Spades", headers);
	}

	@SuppressWarnings("rawtypes")
	public static Response updateTicket(Headers headers, HashMap obj) {
		return TestUtil.put("/api/tickets", obj, headers);
	}

}
