# CHAPTER 12. 다형성

</br>

> 상속의 목적은 코드 재사용이 아니다.  
> 상속은 타입 계층을 구조화하기 위해 사용해야 한다.  
> 부모 클래스의 코드를 재사용하기 위해 상속을 사용했을 경우  
> 부모 코드와 자식 코드가 강결합 되는 것을 볼 수 있었다!

</br>

## 다형성

</br>

> 여러 타입을 대상으로 동작할 수 있는 코드를 작성할 수 있는 방법

</br>

|                 다형성의 분류                  |
| :--------------------------------------------: |
| ![다형성의 분류](../res/_12_polymorphism.jpeg) |

</br>

- 매개변수
  - 제네릭과 관련이 깊고, 변수나 메서드의 매개변수 타입을 임의이ㅡ 타입으로 선언 후
  - 사용하는 시점에 구체적인 타입으로 지정하는 방식
  - List\<T> 는 실제 인스턴스 생성 시점에 T를 지정한다.
- 포함
  - 메시지가 동일하더라도 수신하는 객체에 따라 실제로 수행하는 행동이 달라지는 능력
  - 서브타입 다형성
  - 일반적인 다형성을 얘기할 때!
- 오버로딩
  - 메서드 파라미터 타입이 다를때 메서드 이름이 다를 경우
- 강제 다형성
  - 언어가 지원하는 자동적인 타입 변환이나, 사용자가 지정하는 타입변환을 통해 다른 타입에 적용할 수 있는 것

## 상속의 양면성

</br>

### 상속을 사용한 강의 평가

</br>

- 원하는 결과물

```text
Pass:3 Fail:2, A:1 B:1 C:1 D:0 F:2
```

</br>

> Pass:3 Fail:2는 강의를 이수한 학생의 수와 낙제한 학생의 수  
> 나머지는 등급별로 학생들 분포 현황이다.

</br>

```java
public class Lecture {
    private int pass;
    private String title;
    private List<Integer> scores = new ArrayList<>();

    public Lecture(String title, int pass, List<Integer> scores) {
        this.title = title;
        this.pass = pass;
        this.scores = scores;
    }

    public double average() {
        return scores.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    public List<Integer> getScores() {
        return Collections.unmodifiableList(scores);
    }

    public String evaluate() {
        return String.format("Pass:%d Fail:%d", passCount(), failCount());
    }

    private long passCount() {
        return scores.stream().filter(score -> score >= pass).count();
    }

    private long failCount() {
        return scores.size() - passCount();
    }
}
```

</br>

> Lecture를 살펴보면 pass - 기준, title - 강의 제목, scores - 학생들의 점수를 갖는다.  
> evalueate를 통해 `Pass:3 Fail:2` 이렇게 출력 가능하다.
> 학생의 점수가 pass 이상이면 이수한 것으로 판단한다.

</br>

```java
public class GradeLecture extends Lecture {
    private List<Grade> grades;

    public GradeLecture(String name, int pass, List<Grade> grades, List<Integer> scores) {
        super(name, pass, scores);
        this.grades = grades;
    }

}
```

> Lecture에서 점수를 평가하고 기본 출력을 이미 담당해주어  
> 상속을 통해 원하는 출력물을 만들기 위한 클래스를 작성한다.
> 우선 GradeLecture의 인스턴스 변수로 grades를 갖고

</br>

```java
public class Grade {
    private String name;
    private int upper,lower;

    private Grade(String name, int upper, int lower) {
        this.name = name;
        this.upper = upper;
        this.lower = lower;
    }

    public String getName() {
        return name;
    }

    public boolean isName(String name) {
        return this.name.equals(name);
    }

    public boolean include(int score) {
        return score >= lower && score <= upper;
    }
}
```

> Grade는 등급의 이름과 각 등급의 범위를 정의하는 성적을 인스턴스로 갖는다.  
> include는 수강생의 성적이 등급에 포함된느지를 검사한다.

</br>

```java
public class GradeLecture extends Lecture {
    @Override
    public String evaluate() {
        return super.evaluate() + ", " + gradesStatistics();
    }

    private String gradesStatistics() {
        return grades.stream().map(grade -> format(grade)).collect(joining(" "));
    }

    private String format(Grade grade) {
        return String.format("%s:%d", grade.getName(), gradeCount(grade));
    }

    private long gradeCount(Grade grade) {
        return getScores().stream().filter(grade::include).count();
    }

    public double average(String gradeName) {
        return grades.stream()
                .filter(each -> each.isName(gradeName))
                .findFirst()
                .map(this::gradeAverage)
                .orElse(0d);
    }

    private double gradeAverage(Grade grade) {
        return getScores().stream()
                .filter(grade::include)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }
}
```

