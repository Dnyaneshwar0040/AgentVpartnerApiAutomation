package AuthAPI;

import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.*;

public class LoginPagePartnerAPI {

    @Test
    @Epic("API Testing")
    @Feature("Login API")
    @Story("Valid Login")
    @Severity(SeverityLevel.CRITICAL)
    public void successTest() {
        Assert.assertTrue(true);
    }

    @Test
    @Epic("API Testing")
    @Feature("Login API")
    @Story("Invalid Login")
    @Severity(SeverityLevel.CRITICAL)
    public void failedTest() {
        Assert.assertTrue(false);
    }
}