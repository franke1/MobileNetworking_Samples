# MobileNetworking_Samples
TAcademy - MobileNetworking Course Sample Codes


# 네트워크 기본

- Basic/NetworkBasic_Android : 안드로이드. 동기식과 비동기식, Handler 사용 예제
- Basic/AsyncTask_Android : AsyncTask 사용 예제
- Basic/NetworkInfo_Android : 네트워크 환경 정보 얻기 예제

# HTTP 통신

- HTTP/RequestAndResponse : 요청과 응답 메세지 예제
- HTTP/AsyncTask : 이미지 요청 - 응답

## HTTP 라이브러리

- HTTP/ApacheHttpLibrary : 아파치 라이브러리
- HTTP/VolleyLibaray : Volley 라이브러리
- HTTP/OkHttp_Android_OKHttp : OKHttp 라이브러리

## HTTP 웹뷰

- HTTP/WebView_Android : 웹뷰

## 이미지 로딩

- HTTP/ImageLoader_AsyncTask_Android : AsyncTask로 구현한 이미지 로딩, 캐시
- HTTP/ImageLoader_Volley_Android : Volley 이미지 로더 라이브러리
- HTTP/ImageLoader_Glide_Android : Glide 이미지 로더 라이브러리

## 이미지 목록과 로딩

- HTTP/ImageList_AsyncTask_Android : AsyncTask로 작성한 목록 형태의 이미지
- HTTP/ImageList_Volley_Android : Volley 로 작성한 목록 형태의 이미지
- HTTP/ImageList_Glide_Android : Glide 로 작성한 목록 형태의 이미지

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

서버 동작

$ npm install
$ node Server/errorServer.js

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

### Android 예제

- BasicPost_Android : HttpURLConnection 으로 구현한 예제
- BasicPost_Volley_Android : Volley로 구현한 예제
- BasicPost_OkHttp_Android : OkHttp 로 구현한 예제


## Multipart 방식 

멀티파트를 이용한 파일과 데이터 업로드

### Server

/files 폴더 생성

서버 동작 시키기

$ npm install

$ node Server/imageUploadServer.js

### Android

- PostRequest/PhotoUpload_Android : HttpUrlConnection을 이용한 이미지 업로드
- PostRequest/PhotoUpload_Volley : Volley을 이용한 이미지 업로드
- PostRequest/PhotoUpload_OkHttp_Android : OkHttp 이용한 이미지 업로드

# XML/JSON

## XML/JSON 응답 파싱

- XmlJson/TopSongs_XML_SAX_Android : SAX파서를 이용한 XML 파싱 예제
- XmlJson/TopSongs_XML_Dom_Android : DOM 파서를 이용한 XML 파싱 예제
- XmlJson/TopSongs_JSON_Android : 기본 JSON 파서를 이용한 JSON 파싱 예제
- XmlJson/TopSongs_JSON_Volley_Android : Volley를 이용한 JSON 파싱 예제

## JSON 요청

JSON 요청

### Server

- XmlJson/Server/server

npm install
node server.js

- Method : POST
- Url : /upload
- 요청 바디 : { "data" : { "name" : "IU" } }
- 응답 바디 : { "msg" : "success", "data" : { "name" : "IU" } }

### Android

- XmlJson/JsonRequest_Volley_Android : Volley를 이용한 JSON 요청
- XmlJson/JsonRequest_OkHttp_Android : Volley를 이용한 JSON 요청

# REST 서비스

## 서버

메소드 | 경로 | 동작
----|----|----
get | /movies | 영화 목록 JSON
post | /movies | 영화 정보 추가하기. 인코딩 : URLEncoded 방식, title, director, year, synopsis
get | /movies/ID | 영화 상세 정보 보기
delete | /movies/ID | 영화 정보 삭제. id는 영화 ID
put | /movies/ID | 영화 정보 수정. URLEncoded/JSON 방식. title, director, year, synopsis

# 인증

## Cookie

쿠키 저장소 설정 예제. 서버 필요

### 서버

$ npm install
$ node /Server/cookieServer.js

### 안드로이드 샘플

- AuthAndSecurity/Cookies_Android : HttpUrlConnection, Volley, OkHttp 쿠키 설정

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

- AuthAndSecurity/LocalAuth_Android : LocalAuth를 이용한 인증/글쓰기


## OAuth

준비중

- AuthAndSecurity/FBAuth_Android : Facebook OAuth를 이용한 인증/글쓰기

# 클라우드 메세지

## 서버

- Messaging/Server : FCM 토큰과 기기 ID, 메세지 발송 서버

$ npm install
$ npm server.js

## 클라이언트

- Messaging/CloudMessaging_Android : FCM을 사용하는 안드로이드 클라이언트

# SNS

## Facebook

- SNS/Facebook_Android : 페이스북 서비스 사용 앱