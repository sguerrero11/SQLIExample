package listeners;

import helpers.assertions.AssertionsListHelper;
import helpers.browser.BrowserDriverHelper;
import helpers.logger.LoggerHelper;
import helpers.yamlReader.YMLHelper;
import org.testng.*;
import org.testng.annotations.AfterMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ProjectListener extends LoggerHelper implements ITestListener, ISuiteListener, IDataProviderListener {

    // region VARIABLES

    String testClassName;
    String folderPath = "reportsForManualQA/";

    // private final Logger log = LoggerFactory.getLogger(ProjectListener.class); // if you don't use extends
    public String screenshotName;
    public String currentTestName;
//    ExtentReports extent = LoggerHelper.getReporterObject();
//    ExtentTest test;

    // endregion

    @Override
    public void onStart(ISuite suite) {
        if (suite.getName().equals("Surefire suite")) {
            // Selenium Configuration
            BrowserDriverHelper.setupBrowser();
        }

        logInfo("Starting suite");
        logInfo("I am in onStart method from " + suite.getName());
        logSeparatorSpaced();
    }

    @Override
    public void onFinish(ISuite suite) {
        // Check if the suite name is "TestSuite"
        if (suite.getName().equals("Surefire suite")) {
            // Selenium Configuration
            BrowserDriverHelper.closeBrowser();
        }

        logInfo("Finishing suite");
        logInfo("I am in onFinish method from " + suite.getName());
        logSeparatorSpaced();
    }

    @Override
    public void beforeDataProviderExecution(IDataProviderMethod iDataProviderMethod, ITestNGMethod iTestNGMethod, ITestContext iTestContext) {
    }

    @Override
    public void afterDataProviderExecution(IDataProviderMethod iDataProviderMethod, ITestNGMethod iTestNGMethod, ITestContext iTestContext) {
    }


    @Override
    public void onTestStart(ITestResult result) {

        currentTestName = result.getMethod().getMethodName();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = currentTestName + "_" + timestamp + "_report.txt";

        // Create folder for Manual QA reports if it doesn't exist
        Path dirPath = Paths.get(folderPath);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String path = System.getProperty("user.dir") + File.separator + folderPath + fileName; // folder needs to exist

        logInfo("@Test: {}", result.getName());
        logInfo("Description: {}", result.getMethod().getDescription());
        logSeparator();

        createReportForQAs(path);
        logStep("Test case name: " + result.getName());
        logStep("Test case description: " + result.getMethod().getDescription());
        logBreak();
        logStep("Steps:");

        //result.setAttribute("WebDriver", this.driver1);

//        //Creating Extent Report
//        test = extent.createTest(currentTestName)
//                .assignAuthor("Santiago Guerrero");

    }

    @Override
    public void onTestSuccess(ITestResult result) {

        String timestamp = new SimpleDateFormat("MMMM d_ yyyy HH-mm-ss").format(new Date());
        screenshotName = timestamp + "_" + result.getName() + "_PASSED";

        logInfo("@Test: {}", result.getName() + " has PASSED");
        logSeparator();
        if (!testClassName.contains("API")) {
            finalizeTest("Test Passed");
        }
        else {
            finalizeTest("Test Passed based on API assertions");
        }

//        takeSS(BrowserDriverHelper.getDriver(), screenshotName);
//        test.log(Status.PASS, "Test successfully passed")
//                .addScreenCaptureFromPath(System.getProperty("user.dir") + File.separator + "screenshots" + File.separator + screenshotName + "_screenshot.png");


    }

    @Override
    public void onTestFailure(ITestResult result) {

        String timestamp = new SimpleDateFormat("MMMM d_ yyyy HH-mm-ss").format(new Date());
        screenshotName = timestamp + "_" + result.getName() + "_FAILED";

        logInfo("@Test: {}", result.getName() + " has FAILED");
        logSeparator();
        if (!testClassName.contains("API")) {
            finalizeTest("Test Failed. The following errors were found: \n\n" + result.getThrowable());
        }
        else {
            finalizeTest("API assertions via Rest Assured have failed: \n\n" + result.getThrowable());
        }

//        takeSS(BrowserDriverHelper.getDriver(), screenshotName);
//        test.log(Status.FAIL, "Test failed: " + result.getThrowable())
//                .addScreenCaptureFromPath(System.getProperty("user.dir") + File.separator + "screenshots" + File.separator + screenshotName + "_screenshot.png")
//                .fail(MediaEntityBuilder.createScreenCaptureFromPath(System.getProperty("user.dir") + File.separator +
//                        "screenshots" + File.separator + screenshotName + "_screenshot.png").build());

    }

    @Override
    public void onTestSkipped(ITestResult result) {

        logInfo("@Test: {}", result.getName() + " was SKIPPED");
        logSeparator();
        finalizeTest("Test Skipped");

        //ExtentReports log operation for skipped tests.
        //getTest().log(Status.SKIP, "Test Skipped");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

        String timestamp = new SimpleDateFormat("MMMM d_ yyyy HH-mm-ss").format(new Date());
        screenshotName = timestamp + "_" + result.getName() + "_FAILED_but_within_success";

        logInfo("@Test: {}", result.getName() + " has FAILED but it is within defined success ratio.");
        logSeparator();
        finalizeTest("@Test \"", result.getName() + "\" has FAILED but it is within defined success ratio (" +
                result.getMethod().getSuccessPercentage() + "). The following errors were found: \n\n" + result.getThrowable());
//        takeSS(BrowserDriverHelper.getDriver(), screenshotName);
//        test.log(Status.FAIL, "Test failed but within success ratio (" +
//                        result.getMethod().getSuccessPercentage() + "). The following errors were found: \n\n" + result.getThrowable())
//                .addScreenCaptureFromPath(System.getProperty("user.dir") + File.separator + "screenshots" + File.separator + screenshotName + "_screenshot.png")
//                .fail(MediaEntityBuilder.createScreenCaptureFromPath(System.getProperty("user.dir") + File.separator +
//                        "screenshots" + File.separator + screenshotName + "_screenshot.png").build());
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        logInfo("@Test: {}", result.getName() + " has FAILED due to timeout");
        logSeparator();
        finalizeTest("@Test: {}", result.getName() + " has FAILED due to timeout");

    }

    @Override
    public void onStart(ITestContext context) {

        // Get the test class name
        testClassName = context.getCurrentXmlTest().getClasses().get(0).getName();

        // Check if the test class name indicates an API test
        if (!testClassName.contains("API")) {
            // Selenium Configuration
            BrowserDriverHelper.setupBrowser();
        }
        else {
            YMLHelper.setProperties();
        }
    }

    @Override
    public void onFinish(ITestContext context) {
//        //Do tier down operations for ExtentReports reporting
//        test.assignCategory(context.getName());
//        extent.flush();

        // Selenium Configuration
        BrowserDriverHelper.closeBrowser();

    }


    @AfterMethod
    public void afterEachIteration() {
        AssertionsListHelper.assertions = new ArrayList<>();
    }
}