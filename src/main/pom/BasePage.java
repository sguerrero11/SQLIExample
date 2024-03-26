package pom;

import helpers.browser.BrowserDriverHelper;
import helpers.logger.LoggerHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BasePage extends LoggerHelper {

    private By uiNotifyDynamicMultiplePopupMessageLocator(String text) {
        return By.xpath(String.format("//div[@class='ui-pnotify-text']/ul/li[contains(text(), '%s')]", text));
    }

    private By uiNotifyDynamicSinglePopupMessageLocator(String text) {
        return By.xpath(String.format("//div[@class='ui-pnotify-text' and contains(text(), '%s')]", text));
    }

    private By uiErrorDynamicMessageLocator(String text) {
        return By.xpath(String.format("//div[@id='errorsBox']//label[contains(text(), '%s')]", text));
    }

    /*
     ***********************************
     ************ GETS *****************
     ***********************************
     */
    // region GETS

    /***
     * Get the plain text of element. (innerHTML)
     * @param element [WebElement] -> Target element
     * @return [String] Text of the element.
     */
    public String getText(WebElement element) {
        return element.getText();
    }

    /***
     * Get the plain text of element. (innerHTML)
     * @param locator [By] -> Locator of the target element.
     * @return [String] -> Text of the element.
     */
    public String getText(By locator) {
//        LoggerHelp.logAction("getText",locator, locator.getText());
        return BrowserDriverHelper.getDriver().findElement(locator).getText();
    }

    public String getValue(By locator) {
        return BrowserDriverHelper.getDriver().findElement(locator).getAttribute("value");
    }

    public String getAttribute(By locator, String attribute) {
        return BrowserDriverHelper.getDriver().findElement(locator).getAttribute(attribute);
    }

    /***
     * Return all the values from specific dropdown
     * @param locator [By]
     * @return [List<String>]
     */
    public List<String> getDropdownValues(By locator) {
        Select dropdownObj = new Select(BrowserDriverHelper.getDriver().findElement(locator));
        List<String> dropdownValues = new ArrayList<>();

        for (WebElement option : dropdownObj.getOptions()) {
            dropdownValues.add(option.getText());
        }

        return dropdownValues;
    }

    /***
     * Return all the values from specific dropdown
     * @param locator [By]
     * @return [List<String>]
     */
    public List<String> getDropdownOptionValues(By locator) {
        Select dropdownObj = new Select(BrowserDriverHelper.getDriver().findElement(locator));
        List<String> dropdownOptionValues = new ArrayList<>();

        for (WebElement option : dropdownObj.getOptions()) {
            dropdownOptionValues.add(option.getAttribute("value"));
        }

        return dropdownOptionValues;
    }

    public String getDropdownSelectedValue(By locator) {
        Select dropdownObj = new Select(BrowserDriverHelper.getDriver().findElement(locator));
        return getText(dropdownObj.getFirstSelectedOption());
    }


    public Boolean checkFormLabels(By formLabelLocator, List<String> expectedLabels) {
        List<WebElement> labels = BrowserDriverHelper.getDriver().findElements(formLabelLocator);

        boolean areEqual = true;
        for (WebElement label : labels) {
            String formattedLabel = label.getText().replaceAll("\\s+", " ");
            areEqual = areEqual && expectedLabels.contains(formattedLabel);
            if (!areEqual) {
                LoggerHelper.logInfo("[BasePage/checkFormLabels]: Expected label doesn't exist in current form: " + formattedLabel);
            }
        }
        return areEqual;
    }

    public Boolean checkDatatableTitles(By datatableLocator, ArrayList<String> expectedTitles) {
        WebElement datatable = BrowserDriverHelper.getDriver().findElement(datatableLocator);
        List<WebElement> titles = datatable.findElements(By.xpath(".//thead//th[text()]"));

        boolean areEqual = true;
        int i = 0;
        for (WebElement title : titles) {
            areEqual = areEqual && title.getText().equals(expectedTitles.get(i));
            if (!areEqual)
                LoggerHelper.logWarning("[BasePage/checkDatatableTitles]: Expected: " + expectedTitles.get(i) + ", Label: " + title.getText());
            i++;
        }

        return areEqual;
    }

    public String getTextElementBesideAnotherByText(String firstElementTag, String secondElementTag, String textFirstElement) {
        String xpath = "//" + firstElementTag + "[contains(.,'" + textFirstElement + "')]/following-sibling::" + secondElementTag + "[1]";
        WebElement firstElement = BrowserDriverHelper.getDriver().findElement(By.xpath(xpath));

        return firstElement.getText();
    }


    public String getCurrentUrl() {
        return BrowserDriverHelper.getDriver().getCurrentUrl();
    }
    // endregion

    /*
     ***********************************
     ************* ACTIONS *************
     ***********************************
     */
    // region ACTIONS

    /**
     * Send ENTER KEY along with a specific text in a search field
     * @param text
     * @param locator
     */

    public void sendEnterKey(String text, By locator) {
        BrowserDriverHelper.getDriver().findElement(locator).sendKeys(text,Keys.ENTER);
    }

    /***
     * Set Text to element.
     * @param inputText [String] -> Value to write.
     * @param locator [By] -> Locator of the target element.
     */

    public void sendKeys(String inputText, By locator) {
        BrowserDriverHelper.getDriver().findElement(locator).sendKeys(inputText);
        logStep("Fill in field identified as " + returnAttributeBasedOnExistence(locator) + " with value: " + inputText);
    }

    /**
     *
     * @param inputText [String] -> Value to write.
     * @param key [Keys] -> Key from keyboard. e.g. ENTER
     * @param locator [By] -> Locator of the target element.
     */
    public void sendKeys(String inputText, Keys key, By locator) {
        BrowserDriverHelper.getDriver().findElement(locator).sendKeys(inputText,key);
        logStep("Fill in field identified as " + returnAttributeBasedOnExistence(locator) + " with value: " + inputText);
    }

    /**
     *
     * @param inputText
     * @param locator
     * @param driver local Chromedriver
     */
    public void sendKeys(String inputText, By locator, ChromeDriver driver) {
        driver.findElement(locator).sendKeys(inputText);
        logStep("Fill in field identified as " + returnAttributeBasedOnExistence(locator) + " with value: " + inputText);
    }



    /***
     * Clear the element and set Text.
     * @param inputText [String] -> Value to write.
     * @param locator [By] -> Locator of the target element.
     * @param clearElement [Boolean] -> To clear the element first.
     */
    public void sendKeys(String inputText, By locator, boolean clearElement, boolean tabAtEnd) {
        WebElement input = BrowserDriverHelper.getDriver().findElement(locator);

        if (clearElement) {
            input.clear();
        }

        input.sendKeys(inputText);

        if (tabAtEnd) {
            input.sendKeys(Keys.TAB);
        }
    }

    /***
     * Clear element input
     * @param locator [String]
     */
    public void clearElement(By locator) {
        BrowserDriverHelper.getDriver().findElement(locator).clear();
    }

    /***
     * Click element
     * @param locator [By] -> The locator of the target element.
     */
    public void click(By locator) {
        logStep("Click on button identified as " + returnAttributeBasedOnExistence(locator));
        BrowserDriverHelper.getDriver().findElement(locator).click();
    }

    public void click(By locator, ChromeDriver driver) {
        logStep("Click on button identified as " + returnAttributeBasedOnExistence(locator));
        driver.findElement(locator).click();
    }

//    /***
//     * Click element by JavaScript
//     * @param xpath [String] -> Id of the element
//     */
//    public void clickByIdWithJs(String xpath) {
//        JavascriptExecutor js = BrowserDriverHelper.getDriver();
//        js.executeScript(xpath + ".click();");
//    }

    /***
     * Click element
     * @param locator [By] -> The locator of the target element.
     */
    public void clickIfDisplay(By locator) {
        if (isDisplayed(locator)) {
            BrowserDriverHelper.getDriver().findElement(locator).click();
        }
    }

    /***
     * Click in the checkbox if is not already checked
     * @param locator [By]
     */
    public void clickIfNotChecked(By locator) {
        if (isDisplayed(locator) && !elementHasAttribute(BrowserDriverHelper.getDriver().findElement(locator), "checked")) {
            click(locator);
        }
    }

    /***
     * Select value from dropdown by Visible Text.
     * @param targetValue [String] -> Option's Visible text, if is empty, the first value will be selected.
     * @param locator [By] -> Locator of the target element.
     */
    public void selectValueByTextFromDropdown(String targetValue, By locator) {
        Select dropdownObj = new Select(BrowserDriverHelper.getDriver().findElement(locator));
        waitUntilSelectOptionsPopulated(dropdownObj);
        if (targetValue.isEmpty()) {
            dropdownObj.selectByIndex(1);
        } else {
            dropdownObj.selectByVisibleText(targetValue);
        }
    }

    public void selectValueByTextFromDropdown(String targetValue, WebElement element) {
        Select dropdownObj = new Select(element);
        waitUntilSelectOptionsPopulated(dropdownObj);
        if (targetValue.isEmpty()) {
            dropdownObj.selectByIndex(1);
        } else {
            dropdownObj.selectByVisibleText(targetValue);
        }
    }

    /***
     * Select value from dropdown by Visible Text.
     * @param targetValue [String] -> Option's Visible text, if is empty, the first value will be selected.
     * @param locator [By] -> Locator of the target element.
     */
    public void selectValueByValueFromDropdown(String targetValue, By locator) {
        Select dropdownObj = new Select(BrowserDriverHelper.getDriver().findElement(locator));
        waitUntilSelectOptionsPopulated(dropdownObj);
        if (targetValue.isEmpty()) {
            dropdownObj.selectByIndex(1);
        } else {
            dropdownObj.selectByValue(targetValue);
        }
    }

    /***
     * Check if the dropdown has specific value
     * @param targetValue [String]
     * @param locator [By]
     * @param contains [boolean]
     * @return [boolean]
     */
    public boolean dropdownHasValue(String targetValue, By locator, boolean contains) {
        Select dropdownObj = new Select(BrowserDriverHelper.getDriver().findElement(locator));
        for (WebElement option : dropdownObj.getOptions()) {
            if (contains) {
                if (option.getAttribute("value").contains(targetValue)) {
                    return true;
                }
            } else {
                if (option.getAttribute("value").equals(targetValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    /***
     * Check if the dropdown has specific description
     * @param targetValue [String]
     * @param locator [By]
     * @param contains [boolean]
     * @return [boolean]
     */
    public boolean dropdownHasText(String targetValue, By locator, boolean contains) {
        Select dropdownObj = new Select(BrowserDriverHelper.getDriver().findElement(locator));
        for (WebElement option : dropdownObj.getOptions()) {
            if (contains) {
                if (option.getText().contains(targetValue)) {
                    return true;
                }
            } else {
                if (option.getText().equals(targetValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    /***
     * Select value from dropdown by Visible Text.
     * @param targetValue [String] -> Option's Visible text, if it is empty, the first value will be selected.
     * @param dropdownId [By] -> Locator of the target element.
     */
    public void selectValueByTextFromSpecialDropdown(String targetValue, String dropdownId) throws InterruptedException {
        var dropdownSpanLocator = By.xpath("//select[@id='" + dropdownId + "']/following-sibling::span");
        click(dropdownSpanLocator);

        var dropdownInputLocator = By.xpath("//input[@class='select2-search__field']");
        waitForElementVisible(dropdownInputLocator);
        sendKeys(targetValue, dropdownInputLocator);
        Thread.sleep(1500);
        sendKeys(String.valueOf(Keys.ENTER), dropdownInputLocator);
        //ToDo -> Acá si hay otros resultados falla, ya que solo da click al primero que aparece. Es un primer approach
    }

    public void mouseHover(By locator) {
        WebElement element = BrowserDriverHelper.getDriver().findElement(locator);
        Actions builder = new Actions(BrowserDriverHelper.getDriver());
        builder.moveToElement(element).perform();
    }

    // endregion

    /*
     ***********************************
     ************** OTHERS *************
     ***********************************
     */


    public static String findEarliestYearInPage(String pageURL) {
        try {
            // Parse the Wikipedia page
            Document doc = Jsoup.connect(pageURL).get();

            // Search for the word "earliest" in the entire page
            Elements elementsContainingEarliest = doc.getElementsContainingOwnText("earliest");
            for (Element element : elementsContainingEarliest) {
                // Once "earliest" is found, search for the year in the paragraph
                String paragraph = element.text();
                // Search for years in various formats: BC, AD, and normal Roman years
                Pattern yearPattern = Pattern.compile("\\b(\\d{3,4}(?: BC|AD)?|\\(\\d{3,4}–\\d{3,4} (?:BC|AD)\\))\\b");
                Matcher yearMatcher = yearPattern.matcher(paragraph);
                if (yearMatcher.find()) {
                    return "Earliest year found around " + yearMatcher.group(1); // Return the year found
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "No year was found or there was an error."; // Return message if no year is found or if there is an error
    }

    public boolean isAlertPresent() {
        boolean foundAlert;
        var wait = new WebDriverWait(BrowserDriverHelper.getDriver(), Duration.ofSeconds(0)  /*timeout in seconds*/);
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            foundAlert = true;
        } catch (TimeoutException eTO) {
            foundAlert = false;
        }
        return foundAlert;
    }

    public String getJavascriptAlertText() {
        var alert = BrowserDriverHelper.getDriver().switchTo().alert();
        String alertMessage = BrowserDriverHelper.getDriver().switchTo().alert().getText();
        alert.accept();
        return alertMessage;
    }

    /***
     * Find element in the driver view by his locator.
     * @param locator [By] -> Locator of the target element.
     * @return [WebElement] -> The element found
     */
    public WebElement findElement(By locator) {
        return BrowserDriverHelper.getDriver().findElement(locator);
    }


    /***
     * Find elements in the driver view by the locator.
     * @param locator [By] -> Locator of the target elements.
     * @return [List<WebElement>] -> The elements found
     */
    public List<WebElement> findElements(By locator) {
        return BrowserDriverHelper.getDriver().findElements(locator);
    }

    /***
     * Wait until dropdown values are fully loaded.
     * @param select [WebElement] -> Dropdown target
     */
    public void waitUntilSelectOptionsPopulated(final Select select) {
        new FluentWait<WebDriver>(BrowserDriverHelper.getDriver()).withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(Exception.class)
                .until(driver -> (select.getOptions().size() >= 1));

    }

    /**
     * Wait until the field in the table is filled in with data
     * @param locator --> the field that needs to be completed
     * We'll check every 2 seconds if the field is completed, and after 10 seconds, we'll throw an exception
     */
    public void waitUntilFieldIsPopulated(final By locator) {
        new FluentWait<WebDriver>(BrowserDriverHelper.getDriver())
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofSeconds(2))
                .until(driver -> (driver.findElement(locator).getAttribute("value") != ""));
//                .until(driver -> (getValue(locator) != "")); // same as above

    }

    public void waitForElementAttributeEqualsString(WebElement element, String attribute, String expectedString) {
        WebDriverWait wait = new WebDriverWait(BrowserDriverHelper.getDriver(), Duration.ofSeconds(5));

        wait.until(ExpectedConditions.attributeToBe(element, attribute, expectedString));
    }

    public void waitForElementToExists(By element) {
        WebDriverWait wait = new WebDriverWait(BrowserDriverHelper.getDriver(), Duration.ofSeconds(5));

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(element, 0));
    }

    /***
     * Wait that the page is fully loaded via JavaScript
     */
    private void waitForPageLoad() {
        new FluentWait<WebDriver>(BrowserDriverHelper.getDriver()).withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(10))
                .ignoring(Exception.class)
                .until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
    }

    public void waitForElementVisible(By locator) {
        new FluentWait<WebDriver>(BrowserDriverHelper.getDriver()).withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(Exception.class)
                .until(driver -> findElement(locator).isDisplayed());
    }


    public void waitForElementGone(By locator) {
        new FluentWait<WebDriver>(BrowserDriverHelper.getDriver()).withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(Exception.class)
                .until(driver -> !isDisplayed(locator));
    }

    public void waitForElementQtyIs(By locator, Integer qty) {

        new FluentWait<WebDriver>(BrowserDriverHelper.getDriver()).withTimeout(Duration.ofSeconds(180))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(Exception.class)
                .until(driver -> {
                    List<WebElement> elements = BrowserDriverHelper.getDriver().findElements(locator);
//                    return elements.size() == qty; // this line is the same as below
                    if (elements.size() == qty) {
                        return true;
                    } else {
                        return false;
                    }
                });
    }

    /***
     * Check if the element is display (visible and interactive) in the view.
     * @param locator [By] -> The locator of the target element.
     * @return [Boolean] -> If the element is display or not.
     */
    public Boolean isDisplayed(By locator) {
        try {
            return BrowserDriverHelper.getDriver().findElement(locator).isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    /***
     * Check if the element is disabled (visible and interactive) in the view.
     * @param locator [By] -> The locator of the target element.
     * @return [Boolean] -> If the element is display or not.
     */
    public Boolean isDisabled(By locator) {
        try {
            waitForPageLoad();
            return !BrowserDriverHelper.getDriver().findElement(locator).isEnabled();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            LoggerHelper.logInfo("[BasePage/isDisabled]: The element with locator: " + locator.toString() + " was not found.");
            return false;
        }
    }

    /***
     * Go to url
     * @param url [String]
     */
    public void visit(String url) {
        BrowserDriverHelper.getDriver().get(url);
        logStep("Navigate to URL: " + url);
    }

    /***
     * Check if Dynamic popup is visible based on message
     * @param text [String] -> Popup message
     * @return [Boolean]
     */
    public Boolean isPopupMessageVisible(String text) {
        By locator = uiNotifyDynamicMultiplePopupMessageLocator(text);
        if (!isDisplayed(locator)) {
            locator = uiNotifyDynamicSinglePopupMessageLocator(text);

            return isDisplayed(locator);
        } else {
            return true;
        }
    }

    /***
     * Check if Dynamic Error Message is visible based on text
     * @param text [String] -> Error message
     * @return [Boolean]
     */
    public Boolean isErrorMessageVisible(String text) {
        By locator = uiErrorDynamicMessageLocator(text);

        return isDisplayed(locator);
    }

    /***
     * Filter by expected value using the search input of the table.
     * @param searchInputLocator [By]
     * @param expectedValue [String]
     */
    public void filterValueInTable(By searchInputLocator, String expectedValue) throws InterruptedException {
        sendKeys(expectedValue, searchInputLocator, true, false);
        findElement(searchInputLocator).sendKeys(Keys.ENTER);
        Thread.sleep(2500);
    }

    /***
     * Find value in table (in specific column)
     * @param colNameLocator [By] -> Locator of the target column to check
     * @param expectedValue [String] -> Searched Value
     * @return [Boolean] -> If the value exists in the table (column)
     */
    public boolean verifyValueInTable(By colNameLocator, String expectedValue) {
        boolean result = false;

        for (WebElement singleRowEl : findElements(colNameLocator)) {
            if (singleRowEl.getText().equals(expectedValue)) {
                result = true;
                break;
            }
        }

        return result;
    }

    /***
     * Find value in table (in specific column)
     * @param colNameLocator [By] -> Locator of the target column to check
     * @param expectedValue [String] -> Searched Value
     * @param contains [boolean] -> Searched Value
     * @return [Boolean] -> If the value exists in the table (column)
     */
    public boolean verifyValueInTable(By colNameLocator, String expectedValue, boolean contains) {
        boolean result = false;

        for (WebElement singleRowEl : findElements(colNameLocator)) {
            if (contains ? singleRowEl.getText().contains(expectedValue) : singleRowEl.getText().equals(expectedValue)) {
                result = true;
                break;
            }
        }

        return result;
    }

    /***
     * Find button in table and click it based on the targetValue in the same row.
     * @param colNameLocator [By] -> Locator of the target column to check
     * @param targetValue [String] -> Searched Value
     * @param btnLocator [By] -> Locator of the button in the same row of colNameLocator.
     */
    public void clickBtnInTable(By colNameLocator, String targetValue, By btnLocator) throws InterruptedException {
        int index = 0;
        List<WebElement> btnsToClick = findElements(btnLocator);
        List<WebElement> users = findElements(colNameLocator);

        for (WebElement singleRowEl : users) {
            if (singleRowEl.getText().equals(targetValue)) {
                if (!btnsToClick.get(index).isDisplayed()) {
                    scrollToElement(btnsToClick.get(index));
                }

                btnsToClick.get(index).click();
                break;
            }
            index++;
        }
    }

    /***
     * Check target value based on condition value. First find the row which match with conditionalValue, then compare targetValue in the another column.
     * @param conditionValue [String] -> Pivot value
     * @param targetValue [String] -> Target Value
     * @param conditionLocator [By] -> Pivot column
     * @param targetLocator [By] -> Target column
     * @return [Boolean]
     */
    public boolean verifyDependencyValue(String conditionValue, String targetValue, By conditionLocator, By targetLocator) {
        int index = 0;
        List<WebElement> conditionalValues = findElements(targetLocator);

        for (WebElement singleRowEl : findElements(conditionLocator)) {
            if (singleRowEl.getText().equals(conditionValue)) {
                return conditionalValues.get(index).getText().trim().equals(targetValue);
            }
            index++;
        }
        return false;
    }

    /***
     * Upload file to input type file. Not work in any other uploader type element.
     * By default wait for alert-success class
     * @param path [String] -> Path to the file (absolute)
     * @param locator [By] -> Locator of the input with type file
     */
    public void uploadFile(String path, By locator, By progressBarLocator) {
        uploadFile(path, locator, progressBarLocator, "alert-success");
    }

    /***
     * Upload file to input type file. Not work in any other uploader type element.
     * @param path [String] -> Path to the file (absolute)
     * @param locator [By] -> Locator of the input with type file
     * @param className [String] -> The class to check in progress bar
     */
    public void uploadFile(String path, By locator, By progressBarLocator, String className) {
        BrowserDriverHelper.getDriver().findElement(locator).sendKeys(path);
        if (progressBarLocator != null) {
            waitUntilElementHasClass(BrowserDriverHelper.getDriver().findElement(progressBarLocator), className);
        }
    }

    public void uploadFile(String path, WebElement elementInput, WebElement elementProgressBar, String className) {
        elementInput.sendKeys(path);
        if (elementProgressBar != null) {
            waitUntilElementHasClass(elementProgressBar, className);
        }
    }

    /***
     * Wait until element has specific class.
     * @param element [WebElement] -> Target element
     */
    private void waitUntilElementHasClass(final WebElement element, String className) {
        new FluentWait<WebDriver>(BrowserDriverHelper.getDriver()).withTimeout(Duration.ofSeconds(20))
                .pollingEvery(Duration.ofMillis(15))
                .ignoring(Exception.class)
                .until(driver -> (elementHasClass(element, className)));
    }

    /***
     * Check if element has specific class
     * @param element [WebElement] -> Target Element
     * @param targetClass [String] -> Class to find
     * @return [Boolean]
     */
    public boolean elementHasClass(WebElement element, String targetClass) {
        return Arrays.asList(element.getAttribute("class").split(" ")).contains(targetClass);
    }

    /***
     * Check if element has specific attribute
     * @param element [WebElement] -> Target Element
     * @param targetAttribute [String] -> Attribute to find
     * @return [Boolean]
     */
    public boolean elementHasAttribute(WebElement element, String targetAttribute) {
        return element.getAttribute(targetAttribute) != null;
    }

    public boolean elementHasAttribute(By locator, String targetAttribute) {
        return elementHasAttribute(BrowserDriverHelper.getDriver().findElement(locator), targetAttribute);
    }

    /***
     * Check if the html validation is present on element, also check if the value if correct.
     * For example: if the attribute is required validate that exists, but if the attribute if pattern, validate that
     * exists and also the patter (by value parameter)
     * @param attribute [String]
     * @param value [String]
     * @param contains [boolean]
     * @return [boolean]
     */
    public boolean verifyHTMLValidation(By locator, String attribute, String value, boolean contains) {
        waitForPageLoad();
        boolean result;
        WebElement element = findElement(locator);
        result = elementHasAttribute(element, attribute);
        if (result && !value.isEmpty()) {
            if (contains) {
                result = element.getAttribute(attribute).contains(value);
            } else {
                result = element.getAttribute(attribute).equals(value);
            }
        }

        return result;
    }

    /***
     * Scroll to Top of the page
     */
    public void goToTop() {
        ((JavascriptExecutor) BrowserDriverHelper.getDriver()).executeScript("window.scrollTo(0, 0);");
    }

    /***
     * Scroll to Bottom of the page
     */
    public void goToBottom() {
        ((JavascriptExecutor) BrowserDriverHelper.getDriver()).executeScript("window.scrollTo(0,document.body.scrollHeight);");
    }

    /***
     * Scroll to specific Element
     * @param locator [By] -> Target
     */
    public void scrollToElement(By locator) throws InterruptedException {
        Coordinates cor = ((Locatable) findElement(locator)).getCoordinates();

        cor.inViewPort();
        Thread.sleep(700);
    }

    /***
     * Scroll to specific Element
     * @param element [WebElement] -> Target
     */
    public void scrollToElement(WebElement element) throws InterruptedException {
        Coordinates cor = ((Locatable) element).getCoordinates();

        cor.inViewPort();
        Thread.sleep(700);
    }

    /***
     * Return the current driver
     * @return [WebDriver]
     */
    public WebDriver getDriver() {
        return BrowserDriverHelper.getDriver();
    }

    /***
     * Close the Browser Driver.
     */
    public void closeDriver() {
        if (BrowserDriverHelper.getDriver() != null) {
            BrowserDriverHelper.getDriver().quit();
        } else {
            LoggerHelper.logWarning("[BasePage]: The Browser Driver is already closed.");
        }
    }

    public Boolean exists(By locator) {
        return !BrowserDriverHelper.getDriver().findElements(locator).isEmpty();
    }

    public void switchToTabWithTitle(String titlePart) {
        // We get all tabs' identifiers (tab)
        ArrayList<String> tabs = new ArrayList<>(BrowserDriverHelper.getDriver().getWindowHandles());

        // We traverse all tabs
        for (String tab : tabs) {
            // We switch to each tab
            BrowserDriverHelper.getDriver().switchTo().window(tab);

            // We verify if the tab title contains the titlePart
            if (BrowserDriverHelper.getDriver().getTitle().contains(titlePart)) {
                // If so, functions ends
                return;
            }
        }

        // If we reach this point, nothing was found
        throw new IllegalArgumentException("No tab with title containing '" + titlePart + "' was found.");
    }

    public String returnAttributeBasedOnExistence(By locator) {
        // Find the element
        WebElement element = BrowserDriverHelper.getDriver().findElement(locator);
        // Save all the attributes of the element
        Map<String, String> attributes = new HashMap<>();
        for (String attribute : element.getAttribute("outerHTML").split("\\s")) {
            int indexOfEquals = attribute.indexOf('=');
            if (indexOfEquals != -1) {
                String attributeName = attribute.substring(0, indexOfEquals);
                String attributeValue = element.getAttribute(attributeName);
                attributes.put(attributeName, attributeValue);
            }
        }
        // Analyze the attributes and return a string based on each case
        String result;
        if (attributes.containsKey("text")) {
            result = "\"" + attributes.get("text") + "\" (attribute: text)";
        } else if (attributes.containsKey("name")) {
            result = "\"" + attributes.get("name") + "\" (attribute: name)";
        } else if (attributes.containsKey("data-test")) {
            result = "\"" + attributes.get("data-test") + "\" (attribute: data-test)";
        } else if (attributes.containsKey("id")) {
            result = "\"" + attributes.get("id") + "\" (attribute: id)";
        } else if (attributes.containsKey("data-action")) {
            result = "\"" + attributes.get("data-action") + "\" (attribute: data-action)";
        }
        else {
            result = "\"" + locator + "\" (locator)";
        }
        return result;
    }

    /*
     ***********************************
     ************ ASSERTS *****************
     ***********************************
     */
    // region ASSERTS

    public boolean isPageLoaded() {
        return ((JavascriptExecutor) BrowserDriverHelper.getDriver()).executeScript("return document.readyState").equals("complete");
    }

    // endregion
}