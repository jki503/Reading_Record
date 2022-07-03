# CHAPTER 11. 합성과 유연한 설계

</br>

> 상속과 인터페이스의 가장 큰 차이점은  
> 상속의 의존성은 컴파일타임에서 결정되지만  
> 합성에서 두 객체 사이의 의존성은 런타임에 결정된다.

</br>

- 상속의 가장 큰 단점은 부모 클래스 내부 구현을 자세히 알아야한다.
  - 캡슐화가 깨짐
  - 자식 클래스가 부모 클래스의 구현에 의존적이라는 것

</br>

- 인터페이스는 구현에 의존하지 않는다
  - 내부 포함된 객체의 구현이 아닌 퍼블릭 인터페이스(오퍼레이션)에 의존한다.
  - 그래서 포함된 객체의 내부 구현이 변경되더라도 영향을 최소화 할 수 있다.

</br>

## 상속을 합성으로 변경하기

</br>

- 코드 재사용을 위해 상속을 남용했을 때의 문제점
  - 불필요한 인터페이스 상속문제
    - java.util.Properties와 java.util.Stack
  - 메서드 오버라이딩의 오작용 문제
    - 자식클래스가 부모 클래스의 메서드를 오버라이딩할 때 부모 클래스의 영향을 받는 문제
  - 부모 클래스와 자식 클래스의 동시 수정 문제
    - 부모 클래스를 변경할 때 자식 클래스도 함께 변경해야 하는 문제

</br>

### 불필요한 인터페이스 상속 문제: java.util.Properties와 java.util.Stack

</br>

#### java.util.Properties

</br>

```java
public class Properties extends HashTable<Object, Object>{
  ...
  @Override
  public synchronized Object put(Object key, Object value) {
    return map.put(key, value);
  }

  public synchronized Object setProperty(String key, String value) {
      return put(key, value);
  }

  @Override
  public Object get(Object key) {
     return map.get(key);
  }

  public String getProperty(String key) {
      Object oval = map.get(key);
      String sval = (oval instanceof String) ? (String)oval : null;
      Properties defaults;
      return ((sval == null) && ((defaults = this.defaults) != null)) ? defaults.getProperty(key) : sval;
  }
}
```

</br>

```java
import java.util.Hashtable;

public class Properties {
    private Hashtable<String, String> properties = new Hashtable <>();

    public String setProperty(String key, String value) {
        return properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }
}
```

</br>

> 합성으로 바꾸면서 불필요한 HashTable의 오퍼레이션들이  
> Properties 클래스의 인터페이스를 오염시키지 않는다.

</br>

#### java.util.Stack

</br>

```java
public class Stack<E> {
    private Vector<E> elements = new Vector<>();

    public E push(E item) {
        elements.addElement(item);
        return item;
    }

    public E pop() {
        if (elements.isEmpty()) {
            throw new EmptyStackException();
        }
        return elements.remove(elements.size() - 1);
    }
}
```

> Stack도 상속이 아닌 합성을 이용하여  
> Vector의 오퍼레이션을 포함하지 않아  
> 더이상 Stack의 규칙을 어기지 못하게 한다.

</br>

### 메서드 오버라이딩의 오작용 문제 : InstrumentedHashSet

</br>

```java
public class InstrumentedHashSet<E> extends HashSet<E>{
  private int addCount = 0;

  @Override
  public boolean add(E e){
    addCount++;
    return super.add(e);
  }

  @Override
  public boolean addAll(Collection<> extends E> c){
    addCount += c.size();
    return super.addAll(c);
  }
}
```

</br>

> 현재 오버라이딩의 문제점은 자식 클래스가 부모 클래스의 메서드를 오버라이딩할 경우  
> 부모 클래스가 자신의 메서드를 사용하는 방법에 자식 클래스가 결합될 수 있다는 것이다.

</br>

