package AuthAPI;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Epic("API Testing")
@Feature("Login API")
public class LoginPagePartnerAPITest {

    String url = "https://vpartner.staging.api.indifly.in/vagentlogin/auth/login";

    // ================= COMMON METHOD =================

    @Step("Login with PAN: {0} and Password: {1}")
    public Response loginRequest(String pan, String password) {

        Map<String, Object> body = new HashMap<>();
        body.put("IP", "27.107.46.10");
        body.put("browser", "chrome");
        body.put("device", "desktop");
        body.put("latitude", "18.553855349376658");
        body.put("longitude", "73.94797503719077");
        body.put("os", "Windows");
        body.put("contactNumber", null);
        body.put("panCardNumber", pan);
        body.put("password", password);

        attachRequest(body.toString());

        Response response = given()
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .body(body)
                .when()
                .post(url)
                .then()
                .extract().response();

        attachResponse(response.asPrettyString());

        return response;
    }

    // ================= ALLURE ATTACHMENTS =================

    @Attachment(value = "Request", type = "application/json")
    public String attachRequest(String request) {
        return request;
    }

    @Attachment(value = "Response", type = "application/json")
    public String attachResponse(String response) {
        return response;
    }

    // ================= VALIDATIONS =================

    public void validateSuccess(Response res) {
        Assert.assertEquals(res.getStatusCode(), 200);

        String body = res.asString().toLowerCase();

        Assert.assertTrue(
                body.contains("success") ||
                body.contains("token") ||
                body.contains("data"),
                "Expected SUCCESS but got: " + body
        );
    }

    public void validateFailure(Response res) {
        Assert.assertEquals(res.getStatusCode(), 200);

        String body = res.asString().toLowerCase();

        Assert.assertTrue(
                body.contains("invalid") ||
                body.contains("error") ||
                body.contains("fail"),
                "Expected FAILURE but got: " + body
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