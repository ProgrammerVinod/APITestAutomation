package com.mntn.java.api;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

public class ExtentManager {
	private static ExtentReports extent;
	//
	ExtentReports reports;
	ExtentTest test;

	String catchImagePath;
	String attachImagePath;

	public static String generateDataTime() {
		Date now = new Date();
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		String currentDate = df.format(now).toString().replace("/", "_");
		currentDate = currentDate.toString().replace(" ", "_");
		currentDate = currentDate.toString().replace(":", "_");
		currentDate = currentDate.toString().replace(",", "_");

		return currentDate;
	}

	public static ExtentReports getInstance() {
		if (extent == null) {
			Date d = new Date();
			String fileName = d.toString().replace(":", "_").replace(" ", "_") + ".html";
			try {
				File reportPath = new File(Constants.REPORT_PATH);

				if (!reportPath.exists())
					reportPath.mkdirs();

				extent = new ExtentReports(
						new File(Constants.REPORT_PATH + File.separator + fileName).getCanonicalPath(), true,
						DisplayOrder.NEWEST_FIRST);
			} catch (IOException e) {
				e.printStackTrace();
			}

			extent.loadConfig(new File("src" + File.separator + "test" + File.separator + "resources" + File.separator
					+ "ReportConfig.xml"));
			extent.addSystemInfo("API Version", "1.0.0").addSystemInfo("Environment", "QA");
		}
		return extent;
	}

	public static String formatMapToTabularString(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();

		sb.append("<style type=\"text/css\">\r\n"
				+ ".tg {border-collapse:collapse;border-spacing:0;border-color:#999;}\r\n"
				+ ".tg td {font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#999;color:#444;background-color:#F7FDFA;}\r\n"
				+ ".tg th {font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#999;color:#444;background-color:#26ADE4;}\r\n"
				+ ".tg tg-3fs7 {font-family:Tahoma, Geneva, sans-serif!important;font-size:1qpx;vertical-align:top}\r\n"
				+ ".tg tg-yw41 {vertical-align:top;}\r\n" + "</style>" + "<table class=\"tg\">"
				+ "<tr><th class=\"tg-yw41\">Column</th><th class=\"tg-yw41\">Value</th></tr>");

		for(Entry<String, String> entry : map.entrySet()) {
			sb.append("<tr><td class=\"tg-yw41\">").append(entry.getKey()).append("</td><td class=\"tg-yw41\">").append(entry.getValue()).append("</td></tr>");
		}
		
		return sb.toString();
	}
}