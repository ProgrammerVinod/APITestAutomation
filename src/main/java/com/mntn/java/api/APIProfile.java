package com.mntn.java.api;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class APIProfile {
	private static PropertiesConfiguration config;

	private String baseURI;
	private Environment environment;
	private AuthType authType;

	private boolean enableProxy = false;

	private String proxyHost;
	private String proxyPort;

	private boolean sslEnabled = false;

	private Map<String, String> headers = new HashMap<>();

	public static PropertiesConfiguration getConfig() {
		return config;
	}

	public String getBaseURI() {
		return baseURI;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public AuthType getAuthType() {
		return authType;
	}

	public boolean isEnableProxy() {
		return enableProxy;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public boolean isSslEnabled() {
		return sslEnabled;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}

	public void setHeaders() {
		Map<String, String> requestHeaders = new HashMap<>();

		String headerText = getConfig().getString("headers");

		if (headerText.isEmpty() || headerText.isBlank()) {
			headerText = "None";
		}

		String[] listHeaders = headerText.split("~~");
		for (String headerPart : listHeaders) {
			String[] args = headerPart.split(":");

			if (args[0].equalsIgnoreCase("None")) {
				break;
			} else {
				requestHeaders.put(args[0], args[1]);
			}
		}
		headers.putAll(requestHeaders);
	}

	public static void setConfig(String filePath) throws ConfigurationException {
		config = new PropertiesConfiguration(filePath);
	}

	public APIProfile() {
		environment = Environment.valueOf(getConfig().getString("Environment").toUpperCase());

		// Base URI
		readBaseURI(environment);

		// Setting up the headers
		setHeaders();

		// Auth Details
		readAuth();

		// Proxy Settings
		readProxySettings();

		// SSL Settings
		readSSLSettings();
	}

	public APIProfile(AuthType authType, Object... authParams) {
		environment = Environment.valueOf(getConfig().getString("Environment"));

		// Base URI
		readBaseURI(environment);

		// Setting up the headers
		setHeaders();

		// Auth Details
		handleAuth(authType, authParams);

		// Proxy Settings
		readProxySettings();

		// SSL Settings
		readSSLSettings();
	}

	public APIProfile(Map<String, String> headers, AuthType authType, Object... authParams) {
		environment = Environment.valueOf(getConfig().getString("Environment"));

		// Base URI
		readBaseURI(environment);

		// Setting up the headers
		if (headers.isEmpty()) {
			headers = new HashMap<>();
		}

		this.headers.putAll(headers);

		// Auth Details
		handleAuth(authType, authParams);

		// Proxy Settings
		readProxySettings();

		// SSL Settings
		readSSLSettings();
	}

	public APIProfile(String baseURI, Map<String, String> headers, AuthType authType, Object... authParams) {
		this(headers, authType, authParams);
		setBaseURI(baseURI);
	}

	private void readBaseURI(Environment environment) {
		switch (environment) {
		case DEV:
			this.baseURI = getConfig().getString("DevBaseURL");
			break;
		case QA:
			this.baseURI = getConfig().getString("QABaseURL");
			break;
		case UAT:
			this.baseURI = getConfig().getString("UATBaseURL");
			break;
		case PROD:
			this.baseURI = getConfig().getString("ProdBaseURL");
			break;
		case DR:
			this.baseURI = getConfig().getString("DRBaseURL");
			break;
		default:
			break;
		}
	}

	private void readAuth() {
		authType = AuthType.valueOf(getConfig().getString("AuthType").toUpperCase());
		List<String> authParams = new ArrayList<>();

		String credential = getConfig().getString("Credential");
		authParams.add(credential);
		handleAuth(authType, authParams.toArray());
	}

	private void handleAuth(AuthType authType, Object... authParams) {
		this.authType = authType;
		String credential;

		switch (authType) {
		case BASIC:
			if (authParams != null && authParams.length != 0) {
				credential = authParams[0].toString();
				String userName = credential.split(":")[0];
				String decodedPwd = new String(
						Base64.decodeBase64(credential.split(":")[1].getBytes(StandardCharsets.UTF_8)),
						StandardCharsets.UTF_8);
				credential = userName + ":" + decodedPwd;
			} else {
				throw new IllegalArgumentException("Authorization Params can't be null");
			}
			String encoding = Base64.encodeBase64String(credential.getBytes(StandardCharsets.UTF_8));
			headers.put("Authorization", "Basic " + encoding);
			break;
		case TOKEN:
			if (authParams != null && authParams.length != 0) {
				credential = authParams[0].toString();
			} else {
				throw new IllegalArgumentException("Authorization Params can't be null");
			}
			headers.put("Authorization", "Bearer " + credential);
			break;
		case OAUTH:
			// TODO
			break;
		case OAUTHV2:
			// TODO
			break;
		default:
			break;
		}

	}

	private void readProxySettings() {
		if (getConfig().getString("ProxyEnabled").equalsIgnoreCase("true")) {
			enableProxy = true;
		}

		if (enableProxy) {
			proxyHost = getConfig().getString("ProxyHost");
			proxyPort = getConfig().getString("ProxyPort");
		}
	}

	private void readSSLSettings() {
		if (baseURI.startsWith("https://")) {
			sslEnabled = true;
		}
		// TODO - Read SSL properties (certs or keystore)
	}

}
