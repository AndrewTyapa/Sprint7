package api.client;

import api.models.Courier;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.HttpURLConnection;

import static api.endpoints.Endpoints.BASE_URL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CourierClientTest {

    private Courier courier;

    final private String login = RandomStringUtils.randomAlphabetic(10);
    final private String password = RandomStringUtils.randomAlphabetic(10);
    final private String firstName = RandomStringUtils.randomAlphabetic(10);

    @BeforeClass
    static public void setUpBase() {
        RequestSpecification requestSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .build();

        RestAssured.requestSpecification = requestSpec;
    }

    @Before
    public void setUp() {
        CourierClient courierClient = new CourierClient();
        courier = courierClient.registerCourier();
    }

    @After
    public void afterMethod() {
        CourierClient courierClient = new CourierClient();
        Response response = courierClient.getResponseForRegisterRequest(courier);
        JsonPath jsonPath = new JsonPath(response.asString());
        String userId = jsonPath.getString("id");
        courierClient.deleteCourier(userId);
    }


    @Test
    @DisplayName("Check response for correct login and password")
    public void testResponseForCorrectLoginData() {
        CourierClient courierClient = new CourierClient();
        Response response = courierClient.getResponseForLoginRequest(courier);
        response.then().assertThat().statusCode(HttpURLConnection.HTTP_OK).and().assertThat().body("id", notNullValue());
    }

    @Test
    @DisplayName("Check response for incorrect login")
    public void testResponseForIncorrectLogin() {
        CourierClient courierClient = new CourierClient();
        Response response = courierClient.getResponseForLoginRequest(new Courier("incorrectLogin", courier.getPassword()));
        response.then().assertThat().statusCode(HttpURLConnection.HTTP_NOT_FOUND).and().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Check response for incorrect password")
    public void testResponseForIncorrectPassword() {
        CourierClient courierClient = new CourierClient();
        Response response = courierClient.getResponseForLoginRequest(new Courier(courier.getLogin(), "incorrectPassword"));
        response.then().assertThat().statusCode(HttpURLConnection.HTTP_NOT_FOUND).and().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Check response for incorrect login and password")
    public void testResponseForNonExistsCourier() {
        String randomWord = RandomStringUtils.randomAlphabetic(10);
        Courier courier = new Courier(randomWord, randomWord);
        CourierClient courierClient = new CourierClient();
        Response response = courierClient.getResponseForLoginRequest(courier);
        response.then().assertThat().statusCode(HttpURLConnection.HTTP_NOT_FOUND).and().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Check response for login without login field")
    public void testResponseForAuthWithoutLoginField() {
        CourierClient courierClient = new CourierClient();
        String password = "pass";
        String registerBody = "{\"password\":\"" + password + "\"}";
        Response response = courierClient.getResponseForLoginWithCustomRequest(registerBody);
        response.then().assertThat().statusCode(HttpURLConnection.HTTP_BAD_REQUEST).and().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }


    @Test
    @DisplayName("Check response for create courier duplicate")
    public void testCreateDuplicateCourier() {
        CourierClient courierClient = new CourierClient();
        Courier courier = courierClient.registerCourier();
        Response response = courierClient.getResponseForRegisterRequest(courier);
        response.then()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_CONFLICT)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Check response for create courier with valid data")
    public void testCreateCourierWithValidData() {
        Courier courier = new Courier(login, password, firstName);
        CourierClient courierClient = new CourierClient();
        Response response = courierClient.getResponseForRegisterRequest(courier);
        response.then()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_CREATED)
                .and()
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Check response for login when password field is missing")
    public void testCreateCourierWithoutFillInPassword() {
        String registerBody = "{\"login\":\"" + login + "\","
                + "\"firstName\":\"" + firstName + "\"}";
        CourierClient courierClient = new CourierClient();
        Response response = courierClient.getResponseForRegisterWithCustomBodyRequest(registerBody);
        response.then()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Check response for login when login field is missing")
    public void testCreateCourierWithoutFillInLogin() {
        String registerBody = "{\"password\":\"" + password + "\","
                + "\"firstName\":\"" + firstName + "\"}";
        CourierClient courierClient = new CourierClient();
        Response response = courierClient.getResponseForRegisterWithCustomBodyRequest(registerBody);
        response.then()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}
