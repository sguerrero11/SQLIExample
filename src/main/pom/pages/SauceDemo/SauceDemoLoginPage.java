package pom.pages.SauceDemo;

import org.openqa.selenium.By;
import pom.BasePage;
import pom.DefaultPage;

public class SauceDemoLoginPage extends BasePage implements DefaultPage {

    //region locators

    By username_locator = By.cssSelector("[data-test='username']");
    By password_locator = By.cssSelector("[data-test='password']");
    By loginButton_locator = By.cssSelector("[data-test='login-button']");

    //endregion


    //region load
    @SafeVarargs // to supress warnings
    @Override
    public final <T> String getUrl(T... values) {
        return "https://www.saucedemo.com/";
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

    public void performLogin(String username, String password){
        sendKeys(username, username_locator);
        sendKeys(password, password_locator);
        click(loginButton_locator);

    }

    //endregion
}
