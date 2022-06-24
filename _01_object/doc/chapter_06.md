# CHAPTER 06. 메시지와 인터페이스

</br>

> 객체지향에 대한 가장 큰 오해는 애플리케이션이 클래스의 집합으로 구성된다는 것  
> 객체지향을 위해서는 클래스가 아니라 `객체를 지향해야한다.`  
> `즉, 협력 안에서 객체가 수행하는 책임에 초점을 맞춰야 한다는 것`

</br>

- `객체지향에서 가장 중요한 것은 클래스가 아니라 객체들이 주고받는 메시지다.`
- 애플리케이션은 클래스로 구성되지만 메시지를 통해 정의된다.

</br>

## 협력과 메시지

</br>

> 우리가 흔히 웹애플리케이션을 만들 때  
> 클라이언트로부터 요청을 받은 서버는 응답을 위해  
> 다른 객체들과 소통하여 정보를 얻은 후에 클라이언트가  
> 요구하는 정보를 반환해 준다.

</br>

- 협력안에서 메시지 전송쪽을 클라이언트
- 메시지 수신쪽을 서버라 지칭
- 협력이란 클라이언트가 서버의 서비스를 요청하는 단방향 상호작용

</br>

|              Screening - Movie               |
| :------------------------------------------: |
| ![상영 - 영화](../res/_06_client_server.png) |

</br>

> Screening은 Movice에게 가격을 `계산하라`는 메시지를 보낸다.

</br>

|               Movie - DiscountPolicy               |
| :------------------------------------------------: |
| ![영화 - 할인 조건](../res/_06_client_server2.png) |

</br>

> 요청을 받은 Movie는 할인 요금을 계산하기 위한 정보가 부족하여  
> DiscountPolicy에게 `할인 요금을 계산하라`는 메시지를 보낸다.

</br>

> 위의 사례로 알 수 있듯, 객체간의 협력은 클라이언트와 서버의 역할을 동시에 수행 한다.  
> 따라서 우리는 객체가 수신하는 메시지에만 초점을 맞추는 것이 아니라  
> `외부의 전송하는 메시지 집합까지도 고려하는 것이 바람직하다.`

</br>

- 용어 정리

  - 메시지 : 객체들이 협력하기 위해 사용하는 유일한 의사소통
  - 메시지 전송(패싱) : 한 객체가 다른 객체에게 도움을 요청하는 것
  - 메시지 전송자
  - 메시지 수신자

- 메시지 구성 요소
- 오퍼레이션명
- 인자
- 메시지 수신자

</br>

|              message               |
| :--------------------------------: |
| ![message](../res/_06_message.png) |

</br>

> 메시지를 수신했을 때 실제로 어떤 코드가 실행되는지 결정하는 것은 메시지 수신자의 타입  
> condition이 PeriodCondition이냐 SequenceCondition이냐에 따라서 달라진다.  
> 즉 메시지를 수신했을 때 `실제로 실행되는 함수 또는 프로시저를 메서드라 지칭`
> 메시지와 메서드가 실행시점에 연결되어야 함으로 컴파일 타임과 런타임에서의 의미가 달라질 수 있다.

</br>

### 여기서 다시 한 번 중간 정리

> 그러니까 저자의 메시지와 메서드를 구분하라는 말은,  
> 다형성을 살려, 메시지 전송자가 어떤 메시지를 요청할 건지만 알면 된다는 것이다.  
> 메시지 수신자도 그냥 행위만 처리해줄 뿐, 구체적인 타입이 컴파일 타임에 정해지지 않아,  
> `메시지 메서드 구분하라! = 다형성을 잘 살리면`  
> 결합도가 낮아진다!

</br>

- 용어 정리
  - 오퍼레이션 : 인터페이스 각 요소로서 구현이 아닌 추상화
  - 메서드 : 오퍼레이션을 구현한 것

## 인터페이스 설계 품질

</br>

> 좋은 인터페이스는  
> `최소한의 인터페이스`, 즉 꼭 필요한 오퍼레이션만을 포함  
> `추상적인 인터페이스`, 어떻게 수행할지가 아닌 무엇을 할지를 표현

</br>

### 디미터 법칙

</br>

> 객체 내부 구조에 강하게 결합되지 않도록 협력 경로를 제한하라는 것.  
> 그러니까 ERD에서 최대한 의존성이 근접하게 물려있는 객체들하고만  
> 메시지를 주고받으라는 것.

</br>

> 모든 클래스 C와 C에서 구현된 모든 메서드 M에 대하여  
> M이 메시지를 전송할 수 있는 모든 객체는

</br>

