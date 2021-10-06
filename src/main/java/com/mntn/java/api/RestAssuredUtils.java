package com.mntn.java.api;

import java.io.File;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestAssuredUtils {
	private final RequestSpecBuilder builder;
	private final APIProfile apiProfile;

	private Map<ResponseObject, String> responseMap = new HashMap<>();

	private Response response;

	public RestAssuredUtils() {
		this(new APIProfile());
	}

	public RestAssuredUtils(APIProfile apiProfile) {
		this.apiProfile = apiProfile;
		RestAssured.config = RestAssured.config().encoderConfig(
				EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false));

		builder = new RequestSpecBuilder();
		builder.addHeaders(apiProfile.getHeaders());
		proxySetting();
	}

	private void proxySetting() {
		if (apiProfile.isEnableProxy()) {
			System.setProperty("https.proxyHost", apiProfile.getProxyHost());
			System.setProperty("http.proxyHost", apiProfile.getProxyHost());

			if (!apiProfile.getProxyPort().isEmpty() && !apiProfile.getProxyPort().isBlank()
					&& !apiProfile.getProxyPort().equals("80")) {
				System.setProperty("https.proxyPort", apiProfile.getProxyPort());
				System.setProperty("http.proxyPort", apiProfile.getProxyPort());
			}
		}
	}

	/**
	 * Hit REST API end-point with no parameters
	 * 
	 * @param endPoint - endPoint
	 * @param verb     - Verb : GET / POST / PUT / DELETE / PATCH
	 * @return Response object
	 * @throws MalformedURLException
	 */
	public Response getResponse(String endPoint, APIVerb verb) throws MalformedURLException {
		return getResponse(endPoint, verb, null, null, null);
	}

	/**
	 * Hit REST API end-point with query parameters
	 * 
	 * @param params   - Query parameters
	 * @param endPoint - endPoint
	 * @param verb     - Verb : GET / POST / PUT / DELETE / PATCH
	 * @return Response object
	 * @throws MalformedURLException
	 */
	public Response getResponse(Map<String, String> params, String endPoint, APIVerb verb)
			throws MalformedURLException {
		return getResponse(endPoint, verb, null, params, null);
	}

	/**
	 * Hit REST API end-point with path parameters
	 * 
	 * @param endPoint   - endPoint
	 * @param pathParams - Path parameters
	 * @param verb       - Verb : GET / POST / PUT / DELETE / PATCH
	 * @return Response object
	 * @throws MalformedURLException
	 */
	public Response getResponse(String endPoint, Map<String, String> pathParams, APIVerb verb)
			throws MalformedURLException {
		return getResponse(endPoint, verb, pathParams, null, null);
	}

	/**
	 * Hit REST API end-point with request payload
	 * 
	 * @param endPoint       - endPoint
	 * @param verb           - Verb : GET / POST / PUT / DELETE / PATCH
	 * @param requestPayload - Request body
	 * @return Response object
	 * @throws MalformedURLException
	 */
	public Response getResponse(String endPoint, APIVerb verb, Object requestPayload) throws MalformedURLException {
		return getResponse(endPoint, verb, null, null, requestPayload);
	}

	/**
	 * Hit REST API end-point with path parameters and request payload
	 * 
	 * @param pathParams     - Path parameters
	 * @param requestPayload - Request body
	 * @param endPoint       - endPoint
	 * @param verb           - Verb : GET / POST / PUT / DELETE / PATCH
	 * @return Response object
	 * @throws MalformedURLException
	 */
	public Response getResponse(Map<String, String> pathParams, Object requestPayload, String endPoint, APIVerb verb)
			throws MalformedURLException {
		return getResponse(endPoint, verb, pathParams, null, requestPayload);
	}

	/**
	 * Hit REST API end-point with path parameters and form/query params
	 * 
	 * @param endPoint   - endPoint
	 * @param pathParams - Path parameters
	 * @param verb       - Verb : GET / POST / PUT / DELETE / PATCH
	 * @param params     - parameters
	 * @return Response object
	 * @throws MalformedURLException
	 */
	public Response getResponse(String endPoint, Map<String, String> pathParams, APIVerb verb,
			Map<String, String> params) throws MalformedURLException {
		return getResponse(endPoint, verb, pathParams, params, null);
	}

	/**
	 * Hit REST API end-point with params and request payload
	 * 
	 * @param endPoint       - endPoint
	 * @param verb           - Verb : GET / POST / PUT / DELETE / PATCH
	 * @param params         - parameters
	 * @param requestPayload - Request body
	 * @return Response object
	 * @throws MalformedURLException
	 */
	public Response getResponse(String endPoint, APIVerb verb, Map<String, String> params, Object requestPayload)
			throws MalformedURLException {
		return getResponse(endPoint, verb, null, params, requestPayload);
	}

	/**
	 * Hit REST API end-point with path parameters, params and request payload
	 * 
	 * @param endPoint       - endPoint
	 * @param verb           - Verb : GET / POST / PUT / DELETE / PATCH
	 * @param pathParams     - Path parameters
	 * @param params         - parameters
	 * @param requestPayload - Request body
	 * @return Response object
	 * @throws MalformedURLException
	 */
	public Response getResponse(String endPoint, APIVerb verb, Map<String, String> pathParams,
			Map<String, String> params, Object requestPayload) throws MalformedURLException {
		return getAPIResponse(endPoint, verb, pathParams, params, requestPayload);
	}

	/**
	 * Hit REST API end-point with request body and multipart params
	 * 
	 * @param endPoint       - endPoint
	 * @param verb           - Verb : GET / POST / PUT / DELETE / PATCH
	 * @param requestPayload - Request body
	 * @param multiPart      - multipart params
	 * @return Response object
	 * @throws MalformedURLException
	 */
	public Response getResponseMultiPart(String endPoint, APIVerb verb, Object requestPayload,
			Map<String, String> multiPart) throws MalformedURLException {
		return getAPIResponseMultiPart(endPoint, verb, requestPayload, multiPart);
	}

	/**
	 * @param oauthGrantType - OAuth Grant Type - PASSWORD / REFRESH_TOEKN
	 * @param endPoint       - endPoint
	 * @param verb           - Verb : GET / POST / PUT / DELETE / PATCH
	 * @param params         - parameters
	 * @return Map containing ResonseObject and values
	 * @throws MalformedURLException
	 */
	public Map<ResponseObject, String> getOAuthBearerToken(OAuthGrantType oauthGrantType, String endPoint, APIVerb verb,
			Map<String, String> params) throws MalformedURLException {
		switch (oauthGrantType) {
		case PASSWORD:
		case REFRESH_TOKEN:
			return getOAuthAccessAndRefreshTokens(endPoint, verb, params);
		default:
			return null;
		}
	}

	/**
	 * Parse response String to a desired type
	 * 
	 * @param responseString - Response body in String
	 * @param classType      - class type - Map or List
	 * @returnReturn object of desired type
	 */
	public Object parseJsonResponseBody(String responseString, String classType) {
		Gson gsonParser = new GsonBuilder().create();
		Type type;

		if (Strings.isNullOrEmpty(classType)) {
			switch (classType.toUpperCase()) {
			case "MAP":
				type = new TypeToken<Map<String, Object>>() {
				}.getType();
				break;
			case "LIST":
				type = new TypeToken<List<Object>>() {
				}.getType();
				break;
			default:
				throw new IllegalArgumentException("Invalid class type : " + classType);
			}
		} else {
			throw new IllegalArgumentException("Class type can't be null");
		}
		return gsonParser.fromJson(responseString, type);
	}

	/**
	 * Get the value of required Response header
	 * 
	 * @param header - header field
	 * @return Value of Response header
	 */
	public String getResponseHeader(String header) {
		return response.getHeader(header);
	}

	/**
	 * Get the value of response body string based on jsonPath
	 * 
	 * @param jsonPath - JsonPath of response field
	 * @return - Value of response field
	 */
	public String getResponseBodyField(String jsonPath) {
		return response.body().jsonPath().getString(jsonPath);
	}

	/**
	 * Get Response Map
	 * 
	 * @param response - response object
	 * @return - Map of ResponseObject with value
	 */
	public Map<ResponseObject, String> getResponseMap(Response response) {
		responseMap.put(ResponseObject.Body, response.getBody().asString());
		getResponseMapOtherParams(response);
		return responseMap;
	}

	private void getResponseMapOtherParams(Response response2) {
		responseMap.put(ResponseObject.StatusCode, Integer.toString(response.getStatusCode()));
		responseMap.put(ResponseObject.ContentType, response.getContentType());
		responseMap.put(ResponseObject.SessionId, response.getSessionId());
		responseMap.put(ResponseObject.Headers, response.getHeaders().asList().toString());
		responseMap.put(ResponseObject.Body, response.getBody().asString());
		responseMap.put(ResponseObject.Time, Long.toString(response.getTime()));
	}

	private Map<ResponseObject, String> getOAuthAccessAndRefreshTokens(String endPoint, APIVerb verb,
			Map<String, String> params) throws MalformedURLException {
		response = getResponse(params, endPoint, verb);
		String accessToken = (String) getResponseBodyField("access_token");
		String refreshToken = (String) getResponseBodyField("refresh_token");

		responseMap = getResponseMap(response);

		responseMap.put(ResponseObject.Oauth_AccessToken, accessToken);
		responseMap.put(ResponseObject.OAuth_RefreshToken, refreshToken);

		return responseMap;
	}

	private Response getAPIResponse(String endPoint, APIVerb verb, Map<String, String> pathParams,
			Map<String, String> params, Object requestPayload) throws MalformedURLException {
		URL url = new URL(apiProfile.getBaseURI() + endPoint);

		RequestSpecification reqSpec = getReqSpecBasedOnBody(pathParams, params, requestPayload);

		switch (verb) {
		case GET:
			response = reqSpec.when().get(url);
			break;
		case POST:
			response = reqSpec.when().post(url);
			break;
		case PUT:
			response = reqSpec.when().put(url);
			break;
		case DELETE:
			response = reqSpec.when().delete(url);
			break;
		case PATCH:
			response = reqSpec.when().patch(url);
			break;
		default:
			throw new IllegalArgumentException("Invalid input for verb : " + verb.name());
		}
		return response;
	}

	private Response getAPIResponseMultiPart(String endPoint, APIVerb verb, Object requestPayload,
			Map<String, String> multiPart) throws MalformedURLException {
		URL url = new URL(apiProfile.getBaseURI() + endPoint);

		RequestSpecification reqSpec = getReqSpecBasedOnBody(null, null, requestPayload, multiPart);

		switch (verb) {
		case GET:
			response = reqSpec.when().get(url);
			break;
		case POST:
			response = reqSpec.when().post(url);
			break;
		case PUT:
			response = reqSpec.when().put(url);
			break;
		case DELETE:
			response = reqSpec.when().delete(url);
			break;
		case PATCH:
			response = reqSpec.when().patch(url);
			break;
		default:
			throw new IllegalArgumentException("Invalid input for verb : " + verb.name());
		}
		return response;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private RequestSpecification getReqSpecBasedOnBody(Map<String, String> pathParams, Map<String, String> params,
			Object requestPayload, Map... multiPartFile) {
		String contentType = (apiProfile.getHeaders().get("Content-Type") == null)
				? apiProfile.getHeaders().get("ContentType")
				: apiProfile.getHeaders().get("Content-Type");
		RequestSpecification reqSpec = RestAssured.given(builder.build());

		if (apiProfile.isSslEnabled()) {
			reqSpec.relaxedHTTPSValidation();
		}

		if (requestPayload != null) {
			if (("application/x-www-form-urlencoded".equals(contentType) || "multipart/form-data".equals(contentType))
					&& requestPayload instanceof Map<?, ?>) {
				reqSpec = reqSpec.with().formParams((Map<String, Object>) requestPayload);
			} else {
				reqSpec = reqSpec.with().body(requestPayload);
			}
		}

		if (params != null) {
			reqSpec = reqSpec.with().queryParams(params);
		}

		if (pathParams != null) {
			reqSpec = reqSpec.with().pathParams(pathParams);
		}

		if (multiPartFile != null && multiPartFile.length > 0) {
			Map<String, String> multiPathMap = multiPartFile[0];

			for (Map.Entry<String, String> entry : multiPathMap.entrySet()) {
				reqSpec = reqSpec.with().multiPart(entry.getKey(), new File(entry.getValue()));
			}
		}

		return reqSpec;
	}
}
