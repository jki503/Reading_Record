# CHAPTER 01. 객체, 설계

## 01. 티켓 판매 애플리케이션 구현하기

</br>

- 상황

</br>

> 소극장을 운영하던 도중 작은 이벤트 기획  
> `이벤트는 추첨을 통해 선정된 관람객에게 공연을 무료로 관람할 수 잇는 초대장 발송`

</br>

- 이벤트 당첨된 관람객 : 초대장을 티켓으로 교환
- 일반 관람객 : 티켓을 구매

</br>

> 관람객 입장시키기 전에 이벤트 당첨 여부 확인  
> 이벤트 당첨자가 아닌 경우에는 티켓 판매 후 입장

</br>

```java
import java.time.LocalDateTime;

public class Invitation {
    private LocalDateTime when;
}
```

> 초대일자를 포함하는 초대장 클래스

</br>

```java
public class Ticket {
    private Long fee;

    public Long getFee() {
        return fee;
    }
}
```

> 공연을 관람하기 위한 모든 사람들은 티켓 소지 필수  
> 요금 인스턴스 변수를 가짐

</br>

```java
public class Bag {
    private Long amount;
    private Invitation invitation;
    private Ticket ticket;

    public Bag(long amount) {
        this(null, amount);
    }

    public Bag(Invitation invitation, long amount) {
        this.invitation = invitation;
        this.amount = amount;
    }

    public boolean hasInvitation() {
        return invitation != null;
    }

    public boolean hasTicket() {
        return ticket != null;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public void minusAmount(Long amount) {
        this.amount -= amount;
    }

    public void plusAmount(Long amount) {
        this.amount += amount;
    }
}
```

> Bag에서는 초대장, 돈, 티켓 세 개의 변수를 가진다.  
> 입장하기 위해서 먼저 Bag의 invitation의 null 여부를 판단하고,  
> invitation이 있는 경우 Ticket을 설정하고  
> 없는 경우에는 amount를 이용하여 Ticket을 설정해야한다.

</br>

- 생성자 만들기
  - All-args-constructor 및 가장 파라미터가 많은 생성자를 활용
  - null을 허용하거나 초기에 모든 변수의 값을 지정할 필요 없을 때 아래와 같이 사용

```java
    public Bag(long amount) {
        this(null, amount);
    }

    public Bag(Invitation invitation, long amount) {
        this.invitation = invitation;
        this.amount = amount;
    }
```

</br>

> 생성자 오버라이딩시 자주 사용하는 설계 방식  
> 개인적으로도 즐겨쓴다.

</br>

```java
public class Audience {
    private Bag bag;

    public Audience(Bag bag) {
        this.bag = bag;
    }

    public Bag getBag() {
        return bag;
    }
}
```

> 관람객은 소지품을 보관하기 위한 가방을 소지

</br>

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicketOffice {
    private Long amount;
    private List<Ticket> tickets = new ArrayList<>();

    public TicketOffice(Long amount, Ticket ... tickets) {
        this.amount = amount;
        this.tickets.addAll(Arrays.asList(tickets));
    }

    public Ticket getTicket() {
        return tickets.remove(0);
    }

    public void minusAmount(Long amount) {
        this.amount -= amount;
    }

    public void plusAmount(Long amount) {
        this.amount += amount;
    }
}
```

> 매표소는 티켓의 가격과 판매할 티켓을 보관한다.

</br>

```java
public class TicketSeller {
    private TicketOffice ticketOffice;

    public TicketSeller(TicketOffice ticketOffice) {
        this.ticketOffice = ticketOffice;
    }