</br>

> 이제 원하는 출력물, 학생들의 이수 여부와 등급별 통계를 함께 반환하도록  
> evaluate 메서드 재정의하였다.

</br>

> evaluate에서는 `super의 evaluate를 먼저 출력한다.`  
> GradeLecture의 시그니처와 Lecture의 시그니처가 동일하다.  
> 이 경우 자식 클래스의 메서드 우선순위가 더 높은데,  
> 메시지를 수신할 경우 부모 클래스의 메서드가 아닌  
> 자식 클래스의 메서드가 실행된다는 것이다.

</br>

> 결과적으로 오버라이딩한 동일한 시그니처를 가진 자식 메서드가  
> 부모클래스의 메서드를 가리게 된다.  
> 따라서 GradeLecture의 evaluate가 메시지를 수신 받으면  
> Lecture의 evaluate 메서드를 오버라이딩 한  
> GradeLecture의 evalueate 메서드가 실행 된다.

</br>

```java
    public double average(String gradeName) {
        return grades.stream()
                .filter(each -> each.isName(gradeName))
                .findFirst()
                .map(this::gradeAverage)
                .orElse(0d);
    }
```

> 부모 클래스의 없는 메서드도 작성 가능하다.  
> 부모 클래스와 시그니처가 달라 공존 할 수 있고  
> 이를 메서드 오버로딩이라고 한다.

</br>

### 데이터 관점의 상속

</br>

```java
Lecture = new Lecture("객체지향 프로그래밍",
                      70,
                      Arrays.asList(81, 95, 75, 50, 45));
```

> Lecture의 인스턴스를 생성하면 시스템은 인스턴스 변수 title, pass, scores를  
> 저장할 수 있는 메모리 공간을 할당하고 생성자의 매개 변수를 이용해 값을 설정한 후  
> 생성된 인스턴스의 주소를 lecture라는 이름의 변수에 대입한다.

</br>

```java
Lecture lecture = new GradeLecture("객체지향 프로그래밍",
                       70,
                      Arrays.asList(
                          new Grade("A", 100, 95),
                          new Grade("B", 94, 80),
                          new Grade("C", 79, 70),
                          new Grade("D", 69, 50),
                          new Grade("F", 49, 0),
                      ),
                      Arrays.asList(81, 95, 75, 50, 45));
```

</br>

> 이번에는 GradeLecture의 인스턴스를 생성할 경우  
> 직접 정의한 인스턴스 변수뿐만 아니라 부모 클래스인 Lecture가  
> 정의한 인스턴스 변수도 포함한다.

</br>

|                   부모 클래스의 인스턴스를 포함하는 자식                   |
| :------------------------------------------------------------------------: |
| ![부모 클래스의 인스턴스를 포함하는 자식](../res/_12_parent_instance.jpeg) |

</br>

> 상속을 인스턴스 관점에서 바라볼 때 개념적으로  
> 자식 클래스의 인스턴스 안에 부모 클래스의 인스턴스가  
> 포함되는 것으로 생각하는 것이 유용하다.  
> 인스턴스를 참조하는 lecutre는 GradeLecture의 인스턴스를 가리키기 때문에  
> 특별한 방법을 사용하지 않으면 GradeLecture안에 포함된
> Lecture의 인스턴스에 직접 접근 할 수 없다.

</br>

> 데이터 관점에서 상속은 자식 클래스의 인스턴스 안에  
> 부모 클래스의 인스턴스를 포함하는 것으로 볼 수 있다.  
> 따랄서 자식 클래스의 인스턴스는 자동으로 부모 클래스에서  
> 정의한 모든 인스턴스 변수를 내부에 포함하게 되는 것이다.

</br>

### 행동 관점의 상속

</br>

> 행동 관점의 상속은 부모 클래스가 정의한 일부 메서드를  
> 자식 클래스의 메서드로 포함 시키는 것을 의미

</br>

- 어떻게?

> 런타임에 자식클래스가 메서드를 실행 시키려고 할 때  
> 자식 클래스에서 메서드 탐색 과정을 실행 한 후  
> 없다면 상위 클래스로 메서드 탐색을 이어가기 때문이다.

</br>

> 객체의 경우 서로 다른 상태를 저장할 수 있도록 각 인스턴스 별로  
> 독립적인 메모리를 할당 받아야 한다.  
> 하지만 메서드는 동일한 클래스의 인스턴스끼리 공유가 가능함으로 클래스는 한 번만  
> 메모리에 로드하고 각 인스턴스별로 클래스를 가리키는 포인터를 갖게하는 것이 경제적

</br>