- M의 인자로 전달된 클래스(C 자체 포함)
- C의 인스턴스 변수의 클래스

</br>

- this 객체
- 메서드의 파라미터
- this의 속성
- this의 속성인 컬렉션의 요소
- 메서드 내에서 생성된 지역 객체

</br>

```java
public class ReservationAgency{
  // Screening에 있는 Movie를 꺼내서 계산하는 것이 아니라, 메시지를 던지고 책임을 위임
  public Reservation reserve(Screening screening, Customer customer, int audienceCount){
    Money fee = screening.calculateFee(audienceCount);
    return new Reservation(customer, screening, fee, audienceCount);
  }
}
```

> ReservationAgency는 Screening에 메시지를 전송하기만 하여 screening 내부에 대한 정보를 모른다.  
> 즉, Screening의 내부 구조 및 속성과 결합되어 있지 않아서  
> Screening이 변경된다 하더라도 ReservationAgency를 함께 변경할 필요가 없다.

</br>

- 캡슐화는 클래스 내부의 구현을 감춰야 한다는 것을 강조
- 디미터 법칙은 `협력하는 클래스의 캡슐화를 지키기 위해 접근해야하는 요소를 제한하는 것`
- 정보를 처리하는 데 `필요한 책임을 정보를 알고 있는 객체에게 할당`하기 때문에 응집도가 높은 객체가 만들어짐

```java
// 디미터 법칙을 위반하는 코드의 전형적인 모습
screening.getMovie().getDiscountConditions();
```

</br>

### 묻지 말고 시켜라

> 메시지 전송자는 메시지 수신자의 상태를 알고 메시지를 보내는 것이 아님.  
> 메시지 수신자의 상태를 기반으로 결정을 내린 후에  
> 수신자의 상태를 바꿔서는 안된다

- 객체 내부 상태를 알고 명령을 결정하는 것 -> 캡슐화 위반.

</br>

- 오퍼레이션이 내부 상태를 요구한다면 설계를 다시 고민
  - 항상 행동을 요청하는 오퍼레이션을 고려하여 인터페이스를 향상 시킬 것
  - 그리고 행동을 요청하되 `오퍼레이션이 어떻게 동작하는지 노출해서도 안된다.`

</br>

### 의도를 드러내는 인터페이스 : 어떻게 동작하는지가 아니라, 무엇을 하느냐!

- 아래같으 코드가 안좋은 이유

```java
public classs PeriodCondition{
  public boolean isSatisfiedByPeriod(Screening Screening){

  }
}

public classs SequenceCondition{
  public boolean isSatisfiedBySequence(Screening Screening){

  }
}

```

- 1. 메서드가 제대로 소통하지 못한다.
  - 둘 다 조건이 만족하는지 판별하는 동일한 작업을 수행한다.
  - 하지만 메서드 네이밍이 달라 두 메서드 내부 구조를 이해해야함.
  - `어떻게 동작하는지 유추 가능`
- 2. 메서드 수준에서 캡슐화 위반
  - 협력하는 객체의 종류를 알도록 한다,
  - 참조하는 객체 ..Condition 뿐만 아니라 호출하는 메서드도 변경해야하기 때문!

</br>

```java
public classs PeriodCondition implements DiscountCondtion{
  public boolean isSatisfiedBy(Screening Screening){

  }
}

public classs SequenceCondition implements DiscountCondition{
  public boolean isSatisfiedBy(Screening Screening){

  }
}

```

</br>

> 앞장에서 계속 말하든 행위(operation)이 정해지고 추상화가 정해진다면  
> 다형성을 활용하자 -> 합성  
> 메서드만 같다고 동작하지 않는다. -> `JAVA가 Complie 언어야!`

</br>

```java
public class Theater {
    private TicketSeller ticketSeller;

    public Theater(TicketSeller ticketSeller) {
        this.ticketSeller = ticketSeller;
    }

    public void enter(Audience audience) {
        if (audience.getBag().hasInvitation()) {
            Ticket ticket = ticketSeller.getTicketOffice().getTicket();
            audience.getBag().setTicket(ticket);
        } else {
            Ticket ticket = ticketSeller.getTicketOffice().getTicket();
            audience.getBag().minusAmount(ticket.getFee());
            ticketSeller.getTicketOffice().plusAmount(ticket.getFee());
            audience.getBag().setTicket(ticket);
        }
    }
}
```

> 디미터 법칙에서 Theater가 인자로 전달된 audience와 ticketSeller에 메시지를 전달하는 것은 문제 X  
> 문제는 지금 다른 객체의 프로퍼티에 마구 접근한다는 것.

