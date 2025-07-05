//package me.jaesung.simplepg.common.util.filter;
//
//import me.jaesung.simplepg.common.exception.ApiException;
//import me.jaesung.simplepg.common.util.HmacUtil;
//import me.jaesung.simplepg.domain.dto.api.ApiCredentialResponse;
//import me.jaesung.simplepg.domain.vo.api.ApiStatus;
//import me.jaesung.simplepg.service.api.ApiCredentialService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.mock.web.MockFilterChain;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//class ApiAuthenticationFilterTest {
//
//    @Mock
//    private ApiCredentialService apiCredentialService;
//
//    @InjectMocks
//    private ApiAuthenticationFilter filter;
//
//    private MockHttpServletRequest request;
//    private MockHttpServletResponse response;
//    private MockFilterChain filterChain;
//    private final String VALID_CLIENT_ID = "test-client-id";
//    private final String VALID_CLIENT_SECRET = "test-client-secret";
//    private final String TIMESTAMP = String.valueOf(System.currentTimeMillis());
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        ReflectionTestUtils.setField(filter, "validatesMinutes", 5);
//
//        request = new MockHttpServletRequest();
//        response = new MockHttpServletResponse();
//        filterChain = new MockFilterChain();
//
//        ApiCredentialResponse credentialResponse = ApiCredentialResponse.builder()
//                .clientId(VALID_CLIENT_ID)
//                .clientSecret(VALID_CLIENT_SECRET)
//                .status(ApiStatus.ACTIVE)
//                .build();
//
//        when(apiCredentialService.findByClientId(VALID_CLIENT_ID)).thenReturn(credentialResponse);
//    }
//
//    @Test
//    @DisplayName(value = "v1 엔드포인트 인증 체크 - 성공")
//    void checkFilterEndPointsSuccess() throws Exception {
//        //given
//        request.setRequestURI("/api/v1/test");
//
//        //when
//        filter.doFilterInternal(request, response, filterChain);
//
//        //then
//        assertTrue(filterChain.getRequest() != null);
//    }
//
//    @Test
//    @DisplayName(value = "헤더 없을 경우 -> MissingApiHeaderException 확인")
//    void checkHeaderException() {
//        //given
//        request.setRequestURI("/api/v2/test");
//
//        //then
//        assertThrows(ApiException.MissingApiHeaderException.class, () -> {
//            filter.doFilterInternal(request, response, filterChain);
//        });
//    }
//
//    @Test
//    @DisplayName(value = "정상적으로 필터 통과하는지 체크")
//    void shouldAuthenticateValidRequest() throws Exception {
//        request.setRequestURI("/api/v2/test");
//        request.setMethod("POST");
//
//        String requestBody = "{\"key\":\"value\"}";
//        request.setContent(requestBody.getBytes());
//
//        String timeMillis = String.valueOf(System.currentTimeMillis());
//
//        request.addHeader("X-CLIENT-ID", VALID_CLIENT_ID);
//        request.addHeader("X-TIMESTAMP", timeMillis);
//
//        String dataToSign = request.getMethod() + request.getRequestURI() + requestBody + timeMillis;
//        String signature = HmacUtil.generateSignature(dataToSign, VALID_CLIENT_SECRET);
//        request.addHeader("X-SIGNATURE", signature);
//
//        filter.doFilterInternal(request, response, filterChain);
//
//        assertTrue(filterChain.getRequest() != null);
//    }
//
//}