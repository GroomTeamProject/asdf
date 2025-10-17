# 분산 트랜젝션 기반 배달 서비스 플랫폼

> **확장성과 안정성을 고려한 모듈화 배달 서비스 시스템**

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Version](https://img.shields.io/badge/version-0.0.1-blue)]()
[![AWS](https://img.shields.io/badge/deployed%20on-AWS-orange)]()

## 프로젝트 개요

**분산 트랜젝션 처리를 활용한 다중 사용자 배달 서비스 시스템**으로,<br /> 
고객-매장-배달원 간의 완전한 배달 생태계를 구현한 엔터프라이즈급 플랫폼입니다.

### 주요 목표
- **완전한 주문 생태계**: 주문부터 배달 완료까지 전 과정의 실시간 추적
- **통합 매장 관리**: 메뉴, 주문, 영업상태를 한 곳에서 관리
- **최적화된 배달 시스템**: 효율적인 배차 및 배달 관리
- **엔터프라이즈 보안**: JWT, RBAC 기반 다층 보안 체계
- **고성능 처리**: 다중 사용자 동시 접속 및 트랜잭션 처리

## 시스템 아키텍처

### AWS 클라우드 아키텍처
<img width="2302" height="1624" alt="Image" src="https://github.com/user-attachments/assets/c3b18830-aa0e-4325-8efd-a876ecae3aeb" />

### 주요 서비스 플로우
<img width="2090" height="1302" alt="Image" src="https://github.com/user-attachments/assets/c096ffa2-8862-48d3-bbfd-911bdd8d39bc" />
<img width="2090" height="1237" alt="Image" src="https://github.com/user-attachments/assets/8a5617e4-6bf7-4d2c-a66c-69e66abd2028" />

### 데이터베이스 설계
<img width="3217" height="2064" alt="Image" src="https://github.com/user-attachments/assets/53ad8174-4a70-4edd-b62d-e0dd894c0138" />

**주요 엔티티 관계:**
- `users` ↔ `orders`: 1:N (사용자-주문)
- `stores` ↔ `menus`: 1:N (매장-메뉴)
- `orders` ↔ `delivery_locations`: 1:N (주문-배송지)
- `payments` ↔ `orders`: 1:1 (결제-주문)

## 🛠️ 기술 스택

### Backend
- ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) **Spring Boot** - RESTful API 및 비즈니스 로직
- ![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariadb&logoColor=white) **MariaDB** - 관계형 데이터베이스 (RDS)
- ![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white) **JWT & RBAC** - 인증/인가 시스템

### Frontend
- ![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=flat-square&logo=vue.js&logoColor=white) **Vue.js** - SPA 프론트엔드 프레임워크

### Infrastructure & DevOps
- ![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazonaws&logoColor=white) **AWS** - EC2, RDS, S3, CloudFront
- ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white) **Docker** - 컨테이너화 및 배포
- ![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white) **GitHub Actions** - CI/CD 자동화

### Communication
- ![WebSocket](https://img.shields.io/badge/WebSocket-010101?style=flat-square&logo=socketdotio&logoColor=white) **WebSocket** - 실시간 주문 상태 추적
- **HTTPS** - 보안 통신 프로토콜

## 핵심 기능

### 회원/인증 시스템
✅ 회원가입/로그인/로그아웃<br />
✅ 프로필 관리 (닉네임, 연락처, 비밀번호 변경)<br />
✅ 배송지 관리 (CRUD, 기본 배송지 설정)<br />
✅ RBAC 권한 분리 (사용자/점주/라이더)<br />
✅ JWT 기반 인증 및 인가<br />

### 사용자 도메인
✅ 가게 탐색 (카테고리 필터링)<br />
✅ 메뉴 탐색 및 검색 (키워드, 옵션 포함)<br />
✅ 장바구니 관리 (CRUD, 옵션 선택)<br />
✅ 다양한 결제 수단 (카드, 간편결제, 포인트, 쿠폰)<br />
✅ 주문 취소 (조리 전 상태 제한)<br />
✅ 실시간 주문 상태 추적 (접수→조리→배달→완료)<br />

### 매장 관리 시스템
✅ 점주 전용 관리자 대시보드<br />
✅ 가게 정보 관리 (운영시간, 배달비, 휴무, 영업상태)<br />
✅ 메뉴 관리 (CRUD, 옵션 관리)<br />
✅ 주문 관리 (접수/거절, 조리 상태 업데이트)<br />
✅ 주문 상세 조회 (메뉴, 금액, 고객정보, 요청사항)<br />

### 라이더 시스템
✅ 배차 요청 수락/거절<br />
✅ 배달 상태 실시간 업데이트 (픽업→배달중→완료)<br />
✅ 배달 내역 조회 및 관리<br />

### 주문/결제 플랫폼
✅ 분산 트랜잭션 관리 (생성/변경/취소)<br />
✅ 외부 PG사 연동 결제 모듈<br />
✅ 결제 취소 및 환불 처리<br />
✅ 주문 상태 이벤트 기반 관리<br />

## 비기능적 요구사항

### 성능
- **동시성 처리**: 다중 사용자 동시 주문/조회 지원
- **DB 최적화**: 인덱스 적용, N+1 문제 해결
- **캐싱**: CloudFront 기반 정적 리소스 캐싱

### 보안
- **인증/인가**: JWT + RBAC 다층 보안
- **통신 보안**: HTTPS 강제 적용
- **권한 제어**: 역할 기반 접근 제어

### 안정성/가용성
- **자동 복구**: Docker 기반 자동 재시작
- **데이터 무결성**: 트랜잭션 처리 및 롤백 지원
- **모니터링**: 실시간 시스템 상태 추적

### 사용자 경험
- **반응형 UI**: 사용자/점주/라이더별 최적화된 인터페이스
- **실시간 알림**: WebSocket 기반 푸시 알림
- **직관적 UX**: 사용자 중심 인터페이스 설계

## 시작하기

### 사전 요구사항
```bash
- Java 11+
- Node.js 16+
- Docker & Docker Compose
- AWS CLI (배포용)
```

### 로컬 개발 환경 설정

1. **프로젝트 클론**
```bash
git clone https://github.com/GroomTeamProject/asdf.git
```

2. **환경 변수 설정**
```bash
# backend/.env
DB_URL_LOCAL=your-db-url
DB_USERNAME_LOCAL=your-db-username
DB_PASSWORD_LOCAL=your-db-password
ORIGIN_URL_LOCAL=your-vue-url
AWS_ACCESS_KEY=your-access-key
AWS_SECRET_KEY=your-secret-key
AWS_REGION=your-s3-region
AWS_S3_BUCKET=your-s3-name
JWT_SECRET_DEV=your-secret-key
```

3. **데이터베이스 초기화**
```bash
docker-compose up -d mariadb
```

4. **백엔드 실행**
```bash
cd backend
./gradlew bootRun
```

5. **프론트엔드 실행**
```bash
cd frontend
npm install
npm run serve
```

### Docker로 전체 실행
```bash
docker-compose up -d
```

## 개발 현황

### MVP 완료 상황
| 도메인 | 상태 | 주요 기능 |
|--------|------|-----------|
| **회원/인증** | ![완료](https://img.shields.io/badge/-완료-brightgreen) | JWT 인증, RBAC, 프로필 관리 |
| **사용자** | ![완료](https://img.shields.io/badge/-완료-brightgreen) | 주문, 결제, 실시간 추적 |
| **매장** | ![완료](https://img.shields.io/badge/-완료-brightgreen) | 메뉴 관리, 주문 처리 |
| **라이더** | ![완료](https://img.shields.io/badge/-완료-brightgreen) | 배차, 배달 상태 관리 |
| **결제** | ![완료](https://img.shields.io/badge/-완료-brightgreen) | PG 연동, 환불 처리 |

## API 문서

### 인증 API
| API | Method | Endpoint | Request | Response |
|-----|--------|----------|---------|----------|
| 회원가입 | POST | `/api/auth/signup` | SignupRequest | SignupResponse |
| 로그인 | POST | `/api/auth/login` | LoginRequest | LoginResponse |
| 토큰 재발급 | POST | `/api/auth/refresh` | RefreshRequest | RefreshResponse |

### 사용자 API
| API | Method | Endpoint | Request | Response |
|-----|--------|----------|---------|----------|
| 사용자 정보 조회 | GET | `/api/users/{userId}` | Path: userId | UserResponse |
| 사용자 정보 업데이트 | PUT | `/api/users/{userId}` | Path: userId, Body: UserUpdateRequest | UserResponse |
| 사용자 주소 목록 조회 | GET | `/api/users/{userId}/addresses` | Path: userId | List\<UserAddressResponse> |
| 사용자 주소 생성 | POST | `/api/users/{userId}/addresses` | Path: userId, Body: UserAddressRequest | UserAddressResponse |
| 사용자 주소 수정 | PUT | `/api/users/{userId}/addresses/{addressId}` | Path: userId, addressId, Body: UserAddressRequest | UserAddressResponse |
| 사용자 주소 삭제 | DELETE | `/api/users/{userId}/addresses/{addressId}` | Path: userId, addressId | - |
| 기본 배송지 설정 | PUT | `/api/users/{userId}/addresses/{addressId}/default` | Path: userId, addressId | UserAddressResponse |
| 비밀번호 변경 | PATCH | `/api/users/me/password` | Body: ProfilePasswordEdit | String |
| 계정 삭제(비활성화) | DELETE | `/api/users/me/deactivate` | - | - |

### 매장 관리 API (점주)
| API | Method | Endpoint | Request | Response |
|-----|--------|----------|---------|----------|
| 가게 등록 | POST | `/api/owner/store` | Body: StoreCreateRequest | Store |
| 내 가게 정보 조회 | GET | `/api/owner/store` | - | StoreResponse |
| 가게 기본 정보 수정 | PUT | `/api/owner/store` | Body: StoreUpdateRequest | StoreResponse |
| 가게 삭제(비활성화) | DELETE | `/api/owner/store` | - | - |
| 연락처 정보 수정 | PUT | `/api/owner/store/contact` | Body: StoreContactRequest | StoreResponse |
| 배달 설정 수정 | PUT | `/api/owner/store/delivery` | Body: StoreDeliveryRequest | StoreResponse |
| 위치 정보 수정 | PUT | `/api/owner/store/location` | Body: StoreLocationRequest | StoreResponse |
| 가게 이미지 업로드 | POST | `/api/owner/store/images` | Form: file | String(imageUrl) |
| 가게 이미지 삭제 | DELETE | `/api/owner/store/images/{id}` | Path: id | - |
| 운영시간 조회 | GET | `/api/owner/store/hours` | - | List |
| 운영시간 설정 | PUT | `/api/owner/store/hours` | Body: List | List |
| 휴무일 등록 | POST | `/api/owner/store/holidays` | Body: StoreHolidayRequest | String |
| 휴무일 목록 조회 | GET | `/api/owner/store/holidays` | - | List |
| 휴무일 삭제 | DELETE | `/api/owner/store/holidays/{id}` | Path: id | String |
| 가게 상태 조회 | GET | `/api/owner/store/status` | - | StoreStatusResponse |
| 가게 상태 변경 | PUT | `/api/owner/store/status` | Body: StoreStatusRequest | StoreStatusModifyResponse |
| 가게 대시보드 조회 | GET | `/api/owner/store/dashboard` | - | StoreDashboardResponse |

### 메뉴 API
| API | Method | Endpoint | Request | Response |
|-----|--------|----------|---------|----------|
| 메뉴 카테고리 조회 | GET | `/api/stores/{storeId}/menus/categories` | Path: storeId | List |
| 메뉴 목록 조회 | GET | `/api/stores/{storeId}/menus` | Path: storeId, Query: categoryId(optional) | List |
| 메뉴 상세 조회 | GET | `/api/stores/{storeId}/menus/{menuId}` | Path: storeId, menuId | MenuDetailResponse |

### 주문 API
| API | Method | Endpoint | Request | Response |
|-----|--------|----------|---------|----------|
| 주문 생성 | POST | `/api/orders` | Body: OrderRequest | OrderResponse |
| 주문 목록 조회 (검색) | GET | `/api/orders` | Query: OrderSearchRequest | Page |
| 주문 상세 조회 | GET | `/api/orders/{orderId}` | Path: orderId | OrderResponse |
| 주문 수락 (예상 조리 시간) | PUT | `/api/orders/{orderId}/accept` | Path: orderId, Body: OrderAcceptRequest | OrderResponse |
| 주문 거절 | PUT | `/api/orders/{orderId}/reject` | Path: orderId, Body: OrderRejectRequest | OrderResponse |
| 조리 시작 | PUT | `/api/orders/{orderId}/start-cooking` | Path: orderId | OrderResponse |
| 조리 완료 | PUT | `/api/orders/{orderId}/complete-cooking` | Path: orderId | OrderResponse |
| 배달 시작 | PUT | `/api/orders/{orderId}/start-delivery` | Path: orderId | OrderResponse |
| 배달 완료 | PUT | `/api/orders/{orderId}/deliver` | Path: orderId | OrderResponse |
| 주문 취소 | PUT | `/api/orders/{orderId}/cancel` | Path: orderId, Body: OrderCancelRequest | OrderResponse |
| 픽업 가능한 주문 목록 조회 | GET | `/api/orders/delivery/available` | Query: storeId(optional) | List |

### 라이더 API
| API | Method | Endpoint | Request | Response |
|-----|--------|----------|---------|----------|
| 요청 목록(REQUESTED) | GET | `/api/rider/deliveries` | Query: page, size, sort | ApiResponse\<DeliveryListResponse> |
| 배달 수락 | POST | `/api/rider/deliveries/{id}/accept` | Path: id, Body: { riderId } | ApiResponse\<DeliveryResponse> |
| 배달 거절 | POST | `/api/rider/deliveries/{id}/reject` | Path: id, Body: { riderId, reason } | ApiResponse\<DeliveryResponse> |
| 픽업 완료 | PUT | `/api/rider/deliveries/{id}/pickup` | Path: id | ApiResponse\<DeliveryResponse> |
| 배달 완료 | PUT | `/api/rider/deliveries/{id}/complete` | Path: id | ApiResponse\<DeliveryResponse> |
| 오늘 수익 합계 | GET | `/api/rider/deliveries/{riderId}/today-earnings` | Path: riderId | long |
| 오늘 배달 건수 | GET | `/api/rider/deliveries/{riderId}/count` | Path: riderId | long |
| 평균 배달 시간(분) | GET | `/api/rider/deliveries/{riderId}/avg` | Path: riderId | long |
| 라이더 현재 상태 | GET | `/api/rider/deliveries/{riderId}/status` | Path: riderId | DeliveryStatus |

### 리뷰 API
| API | Method | Endpoint | Request | Response |
|-----|--------|----------|---------|----------|
| 리뷰 생성 | POST | `/api/reviews` | Body: ReviewRequest | ReviewResponse |
| 가게별 리뷰 조회 | GET | `/api/reviews/store` | Query: storeId | List |
| 사용자별 리뷰 조회 | GET | `/api/reviews/user` | Query: userId | List |
| 리뷰 단건 조회 | GET | `/api/reviews/{reviewId}` | Path: reviewId | ReviewResponse |
| 리뷰 수정 | PUT | `/api/reviews/{reviewId}` | Path: reviewId, Body: ReviewRequest | ReviewResponse |
| 리뷰 삭제 | DELETE | `/api/reviews/{reviewId}` | Path: reviewId | - |
| 리뷰에 사장님 답글 추가 | POST | `/api/reviews/{reviewId}/reply` | Path: reviewId, Body: OwnerReplyRequest | ReviewResponse |
| 리뷰 신고 | POST | `/api/reviews/{reviewId}/report` | Path: reviewId | ReviewResponse |
| 가게 평균 평점 조회 | GET | `/api/reviews/store/rating` | Query: storeId | Double |
| 가게 리뷰 개수 조회 | GET | `/api/reviews/store/count` | Query: storeId | Long |

### 실시간 알림 API (SSE)
| API | Method | Endpoint | Request | Response |
|-----|--------|----------|---------|----------|
| SSE 연결 생성 | GET | `/api/sse/connect/{userId}` | Path: userId | SseEmitter |
| 연결 상태 확인 | GET | `/api/sse/status/{userId}` | Path: userId | boolean |

## 프로젝트 구조

```
📦 delivery-service-platform
├── 📁 backend/
│   ├── src/main/java/com/delivery/
│   │   ├── auth/          # 인증/인가
│   │   ├── user/          # 사용자 관리
│   │   ├── store/         # 매장 관리
│   │   ├── order/         # 주문 처리
│   │   ├── payment/       # 결제 시스템
│   │   └── delivery/      # 배달 관리
│   └── src/main/resources/
├── 📁 frontend/
│   ├── src/
│   │   ├── components/    # Vue 컴포넌트
│   │   ├── views/         # 페이지 뷰
│   │   ├── store/         # Vuex 상태관리
│   │   └── router/        # 라우팅
└── 📁 docker/
    ├── docker-compose.yml
    └── Dockerfile
```

## 팀 구성

## 팀 구성

이 프로젝트는 총 **5명의 팀원**으로 구성되어 있으며, 각 팀원은 **모듈화 기반 설계**에 따라 **하나의 모듈(도메인)**을 전담하여 해당 도메인의 전문가로 성장하는 것을 목표로 합니다.

| 팀원 이름 | 담당 역할 (도메인) | 주요 임무 및 세부 기능 |
|-----------|-------------------|----------------------|
| **오채영** <br />(팀장) | **회원/인증 담당** | **전 서비스 공통 Core 기능**을 담당 <ul><li>공통 인증/인가 (JWT, RBAC) 기능 구현</li><li>회원가입 및 로그인</li><li>프로필/마이페이지 관리 및 배송지 관리</li><li>사용자, 가게, 라이더에 대한 권한 분리 관리</li></ul> |
| **박승호**<br />(부팀장) | **가게 도메인 담당** | 사장님(점주) 전용 관리자 페이지 및 가게 운영 관리를 담당 <ul><li>가게 정보 관리 (운영시간, 배달비, 영업 상태 등)</li><li>메뉴 생성/조회/수정/삭제 (CRUD) 및 옵션 관리</li><li>주문 관리 (주문 접수/거절, 조리 상태 업데이트) 및 주문 상세 조회</li></ul> |
| **전우석** | **사용자 도메인 담당** | 일반 고객(사용자)의 서비스 이용 흐름을 담당 <ul><li>가게 및 메뉴 탐색, 메뉴 검색 기능 구현</li><li>장바구니 관리 (담기, 수정, 삭제, 옵션 포함)</li><li>결제 처리 (카드, 간편결제, 포인트, 쿠폰) 및 주문 취소</li><li>주문 상태 확인 기능</li></ul> |
| **김연성** | **라이더 도메인 담당** | 배달원(라이더)의 배달 업무 및 상태 관리를 담당 <ul><li>배차 요청 수락 및 거절 기능</li><li>배달 상태 관리 (픽업 → 배달중 → 완료)</li><li>배달 내역 조회 기능 구현</li></ul> |
| **이준희** | **주문/결제·플랫폼 공통 담당** | 핵심 비즈니스 로직인 주문/결제 흐름 전체의 안정화를 담당 <ul><li>주문 생성, 변경, 취소에 대한 **트랜잭션 관리**</li><li>결제 모듈 통합 (PG사 연동)</li><li>주문 상태 이벤트 관리</li></ul> |

### 개발 협업 방식
이러한 분업은 **API 명세서를 먼저 작성**하고 이를 기반으로 병렬 개발을 진행하여 체계적인 협업 경험을 쌓는 프로젝트 수행 방법을 적용했습니다. 각 도메인별 전문가가 되어 모듈 간 의존성을 최소화하고 효율적인 개발 프로세스를 구축했습니다.

## 향후 계획

- [ ] **마이크로서비스 아키텍처** 전환
- [ ] **Kubernetes** 오케스트레이션 도입
- [ ] **모니터링 시스템** 구축 (Prometheus, Grafana)
- [ ] **모바일 앱** 개발 (React Native)
- [ ] **AI 기반 추천 시스템** 도입



## 문의 및 지원

- **Issues**: [GitHub Issues](https://github.com/GroomTeamProject/asdf/issues)

---

<div align="center">

### 이 프로젝트가 도움이 되었다면 Star를 눌러주세요!

![GitHub stars](https://img.shields.io/github/stars/your-username/delivery-service-platform?style=social)
![GitHub forks](https://img.shields.io/github/forks/your-username/delivery-service-platform?style=social)

**엔터프라이즈급 배달 서비스 플랫폼으로 차세대 배달 생태계를 경험해보세요**

</div>