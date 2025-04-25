package me.jaesung.simplepg.controller.api;

import me.jaesung.simplepg.domain.dto.ApiCredentialRequest;
import me.jaesung.simplepg.domain.dto.ApiCredentialResponse;
import me.jaesung.simplepg.service.auth.ApiCredentialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/api-credentials")
public class ApiKeyController {

    private final ApiCredentialService apiCredentialService;

    public ApiKeyController(ApiCredentialService apiCredentialService) {
        this.apiCredentialService = apiCredentialService;
    }

    public ResponseEntity<String> validCredential(@RequestBody ApiCredentialRequest request){
        apiCredentialService.isValidApiCredential(request.getCliendId());
        return ResponseEntity.ok("Success");
    }
}
