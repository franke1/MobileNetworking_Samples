# MobileNetworking_Samples
TAcademy - MobileNetworking Course Sample Codes


# HTTP 예제

## HTTP 라이브러리

- HttpLibrary_Android_OKHttp : OKHttp 라이브러리

## 목록에서의 이미지 로딩

### Android

- ImageLoader_AsyncTask : AsyncTask로 구현
- ImageLoader_Volley : Volley로 구현

## HTTP 응답 에러 처리

폴더 : /HTTP

### 서버

요청

Path | 동작
----|----
/delayed | 요청 후 40초 뒤 응답 완료
/infinite | 응답이 종료되지 않는 상황
/badreq | 400번 응답 코드
/unauth | 401번 응답 코드


동작시키기

$ npm install

$ node Server/errorServer.js

### Android

### iOS


# HTTP POST 요청

폴더 : /PostRequest

## URLEncoded 방식

### Server

메소드 | Path | 동작
----|----|----
get | '/' | 목록 보기(HTML)
get | '/post' | 입력 폼 보기(HTML)
post | '/' | POST 요청. 예) title=value1&director=value2

서버 동작시키기

$ npm install

$ node Server/movieListServer.js

### Android

- BasicPost_Android : HttpURLConnection 으로 구현한 예제

## Multipart 방식 


### Server

/files 폴더 생성

서버 동작 시키기

$ npm install

$ node Server/imageUploadServer.js

### Android


# XML/JSON



# REST 서비스

## 서버

메소드 | 경로 | 동작
----|----|----
get | /movies | 영화 목록 JSON
post | /movies | 영화 정보 추가하기. 인코딩 : URLEncoded 방식, title, director, year, synopsis
get | /movies/:movie_id | 영화 상세 정보 보기
delete | /movies/:movie_id | 영화 정보 삭제. :movie_id는 영화 ID
put | /movies/:movie_id | 영화 정보 수정. URLEncoded 방식. title, director, year, synopsis
post | /movies/:movie_id | 리뷰 추가하기. URLEncoded 방식. review=MovieReview	



# 인증

## Cookie

### 서버

$ npm install

$ node /Server/cookieServer.js

### 안드로이드 샘플


## LocalAuth

### 서버

/Server/AuthServer

$ npm install

$ node server.js

웹 브라우저에서 확인 가능.

메소드 | 경로 | 동작
----|----|----
get | / | 웹 페이지(웹 브라우저 접속)
get | /talks | 글 목록 보기
post | /talks | 글 작성하기. URLEncoded, talk=NewTalk
post | /login | 로그인 ( username="user", password="1234" )
delete | /logout | 로그아웃

### 클라이언트

안드로이드 : LocalAuth_Android