</br>

> 위와같이 프로퍼티에 접근하거나  
> 프로퍼티 정보를 마구 가져와서 사용하는 행위 모두  
> 결합도를 높이는 행위다.  
> 또한 내부 속성을 가져온다는 것은 `클래스 내부 구조를 모두 알고 있다는 것!` -> `캡슐화 어김`

</br>

```java
public class TicketSeller{
  public void setTicket(Audience audience){
    if(audience.getBag().hasInviatation()){
      Ticket ticket = ticketOffice.getTicket();
      audience.getBag().setTicket(ticket);
    }
    else{
      Ticket ticket = ticketOffice.getTicket();
      audience.getBag().minusAmount(ticket.getFee());
      ticketOffice.plusAmount(ticket.getFee());
      audience.getBag().setTicket(ticket);
    }
  }
}
```

</br>

> 현재 Theater와 TicketSeller만 의존성을 가지도록 하였다.  
> 이제 TicketSeller는 Audiencerk ticket을 가지도록 하는 것임으로  
> Audience에 setTicket 메서드를 추가함으로써 스스로 티켓을 가지도록 하자

</br>

```java
public class Audience{
  public Long setTicket(Ticket ticket){
    if(bag.hasInvitation()){
      bag.setTicket(ticket);
      return 0L;
    }
    else{
      bag.setTicket(ticket);
      bag.minusAmount(ticket.getFee());
      return ticket.getFee();
    }
  }
}
```

</br>

```java
public class TicketSeller {
    private TicketOffice ticketOffice;

    public void setTicket(Audience audience) {
        ticketOffice.plusAmount(audience.setTicket(ticketOffice.getTicket()));
    }

```

> 티켓셀러가 관객에게 티켓을 할당하는 행위에서  
> audience가 setTicket을 호출하는데,  
> 이때 bag의 hasInvitation()을 통해  
> 초대권 여부를 묻는데, 메시지를 통해 서로 협력하는 것이 아니라  
> 그냥 내부 속성을 알고 호출함으로 디미터 법칙에 위반된다.

</br>

```java
public class Audience {
    private Bag bag;

    public Audience(Bag bag) {
        this.bag = bag;
    }

    public Long setTicket(Ticket ticket) {
        return bag.setTicket(ticket);
    }

}
```

- 나도 항상 생각할 것
  - 도메인이 크게 잘못되지 않았다면 되도록 의존성을 가진 객체들끼리만 결합할 것.
    - 생각없이 객체 박지 않기.
  - `저런식으로 Audience에 던져주고, 다시 내부 객체가 메시지 호출하여 책임 위임하는 것도 고려해보기`

</br>

### 다시 한 번 인터페이스에 의도 드러내기

- TicketSeller -> Audience : 티켓을 파는 행위, 반대로 관객은 사는 행위
- Audience -> Bag : 티켓을 보관하는 행위

```java
public class TicketSeller{
  public void sellTo(Audience audience){}
}

public class Audience{
  public Long buy(Ticket ticket){}
}

pulbic class Bag{
  public Long hold(Ticket ticket){}
}

```

> 객체입장에서 동일한 액션이라도  
> `의도가 분명한 네이밍 고민하기`

</br>

## 원칙의 함정

</br>

```java
public clalss PeriodCondtion implements DiscountCondition{
  public boolean isSatisfiedBy(Screening screening){
    return screening.getStartTime().getDayOfWeek().equals(dayOfWeek) &&
      startTime.compareTo(screening.getStartTime().toLocalTime() <= 0 &&
      endTime.compareTo(screening.getStartTime().toLocalTime()) >= 0;
  }
}
```

> 현재 위의 코드는 Screenin의 내부 속성을 모두 가져와서  
> 메서드의 구현을 결정한다. -> `캡슐화를 어긴것?`

```java
public class Screening{
  public boolean isDiscountable(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime){
    ...
  }
}

public class PeriodCondtion implements DiscountCondtion{
  public boolean isSatisfiedBy(Screening screening){
    return screening.isDiscountable(dayOfWeek, startTime, endTime);
  }
}
```

> 이런식으로 처리한다면 Screening이 내부 할인 조건을 판단하는 책임을 가져야한다.  
> `Screening이 갖는 적합한 책임인지..?`

