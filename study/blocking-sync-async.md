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
    - 지피티, 제미나이 답변: "자바 내부적으로 숨겨져 있어 확인할 수 없다" 라는 답변을 줌
    - 블로그, 자바 공식 문서를 통해 Net.java에 accept에 native 키워드가 선언되어 있는 것을 확인, 추가로 공식문서에 The method blocks until a connection is made. 라고 되어있는 것을 확인
- accept는 동기이면서 블로킹

## 동기/비동기 + 블로킹/논블로킹 조합

### Sync Blocking (동기 + 블로킹)
Sync + Blocking 조합은 작업이 다른 곳에서 처리되고 있을 때 다른일을 하지 않고, 작업이 처리됨에 따라 바로 결과를 받아 순차적으로 처리한다. 작업의 결과가 직접적으로 영향을 준다.

예를 들어 직원이 보고서 팀장한테 검토를 받으러 갔을 때 팀장이 보고서 검토가 끝날 때까지 앞에서 기다리라 하고 보고서 검토가 끝나면 팀장이 직접 보고서를 앞에있는 직원에게 주는 경우 그리고 직원은 보고서를 받아 바로 다음 업무를 시작

accept()를 예시로 들면

자바에서 먼저 accept() 실행 -> OS로 system call 진입 (스레드가 커널 모드로 전환) ->  연결 없으면 블로킹 (sleep) -> 클라이언트 SYN 도착 -> 커널이 TCP handshake 진행 (SYN → SYN+ACK → ACK) -> 커널이 "연결 됐다" 판단되면: 새 소켓 생성, accept() 기다리던 스레드 깨움 -> accept() 반환: 자바는 Socket 객체를 받음, 이때부터 read/write 가능
### Async Blocking (비동기 + 블로킹)
Async Blocking 조합은 작업이 다른 곳에서 처리되고 있을 때 다른일을 하지않고, 작업이 끝나고 바로 다음 작업을 바로 처리하지 않아 순차적으로 진행하지 않는 방식

### Sync Non-Blocking (동기 + 논블로킹)
Sync Non-Blocking 조합은 작업이 다른 곳에서 처리되고 있을 때 자신의 다른 작업을 수행하고, 다른 곳에서 끝난 작업을 바로 받아서 순차적으로 처리한다.

예를 들어 직원이 보고서를 팀장에게 검토 요청하고 다른 일을 하다가, 나중에 팀장 사무실에 가서 "보고서 다 됐나요?"라고 물어봤는데 아직 안 끝났으면 그 자리에서 끝날 때까지 기다리는 경우

예시: 파일 다운로드 상태를 계속 확인하는 경우

### Async Non-Blocking (비동기 + 논블로킹)
Async Non Blocking 조합은 작업이 다른 곳에서 처리되고 있을 때 자신의 다른 작업을 수행하고, 다른 곳에서 작업이 끝나 도착해도 바로 처리하지 않아 순차적으로 진행하지 않는 방식

예를 들어 직원이 보고서를 팀장에게 검토 요청하고 다른 일을 하고 있는데, 팀장이 검토 완료 후 메신저로 알림을 보내면 직원은 그 알림을 바로 확인하지 않고 현재 하던 일을 마친 후에 나중에 확인하는 경우


## 느낀점
AI에 질문을 하고 정보를 얻어가는것에 대해 한번 더 생각해보게 되었다. 특히 accept() 이부분을 찾을 때 크게 느꼈다. 한번 더 확인해보는 시간을 가져야겠다.

네트워크도 알아야할게 많다...

## 참고
- https://www.youtube.com/watch?v=oEIoqGd-Sns&t=299s
- https://inpa.tistory.com/entry/%F0%9F%91%A9%E2%80%8D%F0%9F%92%BB-%EB%8F%99%EA%B8%B0%EB%B9%84%EB%8F%99%EA%B8%B0-%EB%B8%94%EB%A1%9C%ED%82%B9%EB%85%BC%EB%B8%94%EB%A1%9C%ED%82%B9-%EA%B0%9C%EB%85%90-%EC%A0%95%EB%A6%AC#blocking_/_non-blocking
- https://ones1kk.tistory.com/entry/Java-Native-Method
- https://mangkyu.tistory.com/438
- https://gray-room.tistory.com/entry/SOCKET-TCP%EC%9D%98-%EC%97%B0%EA%B2%B0establish%EA%B3%BC-%EC%97%B0%EA%B2%B0%EC%A2%85%EB%A3%8Cclose
- https://d2.naver.com/helloworld/47667
