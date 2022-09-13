# 제 3장 HTTP 정보는 HTTP 메시지에 있다

## 3.1 HTTP 메시지

</br>

> HTTP에서 교환하는 정보이다.  
> CRLF로 헤더와 바디를 구분한다.  
> GET에서는 바디를 실을 수 없다.

</br>

## 3.2 리퀘스트 메시지와 리스폰스 메시지의 구조

</br>

|    메시지 구조     |
| :----------------: |
|    메시지 헤더     |
| 개행 문자[CR + LF] |
|    메시지 바디     |

- 메시지 헤더 구조
  - 리퀘스트 라인 or 상태 라인
  - 리퀘스트(리스폰스) 헤더 필드
  - 일반 헤더 필드
  - 엔티티 헤더 필드

</br>

### 메시지 헤더 정리

- 리퀘스트 라인: 리퀘스트에 사용하는 메소드와 리퀘스트 URI와 사용하는 HTTP 버전 포함
- 상태 라인: 리스폰스 결과를 나타내는 상태 코드와 HTTP 버전
- 헤더 필드: 리퀘스트와 리스폰스의 여러 조건과 속성 등을 나타내는 강종 헤더 필드 포함

### 그외

- HTTP의 RFC에는 없는 헤더필드가 포함되는 경우가 있다.
  - 쿠키와 같은

### 여기서 잠깐 RFC(Request for Comments)란?

> RFC란 미국의 국제 인터넷 표준화 기구인 IETF에서 제공, 관리하는 문서로  
> 웹 개발에 있어서 필요한 기술, 연구 결과, 절차 등을 기술해놓은 메모이다.

- 모든 인터넷 표준에 대해 RFC로 항상 문서화가 되어있다.
  - 예시) 이메일 표준 RFC 822, RFC5822

</br>

## 3.3 인코딩으로 전송 효율을 높이다.

> HTTP로 데이터를 보낼때 그냥 전송할 수 있지만,  
> 인코딩을 실시하여 전송을 높일 수 있다.

- 다량의 액세스를 효율 좋게 처리 가능하다.
- 하지만, 컴퓨터에서 인코딩 처리를 해야함으로 CPU등의 리소스를 소비한다.

### 3.3.1 메시지 바디와 엔티티 바디의 차이

- 메시지
  - HTTP 통신의 기본 단위로 옥텟 시퀀스로 구성되고 통신을 통해서 전송된다.
- 엔티티
  - 리퀘스트랑 리스폰스의 페이로드로 전송되는 정보로 엔티티 헤더 필드와 엔티티 바디로 구성된다.

> HTTP 메시지 바디의 역할은 리퀘스트랑 리스폰스에 관한 엔티티 바디를 운반하는 일이다.  
> 두 개는 같지만, 전송 코딩이 적용된 경우 엔티티 바디의 내용은 변하여 메시지 바디와는 달라진다.

</br>

### 3.3.2 압축해서 보내는 콘텐츠 코딩

</br>

- Contents codings: 엔티티에 적용하는 인코딩을 가리키는데 엔티티 정보를 유지한 채로 압축한다. 콘텐츠 코딩된 엔티티는 수신한 클라이언트 측에서 디코딩 한다.

- 주요 콘텐츠 압축
  - gzip(GNU zip)
  - compress(UNIX 표준 압축)
  - deflate(zlib)
  - identity(인코딩 없음)

</br>

### 분해해서 보내는 청크 전송 코딩

</br>

> HTTP 에서 요청했던 리소스 중 엔티티 바디의 전송이 완료되지 않으면 렌더링 되지 않는다.  
> 이때 사이즈가 큰 데이터를 전송하는 경우에 데이터를 분할하여 표시할 수 있다.
> 엔티티 바디를 `분할하는 기능을 청크 전송 코딩`이라고 부른다.

</br>

- 청크 전송 코딩 과정
  - 엔티티 바디를 청크로 분해한다.
  - 다음 청크 사이즈를 16진수로 사용해서 단락을 표시하고 엔티티 바디 끝에는 0(CRLF)을 기록한다.
  - 청크 전송 코딩된 엔티티 바디는 클라이언트가 원래의 엔티티 바디로 디코딩 시작

