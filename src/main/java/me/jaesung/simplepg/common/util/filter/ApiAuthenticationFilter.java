package me.jaesung.simplepg.common.util.filter;

import me.jaesung.simplepg.common.exception.CustomException;
import me.jaesung.simplepg.common.util.HmacUtil;
import me.jaesung.simplepg.domain.dto.ApiCredentialResponse;
import me.jaesung.simplepg.domain.vo.ApiCredential;
import me.jaesung.simplepg.domain.vo.ApiStatus;
import me.jaesung.simplepg.service.auth.ApiCredentialService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@Component
public class ApiAuthenticationFilter extends OncePerRequestFilter {

    private final ApiCredentialService apiCredentialService;

    @Value("${api.key.validity.minutes}")
    private int validatesMinutes;

    public ApiAuthenticationFilter(ApiCredentialService apiCredentialService) {
        this.apiCredentialService = apiCredentialService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/api/v1")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientId = request.getHeader("X-CLIENT-ID");
        String timestamp = request.getHeader("X-TIMESTAMP");
        String signature = request.getHeader("X-SIGNATURE");

        if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(signature)) {
            throw new CustomException.MissingApiHeaderException("header parameter(s) is(are) required");
        }

        if (!HmacUtil.isTimestampValid(timestamp, validatesMinutes)) {
            throw new CustomException.ExpiredRequestException("Request timestamp has expired");
        }

        ApiCredentialResponse credential = apiCredentialService.findByClientId(clientId);
        if (credential == null || !ApiStatus.ACTIVE.equals(credential.getStatus())) {
            throw new CustomException.ApiAuthenticationException("Invalid API credential");
        }

        String requestBody = getRequestBody(request);
        String method = request.getMethod();
        String path = request.getRequestURI();
        String dataToSign = method + path + requestBody + timestamp;

        try {
            String generatedWithDataToSign = HmacUtil.generateSignature(dataToSign, credential.getClientSecret());
            if (!generatedWithDataToSign.equals(signature)) {
                throw new CustomException.ApiAuthenticationException("Invalid signature");
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Request의 Body의 내용 추출해서 반환
     */
    private String getRequestBody(HttpServletRequest request) throws IOException {
        if ("GET".equals(request.getMethod())) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        BufferedReader br = request.getReader();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