    public TicketOffice getTicketOffice() {
        return ticketOffice;
    }
}
```

</br>

> 판매원은 매표소에서 초대장을 티켓으로 교환해 주는 티켓 판매의 역할  
> 따라서 TicketSeller 클래스는 자신이 일하는 ticket Office를 알아야한다.

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

</br>

- enter 메서드

> enter 메서드에서 현재 극장에 입장하기위한 로직 모두 처리

</br>

### 01. 티켓 애플리케이션 판매 코드 문제점 정리

</br>

> 대표적으로 Theater에서 관람객과 판매원이 조금 어색하다.

</br>

- 1. 관람객의 reference 타입인 변수 bag을 꺼내와?
  - 현재 극장에서 마음대로 관람객의 bag을 호출
    - `캡슐화의 잘못된 예`
  - `class 역할에 맞게 getter를 사용해라`
    - 굳이 노출시킬 필요가 있는지?
    - 노출 시킨다면, `단지 전달만 할 것인지 값을 변경하게 되는지 스스로 판단해보기`

</br>

## 02. 무엇이 문제인가

</br>

- 소프트웨어 모듈의 세가지 목적
  - 실행 중에 제대로 동작
  - 변경을 위해 존재
  - 코드를 읽는 사람과 소통

> 하지만 위 `코드들은 변경 용이성과, 의사소통 목적 만족 X`

</br>

### 예상을 빗나가는 코드

</br>

- enter 메서드의 기능을 말로 풀어보자!

> 소극장은 관람객의 가방을 열어 그 안에 초대장이 있는지 살펴본다.  
> 가방 안에 초대장이 들어 있으면 판매원은 매표소에 보관돼 있는 티켓을 관람객 가방으로 옮긴다.  
> 가방 안에 초대장이 들어 있찌 않다면 관람객의 갑ㅇ에서 티켓 금액만큼 현금 꺼내서  
> 매표소에 보관돼 있는 티켓을 관람객의 가방으로 옮긴다.

- 문제 1.

  - 소극장이라는 외부 클래스 제 3자가 초대장을 확인하기 위해 관람객의 가방을 마음대로 열어본다.
  - `audience.getBag().hasInvitation()`

- 문제 2.

  - 판매원 입장에서도 소극장이 매표소에 보관 중인 `티켓과 현금에 접근 가능`.
  - `Ticket ticket = ticketSeller.getTicketOffice().getTicket()`

- 문제 3.
  - 티켓을 꺼내 `관람객의 가방에 집어넣고 관람객에게서 받은 돈을 매표소에 적립하는 일도 소극장이 수행`
  - `audience.getBag().setTicket(ticket)`

</br>

> 현실에서는 관람객이 판매원과 소통하여 티켓을 얻는 과정인데  
> 지금은 소극장 theater가 모든 클래스의 변수를 불러들여  
> 비즈니스 로직을 다 처리해주는 구조

</br>

### 변경에 취약한 코드

- 관람객이 가방을 들고 있지 않을때?
- 현금이 아닌 신용카드?
- 판매원이 매표소 밖에서 티켓을 판매할때?

</br>

> 가정이 깨지는 순간 내부 구현들의 side effect가 너무 클 것이다...  
> 하위 클래스 정보들을 너무 많이 가지고 있기 때문에  
> 변경에 취약한 구조
> `의존성이 높다`

</br>

## 03. 설계 개선하기

</br>

- 관람객과, 판매원은 스스로 자신의 일을 처리해야한다.
  - SRP

</br>

> 다시 Theater의 enter 메서드를 보면  
> 그냥 관객을 입장시키는 것뿐인 기능이다.  
> 따라서 관람객은 스스로 가방안의 로직 처리  
> 판매원도 스스로 매표소 티켓과 판매 요금을 다루도록  
> `자율적인 존재로 만들자`

</br>

```java
public class Theater {
    private TicketSeller ticketSeller;

    public Theater(TicketSeller ticketSeller) {
        this.ticketSeller = ticketSeller;
    }

    public void enter(Audience audience) {
        ticketSeller.sellTo(audience);
    }
}

```

> 먼저 기존 enter 로직은 theater 것이 아님으로  
> ticketSeller로 옮긴다.

</br>

```java
public class TicketSeller {
    private TicketOffice ticketOffice;

    public TicketSeller(TicketOffice ticketOffice) {
        this.ticketOffice = ticketOffice;
    }

    public void sellTo(Audience audience) {
        ticketOffice.plusAmount(audience.buy(ticketOffice.getTicket()));
    }
}
```

</br>

```java
public class Audience {
    private Bag bag;

    public Audience(Bag bag) {
        this.bag = bag;
    }