## 3.4 여러 데이터를 보내는 멀티파트

</br>

> 메일의 경우 메일의 본문이나 첨부파일을 함께 붙여 보낼 수 있도록 MIME이 지원된다.

- MIME(multipurpose internet mail extensions)
  - 텍스트나 영상, 이미지와 같은 데이터를 다루기 위한 기능

> HTTP도 멀티파트에 대응하고 있다.  
> 하나의 메ㅣ지 바디에 여러 엔티티를 포함 시킬 수 있다.

- myltipart/form-data
  - Web 폼으로부터 파일 업로드에 사용
- multipart/byteranges
  - 상태코드 206(partial Content) 리스폰스 메시지가 복수 범위의 내용을 포함하는때 사용

### 예시

```http
Content-Type: multipart/form-data; boundary=AaB03x

--AaB03x
Content-Disposition: form-data; name="field1"

Joe Blow
--AaB03x
Content-Disposition: form-data; filename="file1.txt"
Content-Type: text/plain

...(file1.txt 데이터)
--AaB03x
```

```http
HTTP/1.1 206 Partial Content
Date: Fri, 13 Jul 2012 02:45:26 GMT
Content-Type: multipart/byteranges; boundary=THIS_STRING_SEPARATES

--THIS_STRING_SEPERATES
Content-Type: application/pdf
Content-Range: bytes 500-999/8000

...(지정한 범위의 데이터)...
--THIS_STRING_SEPERATES
Content-Type: application/pdf
Content-Range: bytes 7000-7999/8000
...(지정한 범위의 데이터)...

--THIS_STRING_SEPERATES
```

- 멀티파트 세부사항 RFC2046

</br>

## 3.5 일부분만 받는 레인지 리퀘스트

</br>

> 대용량 데이터를 다운받는 도중에 커넥션이 끊어지게 되면 처음부터 다시 다운로드를 받아야하는 문제가 발생한다.  
> 이러한 문제를 해결하기 위해 이전에 다운로드 한 곳에서부터 다운로드를 재개할 수 있는 리줌이라는 기능이 필요하였다.  
> 이 기능을 실현하기 위해서 엔티티의 범위를 지정해서 다운로드할 필요가 있다.  
> 이와 같이 범위를 지정하여 리퀘스트하는 것을 `레인지 리퀘스트`라고 한다.

</br>

### 레인지 리퀘스트 예시

- 클라이언트

```http
GET /tip.jpg HTTP/1.1
HOST: www.usagidesign.jp
Range: bytes = 5001-10000
```

- 서버

```http
HTTP 1.1 206 Partial Content
Date: Fri, 13 Jul 2012 04:39 GMT
Content-Range: bytes 5001-10000 / 10000
Content-Length: 5000
Content-Type: image/jpeg
```

</br>

## 3.6 최적의 콘텐츠를 돌려주는 콘텐츠 네고시에이션

</br>

- 콘텐츠 네고시에이션은 제공하는 리소스 내용에 대해 교섭하는 것이다.

  - 클라이언트에 적절한 리소스를 제공하기 위해서

- 판단 기준
  - Accept
  - Accept-Charset
  - Accept-Encoding
  - Accept-Language
  - Content-Language

> 콘텐츠 네고시에이션은 제공하는 리소스를 언어와 문자 세트, 인코딩 방식을 기준으로 판단하고 있다.

</br>

### 네고시에이션 종류

- 서버 구동형 네고시에이션

  - 서버측에서 리퀘스트 헤더 필드의 정보를 참고해서 자동적으로 처리

- 에이전트 구동형 네고시에이션

  - 클라이언트 측에서 콘텐츠 네고시에이션을 하는 방식, 유저가 수동으로 선택한다.

- 트랜스페어런트 네고시에이션
  - 서버 구동형과 에이전트 구동형을 혼합한 것이다
