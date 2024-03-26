# **Automated Testing Framework for SQLI README**

**Description:**

This repository contains an automated testing framework designed for testing web applications and APIs. The framework includes test cases for the Pet Store API and Google search functionality. It utilizes Java, Selenium WebDriver, TestNG and Rest Assured (among other dependencies) for test automation, along with various helper classes (browser management, logging, yml files reader, etc.).

**Repository Structure:**

- logs: Contains log files.
- reportsForManualQA: Contain custom reports depicting test name, steps, assertions and outcome.
- config: Contains config files.
- helpers: Contains various helpers (browser, logger, etc.)
- pom/pages: Page object model classes for interacting with web pages, along with base files.
- API/PetStoreApi: Contains test cases for the Pet Store API.
- FrontEnd/Google: Contains test cases for Google search functionality.

**Test Cases:**

- GoogleSearchTest.java: Test cases for Google search functionality.
- PetStoreAPITest.java: Test cases for the Pet Store API, including user creation, retrieval, and pet listing.

**Helper Classes:**

- BrowserDriverHelper.java: Helper class for managing Selenium WebDriver instances, including setup and configuration for different browsers.
- LoggerHelper.java: Helper class for logging test execution details, including warnings, errors, and assertions.
- PetStoreApiHelper.java: Helper class for interacting with Pet Store API.
- AssertionsListHelper.java: Helper class for managing different assertions.
- YMLHelper.java: Helper class for reading and writing yml files.

**Usage:**
1) Clone the repository to your local machine.
2) Import the project into your preferred Java IDE.
3) Do a `mvn clean install`.
3) Configure the test suite and test cases as needed.
4) Run the test suite using TestNG.

**Most Relevant Dependencies:**

1) Java 11+
2) Selenium WebDriver
3) TestNG
4) Log4j
5) Rest Assured
6) JSoup
7) JSON Simple
8) Faker
9) Bonigarcia's WebDriverManager

Contributors:

[Santiago Guerrero]