- 또한 이것이 결합도를 높인다.
  - PeriodCondition의 속성들을 파라미터로(dayOfWeek...) 넘겨주기 때문에
  - 기간할인 조건이 바뀌게 될 경우(
    - 예시 : 시작 시간 이후 모두 할인해주겠다... 하면 endTime도 필요없고.. 또 Screening도 수정되고..

</br>

> 이 경우 캡슐화를 조금 낮추더라도  
> Screening의 응집도를 높이고 Screenign과 PeriodCondition의 결합도를 낮추는 것이  
> 더 좋은 방법! -> `결국 설계는 기회비용이다.`

</br>

## 명령 쿼리 분리 원칙

</br>

- 루틴 : 어떤 절차를 묶어 호출 가능하도록 이름을 부여한 기능 모듈
  - 프로시저 : 정해진 절차에 따라 내부의 상태를 변경
  - 함수 : 어떤 절차에 따라 필요한 값을 계산해서 반환

</br>

> 둘 다 루틴의 종류이고, 부수효과와 반환값의 유무라는 측면에서 명확하게 구분되는 개념  
> 프로시저는 부수효과를 발생시킬 수 있지만, 값을 반환할 수 없다.  
> 함수는 값을 반환할 수 있지만 부수효과를 발생 시킬 수 없다.

</br>

- 명령과 쿼리
  - 명령 : 객체의 상태를 수정하는 오퍼레이션 - 프로시저
  - 쿼리 : 객체와 관련된 정보를 반환하는 오퍼레이션 - 함수

> 어떠한 오퍼레이션도 `명령인 동시에 쿼리여서는 안된다.`

- 객체의 상태를 변경하는 명령은 반환값을 가질 수 없다.
- 객체의 정보를 반환하는 쿼리는 상태를 변경할 수 없다.

</br>

### 반복 일정의 명령과 쿼리 분리하기

</br>

- 반복 일정 : 매주 수요일 10시 30분부터 11시까지 회의가 반복되는 일정
- 이벤트 : 그리고, 위와같이 특정 일자와 시간에 발생하는 사건이 바로 이벤트

</br>

```java
public class Event {
    private String subject;
    private LocalDateTime from;
    private Duration duration;

    public Event(String subject, LocalDateTime from, Duration duration) {
        this.subject = subject;
        this.from = from;
        this.duration = duration;
    }

    public boolean isSatisfied(RecurringSchedule schedule) {
        if (from.getDayOfWeek() != schedule.getDayOfWeek() ||
                !from.toLocalTime().equals(schedule.getFrom()) ||
                !duration.equals(schedule.getDuration())) {
            reschedule(schedule);
            return false;
        }

        return true;
    }

    private void reschedule(RecurringSchedule schedule) {
        from = LocalDateTime.of(from.toLocalDate().plusDays(daysDistance(schedule)),
                schedule.getFrom());
        duration = schedule.getDuration();
    }

    private long daysDistance(RecurringSchedule schedule) {
        return schedule.getDayOfWeek().getValue() - from.getDayOfWeek().getValue();
    }
}
```

> isSatisfied 함수는 반복일정이 만족 되는지 체크하고,  
> 만족하지 않는 경우 private 메서드로 Event를 schedule로 수정하는 메서드인 rechedule 호출

```java
public class RecurringSchedule {
    private String subject;
    private DayOfWeek dayOfWeek;
    private LocalTime from;
    private Duration duration;

    public RecurringSchedule(String subject, DayOfWeek dayOfWeek,
                             LocalTime from, Duration duration) {
        this.subject = subject;
        this.dayOfWeek = dayOfWeek;
        this.from = from;
        this.duration = duration;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getFrom() {
        return from;
    }

    public Duration getDuration() {
        return duration;
    }
}
```

</br>

> Recurring Schedule은 반복되는 일정을 정의하기 위한 클래스  
> 일정의 주제(subject)와 반복될 요일(day), 시작 시간(from), 기간(duration)을 포함

</br>

- 개발자 팀을 당혹시키게 한 버그

</br>

```java
RecurringSchedule schedule = new RecurringSchedule("회의", DayOfWeek.WEDNESDAY,
  LocalaTime.of(10,30), Duration.ofMinutes(30));

Event meeting = new Event("회의", LocalTime.of(2019, 5, 9 , 10, 30), Duration.ofMinutes(30));

asssert meeting.isSatisfied(schedule) == false;
asssert meeting.isSatisfied(schedule) == true;
```

> 위에서 언급했듯  
> isSatisfied 조건이 충족되지 않으면  
> reschedule 메서드를 호출한다. -> `Event의 상태까지 변경!`

</br>

> 즉 위 용어대로 다시 설명하면  
> isSatisfied는 쿼리 - 함수의 역할만을 요구하지만  
> 부수효과를 가진 명령 - 프로시저의 역할을 겸하고 있다.

</br>

- 명령과 쿼리 분리하기

```java
import java.time.Duration;
import java.time.LocalDateTime;

public class Event {
    private String subject;
    private LocalDateTime from;
    private Duration duration;

    public Event(String subject, LocalDateTime from, Duration duration) {
        this.subject = subject;
        this.from = from;
        this.duration = duration;
    }

    public boolean isSatisfied(RecurringSchedule schedule) {
        if (from.getDayOfWeek() != schedule.getDayOfWeek() ||
                !from.toLocalTime().equals(schedule.getFrom()) ||
                !duration.equals(schedule.getDuration())) {
            return false;
        }

        return true;
    }

    public void reschedule(RecurringSchedule schedule) {
        from = LocalDateTime.of(from.toLocalDate().plusDays(daysDistance(schedule)),
                schedule.getFrom());
        duration = schedule.getDuration();
    }

    private long daysDistance(RecurringSchedule schedule) {
        return schedule.getDayOfWeek().getValue() - from.getDayOfWeek().getValue();
    }
}
```

> isSatisfied는 순수하게 조건이 맞지 않을 경우 상태만 내보내주고 : 쿼리  
> rechedule은 from과 duration을 변경하는 부수효과를 가진다 : 명령
> 이제 클라이언트는 isSatisfied의 결과에 따라 rechedule을 수행할지 말지 판단해야할 것

</br>

### 명령 쿼리 분리와 참조 투명성

- 참조 투명성
  - 어떤 표현식 e가 있을 때 e의 값으로 e가 나타나는 모든 위치를 교체하더라도 결과가 달라지지 않는 특성

```plain text
f(1) + f(1) = 6
f(1) * 2    = 6
f(1) - 1    = 2
```

> 위의 f(1)을 3으로 값을 바꾸어도 식의 결과는 변하지 않는다.  
> 수학의 개념에서 f(1)은 불변이며 부수효과를 가지지 않는 값이기 때문  
> 위에서 `식의 순서를 변경해도 식의 결과가 보장된다.`

</br>

- 참조 투명성의 장점
  - 모든 함수를 이미 알고 있는 하나의 결괏값으로 대체할 수 있기 때문에 식을 쉽게 계산
  - 모든 곳에서 함수 결괏값이 동일하여 식의 순서를 변경하더라도 각 식의 결과는 달리지지 않음

</br>

> 객체지향 프로그래밍은 객체의 상태 변경이라는 부수효과를 기반으로 함으로  
> 참조 투명성은 예외에 가깝다.  
> 그래도 그나마 `명령-쿼리 분리 원칙을 사용하여 제한적으로 참조 투명성의 혜택을 누릴 수 있다.`

</br>

- 명령형 프로그래밍 : 부수효과를 기반으로 하는 프로그래밍 방식
  - 상태를 변경시키는 연산들을 적절한 순서대로 나열함으로싸 프로그램 작성
- 함수형 프로그래밍 : 부수효과가 존재하지 않는 수학적인 함수에 기반
  - 참조 투명성 장점 극대화
  - 명령형 프로그래밍 보다 실행 결과를 이해하고 예측하기 쉬움
  - `하드웨어의 발달로 병렬처리가 중요해져 인기 상승?`...?

</br>

### 책임에 초점을 맞춰라

</br>

- 디미터 법칙

> 협력이라는 컨텍스트 안에서 객체보다 `메시지를 먼저 결정`하면 두 객체 사이의 구조적인 결합도를 낮출 수 있다.  
> 수신할 객체를 알지 못한 상태에서 메시지를 먼저 선택함으로써 `객체의 내부 구조에 대해 고민할 필요 X`

</br>

- 묻지 말고 시켜라

> 메시지를 먼저 선택하면 협력을 구조화 하게 한다.  
> 클라이언트 관점에서 메시지를 선택하기 때문에,  
> 필요한 정보가 아닌 `원하는 것을 표현한 메시지를 전송하기만 하면 되는 것`

</br>

- 의도를 드러내는 인터페이스

> 메시지를 선택할 때 의도를 드러내는 메시지 이름을 지어라

</br>

- 명령 - 쿼리 분리 원칙

</br>

> 메시지를 선택한다는 것은 어떤일을 하는 것 뿐 아니라  
> `협력 속에서 객체의 상태를 예측가능하기 위한 방법을 고민`  
> 따라서, `부수효과를 고민`하여 명령과 쿼리를 분리하여 작성하라

</br>

> 훌륭한 메시지를 얻기 위해서 `객체가 아닌 메시지를 선택해야한다.`  
> 협력에 적합한 객체보다 협력에 적합한 메시지를 선택할 것
