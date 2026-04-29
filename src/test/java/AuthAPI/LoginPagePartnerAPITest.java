package AuthAPI;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Epic("API Testing")
@Feature("Login + OTP Flow")
public class LoginPagePartnerAPITest {

    String loginUrl = "https://vpartner.dev.api.indifly.in/vagentlogin/auth/login";
    String otpUrl = "https://vpartner.dev.api.indifly.in/vagentlogin/auth/validate/loginOtp";

    // ================= LOGIN API =================
    @Step("Login API")
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

        Response res = given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(loginUrl);

        attachResponse(res.asPrettyString());
        return res;
    }

    // ================= OTP API =================
    @Step("Validate OTP API")
    public Response validateOtp(String token, String otp) {

        Map<String, Object> body = new HashMap<>();
        body.put("otpVerificationToken", token);
        body.put("otp", otp);

        attachRequest(body.toString());

        Response res = given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(otpUrl);

        attachResponse(res.asPrettyString());
        return res;
    }

    // ================= ALLURE =================
    @Attachment(value = "Request", type = "application/json")
    public String attachRequest(String request) {
        return request;
    }

    @Attachment(value = "Response", type = "application/json")
    public String attachResponse(String response) {
        return response;
    }

    // ================= VALIDATIONS =================
    public void success(Response res) {
        Assert.assertEquals(res.getStatusCode(), 200);
        Assert.assertTrue(res.asString().toLowerCase().contains("success"));
    }

    public void failure(Response res) {
        Assert.assertEquals(res.getStatusCode(), 200);
        Assert.assertTrue(res.asString().toLowerCase().contains("invalid")
                || res.asString().toLowerCase().contains("error")
                || res.asString().toLowerCase().contains("fail"));
    }

    // ================= TEST CASES =================

    // 1. Valid Login
    @Test
    public void TC01_validLogin() {
        success(loginRequest("bmjpt8242f", "Test@123"));
    }

    // 2. Invalid Password
    @Test
    public void TC02_invalidPassword() {
        failure(loginRequest("bmjpt8242f", "Wrong@123"));
    }

    // 3. Invalid PAN
    @Test
    public void TC03_invalidPan() {
        failure(loginRequest("INVALID", "Test@123"));
    }

    // 4. Empty PAN
    @Test
    public void TC04_emptyPan() {
        failure(loginRequest("", "Test@123"));
    }

    // 5. Empty Password
    @Test
    public void TC05_emptyPassword() {
        failure(loginRequest("bmjpt8242f", ""));
    }

    // 6. Both Empty
    @Test
    public void TC06_bothEmpty() {
        failure(loginRequest("", ""));
    }

    // 7. Null PAN
    @Test
    public void TC07_nullPan() {
        failure(loginRequest(null, "Test@123"));
    }

    // 8. Null Password
    @Test
    public void TC08_nullPassword() {
        failure(loginRequest("bmjpt8242f", null));
    }

    // 9. Special Characters
    @Test
    public void TC09_specialChar() {
        failure(loginRequest("@@@###", "Test@123"));
    }

    // 10. SQL Injection
    @Test
    public void TC10_sqlInjection() {
        failure(loginRequest("bmjpt8242f' OR '1'='1", "Test@123"));
    }

    // 11. Long PAN
    @Test
    public void TC11_longPan() {
        failure(loginRequest("bmjpt8242fxxxxxxxx", "Test@123"));
    }

    // 12. Long Password
    @Test
    public void TC12_longPassword() {
        failure(loginRequest("bmjpt8242f", "Test@123xxxxxxxx"));
    }

    // 13. Case Sensitivity
    @Test
    public void TC13_caseSensitive() {
        success(loginRequest("BMJPT8242F", "Test@123"));
    }

    // 14. Missing Body
    @Test
    public void TC14_missingBody() {
        Response res = given().post(loginUrl);
        failure(res);
    }

    // 15. Invalid Content-Type
    @Test
    public void TC15_invalidContentType() {
        Response res = given()
                .header("Content-Type", "text/plain")
                .body("invalid")
                .post(loginUrl);
        failure(res);
    }

    // ================= OTP FLOW =================

    // 16. Full Flow (Login → OTP → Validate)
    @Test
    public void TC16_loginAndValidateOtp() {

        Response loginRes = loginRequest("bmjpt8242f", "Test@123");
        success(loginRes);

        String token = loginRes.jsonPath().getString("body.data.otpVerificationToken");
        String otp = loginRes.jsonPath().getString("body.data.otp");

        Response otpRes = validateOtp(token, otp);
        success(otpRes);
    }

    // 17. Invalid OTP
    @Test
    public void TC17_invalidOtp() {

        Response loginRes = loginRequest("bmjpt8242f", "Test@123");

        String token = loginRes.jsonPath().getString("body.data.otpVerificationToken");

        Response otpRes = validateOtp(token, "000000");
        failure(otpRes);
    }
    
    @Test
    public void TC18_emptyOtp() {
        Response loginRes = loginRequest("bmjpt8242f", "Test@123");
        String token = loginRes.jsonPath().getString("body.data.otpVerificationToken");

        failure(validateOtp(token, ""));
    }
    
    @Test
    public void TC19_nullOtp() {
        Response loginRes = loginRequest("bmjpt8242f", "Test@123");
        String token = loginRes.jsonPath().getString("body.data.otpVerificationToken");

        failure(validateOtp(token, null));
    }
    @Test
    public void TC20_wrongToken() {
        Response loginRes = loginRequest("bmjpt8242f", "Test@123");
        String otp = loginRes.jsonPath().getString("body.data.otp");

        failure(validateOtp("invalid-token", otp));
    }
    @Test
    public void TC21_emptyToken() {
        Response loginRes = loginRequest("bmjpt8242f", "Test@123");
        String otp = loginRes.jsonPath().getString("body.data.otp");

        failure(validateOtp("", otp));
    }
    @Test
    public void TC22_nullToken() {
        Response loginRes = loginRequest("bmjpt8242f", "Test@123");
        String otp = loginRes.jsonPath().getString("body.data.otp");

        failure(validateOtp(null, otp));
    }
    @Test
    public void TC23_alphaOtp() {
        Response loginRes = loginRequest("bmjpt8242f", "Test@123");
        String token = loginRes.jsonPath().getString("body.data.otpVerificationToken");

        failure(validateOtp(token, "ABC123"));
    }
    @Test
    public void TC24_specialCharOtp() {
        Response loginRes = loginRequest("bmjpt8242f", "Test@123");
        String token = loginRes.jsonPath().getString("body.data.otpVerificationToken");

        failure(validateOtp(token, "@@@###"));
    }
    @Test
    public void TC25_longOtp() {
        Response loginRes = loginRequest("bmjpt8242f", "Test@123");
        String token = loginRes.jsonPath().getString("body.data.otpVerificationToken");

        failure(validateOtp(token, "123456789"));
    }
    @Test
    public void TC26_shortOtp() {
        Response loginRes = loginRequest("bmjpt8242f", "Test@123");
        String token = loginRes.jsonPath().getString("body.data.otpVerificationToken");

        failure(validateOtp(token, "123"));
    }
    @Test
    public void TC27_reuseOtp() {
        Response loginRes = loginRequest("bmjpt8242f", "Test@123");

        String token = loginRes.jsonPath().getString("body.data.otpVerificationToken");
        String otp = loginRes.jsonPath().getString("body.data.otp");

        validateOtp(token, otp); // first time

        Response second = validateOtp(token, otp); // reuse
        failure(second);
    }
    @Test
    public void TC28_expiredOtp() throws InterruptedException {
        Response loginRes = loginRequest("bmjpt8242f", "Test@123");

        String token = loginRes.jsonPath().getString("body.data.otpVerificationToken");
        String otp = loginRes.jsonPath().getString("body.data.otp");

        Thread.sleep(5000);

        failure(validateOtp(token, otp));
    }
    @Test
    public void TC29_missingBodyOtp() {
        Response res = given()
                .header("Content-Type", "application/json")
                .post(otpUrl);

        failure(res);
    }
    @Test
    public void TC30_invalidContentTypeOtp() {
        Response res = given()
                .header("Content-Type", "text/plain")
                .body("invalid")
                .post(otpUrl);

        failure(res);
    }
    
}