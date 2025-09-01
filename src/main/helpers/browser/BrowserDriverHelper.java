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
import java.util.*;

import static helpers.yamlReader.YMLHelper.*;

/***
 * Helper class to handle Selenium WebDriver.
 */
public abstract class BrowserDriverHelper extends LoggerHelper {
    private static WebDriver driver;
    private static WebDriver browserVNC;
    private static WebDriverManager wdm;
    private static boolean vncEnabled;

    private static String getRandomUserAgent() {
        String[] userAgents = {
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_0) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Safari/605.1.15",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 16_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 Safari/604.1",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:123.0) Gecko/20100101 Firefox/123.0"
        };

        int index = (int) (Math.random() * userAgents.length);
        return userAgents[index];
    }



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
            // 👤 Random User Agent
            options.addArguments("--user-agent=" + getRandomUserAgent());

            // 🕵️‍♂️ Remove automation flags
            options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
            options.setExperimentalOption("useAutomationExtension", false);

            // 🌐 Start with a clean profile
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--remote-allow-origins=*");




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

            // 🚫 Hide 'navigator.webdriver'
            ((ChromeDriver) driver).executeCdpCommand(
                    "Page.addScriptToEvaluateOnNewDocument",
                    Map.of("source",
                            "Object.defineProperty(navigator, 'webdriver', {get: () => undefined});"
                    )
            );

            // 🧹 Clean other JS fingerprints (optional)
            ((ChromeDriver) driver).executeCdpCommand(
                    "Page.addScriptToEvaluateOnNewDocument",
                    Map.of("source",
                            "window.navigator.chrome = { runtime: {} };"
                    )
            );
            ((ChromeDriver) driver).executeCdpCommand(
                    "Page.addScriptToEvaluateOnNewDocument",
                    Map.of("source",
                            "Object.defineProperty(navigator, 'languages', {get: () => ['en-US', 'en']});"
                    )
            );
            ((ChromeDriver) driver).executeCdpCommand(
                    "Page.addScriptToEvaluateOnNewDocument",
                    Map.of("source",
                            "Object.defineProperty(navigator, 'plugins', {get: () => [1, 2, 3, 4, 5]});"
                    )
            );

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
            String userAgent = getRandomUserAgent();
            options.addPreference("general.useragent.override", userAgent);
            options.addPreference("dom.webdriver.enabled", false);
            options.addPreference("useAutomationExtension", false);



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
            String userAgent = getRandomUserAgent();
            options.addArguments("--user-agent=" + userAgent);


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

            ((EdgeDriver) driver).executeCdpCommand(
                    "Page.addScriptToEvaluateOnNewDocument",
                    Map.of("source",
                            "Object.defineProperty(navigator, 'webdriver', { get: () => undefined })")
            );

        } catch (Exception ex) {
            logError("[BrowserDriver/loadEdgeDriver] Error loading Selenium Driver: " + ex.getMessage());
        }
    }

    private static void loadRemoteDriver() throws InterruptedException {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--user-agent=" + getRandomUserAgent());
        // continue with WDM setup...

        String dockerVideosPath = System.getProperty("user.dir") + "/src/tests/videos";;
        wdm = WebDriverManager.chromedriver()
                .browserInDocker()
                .avoidDockerLocalFallback()
//                .browserVersion() // to use a specific version
//                .browserInDockerAndroid() // --> for Chrome Mobile
                .enableRecording() // only if you want to see the recordings // recordings are saved locally
                .dockerRecordingOutput(dockerVideosPath)
                .capabilities(chromeOptions) // passing options e.g. headless
                .enableVnc();
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