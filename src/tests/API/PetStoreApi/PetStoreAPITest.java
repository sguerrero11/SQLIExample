package API.PetStoreApi;

import helpers.api.PetStore.PetStoreApiHelper;
import helpers.api.PetStore.PetStoreUser;
import listeners.ProjectListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ProjectListener.class})
public class PetStoreAPITest {

    private PetStoreApiHelper petStoreApi;

    @BeforeClass
    public void beforeTests() {
        petStoreApi = new PetStoreApiHelper();
        petStoreApi.setBaseURI("https://petstore.swagger.io/v2"); // sets the endpoint
    }

    @Test(description = "Verify a user gets created via HTTP request using random data and status is 200", priority = 1)
    public void createASingleUserWithRandomData() {
        petStoreApi.createUser();
    }

    @Test(description = "Verify a user gets created via HTTP request using specific data and status is 200", priority = 10)
    public void createASingleUserWithSpecificData() {
        PetStoreUser petStoreUser = new PetStoreUser(10913212, "santiago", "guerrero",
                "santiago.guerrero", "s.guerrero@outlook.com", "MyPassword123", "549113033443122");
        petStoreApi.createUser(petStoreUser);
    }

    @Test(description = "Verify a user's data can be obtained via HTTP request and status is 200", priority = 20)
    public void getASingleUser() {
        petStoreApi.getSpecificUser("santiago.guerrero");
    }

    @Test(description = "Verify a user is created and then retrieved via HTTP request and status is 200", priority = 30)
    public void createAndGetAUser() {
        petStoreApi.createUserAndRetrieve(petStoreApi);
    }

    @Test(description = "Get a list of all pets and display sold ones and how many share their names", priority = 40)
    public void getPetsList() {
        String status = "available,pending,sold";
        petStoreApi.getPetsByStatusList(status);
    }
}