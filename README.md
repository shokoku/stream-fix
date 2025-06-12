# StreamFix 🎬

헥사고날 아키텍처(Hexagonal Architecture) 패턴을 적용하여 설계된 스트리밍 서비스 플랫폼입니다. 이 문서는 신규 개발자가 프로젝트의 구조를 이해하고, 로컬 환경에서 프로젝트를 실행할 수 있도록 정확한 가이드를 제공하는 것을 목표로 합니다.

## 🏗️ 아키텍처

본 프로젝트는 애플리케이션의 핵심 비즈니스 로직(Core)과 외부 기술(Adapters)을 분리하는 헥사고날 아키텍처를 따릅니다. 이 구조는 유연하고 확장 가능하며, 유지보수가 용이한 시스템을 만드는 데 도움을 줍니다.

```
┌─────────────────────────────────────────────────────────────┐
│                        Applications                         │
│  ┌─────────────────┐              ┌─────────────────┐       │
│  │    app-api      │              │   app-batch     │       │
│  │   (REST API)    │              │    (Batch Job)  │       │
│  └─────────────────┘              └─────────────────┘       │
└─────────────────────────────────────────────────────────────┘
                              ▲
                              │ 의존성 방향
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                           Core                              │
│  ┌────────────────┐  ┌───────────────┐  ┌──────────────┐    │
│  │  core-domain   │<-│ core-service  │->│  core-port   │    │
│  │ (도메인 모델)     │  │ (비즈니스 로직)  │  │  (인터페이스)   │    │
│  └────────────────┘  └───────────────┘  └──────────────┘    │
│                          ▲                                  │
│                          │                                  │
│                     ┌──────────────┐                        │
│                     │ core-usecase │                        │
│                     │  (유스케이스)   │                        │
│                     └──────────────┘                        │
└─────────────────────────────────────────────────────────────┘
                              ▲
                              │ 의존성 방향
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                         Adapters                            │
│  ┌────────────────┐  ┌──────────────────┐  ┌───────────────┐│
│  │  adapter-http  │  │adapter-persistence│ │ adapter-redis ││
│  │ (외부 API 연동)  │  │   (DB 연동)        │  │   (캐시 연동)   ││
│  └────────────────┘  └──────────────────┘  └───────────────┘│
└─────────────────────────────────────────────────────────────┘
```

### 📦 모듈 구조

| 계층          | 모듈                      | 설명                                                                   |
| ------------- | ------------------------- | ---------------------------------------------------------------------- |
| **Applications** | `app-api`                 | REST API를 제공하는 Spring Boot 웹 애플리케이션입니다.                  |
|               | `app-batch`               | 데이터 마이그레이션 등 배치 작업을 처리하는 애플리케이션입니다.                |
| **Core** | `core-domain`             | 프로젝트의 핵심 도메인 모델과 비즈니스 규칙을 정의합니다.                          |
|               | `core-port`               | 외부 시스템(DB, API 등)과의 통신을 위한 인터페이스(Port)를 정의합니다.          |
|               | `core-service`            | 유스케이스의 실제 비즈니스 로직을 구현합니다.                                   |
|               | `core-usecase`            | 애플리케이션이 시스템에 어떤 기능을 제공해야 하는지 정의하는 유스케이스입니다.      |
| **Adapters** | `adapter-http`            | 외부 API(TMDB, Kakao)와의 통신을 구현하는 어댑터입니다.              |
|               | `adapter-persistence`     | 데이터베이스(MySQL, JPA) 연동을 처리하는 영속성 어댑터입니다.              |
|               | `adapter-redis`           | 캐싱 처리를 위한 Redis 연동 어댑터입니다.                            |
| **Commons** | `stream-fix-commons`      | 예외 처리 등 여러 모듈에서 공통으로 사용되는 코드를 포함합니다.                   |
| **Frontend** | `stream-fix-frontend`     | React 기반의 프론트엔드 애플리케이션입니다.                                      |
| **Infra** | `infra`                   | Docker Compose를 이용해 개발에 필요한 인프라(MySQL, Redis)를 설정합니다. |

## 🛠️ 기술 스택

### 백엔드 (Backend)
- **Java:** `21`
- **Spring Boot:** `3.5.0`
- **Spring Data JPA**
- **QueryDSL:** `5.1.0`
- **Gradle:** `8.13`
- **DB Migration:** Flyway
- **Database:** MySQL `8.0`
- **Cache:** Redis
- **Code Formatting:** Spotless (google-java-format)

### 프론트엔드 (Frontend)
- **React:** `19.1.0`
- **Axios**, **Bootstrap**

### 인프라 (Infrastructure)
- **Docker & Docker Compose**

*상세 버전 정보는 `versions.properties` 및 각 모듈의 `build.gradle.kts` 파일, `package.json` 파일을 참고하세요.*

## 🚀 실행 가이드

### 1. 사전 요구사항
- **Java 21**
- **Node.js 16** 이상
- **Docker & Docker Compose**

### 2. 저장소 클론 및 이동
```bash
git clone https://github.com/shokoku/stream-fix.git
cd stream-fix
```

### 3. 인프라 실행
프로젝트에 필요한 MySQL, Redis 데이터베이스를 Docker로 실행합니다.

```bash
cd infra
docker-compose up -d
```
> 🐳 Docker 컨테이너가 정상적으로 실행되었는지 확인하세요. (`docker ps`)

### 4. 환경 변수 설정
프로젝트 실행에 필요한 환경 변수를 설정합니다. 샘플 파일을 복사하여 실제 값을 입력하세요.

**1) 백엔드 환경변수 (.env)**
루트 경로의 `.env.sample` 파일을 `.env`로 복사한 후, 아래 항목들을 자신의 환경에 맞게 수정합니다.

