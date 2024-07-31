<div align="center">
  <br>
  <h2> hey - backend </h2>
  <h1> hey 👋 </h1>
  <strong>서버 레포지토리</strong>
</div>
<br>

- [프로젝트 소개](#프로젝트-소개)
    * [프로젝트 기능](#프로젝트-기능)
- [기술 스택](#기술-스택)
- [서버 아키텍처](#서버-아키텍처)
- [ERD](#erd)
- [프로젝트 wiki](#프로젝트-wiki)
- [패키지 구조](#패키지-구조)
- [컨벤션과 협업 전략](#컨벤션과-협업-전략)
- [API 에러 코드](#api-에러-코드)


## 프로젝트 소개

각 플랫폼에 흩어져있는 공연(콘서트 및 페스티벌) 정보를 한눈에 볼 수 있는 서비스입니다. 공연 및 아티스트 알림 제공을 통해 공연을 놓치는 불편함을 해소하고자 합니다.


![프로젝트 소개](https://github.com/PJ-hey/hey-backend/assets/136677284/7193da23-5439-41ff-a7a2-0c178712c061)

### 프로젝트 기능

[서비스 세부 기능](/docs/service_detail.md)

## 기술 스택

- Java 22
- Gradle
- Spring Boot 3.3.1
- Spring Data JPA
- Spring Security
- QueryDSL
- PostgreSQL
- JUnit 5, Mockito
- AWS(ECS, RDS, S3)
- 협업 : Notion, Discord

## 서버 아키텍처

![서버 아키텍처](https://github.com/user-attachments/assets/dcb85251-1d65-46b1-a479-48cc071c1508)

## ERD

![ERD](https://github.com/user-attachments/assets/72b49d84-54a5-4f16-895c-ef0bf2a341c1)

## 프로젝트 wiki

프로젝트를 경험하면서 알게된 지식, 경험을 정리한 위키입니다.

![프로젝트 위키](https://few-monkey-6ee.notion.site/0ddf3035c24c44bfa31554458e89358e?v=77eea0b5d72944e5872fb96ec575d1af)

## 패키지 구조

[패키지 구조 설명](/docs/package_structure.md)

## Kopis, Spotify API 플로우

공연 정보를 생성하는 Kopis API, 아티스트 정보를 생성하는 Spotify API로 구성되어 있습니다.
![Kopis, Spotify API](https://github.com/user-attachments/assets/6f23513b-d658-493a-82af-04d84275a38a)

## 컨벤션과 협업 전략

어떤 컨벤션을 가지고 협업하였는 작성하였습니다. ( 로깅 컨벤션 포함 )

[컨벤션 & 협업 전략](docs/convention.md)

## API 에러 코드

[API 에러 코드](https://seeeeeeong.github.io/seong-blog/)

