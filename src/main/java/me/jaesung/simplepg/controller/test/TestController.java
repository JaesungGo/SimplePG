package me.jaesung.simplepg.controller.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/test")
public class TestController {

    /**
     * 메인 대시보드 페이지
     */
    @GetMapping()
    public String index() {
        log.info("메인 대시보드 페이지 요청");
        return "index";
    }

    /**
     * 결제 시스템 테스트 페이지
     */
    @GetMapping("/payment-test")
    public String paymentTest() {
        log.info("결제 시스템 테스트 페이지 요청");
        return "payment-test";
    }

    /**
     * 웹훅 테스트 페이지
     */
    @GetMapping("/webhook-test")
    public String webhookTest() {
        log.info("웹훅 테스트 페이지 요청");
        return "webhook-test";
    }

    /**
     * API 테스트 페이지
     */
    @GetMapping("/api-test")
    public String apiTest() {
        log.info("API 테스트 페이지 요청");
        return "api-test";
    }

    /**
     * 헬스 체크용 엔드포인트
     */
    @GetMapping("/health")
    public String health() {
        log.info("헬스 체크 요청");
        return "OK";
    }

    /**
     * JSP 테스트용 엔드포인트
     */
    @GetMapping("/test")
    public String test() {
        log.info("JSP 테스트 페이지 요청");
        return "test";
    }

    /**
     * 간단한 결제 테스트 페이지
     */
    @GetMapping("/payment-test-simple")
    public String paymentTestSimple() {
        log.info("간단한 결제 테스트 페이지 요청");
        return "payment-test-simple";
    }
} 