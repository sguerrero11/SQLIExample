package pom.pages.HerokuApp;

import helpers.browser.BrowserDriverHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import pom.BasePage;
import pom.DefaultPage;

public class DragAndDropPage extends BasePage implements DefaultPage {
    //region locators
    By title_locator = By.cssSelector("h3"); // "Drag and Drop"
    By squareA_locator = By.id("column-a");
    By squareB_locator = By.id("column-b");
    //endregion

    //region load
    @SafeVarargs
    @Override
    public final <T> String getUrl(T... values) {
        return "https://the-internet.herokuapp.com/drag_and_drop";
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
    public boolean isTitleVisible() {
        return isDisplayed(title_locator);
    }

    public WebElement getSquareA() {
        return findElement(squareA_locator);
    }

    public WebElement getSquareB() {
        return findElement(squareB_locator);
    }

    /**
     * JS-based drag and drop (works on HTML5 DnD)
     */
    private void dragAndDropJs(WebElement source, WebElement target) {
        String script = """
            function createEvent(typeOfEvent) {
                var event = document.createEvent("CustomEvent");
                event.initCustomEvent(typeOfEvent, true, true, null);
                event.dataTransfer = {
                    data: {},
                    setData: function(key, value) {
                        this.data[key] = value;
                    },
                    getData: function(key) {
                        return this.data[key];
                    }
                };
                return event;
            }

            function dispatchEvent(element, event, transferData) {
                if (transferData !== undefined) {
                    event.dataTransfer = transferData;
                }
                if (element.dispatchEvent) {
                    element.dispatchEvent(event);
                } else if (element.fireEvent) {
                    element.fireEvent("on" + event.type, event);
                }
            }

            function simulateHTML5DragAndDrop(element, destination) {
                var dragStartEvent = createEvent('dragstart');
                dispatchEvent(element, dragStartEvent);
                var dropEvent = createEvent('drop');
                dispatchEvent(destination, dropEvent, dragStartEvent.dataTransfer);
                var dragEndEvent = createEvent('dragend');
                dispatchEvent(element, dragEndEvent, dropEvent.dataTransfer);
            }

            simulateHTML5DragAndDrop(arguments[0], arguments[1]);
        """;

        JavascriptExecutor js = (JavascriptExecutor) BrowserDriverHelper.getDriver();
        js.executeScript(script, source, target);
    }

    public void dragSquareAToB() {
        dragAndDropJs(getSquareA(), getSquareB());
    }

    public void dragSquareBToA() {
        dragAndDropJs(getSquareB(), getSquareA());
    }
    //endregion
}
