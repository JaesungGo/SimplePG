package me.jaesung.simplepg.common.filter;

import lombok.extern.slf4j.Slf4j;
import me.jaesung.simplepg.common.exception.ApiException;
import me.jaesung.simplepg.common.util.HmacUtil;
import me.jaesung.simplepg.domain.dto.api.ApiCredentialResponse;
import me.jaesung.simplepg.domain.vo.api.ApiStatus;
import me.jaesung.simplepg.service.api.ApiCredentialService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@Slf4j
public class ApiAuthenticationFilter extends OncePerRequestFilter {

    private final ApiCredentialService apiCredentialService;

    @Value("${api.key.validity.minutes}")
    private int validatesMinutes;

    public ApiAuthenticationFilter(ApiCredentialService apiCredentialService) {
        this.apiCredentialService = apiCredentialService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().startsWith("/api/protected")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientId = request.getHeader("X-CLIENT-ID");
        String timestamp = request.getHeader("X-TIMESTAMP");
        String signature = request.getHeader("X-SIGNATURE");

        if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(signature)) {
            throw new ApiException.MissingApiHeaderException("header parameter(s) is(are) required");
        }

        if (!HmacUtil.isTimestampValid(timestamp, validatesMinutes)) {
            throw new ApiException.ExpiredRequestException("Request timestamp has expired");
        }

        ApiCredentialResponse credential = apiCredentialService.findByClientId(clientId);
        if (credential == null || !ApiStatus.ACTIVE.equals(credential.getStatus())) {
            throw new ApiException.ApiAuthenticationException("Invalid API credential");
        }

        // 인증 객체 생성 (clientId : ROLE_API_CLIENT)
        Authentication roleApiClient = new PreAuthenticatedAuthenticationToken(
                clientId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_API_CLIENT"))
        );

        SecurityContextHolder.getContext().setAuthentication(roleApiClient);

        String requestBody = getRequestBody(request);
        String method = request.getMethod();
        String path = request.getRequestURI();
        String dataToSign = method + path + requestBody + timestamp;

        try {
            String generatedWithDataToSign = HmacUtil.generateSignature(dataToSign, credential.getClientSecret());
            if (!generatedWithDataToSign.equals(signature)) {
                throw new ApiException.ApiAuthenticationException("Invalid signature");
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