|                  클래스와 인스턴스의 개념적인 관계                   |
| :------------------------------------------------------------------: |
| ![클래스와 인스턴스의 개념적인 관계](../res/_12_class_instance.jpeg) |

</br>

> 그림을 살펴 보면 인스턴스는 두개가생성 됐지만  
> 클래스는 단 하나만 메모리에 로드 되었다.  
> 메시지를 수신한 객체는 class 포인터로 연결된 자신의 클래스에서  
> 적절한 메서드가 존재하는지 판단한다.  
> 그 후 재하지 않으면 parent 포인터를 타고 올라가  
> 부모 클래스를 차례대로 훑어 가면서 메서드가 존재하는지 탐색한다.

</br>

## 업캐스팅과 동적 바인딩

</br>

```java
public class Professor {
    private String name;
    private Lecture lecture;

    public Professor(String name, Lecture lecture) {
        this.name = name;
        this.lecture = lecture;
    }

    public String compileStatistics() {
        return String.format("[%s] %s - Avg: %.1f", name,
                lecture.evaluate(), lecture.average());
    }
}
```

> 실행 시점에 메서드 탐색하는 과정을 보기 위해  
> 지금까지 작성한 성적 계산 프로그램에 각 교수별로 강의에 대한  
> 성적 통계 계산하느 기능 추가하기.

</br>

> 통계 계산 책임은 Professor 클래스가 맡도록 하자.  
> compileStatics는 통계 정보를 생성하기 위해  
> Lecture의 evaluate와 average 메서드 호출

</br>

> lecture가 GradeLecture로 전달 되어도 아무 문제없이 실행 된다.  
> 동일한 객체 참조인 lecture에 대해 동일한 evaluate 메시지를 전송하면  
> 코드 안에서 서로 다른 클래스 안에 구현된 메서드가 실행된다.

</br>

> 이처럼 코드 안에서 선언된 참조 타입(`Professor의 Lecture`)과 무관하게  
> 실제로 메시지를 수신하는 객체의 타입에 따라 실행되는 메서드가 달라질 수 있는 것이  
> 업캐스팅과 동적바인딩이라는 메커니즘이 작용하기 때문이다.

</br>

- 업캐스팅
  - 부모 클래스 타입으로 선언된 변수에 자식 클래스의 인스턴스를 할당하는 것이 가능
- 동적바인딩
  - 선언된 변수 타입이 아니라 메시지를 수신하는 객체의 타입에 따라 실행되는 메서드가 결정
  - 객체지향 시스템이 메시지를 처리할 메서드를 런타임에 결정하기 때문!

</br>

### 업캐스팅

</br>

> 상속을 이용하면 부모의 퍼블릭 인터페이스와 자식의 퍼블릭 인터페이스가 합쳐지기 때문에  
> 부모 클래스의 인스턴스에게 전송할 수 있는 메시지를 자식 클래스의 인스턴스에게 전송할 수 있다.  
> 또한 대입문과 파라미터 타입을 통해 부모 타입에 자식 타입을 전달하여 사용하는 것도 가능하다.

</br>

```java
Lecture lecture = new GradeLecture(); //대입문

public class Professor { // 파라미터
  public Professor(String name, Lecture lecture) {...}
}

Professor professor = new Professor("교수님", new GradeLecture());
```

</br>

```java
Lecture lecture = new GradeLecture();
GradeLecture gradeLecture = (GradeLecture) lecture;
```

|                 다운 캐스팅                  |
| :------------------------------------------: |
| ![다운 캐스팅](../res/_12_down_casting.jpeg) |

> 반대로 부모 클래스의 인스턴스를 자식 클래스타입으로 변환하기 위해  
> 사용하는 명시적 타입캐스팅을 다운 캐스팅이라고 한다.

</br>

### 동적 바인딩

> 전통적인 언어에서 함수를 실행하는 방법은 함수를 호출하는 것이다.  
> 객체지향 언어에서 메서드를 실행하는 방법은 메시지를 전송하는 것이다.  
> 이 두가지의 메커니즘은 완전히 다르다.

- 함수 호출
  - 코드를 작성하는 시점에 호출될 코드가 결정된다.
  - 컴파일 타임에 호출할 함수를 결정하는 방식을
  - 정적 바인딩(static binding), 초기 바인딩(early binding), 컴파일타임 바인딩(compile-time binding)
- 메서드 호출
  - 메시지를 수신했을 때 실행될 메서드가 런타임에 결정된다.
  - 실행될 메서드를 런타임에 결정하는 방식을 동적 바인딩(dynamic binding), 지연 바인딩(late binding)

</br>

## 동적 메서드 탐색과 다형성

</br>

</br>

## 상속 대 위임

</br>

</br>