```bash
# 1. .env 파일 생성
cp .env.sample .env

# 2. .env 파일 수정
vi .env
```
```.env
# .env
KAKAO_CLIENT_ID=your-kakao-client-id
KAKAO_CLIENT_SECRET=your-kakao-client-secret
KAKAO_REDIRECT_URL=http://localhost:3000/login/oauth2/code/kakao

JWT_SECRET=your-jwt-secret-key-that-is-long-enough-to-be-secure
```

**2) TMDB API Key 설정**
영화 데이터 마이그레이션에 필요한 TMDB API 키를 설정합니다.

```bash
# 1. adapter-http-property.yml 파일 생성
cp stream-fix-adapters/adapter-http/src/main/resources/adapter-http-property-sample.yml \
   stream-fix-adapters/adapter-http/src/main/resources/adapter-http-property.yml

# 2. adapter-http-property.yml 파일 수정
vi stream-fix-adapters/adapter-http/src/main/resources/adapter-http-property.yml
```
```yaml
# adapter-http-property.yml
tmdb:
  auth:
    access-token: your-tmdb-access-token
```

### 5. 백엔드 실행
IntelliJ IDEA와 같은 IDE에서 `StreamFixApplication.java`를 직접 실행하거나, 터미널에서 Gradle 명령어를 사용합니다.

```bash
# API 애플리케이션 실행
./gradlew :stream-fix-apps:app-api:bootRun
```
> ☕ 최초 실행 시 의존성을 다운로드하므로 시간이 다소 소요될 수 있습니다.

### 6. 프론트엔드 실행
별도의 터미널을 열어 프론트엔드 개발 서버를 실행합니다.

```bash
# 프론트엔드 폴더로 이동
cd stream-fix-frontend

# 의존성 설치
npm install

# 개발 서버 실행
npm start
```
- 프론트엔드 서버는 `http://localhost:3000` 에서 실행됩니다.

### 7. (선택) 배치 애플리케이션 실행
TMDB에서 영화 정보를 가져와 데이터베이스에 저장하는 배치 작업을 실행할 수 있습니다.

```bash
# app-batch 모듈 실행
./gradlew :stream-fix-apps:app-batch:bootRun
```

## 📋 API 엔드포인트

| Method | URL                                        | 설명                                  | 인증 필요 |
| :----- | :----------------------------------------- | :------------------------------------ | :-------- |
| `POST` | `/api/v1/user/register`                    | 일반 회원가입을 진행합니다.           | No        |
| `POST` | `/api/v1/user/login`                       | 이메일과 비밀번호로 로그인합니다.     | No        |
| `POST` | `/api/v1/user/callback`                    | 카카오 OAuth 인증 콜백을 처리합니다.  | No        |
| `GET`  | `/api/v1/movie/client/{page}`              | 외부(TMDB)에서 영화 목록을 조회합니다.| Yes       |
| `POST` | `/api/v1/movie/search`                     | DB에 저장된 영화 목록을 조회합니다.     | Yes       |
| `POST` | `/api/v1/movie/{movieId}/download`         | 특정 영화를 다운로드합니다.           | Yes       |
| `POST` | `/api/v1/movie/{movieId}/like`             | 특정 영화에 '좋아요'를 표시합니다.    | Yes       |
| `GET`  | `/api/v1/sample`                           | 샘플 API를 호출합니다.                | Yes       |

## 🔧 개발 규칙 및 가이드

### 코드 포맷팅
프로젝트에는 `Spotless` 플러그인이 적용되어 있어, 커밋 전에 코드를 `google-java-format` 스타일에 맞게 정리해야 합니다. 아래 명령을 실행하여 코드 스타일을 적용하세요.

```bash
./gradlew spotlessApply
```

### Git 브랜치 전략
`Git-Flow` 또는 `GitHub Flow`와 같은 브랜치 전략을 따르는 것을 권장합니다. 브랜치 이름은 목적을 명확히 나타내도록 작성합니다.
- **feature**: `feature/기능-이름` (예: `feature/login-api`)
- **bugfix**: `bugfix/버그-수정-내용` (예: `bugfix/fix-movie-like-error`)
- **docs**: `docs/문서-업데이트-내용` (예: `docs/update-readme`)
- **refactor**: `refactor/리팩토링-내용`

## ❓ 트러블슈팅 가이드

- **Q: `bootRun` 실행 시 `Could not find...` 에러가 발생해요.**
  - **A:** Gradle 의존성을 제대로 다운로드하지 못했을 수 있습니다. IDE의 Gradle 탭에서 'Reload All Gradle Projects'를 실행하거나, 터미널에서 `./gradlew clean build`를 실행해 보세요.

- **Q: 카카오 로그인 시 리다이렉트 URI 오류가 발생해요.**
  - **A:** 카카오 개발자 콘솔에 등록된 `Redirect URI`와 `.env` 파일의 `KAKAO_REDIRECT_URL`이 일치하는지 확인하세요. (기본값: `http://localhost:3000/login/oauth2/code/kakao`)

- **Q: 포트 충돌(Port already in use)이 발생해요.**
  - **A:** 백엔드(8080), 프론트엔드(3000), MySQL(3306), Redis(6379) 포트가 다른 프로세스에서 사용 중인지 확인하고, 필요 시 해당 프로세스를 종료하거나 `application.yml`, `docker-compose.yml` 파일에서 포트를 변경하세요.

- **Q: 프론트엔드에서 API 호출 시 403 Forbidden 에러가 발생해요.**
  - **A:** 로그인 후 받은 JWT 토큰이 HTTP 요청 헤더의 `Authorization`에 `Bearer {token}` 형식으로 올바르게 포함되었는지 확인하세요.