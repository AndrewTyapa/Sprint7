package api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import api.models.Courier;
import org.apache.commons.lang3.RandomStringUtils;
import api.endpoints.Endpoints;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;

public class CourierClient {

    private final static String COURIER_PATH = Endpoints.COURIER_PATH;
    private final static String COURIER_LOGIN_PATH = Endpoints.COURIER_LOGIN_PATH;

    @Step("Get response for login request")
    public Response getResponseForLoginRequest(Object body) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post(COURIER_LOGIN_PATH);
    }

    @Step("Get response for register request")
    public Response getResponseForRegisterRequest(Object body) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post(COURIER_PATH);
    }

    @Step("Get response for register request")
    public Response getResponseForRegisterWithCustomBodyRequest(String body) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post(COURIER_PATH);
    }

    @Step("Get response for delete request")
    public void deleteCourier(String userId) {
        delete(COURIER_PATH + userId);
    }

    @Step("Get response for login with custom body")
    public Response getResponseForLoginWithCustomRequest(String registerBody) {
        return given()
                .body(registerBody)
                .when()
                .post(COURIER_LOGIN_PATH);
    }

    @Step("Register courier")
    public Courier registerCourier() {
        String login = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        String firstName = RandomStringUtils.randomAlphabetic(10);
        String registerRequestBody = "{\"login\":\"" + login + "\","
                + "\"password\":\"" + password + "\","
                + "\"firstName\":\"" + firstName + "\"}";
        Response response = getResponseForRegisterWithCustomBodyRequest(registerRequestBody);
        Courier courier = new Courier();
        if (response.statusCode() == 201) {
            courier.setLogin(login);
            courier.setPassword(password);
        }
        return courier;
    }
}