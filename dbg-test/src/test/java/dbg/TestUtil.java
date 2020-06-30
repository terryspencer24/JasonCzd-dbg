package dbg;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class TestUtil {

	private final static Logger logger = LoggerFactory.getLogger(TestUtil.class);

	private static String noschemeURL;

	public static String setupBaseURL() {
		boolean initNeeded = false;
		if (noschemeURL == null) {
			initNeeded = true;
			String sysprop = System.getProperty("server");
			if (StringUtils.isNotBlank(sysprop)) {
				noschemeURL = sysprop;
				logger.info("Setting server from system property (" + noschemeURL + ") ...");
			} else {
				try (InputStream input = TestUtil.class.getClassLoader().getResourceAsStream("test.properties")) {
					Properties prop = new Properties();
					prop.load(input);
					noschemeURL = prop.getProperty("baseurl");
					logger.info("Setting server from properties file (" + noschemeURL + ") ...");
				} catch (IOException ex) {
					logger.error("Error", ex);
				}
			}
		}
		if (noschemeURL == null || "${baseurl}".equals(noschemeURL)) {
			noschemeURL = "localhost:8080/";
			logger.info("Setting server to default value (" + noschemeURL + ") ...");
		}
		if (initNeeded) {
			RestAssured.baseURI = "http://" + noschemeURL;
			RestAssured.defaultParser = Parser.JSON;
			init();
		}
		return noschemeURL;
	}

	private static void init() {
		logger.info("Waiting on server startup ...");
		Response response = null;
		boolean up = false;
		for (int i = 0; i < 30 && !up; i++) {
			try {
				response = get("actuator/health");
				up = response.getStatusCode() == 200 && "UP".equals(response.jsonPath().getString("status"));
			} catch (Throwable e) {
				if (e instanceof ConnectException) {
					System.err.println("Server is not running");
					System.exit(1);
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e2) {
				}
			}
		}
		if (up) {
			logger.info("Server is up!");
		} else {
			throw new RuntimeException("Server doesn't appear to be up!");
		}
	}

	public static Response get(String endpoint) {
		return first().get(endpoint).then().contentType(ContentType.JSON).extract().response();
	}

	public static Response get(String endpoint, Headers headers) {
		return first().headers(headers).get(endpoint).then().contentType(ContentType.JSON).extract().response();
	}

	public static Response post(String endpoint, String body) {
		return first().body(body).post(endpoint).then().contentType(ContentType.JSON).extract().response();
	}

	public static Response post(String endpoint, String body, Headers headers) {
		return first().headers(headers).body(body).post(endpoint).then().contentType(ContentType.JSON).extract()
				.response();
	}

	public static Response put(String endpoint, Object body, Headers headers) {
		return first().headers(headers).body(body).put(endpoint).then().contentType(ContentType.JSON).extract()
				.response();
	}

	private static RequestSpecification first() {
		return given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON).when();
	}

	public static Response delete(String endpoint, Headers headers) {
		return first().headers(headers).delete(endpoint).then().contentType(ContentType.JSON).extract().response();
	}

}
