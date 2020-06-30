package dbg;

import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import junit.framework.Assert;

public class UserIntTest {

	@BeforeClass
	public static void setUp() {
		TestUtil.setupBaseURL();
	}

	@Test
	public void testUnauth() {
		Response response = TestUtil.get("/api/users");
		Assert.assertEquals(401, response.getStatusCode());
	}

	@Test
	public void testInvalidLogin() {
		Response response = TestUtil.post("/papi/users/login", "{\"username\":\"xxjason\",\"password\":\"xxjason\"}");
		Assert.assertEquals(401, response.getStatusCode());
		Assert.assertEquals(null, response.jsonPath().getString("value"));
	}

	@Test
	public void testInvalidPassword() {
		Response response = TestUtil.post("/papi/users/login", "{\"username\":\"jason\",\"password\":\"xxjason\"}");
		Assert.assertEquals(401, response.getStatusCode());
		Assert.assertEquals(null, response.jsonPath().getString("value"));
	}

	@Test
	public void testLogin() {
		Response response = TestUtil.post("/papi/users/login", userjson());
		Assert.assertEquals(200, response.getStatusCode());
		Assert.assertNotNull(response.jsonPath().getString("value"));
	}

	@Test
	public void testUsersEndpoint() {
		Response response2 = TestUtil.get("/api/users", token());
		Assert.assertEquals(200, response2.getStatusCode());
		Assert.assertEquals(1, response2.jsonPath().getList("$").size());
		Assert.assertEquals(1, response2.jsonPath().getLong("[0]['id']"));
		Assert.assertEquals("jason", response2.jsonPath().getString("[0]['username']"));
	}

	@Test
	public void testLogout() {
		Headers token = token();
		Response response2 = TestUtil.get("/api/users", token);
		Assert.assertEquals(200, response2.getStatusCode());
		Response response3 = TestUtil.get("/api/users/logout", token);
		Assert.assertEquals(200, response3.getStatusCode());
		Response response4 = TestUtil.get("/api/users", token);
		Assert.assertEquals(401, response4.getStatusCode());
	}

	@Test
	public void testCreateUser() {
		TestUtil.post("/papi/users/register", userjson("jason2", "jason2"));
		Headers token = token(userjson("jason2", "jason2"));
		Response response2 = TestUtil.get("/api/users", token);
		Assert.assertEquals(200, response2.getStatusCode());
		Assert.assertEquals("jason2", response2.jsonPath().getString("[0]['username']"));
	}

	public static String tokenValue() {
		return token(userjson()).get("Authorization").getValue();
	}

	public static Headers token() {
		return token(userjson());
	}

	public static Headers token(String userjson) {
		Response tokenResponse = TestUtil.post("/papi/users/login", userjson);
		String token = tokenResponse.jsonPath().getString("value");
		return new Headers(new Header("Authorization", "Bearer " + token));
	}

	public static String userjson() {
		return userjson("jason", "jason");
	}

	public static String userjson(String user, String pass) {
		return "{\"username\":\"" + user + "\",\"password\":\"" + pass + "\"}";
	}

}
