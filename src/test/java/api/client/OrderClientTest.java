package api.client;

import api.models.Order;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.HttpURLConnection;

import static api.endpoints.Endpoints.BASE_URL;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderClientTest {
    private final String[] color;
    private int track;

    public OrderClientTest(String[] color) {
        this.color = color;
    }

    @BeforeClass
    static public void setUpBase() {
        RequestSpecification requestSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .build();

        RestAssured.requestSpecification = requestSpec;
    }

    @Parameterized.Parameters
    public static Object[][] getOrderData() {
        return new Object[][]{
                {new String[]{"BLACK", "GREY"}},
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{}}
        };
    }

    @After
    public void afterMethod() {
        OrdersClient orderClient = new OrdersClient();
        orderClient.cancelOrder(track);
    }

    @Test
    @DisplayName("Check response when data is valid")
    public void testCheckResponseCreateOrderWithValidData() {
        OrdersClient orderClient = new OrdersClient();
        Order order = new Order(color);
        order.setUpFieldsForRequest();
        Response response = orderClient.getResponseForOrders(order);
        response.then()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_CREATED)
                .and()
                .body("track", notNullValue());
        String responseString = response.asString();
        JsonPath jsonPath = new JsonPath(responseString);
        track = jsonPath.getInt("track");
    }

    @Test
    @DisplayName("Check response for get orders")
    public void testCheckResponseForGetOrderList() {
        OrdersClient orderClient = new OrdersClient();
        Response response = orderClient.getResponseForOrders();
        response.then()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .and()
                .body("orders", notNullValue());
    }
}
