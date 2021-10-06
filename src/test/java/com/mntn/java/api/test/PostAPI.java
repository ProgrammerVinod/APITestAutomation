package com.mntn.java.api.test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.jfairy.Fairy;
import org.jfairy.producer.person.Person;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

import com.mntn.java.api.APIProfile;
import com.mntn.java.api.APIVerb;
import com.mntn.java.api.ExtentManager;
import com.mntn.java.api.ResponseObject;
import com.mntn.java.api.RestAssuredUtils;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.response.Response;

public class PostAPI extends APISetup {
	private Map<ResponseObject, String> responseMap;
	private Response response;
	private ExtentReports rep = ExtentManager.getInstance();
	private ExtentTest test;
	private static Logger logger = Logger.getLogger(PostAPI.class);
	Fairy fairy = Fairy.create();
	Person person = fairy.person();

	@Test
	public void testSenseMeasurementAPI(Method method) throws MalformedURLException {
		test = rep.startTest(method.getName());
		test.log(LogStatus.INFO, "Starting Test " + method.getName());
		logger.info("Strting Test " + method.getName());

		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("boxId", "5386026e5f08822009b8b60d");
		queryParams.put("phenomenon", "Temperatur");
		queryParams.put("from-date", "2015-02-20T22:36:18.843Z");
		queryParams.put("to-date:toDate", "2015-02-23T22:36:18.843Z");

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");

		APIProfile profile = new APIProfile();
		profile.getHeaders().putAll(headers);

		restUtils = new RestAssuredUtils(profile);

		response = restUtils.getResponse(queryParams, "/boxes/data", APIVerb.POST);
		responseMap = restUtils.getResponseMap(response);

		System.out.println(response.asPrettyString());

		test.log(LogStatus.INFO, "Response String : " + response.asPrettyString());
		logger.info("Response String : " + response.asPrettyString());
		
		Assert.assertEquals(responseMap.get(ResponseObject.StatusCode), "200");
		test.log(LogStatus.PASS, "Step 1 - Status code should be 200");

		Assert.assertTrue(responseMap.get(ResponseObject.Body).contains("sensorId,createdAt,value,lat,lon"));
		test.log(LogStatus.PASS, "Step 2 - Header of the response should be as per specifications");

		Assert.assertTrue(responseMap.get(ResponseObject.Body)
				.contains("5386026e5f08822009b8b612,2015-02-23T22:36:18.843Z,4.1,49.57165903690524,10.59951787814498"));
		test.log(LogStatus.PASS, "Step 3 - Response value should be as per data");

		Assert.assertEquals(responseMap.get(ResponseObject.ContentType), "text/csv");
		test.log(LogStatus.PASS, "Step 4 - Validate that Content Type is text/csv");

		Assert.assertTrue(Integer.parseInt(responseMap.get(ResponseObject.Time)) > 0);
		test.log(LogStatus.PASS, "Step 5 - Validate the response time : " + responseMap.get(ResponseObject.Time));

		Assert.assertTrue(responseMap.get(ResponseObject.Headers).contains("Content-Length=126"));
		test.log(LogStatus.PASS, "Step 6 - Validate that Content length is correct");

		Assert.assertNull(responseMap.get(ResponseObject.SessionId));
		test.log(LogStatus.PASS, "Step 7 - Validate that Session id is null");
	}

	@Test
	public void testUserRegisterAPI(Method method) throws MalformedURLException {
		test = rep.startTest(method.getName());
		test.log(LogStatus.INFO, "Starting Test " + method.getName());
		logger.info("Starting Test " + method.getName());

		String userName = person.firstName();
		String email = person.email();
		
		Map<String, String> params = new HashMap<>();
		params.put("name", userName);
		params.put("email", email);
		params.put("password", "Password#123");
		params.put("language", "en_US");
		
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");

		APIProfile profile = new APIProfile();
		profile.getHeaders().putAll(headers);

		restUtils = new RestAssuredUtils(profile);

		response = restUtils.getResponse("/users/register", APIVerb.POST, params);
		responseMap = restUtils.getResponseMap(response);

		System.out.println(response.asPrettyString());

		test.log(LogStatus.INFO, "Response String : " + response.asPrettyString());
		logger.info("Response String : " + response.asPrettyString());
		
		String respCode = restUtils.getResponseBodyField("code");
		String respMessage = restUtils.getResponseBodyField("message");
		String respUserName = restUtils.getResponseBodyField("data.user.name");
		String respUserEmail = restUtils.getResponseBodyField("data.user.email");
		String respUserRole = restUtils.getResponseBodyField("data.user.role");
		String respUserLanguage = restUtils.getResponseBodyField("data.user.language");
		boolean respEmailConfirmed = Boolean.getBoolean(restUtils.getResponseBodyField("data.user.emailIsConfirmed"));
		
		Assert.assertEquals(responseMap.get(ResponseObject.StatusCode), "201");
		test.log(LogStatus.PASS, "Step 1 - Status code should be 201");
		
		Assert.assertEquals(respCode, "Created");
		test.log(LogStatus.PASS, "Step 2 - Response code should be created");
		
		Assert.assertEquals(respMessage, "Successfully registered new user");
		test.log(LogStatus.PASS, "Step 3 - Response msg should be as per spec");
		
		Assert.assertEquals(respUserName, userName);
		test.log(LogStatus.PASS, "Step 4 - Response user " + respUserName + " should be as per data");
		
		Assert.assertEquals(respUserEmail, email);
		test.log(LogStatus.PASS, "Step 5 - Response user email " + respUserEmail + " should be as per data");
		
		Assert.assertEquals(respUserRole, "user");
		test.log(LogStatus.PASS, "Step 6 - Response user role should be user");
		
		Assert.assertEquals(respUserLanguage, "en_US");
		test.log(LogStatus.PASS, "Step 7 - Response user role should be as per data");

		Assert.assertFalse(respEmailConfirmed);
		test.log(LogStatus.PASS, "Step 8 - Response user confirmation should not be done");
		
		Assert.assertEquals(responseMap.get(ResponseObject.ContentType), "application/json; charset=utf-8");
		test.log(LogStatus.PASS, "Step 9 - Validate that Content Type is application/json; charset=utf-8");

		Assert.assertTrue(Integer.parseInt(responseMap.get(ResponseObject.Time)) > 0);
		test.log(LogStatus.PASS, "Step 10 - Validate the response time : " + responseMap.get(ResponseObject.Time));

		Assert.assertNull(responseMap.get(ResponseObject.SessionId));
		test.log(LogStatus.PASS, "Step 11 - Validate that Session id is null");
		
		test.log(LogStatus.INFO, "OAuth Token for user " + userName + " is : " + restUtils.getResponseBodyField("token"));
		test.log(LogStatus.INFO, "OAuth Refresh Token for user " + userName + " is : " + restUtils.getResponseBodyField("refreshToken"));
	}

	@AfterMethod
	public void quit() {
		if (rep != null)
			rep.endTest(test);
		rep.flush();
	}
}
