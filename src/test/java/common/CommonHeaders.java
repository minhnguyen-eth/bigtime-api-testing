package common;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.specification.RequestSpecification;

public class CommonHeaders {

    public static RequestSpecification noToken() {
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .config(RestAssured.config().sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation()))
                .contentType("application/json")
                .header("Accept", "application/json")
                .header("x-client-request", "hero")
                .header("x-client-language", "en");
    }

    public static RequestSpecification withToken(String token) {
        return noToken()
                .header("Authorization", "Bearer " + token);
    }
}
