# StreamFix 🎬
___

## 📋 프로젝트 개요

StreamFix는 헥사고날 아키텍처(Hexagonal Architecture) 패턴을 적용하여 설계된 스트리밍 서비스 플랫폼입니다. 모듈화된 구조로 개발되어 유지보수성과 확장성을 고려했습니다.

## 🌟 최근 주요 업데이트 (Project Stabilization)

최근 프로젝트 안정화 단계(Phase 1)를 통해 JDK 버전을 21로 마이그레이션하고, 주요 의존성을 최신 안정 버전으로 업데이트하여 보안성과 성능을 강화했습니다.

- **JDK 21 마이그레이션**: 프로젝트의 기반 자바 버전을 17에서 **21**로 업그레이드했습니다.
- **주요 의존성 업데이트**:
    - Spring Boot: `3.3.3` → `3.5.0`
    - Kotlin: `2.0.20` → `2.1.21`
    - QueryDSL: `5.0.0` → `5.1.0`
    - Fixture Monkey: `0.4.12` → `1.1.11`
    - Asciidoctor Plugin: `3.3.2` → `4.0.4`
    - 기타 라이브러리 및 플러그인 버전을 최신 안정 버전으로 동기화했습니다.
- **의존성 구조 리팩토링**:
    - **Spring Boot BOM 활용**: BOM(Bill of Materials)에서 관리하는 의존성(Jackson, JUnit 등)의 명시적 버전 선언을 제거하여 중복을 없애고 관리 효율을 높였습니다.
    - **모듈별 최적화**: `app-batch`, `adapter-redis` 등 각 모듈에서 불필요한 `spring-boot-starter-web` 같은 의존성을 제거하여 JAR 파일 크기를 줄이고 빌드 시간을 단축했습니다.
- **코드 스타일 일관성 확보**: `Spotless` 플러그인을 도입하여 `google-java-format` 기반으로 전체 코드 스타일을 통일했습니다.
- **테스트 코드 정비**: 의존성 마이그레이션을 위해 기존 테스트 코드를 전체 삭제했으며, 새로운 표준에 맞는 테스트 전략 수립이 필요합니다.

## 🏗️ 아키텍처

### 헥사고날 아키텍처 (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────────┐
│                        Applications                         │
│  ┌─────────────────┐              ┌─────────────────┐       │
│  │    app-api      │              │   app-batch     │       │
│  │   (REST API)    │              │    (batch)      │       │
│  └─────────────────┘              └─────────────────┘       │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                          Core                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │ core-domain │  │core-usecase │  │core-service │          │
│  │   (domain)  │  │  (usecase)  │  │  (service)  │          │
│  └─────────────┘  └─────────────┘  └─────────────┘          │
│              ┌─────────────┐                                │
│              │ core-port   │                                │
│              │  (port)     │                                │
│              └─────────────┘                                │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                        Adapters                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐  │
│  │  adapter-http   │  │adapter-persisten│  │adapter-redis│  │
│  │ (api sync )     │  │   (database)    │  │   (cache)   │  │
│  └─────────────────┘  └─────────────────┘  └─────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 📦 모듈 구조

### Applications
- **app-api**: REST API 애플리케이션 (Spring Boot Web)
- **app-batch**: 배치 처리 애플리케이션

### Core (비즈니스 로직)
- **core-domain**: 도메인 모델
- **core-usecase**: 유스케이스 인터페이스
- **core-service**: 비즈니스 로직 구현
- **core-port**: 포트 인터페이스 (외부 연동 정의)

### Adapters (외부 연동)
- **adapter-http**: 외부 API 연동 (TMDB API)
- **adapter-persistence**: 데이터베이스 연동 (MySQL, JPA)
- **adapter-redis**: 캐시 연동 (Redis)

### Frontend
- **stream-fix-frontend**: React 기반 프론트엔드

### Commons
- **stream-fix-commons**: 공통 모듈

## 🛠️ 기술 스택

### Backend
- **Java 21**
- **Spring Boot 3.5.0**
- **Spring Data JPA**
- **QueryDSL 5.1.0**
- **MySQL 8.0**
- **Redis**
- **Flyway** (DB 마이그레이션)
- **Spotless** (코드 포맷터)

### Frontend
- **React 19.1.0**
- **Create React App**

### Build Tools
- **Gradle 8.13**
- **Kotlin DSL**

### External APIs
- **TMDB (The Movie Database) API**

## 🚀 시작하기

### 사전 요구사항
- **Java 21** 이상
- Node.js 16 이상
- Docker & Docker Compose

### 1. 저장소 클론
```bash
git clone https://github.com/shokoku/stream-fix
cd stream-fix
```

### 2. 인프라 설정
```bash
# Docker로 MySQL, Redis 실행
cd infra
docker-compose up -d
```

### 3. API 설정 파일 생성
```bash
# env 환경 변수 설정
cp .env.sample .env
# .env 파일을 열어서 실제 값들을 입력하세요

# TMDB API 설정
cp stream-fix-adapters/adapter-http/src/main/resources/adapter-http-property-sample.yml \
   stream-fix-adapters/adapter-http/src/main/resources/adapter-http-property.yml
```

`adapter-http-property.yml` 파일에 TMDB API 키 설정:

```yaml
tmdb:
  auth:
    access-token: your-tmdb-access-token
  api:
    movie-lists:
      now-playing: https://api.themoviedb.org/3/movie/now_playing
```

### 4. 백엔드 실행
```bash
# 애플리케이션 빌드 및 실행
./gradlew :stream-fix-apps:app-api:bootRun
```

### 5. 프론트엔드 실행
```bash
cd stream-fix-frontend
npm install
npm start
```

## 🗄️ 데이터베이스

### 마이그레이션
Flyway를 사용하여 데이터베이스 스키마를 관리합니다.

- 마이그레이션 파일: `stream-fix-adapters/adapter-persistence/src/main/resources/flyway/`

## 🔧 개발 환경 설정

### IDE 설정
- IntelliJ IDEA 권장
- Lombok 플러그인 설치 필요

### 코드 포맷팅
프로젝트에는 Spotless 플러그인이 적용되어 있어, google-java-format을 따릅니다. 커밋 전 아래 명령을 실행하여 코드 스타일을 일관성 있게 유지해주세요.

```bash
./gradlew spotlessApply
```

## 📁 프로젝트 구조 상세

```
stream-fix/
├── buildSrc/                          # Gradle 빌드 설정
├── stream-fix-apps/                   # 애플리케이션 계층
│   ├── app-api/                       # REST API 애플리케이션
│   └── app-batch/                     # 배치 애플리케이션
├── stream-fix-core/                   # 핵심 비즈니스 로직
│   ├── core-domain/                   # 도메인 모델
│   ├── core-usecase/                  # 유스케이스 인터페이스
│   ├── core-service/                  # 비즈니스 로직 구현
│   └── core-port/                     # 포트 인터페이스
├── stream-fix-adapters/               # 어댑터 계층
│   ├── adapter-http/                  # HTTP 외부 연동
│   ├── adapter-persistence/           # 데이터베이스 연동
│   └── adapter-redis/                 # Redis 연동
├── stream-fix-frontend/               # React 프론트엔드
├── stream-fix-commons/                # 공통 모듈
└── infra/                            # 인프라 설정 (Docker)
```