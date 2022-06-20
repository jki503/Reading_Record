# CHAPTER 04. 설계 품질과 트레이드 오프

> 3장에서 말했듯이 객체지향 설계에서 가장 중요한 것은 책임이다.  
> 객체에게 올바른 책임을 할당함으로써 응집도를 높이고, 결합도를 낮춰, 변경에 자유로울 수 있다.  
> 그리고 그것을 가능하게 하는 것은 데이터 중심이 아닌 행위 중심으로 클래스를 만들고 설계하는 것이다.

## 데이터 중심의 영화 예매 시스템

> 계속하여 데이터 중심으로 설계하느냐 책임 중심으로 객체를 설계하느냐  
> 뭐가 좋냐를 반복하며 묻고 있음...  
> 결론은 행위를 중심으로 짜야, 그거에 맞춘 데이터를 구현하고  
> 그 데이터를 캡슐화하기가 쉽다는 것.

</br>

```java
public class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private List<DiscountCondition> discountConditions;

    private MovieType movieType;
    private Money discountAmount;
    private double discountPercent;

    // getter, setter 생략
}
```

> 영화에 필요한 데이터들을 일단 모두 정의하였다.  
> 데이터 중심으로 짜다보니, discountConditions를 필드로 가지고 있고,  
> 이전에는 discountPolicy 데이터를 가지고 있던 것이  
> discountAmount와 percent 모두를 갖게 했다.  
> 또한 할인 정책의 종류를 결정하는 것이 movieType의 인스턴스다.  
> 심지어 이 경우 NONE_DISCOUNT 정책일 경우, percent와 amount를 아예 사용하지도 않는다.

</br>

- 그 다음 현재 할인 조건의 타입 저장할 데이터 정의

```java
public enum DiscountConditionType {
    SEQUENCE,       // 순번조건
    PERIOD          // 기간 조건
}
```

- 그 다음 할인 조건을 구현한다

```java
public class DiscountCondition {
    private DiscountConditionType type;

    private int sequence;

    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    //getter, setter 생략
}
```

</br>

```java
public class Screening {
    private Movie movie;
    private int sequence;
    private LocalDateTime whenScreened;


    //getter, setter 생략
}
```

</br>

```java
public class Reservation {
    private Customer customer;
    private Screening screening;
    private Money fee;
    private int audienceCount;

    public Reservation(Customer customer, Screening screening, Money fee,
                       int audienceCount) {
        this.customer = customer;
        this.screening = screening;
        this.fee = fee;
        this.audienceCount = audienceCount;
    }

    //getter, setter 생략
}
```

</br>

```java
public class Customer {
    private String name;
    private String id;

    public Customer(String name, String id) {
        this.id = id;
        this.name = name;
    }
}
```

</br>

- 영화에 필요한 클래스들을 구현한 후 이제 ReservationAgency로 영화 예매 절차 구현 클래스

```java
public class ReservationAgency {
    public Reservation reserve(Screening screening, Customer customer,
                               int audienceCount) {
        Movie movie = screening.getMovie();

        // DiscountCondition을 루프를 돌아 할인 가능한지 판별
        boolean discountable = false;
        for(DiscountCondition condition : movie.getDiscountConditions()) {
            if (condition.getType() == DiscountConditionType.PERIOD) {
                discountable = screening.getWhenScreened().getDayOfWeek().equals(condition.getDayOfWeek()) &&
                        condition.getStartTime().compareTo(screening.getWhenScreened().toLocalTime()) <= 0 &&
                        condition.getEndTime().compareTo(screening.getWhenScreened().toLocalTime()) >= 0;
            } else {
                discountable = condition.getSequence() == screening.getSequence();
            }

            if (discountable) {
                break;
            }
        }

        // 할인 가능 여부에 따라 금액 판별
        Money fee;
        if (discountable) {
            Money discountAmount = Money.ZERO;
            switch(movie.getMovieType()) {
                case AMOUNT_DISCOUNT:
                    discountAmount = movie.getDiscountAmount();
                    break;
                case PERCENT_DISCOUNT:
                    discountAmount = movie.getFee().times(movie.getDiscountPercent());
                    break;
                case NONE_DISCOUNT:
                    discountAmount = Money.ZERO;
                    break;
            }

            fee = movie.getFee().minus(discountAmount).times(audienceCount);
        } else {
            fee = movie.getFee().times(audienceCount);
        }

        // 로직 2개 수행 후 예약 반환
        return new Reservation(customer, screening, fee, audienceCount);
    }
}
```

