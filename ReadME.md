<div align=center><span style='center'><h3> 2022년 인천대학교 졸업작품 발표회에 출품된 작품입니다.</h3></span></div><br>

<div align=center><img src="https://img.shields.io/badge/IntelliJ-000000?style=flat&logo=IntelliJ IDEA&logoColor=white"/> <img src="https://img.shields.io/badge/Spring-6DB33F?style=flat&logo=Spring Boot&logoColor=white"/> <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=Spring Boot&logoColor=white"/> <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat&logo=Spring Security&logoColor=white"> <img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=flat&logo=Databricks&logoColor=white"> <img src="https://img.shields.io/badge/Java-6DB33F?style=flat&logo=JAVA&logoColor=white"> <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=MySQL&logoColor=white"></div>

<br>

<details>
<summary> APP 소개 </summary>

<br>

![inunavi](https://user-images.githubusercontent.com/85429793/235772684-2e480f82-1a84-4792-a270-5609f6b9bb39.png)

<div align=center> 서비스 기간 : 2022.03.02 ~ 2022.12.29
 
<br><br>
[playstore](https://play.google.com/store/apps/details?id=com.maru.inunavi)
<br>

<b> 캠퍼스 지리에 익숙치 않은 신입생 및 복학생을 위한 안드로이드 교내 지도 어플. </b><br>
<b> 서버단을 작성하였으며 9개월 간 약 400명의 이용자에게 서비스를 제공하였습니다.</b>
</div>

</details>

<details>
<summary> 수상 관련 </summary>

<br>

![KakaoTalk_20230503_043420577_06](https://user-images.githubusercontent.com/85429793/235777867-efeba544-5417-47da-9a67-27cdd1d2313c.jpg)

- [대회 정보](https://www.inu.ac.kr/user/indexSub.do?codyMenuSeq=1477369&siteId=isis&dum=dum&boardId=490566&page=1&command=albumView&boardSeq=681579&chkBoxSeq=&categoryId=&categoryDepth=)<br>

</details>

<br>

## 구현 API

<details>
 <summary><code>회원가입</code> <code>POST</code> <code><b>/user/insert</b></code></summary>
 
<br>

> ### Parameters
> ```java
> email : 이메일
> password : 비밀번호
> major : 전공
>```
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "true",
>    "email": "liardanc3@gmail.com"
>}
> ```
> </details>
>
> <details><summary>failure</summary> 
> <br>
>
> ```java
> HTTP/1.1 500 Internal Server Error
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "false",
>    "email": "liardanc3@gmail.com"
>}
> ```
> </details>
>
> ### Run example
>
> ![insert](https://github.com/liardanc3/inunavi/assets/85429793/2709b32c-193a-4e82-a769-2f66f7bcbd8f)
></details>
></details>

<details>
 <summary><code>유저 강의 조회</code> <code>POST</code> <code><b>/user/select/class</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "response": [
>        {
>            "id": "799",
>            "department": "컴퓨터공학부",
>            "grade": "3",
>            "category": "전공필수",
>            "number": "IAA6021002",
>            "lectureName": "컴퓨터네트워크",
>            "professor": "---",
>            "classRoomRaw": "제7호관 정보기술대학-504 강의실(중)-1[SH504]",
>            "classTimeRaw": "\" [SH504:화(2B-3),금(7-8A)]\"",
>            "classRoom": "SH504,SH504,",
>            "classTime": "69-71,222-224,",
>            "how": "-",
>            "point": "3",
>            "formattedTime": "화 10:30 - 12:00, 금 15:00 - 17:00"
>        }
>    ]
>}
> ```
> </details>
>
> <details><summary>failure</summary> 
> <br>
>
> ```java
> HTTP/1.1 500 Internal Server Error
> Content-Type: application/json;charset=UTF-8
>
>{
>    "response": []
>}
> ```
></details>
>
>
> ### Run example
>
>![select-class](https://github.com/liardanc3/inunavi/assets/85429793/e73408a4-a6b8-426e-81e0-44648c36b0db)
> </details>
> </details>

<details>
 <summary><code>아이디 중복 체크</code> <code>POST</code> <code><b>/user/check/id={id}</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "true",
>    "email": "liardanc3@gmail.com"
>}
> ```
> </details>
>
> ### Run example
>
> ![user-check-id](https://github.com/liardanc3/inunavi/assets/85429793/ebbc7813-15a0-4aa4-bfe4-072452820ac2)
> </details>
> </details>

<details>
 <summary><code>로그인</code> <code>POST</code> <code><b>/user/login</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
> password : 비밀번호
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "true",
>    "email": "liardanc3@gmail.com"
>}
> ```
> </details>
>
> <details><summary>failure</summary> 
> <br>
>
> ```java
> HTTP/1.1 500 Internal Server Error
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "false",
>    "email": "liardanc3@gmail.com",
>    "message": "로그인 실패"
>}
> ```
></details>
>
>
> ### Run example
>
> ![login](https://github.com/liardanc3/inunavi/assets/85429793/4cad3f58-b8ec-4c6e-bb4f-34ea9474e3a1)
> </details>
> </details>

<details>
 <summary><code>이메일 인증</code> <code>POST</code> <code><b>/user/verify</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "true",
>    "code": "4b52bb9f"
>}
> ```
> </details>
>
> <details><summary>failure</summary> 
> <br>
>
> ```java
> HTTP/1.1 500 Internal Server Error
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "false",
>    "message": "Unauthorized Access"
>}
> ```
></details>
>
>
> ### Run example
>
> ![verify](https://github.com/liardanc3/inunavi/assets/85429793/cf80d0e8-16e7-43c9-9e8a-92dc5b2f23ae)
> </details>
> </details>

<details>
 <summary><code>비밀번호 변경</code> <code>POST</code> <code><b>/user/update</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
> newPassword : 새로운 비밀번호
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "true",
>    "email": "test@gmail.com"
>}
> ```
> </details>
>
> <details><summary>failure</summary> 
> <br>
>
> ```java
> HTTP/1.1 500 Internal Server Error
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "false",
>    "email": "not_present_email@no.com"
>}
> ```
></details>
>
>
> ### Run example
>
> ![updatepassword](https://github.com/liardanc3/inunavi/assets/85429793/8937e6a8-752e-4ac9-aa7e-1da877126fad)
> </details>
> </details>

<details>
 <summary><code>전공 업데이트</code> <code>POST</code> <code><b>/user/update2</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
> newMajor : 새로운 전공
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "true",
>    "email": "liardanc3@gmail.com"
>}
> ```
> </details>
>
> <details><summary>failure</summary> 
> <br>
>
> ```java
> HTTP/1.1 500 Internal Server Error
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "false",
>    "email": "not_present_email@no.com"
>}
> ```
></details>
>
>
> ### Run example
>
> ![update2](https://github.com/liardanc3/inunavi/assets/85429793/636de0e0-d206-4a1d-b640-bc9b4d624bde)
> </details>
> </details>

<details>
 <summary><code>회원 탈퇴</code> <code>POST</code> <code><b>/user/quit</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
> password : 비밀번호
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "true",
>    "email": "test@gmail.com"
>}
> ```
> </details>
>
> <details><summary>failure</summary> 
> <br>
>
> ```java
> HTTP/1.1 500 Internal Server Error
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "false",
>    "email": "not_present_email@no.com"
>}
> ```
></details>
>
>
> ### Run example
>
> ![quit](https://github.com/liardanc3/inunavi/assets/85429793/d413784f-d4a5-480d-8292-43a94e5d78ac)
> </details>
> </details>

<details>
 <summary><code>강의 추가</code> <code>POST</code> <code><b>/user/insert/class</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
> class_id : 강좌번호
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "true",
>    "email": "test@gmail.com"
>}
> ```
> </details>
>
> <details><summary>failure</summary> 
> <br>
>
> ```java
> HTTP/1.1 500 Internal Server Error
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "false",
>    "email": "test@gmail.com"
>}
> ```
></details>
>
>
> ### Run example
>
> ![insert-class](https://github.com/liardanc3/inunavi/assets/85429793/06515652-a462-46bd-b299-b6ac2122142b)
> </details>
> </details>

<details>
 <summary><code>강의 삭제</code> <code>POST</code> <code><b>/user/delete/class</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
> class_id : 강의번호
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "true",
>    "email": "test@gmail.com"
>}
> ```
> </details>
>
> <details><summary>failure</summary> 
> <br>
>
> ```java
> HTTP/1.1 500 Internal Server Error
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "false",
>    "email": "test@gmail.com"
>}
> ```
></details>
>
>
> ### Run example
>
> ![delete-class](https://github.com/liardanc3/inunavi/assets/85429793/39413b70-5ba4-494c-b424-47be04716d6a)
> </details>
> </details>

<details>
 <summary><code>시간표 정보</code> <code>GET</code> <code><b>/getTimeTableInfo</b></code> </summary>

<br>

> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "response": {
>        "year": "2023",
>        "semester": "여름",
>        "majorArrayString": "국어국문학과,영어영문학과,독어독문학과,불어불문학과,일어일문학과,중어중국학과,수학과,물리학과,화학과,소비자ㆍ아동학과,패션산업학과,해양학과,사회복지학과,신문방송학과,문헌정보학과,창의인재개발학과,행정학과,정치외교학과,경제학과,경제학과(야),무역학부,무역학부(야),소비자학과,기계공학과,기계공학과(야),메카트로닉스공학과,전기공학과,전자공학과,전자공학과(야),산업경영공학과,산업경영공학과(야),안전공학과,신소재공학과,에너지화학공학과,컴퓨터공학부,컴퓨터공학부(야),정보통신공학과,임베디드시스템공학과,경영학부,세무회계학과,조형예술학부,한국화전공,서양화전공,디자인학부,공연예술학과,체육학부,운동건강학부,국어교육과,영어교육과,일어교육과,수학교육과,체육교육과,유아교육과,역사교육과,윤리교육과,도시행정학과,도시건축학부,건축공학전공,도시건축학전공,도시공학과,도시환경공학부,건설환경공학전공,환경공학전공,생명과학부,생명과학전공,분자의생명전공,생명공학부,생명공학전공,나노바이오전공,동북아국제통상학부,한국통상전공,법학부,광전자공학전공(연계),물류학전공(연계),인공지능소프트웨어연계전공,창의적디자인연계전공,뷰티산업연계전공,인문문화예술기획연계전공,소셜데이터사이언스연계전공,전체",
>        "CSEArrayString": "대학영어2,Academic English,컴퓨팅적사고와SW,글쓰기이론과실제,대학영어회화2,전체",
>        "categoryListString": "전공기초,전공선택,전공필수,교양필수,기초과학,교양선택,교직,일반선택,군사학,전체"
>    }
>}
> ```
> </details>
> </details>

<details>
 <summary><code>강의 검색</code> <code>GET</code> <code><b>/selectLecture</b></code> </summary>

<br>

> ### Parameters
> ```java
> main_keyword : 검색 단어
> keyword_option : 검색 옵션(과목명, 교수명)
> major_option : 전공 옵션
> cse_option : 교양필수 옵션
> sort_option : 정렬 기준(기본, 과목코드, 과목명)
> grade_option : 학년
> category_option : 카테고리 옵션(교양, 전공, 일반선택 등)
> score_option : 학점
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "response": [
>        {
>            "id": 767,
>            "department": "컴퓨터공학부",
>            "grade": "1",
>            "category": "교양필수",
>            "number": "XAA1359031",
>            "professor": "XXX",
>            "classRoomRaw": "제7호관 정보기술대학-304 강의실(대)-계단식[SH304]",
>            "classTimeRaw": "\" [SH304:화(2)(3),목(6)]\"",
>            "classRoom": "SH304,SH304",
>            "classTime": "68-71,172-173,",
>            "how": "-",
>            "point": "3",
>            "realTime": "화 10:00 - 12:00, 목 14:00 - 15:00",
>            "lecturename": "대학수학(2)"
>        }
>     ]
>}
> ```
> </details>
>
>
>
> ### Run example
>
> ![selectlecture](https://github.com/liardanc3/inunavi/assets/85429793/52e82a68-a1f4-40f7-9767-f89150d5c539)
> </details>
> </details>

<details>
 <summary><code>경로 묻기</code> <code>GET</code> <code><b>/getRootLive</b></code> </summary>

<br>

> ### Parameters
> ```java
> startPlaceCode : 시작 장소 식별값
> startLocation : 시작 장소 좌표
> endPlaceCode : 도착 장소 식별값
> endLocation : 도착 장소 좌표
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>   "response" : [
>       {
>           "id":269,
>           "query":"LOCATION,37.37696025287909,126.63286875933409,LOCATION,37.3735037752572,126.63280069828033",
>           "isArrived":"false",
>           "dist":501.6447064033045,
>           "route":"37.37696025287909,126.63286875933409,37.3768112,126.6328854,37.3767337,126.6328073,37.3764846,126.6324536,37.3760999,126.6324209,37.3760338,126.6325386,37.3759507,126.6326781,37.3758297,126.6326989,37.3757593,126.6326265,37.3755988,126.6324683,37.3755278,126.6323871,37.3753751,126.6327221,37.3752525,126.6328173,37.3750951,126.6326523,37.3749913,126.6325527,37.3747223,126.6327219,37.3746369,126.6327431,37.3744802,126.6326372,37.3743619,126.6325219,37.3742006,126.6327746,37.3739885,126.6325177,37.3738392,126.6327069,37.3735867,126.6324642,37.37348,126.6326007",
>           "time":8,
>           "steps":827
>       }
>   ]
> }
> ```
> </details>
>
>
> ### Run example
>
> ![getrootlive](https://github.com/liardanc3/inunavi/assets/85429793/31505e6c-5c92-48cb-87b9-dc91f8437b42)
> </details>
> </details>

<details>
 <summary><code>다음 강의장소 조회</code> <code>POST</code> <code><b>/getNextPlace</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "true",
>    "nextPlaceCode" : "SF"
>    "nextPlaceLocationString" : "37.3757955317388, 126.63483788738135"
>    "nextPlaceTitle" : "자연과학대학 생명과학기술대학"
>}
> ```
> </details>
>
>
> ### Run example
>
> ![getnextplace](https://github.com/liardanc3/inunavi/assets/85429793/29e59910-ddea-4385-a45f-559b4b949652)
> </details>
> </details>


<details>
 <summary><code>장소 검색</code> <code>GET</code> <code><b>/placeSearchList</b></code> </summary>

<br>

> ### Parameters
> ```java
> searchKeyword : 검색어
> myLocation : 현재 좌표
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "response": [
>        {
>            "placeCode": "FOODSTU0,CAFECDSTU0",
>            "title": "학생식당",
>            "sort": "식당",
>            "distance": 1011.9700490800614,
>            "location": "37.374161554994025, 126.63175437187864",
>            "time": "점심 10:30 ~ 14:00 · 저녁 17:00 ~ 18:30",
>            "callNum": "-"
>        },
>        {
>            "placeCode": "FOODSTU1,CONGSSTU1",
>            "title": "제1기숙사 식당",
>            "sort": "식당",
>            "distance": 1127.305253103789,
>            "location": "37.37357062725584, 126.62995831475537",
>            "time": "아침 08:00 ~ 10:00 · 점심 11:30 ~ 13:30 ",
>            "callNum": "-"
>        }
>    ]
>}
> ```
> </details>
>
>
> ### Run example
>
> ![placeSearchList](https://github.com/liardanc3/inunavi/assets/85429793/8d533b83-eaaf-47a2-8fd5-81c4dd3568bb)
> </details>
> </details>


<details>
 <summary><code>주간 경로 미리보기</code> <code>POST</code> <code><b>/getOverviewRoot</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "response": [
>        {
>            "startLectureName": "정문(버스정류장)",
>            "endLectureName": "헌법과시민의식",
>            "totalTime": "6.954737169047408",
>            "distance": "421.49922236650957",
>            "directionString": "37.3779618948447,126.634624941891,37.3776561,126.6343478,37.3775462,126.634278,37.3776165,126.6340366,37.3774093,126.6338804,37.3772261,126.6337823,37.3771011,126.6336014,37.3770244,126.6334982,37.3769344,126.6334508,37.3765983,126.6331388,37.3764788,126.6330362,37.3763888,126.632986,37.3762801,126.6328787,37.3761388,126.632927,37.3761087,126.6328564,37.3759851,126.6327438,37.3758297,126.6326989,37.3757593,126.6326265,37.3755988,126.6324683,37.3755278,126.6323871,37.3754372,126.6323093,37.3753669,126.632253,37.3751692,126.632078,37.3752193,126.6319707",
>            "endLectureTime": "월요일 01:00 PM"
>        },
>        {
>            "startLectureName": "공과대학(버스정류장)",
>            "endLectureName": "행정학의이해",
>            "totalTime": "4.324380023352096",
>            "distance": "262.0836377789149",
>            "directionString": "37.3724664576151,126.634094079558,37.3722549507586,126.633861767182,37.3722695442901,126.633637436853,37.3723266,126.6335242,37.3722275,126.6334276,37.3723439,126.6332448,37.3724601,126.6330638,37.3725496,126.6329055,37.3726391,126.6327446,37.3727536,126.6328199,37.3728804,126.6329648,37.3730062,126.6330841,37.3730797,126.6331537,37.3731426,126.6332087,37.3732641,126.6330075,37.3733078,126.6331966",
>            "endLectureTime": "수요일 11:30 AM"
>        },
>        {
>            "startLectureName": "행정학의이해",
>            "endLectureName": "데이터베이스",
>            "totalTime": "1.4395518845330186",
>            "distance": "87.2455687595769",
>            "directionString": "37.3749611,126.6323935,37.3746872,126.6330319,37.3745278,126.6332002",
>            "endLectureTime": "수요일 05:30 PM"
>        }
>    ]
>}
> ```
> </details>
>
> ### Run example
>
> ![getoverview](https://github.com/liardanc3/inunavi/assets/85429793/9f8a5b6a-3239-4d21-8376-56ff421180c6)
> </details>
> </details>

<details>
 <summary><code>경로 분석 결과 조회</code> <code>POST</code> <code><b>/getAnalysisResult</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>    "success": "true",
>    "distancePercentage": "15",
>    "tightnessPercentage": "10",
>    "totalDistance" : "200.1323"
>}
> ```
> </details>
>
> ### Run example
>
> ![analysisresult](https://github.com/liardanc3/inunavi/assets/85429793/a2442ae7-49e3-4b2e-a200-6d8698c5c42f)
> </details>
> </details>


<details>
 <summary><code>추천 강의 조회</code> <code>POST</code> <code><b>/getRecommendLecture</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
>```
> 
>
> ### Responses
> <details open><summary>success</summary> 
> <br>
>
> ```HTTP
> HTTP/1.1 200 OK
> Content-Type: application/json;charset=UTF-8
>
>{
>   "response": [
>       {
>           "id": "1893",
>           "department": "교양",
>           "grade": "전학년",
>           "category": "교양선택",
>           "number": "XAA1375001",
>           "professor": "김재영",
>           "how": "-",
>           "point": "3",
>           "lecturename": "행정학의이해",
>           "classroom_raw": "제12호관 컨벤션센터-101 용정강의실[SM101]",
>           "classtime_raw": " [SM101:수(1)(2)(3)]",
>           "classroom": "SM101,",
>           "classtime": "114-119,",
>           "realTime": "수 9:00 - 12:00"
>       },
>       {
>           "id": "1778",
>           "department": "교양",
>           "grade": "전학년",
>           "category": "교양선택",
>           "number": "0007002001",
>           "professor": "익명",
>           "how": "-",
>           "point": "3",
>           "lecturename": "헌법과시민의식",
>           "classroom_raw": "제15호관 인문대학-201 강의실(대)-계단식[SP201]",
>           "classtime_raw": " [SP201:월(2B-3)(4-5A)]",
>           "classroom": "SP201,",
>           "classtime": "21-26,",
>           "realTime": "월 10:30 - 14:00"
>       }
>   ]
>}
> ```
> </details>
>
>
> ### Run example
>
> ![recommend](https://github.com/liardanc3/inunavi/assets/85429793/ff981f04-6e16-4cf6-86f9-f642efee9a28)
> </details>
> </details>
