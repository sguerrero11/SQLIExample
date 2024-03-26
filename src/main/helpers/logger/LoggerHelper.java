package helpers.logger;

import helpers.assertions.AssertionsListHelper;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static helpers.assertions.AssertionsListHelper.assertions;


/***
 * Helper Class to Handle logs in execution time.
 */
public abstract class LoggerHelper {

    private final static Logger _logger = LoggerFactory.getLogger(LoggerHelper.class);

    //Using BufferedWriter (uncomment all required lines). This has better performance
    public static PrintWriter writer;

    // This variable is needed for each class that has a @Test and an assertion
    public static AssertionsListHelper asserts = new AssertionsListHelper();

    // Using OutputStream + OutputStreamWriter (uncomment all required lines)
//    public static OutputStream outputStream;
//    public static OutputStreamWriter reportWriter;


    /***
     * Write a warning message in stdout console.
     * @param message [String]
     */
    public static void logWarning(String message) {
        _logger.warn(message);
    }

    /**
     * @param message [String]
     */
    public static void logTrace(String message) {
        _logger.trace(message);
    }


    /***
     * Write an error message in stdout console.
     * @param message [String]
     */
    public static void logError(String message) {
        _logger.error(message);
    }

    public static void logError(String message, String extendedMessage) {
        _logger.error(message, extendedMessage);
    }

    public static void logError(Throwable errorMessage) {
        _logger.error(String.valueOf(errorMessage));
    }

    /***
     * Write an info message in stdout console.
     * @param message [String]
     */
    public static void logInfo(String message) {
        _logger.info(message);
    }

    public static void logInfo(Integer number) {
        _logger.info(String.valueOf(number));
    }

    public static void logInfo(Double number) {
        _logger.info(String.valueOf(number));
    }

    public static void logInfo(String message, String extendedMessage) {
        _logger.info(message, extendedMessage);
    }

    public static void logInfo(String message, String eMess1, String eMess2, String eMess3) {
        _logger.info(message, eMess1, eMess2, eMess3);
    }

    public static void logInfo(String message, List extendedMessage) {
        _logger.info(message, extendedMessage);
    }

    public static void logSeparator() {
        logInfo("######################################################################################");
    }

    public static void logSeparatorSpaced() {
        logInfo("# # # # # # # # # # # # # # # # # # # # # # # # # # # #");
    }


    // region Reports for QAs

    public void createReportForQAs(String path) {

        try {
            // One way of doing it (using BufferedWriter, better performance)
            writer = new PrintWriter(new File(path));

            // Second way of doing it
//            outputStream = new FileOutputStream(path);
//            reportWriter = new OutputStreamWriter(outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logBreak() {
        //            reportWriter.write("\n");
        writer.write("\n");

    }

    public void logStep(String step) {
        String timestamp = new SimpleDateFormat("MMMM d, yyyy HH:mm:ss").format(new Date());
        String formattedStep = "[" + timestamp + "] " + step;
//        logInfo(formattedStep); // optional if you want to see it in the console as well
        //            reportWriter.write(formattedStep + "\n");
        writer.write(formattedStep + "\n");
    }

    public void logAPIAssertion(String validation) {
        logBreak();
        logStep("Assertions for API:");
        logStep(validation);
    }
    public void logUIAssertion(String validation) {
        if (validation != null && !validation.isEmpty()) {
            logBreak();
            logStep("Assertions for UI:");
            logStep(validation);
        }
    }

    public void logUIAssertion(List<String> validation) {
        // Check if there are UI assertions to log
        if (validation != null && !validation.isEmpty()) {
            logBreak();
            logStep("Assertions for UI:"); // Log a header for UI assertions
            for (String assertion : validation) {
                logStep(assertion); // Log each UI assertion
            }
        }
    }

    public void logStep(String step, String extendedMessage) {
        String timestamp = new SimpleDateFormat("MMMM d, yyyy HH:mm:ss").format(new Date());
        String formattedStep = "[" + timestamp + "] " + step + extendedMessage;
//        logInfo(formattedStep); // optional if you want to see it in the console as well
        //            reportWriter.write(formattedStep + "\n");
        writer.write(formattedStep + "\n");
    }

    public static void takeSS(WebDriver driver, String fileName) {
        // Take screenshot and save to a file
        File screenshotSourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String destinationFile = System.getProperty("user.dir") + File.separator + "screenshots/" + fileName + "_screenshot.png";
        try {
            FileUtils.copyFile(screenshotSourceFile, new File(destinationFile));
            logInfo("Screenshot saved successfully.");

        } catch (IOException e) {
            logInfo("Failed to save screenshot: " + e.getMessage());
        }
    }

    public static void takePartialSS(WebElement element, String fileName) {
        // Take screenshot and save to a file
        File screenshotSourceFile = ((TakesScreenshot) element).getScreenshotAs(OutputType.FILE);
        String destinationFile = System.getProperty("user.dir") + File.separator + "screenshots/" + fileName + "_screenshot.png";
        try {
            FileUtils.copyFile(screenshotSourceFile, new File(destinationFile));
//            System.out.println("Screenshot saved successfully.");
            logInfo("Screenshot saved successfully.");

        } catch (IOException e) {
//            System.out.println("Failed to save screenshot: " + e.getMessage());
            logInfo("Failed to save screenshot: " + e.getMessage());
        }
    }

    public void finalizeTest(String outcome) {
        logUIAssertion(assertions);
        logBreak();
        logStep("Outcome:");
        logStep(outcome);
//            reportWriter.close();
//            writer.flush(); // optional
        writer.close();
    }

    public void finalizeTest(String outcome, String extendedMessage) {
        logUIAssertion(assertions);
        logBreak();
        logStep("Outcome:");
        logStep(outcome + extendedMessage);
//            reportWriter.close();
//            writer.flush(); // optional
        writer.close();
    }

    // endregion


    // region Reports for squad (Extent Reports)


//    public static ExtentReports getReporterObject() {
//
//        // ExtentReports, ExtentSparkReporter
//        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String path = System.getProperty("user.dir") + File.separator + "reportsForTeam" + File.separator + timestamp + "_team_report.html";
//        ExtentReports extent = new ExtentReports();
//        ExtentSparkReporter reporter = new ExtentSparkReporter(path);
//
//
//        reporter.config().setReportName("Automation results");
//        reporter.config().setDocumentTitle("Automation test results");
//        reporter.config().setTheme(Theme.DARK);
//        reporter.config().setEncoding("utf-8");
//
//        extent.attachReporter(reporter);
//        extent.setSystemInfo("Operating System", "Windows 11");
//        return extent;
//
//    }


    // endregion


    // Since ProjectListener extends from LoggerHelper, this after method will be applied for each @Test
    @AfterMethod
    public void afterEachIteration() {
        AssertionsListHelper.softAssert = new SoftAssert(); // We clean the soft assert object after each iteration
        assertions = new ArrayList<>(); // We clean the assertions list after each iteration
    }

}
