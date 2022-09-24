# 제 5장 HTTP와 연계하는 웹 서버

## 5.1 1대로 멀티 도메인을 가능하게 하는 가상 호스트

> HTTP 1.1에서는 하나의 HTTP 서버에 여러 개의 도메인을 등록할 수 있다.  
> 같은 IP 주소에서 호스트 명과 도메인 명을 가진 여러개의 웹 사이트가 실행되는 가상 호스트의 시스템이 있기 때문에  
> HTTP 리퀘스트를 보내는 경우에는 호스트와 도메인을 완전하게 포함한 URI를 지정하거나 HOST 헤더 필드에서 지정해야 한다.

</br>

## 5.2 통신을 중계하는 프로그램 : 프록시, 게이트웨이, 터널

### 5.2.1 프록시

</br>

> 클라이언트와 서버 사이에서 중계 서버를 투고, 클라이언트 <-> 프록시 <-> 서버  
> 구조로 요청을 주고 받는다.

</br>

> 프록시 기본 동작은 클라이언트로부터 받은 리퀘스트를 서버에 전송한다.  
> 클라이언트로부터 받은 리퀘스트 URI를 변경하지 않고, 리소스를 가지고 있는 서버에 보낸다.  
> 서버로부터 되돌아온 리스폰스는 프록시 서버를 경유해서 클라이언트에 돌아온다.

</br>

- 프록시 서버 사용하는 이유
  - 캐시를 사용해서 네트워크 대역 등을 효율적으로 사용하기 위해
  - 조직 내 특정 웹 사이트에 대한 액세스 제한, 액세스 로그를 획득하는 정책을 철저하게 지키기 위해

#### 캐싱 프록시

</br>

> 캐시를 보존해 두는 타입의 프록시  
> 동일한 리퀘스트에 대해 서버로부터 요청하지 않고 캐시에서 응답을 되돌려 주는 경우

</br>

#### 투명 프록시

</br>

> 리퀘스트와 리스폰스를 중계할 때 메시지 변경을 하지 않는 타입의 프록시

</br>

### 5.2.2 게이트 웨이

</br>

> HTTP 서버 이외의 서비스를 제공하는 서버가 된다.  
> 클라이언트와 게이트웨이 사이를 암호화 하는 등으로 안전하게 접속함으로써  
> 통신의 안전성을 높이는 역할 등을 한다.

</br>

### 5.2.3 터널

</br>

> 터널은 요구에 따라서 다른 서버와의 통신 경로를 확립한다.  
> 이때 클라이언트는 SSL같은 암호화 통신을 통해 서버와 안전하게 통신을 하기 위해 사용한다.  
> 터널 자체는 HTTP 리퀘스트를 해석하려고 하지 않는다.  
> 결국 리퀘스트를 그대로 다음 서버에 중계하낟.  
> 그리고 터널은 통신하고 있는 양쪽 끝의 접속이 끊어질 때 종료한다.

</br>

## 5.3 리소스를 보관하는 캐시

- 캐시

> 캐시는 프록시 서버와 클라이언트의 로컬 디스크에 보관된 리소스의 사본을 의미한다.

</br>

### 5.3.1 캐시는 유효기간이 있다

</br>

> 캐시 서버에 있는 경우라도 같은 리소스의 리퀘스트에 대해서 항상 캐시를 돌려준다고 할 수 없다.  
> 이것은 캐시되어 있는 리소스의 유효성과 관계가 있다.

- 서버에 데이터가 갱신된다면?
  - 유효기간 및 클라이언트의 요구에 의해 새로운 데이터를 다시 획득하는 경우도 있다.

</br>

### 5.3.2 클라이언트 측에도 캐시가 있다

> 캐시 서버만 캐시를 가지는 것이 아니다.  
> 클라이언트가 사용하고 있는 브라우저에서도 캐시를 가질 수 있다.  
> 브라우저가 유효한 캐시를 가지고 있다면 같은 리소스의 액세스는  
> 서버에 액세스하지않고 로컬 디스크로부터 불러온다.