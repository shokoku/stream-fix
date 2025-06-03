# StreamFix 🎬

Spring Security 학습을 위한 구독 기반 스트리밍 서비스 프로젝트

## 📋 프로젝트 개요

StreamFix는 헥사고날 아키텍처(Hexagonal Architecture) 패턴을 적용하여 설계된 스트리밍 서비스 플랫폼입니다. 
모듈화된 구조로 개발되어 유지보수성과 확장성을 고려했습니다.

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
│  │ (api sync )     │  │   (database)    │  │   (cash)    │  │ 
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
- **Java 17**
- **Spring Boot 3.3.3**
- **Spring Data JPA**
- **QueryDSL 5.0.0**
- **MySQL 8.0**
- **Redis**
- **Flyway** (DB 마이그레이션)

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
- Java 17 이상
- Node.js 16 이상
- Docker & Docker Compose

### 1. 저장소 클론
```bash
git clone [repository-url]
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

## 📚 API 문서

### 주요 엔드포인트

#### 영화 API
- `GET /api/v1/movie/client/{page}` - 현재 상영 중인 영화 목록 조회

#### 샘플 API
- `GET /api/v1/sample` - 샘플 데이터 조회

### 테스트 요청
```bash
# 샘플 API 테스트
curl http://localhost:8080/api/v1/sample

# 영화 API 테스트
curl http://localhost:8080/api/v1/movie/client/1
```

## 🗄️ 데이터베이스

### 테이블 구조
- **users**: 사용자 정보
- **sample**: 샘플 데이터

### 마이그레이션
Flyway를 사용하여 데이터베이스 스키마를 관리합니다.
- 마이그레이션 파일: `stream-fix-adapters/adapter-persistence/src/main/resources/flyway/`

## 🔧 개발 환경 설정

### IDE 설정
- IntelliJ IDEA 권장
- Lombok 플러그인 설치 필요

### 코드 포맷팅
프로젝트에 설정된 포맷팅 규칙을 따릅니다.

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

## 🌟 주요 특징

1. **헥사고날 아키텍처**: 비즈니스 로직과 외부 연동의 명확한 분리
2. **모듈화**: 관심사의 분리와 의존성 관리
3. **테스트 가능한 구조**: 포트/어댑터 패턴으로 모킹 가능
4. **확장 가능한 설계**: 새로운 어댑터 추가 용이


## 📄 라이선스

이 프로젝트는 학습 목적으로 만들어졌습니다.
