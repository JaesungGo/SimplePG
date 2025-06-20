# 💳 PG 결제 서비스 (SimplePG MVP) [![SimplePG 결제 API 배포(Azure)](https://github.com/JaesungGo/SimplePG/actions/workflows/main_simplepg-backend.yml/badge.svg)](https://github.com/JaesungGo/SimplePG/actions/workflows/main_simplepg-backend.yml)

> 간편하게 결제 요청 및 승인 처리를 할 수 있는 **경량형 PG 백엔드 서비스**입니다.   
> 가맹점 등록부터 결제 승인, 결제 로그 기록, 외부 Webhook 처리까지의 흐름을 구현했습니다.

## 📌 프로젝트 요약

| 항목 | 내용 |
|------|------|
| 기간 | 2025.04.22 ~ 2025.05.02 |
| 목적 | PG 서비스의 핵심 결제 흐름을 직접 구현하고 인증, 보안, 상태 관리 등 학습 |
| 역할 | 백엔드 단독 구현 |
| 기술 스택 | Java 17, Spring Legacy (5.3.37), Mybatis, MySQL 8, Gradle, Postman |
| 설계 문서 | [관련 문서](https://tan-jersey-c73.notion.site/SimplePG-PG-1dd419fd2c4b80489312d5571bbec066) / [ERD 보기](#erd-구조) / [API 명세](#api-요약) |
| 실행 방법 | `./gradlew bootRun` 또는 IntelliJ 실행 |

---

## 🧠 구현 목표

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
 ├── common/                            # 공통 유틸리티 및 예외 처리와 관련된 모듈
 │   ├── exception/                     # 커스텀 예외 클래스 및 예외 처리 로직 
 │   ├── util/                          # 전역적으로 사용되는 유틸리티 클래스 
 │   └── filter/                        # 요청 필터링 및 보안 관련 필터 클래스
 │                
 ├── config/                            # 스프링 설정 파일 및 빈 등록 관련 클래스
 ├── controller/                        # REST API 및 웹훅 처리 컨트롤러 모음
 │   ├── api/                           # API 인증 및 설정 관련 컨트롤러
 │   ├── mock/                          # 외부 결제 시스템 모의(mock) 컨트롤러
 │   ├── payment/                       # 결제 요청 및 상태 조회 관련 컨트롤러
 │   └── webhook/                       # 웹훅 요청 처리 컨트롤러
 ├── domain/                            # 도메인 객체 및 데이터 전송 객체(DTO) 정의
 │   ├── dto/                           # 데이터 전송 객체(DTO) 모음
 │   │   ├── api/                       # API 인증 및 응답 관련 DTO
 │   │   ├── payment/                   # 결제 요청/응답 관련 DTO
 │   │   └── webhook/                   # 웹훅 요청/응답 관련 DTO
 │   └── vo/                            # 값 객체(Value Object) 모음
 │       ├── api/                       # API 인증 객체 
 │       ├── payment/                   # 결제 관련 값 객체
 │       └── webhook/                   # 웹훅 관련 값 객체
 ├── mapper/                            # MyBatis를 위한 SQL 매핑 인터페이스 및 XML 파일
 ├── service/                           # 비즈니스 로직 처리 계층
 │   ├── api/                           # API 인증 및 관리 서비스
 │   ├── payment/                       # 결제 처리 및 상태 관리 관련 서비스
 │   ├── webclient/                     # WebClient를 사용한 외부 API 호출 서비스
 └── └── webhook/                       # 웹훅 수신 및 처리 관련 서비스
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
## 🧪 테스트 방법

### 🌐 배포 환경 테스트 페이지 사용법

브라우저에서 다음 URL로 접속하여 테스트할 수 있습니다

#### 📊 메인 대시보드
[🔗 메인 대시보드 접속하기](https://simplepg-backend-f5g2apf5h4efgjd4.koreacentral-01.azurewebsites.net/SimplePG-1.0.0/)
- 시스템 상태 확인
- 각 테스트 페이지로 이동하는 네비게이션 제공

#### 💳 결제 시스템 테스트
[🔗 결제 테스트 페이지](https://simplepg-backend-f5g2apf5h4efgjd4.koreacentral-01.azurewebsites.net/SimplePG-1.0.0/payment-test)
- **결제 요청**: 새로운 결제 요청 생성 및 Payment Key 발급
- **결제 상태 조회**: Payment Key로 결제 상태 확인
- **결제 취소**: 결제 취소 처리 (사유 포함)
- **결제 완료**: 결제 완료 처리
- **최근 결제 내역**: Mock 데이터로 결제 내역 확인

#### 🔄 웹훅 테스트
[🔗 웹훅 테스트 페이지](https://simplepg-backend-f5g2apf5h4efgjd4.koreacentral-01.azurewebsites.net/SimplePG-1.0.0/webhook-test)
- **웹훅 플로우 시각화**: 결제 처리 과정을 단계별로 표시
- **성공 웹훅 전송**: 외부 결제 시스템에서 성공 결과 전송 시뮬레이션
- **실패 웹훅 전송**: 외부 결제 시스템에서 실패 결과 전송 시뮬레이션
- **Mock 서버 요청**: Mock 서버로 결제 요청 전송
- **실시간 로그**: 웹훅 전송 과정을 실시간으로 모니터링

#### 🔧 API 테스트
[🔗 API 테스트 페이지](https://simplepg-backend-f5g2apf5h4efgjd4.koreacentral-01.azurewebsites.net/SimplePG-1.0.0/api-test)
- **빠른 테스트**: 간단한 결제 요청 및 상태 조회
- **JSON 요청 테스트**: 커스텀 JSON으로 API 직접 호출
- **응답 분석**: API 응답 결과 분석 및 표시

### 🔐 보안 고려사항
- 실제 설정 파일들은 `.gitignore`에 의해 제외됩니다
- 민감한 정보는 환경변수를 통해 주입됩니다

### 1-1. 설정 파일 생성
```bash
# Template 파일을 복사하여 실제 설정 파일 생성
cp src/main/resources/application.properties.template src/main/resources/application.properties
```

### 1-2. 환경변수 설정
다음 환경변수들을 설정해주세요

```bash
# 데이터베이스 설정
export MYSQL_HOST=your-mysql-host
export MYSQL_DATABASE=your-database-name  
export MYSQL_USERNAME=your-username
export MYSQL_PASSWORD=your-password

# 보안 설정
export ENCRYPTION_KEY=your-32-character-encryption-key

# API 설정
export API_REQUEST_URL=https://your-domain.com/mock/request
export API_RETURN_URL=https://your-domain.com/api/protected/webhook
```

### 2-1. Postman 테스트 리소스
- Postman 컬렉션: [SimplePG API Tests.postman_collection.json](https://github.com/user-attachments/files/20829045/SimplePG.API.Tests.postman_collection.json)
- 환경 설정 파일: [ApiAuthentication.postman_environment.json](https://github.com/user-attachments/files/20025719/ApiAuthentication.postman_environment.json)


### 2-2. 환경변수 설정
- `BASE_URL`: `http://localhost:8080`
- `CLIENT_ID`: 가맹점 등록 후 발급받은 ID
- `SECRET_KEY`: 가맹점 등록 후 발급받은 Secret

---
## 📌 향후 계획 및 회고

- 가맹점 back-office 개발 (관리자 인증, 결제 조회, 통계)
- 결제 취소 및 재시도 로직 구현
- 분산 환경에서의 Redis 분산 락 구현

  <aside>

실제 서비스에는 미치지 못하겠지만, 전체적인 턴 키 방식의 결제 서비스 동작 흐름을 이해하기 위해 진행하였습니다. 프로젝트를 통해 결제 시스템의 핵심은 보안과 데이터 **무결성**, 그리고 장애 대응 능력이라는 점을 깨달았습니다. 특히 동시성 제어와 분산 환경에서의 **데이터 일관성 유지**는 실제 서비스에서 계속 고민해야 할 부분이라는 생각이 들었습니다.

</aside>
