package FrontEnd.Google;

import helpers.assertions.AssertionsListHelper;
import listeners.ProjectListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pom.pages.GooglePage;

@Listeners({ProjectListener.class})
public class GoogleSearchTest {

    private final GooglePage googlePage = new GooglePage();

    AssertionsListHelper asserts = new AssertionsListHelper();

    @Test(description = "Test case #1: Search for 'automation', open wikipedia result," +
            " check the year for first automatic process and take a SS", priority = 1)
    public void openGoogleSiteAndSearchForSpecificWord() throws InterruptedException {
        // region ARRANGE
        String targetWord = "Automation";
        // endregion

        // region ACT
        googlePage.load();
        googlePage.inputQueryAndSubmit(targetWord);

        // Store related variables for final assertion
        int resultsTotal = googlePage.getResultsTotal();
        boolean wikiResultFound = googlePage.isWikipediaLinkPresent(googlePage.getDriver());

        googlePage.openFirstWikipediaResultIfAny();
        String currentURL = googlePage.getCurrentUrl();
        // Store related variables for final assertion
        int earliestYear = googlePage.getEarliestYear(currentURL);
        googlePage.saveScreenshotInsideGooglePackageAs("wiki-result");
        // endregion

        // region ASSERT
        asserts.isTrue(resultsTotal>0,"Verify results for target word are found");
        asserts.isTrue(wikiResultFound, "Verify Wikipedia link was present within the results");
        asserts.isTrue(googlePage.isPageLoaded(), "Verify Wikipedia page loads correctly");
        asserts.equals(earliestYear,300, "Verify the first automatic process was done around 300 BC");
        // endregion
    }
}