    public Long buy(Ticket ticket) {
        if (bag.hasInvitation()) {
            bag.setTicket(ticket);
            return 0L;
        } else {
            bag.setTicket(ticket);
            bag.minusAmount(ticket.getFee());
            return ticket.getFee();
        }
    }
}
```

</br>

- 로직 변경
  - theater가 관객을 입장 시킬 때 ticketSeller는 audience에게 티켓을 판매.
  - ticketSeller는 aududience에게 판매할때 티켓오피스의 가격을 올리고, 티켓을 audience에게 판매한다.
  - audience가 buy메서드를 호출할때
    - 초대장이 있는지 없는지의 판단을 현재 `자신이 가지고 있는 변수 bag을 이용해서 실행한다.`
    - 자신의 bag을 통해서 로직을 수행하면 된다!

</br>

### 캡슐화와 응집도

</br>

> 핵심은 `객체 내부 상태를 캡슐화`하고 객체 사이에는 오직 파라미터 메시지를 통해서만  
> 상호작용하여 `결합도를 낮춘다는 것`이다.

</br>

> Theater는 TicketSeller의 내부 상태를 몰라도 sellTo를 통해 행위를 수행하도록 하는 것.
> TicketSeller도 Audience의 내부 상태를 알 필요가 없다.

</br>

> 그러니까 즉 `SRP를 잘 지키려고 노력하면 응집도`를 높여야하는데,  
> 응집도를 높이기 위해 `객체 스스로가 자신의 데이터를 처리`해야한다.  
> 그리고 자신의 데이터를 스스로 처리하고 객체가 서로 메시지를 통해서 소통한다면  
> `결합도를 낮출 수 있다.`
> 결국 핵심은 `위 모든 과정이 의존성을 낮추기 위한 과정`

</br>

### 더 개선

```java
    public Long buy(Ticket ticket) {
        if (bag.hasInvitation()) {
            bag.setTicket(ticket);
            return 0L;
        } else {
            bag.setTicket(ticket);
            bag.minusAmount(ticket.getFee());
            return ticket.getFee();
        }
    }
```

> 현재 메서드를 보면 bag의 모든 내부 상태에 의존하여 audience 클래스의 buy 메서드가 작성 돼있다.  
> 즉, bag이 바뀌면, 이 메서드도 변경이 이뤄져야한다는 것.

```java
public class Bag {
    private Long amount;
    private Ticket ticket;
    private Invitation invitation;

    public Long hold(Ticket ticket) {
        if (hasInvitation()) {
            setTicket(ticket);
            return 0L;
        } else {
            setTicket(ticket);
            minusAmount(ticket.getFee());
            return ticket.getFee();
        }
    }

    private void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    private boolean hasInvitation() {
        return invitation != null;
    }

    private void minusAmount(Long amount) {
        this.amount -= amount;
    }
}
```

</br>

> bag의 hold 메서드를 만들고  
> 이 안에서 `자신의 상태를 이용하고 변경하여 로직을 수행하도록 변경`
> 또한 내부 상태들을 외부에 노출 시킬 필요가 없기 때문에  
> hold()를 위한 메서드들의 `접근 제어자를 private으로 변경`

</br>

## 04. 객체지향 설계

- 마지막 정리 파트
- 책과 더불어 나의 생각 첨부

</br>

## 우선 객체 지향은 내부 속성을 최대한 외부로 공개하지 않으려고 노력해야한다.

> 물론 상황마다 다르다. DTO와 같은 역할을 하는 클래스들은 데이터의 그릇임으로  
> Entity의 정보들을 알고 받아와야한다.  
> 혹은 반대로 DTO의 속성들을 노출 시키거나..

- 1. A 클래스의 속성을 B 클래스가 가져가서 로직을 작성할 경우 A 클래스가 변경될 때 B클래스의 메서드도 변경되어야하니깐.
  - A클래스의 속성을 없앤다면?
- 2. A 클래스의 속성이 레퍼런스 타입이면?
  - 값이 아닌 레퍼런스다.
  - 특히 컬렉션 속성 을 외부 애플리케이션에 노출한다면?
- 3. 이 말이 곧 캡슐화인데, 이 하나의 원칙만 지키려고 노력해도, `응집도가 높아질 수 있다.`

</br>

## 의존성을 낮게 설계해야하는 것은 맞지만, 구조성 의존도가 높게 맞물리는 경우는 기회비용

> 이것은 진짜 기회비용.  
> API request나 response 그리고 Entity 클래스들의 관계를 생각해보자.  
> 객체 지향 관점으로 이 클래스들의 관계를 완전하게 지킬 수 없다.
> `결국 모든 설계가 기회비용인 것같다.`
