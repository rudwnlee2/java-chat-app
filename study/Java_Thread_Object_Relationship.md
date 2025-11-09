# 스레드와 객체의 관계 학습 정리

## 핵심 개념: 스레드 vs 객체

### 스레드와 객체의 유사점
- **상태(State) 보유**: 둘 다 자신만의 정보를 가지고 있음
- **독립적 실행 컨텍스트**: 각자의 영역에서 작업 수행
- **캡슐화된 책임**: 특정 역할을 담당

### 핵심 차이점
- **객체**: 데이터 + 행동의 묶음 (논리적 단위)
- **스레드**: 실행 흐름 + 컨텍스트 (물리적 실행 단위)

### 관계 정의
```
// 스레드 = 일꾼, 객체 = 도구
Thread workerThread = new Thread(() -> {
    Socket socket = clientSocket;           // Socket 객체 사용
    BufferedReader reader = new BufferedReader(...); // Reader 객체 사용
    String message = reader.readLine();     // 스레드가 객체의 메서드 호출
});
```

---

## 채팅 서버에서의 스레드 활용

### 현재 구조의 동작 방식
```
while (true) {
    Socket clientSocket = serverSocket.accept(); // main 스레드가 연결 대기
    new Thread(() -> handleClient(clientSocket)).start(); // 각 클라이언트마다 새 스레드
}
```

### 스레드가 필요한 이유
- **동시성**: 여러 클라이언트를 동시에 처리
- **독립성**: 각 클라이언트는 독립적으로 작동
- **실시간성**: 한 클라이언트가 메시지를 안 보내도 다른 클라이언트는 정상 처리

### 실행 흐름
```
스레드1: 클라이언트A readLine() 대기 중...
스레드2: 클라이언트B readLine() 대기 중...  
스레드3: 클라이언트C readLine() 대기 중...
```

---

## 스레드 풀 (Thread Pool)

### 개념
미리 생성된 스레드들을 재사용하여 스레드 생성/소멸 비용을 절약하는 기법

### 일반적인 웹 서버에서의 활용
```
ExecutorService threadPool = Executors.newFixedThreadPool(100);

// 요청 처리
threadPool.submit(() -> {
    handleRequest(request); // 요청 → 처리 → 응답 → 스레드 반납 ✅
});
```

### 채팅 서버에서 스레드 풀이 제한적인 이유
```
threadPool.submit(() -> {
    while (client.isConnected()) {
        String message = readLine(); // 계속 대기...
    }
}); // 스레드가 반납되지 않음! ❌
```

**문제점:**
- **웹 서버**: 요청 → 처리 → 응답 → 스레드 반납 ✅
- **채팅 서버**: 연결 → 계속 점유 → 연결 끊을 때까지... ❌

---

## 커넥션 풀 vs 스레드 풀

### 커넥션 풀 (Connection Pool)
```
// 데이터베이스 연결을 미리 만들어 놓는 것
HikariDataSource connectionPool = new HikariDataSource();
connectionPool.setMaximumPoolSize(10); // DB 연결 10개 미리 생성
```

### 스레드 풀 (Thread Pool)
```
// 스레드를 미리 만들어 놓는 것
ExecutorService threadPool = Executors.newFixedThreadPool(50);
```

### 실제 스프링에서의 조합
```
// 요청 처리 과정
threadPool.submit(() -> {
    Connection conn = connectionPool.getConnection(); // 커넥션 빌려옴
    
    // 실제 DB 작업 (이 스레드가 수행)
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
    ResultSet rs = stmt.executeQuery();
    
    connectionPool.returnConnection(conn); // 커넥션 반납
}); // 스레드도 풀로 반납
```

---

## 스프링의 실제 구조

### 설정 예시
```
# application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10    # 커넥션 10개
  task:
    execution:
      pool:
        core-size: 20          # 스레드 20개
        max-size: 50           # 최대 스레드 50개
```

### 동시 요청 처리
```
// 실제 매핑
요청1 → 스레드1 → 커넥션1 → DB 작업
요청2 → 스레드2 → 커넥션2 → DB 작업  
요청3 → 스레드3 → 커넥션3 → DB 작업
...
요청11 → 스레드11 → 대기 (커넥션 10개 모두 사용 중)
```

### 왜 스레드 > 커넥션인가?
```
@GetMapping("/api")
public String api() {
    // 1. 비즈니스 로직 (CPU 작업)
    String result = businessLogic();
    
    // 2. DB 작업 (커넥션 필요)
    User user = userRepository.findById(1L);
    
    // 3. 외부 API 호출 (커넥션 불필요)
    String external = restTemplate.getForObject("...", String.class);
    
    return result;
}
```

**효율성:**
- **스레드**: CPU 작업 + I/O 대기 모두 처리
- **커넥션**: 실제 DB 통신만 담당
- **결과**: 적은 커넥션으로도 많은 요청 처리 가능

---

## 핵심 깨달음

### 1. 스레드와 객체의 관계
- **스레드**: 실행의 주체 (일꾼)
- **객체**: 실행에 필요한 자원과 도구
- **관계**: 스레드가 객체를 사용하여 작업 수행

### 2. 풀(Pool) 패턴의 이해
- **커넥션 풀**: Connection 객체들을 재사용
- **스레드 풀**: Thread들을 재사용
- **조합**: 하나의 스레드가 하나의 커넥션을 독점 사용

### 3. 채팅 서버의 특수성
- 일반적인 스레드 풀 패턴이 제한적
- 각 클라이언트가 연결을 계속 유지
- NIO + Selector로 근본적 해결 필요
