package FrontEnd.SauceDemo;

import helpers.assertions.AssertionsListHelper;
import listeners.ProjectListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pom.pages.SauceDemo.SauceDemoInventoryPage;
import pom.pages.SauceDemo.SauceDemoLoginPage;

@Listeners({ProjectListener.class})
public class LoginTest {

    SauceDemoLoginPage sauceDemoPage = new SauceDemoLoginPage();
    AssertionsListHelper asserts = new AssertionsListHelper();
    @Test(description = "Verify user can login successfully", priority = 1, groups = {"Login"})
    public void loginSuccess(){

        sauceDemoPage.load();
        sauceDemoPage.performLogin("standard_user","secret_sauce");
        SauceDemoInventoryPage sauceDemoInventoryPage = new SauceDemoInventoryPage();

        asserts.isTrue(sauceDemoInventoryPage.isTitleVisible(),"Verify Products title is visible");
    }
}