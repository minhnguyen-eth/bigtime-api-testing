package salary;

import common.BaseTest;
import common.CommonHeaders;
import org.testng.annotations.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

public class Paysheet extends BaseTest {

    public String getLatestPaysheetId() {
        List<Map<String, Object>> paysheets = CommonHeaders.withToken(token)
                .when()
                .get("/api/pay-sheet/list?page=1&itemsPerPage=20")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data.data");

        return paysheets.stream()
                .map(p -> (String) p.get("id"))
                .findFirst()
                .orElse(null);
    }

    @Test
    public void testE2EPaysheet() throws InterruptedException {
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String createPayload = String.format("""
        {
          "id": "",
          "name": "Testing",
          "note": "Testing ",
          "type": 0,
          "status": 0,
          "isChooseAllUser": false,
          "rangeMonth": "2025-7",
          "start_date": "2025-07-01",
          "end_date": "2025-07-31",
          "created_at": "%s",
          "created_by": "admin",
          "users_id": [
            "4cMiTbHpAz"
          ]
        }
        """, createdAt);

        System.out.println("====== STEP 1: Create Paysheet ======");

        CommonHeaders.withToken(token)
                .body(createPayload)
                .when()
                .post("/api/pay-sheet/create")
                .then()
                .log().all()
                .statusCode(200)
                .body("code", equalTo(200));

        Thread.sleep(2000);

        String paysheetId = getLatestPaysheetId();
        System.out.println("==> Created Paysheet ID: " + paysheetId);

        String submitPayload = String.format("""
        {
            "paysheet_id": "%s"
        }
        """, paysheetId);

        System.out.println("====== STEP 2: Submit Paysheet ======");
        System.out.println("Submit Payload:\n" + submitPayload);

        CommonHeaders.withToken(token)
                .body(submitPayload)
                .when()
                .post("/api/pay-sheet/submit-pay-sheet")
                .then()
                .log().all()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data", equalTo("Updated successfully"));
    }

    @Test
    public void testCreateAndSubmitPaysheetForAllUsers() throws InterruptedException {
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String createPayload = String.format("""
            {
              "id": "",
              "name": "API Testing ",
              "note": "API Testing  ",
              "type": 0,
              "status": 0,
              "isChooseAllUser": true,
              "rangeMonth": "2025-8",
              "start_date": "2025-08-01",
              "end_date": "2025-08-31",
              "created_at": "%s",
              "created_by": "admin",
              "users_id": []
            }
        """, createdAt);

        System.out.println("====== STEP 1: Create Paysheet for All Users ======");

        CommonHeaders.withToken(token)
                .body(createPayload)
                .when()
                .post("/api/pay-sheet/create")
                .then()
                .log().all()
                .statusCode(200)
                .body("code", equalTo(200));

        // Wait for system to update
        Thread.sleep(2000);

        String paysheetId = getLatestPaysheetId();
        System.out.println("==> Created Paysheet ID: " + paysheetId);

        String submitPayload = String.format("""
            {
                "paysheet_id": "%s"
            }
        """, paysheetId);

        System.out.println("====== STEP 2: Submit Paysheet for All Users ======");
        System.out.println("Submit Payload:\n" + submitPayload);

        CommonHeaders.withToken(token)
                .body(submitPayload)
                .when()
                .post("/api/pay-sheet/submit-pay-sheet")
                .then()
                .log().all()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data", equalTo("Updated successfully"));
    }
}
