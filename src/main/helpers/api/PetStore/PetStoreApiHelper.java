package helpers.api.PetStore;

import com.github.javafaker.Faker;
import helpers.logger.LoggerHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import utils.PetNameCounter;

import java.io.File;
import java.io.InputStream;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PetStoreApiHelper extends LoggerHelper {

    private final String apiSchemasPath = "apiSchemas" + File.separator;
    private final JSONObject bodyForNewUser = new JSONObject();
    Faker faker = new Faker(new Locale("en"));
    String usersPath = "user";
    String petsByStatusPath = "/pet/findByStatus";
    int statusCodeExpected;
    long responseTimeExpected;
    Integer[] statusesCodeExpected;

    public void setBaseURI(String url) {
        RestAssured.baseURI = url;
    }

    public void createUser() {

        InputStream userCreationSchema = getClass().getClassLoader().getResourceAsStream(apiSchemasPath + "user-creation-schema.json");
        statusCodeExpected = 200;
        responseTimeExpected = 2500L;

        long id = faker.number().randomNumber();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String userName = firstName + "." + lastName;
        String emailAddress = faker.internet().emailAddress(firstName + lastName);
        String password = faker.internet().password();
        String cellPhone = faker.phoneNumber().cellPhone();

        bodyForNewUser.put("id", id);
        bodyForNewUser.put("username", userName);
        bodyForNewUser.put("firstName", firstName);
        bodyForNewUser.put("lastName", lastName);
        bodyForNewUser.put("email", emailAddress);
        bodyForNewUser.put("password", password);
        bodyForNewUser.put("phone", cellPhone);
        bodyForNewUser.put("userStatus", 0);

        logStep("Creating new user with random data. The following body will be posted:\n" + bodyForNewUser);

        Response response = given()
                .basePath(usersPath)
                .body(bodyForNewUser.toJSONString())
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .post()
                .then()
                .statusCode(statusCodeExpected)
                .time(lessThanOrEqualTo(responseTimeExpected))
                .log().all()
                .body(JsonSchemaValidator.matchesJsonSchema(userCreationSchema))
                .extract().response();

        if (response.getStatusCode() == statusCodeExpected) {
            logInfo("User successfully created");
            logAPIAssertion("Checking status code as " + statusCodeExpected + " and response time less than or equal to " + responseTimeExpected);
        }

        String userMessage = response.jsonPath().getString("message");
        System.out.println("User creation message: " + userMessage);
    }

    public void createUser(PetStoreUser petStoreUser) {

        InputStream userCreationSchema = getClass().getClassLoader().getResourceAsStream(apiSchemasPath + "user-creation-schema.json");
        statusCodeExpected = 200;
        responseTimeExpected = 2500L;

        bodyForNewUser.put("id", petStoreUser.getId());
        bodyForNewUser.put("username", petStoreUser.getUserName());
        bodyForNewUser.put("firstName", petStoreUser.getFirstName());
        bodyForNewUser.put("lastName", petStoreUser.getLastName());
        bodyForNewUser.put("email", petStoreUser.getEmailAddress());
        bodyForNewUser.put("password", petStoreUser.getPassword());
        bodyForNewUser.put("phone", petStoreUser.getCellPhone());
        bodyForNewUser.put("userStatus", 0);

        logStep("Creating new user with specific data. The following body will be posted:\n" + bodyForNewUser);

        Response response = given()
                .basePath(usersPath)
                .body(bodyForNewUser.toJSONString())
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .post()
                .then()
                .statusCode(statusCodeExpected)
                .time(lessThanOrEqualTo(responseTimeExpected))
                .log().all()
                .body(JsonSchemaValidator.matchesJsonSchema(userCreationSchema))
                .extract().response();

        if (response.getStatusCode() == statusCodeExpected) {
            logInfo("User successfully created");
            logAPIAssertion("Checking status code as " + statusCodeExpected + " and response time less than or equal to " + responseTimeExpected);
        }

        String userMessage = response.jsonPath().getString("message");
        System.out.println("User creation message: " + userMessage);
    }

    public void getSpecificUser(String usernameToFind) {
        InputStream foundUserSchema = getClass().getClassLoader().getResourceAsStream(apiSchemasPath + "found-user-schema.json");
        InputStream missingUserSchema = getClass().getClassLoader().getResourceAsStream(apiSchemasPath + "missing-user-schema.json");
        statusesCodeExpected = new Integer[]{200, 404};
        responseTimeExpected = 1900L;

        Response response = given()
                .basePath(usersPath)
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get(usernameToFind)
                .then()
                .statusCode(is(oneOf(statusesCodeExpected)))
                .time(lessThanOrEqualTo(responseTimeExpected))
                .log().all()
                .extract().response();

        // Optionally, handle the response further based on the status code
        if (response.getStatusCode() == 200) {
            // User found, handle accordingly
            assert foundUserSchema != null;
            response.then().body(JsonSchemaValidator.matchesJsonSchema(foundUserSchema));
            System.out.println("User found.");
        } else if (response.getStatusCode() == 404) {
            // User not found, handle accordingly
            assert missingUserSchema != null;
            response.then().body(JsonSchemaValidator.matchesJsonSchema(missingUserSchema));
            System.out.println("User not found.");
        }
    }

    public void createUserAndRetrieve(PetStoreApiHelper api) {

        statusCodeExpected = 200;
        responseTimeExpected = 2500L;

        long id = faker.number().randomNumber();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String userName = firstName + "." + lastName;
        String emailAddress = faker.internet().emailAddress(firstName + lastName);
        String password = faker.internet().password();
        String cellPhone = faker.phoneNumber().cellPhone();

        bodyForNewUser.put("id", id);
        bodyForNewUser.put("username", userName);
        bodyForNewUser.put("firstName", firstName);
        bodyForNewUser.put("lastName", lastName);
        bodyForNewUser.put("email", emailAddress);
        bodyForNewUser.put("password", password);
        bodyForNewUser.put("phone", cellPhone);
        bodyForNewUser.put("userStatus", 0);

        Response response = given()
                .basePath(usersPath)
                .body(bodyForNewUser.toJSONString())
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .post()
                .then()
                .statusCode(statusCodeExpected)
                .time(lessThanOrEqualTo(responseTimeExpected))
                .log().all()
                .extract().response();

        String userMessage = response.jsonPath().getString("message");
        System.out.println("User creation message: " + userMessage);

        api.getSpecificUser(userName);
    }

    public void getPetsByStatusList(String status) {
        responseTimeExpected = 1900L;

        Response response = given()
                .basePath(petsByStatusPath)
                .queryParam("status", status)
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get()
                .then()
                .time(lessThanOrEqualTo(responseTimeExpected))
//                .log().all()
                .extract().response();


        // Check the status code of the response
        int statusCode = response.getStatusCode();

        // Code is always 200, but it should return 400 if status is invalid according to API documentation
        if (statusCode == 200) {
            printPetsByStatus(response, "sold");
        } else {
            System.out.println("Failed to get pets by status. Status code: " + statusCode);
            // Optionally, throw an exception or handle the error accordingly
        }
    }

    public void printPetsByStatus(Response response, String status) {
        List<Map<String, Object>> petsList = new ArrayList<>();

        // Parse the JSON response and extract id and name fields for pets with status 'sold'
        List<Map<String, Object>> pets = JsonPath.from(response.asString()).getList("");
        System.out.println("List of " + status + " pets:");
        for (Map<String, Object> pet : pets) {
            String petStatus = (String) pet.get("status");
            if (petStatus.equals(status)) {
                String id = String.valueOf(pet.get("id"));
                String name = (String) pet.get("name");
                System.out.println("id: " + id + ", name: " + name);
                petsList.add(pet); // Add the pet to the list
            }
        }

        // Instantiate constructor with list to count the names based on status provided
        System.out.println("List of " + status + " pets that share the same name:");
        PetNameCounter petNameCounter = new PetNameCounter(petsList);
        System.out.println(petNameCounter.countPetNames());
    }
}