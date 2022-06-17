# CHAPTER 02. 객체지향 프로그래밍

## 01. 영화 예매 시스템

</br>

- 하나의 영화는 하루중 다양한 시간대에 걸쳐 한 번 이상 상영될 수 있다.
- 할인 조건
  - 순서 조건 : 순번이 10인 경우 매일 10번째로 상영되는 영화를 예매한 사용자들에게 할인
  - 기간 조건 : 월요일 시작시간이 10시 종료시간이 오후 1시라면, 이때 상영되는 영화 할인
- 할인 정책
  - 금액 할인 정책 : 고정 금액 할인
  - 비율 할인 정책 : 말 그대로 비율 할인
- 단 영화별로 하나의 할인 정책만 할당하여 할인 한다
- 힐인 조건은 다수의 할인 조건 지정 가능, 순서 조건과 기간 조건 섞기도 가능
- 할인 정책이나 할인 조건은 없을 수도 있다.

</br>

## 02. 객체지향 프로그래밍을 향해

</br>

- 어떤 클래스가 필요한지를 고민하기 전에 어떤 객체들이 필요한지 고민

  - 클래스: 공통적인 상태와 행동을 공유하는 객체들을 추상화 한 것.
  - 객체들이
    - 어떤 상태와 행동을 가지는지를 결정해야 한다.

- 객체를 독립적인 존재가 아니라 기능을 구현하기 위해 협력하는 존재로 인식

</br>

```java
import java.time.LocalDateTime;

public class Screening {
    private Movie movie;
    private int sequence;
    private LocalDateTime whenScreened;

    public Screening(Movie movie, int sequence, LocalDateTime whenScreened) {
        this.movie = movie;
        this.sequence = sequence;
        this.whenScreened = whenScreened;
    }

    public LocalDateTime getStartTime() {
        return whenScreened;
    }

    public boolean isSequence(int sequence) {
        return this.sequence == sequence;
    }

    public Money getMovieFee() {
        return movie.getFee();
    }
}
```

> Screening은 영화, 순번, 상영 시작 시간을 필드로 갖는다.

</br>

- 캡슐화

> 객체지향에서는 데이터와 기능, 즉 상태와 행위를 묶음으로써 문제 영역을 해결하고자 함  
> 이때 `데이터와 기능을 객체 내부로 함께 묶는 것을 캡슐화`

</br>

- 접근 제어자

> 객체는 자율적인 존재로 존재하여, 스스로 상태를 관리하고, 행위를 결정해야한다.  
> 따라서 외부의 간섭을 최소화하기 위해 접근 제어자를 통해  
> 내부 객체 상태에 대한 권한을 상황에 맞게 설정해야한다.

</br>

```java
    public Reservation reserve(Customer customer, int audienceCount) {
        return new Reservation(customer, this, calculateFee(audienceCount),
                audienceCount);
    }

    private Money calculateFee(int audienceCount) {
        return movie.calculateMovieFee(this).times(audienceCount);
    }
```

</br>

> Screening에 다음과 같은 메서드 두 개를 추가한다.  
> 예매 정보 Reservation을 생성할 때  
> 금액 산정은 calculateFee를 통해서 결정  
> 영화 금액을 결정할때, Screening과 관객 수 audienceCount를 통해서 결정

</br>

```java
import java.math.BigDecimal;
import java.util.Objects;

public class Money {
    public static final Money ZERO = Money.wons(0);

    private final BigDecimal amount;

    public static Money wons(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public static Money wons(double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    Money(BigDecimal amount) {
        this.amount = amount;
    }

    public Money plus(Money amount) {
        return new Money(this.amount.add(amount.amount));
    }

    public Money minus(Money amount) {
        return new Money(this.amount.subtract(amount.amount));
    }

    public Money times(double percent) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(percent)));
    }

    public boolean isLessThan(Money other) {
        return amount.compareTo(other.amount) < 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return amount.compareTo(other.amount) >= 0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Money)) {
            return false;
        }

        Money other = (Money)object;
        return Objects.equals(amount.doubleValue(), other.amount.doubleValue());
    }

    public int hashCode() {
        return Objects.hashCode(amount);
    }

    public String toString() {
        return amount.toString() + "원";
    }
}
```

