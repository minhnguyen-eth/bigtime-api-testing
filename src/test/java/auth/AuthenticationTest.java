package auth;
import common.CommonHeaders;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.equalTo;

public class AuthenticationTest {

    Dotenv dotenv = Dotenv.load();

    @Test
    public void testLoginSuccess() {
        RestAssured.baseURI = dotenv.get("BASE_URI");

        String loginPayload = String.format("""
            {
                "username": "%s",
                "password": "%s",
                "remember": true
            }
        """, dotenv.get("ADMIN_USERNAME"), dotenv.get("ADMIN_PASSWORD"));

        CommonHeaders.noToken()
                .body(loginPayload)
                .when()
                .post("/api/auth/login")
                .then()
                .log().all()
                .statusCode(200)
                .body("message", equalTo("Logged in successfully"));
    }
}
