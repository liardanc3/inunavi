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
 <summary><code>d회원가입</code> <code>POST</code> <code><b>/user/insert</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
> password : 비밀번호
> major : 전공
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
>    "email": "liardanc3@gmail.com"
>}
> ```
></details>
></details>

<details>
 <summary><code>d유저 강의 조회</code> <code>POST</code> <code><b>/user/select/class</b></code> </summary>

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
> // gif
> </details>
> </details>

<details>
 <summary><code>d아이디 중복 체크</code> <code>POST</code> <code><b>/user/check/id={id}</b></code> </summary>

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
> // gif
> </details>
> </details>

<details>
 <summary><code>d로그인</code> <code>POST</code> <code><b>/user/login</b></code> </summary>

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
> // gif
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
>    "response": []
>}
> ```
></details>
>
>
> ### Run example
>
> // gif
> </details>
> </details>

<details>
 <summary><code>d비밀번호 변경</code> <code>POST</code> <code><b>/user/update</b></code> </summary>

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
> // gif
> </details>
> </details>

<details>
 <summary><code>d전공 업데이트</code> <code>POST</code> <code><b>/user/update2</b></code> </summary>

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
> // gif
> </details>
> </details>

<details>
 <summary><code>d회원 탈퇴</code> <code>POST</code> <code><b>/user/quit</b></code> </summary>

<br>

> ### Parameters
> ```java
> email : 이메일
> password : 비밀번호호
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
> // gif
> </details>
> </details>

<details>
 <summary><code>d강의 추가</code> <code>POST</code> <code><b>/user/insert/class</b></code> </summary>

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
> // gif
> </details>
> </details>

<details>
 <summary><code>d강의 삭제</code> <code>POST</code> <code><b>/user/delete/class</b></code> </summary>

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
> // gif
> </details>
> </details>

<details>
 <summary><code>d시간표 정보</code> <code>GET</code> <code><b>/getTimeTableInfo</b></code> </summary>

<br>

> ### Parameters
> ```java
> x
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
 <summary><code>d강의 검색</code> <code>GET</code> <code><b>/selectLecture</b></code> </summary>

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
> <details><summary>failure</summary> 
> <br>
>
> ```java
> HTTP/1.1 200 OK
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
> // gif
> </details>
> </details>

<details>
 <summary><code>경로 묻기</code> <code>GET</code> <code><b>/getRootLive</b></code> </summary>

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
> // gif
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
>    "response": []
>}
> ```
></details>
>
>
> ### Run example
>
> // gif
> </details>
> </details>


<details>
 <summary><code>장소 검색</code> <code>GET</code> <code><b>/placeSearchList</b></code> </summary>

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
> // gif
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
>    "response": []
>}
> ```
></details>
>
>
> ### Run example
>
> // gif
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
>    "response": []
>}
> ```
></details>
>
>
> ### Run example
>
> // gif
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
>    "response": []
>}
> ```
></details>
>
>
> ### Run example
>
> // gif
> </details>
> </details>