> Money 타입으로 정의해서 사용할 경우  
> 금액과 관련 돼있음을 전달하고 `금액과 관련된 로직을 다른 곳에서도 중복없이 사용 가능`
> 또한, 필드가 하나일지라도 개념을 명시적으로 표현하고,  
> 내부 상태와 행위를 사용하는데 적합하다면 사용할것

</br>

- 설계 내생각

> 여기서부터 객체지향의 꽃인게  
> 현재 Money의 역할에 대해 이견이 없지만,  
> 같은 클래스여도 제약 조건이 다를 수 있다.  
> `어떻게 추상화할 것인지, 같은 속성으로 묶기 애매하다면 그냥 나눌것인지?`

</br>

```java
public class Reservation {
    private Customer customer;
    private Screening Screening;
    private Money fee;
    private int audienceCount;

    public Reservation(Customer customer, Screening Screening, Money fee, int audienceCount) {
        this.customer = customer;
        this.Screening = Screening;
        this.fee = fee;
        this.audienceCount = audienceCount;
    }
}
```

> 예약의 필요한 필드 변수들이 정의 돼 있다.

</br>

- 메서드

> 객체가 다른 객체와 상호작용하는 것은 메시지를 수신하고 그에 응답하는 것  
> `즉 메시지를 받은 객체가 수신된 메시지를 처리하기 위한 자신만의 방법을 메서드라 정의`

</br>

## 03. 할인 요금 구하기

</br>

```java
import java.time.Duration;

  public class Movie {
  private String title;
  private Duration runningTime;
  private Money fee;
  private DiscountPolicy discountPolicy;

    public Movie(String title, Duration runningTime, Money fee, DiscountPolicy discountPolicy) {
        this.title = title;
        this.runningTime = runningTime;
        this.fee = fee;
        this.discountPolicy = discountPolicy;
    }

    public Money getFee() {
        return fee;
    }

    public Money calculateMovieFee(Screening screening) {
        return fee.minus(discountPolicy.calculateDiscountAmount(screening));
    }

}

```

> calculateMovieFee  
> discountPolicy에게 screening이라는 메시지를 던져 응답받은 것을 토대로  
> 값을 결정하여 반환한다.

</br>

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DiscountPolicy {
    private List<DiscountCondition> conditions = new ArrayList<>();

    public DiscountPolicy(DiscountCondition ... conditions) {
        this.conditions = Arrays.asList(conditions);
    }

    public Money calculateDiscountAmount(Screening screening) {
        for(DiscountCondition each : conditions) {
            if (each.isSatisfiedBy(screening)) {
                return getDiscountAmount(screening);
            }
        }

        return Money.ZERO;
    }

    abstract protected Money getDiscountAmount(Screening Screening);
}
```

> conditions를 통해 하나의 할인 정책이 여러개의 할인 조건을 포함 가능  
> 또한 실제 구현체가 아님으로 abstract으로 정의

</br>

```java
public interface DiscountCondition {
    boolean isSatisfiedBy(Screening screening);
}
```

```java
import java.time.DayOfWeek;
import java.time.LocalTime;

public class PeriodCondition implements DiscountCondition {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public PeriodCondition(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isSatisfiedBy(Screening screening) {
        return screening.getStartTime().getDayOfWeek().equals(dayOfWeek) &&
                startTime.compareTo(screening.getStartTime().toLocalTime()) <= 0 &&
                endTime.compareTo(screening.getStartTime().toLocalTime()) >= 0;
    }
}
```

```java
public class SequenceCondition implements DiscountCondition {
    private int sequence;

    public SequenceCondition(int sequence) {
        this.sequence = sequence;
    }

    public boolean isSatisfiedBy(Screening screening) {
        return screening.isSequence(sequence);
    }
}
```

</br>

> 할인 조건을 interface로 만든 후,  
> 상영 정보에 따라 각 조건이 만족하는지를 구현하도록 강제했다.

</br>

```java
public class PercentDiscountPolicy extends DiscountPolicy {
    private double percent;

    public PercentDiscountPolicy(double percent, DiscountCondition... conditions) {
        super(conditions);
        this.percent = percent;
    }

