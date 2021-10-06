# APITestAutomation
Sample application and Test Automation framework to showcase API test strategy

# How to run the tests

Step 1 - Please setup Java environment and maven in your system and include them in PATH environment variable. Please refer:
<ul>
	<li>Java Setup - https://www.java.com/en/download/help/windows_manual_download.html</li>
	<li>Maven Setup - https://maven.apache.org/guides/getting-started/windows-prerequisites.html</li>
</ul>

Step 2 - After Java and Maven setup, simply navigate to project root in explorer and run "run.bat" (windows) 

Step 3 - Please open the html report in <b><Project root>\Reports folder</b> for results. There are 2 tabs in the report, one is a dashboard and other one is detail test case wise report. Old reports will be archived in <b><Project root>\Archive</b> folder

# Configuartion
One can configure API environment using src\test\resources\config.properties file. Below is the details of config:

<ol>
	<li><u>Environment</u> - Environment to be used (Dev / QA / Prod / DR etc.)</li>
	<li><u>BaseURL</u> - Environment specific baseURL to be used e.g. DevBaseURL, QABaseURL etc.</li>
	<li><u>Headers</u> - Request headers can be passed. Multiple headers can be separated by ~ e.g. headers=Content-Type:application/json~~Accept:application-json</li>
	<li><u>AuthType</u> - Authentication Type - Valid values can be None / Basic / OAuth / Token etc.</li>
	<li><u>Credential</u> - Credential as per the AuthType needs to be passed. Password should be encrypted by base64 encryption</li>
	<li><u>ProxyEnabled</u> - True if proxy is enabled, false otherwise</li>
	<li><u>ProxyHost</u> - ProxyHost, only applicable if proxy is enabled</li>
	<li><u>ProxyPort</u> - ProxyPort, only applicable if proxy is enabled</li>
</ol>

# Utility Classes for API Automation
Following classes can be referred:

<ol>
	<li><u>APIProfile</u> - Used to set environment of API using config file or using methods</li>
	<li><u>RestAssuredUtils</u> - Utility class to provide methods to hit API using multiple options, parse response and validate it</li>
	<li><u>APIVerb</u> - Enum to represent API verb - GET / POST / PUT / DELETE / PATCH</li>
	<li><u>AuthType</u> - Enum to represent API authentication - None / Basic / Token / OAuth are supported</li>
	<li><u>Environment</u> - Enum to represent API environment - DEV / QA / UAT / PROD / DR</li>
	<li><u>ResponseObject</u> - Enum to represent attributes of Response - BODY / STATUS CODE / HEADERS / SESSION ID etc.</li>
</ol>

Other utility classes are for reporting, file operations etc.