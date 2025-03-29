### Say Anything Party API Server
[아무 말 대잔치 커뮤니티](https://github.com/juintination/say-anything-party)를 위한 백엔드 API 서버

### Entity Relationship Diagram
- ErdCloud로 [ERD](https://www.erdcloud.com/d/jnEJjaPDkS2TSJryZ)를 작성하였습니다.

### API Documentation
- [API 문서](https://documenter.getpostman.com/view/32366655/2sAYkAQ2SV)는 Postman으로 작성하였습니다.
  - [API 설계서](https://docs.google.com/spreadsheets/d/1XtaprZiO0qpKhq4kh3CNOoHPTgc19yCJOXGnPUA-X58/edit?gid=1878554884#gid=1878554884)는 Google Spreadsheets로 작성하였습니다.
- 프로젝트가 실행 중일 때 기본 URL을 통해 문서에 접근할 수 있습니다.

### 주요 기능

#### JWT 기반 인증
- **인증 및 인가** Spring Security와 JWT(Json Web Token)를 활용하여 사용자의 인증 및 인가를 처리합니다.
  - JWT를 통해 stateless한 인증 방식을 구현함으로써, 서버의 부하를 줄이고 확장성을 높였습니다.

#### 데이터베이스 설계
- **Member, Post, Comment, PostLike 테이블**
  - **Member:** 사용자 정보를 저장하며, 인증 및 인가에 필요한 정보를 포함합니다.
  - **Post:** 사용자가 작성한 게시글 정보를 관리하며, 다양한 연관 관계를 통해 댓글, 좋아요 등의 데이터를 연계합니다.
  - **Comment:** 게시글에 달린 댓글 정보를 저장하며, 사용자의 피드백 및 소통 기능을 지원합니다.
  - **PostLike:** 게시글에 대한 좋아요 정보를 관리하여, 사용자의 참여도를 측정하고 콘텐츠의 인기도를 분석할 수 있도록 합니다.

#### 레포지토리 최적화
- 각 엔티티에 대한 레포지토리에서는 **fetch join**을 활용한 커스텀 메서드를 제공하여, 연관 관계에 있는 데이터를 한 번의 쿼리로 효율적으로 조회할 수 있도록 하였습니다.  
  - 이를 통해 N+1 문제를 해결하고 성능을 최적화하였습니다.

#### 테스트 코드 작성
- 모든 주요 기능 및 데이터 접근 레이어에 대해 테스트 코드를 작성하여, 코드의 안정성과 신뢰성을 보장합니다.
- 테스트 환경에서는 H2 데이터베이스 등을 활용하여 실제 운영 환경과 유사하게 테스트를 진행하며, 각 기능이 의도한 대로 동작하는지 검증하였습니다.

### 사용한 기술 스택
- Language
  - JAVA 21
- Framework
  - Spring boot 3.4.3
- Dependency Management
  - Gradle
- Security
  - Spring Security
  - JWT
- Database
  - MySQL
  - H2 (for testing)
- ORM
  - JPA
- Utilities
  - Lombok
  - Gson
  - JavaFaker
  - Thumbnailator 0.4.19
- Test
  - JUnit

### 사전 요구 사항
프로젝트를 실행하기 전에 다음과 같은 설정 변경이 필요합니다:
- Database Configuration
  - application.properties와 build.gradle 파일에서 데이터베이스 관련 설정을 수정해야 합니다.

### 설정 방법
- build.gradle
  - Database configuration:
  ```
  dependencies {
      runtimeOnly 'com.mysql:mysql-connector-j' // Replace with your_database_driver if necessary
  }
  ```
- application.properties
  - Database configuration:
  ```
  spring.datasource.driver-class-name=your_database_driver_class_name
  spring.datasource.url=jdbc:your_database_type://your_database_url:your_port/your_database_name
  spring.datasource.username=your_database_username
  spring.datasource.password=your_database_password
  ```

### 회고
전반적인 코드 품질과 유지보수성을 높이기 위해 다양한 개선 작업을 진행하였습니다. 전반적인 설계와 구현 과정에서 여러 개선점을 체감할 수 있었으며 주요 경험과 앞으로의 방향은 다음과 같습니다.

- **어노테이션과 네이밍 일관성 강화**
  - 프로젝트 규모가 커질 것을 대비하여, 포괄적으로 사용되는 어노테이션 대신 필요한 어노테이션만을 명시적으로 적용하고 네이밍 규칙을 일관되게 유지하는 것이 얼마나 중요한지 알게 되었습니다.
    - 이를 통해 코드의 가독성과 유지보수성을 크게 향상시킬 수 있었습니다.
- **DTO 설계 및 데이터 유효성 검증**
  - DTO를 요청(Request)과 응답(Response) DTO로 구분하여 데이터 전송을 명확하게 하였습니다.
    - 특히, Request 관련 DTO에는 `@NotNull`, `@NotBlank` 등의 유효성 검사용 어노테이션을 추가하여 컨트롤러에서 `@Valid` 어노테이션을 활용한 데이터 유효성 검증을 수행함으로써 클라이언트와의 통신 신뢰성을 강화하였습니다.
- **하드코딩된 값 제거**  
  - 특정 부분에 하드코딩된 값들을 `application.properties`로 분리하여 관리하도록 개선하여, 유지보수가 용이해졌습니다.
    - `@Value` 어노테이션을 활용하여 필요한 설정 값을 주입받았으며, 스프링 빈으로 관리되지 않는 곳에도 `@AllArgsConstructor` 어노테이션을 사용한 생성자 주입을 통해 해당 값들을 적용하였습니다.
- **트랜잭션 관리 개선**
  - 읽기 전용 DB 연산에 대해 `@Transactional(readOnly = true)` 옵션을 적용하여 데이터 접근의 일관성과 성능 최적화를 도모하였습니다.
- **테스트 코드 가독성 향상**
  - 테스트 코드에 `@DisplayName`과 `@Nested` 어노테이션을 적극 활용하여, 한글로 작성된 명확한 설명을 추가함으로써 테스트 코드의 가독성과 이해도를 개선하였습니다.
- **API 엔드포인트 개선**
  - API 엔드포인트의 계층 구조를 재검토하여 보다 직관적이고 RESTful한 URL 구조로 수정하였습니다.
    - 이를 통해 클라이언트와의 통신 시 명확한 경로 설계가 가능해졌으며, 유지보수와 확장성 측면에서도 유리한 구성을 갖추게 되었습니다.

또한, fetch join을 통해 연관 데이터를 효율적으로 조회하더라도, 특정 연관 정보(예: 게시글 좋아요)의 경우 불필요하게 많은 데이터를 로드하여 성능 저하가 발생할 수 있음을 인지하였습니다. 이를 개선하기 위해 게시글 엔티티에 좋아요 수를 저장하는 필드를 도입할 예정이며, 동시성 환경에서의 데이터 정합성을 확보하기 위한 동시성 테스트도 진행할 계획입니다.

이번 과제를 통해 얻은 다양한 인사이트와 개선 경험을 바탕으로, 앞으로의 프로젝트에서도 보다 견고하고 효율적인 시스템을 구축하기 위해 지속적으로 노력할 것입니다.