</br>

- DiscountCondion에 대해 루프를 돌아 확인 가능하게 하는 for문
- discountable 변수의 값을 체크하고 할인 정책에 따라 예매 요금 계산

> 심지어 위 코드는 더 최악인게 enum type 활용도 아예 안한다..

- 참고로 getter는 캡슐화의 가장 낮은 단계
  - reference 타입일때 잘못 의도하면 값의 불변성이 깨짐
  - getter로 끄집어 내서 사용한 후 추후 리팩터링 할 경우 의존성이 굉장히 높아진다.
    - 실제로 경험...

</br>

</br>

## 설계 트레이드 오프

</br>

- 캡슐화 : 변경 가능성이 높은 부분을 객체 내부로 숨기는 추상화 기법!

> 상태와 행동을 하나의 객체 안에 모으는 이유는 객체의 내부 구현을 외부로부터 감추기 위해서!  
> 즉, 나의 변경이, 다른 전체 시스템에 영향을 끼치지 않도록 파급효과를 적절하게 조절하는 것
>
> 설계가 필요한 이유는 요구사항이 변경되기 때문이고,  
> `캡슐화가 중요한 이유는 불안정한 부분과 안정적인 부분을 분리해서 변경의 영향을 통제할 수 있기 때문`  
> `그냥 변경될 수 있는 모든 것을 캡슐화 해야한다?` -> how...

</br>

- 응집도와 결합도

</br>

> 캡슐화와 연관지어 설명해보자면,  
> 객체 설계에서 올바른 캡슐화로 내부 상태와 행위에 대한 구조를 감추고  
> 객체간의 소통이 메시지로만 적절하게 이뤄질 때, 변경에 용이한 코드가 될 수 있다.  
> 즉 응집도는 얼마만큼 그 객체의 상태와 행위가 연관지어 잘 캡슐화 되었냐는 뜻이고,  
> 응집도가 높은 코드는 변경 여파가 적기 때문에 결합도가 낮다고 볼 수 있다.

</br>

## 데이터 중심의 영화 예매 시스템의 문제점

- 위와 같이 데이터 중심 설계의 문제점
  - 캡슐화 위반
  - 높은 결합도
  - 낮은 응집도

</br>

### 캡슐화 위반

- getter, setter가 캡슐화를 어기는 이유는
  - method의 이미 필드명을 노출하는 문제도 있고,
  - 사실 그냥 setter나 getter는 class 필드에 접근하여 변경 및 갖다 쓰는 것..

### 높은 결합도

- 저렇게 getter, setter로 캡슐화를 어기게 되면
  - 클라이언트가 구현에 강하게 결합된다..
  - 애플리케이션에 getter를 마구 흩뿌려놓은 결과..
  - 내부 상태가 바뀔 경우 변경 파급효과가 어마무시했다...
  - 그냥 `사실 getter는 필드만 private으로 해놓고 public으로 쓰는거나 같음.`

### 낮은 응집도

- 할인 정책이 추가 될 경우
- 할인 정책별로 할인 요금을 계산하는 방법이 변경 될 경우
- 할인 조건이 추가되는 경우
- 할인 조건별로 할인 여부를 판단하는 방법이 변경될 경우
- 예매 요금을 계산하는 방법이 변경될 경우

</br>

