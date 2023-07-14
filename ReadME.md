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
 <summary><code>강의 추가</code> <code>POST</code> <code><b>/user/insert/class</b></code> </summary>

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
 <summary><code>강의 삭제</code> <code>POST</code> <code><b>/user/delete/class</b></code> </summary>

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
 <summary><code>시간표 정보</code> <code>GET</code> <code><b>/getTimeTableInfo</b></code> </summary>

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
 <summary><code>강의 검색</code> <code>GET</code> <code><b>/selectLecture</b></code> </summary>

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