    @Override
    protected Money getDiscountAmount(Screening screening) {
        return screening.getMovieFee().times(percent);
    }
}

```

```java

public class AmountDiscountPolicy extends DiscountPolicy {
    private Money discountAmount;

    public AmountDiscountPolicy(Money discountAmount, DiscountCondition... conditions) {
        super(conditions);
        this.discountAmount = discountAmount;
    }

    @Override
    protected Money getDiscountAmount(Screening screening) {
        return discountAmount;
    }
}
```

</br>

> 할인 정책은 각 영화의 할인된 가격을 계산하여 리턴한다.  
> 또한 할인정책은 다양한 할인 여러개의 할인 조건들을 가질 수 있다.

</br>

## 04. 상속과 다형성

</br>

> Movie는 DiscountPolicy를 하나 가지고  
> DiscountPolicy의 실제 구현체는 컴파일타임이 아니라  
> 런타임에 결정된다.
>
> 코드 레벨에서 Movie는 실질적 인스턴스인 AmountDiscountPolicy나 Percent~에 의존하는 것이 아니라  
> `추상 클래스인 DiscountPolicy에 의존`

</br>

- 코드 의존성과 실행 시점의 의존성은 다르 수 있다.

> 코드는 더 이해하기 어려워지지만  
> 컴파일타임 의존성과 런타임 의존성이 다를 수록 코드는 더 유연해지고 확장 가능해진다.
> `재사용성이 높아지고, 구조적 변경에 약하지 않은 설계가 가능`

</br>

> DiscountPolicy는 추상클래스로 필요한 속성을 정의 한 후  
> 추상 메서드로 getDiscountAmount의 생성을  
> `실제 인스턴스 구현체 클래스에게 위임하여 오버라이딩을 강제`한다.  
> 즉 인스턴스 구현체에 맞도록 행위를 수정하여 클래스와 메서드를 정의할 수 있게 하여  
> `다형성을 높일 수 있게 됐다.`

</br>

- 상속을 메서드를 재사용하기 위한 것이라고 생각하지 말 것

> 외부 객체 입장에서, 내가 원하는 메서드를 실행할 수 있냐?  
> 즉, 외부 객체는 DiscountPolicy만 알고 있고,  
> `내가 실행시키려고 의도한 메서드를 수행할 수 있느냐가 중요한 것.`  
> 자식클래스가 이처럼 부모클래스를 대신하는 것을 `업캐스팅`이라고 부름.

</br>

- 다형성 : getDicountAmount

> 실제 코드레벨에서는 DiscountPolicy에서 정의한 getDicountAmount()를 실행시킨다.  
> 하지만 런타임에 결정된 인스턴스가 Amount인지 Percent인지에 따라  
> 실행되는 하위 실질적 하위메서드가 달라진다.  
> `다형성`

</br>

- 다형성을 구현하는 방법
  - 즉, 메시지와 메서드를 런타임에 바인딩
  - `동적바인딩`

</br>

## 05. 추상화와 우연성

</br>

- 추상화 : 특정 대상에 대해 좀 더 상위 개념으로 정의 그리고 공통되는 특성을 묶은 것
  - 요구사항을 일반적이고 높은 수준으로 정의
  - 설계를 좀 더 유연하게 가져갈 수 있다.

> 그러니까 이 말이 뭐냐면  
> 특징 하나하나 세부적인 것에 집착하지 않고,  
> 추상화 개념만으로도 도메인의 중요한 개념을 설명한 후  
> 하위 설계로 나누어 객제지향 설계에 도움을 준다는 것

</br>

```java
public class Movie{
    public Money calculateMovieFee(Screening screening) {
        if(discountPolicy == null){
          return fee;
        }

        return fee.minus(discountPolicy.calculateDiscountAmount(screening));
    }
}

```

- 이 방식은 할인 정책이 없는 경우를 예외적으로 처리하여, 일관성을 깨뜨린다.
- `할인 금액이 0원인 것을 결정하는 것이 영화가 된다`
  - 책임이 억지스럽다.

```java
public class NoneDiscountPolicy extends DiscountPolicy {
    @Override
    protected Money getDiscountAmount(Screening screening) {
        return Money.ZERO;
    }
}
```

> 이렇게 할인 정책이 없을때에도  
> DiscountPolicy가 가격을 결정하도록 책임을 유지한다.
> 즉, 위와 같이 설계하고, calculateMovieFee()에서 예외적 상황을 만들지 않는 것은  
> `movie는 어떤 특정한 할인 정책에도 묶이지 않는다.` > `컨텍스트 독립성` -> 8장 설명

</br>

### 추상 클래스와 인터페이스 트레이드 오프

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DiscountPolicy {
    private List<DiscountCondition> conditions = new ArrayList<>();

    public DiscountPolicy(DiscountCondition ... conditions) {
        this.conditions = Arrays.asList(conditions);
    }

    public Money calculateDiscountAmount(Screening screening) {
        for(DiscountCondition each : conditions) {
            if (each.isSatisfiedBy(screening)) {
                return getDiscountAmount(screening);
            }
        }

        return Money.ZERO;
    }

    abstract protected Money getDiscountAmount(Screening Screening);
}
```

> 다시 한 번 DiscountPolicy를 살펴보면  
> calculateDiscountAmount를 실행할 때  
> NoneDicountPolicy는 getDiscountAmount()가 어떤 값이어도 필요가 없다.  
> conditions가 없기 때문에,  
> `calculateDiscountAmount()는 항상 Money.ZERO를 반환한다.`
> 결국 이 말이 뭐냐면 추상 클래스로만 설계를 하였기 때문에  
> `NoneDicountPolicy에서도 getDicountAmount의 구현이 강제된다는 것이다.`

</br>

```java
public interface DiscountPolicy{
  Money calculateDiscountAmount(Screening screeing);
}
```

> 실제로 우리가 필요한 메서드만 인터페이스로 정의해서 강요한 후에

```java
public class NoneDiscountPolicy implements DiscountPolicy {
    @Override
    public Money calculateDiscountAmount(Screening screening) {
        return Money.ZERO;
    }
}
```

```java
public abstract class DefaultDiscountPolicy implements DiscountPolicy{
  private List<DiscountCondition> conditions = new ArrayList<>();

  public DefaultDiscountPolicy(DiscountCondition... conditions) {
      this.conditions = Arrays.asList(conditions);
  }

  public Money calculateDiscountAmount(Screening screening) {
        for(DiscountCondition each : conditions) {
            if (each.isSatisfiedBy(screening)) {
                return getDiscountAmount(screening);
            }
        }

        return Money.ZERO;
  }

  abstract protected Money getDiscountAmount(Screening Screening);
}
```

```java
public class AmountDiscountPolicy extends DefaultDiscountPolicy {
    private Money discountAmount;

    public AmountDiscountPolicy(Money discountAmount, DiscountCondition... conditions) {
        super(conditions);
        this.discountAmount = discountAmount;
    }

    @Override
    protected Money getDiscountAmount(Screening screening) {
        return discountAmount;
    }
}
```

> 이런식으로 설계를 하여 설계를 바꿀 수도 있다.

</br>

> 더 좋은 설계라고는 동의하지만, 상황마다 다를듯하다.  
> 지금 이 상황에서는 학습을 위해 만든 설계라고밖에..

</br>

### 상속의 단점

- 캡슐화의 위반

> 자식 클래스가 부모 클래스의 상태를 알아야한다.  
> 솔직히 그냥 코드 보기가 힘들다..

</br>

- 설계가 유연하지 않다.

> 객체의 종류가 다른데 실행시점에 이를 다시 변경하는 것은 불가능

</br>

```java
public class Movie{
  private DiscountPolicy dicountPolicy;

  public void changeDiscountPolicy(DicountPolicy discountPolicy){
    this.discountPolicy= discountPolicy;
  }
}
```

> 합성을 사용한다면 새로운 인스턴스로 바꾸는 것이 가능하다

</br>

## 정리

- 항상 누가 책임질 것인지를 고려한다. -> 완벽한 캡슐화 따위는 없다.
- 상속을 써야한다면 정확한 이유가 있어야한다.
  - 속성을 가지는지
  - 타입이 결정되면 runtime에 변경 될 일은 없는지 - 결합도가 높은 것을 감안해도 상관 없는지
- 합성을 쓰라는 말이 그냥 가급적 인터페이스를 사용해서 다형성을 사용해라.