> 현재 코드에서 할인정책이 추가되거나 변경 될경우 if문이 다 뜯어진다.  
> 또 그에 맞게 할인 요금 계산하는 방법이 변경 될 경우도 마찬가지다.  
> 기존에 맞지않는 책임을 가진 클래스가 여러 모듈의 property에 마구잡이로 접근하여  
> 쌩뚱맞은 요구사항이 변경되더라도, 자칫 4개의 클래스 구현이 변경될 것...

</br>

## 자율적인 객체를 향해

</br>

```java
public class Rectangle {

  private int left;
  private int right;
  private int top;
  private int bottom;

  public Rectangle(int left, int right, int top, int bottom) {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
  }

  public int getLeft() {
    return left;
  }

  public void setLeft(int left) {
    this.left = left;
  }

  public int getRight() {
    return right;
  }

  public void setRight(int right) {
    this.right = right;
  }

  public int getTop() {
    return top;
  }

  public void setTop(int top) {
    this.top = top;
  }

  public int getBottom() {
    return bottom;
  }

  public void setBottom(int bottom) {
    this.bottom = bottom;
  }
}


```

> 이렇게 직사각형이 있을 때, 외부에서 확장시키지 말자.

```java
class Any{
  public void anyMethod(Rectangle retangle, int multiple){
    retangle.setRight(retangle.getRight() * multiple);
    retangle.setBottom(retangle.getBottom() * multiple);
  }
}

```

> 위 사례는 캡슐화를 어긴 사례...  
> 되도록 클래스 내부에서 행위로 처리하는 것이 올바르고  
> `프로퍼티 접근을 사용할 경우 항상 고민하자.` -> 반드시 외부에 노출시켜야하는지

```java
  public void enlarge(int multiple) {
    this.bottom *= multiple;
    this.right *= multiple;
  }
```

> 이런식으로 Rectangle 클래스 내부에 메서드를 정의하여  
> 프로퍼티를 외부에 노출 시키지도 않으면서  
> Rectangle과 연관된 메서드를 만들 수 있다.

</br>

### 스스로 책임지는 객체

```java

public class DiscountCondition {
    private DiscountConditionType type;

    private int sequence;

    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public DiscountCondition(int sequence){
        this.type = DiscountConditionType.SEQUENCE;
        this.sequence = sequence;
    }

    public DiscountCondition(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime){
        this.type = DiscountConditionType.PERIOD;
        this.dayOfWeek= dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DiscountConditionType getType() {
        return type;
    }

    public boolean isDiscountable(DayOfWeek dayOfWeek, LocalTime time) {
        if (type != DiscountConditionType.PERIOD) {
            throw new IllegalArgumentException();
        }

        return this.dayOfWeek.equals(dayOfWeek) &&
                this.startTime.compareTo(time) <= 0 &&
                this.endTime.compareTo(time) >= 0;
    }

    public boolean isDiscountable(int sequence) {
        if (type != DiscountConditionType.SEQUENCE) {
            throw new IllegalArgumentException();
        }

        return this.sequence == sequence;
    }
}
```

> DiscountCondition의 필드를 이용하여  
> 내부에 할인이 가능한지를 판단하는 메서드 정의

</br>