```java

public class InstrumentedHashSet<E> implements Set<E> {
    private int addCount = 0;
    private Set<E> set;

    public InstrumentedHashSet(Set<E> set) {
        this.set = set;
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return set.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return set.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }

    @Override public boolean remove(Object o) {
        return set.remove(o);
    }

    @Override public void clear() {
        set.clear();
    }

    @Override public boolean equals(Object o) {
        return set.equals(o);
    }

    @Override public int hashCode() {
        return set.hashCode();
    }

    @Override public Spliterator<E> spliterator() {
        return set.spliterator();
    }

    @Override public int size() {
        return set.size();
    }

    @Override public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override public Iterator<E> iterator() {
        return set.iterator();
    }

    @Override public Object[] toArray() {
        return set.toArray();
    }

    @Override public <T> T[] toArray(T[] a) {
        return set.toArray(a);
    }

    @Override public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    @Override public boolean retainAll(Collection<?> c) {
        return set.retainAll(c);
    }

    @Override public boolean removeAll(Collection<?> c) {
        return set.removeAll(c);
    }
}
```

</br>

> 위 방식은 합성을 이용하여 HashSet의 의존도를 해결하면서  
> HashSet이 제공하는 인터페이스를 그대로 제공하는 방식이다.  
> Set의 오퍼레이션을 오버라이딩한 인스턴스 메서드에서 내부의

- 포워딩
  - HashSet 인스턴스에게 동일한 메서드 호출을 그대로 전달한다는 것을 알 수 있다.

> 포워딩은 기존 클래스의 인터페이스를 그대로 외부에 제공하면서 구현에 대한 결합 없이  
> 일부 작동 방식을 변경하고 싶은 경우에 사용할 수 있는 유용한 기법

</br>

## 상속으로 인한 조합의 폭발적인 증가

</br>

- 상속으로 인해 결합도가 높아지면 코드수정 작업양이 과도하게 늘어난다.
  - 하나의 기능을 추가 및 수정하기 위해 많은 수의 클래스를 추가하거나 수정
  - 단일 상속만 지원하는 자바에서는 중복코드의 양이 늘어날 수도 있다.

</br>

### 기본 정책과 부가 정책 조합하기

</br>

- 기본 정책
  - 일반 요금제
  - 심야 할인 요금제
- 부가 정책
  - 세금 정책
  - 기본 요금 할인 정책

</br>

> 새로운 요구사항은 기본 요금제에 `부가 정책을 추가`

</br>

- 기본 정책은 가입자의 통화 정보를 기반으로 한다.
- 기본 정책은 `가입자의 한달 통화량을 기준`으로 부과 할 요금 계산

</br>

- 기본 정책의 계산 결과에 적용된다.
  - 세금 정책은 기본 정책인 RegularPhone이나 NightlyDiscountPhone의 계산이 끝난 결과에 세금 부과
- 선택적으로 적용
  - 기본 정책의 계산 결과에 세금 정책을 적용 할 수도 안 할 수도!
- 조합 가능
  - 기본 정책에 세금정책만 적용하는 것도 가능하고
  - 기본 요금 할인 정책만 적용하는 것도 가능
  - 세금 정책과 기본 요금 할인 정책을 함께 적용하는 것도 가능
- 부가 정책은 임의의 순서로 적용 가능
  - 기본 정책에 세금 정책과 기본 요금 할인 정책을 함께 적용할 경우 세금 정책을 적용한 후
  - 기본 요금 할인 정책을 적용할 수도 있고, 기본 요금 할인 정책을 적용한 후에 세금 정책을 적용할 수도 있다.

</br>

|  다양한 조합에 따른 폭발적인 증가  |
| :--------------------------------: |
| ![복잡성](../res/_11_complex.jpeg) |

</br>

```java
public abstract class Phone {
    private List<Call> calls = new ArrayList<>();

    public Money calculateFee() {
        Money result = Money.ZERO;

        for(Call call : calls) {
            result = result.plus(calculateCallFee(call));
        }

        return result;
    }

    abstract protected Money calculateCallFee(Call call);
}
```

```java
public class RegularPhone extends AbstractPhone {
    private Money amount;
    private Duration seconds;

    public RegularPhone(Money amount, Duration seconds) {
        this.amount = amount;
        this.seconds = seconds;
    }

    @Override
    protected Money calculateCallFee(Call call) {
        return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }
}
```

```java
public class NightlyDiscountPhone extends AbstractPhone {
    private static final int LATE_NIGHT_HOUR = 22;

    private Money nightlyAmount;
    private Money regularAmount;
    private Duration seconds;

    public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds) {
        this.nightlyAmount = nightlyAmount;
        this.regularAmount = regularAmount;
        this.seconds = seconds;
    }

    @Override
    protected Money calculateCallFee(Call call) {
        if (call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
            return nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        } else {
            return regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        }
    }
}
```

