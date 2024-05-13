package helpers.browser;

import helpers.logger.LoggerHelper;
import helpers.yamlReader.YMLHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static helpers.yamlReader.YMLHelper.*;

/***
 * Helper class to handle Selenium WebDriver.
 */
public abstract class BrowserDriverHelper extends LoggerHelper {
    private static WebDriver driver;
    private static WebDriver browserVNC;
    private static WebDriverManager wdm;
    private static boolean vncEnabled;


    /**
     * Load the current driver based on config file
     */
    public static void setupBrowser() {
//        closeBrowser(); // Do we need this?

        try {
            // Load the YML reader object
            YMLHelper.setProperties();

            if (browserDefault != null) {
                loadBrowser(browserDefault);
                if (forceMaximize) {
                    driver.manage().window().maximize();
                }
            } else {
                logError("Define a browser in config.yaml file");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadBrowser(String browserDefault) throws InterruptedException {
        switch (browserDefault) {
            case "chrome":
                loadChromeDriver();
                break;
            case "firefox":
                loadFFDriver();
                break;
            case "edge":
                loadEdgeDriver();
                break;
            case "remote":
                vncEnabled = Boolean.TRUE.equals(YMLHelper.getYMLValue(YMLHelper.yamlData, "browser.seeVNC"));
                loadRemoteDriver();
                break;
            default:
                logError("Choose a valid browser");
                break;
        }
    }

    /**
     * Load and Instantiate Chrome driver to the current one.
     */

    private static void loadChromeDriver() {

        try {
            ChromeOptions options = new ChromeOptions();

            if (withMobileEmulation) {
                Map<String, Object> mobileEmulation = new HashMap<>();
                mobileEmulation.put("deviceName", "iPhone X"); // Set the desired device name for mobile emulation.
                options.setExperimentalOption("mobileEmulation", mobileEmulation);
            }

            if (runHeadless) {
                options.addArguments("--headless"); // Enable headless mode
//              options.addArguments("--disable-gpu"); // Disable GPU usage in headless mode
            }

            logInfo("[BrowserDriver/loadChromeDriver] Loading local driver");
            driver = new ChromeDriver(options);
        } catch (Exception ex) {
            logError("[BrowserDriver/loadChromeDriver] Error loading Selenium Driver: " + ex.getMessage());
        }
    }

    /***
     * Load and Instantiate FF driver to the current one.
     */
    private static void loadFFDriver() {
        try {
            FirefoxOptions options = new FirefoxOptions();

            if (withMobileEmulation) {
                Map<String, Object> mobileEmulation = new HashMap<>();
                mobileEmulation.put("deviceName", "iPhone X"); // Set the desired device name for mobile emulation.
                options.addPreference("devtools.responsiveUI.presets", "{'iPhone X': {'width': 375, 'height': 812," +
                        " 'deviceScaleFactor': 3}}"); // Set specific device properties.
            }

            if (runHeadless) {
                options.addArguments("--headless"); // Enable headless mode
//              options.addArguments("--disable-gpu"); // Disable GPU usage in headless mode
            }

            logInfo("[BrowserDriver/loadFireFoxDriver] Loading local driver");
            driver = new FirefoxDriver(options);
        } catch (Exception ex) {
            logError("[BrowserDriver/loadFireFoxDriver] Error loading Selenium Driver: " + ex.getMessage());
        }
    }

    /**
     * Load and Instantiate Edge driver to the current one.
     */
    private static void loadEdgeDriver() {
        try {
            EdgeOptions options = new EdgeOptions();

            if (withMobileEmulation) {
                Map<String, String> mobileEmulation = new HashMap<>();
                mobileEmulation.put("deviceName", "iPhone X"); // Set the desired device name for mobile emulation.
                options.setExperimentalOption("mobileEmulation", mobileEmulation);
            }

            if (runHeadless) {
                options.addArguments("--headless"); // Enable headless mode
//              options.addArguments("--disable-gpu"); // Disable GPU usage in headless mode
            }

            logInfo("[BrowserDriver/loadEdgeDriver] Loading local driver");
            driver = new EdgeDriver(options);
        } catch (Exception ex) {
            logError("[BrowserDriver/loadEdgeDriver] Error loading Selenium Driver: " + ex.getMessage());
        }
    }

    private static void loadRemoteDriver() throws InterruptedException {
        wdm = WebDriverManager.chromedriver()
                .browserInDocker()
                .avoidDockerLocalFallback()
//                .browserVersion()
//                .browserInDockerAndroid() // --> Chrome Mobile
                .enableVnc()
        .enableRecording(); // only if you want to see the recordings, update ";" if uncommented // recordings as saved locally
        driver = wdm.create();


        // Verify URL for remote session
        if (vncEnabled) {
            URL url = wdm.getDockerNoVncUrl();
            assert url != null : "URL is null";
            asserts.isNotNull(url, "Verify if URL is not null");
            browserVNC = new ChromeDriver();
            browserVNC.get(String.valueOf(url));

            // Add wait to see test being run
            Thread.sleep(2000);
        }
    }


    public static WebDriver getDriver() {
        return driver;
    }

    /***
     * Close the current tab and goes to the first
     */
    public static void closeActiveTab() {
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));
        driver.close();
        driver.switchTo().window(tabs.get(0));
    }

    /***
     * Close the Browser Driver
     */
    public static void closeBrowser() {

        if ((tearDownEnabled) && (!runHeadless)) {
            if (driver != null) {
                if (browserDefault.equals("remote")) {
                    wdm.quit();
                    browserVNC.quit();
                } else {
                    driver.quit();
                }
                logInfo("Your automated browser has been closed."); // Update to TestLogger
            }
        } else {
            if(runHeadless){
               logInfo("Thank you for running headless!");
            }
            else {
                logInfo("Your automated browser is still open. Close it after you're done");
            }

        }
    }
}