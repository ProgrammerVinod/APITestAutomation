package com.mntn.java.api.test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

import com.mntn.java.api.APIVerb;
import com.mntn.java.api.ExtentManager;
import com.mntn.java.api.ResponseObject;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.response.Response;

public class GetAPI extends APISetup {
	private Map<ResponseObject, String> responseMap;
	private Response response;
	private ExtentReports rep = ExtentManager.getInstance();
	private ExtentTest test;
	private static Logger logger = Logger.getLogger(GetAPI.class);

	@Test
	public void testIndexAPI(Method method) throws MalformedURLException {
		test = rep.startTest(method.getName());
		test.log(LogStatus.INFO, "Starting Test " + method.getName());
		logger.info("Strting Test " + method.getName());

		response = restUtils.getResponse("/", APIVerb.GET);
		responseMap = restUtils.getResponseMap(response);
		test.log(LogStatus.INFO, "Response String : " + response.asPrettyString());
		logger.info("Response String : " + response.asPrettyString());

		Assert.assertEquals(responseMap.get(ResponseObject.StatusCode), "200");
		test.log(LogStatus.PASS, "Step 1 - Status code should be 200");

		Assert.assertTrue(responseMap.get(ResponseObject.Body)
				.contains("This is the openSenseMap API running on https://api.opensensemap.org"));
		test.log(LogStatus.PASS,
				"Step 2 - Title of the response should be 'This is the openSenseMap API running on https://api.opensensemap.org'");

		Assert.assertTrue(responseMap.get(ResponseObject.Body).contains("Version: heads/v9.8-a89ebd4"));
		test.log(LogStatus.PASS, "Step 3 - Version of the response should be 'heads/v9.8-a89ebd4'");

		Assert.assertTrue(responseMap.get(ResponseObject.Body)
				.contains("You can find a detailed reference at https://docs.opensensemap.org"));
		test.log(LogStatus.PASS, "Step 4 - Docs reference of the response should be 'https://docs.opensensemap.org'");

		Assert.assertEquals(responseMap.get(ResponseObject.ContentType), "text/plain; charset=utf-8");
		test.log(LogStatus.PASS, "Step 5 - Validate that Content Type is plain text");

		Assert.assertTrue(Integer.parseInt(responseMap.get(ResponseObject.Time)) > 0);
		test.log(LogStatus.PASS, "Step 6 - Validate the response time : " + responseMap.get(ResponseObject.Time));

		Assert.assertTrue(responseMap.get(ResponseObject.Headers).contains("Content-Length=633"));
		test.log(LogStatus.PASS, "Step 7 - Validate that Content length is correct");

		Assert.assertNull(responseMap.get(ResponseObject.SessionId));
		test.log(LogStatus.PASS, "Step 8 - Validate that Session id is null");

		String[] lines = responseMap.get(ResponseObject.Body).split("\\r?\\n");

		Map<String, String> apiEndPointsWOAuth = new HashMap<>();
		Map<String, String> apiEndPointsWithAuth = new HashMap<>();

		int lineAuth = 0, ctr = 0;

		for (String line : lines) {
			++ctr;

			if (line.equalsIgnoreCase("Routes requiring valid authentication through JWT:")) {
				lineAuth = ctr;
				break;
			}
		}

		ctr = 0;
		for (String line : lines) {
			++ctr;

			if (line.startsWith("GET") || line.startsWith("POST") || line.startsWith("PUT")
					|| line.startsWith("DELETE")) {
				String[] words = line.split("\\s+");
				APIVerb verb = APIVerb.valueOf(words[0].trim());
				String endpoint = words[1].trim();
				String docReference = words[3].trim();

				if (ctr < lineAuth) {
					apiEndPointsWOAuth.put(verb.name() + ":" + endpoint, docReference);
				} else {
					apiEndPointsWithAuth.put(verb.name() + ":" + endpoint, docReference);
				}
			}
		}

		logger.info("API end points with no auth : " + Arrays.toString(apiEndPointsWOAuth.entrySet().toArray()));
		logger.info("API end points with auth : " + Arrays.toString(apiEndPointsWithAuth.entrySet().toArray()));

		test.log(LogStatus.INFO, "API wo Authentication : " + Arrays.toString(apiEndPointsWOAuth.entrySet().toArray()));
		test.log(LogStatus.INFO,
				"API with Authentication : " + Arrays.toString(apiEndPointsWithAuth.entrySet().toArray()));
	}

	@Test
	public void testStatsAPI(Method method) throws MalformedURLException {
		test = rep.startTest(method.getName());
		test.log(LogStatus.INFO, "Starting Test " + method.getName());
		response = restUtils.getResponse("/stats", APIVerb.GET);
		responseMap = restUtils.getResponseMap(response);
		test.log(LogStatus.INFO, "Response String : " + response.asPrettyString());
		logger.info("Response String : " + response.asPrettyString());

		Assert.assertEquals(responseMap.get(ResponseObject.StatusCode), "200");
		test.log(LogStatus.PASS, "Step 1 - Status code should be 200");

		Assert.assertNotNull(responseMap.get(ResponseObject.Body));
		test.log(LogStatus.PASS, "Step 2 - Stats should be displayed");

		Assert.assertEquals(responseMap.get(ResponseObject.ContentType), "application/json; charset=utf-8");
		test.log(LogStatus.PASS, "Step 3 - Validate that Content Type is plain text");

		Assert.assertTrue(Integer.parseInt(responseMap.get(ResponseObject.Time)) > 0);
		test.log(LogStatus.PASS, "Step 4 - Validate the response time : " + responseMap.get(ResponseObject.Time));

		Assert.assertTrue(responseMap.get(ResponseObject.Headers).contains("Content-Length=46"));
		test.log(LogStatus.PASS, "Step 5 - Validate that Content length is correct");

		Assert.assertNull(responseMap.get(ResponseObject.SessionId));
		test.log(LogStatus.PASS, "Step 6 - Validate that Session id is null");
	}

	@AfterMethod
	public void quit() {
		if (rep != null)
			rep.endTest(test);
		rep.flush();
	}
}
