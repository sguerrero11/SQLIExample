package pom.pages.SauceDemo;

import org.openqa.selenium.By;
import pom.BasePage;
import pom.DefaultPage;

public class SauceDemoInventoryPage extends BasePage implements DefaultPage {

    //region locators
    By title_locator = By.cssSelector("[data-test='title'");

    //endregion

    //region load
    @SafeVarargs // to supress warnings
    @Override
    public final <T> String getUrl(T... values) {
        return "https://www.saucedemo.com/inventory.html";
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

    public boolean isTitleVisible(){
        return isDisplayed(title_locator);
    }

    //endregion
}