```java
public class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private List<DiscountCondition> discountConditions;

    private MovieType movieType;
    private Money discountAmount;
    private double discountPercent;

    public Movie(String title, Duration runningTime, Money fee, double discountPercent, DiscountCondition... discountConditions) {
        this(MovieType.PERCENT_DISCOUNT, title, runningTime, fee, Money.ZERO, discountPercent, discountConditions);
    }

    public Movie(String title, Duration runningTime, Money fee, Money discountAmount, DiscountCondition... discountConditions) {
        this(MovieType.AMOUNT_DISCOUNT, title, runningTime, fee, discountAmount, 0, discountConditions);
    }

    public Movie(String title, Duration runningTime, Money fee) {
        this(MovieType.NONE_DISCOUNT, title, runningTime, fee, Money.ZERO, 0);
    }

    private Movie(MovieType movieType, String title, Duration runningTime, Money fee, Money discountAmount, double discountPercent,
                  DiscountCondition... discountConditions) {
        this.movieType = movieType;
        this.title = title;
        this.runningTime = runningTime;
        this.fee = fee;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
        this.discountConditions = Arrays.asList(discountConditions);
    }

    public MovieType getMovieType() {
        return movieType;
    }

    public Money calculateAmountDiscountedFee() {
        if (movieType != MovieType.AMOUNT_DISCOUNT) {
            throw new IllegalArgumentException();
        }

        return fee.minus(discountAmount);
    }

    public Money calculatePercentDiscountedFee() {
        if (movieType != MovieType.PERCENT_DISCOUNT) {
            throw new IllegalArgumentException();
        }

        return fee.minus(fee.times(discountPercent));
    }

    public Money calculateNoneDiscountedFee() {
        if (movieType != MovieType.NONE_DISCOUNT) {
            throw new IllegalArgumentException();
        }

        return fee;
    }

    public boolean isDiscountable(LocalDateTime whenScreened, int sequence) {
        for(DiscountCondition condition : discountConditions) {
            if (condition.getType() == DiscountConditionType.PERIOD) {
                if (condition.isDiscountable(whenScreened.getDayOfWeek(), whenScreened.toLocalTime())) {
                    return true;
                }
            } else {
                if (condition.isDiscountable(sequence)) {
                    return true;
                }
            }
        }

        return false;
    }
}
```

> 영화는 조건에 맞도록 할인이 가능한지 판단하는 함수와  
> 할인 금액을 계산하는 메서드가 필요
> `근데 저렇게 오버라이딩 해서 설계할 필요가 없을 것같다.`

```java
public class Screening {
    private Movie movie;
    private int sequence;
    private LocalDateTime whenScreened;

    public Screening(Movie movie, int sequence, LocalDateTime whenScreened) {
        this.movie = movie;
        this.sequence = sequence;
        this.whenScreened = whenScreened;
    }

    public Money calculateFee(int audienceCount) {
        switch (movie.getMovieType()) {
            case AMOUNT_DISCOUNT:
                if (movie.isDiscountable(whenScreened, sequence)) {
                    return movie.calculateAmountDiscountedFee().times(audienceCount);
                }
                break;
            case PERCENT_DISCOUNT:
                if (movie.isDiscountable(whenScreened, sequence)) {
                    return movie.calculatePercentDiscountedFee().times(audienceCount);
                }
            case NONE_DISCOUNT:
                movie.calculateNoneDiscountedFee().times(audienceCount);
        }

        return movie.calculateNoneDiscountedFee().times(audienceCount);
    }
}

```

> 상영 가격을 결정하기 위해 switch문에서 movie의 타입을 사용하는 것을 볼 수 있다.  
> ReservationAgency에서 역할을 뜯어내긴 했지만,  
> 여전히 type이 추가될 경우 변경에 취약한 코드...

</br>

## 하지만 여전히 부족하다

</br>

```java
    public boolean isDiscountable(LocalDateTime whenScreened, int sequence) {
        for(DiscountCondition condition : discountConditions) {
            if (condition.getType() == DiscountConditionType.PERIOD) {
                if (condition.isDiscountable(whenScreened.getDayOfWeek(), whenScreened.toLocalTime())) {
                    return true;
                }
            } else {
                if (condition.isDiscountable(sequence)) {
                    return true;
                }
            }
        }

        return false;
    }
```

> whenScreened와 sequence 이것 역시도 메서드 정의에서 `파라미터 정보들이 필드로 포함되어 있다는 것을 외부로 노출 시킨다는 것`  
> 또한 DiscountCondition의 속성이 변경될 경우 `isDicountable()은 파라미터 정보를 바꾸어야할 것..`

</br>

