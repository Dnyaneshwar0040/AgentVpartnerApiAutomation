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

    // 🔹 Common login method
    @Step("Login with PAN: {0} and Password: {1}")
    public Response loginRequest(String pan, String password) {

        String requestBody = "{"
                + "\"panCardNumber\":\"" + pan + "\","
                + "\"password\":\"" + password + "\""
                + "}";

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

    // 🔹 Allure attachments
    @Attachment(value = "Request", type = "application/json")
    public String attachRequest(String request) {
        return request;
    }

    @Attachment(value = "Response", type = "application/json")
    public String attachResponse(String response) {
        return response;
    }

    // 🔹 Common validation methods
    public void validateSuccess(Response res) {
        Assert.assertEquals(res.getStatusCode(), 200);

        String body = res.asString().toLowerCase();
        Assert.assertTrue(
                body.contains("success") ||
                body.contains("token") ||
                body.contains("data"),
                "Expected success response but got: " + body
        );
    }

    public void validateFailure(Response res) {
        Assert.assertEquals(res.getStatusCode(), 200);

        String body = res.asString().toLowerCase();
        Assert.assertTrue(
                body.contains("invalid") ||
                body.contains("fail") ||
                body.contains("error"),
                "Expected failure response but got: " + body
        );
    }

    // ================= TEST CASES =================

    @Test(description = "Valid Login")
    public void TC01_validLogin() {
        validateSuccess(loginRequest("bmjpt8242f", "Test@123"));
    }

    @Test(description = "Invalid Password")
    public void TC02_invalidPassword() {
        validateFailure(loginRequest("bmjpt8242f", "Wrong@123"));
    }

    @Test(description = "Invalid PAN")
    public void TC03_invalidPan() {
        validateFailure(loginRequest("INVALID", "Test@123"));
    }

    @Test(description = "Empty PAN")
    public void TC04_emptyPan() {
        validateFailure(loginRequest("", "Test@123"));
    }

    @Test(description = "Empty Password")
    public void TC05_emptyPassword() {
        validateFailure(loginRequest("bmjpt8242f", ""));
    }

    @Test(description = "Both Empty")
    public void TC06_bothEmpty() {
        validateFailure(loginRequest("", ""));
    }

    @Test(description = "Null PAN")
    public void TC07_nullPan() {
        validateFailure(loginRequest(null, "Test@123"));
    }

    @Test(description = "Null Password")
    public void TC08_nullPassword() {
        validateFailure(loginRequest("bmjpt8242f", null));
    }

    @Test(description = "Special Characters PAN")
    public void TC09_specialCharPan() {
        validateFailure(loginRequest("@@@###", "Test@123"));
    }

    @Test(description = "SQL Injection")
    public void TC10_sqlInjection() {
        validateFailure(loginRequest("bmjpt8242f' OR '1'='1", "Test@123"));
    }

    @Test(description = "Long PAN")
    public void TC11_longPan() {
        validateFailure(loginRequest("bmjpt8242fxxxxxxxx", "Test@123"));
    }

    @Test(description = "Long Password")
    public void TC12_longPassword() {
        validateFailure(loginRequest("bmjpt8242f", "Test@123xxxxxxxx"));
    }

    @Test(description = "Case Sensitivity")
    public void TC13_caseSensitivePan() {
        validateFailure(loginRequest("BMJPT8242F", "Test@123"));
    }

    @Test(description = "Missing Body")
    public void TC14_missingBody() {
        Response res = given()
                .header("Content-Type", "application/json")
                .when()
                .post(url);

        validateFailure(res);
    }

    @Test(description = "Invalid Content-Type")
    public void TC15_invalidContentType() {
        Response res = given()
                .header("Content-Type", "text/plain")
                .body("invalid")
                .when()
                .post(url);

        validateFailure(res);
    }
}