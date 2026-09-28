package api.client;

import io.qameta.allure.Step;
import io.qameta.allure.internal.shadowed.jackson.annotation.JsonInclude;
import io.restassured.response.Response;
import api.endpoints.Endpoints;
import static io.restassured.RestAssured.given;

public class OrdersClient {

    private final static String ORDER_PATH = Endpoints.ORDER_PATH;

    private final static String ORDER_CANCEL = Endpoints.ORDER_CANCEL;

    @Step("Get response for orders")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Response getResponseForOrders(Object body) {
        return given().header("Content-type", "application/json").and().body(body).when().post(ORDER_PATH);
    }

    @Step("Get response for orders")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Response getResponseForOrders() {
        return given().header("Content-type", "application/json").and().when().get(ORDER_PATH);
    }

    @Step("Cancel order")
    public void cancelOrder(int track) {
        String cancelBody = "{\"track\":" + track + "}";
        given()
                .body(cancelBody)
                .when()
                .put(ORDER_CANCEL);
    }
}