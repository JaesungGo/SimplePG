# 💳 PG 결제 서비스 (SimplePG MVP)

> 간편하게 결제 요청 및 승인 처리를 할 수 있는 **경량형 PG 백엔드 서비스**입니다.   
> 가맹점 등록부터 결제 승인, 결제 로그 기록, 외부 Webhook 처리까지의 흐름을 구현했습니다.

---

## 📌 프로젝트 요약

| 항목 | 내용 |
|------|------|
| 기간 | 2025.04.22 ~ (진행중) |
| 목적 | PG 서비스의 핵심 결제 흐름을 직접 구현하고 인증, 보안, 상태 관리 등 학습 |
| 역할 | 백엔드 단독 구현 |
| 기술 스택 | Java 17, Spring Legacy (5.3.37), Mybatis, MySQL 8, Gradle, Postman |
| 설계 문서 | [관련 문서](https://tan-jersey-c73.notion.site/SimplePG-PG-1dd419fd2c4b80489312d5571bbec066) / [ERD 보기](#erd-구조) / [API 명세](#api-요약) |
| 실행 방법 | `./gradlew bootRun` 또는 IntelliJ 실행 |

---

## 🧠 구현 배경 및 목표

- **HMAC 인증 흐름 이해 및 구현**  
  단순 토큰 방식이 아닌 `secret key`를 통한 시그니처 검증을 구현해 실제 PG처럼 동작하도록 설계

- **상태 기반 결제 처리**  
  단일 테이블에서 `READY → APPROVED → COMPLETED` 등의 상태 전이 관리

- **Webhook 기반 비동기 처리**  
  외부 결제 모듈이 승인/실패 결과를 콜백으로 보내오는 구조를 시뮬레이션

- **로깅을 통한 추적성 확보**  
  모든 상태 변경 및 외부 연동 결과를 `payment_log`에 기록

---

## ⚙️ 핵심 기능 요약

![Image](https://github.com/user-attachments/assets/65049cd3-8ef8-41d5-8bb4-ace633209e50)
| 기능 | 설명 |
|------|------|
| 인증 처리 | client_id + secret 기반 HMAC 서명 검증, 위조 방지 |
| 결제 생성 | `POST /api/payments`를 통해 결제 준비 요청 |
| 결제 승인 처리 | `POST /api/webhook/payment` 으로 외부 결제 결과 반영 |
| 로그 기록 | 모든 결제 상태 변경 내역 자동 기록 |
| 예외 처리 | `@RestControllerAdvice` 기반의 일관된 에러 응답 |

---

## 🗂 폴더 구조

```bash
simplepg/
 ├── common/
 │   ├── exception/
 │   ├── util/
 │   ├── filter/
 │   └── interceptor/
 ├── config/
 ├── controller/
 │   ├── api/
 │   ├── mock/
 │   ├── payment/
 │   └── webhook/
 ├── domain/
 │   ├── dto/
 │   │   ├── api/
 │   │   ├── payment/
 │   │   └── webhook/
 │   └── vo/
 │       ├── api/
 │       ├── payment/
 │       └── webhook/
 ├── mapper/
 ├── service/
 │   ├── api/
 │   ├── payment/
 │   ├── webclient/
 └── └── webhook/
```

---

## ERD 구조
![Image](https://github.com/user-attachments/assets/9c15e8eb-7e43-4328-a011-a3f2cf2266d7)
| 테이블명 | 설명 | 주요 컬럼 |
| --- | --- | --- |
| `api_credential` | 가맹점의 API 인증 정보 (client_id, client_secret 등)를 저장 | `client_id`, `client_secret`, `client_name`, `status`, `return_url`, `created_at` |
| `payment` | 결제 트랜잭션의 핵심 정보 (주문번호, 금액, 상태 등)를 관리 | `payment_id`, `payment_key`, `client_id`, `order_no`, `amount`, `status`, `method_code`, `created_at`, `approved_at` |
| `payment_log` | 결제 상태 변경 이력을 기록하여 검증,디버깅에 활용 | `log_id`, `payment_id`, `action`, `status`, `details`, `created_at` |
- **`api_credential` → `payment`**: 1:N 관계 (FK : `client_id`)
- **`payment` → `payment_log`**: 1:N 관계 (FK : `payment_id`, `ON DELETE CASCADE`)

---
## 테스트 방법

### Postman 테스트 리소스
- Postman 컬렉션: [SimplePG API Tests.postman_collection.json](https://github.com/user-attachments/files/20025718/SimplePG.API.Tests.postman_collection.json)
- 환경 설정 파일: [ApiAuthentication.postman_environment.json](https://github.com/user-attachments/files/20025719/ApiAuthentication.postman_environment.json)


### 환경변수 설정
- `BASE_URL`: `http://localhost:8080`
- `CLIENT_ID`: 가맹점 등록 후 발급받은 ID
- `SECRET_KEY`: 가맹점 등록 후 발급받은 Secret
