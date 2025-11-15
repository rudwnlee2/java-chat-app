# Blocking (블록킹), Non-Blocking (논블록킹), Synchronous (동기), Asynchronous (비동기)

## Blocking (블록킹), Non-Blocking (논블록킹)

### Blocking (블록킹)
- 함수를 호출 했을 때 작업이 완료될 때까지 호출자(스레드)에게 제어권이 돌아오지 않는 상태
- 비유하면 직원이 상사에게 검토 받으러 갔는데 상사가 검토 끝날때까지 앞에서 기다리라고 할때 -> 직원은 다른일을 하지 못하고 기다려야 함 즉 상사에게 제어권이 있음

### Non-Blocking (논블록킹)

- 함수를 호출했을 때 작업 완료 여부와 상관없이 제어권이 즉시 호출자(스레드)에게 돌아오는 상태
- 비유하면 직원이 상사에게 검토 받으로 갔는데 상사가 검토 해볼테니 돌아가라 -> 직원은 검토가 끝날때까지 기다리지 않고 돌아가서 다른거를 할 수 있음 즉 제어권이 직원에게 있음

### 차이
다른 주체가 작업할 때 자신의 제어권이 있는지 없는지로 볼 수 있음

## Synchronous (동기), Asynchronous (비동기)

### Synchronous (동기)

- 작업을 순차적으로 실행하며, 이전 작업이 완료되어야 다음 작업을 시작
- 호출한 함수의 결과를 직접 받아서 처리

### Asynchronous (비동기)

- 작업을 동시에 실행할 수 있으며, 이전 작업 완료를 기다리지 않고 다음 작업 시작 가능
- 호출한 함수의 결과를 나중에 처리


### 차이
작업의 실행 순서와 완료 시점에 대한 판단

## 내가 궁금했던 점

### 논블록킹
- 작업이 끝났는지는 어떻게 확인하지?
  - 작업이 끝났는지 계속 확인해야함 이거를 폴링이라고 함
  - 폴링: 주기적으로 작업 완료 상태를 확인하는 방식
  
### 블록킹
- ServerSocket.accept()는 왜 블록킹인가?
  - 원인을 찾기 위해서 ServerSocket, SocketImpl, SocketOptions을 확인함
  - SocketOptions에 @Native 어노테이션을 발견함 -> @Native은 뭐지?
    - 해당 어노테이션이 적용된 상수(field)가 네이티브 코드에서 참조될 수 있음을 나타냄
  - 네이티브 메서드는 뭐지?
    - JNI(Java Native Interface)는 Java 프로그램이 다른 언어로 작성된 프로그램과 상호 작용할 수 있게 해주는 인터페이스로 C 와 C++ 로 작성된 프로그램과의 상호작용을 위해 사용됨
    - JNI 는 C, C++ 처럼 인터프리터 없이 OS 가 바로 읽을 수 있는 형태의 네이티브 코드를 JVM 이 호출할 수 있게 하는 인터페이스
    - 네이티브 키워드는 c와 c++로 작성된 함수를 호출하게 해줌
- accept()는 어디에 native가 달려있지?
  - ServerSocket, SocketImpl, SocketOptions, DelegatingSocketImpl 확인했는데 native 키워드로 된 메서드가 없었음
    - 지피티, 제미나이 답변: 자바 내부적으로 숨겨져 있어 확인할 수 없다(역시서 AI를 너무 믿어서는 안되겠다고 느낌)
    - 블로그, 자바 공식 문서를 통해 Net.java에 accept에 native 키워드가 선언되어 있는 것을 확인, 추가로 공식문서에 The method blocks until a connection is made. 라고 되어있는 것을 확인
- accept는 동기이면서 블로킹

## 참고
- https://www.youtube.com/watch?v=oEIoqGd-Sns&t=299s
- https://inpa.tistory.com/entry/%F0%9F%91%A9%E2%80%8D%F0%9F%92%BB-%EB%8F%99%EA%B8%B0%EB%B9%84%EB%8F%99%EA%B8%B0-%EB%B8%94%EB%A1%9C%ED%82%B9%EB%85%BC%EB%B8%94%EB%A1%9C%ED%82%B9-%EA%B0%9C%EB%85%90-%EC%A0%95%EB%A6%AC#blocking_/_non-blocking
- https://ones1kk.tistory.com/entry/Java-Native-Method
- https://mangkyu.tistory.com/438
