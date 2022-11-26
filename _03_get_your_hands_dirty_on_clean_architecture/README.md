---
Title: 만들면서 배우는 클린 아키텍처
Author: Jung
---

- [01. 계층형 아키텍처의 문제는 무엇일까?](#01-계층형-아키텍처의-문제는-무엇일까)
  - [계층형 아키텍처는 데이터베이스 주도 설계를 유도한다](#계층형-아키텍처는-데이터베이스-주도-설계를-유도한다)
  - [지름길을 택하기 쉬워진다](#지름길을-택하기-쉬워진다)
  - [테스트하기 어려워진다](#테스트하기-어려워진다)
  - [유스케이스를 숨긴다](#유스케이스를-숨긴다)
  - [동시 작업이 어려워진다](#동시-작업이-어려워진다)
- [02. 의존성 역전하기](#02-의존성-역전하기)
  - [단일 책임원칙](#단일-책임원칙)
  - [의존성 역전 원칙](#의존성-역전-원칙)
  - [클린 아키텍처](#클린-아키텍처)
  - [헥사고날 아키텍처](#헥사고날-아키텍처)
  - [유지보수 가능한 소포트웨어를 만드는데 어떻게 도움이 될까?](#유지보수-가능한-소포트웨어를-만드는데-어떻게-도움이-될까)
- [03. 코드 구성하기](#03-코드-구성하기)
  - [아키텍처적으로 표현력 있는 패키지 구조](#아키텍처적으로-표현력-있는-패키지-구조)
  - [의존성 주입의 역할](#의존성-주입의-역할)
- [04. 유스케이스 구현하기](#04-유스케이스-구현하기)
  - [유스케이스 둘러보기](#유스케이스-둘러보기)
  - [입력 유효성 검증](#입력-유효성-검증)
  - [생성자의 힘](#생성자의-힘)
  - [유스케이스마다 다른 입력 모델](#유스케이스마다-다른-입력-모델)
  - [비즈니스 규칙 검증하기](#비즈니스-규칙-검증하기)
  - [풍부한 도메인 모델 vs 빈약한 도메인 모델](#풍부한-도메인-모델-vs-빈약한-도메인-모델)
  - [읽기 전용 유스케이스는 어떨까?](#읽기-전용-유스케이스는-어떨까)
  - [유지보수 가능한 소프트웨어를 만드는데 어떻게 도움을 줄까?](#유지보수-가능한-소프트웨어를-만드는데-어떻게-도움을-줄까)
- [05. 웹 어댑터 구현하기](#05-웹-어댑터-구현하기)
- [06. 영속성 어댑터 구현하기](#06-영속성-어댑터-구현하기)
- [07. 아키텍처 요소 테스트 하기](#07-아키텍처-요소-테스트-하기)
- [08. 경계간 매핑하기](#08-경계간-매핑하기)
- [09. 애플리케이션 조립하기](#09-애플리케이션-조립하기)
- [10. 아키텍처 경계 강제하기](#10-아키텍처-경계-강제하기)
- [11. 의식적으로 지름길 사용하기](#11-의식적으로-지름길-사용하기)
- [12. 아키텍처 스타일 정하기](#12-아키텍처-스타일-정하기)

</br>

## 01. 계층형 아키텍처의 문제는 무엇일까?

</br>

|              Layered Architecture              |
| :--------------------------------------------: |
| ![Layered Architecture](./res/_01_layered.png) |

</br>

> 해당 그림은 상위 수준 관점에서 일반적인 3계층 아키텍처를 표현한 그림이다.

- 웹계층에서 요청을 받은 후
- 도메인 혹은 비즈니스 계층에 있는 서비스로 요청을 보낸다.
- 이후 서비스에서는 필요한 비즈니스 로직을 수행하고 도메인 엔티티의 현재 상태를 조회하거나 변경하기 위해 영속성 계층의 컴포넌트를 호출한다.

</br>

> 사실 잘 만들어진 계층형 아키텍처는 선택의 폭을 넓히고 변화하는 요구사항과  
> 외부 요인에 빠르게 적응할 수 있게 해준다.  
> 하지만, 계층형 아키텍처는 코두에 나쁜 습관들이 스며들기 쉽게 만들고  
> 시간이 흐를 수록 소프트웨어를 점점 더 변경하기 어렵게 만든다.

</br>

### 계층형 아키텍처는 데이터베이스 주도 설계를 유도한다

</br>

> 우리가 만드는 대부분의 애플리케이션은 비즈니스를 관장하는  
> 규칙이나 정책을 반영한 모델을 만들어서  
> 사용자가 이러한 규칙과 정책을 더욱 편리하게 활용할 수 있게 한다.
> 이때 우리는 상태가 아니라 행동을 중심으로 모델링 한다.  
> 어떤 애플리케이션이든 상태가 중요한 요소이긴 하지만  
> 행동이 상태를 바꾸는 주체이기 때문에 행동이 비즈니스를 이끌어간다.

</br>

> 우리는 데이터베이스의 구조를 먼저 생각하고 이를 토대로 도메인 로직을 구현했을 것이다.  
> 전통적 계층형 아키텍처에서는 의존성의 방향에 따라 자연스럽게 구현한 것이다.  
> 하지만 비즈니스 관점에서는 맞지 않다.  
> 우선적으로 도메인 로직을 만든 후 도메인 로직이 맞다는 것을 확인한 후에  
> 이른 기반으로 영속성 계층과 웹 계층을 만들어야한다.

</br>

|               도메인 계층에서 데이터베이스 엔티티 사용은 영속성 계층과 강한 결합을 유발               |
| :---------------------------------------------------------------------------------------------------: |
| ![도메인 계층에서 데이터베이스 엔티티 사용은 영속성 계층과 강한 결합을 유발](./re/../res/_01_orm.png) |

</br>

> ORM에 의해 관리되는 엔티티들은 일반적으로 영속성 계층에 둔다.  
> 계층은 아래 방향으로만 접근 가능하고 도메인 계층에서 이러한 엔티티에 접근 가능하다.
>
> 하지만 이 경우 영속성 계층과 도메인 계층에 강한 결합이 생긴다.  
> 마치 서비스가 영속성 모델을 비즈니스 모델처럼 사용하게 되고  
> 이런 엔티티 클래스에서는 도메인 로직뿐만 아니라  
> 즉시 로딩, 지연로딩 트랜잭션 등등 영속성 계층과 관련된 작업들을 해야만한다.
>
> 영속성 코드가 사실상 도메인 코드에 녹아들어가서 둘 중 하나만 바꾸는 것이 어려워지고  
> 유연하고 선택의 폭을 넓혀준다던 계층형 아키텍처의 목표와 정확히 반대되는 상황이다.

</br>

### 지름길을 택하기 쉬워진다

</br>

- 계층형 아키텍처에서 적용되는 유일한 규칙은, 특정 계층에서 같은 계층에 있는 컴포넌트나 아래에 있는 계층에만 접근 가능하다는 것이다.

</br>

> 따라서 만약 상위 계층에 위치한 컴포넌트에 접근해야 한다면 간단하게 컴포넌트를 계층 아래로 내려버리면 된다...

|              영속성 계층에서 모든 것에 접근 가능하기 때문에 시간이 지나면서 점점 비대해진다              |
| :------------------------------------------------------------------------------------------------------: |
| ![영속성 계층에서 모든 것에 접근 가능하기 때문에 시간이 지나면서 점점 비대해진다](./res/_01_shorcut.png) |

</br>

> 영속성 계층은 컴포넌트를 아래 계층으로 내릴 수록 비대해진다.  
> 결국 어떤 계층에서도 속하지 않을 것처럼 보이는  
> 헬퍼 컴포넌트나 유틸리티 컴포넌트들이 이처럼 아래 계층으래 내릴 가능성이 큰 후보다.
>
> 아키텍처의 지름길 모드를 끄고싶다면  
> 적어도 추가적인 아키텍처 규칙을 강제하지 않는 이상 최선의 선택이 아니다.  
> 여기서 강제한다는 의미는 해당 규칙이 깨졌을 때 빌드가 실패하도록 만드는 규칙을 의미한다.

</br>

### 테스트하기 어려워진다

</br>

|            도메인 계층을 건너뛰는 것은 도멩니 로직을 코드 여기저기에 흩어지게 만든다             |
| :----------------------------------------------------------------------------------------------: |
| ![도메인 계층을 건너뛰는 것은 도멩니 로직을 코드 여기저기에 흩어지게 만든다](./res/_01_test.png) |

</br>

> 계층형 아키텍처를 사용할 때 일반적으로 나타나는 변화의 형태는 계층을 건너뛰는 것이다.  
> 엔티티의 필드를 하나만 조작하면 되는 경우에  
> 바로 웹계층에서 바로 영속성 계층에 접근하면 도메인 계층을 건드릴 필요가 없지 않을까?

- 첫 번째 문제는 단하나의 필드를 조작하는 것에 불과하더라도 도메인 로직을 웹계층에서 구현한다는 것이다.
  - 만약 유스케이스가 확장된다면 더 많은 도메인 로직을 웹계층에 추가해서 애플리케이션 전반에 걸쳐 책임이 섞이고 핵심 도메인 로직들이 퍼져나갈 확률이 높다.
- 두 번째 문제는 웹 계층 테스트에서 도메인 계층 뿐 아니라 영속성 게층도 모킹해야 한다는 것이다.
  - 실제 테스트보다 테스트를 설정하는 것이 더 복잡해질 것이다.

</br>

### 유스케이스를 숨긴다

</br>

|              넓은 서비스는 코드 상에서 특정 유스케이스를 찾는 것을 어렵게 만든다              |
| :-------------------------------------------------------------------------------------------: |
| ![넓은 서비스는 코드 상에서 특정 유스케이스를 찾는 것을 어렵게 만든다](./res/_01_usecase.png) |

</br>

> 우리가 기능을 추가하거나 변경할 적절한 위치를 찾는 일이 빈번하기 때문에  
> 아키텍처는 코드를 빠르게 탐색하는데 도움이 돼야 한다.
>
> 계층형 아키텍처는 도메인 서비스의 너비에 관한 규칙을 강제하지 않는다.  
> 그렇기 때문에 시간이 지나면 그림처럼 여러개의 유스케이스를 담당하는 아주 넓은 서비스가 만들어진다.
>
> 좁은 도메인 서비스가 유스케이스 하나만 담당하게 한다면 이런 작업들이 수월해질 것이다.  
> UserService에서 등록 유스케이스를 찾는 대신  
> RegisterUserService를 바로 열어서 작업을 시작하는 것처럼 말이다!

</br>

### 동시 작업이 어려워진다

> 계층형 아키텍처는 동시작업면에서 도움이 되지 않는다.  
> 개발자가 3명 있다고 가정하고, 웹, 도메인, 영속성 이런식으로 기능을 추가할 수 없다.  
> 모든 것이 영속성 계층위에 만들어지기 때문에 영속성 계층을 개발해야하고,  
> 그다음에 도메인 계층을, 그리고 마지막으로 웹 계층을 만들어야만 한다.

</br>

## 02. 의존성 역전하기

</br>

### 단일 책임원칙

</br>

- 하나의 컴포넌트는 오로지 한 가지 일만 해야 하고, 그것을 올바르게 수행해야 한다.

</br>

> 여기서 말하는 한 가지 일만 하는 것의 실제 의미는

- 컴포넌트를 변경하는 이유는 오직 하나뿐이어야 한다.

</br>

> 즉 컴포넌트를 변경할 이유가 오로지 한 가지라면 해당 컴포넌트는 딱 한 가지 일만 하게 된다.  
> 하지만 이보다 더 중요한 것은 변경할 이유가 오직 한 가지라는 것이다.
>
> 만약 우리가 변경할 이유가 한 가지라면 우리가 어떤 다른 이유로 소프트웨어를 변경 하더라도  
> 이 컴포넌트에 대해서 전혀 신경 쓸 필요가 없다.  
> 소포트웨어가 변경되더라도 여전히 우리가 기대한 대로 동작할 것이기 때문이다.

</br>

|            어떤 컴포넌트의 의존성 각각은 이 컴포넌트를 변경하는 이유 하나씩에 해당한다.            |
| :------------------------------------------------------------------------------------------------: |
| ![어떤 컴포넌트의 의존성 각각은 이 컴포넌트를 변경하는 이유 하나씩에 해당한다.](./res/_02_srp.png) |

</br>

> 그림 A는 여러 컴포넌트에 의존하는 반면 E는 의존하는 것이 전혀 없다.  
> E를 변경하는 이유는 새로운 요구사항에 의해 E의 기능을 바꿔야할 때 뿐이다.  
> 하지만 A의 경우 모든 컴포넌트를 의존하기 때문에  
> 다른 어떤 컴포넌트가 바뀌든 함께 바뀌어야 한다.
>
> 많은 코드들이 단일 책임 원칙을 위반하기 때문에 시간이 갈수록 변경하기가 더 어려워진다.  
> 시간이 지날 수록 변경할 더 많은 이유가 쌓여간다.  
> 변경할 이유가 많이 쌓인 후에는 한 컴포넌트를 바꾸는 것이 다른 컴포넌트가 실패하는 원인으로 작용한다.

</br>

### 의존성 역전 원칙

</br>

> 계층형 아키텍처에서 계층간 의존성은 항상 다음계층인 아래 방향을 가리킨다.  
> 단일 책임 원칙을 고수준에서 적용할 때 상위 계층들이 하위 계층들에 비해 변경할 이유가 더 많다.
>
> 그러므로 영속성 계층에 대한 도메인 계층의 의존성 때문에 영속성 계층을 변경할 때  
> 잠재적으로 상위 계층을 변경해야 한다.  
> 도메인 코드는 애플리케이션에서 가장 중요한 코드이며 영속성 코드가 바뀐다고 해서  
> 도메인 코드까지 바꾸고 싶지 않다. 이 경우 어떻게 의존성을 제거 할 수 있을까?

</br>

- DIP : 코드상의 어떤 의존성이든 그 방향을 바꿀 수 있다.

</br>

> 사실 의존성의 양쪽 코드를 모두 제어할 수 있을때만 의존성을 역전시킬 수 있다.

|            도메인 계층에 인터페이스를 도입함으로써 의존성을 역전시킬 수 있다            |
| :-------------------------------------------------------------------------------------: |
| ![도메인 계층에 인터페이스를 도입함으로써 의존성을 역전시킬 수 있다](./res/_02_dip.png) |

</br>

> 엔티티는 도메인 객체를 표현하고, 이 도메인 코드는 이 엔티티들의 상태를 변경하는 일을 중심으로 한다.  
> 그래서 먼저 엔티티를 도메인 계층으로 올린다.  
> 그러나 이제 영속성 계층의 레포지토리가 도메인 계층에 있는 엔티티에 의존하기 때문에  
> 두 계층 사이에 순환 의존성이 생긴다.  
> 이 부분이 바로 DIP를 적용하는 부분이다.  
> 도메인 계층에 레포지토리에 대한 인터페이스를 만들고  
> 실제 레포지토리는 영속성 계층에서 구현하게 하는 것이다.

</br>

### 클린 아키텍처

</br>

> 클린 아키텍체에서는 설계가 비즈니스 규칙의 테스트를 용이하게 하고, 비즈니스 규칙은 프레임워크, 데이터베이스, UI 기술, 그 밖의 외부 애플리케이션이나 인터페이스로부터 독립적일 수 있다는 걸 의미한다.
> 이것은 도메인 코드가 바깥으로 향하는 어떤 의존성도 없어야 함을 의미한다.
> 대신 의존성 역전 원칙의 도움으로 모든 의존성이 도메인 코드를 향하고 있다.

|                         clean architecture                          |
| :-----------------------------------------------------------------: |
| ![clean architecture](./res/_02_clean_architecture.jpeg.crdownload) |

</br>

> 이 아키텍처 코어에는 주변 유스케이스에서 접근하는 도메인 엔티티들이 있다.  
> 유스케이스는 서비스라고 불렸던 것들인데, 단일 책임을 갖기 위해 조금 더 세분화돼 있다.  
> 이를 통해 넓은 서비스 문제를 피할 수 있다.
>
> 도메인 코드에서는 어떤 영속성 프레임 워크나 UI 프레임 워크가 사용되는지 알 수 없기 때문에  
> 특정 프레임워크에 특화된 코드를 가질 수 없고 비즈니스 규칙에 집중할 수 있다.  
> 그래서 도메인 코드를 자유롭게 모델링 할 수 있다.
>
> 하지만 클린아키텍처는 대가가 따른다. 도메인 계층이 영속성이나 UI같은 외부 계층과 분리 되어야하므로  
> 애플리케이션의 엔티티에 대한 모델을 각 계층에서 유지보수해야 한다.

</br>

> JPA를 사용한다 했을 때 엔티티클래스를 사용하는데  
> 이 엔티티클래스를 영속성 계층과 도메인 계층에서 함께 사용해서는 안된다.  
> 이는 도메인 계층과 다른 계층들 사이에서도 마찬가지이다.
>
> 하지만 이와같은 형태가 더 바람직하다. 예를 들어 JPA에서는 엔티티 클래스에  
> 기본생성자를 강제한다. 하지만 이것이 도메인 계층에서 알아야할 로직인가..?

</br>

### 헥사고날 아키텍처

</br>

|                     헥사고날 아키텍처                      |
| :--------------------------------------------------------: |
| ![헥사고날 아키텍처](./res/_02_hexagonal_architecture.png) |

</br>

> 육각형 안에서 도메인 엔티티와 이와 상호작용하는 유스케이스가 있다.  
> 육각형 바깥에는 애플리케이션과 상호작용하는 다양한 어댑터들이 있다.
>
> 여기서 왼쪽에 있는 어댑터들은 애플리케이션을 주도하는 어댑터이며  
> 오른쪽에 있는 어댑터들은 애플리케이션에 의해 주도되는 어댑터들이다.
>
> 애플리케이션과 어댑터들간의 통신이 가능하려면 애플리케이션 코어가 각각의 포트를 제공해야 한다.  
> 주도하는 어댑터에게는 그러한 포트가 코어에 있는 유스케이스 클래스들에 의해 구현되고 호출되는 인터페이스가 될 것이고  
> 주도되는 어댑터에게는 그러한 포트가 어댑터에 의해 구현되고 코어에 의해 호출되는 인터페이스가 될 것이다.

</br>

### 유지보수 가능한 소포트웨어를 만드는데 어떻게 도움이 될까?

</br>

> 클린 아키텍처, 육각형 아키텍처, 혹은 포트와 어댑터 아키텍처 중 무엇으로 불리든 의존성을 역전시켜  
> 도메인 코드가 다른 바깥쪽 코드에 의존하지 않게 함으로써 영속성과 UI에 특화된 모든 문제로부터  
> 도메인 로직의 결합을 제거하고 코드를 변경할 이유의 수를 줄일 수 있다.

</br>

## 03. 코드 구성하기

</br>

### 아키텍처적으로 표현력 있는 패키지 구조

```text
- buckpal
  - account
    - adpater
      - in
        - web
          - AccountController
      - out
        - persistence
          - AccountPersistenceAdapter
          - SpringDataAccountRepository
    - domain
      - Account
      - Activity
    - application
      - SendMoneyService
      - port
        - in
          - SendMoneyUseCase
        - out
          - LoadAccountPort
          - UpdateAccountStatePort

```

> 육각형 아키텍처에서 구족적으로 핵심적인 요소는  
> 엔티티, 유스케이스, 인커밍/아웃고잉 포트, 인커밍/아웃고잉 어댑터이다.
>
> 구조의 각 요소들은 패키지 하나씩에 직접 매핑된다.  
> 최상위에는 Account와 관련된 유스케이스를  
> 구현한 모듈임을 나타내는 account 패키지가 있다.
>
> 그다음 레벨에는 도메인 모델이 속한 domain 패키지가 있다.  
> application 패키지는 도메인 모델을 둘러 싼 서비스 계층을 포함한다.  
> SendMoneyService는 인커밍 포트 인터페이스인 SendMoneyUseCase를 구현하고  
> 아웃고인 포트 인터페이스이자 영속성 어댑터에 의해 구현된  
> LoadAccountPort와 UpdateAccountStatePort를 사용한다.
>
> adapter 패키지는 애플리케이션 계층의 인커밍 포트를 호출하는 인커밍 어댑터와  
> 애플리케이션 계층의 아웃고잉 포트에 대한 구현을 제공하는 아웃고잉 어댑터를 포함한다.  
> BuckPal 예제의 경우 각각의 하위 패키지를 가진 web 어댑터와 persistence 어댑터로 이뤈진 간단한 애플리케이션이 된다.
>
> 여기서 패키지가 많은데 모든 것을 public으로 만들어서 패키지 간의 접근을 허용해야 한다는 것을 의미하는 것일까?  
> 적어도 어댑터 패키지에 대해서는 그렇지 않다.  
> 이 패키지에 들어 있는 모든 클래스들은 application 패키지 내에 있는 포트 인터페이스를 통하지 않고는  
> 바깥에선 호출되지 않기 때문에 package-private 수준으로 둬도 된다.  
> 그러므로 애플리케이션 계층에서 어댑터 클래스로 향하는 우발적인 의존성은 있을 수 없다.
>
> 하지만 application 패키지와 domain 패키지 내의 일부 클래스들은 public으로 지정해야 한다.  
> 의도적으로 어댑터에서 접근 가능해야 하는 포트들은 public이어야 한다.  
> 도메인 클래스들은 서비스, 그리고 잠재적으로는 어댑터에서도 접근 가능하도록 public이어야 한다.  
> 서비스는 인커밍 포트 인터페이스 뒤에 숨겨질 수 있기 때문에 public일 필요가 없다.
>
> 어댑터 패키지를 자체 패키지로 이동시키면 하나의 어댑터를 다른 구현으로 쉽게 교체할 수 있다.  
> 예를 들어 특정 데이터베이스가 적절하지 않아 키-밸류 데이터베이스로 개발을 시작했는데  
> SQL 데이터 베이스로 교체해야한다고 가정해보자.  
> 간단하게 관련 아웃고잉 포트들만 새로운 어댑터 패키지에 구현하고 기존 패키지를 지우면 된다.

</br>

### 의존성 주입의 역할

</br>

> 클린 아키텍처의 가장 본질적인 요건은 애플리케이션 계층이  
> 인커밍/아웃고잉 어댑터에 의존성을 갖지 않는다는 것이다.
>
> 웹어댑터와 같이 인커밍 어댑터에 대해서는 그렇게 하기가 쉽다.  
> 제어 흐름 방향이 어댑터와 도메인 코드 간으 의존성 방향과 같은 방향이기 때문이다  
> 어댑터는 그냥 애플리케이션 계층에 위치한 서비스를 호출할 뿐이다.
>
> 하지만 영속성 어댑터와 같이 아웃고잉 어댑터에 대해서는 제어 흐름의 반대 방향으로  
> 의존성을 돌리기 위해 의존성 역전 원칙을 이용해야 한다.
>
> 그런데 포트 인터페이스를 구현한 실제 객체를 누가 애플리케이션 계층에 제공해야 할까?  
> 포트를 애플리케이션 계층 안에서 수동으로 초기화 한다면 애플리케이션 계층에 어댑터에 대한 의존성을 추가해야한다.
>
> 이 부분에서 의존성 주입을 활용할 수 있다.
> 모든 계층에 의존성을 가진 중립적인 컴포넌트를 하나 도입한느 것이다.  
> 이 컴포넌트는 아키텍처를 구성하는 대부분의 클래스를 초기화하는 역할을 갖는다.

</br>

</br>

## 04. 유스케이스 구현하기

</br>

### 유스케이스 둘러보기

</br>

> 먼저 유스케이스가 실제로 무슨일을 하는지 살펴보자.  
> 일반적으로 유스케이스는 다음과 같은 단계를 따른다.

</br>

- 1.  입력을 받는다
- 2.  비즈니스 규칙을 검증한다.
- 3.  모델 상태를 조작한다.
- 4.  출력을 반환한다.

</br>

> 유스케이스는 인커밍 어댑태로부터 입력을 받는다.  
> 이 단계를 왜 `입력 유효성 검증`으로 부르지 않는지 의아할 수도 있다.  
> 책에서는 유스케이스 코드가 도메인 로직에만 신경 써야 하고 입력 유효성 검증으로 오염되면 안된다고 말한다.  
> 그래서 입력 유효성 검증은 곧 살펴볼 다른 곳에서 처리한다.
>
> 그러마 유스케이스는 비즈니스 규칙을 검증할 책임이 있다.  
> 그리고 도메인 엔티티와 이 책임을 공유한다.
> 책의 후반에서 입력 유효성 검증과 비즈니스 규칙 검증의 차이점에 대해 말한다.

</br>

> 비즈니스 규칙을 충족하면 유스케이스는 입력 기반으로 어떤 방법으로든 모델의 상태를 변경한다.  
> 일반적으로 도메인 객체의 상태를 바꾸고 영속성 어댑터를 통해 구현된 포트로 이 상태를 전달해서 저장할 수 있게 한다.  
> 유스케이스는 또 다른 아웃고잉 어댑터를 호출할 수도 있다.
> 즉,

- 비즈니스 규칙 충족
- 유스케이스가 입력 기반으로 모델 상태 변경
- 도메인 객체 상태를 변경 후 영속성 어댑터로 구현된 포트로 상태 저장
- 마지막 단계에서 아웃고잉 어댑터에서 온 출력 값을, 유스케이스를 호출한 어댑터로 반환할 출력 객체로 변환

</br>

> 이 단계들을 염두에 두고 `송금하기` 유스케이스를 구현해보자

```java
package buckpal.cleanarchitecture.account.application.service;

import org.springframework.transaction.annotation.Transactional;

import buckpal.cleanarchitecture.account.application.port.in.SendMoneyCommand;
import buckpal.cleanarchitecture.account.application.port.in.SendMoneyUseCase;
import buckpal.cleanarchitecture.account.application.port.out.AccountLock;
import buckpal.cleanarchitecture.account.application.port.out.LoadAccountPort;
import buckpal.cleanarchitecture.account.application.port.out.UpdateAccountStatePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class SendMoneyService implements SendMoneyUseCase {

	private final LoadAccountPort loadAccountPort;

	private final AccountLock accountLock;

	private final UpdateAccountStatePort updateAccountStatePort;

	@Override
	public boolean sendMoney(SendMoneyCommand command) {
		// TODO : 비즈니스 규칙 검증
		// TODO : 모델 상태 조작
		// TODO : 출력값 반환

		return false;
	}
}
```

| 하나의 서비스가 하나의 유스케이스를 구현하고, 도메인 모델을 변경하고 상태를 저장하기 위해 아웃고잉 포트를 호출한다 |
| :----------------------------------------------------------------------------------------------------------------: |
|                                       ![_04_usecase](./res/_04_usecase.jpeg)                                       |

</br>

- 서비스는 인커밍 포트 인터페이스인 SendMoneyUseCase를 구현한다.
- 계좌를 불러오기 위해 아웃고잉 포트 인터페이스인 LoadAccountPort를 호출한다.
- 그리고 데이터베이스의 계좌 상태를 업데이트 하기 위해 UpdateAccountStatePort를 호출한다.

> 앞의 코드에서 // TODO로 남겨둔 부분을 살펴보자

</br>

### 입력 유효성 검증

</br>

> 호출하는 어댑터가 유스케이스에 입력을 전달하기 전에 입력 유효성을 검증하면 어떨까?  
> 과연 유스케이스에서 필요로 하는 것을 호출자가 모두 검증했다고 믿을 수 있을까?  
> 또, 유스케이스는 하나 이상의 어댑터에서 호출될 텐데, 그러면 유효성 검증을 각 어댑터에서 모두 구현해야 한다.  
> 그럼 그 과정에서 실수할 수도 있고, 유효성 검증을 해야한다는 사실을 잊어버릴 수도 있다.
>
> 애플리케이션 계층에서 입력 유효성을 검증해야 하는 이유는  
> 그렇지 않을 경우 애플리케이션 코어의 바깥쪽으로 부터 유효하지 않은 입력값을 받게 되고, 모델의 상태를 해칠 수 있기 때문이다.
>
> 유스케이스 클래스가 아니라면 도대체 어디에서 입력 유효성을 검증해야 할까?
>
> 입력 모델이 이 문제를 다루도록 해보자.  
> `송금 하기` 유스케이스에서 입력 모델은 예제 코드에서 본 SendMoneyCommand 클래스다.  
> 더 정확히 말하자면 생성자 내에서 입력 유효성을 검증할 것이다.

</br>

```java
package buckpal.cleanarchitecture.account.application.port.in;

import static buckpal.cleanarchitecture.account.domain.Account.*;

import buckpal.cleanarchitecture.account.domain.Money;
import lombok.Getter;

@Getter
public class SendMoneyCommand {

	private final AccountId sourceAccountId;

	private final AccountId targetAccountId;

	private final Money money;

	public SendMoneyCommand(AccountId sourceAccountId,
		AccountId targetAccountId, Money money) {
		this.sourceAccountId = sourceAccountId;
		this.targetAccountId = targetAccountId;
		this.money = money;
	}
}
```

</br>

> 송금을 위해서는 출금 계좌와 입금 계좌의 ID, 송금할 금액이 필요하다.  
> 모든 파라미터가 null이 되어서는 안되고, 송금액은 0보다 커야 한다.  
> 이러한 조건 중 하나라도 위배되면 객체를 생성할 때 예외를 던져서 객체 생성을 막으면 된다.
>
> SendMoneyCommand의 필드에 final을 지정해 불변 필드로 만들었다.  
> 일단 생성에 성공하고 나면 상태는 유효하고 이후에 잘못된 상태로 변경할 수 없다는 사실을 보장할 수 있다.
>
> SendMoneyCommand는 유스케이스 API의 일부이기 때문에 인커밍 포트 패키지에 위치한다.  
> 그러므로 유효성 검증이 애플리케이션의 코어에 남아있지만 유스케이스 코드를 오염시키지 않는다.
>
> 그런데 이런 귀찮은 작업들을 대신해 줄 수 있는 도구가 이미 있는데  
> 굳이 모든 유효성 검증을 직접 구현해야할까?  
> 자바 세계에서는 Bean Validation API가 이러한 작업을 위한 사실상의 표준 라이브러리다.  
> 이 API를 이용하면 필요한 유효성 규칙들을 필드의 애너테이션으로 표현할 수 있다.

</br>

- SelfValidating 구현

```java
package buckpal.cleanarchitecture.common;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public abstract class SelfValidating<T> {

	private Validator validator;

	public SelfValidating() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	protected void validateSelf() {
		Set<ConstraintViolation<T>> violations = validator.validate((T)this);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
	}
}

```

> SelfValidating 추상 클래스는 validateSelf()를 제공한다.  
> 그 후 생성자 코드의 마지막 문장에서 이 메서드를 호출한다.  
> 이 메서드가 필드에 지정된 Bean Validation 애너테이션을 검증하고,  
> 유효성 검증 규칙을 위반한 경우 예외를 던진다.  
> Bean Validation 검증 규칙을 표현하기에 충분하지 않다면 송금액이 0보다 큰지 검사했던 것처럼 직접 구현할 수 있다.

</br>

- SendMoneyCommand, Bean Validation을 통해서

```java
package buckpal.cleanarchitecture.account.application.port.in;

import static buckpal.cleanarchitecture.account.domain.Account.*;

import javax.validation.constraints.NotNull;

import buckpal.cleanarchitecture.account.domain.Money;
import buckpal.cleanarchitecture.common.SelfValidating;
import lombok.Getter;

@Getter
public class SendMoneyCommand extends SelfValidating<SendMoneyCommand> {

	@NotNull
	private final AccountId sourceAccountId;

	@NotNull
	private final AccountId targetAccountId;

	@NotNull
	private final Money money;

	public SendMoneyCommand(AccountId sourceAccountId,
		AccountId targetAccountId, Money money) {
		this.sourceAccountId = sourceAccountId;
		this.targetAccountId = targetAccountId;
		this.money = money;
		this.validateSelf();
	}
}

```

> 입력 모델에 있는 유효성 검증 코드를 통해 유스케이스 구현체 주위에 사실상 오류 방지 계층을 만들었다.  
> 여기서 말하는 계층은 하위 계층을 호출하는 계층형 아키텍처에서의 계층이 아니라  
> 잘못된 입력을 호출자에게 돌려주는 유스케이스 보호막을 의미한다.

</br>

### 생성자의 힘

</br>

> 앞에서 살펴본 입력 모델인 SendMoneyCommand는 생성자에 많은 책임을 지우고 있다.  
> 클래스가 불변이기 때문에 생성자의 리스트에는 클래스의 각 속성에 해당하는 파라미터들이 포함돼 있다.  
> 그 뿐만 아니라 생성자가 파라미터의 유효성 검증까지 하고 있기때문에 유효하지 않은 상태의 객체를 만드는 것은 불가능 하다.
>
> 예제 코드의 생성자엔 3개의 파라미터만 있는데 더 많으면 어떻게 할까?  
> 빌더 패턴을 활용하면 더 편하게 사용할 수 있을까?  
> 긴 파라미터 리스트를 받아야하는 생성자를 private으로 만들고  
> 빌더의 build() 메서드 내부에 생성자 호출을 숨길 수 있다.  
> 그러면 파라미터가 20개인 생성자를 호출하는 대신 다음과 같이 객체를 만들 수 있을 것이다.

```java
new SendCommandBuilder()
  .sourceAccountId(new AccountId(41L))
  .targetAccountId(new AccountId(42L))
  // ... 다른 필드
  .build()
```

> 유효성 검증 로직은 생성자에 그대로 둬서 빌더가 유효하지 않은 상태의 객체를 생성하지 못하도록 막을 수 있다.
>
> 그렇다면 SendMoneyCommandBuilder에 필드를 계속 추가해야할 상황을 생각해보자.  
> 먼저 생성자와 빌더에 새로운 필드를 추가한다.  
> 그 후 빌더를 호출하는 코드에 새로운 필드를 추가하는 것을 잊고 만다...
>
> 컴파일러는 이처럼 유효하지 않은 상태의 불변 객체를 만들려는 시도에 대해서 경고하지 못한다.  
> 물론 런타임에 유효성 검증 로직이 동작해서 예외를 던지긴 하지만 말이다.

</br>

- 내생각
  - 사실 결정적으로 내가 빌더를 사용하지 않는 이유..
  - 객체를 생성하는 시점에 컴파일 타임에 null pointer exception에 항상 노출 되어 있다.

</br>

### 유스케이스마다 다른 입력 모델

</br>

> 각기 다른 유스케이스에 동일한 입력 모델을 사용하고 싶은 생각이 들 때가 있다.  
> `계좌 등록하기`와 `계좌 정보 업데이트 하기`라는 두 가지 유스케이스를 보자.  
> 둘 모두 거의 똑같은 계좌 상세 정보가 필요하다.
>
> 차이점은 계좌 정보 업데이트하기 유스케이스는 업데이트할 계좌를 특정하기 위해 계좌 ID 정보를 필요로 하고  
> 계좌 등록하기 유스케이스는 계좌를 귀속시킬 소유자의 ID 정보를 필요로 한다는 것이다.
>
> 따라서 두 유스케이스에서 같은 입력 모델을 공유할 경우 계좌 정보 업데이트하기에서는 소유자 ID에  
> 계좌 등록하기에서는 계좌 ID에 null 값을 허용해야 한다.

</br>

> 불변 커맨드 객체의 필드에 대해서 null을 유효한 상태로 받아들이는 것은 그자체로 code smell이다.  
> 하지만 더 문제가 되는 부분은 이제 입력 유효성을 어떻게 검증하는 것이다.  
> 등록 유스케이스와 업데이트 유스케이스는 서로 다른 유효성 검증로직이 필요하다.  
> 아마도 유스케이스에 커스텀 검증 로직을 넣어야할 테고, 이는 비즈니스 코드를 입력 유효성 검증과 관련된 관심사로 오염된다.

</br>

> 또, 만약 `계좌 등록하기` 유스케이스에서 계좌 ID 필드에 우연히 null 값이 아닌 값이 들어오면 어떻게 할까?  
> 에러를 던질까? 그냥 무시할까?
>
> 각 유스케이스 전용 입력 모델은 유스케이스를 더 명확하게 만들고  
> 다른 유스케이스와의 결합도 제거해서 불필요한 부수효과를 발생하지 않게 한다.  
> 물론 비용이 안드는 것은 아니지만 들어오는 데이터를 각 유스케이스에 해당하는 입력 모델에 매핑해야 하기 때문이다.

</br>

### 비즈니스 규칙 검증하기

</br>

> 입력 유효성 검증은 유스케이스 로직의 일부가 아닌 반면, 비즈니스 규칙 검증은 분명히 유스케이스 로직의 일부다.  
> 그러면 언제 입력 유효성을 검증하고 언제 비즈니스 규칙을 검증해야 할까?
>
> 둘의 실용적인 구분점은 비즈니스 규칙을 검증하는 것은 도메인 모델의 현재 상태에 접근해야하는 반면  
> 입력 유효성 검증은 그럴 필요가 없다는 것이다.  
> 입력 유효성을 검증하는 일은 @NotNull 애너테이션을 붙인 것처럼 선언적으로 구현할 수 있지만  
> 비즈니스 규칙을 검증하는 일은 조금 더 맥락이 필요하다.
>
> `출금 계좌는 초과 출금되어서는 안된다`라는 규칙을 보자.  
> 정의에 따르면 이 규칙은 출금 계좌와 입근 계좌가 존재하는지 확인하기 위해 모델의 현재 상태에 접근해야함으로 비즈니스 규칙이다.
>
> 반대로 `송금되는 금액은 0보다 커야한다`는 규칙은 모델에 접근하지 않고도 검증 될 수 있다.  
> 그러므로 입력 유효성 검증으로 구현할 수 있다.

</br>

- 입력 유효성 검증과 비즈니스 규칙 검증의 차이는 모델의 상태에 접근하는지의 여부!

</br>

```java
package buckpal.cleanarchitecture.account.domain;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {

  //...

	public boolean withdraw(Money money, AccountId targetAccountId){
		if(!mayWithdraw(money)){
			return false;
		}

		Activity withdrawal = new Activity(
			this.id,
			this.id,
			targetAccountId,
			LocalDateTime.now(),
			money
		);

		this.activityWindow.addActivity(withdrawal);
		return true;
	}

	private boolean mayWithdraw(Money money){
		return Money.add(
				this.calculateBalance(),
				money.negate()
			).isPositive();
	}
}
```

- 비즈니스 규칙을 검증하는 가장 좋은 방법은 비즈니스 규칙을 도메인 엔티티에 넣는 것이다.

</br>

> 만약 도메인 엔티티에서 비즈니스 규칙을 검증하기 애매하다면 유스케이스코드에서 도메인 엔티티를 사용하기 전에 해도 된다.

</br>

### 풍부한 도메인 모델 vs 빈약한 도메인 모델

</br>

> 풍부한 도메인 모델에서는 애플리케이션의 코어에 있는 엔티티에서 가능한 많은 도메인 로직이 구현된다.  
> 엔티티들은 상태를 변경하는 메서드를 제공하고, 비즈니스 규칙에 맞는 유효한 변경만을 허용한다.
>
> 유스케이스는 도메인 모델의 진입점으로 동작한다.  
> 이어서 유스케이스는 사용자의 의도만을 표현하면서 이 의도를 실제 작업을 수행하는 체계화된 도메인 엔티티 메서드 호출로 변환한다.  
> `많은 비즈니스 규칙이 유스케이스 구현체 대신 엔티티에 위치한다.`
>
> 빈약한 도메인 모델에서는 엔티티가 얇다.  
> 상태를 표현하는 필드와 값을 읽고 바꾸기 위한 getter, setter 메서드만 포함하고 어떤 도메인 로직도 가지고 있지 않다.  
> 이말은 즉슨 도메인로직이 엔티티 클래스 내부가 아니라 유스케이스 클래스에 구현돼 있다는 것이다.  
> 비즈니스 규칙을 검증하고 엔티티의 상태를 바꾸고, 데이터베이스 저장을 담당하는 아웃고잉 포트에 엔티티를 전달할 책임 역시 유스케이스 클래스에 있다.

</br>

### 읽기 전용 유스케이스는 어떨까?

</br>

> 계좌 잔고 보여주기 라고 부를 수 있는 특정 유스케이스를 구현하기 위해 요청한 데이터가 필요할 수도 있다.  
> 만약 전체 프로젝트의 맥락에서 이러한 작업이 유스케이스로 분류된다면 어떻게든 다른 유스케이스와 비슷한 방식으로 구현해야 한다.
>
> 하지만 애플리케이션 코어의 관점에서는 이 작업은 간단한 데이터 쿼리이다.  
> 그렇기에 프로젝트 맥락에서 유스케이스로 간주되지 않으면 실제 유스케이스와 구분하기 위해 쿼리로 구현할 수 있다.
> 이 책의 아키텍처 스타일에서 이를 구현하는 한 가지 방법은 쿼리를 위한 인커밍 전용 포트를 만들고  
> 이를 쿼리 서비스에 구현하는 것이다.

</br>

```java
package buckpal.cleanarchitecture.account.application.port.in;

import static buckpal.cleanarchitecture.account.domain.Account.*;

import buckpal.cleanarchitecture.account.domain.Money;

public interface GetAccountBalanceQuery {

	Money getAccountBalance(AccountId accountId);

}

```

</br>

```java
package buckpal.cleanarchitecture.account.application.service;

import static buckpal.cleanarchitecture.account.domain.Account.*;

import java.time.LocalDateTime;

import buckpal.cleanarchitecture.account.application.port.in.GetAccountBalanceQuery;
import buckpal.cleanarchitecture.account.application.port.out.LoadAccountPort;
import buckpal.cleanarchitecture.account.domain.Money;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAccountBalanceService implements GetAccountBalanceQuery {

	private final LoadAccountPort loadAccountPort;

	@Override
	public Money getAccountBalance(AccountId accountId) {
		return loadAccountPort.loadAccount(accountId, LocalDateTime.now())
			.calculateBalance();
	}
}

```

</br>

> 쿼리 서비스는 유스케이스 서비스와 동일한 방식으로 동작한다.  
> GetAccountBalanceQuery라는 인커밍 포트를 구현하고  
> 데이터베이스로부터 실제로 데이터를 로드하기 위해 LoadAccountPort라는 아웃고잉 포트를 호출한다.
>
> 이처럼 읽기 전용 쿼리는 쓰기가 가능한 유스케이스와ㅏ 코드 상에서 명확하게 구분된다.  
> 이런 방식은 CQS나 CQRS같은 개념과 잘 맞다.

</br>

### 유지보수 가능한 소프트웨어를 만드는데 어떻게 도움을 줄까?

</br>

> 이책의 아키텍처는 도메인 로직을 내 마음대로 구현할 수 있도록 허용하지만,  
> 입출력 모델을 독립적으로 모델링한다면 원치않는 부수효과를 피할 수 있다.
>
> 물론 유스케이스 간에 모델을 공유하는 것보다 더 많은 작업이 필요하고  
> 각 유스케이스마다 별도의 모델을 만들어야 하고, 이 모델과 엔티티를 매핑해야 한다.
>
> 그러나 유스케이스별로 모델을 만들면 유스케이스를 명확하게 이해할 수 있고  
> 장기적으로 유지보수하기도 쉽다.  
> 꼼꼼한 입력 유효성 검증, 유스케이스별 입출력 모델은 지속 가능한 코드를 만드는데 도움이 될 것이다.

</br>

## 05. 웹 어댑터 구현하기

</br>

</br>

## 06. 영속성 어댑터 구현하기

</br>

</br>

## 07. 아키텍처 요소 테스트 하기

</br>

</br>

## 08. 경계간 매핑하기

</br>

</br>

## 09. 애플리케이션 조립하기

</br>

</br>

## 10. 아키텍처 경계 강제하기

</br>

</br>

## 11. 의식적으로 지름길 사용하기

</br>

</br>

## 12. 아키텍처 스타일 정하기

</br>

</br>