package com.example.demo.integration;

import com.example.demo.DemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    classes = DemoApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("dev")
class AdminIntegrationTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private String baseUrl() {
        return "http://localhost:" + port + "/api/admin";
    }

    @Test
    void testAdminCRUDOperations() {
        // 测试创建管理员
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("name", "integration_test_admin");
        adminData.put("password", "test_password");
        adminData.put("isSuper", 0);
        adminData.put("groupName", "测试组");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> createRequest = new HttpEntity<>(adminData, headers);

        ResponseEntity<Map> createResponse = restTemplate.postForEntity(
            baseUrl() + "/create", createRequest, Map.class);

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        Map<String, Object> createResponseBody = createResponse.getBody();
        assertNotNull(createResponseBody);
        assertEquals(200, createResponseBody.get("code"));

        // 获取创建的管理员ID
        Map<String, Object> createdAdmin = (Map<String, Object>) createResponseBody.get("data");
        Integer adminId = (Integer) createdAdmin.get("adminId");
        assertNotNull(adminId);

        // 测试获取管理员
        ResponseEntity<Map> getResponse = restTemplate.getForEntity(
            baseUrl() + "/" + adminId, Map.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Map<String, Object> getResponseBody = getResponse.getBody();
        assertNotNull(getResponseBody);
        assertEquals(200, getResponseBody.get("code"));

        // 测试更新管理员
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("adminId", adminId);
        updateData.put("name", "updated_integration_test_admin");
        updateData.put("groupName", "更新测试组");

        HttpEntity<Map<String, Object>> updateRequest = new HttpEntity<>(updateData, headers);
        restTemplate.put(baseUrl() + "/update", updateRequest);

        // 验证更新结果
        ResponseEntity<Map> verifyResponse = restTemplate.getForEntity(
            baseUrl() + "/" + adminId, Map.class);
        Map<String, Object> verifyResponseBody = verifyResponse.getBody();
        Map<String, Object> updatedAdmin = (Map<String, Object>) verifyResponseBody.get("data");
        assertEquals("updated_integration_test_admin", updatedAdmin.get("name"));

        // 测试删除管理员
        restTemplate.delete(baseUrl() + "/" + adminId);

        // 验证删除结果
        ResponseEntity<Map> deleteVerifyResponse = restTemplate.getForEntity(
            baseUrl() + "/" + adminId, Map.class);
        Map<String, Object> deleteVerifyBody = deleteVerifyResponse.getBody();
        assertEquals(404, deleteVerifyBody.get("code"));
    }

    @Test
    void testGetAllAdmins() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
            baseUrl() + "/list", Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertNotNull(responseBody.get("data"));
    }

    @Test
    void testPagination() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
            baseUrl() + "/page?pageNum=1&pageSize=5", Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertNotNull(responseBody.get("data"));
    }
}