> RegularPhone과 NightlyDiscountPhone의 인스턴스 단독으로 생성하는 것은  
> 부가 정책을 적용하지 않겠다는 것을 의미한다.

</br>

### 기본 정책에 세금 조합하기

</br>

- 일반 요금제에 세금 정책을 조합
  - 가장 간단하게 그냥 RegularPhone을 상속받은 클래스를 추가하는 것

```java
public class TaxableRegularPhone extends RegularPhone {
    private double taxRate;

    public TaxableRegularPhone(Money amount, Duration seconds,
                               double taxRate) {
        super(amount, seconds);
        this.taxRate = taxRate;
    }

    @Override
    public Money calculateFee() {
        Money fee = super.calculateFee();
        return fee.plus(fee.times(taxRate));
    }
}
```

> 부모 클래스의 메서드를 재사용하기 위해 super를 호출하면 편하지만  
> 자식 클래스와 부모 클래스 사이의 결합도가 높아진다!  
> `결합도를 낮추는 방법`은 자식 클래스가 부모 클래스의 메서드를  
> 호출하지 않도록 부모 클래스에 추상 메서드를 제공하는 것이다.

</br>

```java
public abstract class Phone {
    private List<Call> calls = new ArrayList<>();

    public Money calculateFee() {
        Money result = Money.ZERO;

        for(Call call : calls) {
            result = result.plus(calculateCallFee(call));
        }

        return afterCalculated(result);
    }

    protected abstract Money calculateCallFee(Call call);
    protected abstract Money afterCalculated(Money fee);
}
```

</br>

> 자신이 정의한 추상 메서드를 호출하고 자식 클래스가 이 메서드를 오버라이딩해서  
> 부모 클래스가 원하는 로직을 제공하도록 수정하면  
> 부모 클래스와 자식 클래스 사이의 결합도를 느슨하게 만들 수 있다.
> 자식 클래스가 부모 클래스의 구체적 구현이아니라 `필요한 동작의 명세를 기술하는 추상화에 의존하도록 한다`

</br>

```java
public class RegularPhone extends Phone {
    private Money amount;
    private Duration seconds;

    public RegularPhone(Money amount, Duration seconds) {
        this.amount = amount;
        this.seconds = seconds;
    }

    @Override
    protected Money calculateCallFee(Call call) {
        return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return fee;
    }
}
```

```java
public class NightlyDiscountPhone extends Phone {
    private static final int LATE_NIGHT_HOUR = 22;

    private Money nightlyAmount;
    private Money regularAmount;
    private Duration seconds;

    public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds) {
        this.nightlyAmount = nightlyAmount;
        this.regularAmount = regularAmount;
        this.seconds = seconds;
    }

    @Override
    protected Money calculateCallFee(Call call) {
        if (call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
            return nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        } else {
            return regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        }
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return fee;
    }
}
```

</br>

> 자식클래스는 afterCalculated 메서드를  
> 오버라이딩해서 계산된 요금에 적용할 작업을 추가한다.
> 기본 정책에 해당하는 위의 두 클래스들은 전달된 요금을 그대로 반환하도록 구현

</br>

- TaxableRegularPhone(기본 정책 + 세금 정책 조합)이 추상메서드에 의존하게 하려고 했을때
  - 부모 클래스에 추상 메서드 추가 이후 모든 자식들이 추상 메서드를 오버라이딩 해야했다.
  - 자식 클래스의 수가 많다면 제법 번거러운 일.
  - 중복 코드를 야기할 수 있다.

</br>

- 기본 정책에서 중복 코드 제거
  - 기본 정책에 대한 afterCalculated 구현을 부모 클래스로 올리기!

```java
public abstract class Phone {
    private List<Call> calls = new ArrayList<>();

    public Money calculateFee() {
        Money result = Money.ZERO;

        for(Call call : calls) {
            result = result.plus(calculateCallFee(call));
        }

        return afterCalculated(result);
    }

    protected Money afterCalculated(Money fee) {
        return fee;
    }

    protected abstract Money calculateCallFee(Call call);
}
```

</br>

- 추상 메서드와 훅 메서드

