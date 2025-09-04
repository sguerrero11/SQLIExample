package FrontEnd.HerokuApp;

import helpers.assertions.AssertionsListHelper;
import listeners.ProjectListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pom.pages.HerokuApp.DragAndDropPage;

@Listeners({ProjectListener.class})

public class DragAndDropTest {

    AssertionsListHelper asserts = new AssertionsListHelper();
    DragAndDropPage page = new DragAndDropPage();

    @Test
    public void testDragAndDrop() {

        page.load();

        page.dragSquareAToB();
        // add assertion here → e.g., check text inside columns
        asserts.isTrue(page.getSquareA().getText().equals("B"), "Square A now contains B");

        page.dragSquareBToA();
        asserts.isTrue(page.getSquareA().getText().equals("A"), "Square A now contains A again");
    }

}
