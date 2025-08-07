package common;

import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    protected static Dotenv dotenv;
    protected static String token;

    @BeforeClass
    public void baseSetup() {
        dotenv = Dotenv.load();
        RestAssured.baseURI = dotenv.get("BASE_URI");

        String loginPayload = String.format("""
            {
                "username": "%s",
                "password": "%s",
                "remember": true
            }
        """, dotenv.get("ADMIN_USERNAME"), dotenv.get("ADMIN_PASSWORD"));

        token = CommonHeaders.noToken()
                .body(loginPayload)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("data.access_token");
    }
}
