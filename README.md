<img width="933" alt="image" src="https://github.com/kimjiyooniiiii/BravePeople-Backend/assets/117561820/ea00a479-2b1a-4140-a999-69bf6ecf542f">



## ✔ 개발 목표
일상에는 누군가에게는 사소하지만, 누군가에게는 어려운 문제들이 있습니다. **용감한 원정대**는 이런 문제들을 내 이웃에게 부탁할 수 있는 **지역 커뮤니티 플랫폼** 입니다. 간편하게 게시글을 올리고, 채팅을 통해 소통하며 **원정대 또는 의뢰인**이 되어 문제를 해결할 수 있습니다.

<br>

### 🕐 개발 기간 : 2023.12.21 ~ 2024.02.29 (2개월)

<br>

### 👥 개발 인원 (5명)
- #### Back-End : &nbsp; 김지윤(PM),&nbsp; 임태호, &nbsp; 유나영 
- #### Front-End : &nbsp; 김한수,&nbsp; 장현지

<br><br>

## 👨‍💻 맡은 역할
### 김지윤 (PM, Back-End Development) <a href = "https://github.com/kimjiyooniiiii"><img alt="GitHub" width="70px" src ="https://img.shields.io/badge/GitHub-181717.svg?&style=for-the-badge&logo=GitHub&logoColor=white"/></a>
-	빠른 개발과 이슈 대응을 위해 Server CI/CD 구축 [ Details ](https://sky-pail-416.notion.site/Server-CI-CD-4251af0cbc194183afa67dd1242cf9d7?pvs=4)
-	Back-End 배포 (AWS EC2),  Front-End 배포 (AWS CloudFront)
-	Sever의 부담이 적고, Rest 서비스가 가능한 JWT Token을 사용하여 사용자 인증, 인가 구현  [ Details ](https://sky-pail-416.notion.site/JWT-Token-274ba219cf694dc79a4a071cc4e4ece1?pvs=4)
-	실시간 채팅 기능 개발을 위해 양방향 통신 프로토콜인 Web Socket, STOMP를 사용하여 채팅 서비스 개발
-	프로젝트 전반적인 예외 관리와 Client에 전송할 에러 메시지 규격화를 위해 프로젝트 전역에서 사용하는 예외처리 Class 개발
-	서버 로그를 팀원들과 공유하고 로그를 빠르게 추적하기 위해 Logback 설정 및 AWS CloudWatch로 로그 전송, 관리
-	사용자의 이미지 업로드 서비스 제공을 위해 API 개발 및 AWS S3에 정적 이미지 파일 배포
-	그외 회원가입, 로그인, 토큰 재발급, 로그아웃, 채팅방 나가기 API 개발

<br>

### 임태호 (Back-End Development) <a href = "https://github.com/taeho99"><img alt="GitHub" width="70px" src ="https://img.shields.io/badge/GitHub-181717.svg?&style=for-the-badge&logo=GitHub&logoColor=white"/></a>
-
-

<br>

### 유나영 (Back-End Development) <a href = "https://github.com/fjeos"><img alt="GitHub" width="70px" src ="https://img.shields.io/badge/GitHub-181717.svg?&style=for-the-badge&logo=GitHub&logoColor=white"/></a>
-	SSE(Server-Sent Events) 단방향 통신을 사용하여 Client에 채팅 메시지, 의뢰 관련 알림 전송
-	채팅 메시지 DB로 사용하는 MongoDB를 AWS EC2에 원격 개발 환경 구축
-	의뢰 기능 핵심 API 개발 (의뢰 생성, 수락, 취소)
-	게시판 기능 CRUD 개발 (작성, 수정, 삭제)
-	회원 서비스 개발 (아이디 찾기, 위치 재설정, 비밀번호 인증, 재설정, 마이페이지 수정)

<br><br>

## ⚙ Tech Stack

#### ◾ Security 　　　　　　　　　      ◾ DB 　　　　　　　　　　     ◾ Front-End
<img height="25px" src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/JWT-CC6699?style=for-the-badge">  　　　　　  <img height="25px" src="https://img.shields.io/badge/MySQL8-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white"> 　　　　　   <img height="25px" src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=white">
#### ◾ Development
<img height="25px" src="https://img.shields.io/badge/java 17-CC6699?style=for-the-badge"> <img height="25px" src="https://img.shields.io/badge/Spring boot 3.2.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/Spring data JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/Spring data MongoDB-6DB33F?style=for-the-badge&logo=mongodb&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/Stomp, Web Socket 2.3.4-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/SSE (Server sent Events)-CC6699?style=for-the-badge">
#### ◾ Deploy
<img height="25px" src="https://img.shields.io/badge/Git Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/AWS Code Deploy-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/AWS S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/AWS EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/Route53-8C4FFF?style=for-the-badge&logo=amazonroute53&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/AWS CloudFront-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/ACM-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/Load Balancer-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/AWS RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"> 
#### ◾ Communication                                  
<img height="25px" src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/Git hub-181717?style=for-the-badge&logo=github&logoColor=white">  <img height="25px" src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/ERD Cloud-CC6699?style=for-the-badge"> <img height="25px" src="https://img.shields.io/badge/Google Sheet-34A853?style=for-the-badge&logo=googlesheets&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white">
#### ◾ ISSUE
<img height="25px" src="https://img.shields.io/badge/AWS Cloud Watch-FF4F8B?style=for-the-badge&logo=amazoncloudwatch&logoColor=white"> <img height="25px" src="https://img.shields.io/badge/Git Issues-181717?style=for-the-badge&logo=github&logoColor=white">

<br><br>

# 📖 프로젝트 내용
## ✔ Server 배포
- 빠른 개발과 이슈 대응을 위해 Git Actions, AWS CodeDeploy로 Server CI/CD 구축
- DB의 원격 개발 환경 구축을 위해 AWS RDS에 MySQL 설치, EC2에 MongoDB 설치
- https 보안 프로토콜 사용

<img width="700" alt="image" src="https://github.com/kimjiyooniiiii/BravePeople-Backend/assets/117561820/3d529287-c547-4a47-8a76-fe2850c4a77d">

<br>

<img width="800" alt="image" src="https://github.com/kimjiyooniiiii/BravePeople-Backend/assets/117561820/1633d1eb-f3c2-418b-9501-9af70a46040a">
  <br>
&nbsp;&nbsp;(* 현재 IPv4 주소 사용이 유료화 되어 배포 서버는 유지하지 않고 있습니다.)

<br><br><br>

## ✔ Client 배포
-	정적 콘텐츠를 안전하게 배포하기 위해, https를 지원하는 AWS CloudFront로 Front-End 배포
-	Route53

<img width="362" alt="image" src="https://github.com/kimjiyooniiiii/BravePeople-Backend/assets/117561820/bec8cdb5-b5a1-4693-af0b-81f2f4dd42e6">


<br>

<img width="600" alt="image" src="https://github.com/kimjiyooniiiii/BravePeople-Backend/assets/117561820/ba060c3b-26fb-4c45-b86d-03d043170756"> <br>
&nbsp;&nbsp;(* 현재 IPv4 주소 사용이 유료화 되어 배포 서버는 유지하지 않고 있습니다.)

<br><br><br>

## ✔ Database
-	Project의 주 개체인 회원, 게시글, 의뢰, 채팅방의 관계를 정의하기 위해 RDBMS인 MySQL을 DB로 사용
<img width="700" alt="image" src="https://github.com/kimjiyooniiiii/BravePeople-Backend/assets/117561820/08134f10-8c1c-4c0a-aec8-7a2edb4f7b6a">

<br><br>

-	채팅 메시지는 빈번한 DB 접근과 빠른 검색 속도가 필요해 NoSQL인 MongoDB를 DB로 사용

<img width="371" alt="image" src="https://github.com/kimjiyooniiiii/BravePeople-Backend/assets/117561820/e54d74aa-677c-4e9d-8549-cbf061fc2946">

<br><br><br>

## ✔ Package Structure
<img width="800" alt="image" src="https://github.com/kimjiyooniiiii/BravePeople-Backend/assets/117561820/31f3cbad-289c-44d7-81d5-1365f617a28c"> <br>

### 👉 API 명세서 : [Notion 링크](https://sky-pail-416.notion.site/c2232e24e44849b48d04b0418ead7dd2?v=e4328a4e469440cdb0d48f8a1704c176&pvs=4)

<br><br><br>

## ✔ 프로젝트 실행 영상
### 📺 회원 기능

### 📺 게시판 기능

### 📺 채팅 기능
