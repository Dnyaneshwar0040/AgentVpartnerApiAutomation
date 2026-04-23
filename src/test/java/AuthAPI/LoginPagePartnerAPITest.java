package AuthAPI;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

@Epic("API Testing")
@Feature("Login API")
public class LoginPagePartnerAPITest {

    String url = "https://vpartner.staging.api.indifly.in/vagentlogin/auth/login";

    @Step("Login with PAN: {0} and Password: {1}")
    public Response loginRequest(String pan, String password) {

        String requestBody = "{ \"panCardNumber\":\"" + pan + "\", \"password\":\"" + password + "\" }";

        attachRequest(requestBody);

        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post(url)
                .then()
                .extract().response();

        attachResponse(response.asPrettyString());

        return response;
    }

    @Attachment(value = "Request", type = "application/json")
    public String attachRequest(String request) {
        return request;
    }

    @Attachment(value = "Response", type = "application/json")
    public String attachResponse(String response) {
        return response;
    }

    @Test(description = "Valid Login")
    public void TC01_validLogin() {
        Assert.assertEquals(loginRequest("bmjpt8242f", "Test@123").getStatusCode(), 200);
    }

    @Test(description = "Invalid Password")
    public void TC02_invalidPassword() {
        Assert.assertTrue(loginRequest("bmjpt8242f", "Wrong@123").getStatusCode() >= 400);
    }

    @Test(description = "Invalid PAN")
    public void TC03_invalidPan() {
        Assert.assertTrue(loginRequest("INVALID", "Test@123").getStatusCode() >= 400);
    }

    @Test(description = "Empty PAN")
    public void TC04_emptyPan() {
        Assert.assertTrue(loginRequest("", "Test@123").getStatusCode() >= 400);
    }

    @Test(description = "Empty Password")
    public void TC05_emptyPassword() {
        Assert.assertTrue(loginRequest("bmjpt8242f", "").getStatusCode() >= 400);
    }

    @Test(description = "Both Empty")
    public void TC06_bothEmpty() {
        Assert.assertTrue(loginRequest("", "").getStatusCode() >= 400);
    }

    @Test(description = "Null PAN")
    public void TC07_nullPan() {
        Assert.assertTrue(loginRequest(null, "Test@123").getStatusCode() >= 400);
    }

    @Test(description = "Null Password")
    public void TC08_nullPassword() {
        Assert.assertTrue(loginRequest("bmjpt8242f", null).getStatusCode() >= 400);
    }

    @Test(description = "Special Characters PAN")
    public void TC09_specialCharPan() {
        Assert.assertTrue(loginRequest("@@@###", "Test@123").getStatusCode() >= 400);
    }

    @Test(description = "SQL Injection")
    public void TC10_sqlInjection() {
        Assert.assertTrue(loginRequest("bmjpt8242f' OR '1'='1", "Test@123").getStatusCode() >= 400);
    }

    @Test(description = "Long PAN")
    public void TC11_longPan() {
        Assert.assertTrue(loginRequest("bmjpt8242fxxxxxxxx", "Test@123").getStatusCode() >= 400);
    }

    @Test(description = "Long Password")
    public void TC12_longPassword() {
        Assert.assertTrue(loginRequest("bmjpt8242f", "Test@123xxxxxxxx").getStatusCode() >= 400);
    }

    @Test(description = "Case Sensitivity")
    public void TC13_caseSensitivePan() {
        Assert.assertTrue(loginRequest("BMJPT8242F", "Test@123").getStatusCode() >= 400);
    }

    @Test(description = "Missing Body")
    public void TC14_missingBody() {
        Response res = given()
                .header("Content-Type", "application/json")
                .when()
                .post(url);
        Assert.assertTrue(res.getStatusCode() >= 400);
    }

    @Test(description = "Invalid Content-Type")
    public void TC15_invalidContentType() {
        Response res = given()
                .header("Content-Type", "text/plain")
                .body("invalid")
                .when()
                .post(url);
        Assert.assertTrue(res.getStatusCode() >= 400);
    }
}