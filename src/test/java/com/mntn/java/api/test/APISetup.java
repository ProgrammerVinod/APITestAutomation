package com.mntn.java.api.test;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeSuite;

import com.mntn.java.api.APIProfile;
import com.mntn.java.api.Constants;
import com.mntn.java.api.FileHandlingUtils;
import com.mntn.java.api.RestAssuredUtils;

public class APISetup {
	protected RestAssuredUtils restUtils;

	@BeforeSuite(enabled = true)
	public void setup() throws IOException, ConfigurationException {
		String todaysDate = DateFormat.getDateTimeInstance().format(new Date()).toString().replaceAll(":", "_")
				.replaceAll("\\s+", "_").replaceAll(",", "");
		String userName = System.getProperty("user.name");
		String hostName = "Global";

		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		}

		String reportZip = Constants.ARCHIVE_PATH + File.separatorChar + hostName + "_" + userName + "_Report_"
				+ todaysDate + ".zip";

		FileHandlingUtils.createDirectory(new File(Constants.ARCHIVE_PATH).getAbsolutePath());
		FileHandlingUtils.zipDir(new File(Constants.REPORT_PATH).getAbsolutePath(),
				new File(reportZip).getAbsolutePath());
		FileHandlingUtils.deleteDirectory(new File(Constants.REPORT_PATH).getAbsolutePath());
		FileHandlingUtils.createDirectory(new File(Constants.REPORT_PATH).getAbsolutePath());

		APIProfile.setConfig(
				"src" + File.separator + "test" + File.separator + "resources" + File.separator + "config.properties");
		restUtils = new RestAssuredUtils();
		PropertyConfigurator.configure("log4j.properties");
	}
}
