# MobileNetworking_Samples
TAcademy - MobileNetworking Course Sample Codes


# HTTP 예제

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