package pom.pages;

import helpers.yamlReader.YMLHelper;
import org.apache.commons.io.FileUtils;

import org.openqa.selenium.*;
import pom.BasePage;
import pom.DefaultPage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GooglePage extends BasePage implements DefaultPage {

    //region selectors

    private final By searchField_locator = By.name("q");
    private final By firstResult_locator = By.xpath("(//*[@jscontroller='SC7lYd']/div)[1]/div[1]");
    private final By wikiResult_locator = By.xpath("(//a[contains(@href, 'wikipedia.org')])[1]");
    private final By resultsStats_locator = By.id("result-stats");

    //endregion

    //region load
    @SafeVarargs // to supress warnings
    @Override
    public final <T> String getUrl(T... values) {
        return "https://www.google.com";
    }

    @SafeVarargs
    @Override
    public final <T> void load(T... values) {
        visit(getUrl(values));
    }


    @Override
    public boolean validateField(String element, String attribute, String value) {
        return false;
    }

    //endregion

    //region methods

    /**
     * We perform a query using the submit() method
     * @param word
     */
    public void inputQueryAndSubmit(String word){
        WebElement element = findElement(searchField_locator);
        sendKeys(word, searchField_locator);
        element.submit();
    }

    /**
     * We perform a query using the ENTER key
     * @param word
     */
    public void performASearchQuery(String word) {
        sendKeys(word, Keys.ENTER, searchField_locator);
    }

    public void openFirstWikipediaResultIfAny() throws InterruptedException {
        try {
            // Click on the first Wikipedia result
            scrollToElement(wikiResult_locator);
            logInfo("Clicking on the first Wikipedia result...");
            click(wikiResult_locator);
            logInfo("First Wikipedia result opened successfully");
        } catch (NoSuchElementException e) {
            logError("No Wikipedia results found.");
        }
    }

    /**
     * This method opens the first result after sponsor/news/articles/etc results
     * @throws InterruptedException
     */
    public void openFirstResultIfAny() throws InterruptedException {
        try {
            scrollToElement(firstResult_locator);
            logInfo("There are " + resultsTotalNumber(getText(resultsStats_locator)) + " results." +
                    "\nClicking on the first one...");
            click(firstResult_locator);
            logInfo("First result opened successfully");
        } catch (NoSuchElementException e) {
            logError("No results found.");
            System.out.println("alla");
        }
    }

    public String resultsTotalNumber(String text) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);

        // Find the first match
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null; // No number found
        }
    }

    public int getResultsTotal(){
        return Integer.parseInt(resultsTotalNumber(getText(resultsStats_locator)));

    }

    public int getEarliestYear(String url){
        String earliestYearString = findEarliestYearInPage(url);
        // Use regex to extract the number
        String numberString = earliestYearString.replaceAll("[^\\d]", "");
        // Convert the extracted number string to an integer
        return Integer.parseInt(numberString);
    }

    public boolean isWikipediaLinkPresent(WebDriver driver) {
        // Find the container holding the search results with id 'search'
        WebElement searchResultsContainer = driver.findElement(By.id("search"));

        // Find all search result links within the container
        List<WebElement> searchResultLinks = searchResultsContainer.findElements(By.tagName("a"));

        // Check if any of the links contain Wikipedia in the URL or title and return
        return searchResultLinks.stream()
                .anyMatch(link -> link.getAttribute("href").contains("wikipedia.org") ||
                        link.getAttribute("href").contains("wikipedia") ||
                        link.getAttribute("title").contains("Wikipedia"));
    }


    public void saveScreenshotInsideGooglePackageAs(String fileName) {

        String fileFormat = YMLHelper.getYMLValue(YMLHelper.yamlData, "screenshots.format");
        // Get current package directory
        String packageDir = System.getProperty("user.dir") + "/src/tests/FrontEnd/Google/screenshots";

        System.out.println(packageDir);

        // Convert WebDriver object to TakesScreenshot
        TakesScreenshot screenshot = (TakesScreenshot) getDriver();

        // Capture screenshot as File
        File srcFile = screenshot.getScreenshotAs(OutputType.FILE);

        // Create the directory if it doesn't exist
        Path dirPath = Paths.get(packageDir);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Define the file path
        String filePath = dirPath + File.separator + fileName + fileFormat;

        try {
            // Copy file to destination
            FileUtils.copyFile(srcFile, new File(filePath));
            logInfo("Screenshot saved at: " + filePath);
        } catch (IOException e) {
            logError("Failed to save screenshot: " + e.getMessage());
        }
    }

    //endregion
}