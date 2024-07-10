# MunhwaSaenghwal - 문화생활

<br/>

## 개요


도파민 문고에 오신 것을 환영합니다. 도파민 많이 얻어가십쇼.

<br/>

## Team

**백승주** <a href="https://github.com/gnwsb"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white"></a>

**홍바다** <a href="https://github.com/BadaHong"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white"></a>

<br/>

## 기술 스택

**프론트엔드**

- **언어:** 코틀린
- **통합개발환경:** 안드로이드 스튜디오

**백엔드**

- **클라우드:** AWS EC2
- **서버:** Node.js
- **DB:** MySQL
- **통합개발환경:** VSCode, DBeaver

<br/>

## 미리보기



<br/>

## 설명

---

메인 화면은 크게 **사용자 프로필, 리뷰한 책, 읽을 책**으로 구성돼있습니다.

사용자 프로필에는 **프로필 사진, 이름, 한 줄 소개**가 전시되어있고, 이를 **수정**할 수 있습니다.

리뷰한 책 목록에는 가장 최근에 서평/별점을 남긴 6개의 책이 전시되어있습니다.

또한 리뷰한 책 목록을 누르면 **전체 목록**을 볼 수 있습니다.

읽은 책 목록의 경우 좌우로 탐색하여 전체 목록을 볼 수 있습니다.

메인 화면에서 오른쪽으로 넘기면 **검색 화면**이 있습니다.

책 표지를 누르면 **책 정보**를 볼 수 있고 **서평/별점**을 남기거나 읽을 책으로 등록할 수 있습니다.

그리고…

.

.

.

.

.

.

.

.

.

.

.

.

.

.

.

### 그리고 ……….

휴대폰을 흔들면 사용자는        **“  도파민  “**       탭으로 진입할 수 있습니다.

.

.

.

<img width="599" alt="db" src="[https://github.com/MunhwaSaenghwal/cultures/assets/110375619/5355a148-b5b8-4b31-9dbe-35711352a38d](https://github.com/MunhwaSaenghwal/cultures/assets/110375619/c66e552d-938c-4cab-912e-f313c20b3f9e)">


# **뭔 책이여 현대사회에 발 빠르게 따라갈려면 트렌드에 민감하고 릴스쇼츠틱톡인스타유튜브나무위키에 빠삭해야지**



https://github.com/MunhwaSaenghwal/cultures/assets/110375619/e4854810-8c1a-48ce-8730-583df26aa059



# 도파민 탭에서 유저는 유튜브 인기급상승 동영상, 구글 인기 검색어의 무한 리스트를 통해 무책임한 도파민을 공급받을 수 있습니다 영상 썸네일을 누르면 재생되고 검색어를 누르면 브라우저에서 검색 결과를 볼 수 있습니다

.

.

.


<br/>


## 기술 서술

---

### 프론트엔드

**카카오 SDK**를 사용해 회원가입, 로그인을 구현했습니다.

**네이버 검색 API**를 통해 책 검색 및 책 정보를 가져오는 기능을 구현했습니다.

**유튜브 API**를 통해 최근 인기 급상승 영상들을 가져와 RecyclerView에 전시했습니다.

### 백엔드

**AWS EC2** 인스턴스를 사용하여 **Node.js** 서버와 **MySQL** 데이터베이스를 연동하였습니다. 

서버가 닫혔을 때 데이터가 유실되지 않도록 **EBS 볼륨**을 부착하고 백업을 하였습니다.

→ MySQL 데이터베이스 구조:

<img width="599" alt="db" src="https://github.com/MunhwaSaenghwal/cultures/assets/110375619/5355a148-b5b8-4b31-9dbe-35711352a38d">


유저의 프로필 이미지를 저장하기 위해 **BLOB(Binary Large Object)** 형식, 

그 중에서도 용량이 큰 이미지를 다루기 위해 **MEDIUMBLOB**을 사용했습니다. 

프론트엔드에서 보내온 이미지 파일은 BLOB 형식으로 변환되고, 이것을 **Base64로 인코딩**해 데이터베이스에 저장합니다. 프론트엔드에서 이미지를 가져올 때는 역순으로 디코딩이 진행됩니다.

**구글 트렌드 API (pytrends)**를 가져오기 위해 서버 내 엔드포인트에 해당 API를 연동하였습니다.


<br/>
## Beta Version APK Link

https://drive.google.com/file/d/1Jty9aDXskRawk5cRtKbEJkJ3yCTKnx-_/view?usp=sharing