> calculateCallFee와 같이 자식 클래스에서 오버라이딩할 목적의 메서드이지만  
> 편의를 위해 기본 구현을 제공하는 메서드를 훅 메서드라고 한다!

</br>

- 이제 afterCalculated 오버라이딩

```java
public class TaxableRegularPhone extends RegularPhone {
    private double taxRate;

    public TaxableRegularPhone(Money amount, Duration seconds, double taxRate) {
        super(amount, seconds);
        this.taxRate = taxRate;
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return fee.plus(fee.times(taxRate));
    }
}
```

- NightlyDiscountPhone에도 세금을 부과 할 수 있도록 NightlyDiscountPhone의 자식 클래스 추가

```java
public class TaxableNightlyDiscountPhone extends NightlyDiscountPhone {
    private double taxRate;

    public TaxableNightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds, double taxRate) {
        super(nightlyAmount, regularAmount, seconds);
        this.taxRate = taxRate;
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return fee.plus(fee.times(taxRate));
    }
}
```

> 현재 코드들을 살펴보면 TaxableDiscountPhone과 TaxableNightlyDiscountPhone의  
> 코드가 중복됐다는 것이다. 현재 부모클래스 이름만 다르고 거의 동일하다.  
> 자바를 비롯한 대부분의 객체지향 언어가 단일 상속만 지원하여  
> 상속으로 인해 발생하는 중복코드 문제를 해결하기 어렵다.

</br>

### 기본 정책에 기본 요금 할인 정책 조합하기

</br>

- 부가 저액인 기본 요금 할인 정책을 Phone의 상속 계층에 추가

> 기본 요금 할인 정책이란 매달 청구되는 요금에서  
> 고정된 요금을 차감하는 부가 정책을 가리킨다.  
> 예를들어 매달 1000원을 할인해주는 요금제가 있다면  
> 이 요금제에는 부가정책으로 기본 요금 할인 정책이 조합돼있다.

</br>

- RateDiscountableRegularPhone(일반 요금제 + 기본 요금 할인 정책)

```java
public class RateDiscountableRegularPhone extends RegularPhone {
    private Money discountAmount;

    public RateDiscountableRegularPhone(Money amount, Duration seconds, Money discountAmount) {
        super(amount, seconds);
        this.discountAmount = discountAmount;
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return fee.minus(discountAmount);
    }
}
```

</br>

- RateDiscountableNightlyDiscountPhone(심야 할인 요금제 + 기본요금 할인정책)

```java
public class RateDiscountableNightlyDiscountPhone extends NightlyDiscountPhone {
    private Money discountAmount;

    public RateDiscountableNightlyDiscountPhone(Money nightlyAmount,
                                                Money regularAmount, Duration seconds, Money discountAmount) {
        super(nightlyAmount, regularAmount, seconds);
        this.discountAmount = discountAmount;
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return fee.minus(discountAmount);
    }
}
```

</br>

> 이것 역시 다른 정책의 조합을 위해 작성하였지만  
> 중복 코드가 발생하였다.

</br>

### 중복 코드의 덫에 걸리다

> 부가 정책은 자유롭게 조합 할 수 있어야하고 적용되는 순서도 결정할 수 있어야 함  
> 요구사항에 따르면 앞에서 구현한 세금 정책하고 기본요금 할인 정책 적용도 가능해야하지만  
> 모든 가능한 조합마다 자식클래스를 추가해야만 한다.

</br>

- ... 무의미한 조합들 작성하는 것 생략..

</br>

|                   클래스 폭발                   |
| :---------------------------------------------: |
| ![클래스 폭발](../res/_11_class_explosion.jpeg) |

</br>

> 위에서 하나의 정책을 만들기 위해서 하나의 클래스가 계속 추가되는 상황이 발생하였다.
> 이것을 클래스 폭발 또는 조합의 폭발이라고 부르는데  
> 이 문제는 자식 클래스가 부모 클래스의 구현에 강하게 결합되도록 강요받기 때문이다.  
> 컴파일 타임에 결정된 부모-자식간의 관계는 변경될 수 없음으로  
> 조합이 필요한 상황에서 해결 방법은 조합의 수만큼 새로운 클래스를 추가하는 것이다. -> 관리도 어려워..  
> 이렇게 클래스가 많으면 기능을 수정할때도 수정하는 양이 장난이 이니다.. 또한 버그문제도..

</br>
