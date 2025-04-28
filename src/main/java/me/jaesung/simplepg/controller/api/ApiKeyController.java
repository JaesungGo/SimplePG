package me.jaesung.simplepg.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v2")
public class ApiKeyController {

    @PostMapping("/endpoint")
    public ResponseEntity<Map<String, Object>> protectedEndpoint(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "인증된 POST 요청이 성공적으로 처리되었습니다");
        response.put("receivedData", requestBody);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resource")
    public ResponseEntity<Map<String, Object>> protectedResource() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "인증된 GET 요청이 성공적으로 처리되었습니다");
        response.put("data", Collections.singletonMap("resource", "보호된 리소스 데이터"));
        return ResponseEntity.ok(response);
    }
}