```java
    public Money calculateAmountDiscountedFee() {
        if (movieType != MovieType.AMOUNT_DISCOUNT) {
            throw new IllegalArgumentException();
        }

        return fee.minus(discountAmount);
    }

    public Money calculatePercentDiscountedFee() {
        if (movieType != MovieType.PERCENT_DISCOUNT) {
            throw new IllegalArgumentException();
        }

        return fee.minus(fee.times(discountPercent));
    }

    public Money calculateNoneDiscountedFee() {
        if (movieType != MovieType.NONE_DISCOUNT) {
            throw new IllegalArgumentException();
        }

        return fee;
    }
```

> movie 역시 계산의 행위를 노출하는 것이 아니라  
> 정책마다의 계산 방식을 정의하여 외부에 할인 정책을 모두 노출한다.  
> 아니 근데 이런식으로 메서드 정의를...  
> 또 할인정책이 삭제되거나 추가될 때의 파급효과는..?

</br>

```java
public class Screening {
    private Movie movie;
    private int sequence;
    private LocalDateTime whenScreened;

    public Screening(Movie movie, int sequence, LocalDateTime whenScreened) {
        this.movie = movie;
        this.sequence = sequence;
        this.whenScreened = whenScreened;
    }

    public Money calculateFee(int audienceCount) {
        switch (movie.getMovieType()) {
            case AMOUNT_DISCOUNT:
                if (movie.isDiscountable(whenScreened, sequence)) {
                    return movie.calculateAmountDiscountedFee().times(audienceCount);
                }
                break;
            case PERCENT_DISCOUNT:
                if (movie.isDiscountable(whenScreened, sequence)) {
                    return movie.calculatePercentDiscountedFee().times(audienceCount);
                }
            case NONE_DISCOUNT:
                movie.calculateNoneDiscountedFee().times(audienceCount);
        }

        return movie.calculateNoneDiscountedFee().times(audienceCount);
    }
}
```

> screening을 다시 보면  
> 앞에서 DiscountPolicy가 변경되면 Movie의 isDiscountable의 파라미터의 종류가 바뀔 것  
> `그렇게 되면 Screening에서 isDiscountable을 호출하는 영역도 변경 되어야한다.`

</br>

### 결과

> 결국 한인 조건의 종류를 변경한다면  
> DiscountCondition, Movie, Screening이 함께 수정되어야 한다.  
> 하나의 변경으로 여러곳을 변경한다는 것 자체가 캡슐화를 위반하여 응집도가 낮기 때문이다.

</br>

## 데이터 중심 설계의 문제점

</br>

- 데이터 중심 설계는 너무 이른 시기에 데이터에 관해 결정하도록 강요
- 데이터 중심 설계는 `협력이라는 문맥 고려하지 않고 객체 고립시킨채 행위를 결정...`

</br>

> 정리하자면, 데이터 중심 설계는 데이터를 정의하고 행위를 구현하기 때문에 객체간의 협력을 무시한다.  
> 즉, 일찍이 데이터를 위주로 구현하기 때문에, 리팩터하기도 굉장히 힘들다...

</br>

### 정리

</br>

- 객체의 행위는 내부 상태로 해겷하려고 노력해야한다.
  - 그것이 캡슐화의 시작
  - 책임의 분배 용이
  - 다들 setter는 잘 안쓴다. 그런데 getter는?
    - 필드 다른 영역에 엮여있으면 수정하기 어렵다.
    - 심지어 data를 전달하는 dto도 의존성 물려서 변경이 어렵다..

</br>

> 나 역시도, 프로퍼티 먼저 정의하고, 그후 로직을 결정했던 듯..

- 시간이 걸리더라도 역할 기반으로 고민을 한 번 한 후
- 어떤 행위 메서드가 필요한 지 고민
- 메서드가 너무 많은 정보를 내보내진 않는지 고려
- 1, 2장 영화 상영 설계 틈틈히 복습해보기.
