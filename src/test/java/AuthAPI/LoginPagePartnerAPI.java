package AuthAPI;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

@Epic("API Testing")
@Feature("Login API")
public class LoginPagePartnerAPI {

    String baseUrl = "https://vpartner.staging.api.indifly.in/vagentlogin/auth/login";

    // Common method
    public Response loginRequest(String pan, String password) {
        return given()
                .header("Content-Type", "application/json")
                .body("{\"panCardNumber\":\"" + pan + "\",\"password\":\"" + password + "\"}")
                .when()
                .post(baseUrl)
                .then()
                .extract().response();
    }

    @Test(description = "Valid Login")
    public void TC01_validLogin() {
        Response res = loginRequest("bmjpt8242f", "Test@123");
        Assert.assertEquals(res.getStatusCode(), 200);
    }

    @Test(description = "Invalid Password")
    public void TC02_invalidPassword() {
        Response res = loginRequest("bmjpt8242f", "Wrong@123");
        Assert.assertNotEquals(res.getStatusCode(), 200);
    }

    @Test(description = "Invalid PAN")
    public void TC03_invalidPan() {
        Response res = loginRequest("INVALID123", "Test@123");
        Assert.assertNotEquals(res.getStatusCode(), 200);
    }

    @Test(description = "Empty PAN")
    public void TC04_emptyPan() {
        Response res = loginRequest("", "Test@123");
        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    @Test(description = "Empty Password")
    public void TC05_emptyPassword() {
        Response res = loginRequest("bmjpt8242f", "");
        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    @Test(description = "Both Empty")
    public void TC06_bothEmpty() {
        Response res = loginRequest("", "");
        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    @Test(description = "Null Password")
    public void TC07_nullPassword() {
        Response res = loginRequest("bmjpt8242f", null);
        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    @Test(description = "Null PAN")
    public void TC08_nullPan() {
        Response res = loginRequest(null, "Test@123");
        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    @Test(description = "Special Characters PAN")
    public void TC09_specialCharPan() {
        Response res = loginRequest("@@@###", "Test@123");
        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    @Test(description = "SQL Injection Attempt")
    public void TC10_sqlInjection() {
        Response res = loginRequest("bmjpt8242f' OR '1'='1", "Test@123");
        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    @Test(description = "Long PAN Input")
    public void TC11_longPan() {
        Response res = loginRequest("bmjpt8242fxxxxxxxxxxxx", "Test@123");
        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    @Test(description = "Long Password Input")
    public void TC12_longPassword() {
        Response res = loginRequest("bmjpt8242f", "Test@123xxxxxxxxxxxx");
        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    @Test(description = "Case Sensitivity Check")
    public void TC13_caseSensitivePan() {
        Response res = loginRequest("BMJPT8242F", "Test@123");
        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    @Test(description = "Missing Body")
    public void TC14_missingBody() {
        Response res = given()
                .header("Content-Type", "application/json")
                .when()
                .post(baseUrl)
                .then()
                .extract().response();

        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    @Test(description = "Invalid Content Type")
    public void TC15_invalidContentType() {
        Response res = given()
                .header("Content-Type", "text/plain")
                .body("invalid")
                .when()
                .post(baseUrl)
                .then()
                .extract().response();

        Assert.assertTrue(res.getStatusCode() >= 400);
    }
}