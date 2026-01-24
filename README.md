# MONEW
관심사 기반 뉴스 큐레이션과 소셜 상호작용을 결합한 뉴스 플랫폼
![Coverage](.github/badges/jacoco.svg)
![Branch Coverage](.github/badges/branches.svg)
MONEW는 여러 뉴스 API를 기반으로 기사를 제공하고,
사용자들이 기사에 댓글을 남기고 좋아요를 통해 의견을 나눌 수 있는 서비스입니다.
본 프로젝트는 백엔드 중심의 팀 프로젝트로 진행되었습니다.

### 작품 소개
MONEW는 여러 뉴스 API를 통합해 사용자의 관심사에 맞는 뉴스롤 제공하고,
뉴스에 대해 의견을 나누고 반응할 수 있는 소셜 기능을 함께 제공하는 서비스입니다.
기존 뉴스 서비스가 단순 정보 전달에 그쳤다면, 모뉴는 사용자의 관심 키워드와 활동 이력을 기반으로 뉴스를 큐레이션하고
댓글, 좋아요, 알림 기능을 통해 뉴스를 '소비'하는 것을 넘어 '참여'할 수 있는 경험을 제공합니다.

### 프로젝트 배경
현대 사회에서 뉴스의 양은 폭발적으로 증가하지만, 
사용자가 실제로 관심 있는 뉴스를 선별해 읽기에는 많은 시간과 노력이 필요합니다.
기존 뉴스 서비스들은 키워드 필터링이 제한적이거나, 관심사 반영이 일회성에 그치고, 뉴스에 대한 의견 교류가 활발하지 않다는 한계가 있었습니다.
이에 저희 팀은 뉴스를 사용자에게 맞게 정리해 주고, 생각을 나눌 수 있는 공간을 목표로 관심사 기반 큐레이션 + 소셜 기능을 결합한 모뉴(MONEW)를 기획하였습니다.

### DEMO
모뉴 서비스 시연 영상
주요 기능별 화면 캡처

### 주요 기능
관심사 기반 뉴스 큐레이션
- 사용자가 구독한 관심사 키워드를 기준으로 관련 뉴스를 자동 추천합니다
관심사 구독 및 관리
- 관심사를 등록, 구독하고 키워드를 통해 관심 뉴스 흐름을 구성할 수 있습니다.
소셜 기능
- 뉴스 기사별 댓글 작성과 좋아요를 통해 사용자 간 의견을 공유할 수 있습니다
알림 시스템
- 관심사 관련 뉴스 등록 또는 댓글 좋아요 발생 시 알림을 제공합니다
사용자 활동 내역 제공
- 댓글, 좋아요, 조회한 뉴스 등 사용자의 활동 이력을 한눈에 확인할 수 있습니다ㅑ.
로그인 기반 개인화 서비스
- 로그인 사용자만 접근 가능한 구조로 개인화된 뉴스 경험을 제공합니다.

### 개발 환경
OS : Windows / macOS
IDE : IntelliJ IDEA
JDK : Amazon Corretto 17
Build Tool : Gradle
Version Control : Git, GitHub
Schedule & Collaboration : Notion
API Documentation : Swagger UI

### 기술 스택
Backend
- Framework : Spring Boot
- ORM : Spring Data JPA, QueryDSL
- Validation : Spring Validation
- Batch Processing : Spring Batch
- API Docs : springdoc-openapi (Swagger UI)
- Utility : Lombok
Database
- RDBMS : PostgreSQL
- In-Memory DB : H2 (테스트 및 개발 환경)
- NoSQL : MongoDB (로그/비정형 데이터 처리)
Infrastructure
- Cloud : AWS

### 프로젝트 구조
본 프로젝트는 도메인 중심 구조를 기반으로 설계되었으며, 기능 확장과 유지보수를 고려해 책임별 패키지를 분리했습니다
monew
 ├─ domain
 │   ├─ user                # 사용자 및 인증 관리
 │   ├─ interest            # 관심사 및 구독 관리
 │   ├─ article             # 뉴스 기사 관리
 │   ├─ comment             # 댓글 및 좋아요 기능
 │   ├─ notification        # 알림 기능
 │   └─ userActivity        # 사용자 활동 내역 관리
 │
 ├─ global
 │   ├─ config              # 공통 설정
 │   ├─ exception           # 전역 예외 처리
 │   ├─ dto                 # 공통 DTO
 │   ├─ enums               # 공통 Enum
 │   ├─ aop                 # 로깅 및 공통 관심사 처리
 │   └─ batch               # 배치 작업
 │
 └─ MonewApplication
 
## 구조 설계 특징
- Controller/Service/Repository 계층 분리
- 도메인별 독립성 확보로 협업 및 역할 분담 용이
- 배치 작업을 통한 데이터 정리 및 자동 처리 구조 도입

## 기능 처리 흐름
- 뉴스 수집 -> 관심사 매칭 -> 사용자 큐레이션 -> 소설 상호작용 -> 알림 제공

### 기대효과
- 사용자 관심사에 맞춘 뉴스 제공으로 정보 탐색 비용 감소
- 뉴스에 대한 상호작용을 통해 사용자 참여도 향상
- 실서비스를 고려한 도메인 설계 및 확장성 있는 구조 경험

### Developer
- 김지예 여따가 각자의 깃허브 주소를 넣을까요?
- 박도겸
- 박재완
- 신제원
- 이정훈
- 최지혜



* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

