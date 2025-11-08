# 자바 IO 스트림(Stream) 학습 정리

## 스트림(Stream)이란 무엇인가?

- **비유**: 데이터가 흐르는 **"통로(Pipe)"**
- **동작**:
    - **InputStream**: 자바가 데이터를 읽어올 때 사용하는 통로
    - **OutputStream**: 자바가 데이터를 밖으로 내보낼 때 사용하는 통로

---

## 스트림의 3가지 특징

- **단방향 (Unidirectional)**
    - 데이터는 한쪽 방향으로만 흐름
    - 읽기용(`InputStream`)과 쓰기용(`OutputStream`) 통로는 항상 분리됨
- **FIFO (First-In, First-Out)**
    - 선입선출 구조
    - 먼저 보낸 데이터가 반대편에 먼저 도착함
- **블로킹 (Blocking)**
    - **입력**: `read()` 호출 시 통로에 데이터가 없으면, 데이터가 들어올 때까지 스레드가 멈춤 (WAITING)
    - **출력**: `write()` 호출 시 통로(버퍼)가 꽉 차면, 빈 공간이 생길 때까지 스레드가 멈춤 (WAITING)

---

## 스트림의 종류

자바 IO 스트림은 크게 **바이트 스트림**과 **문자 스트림**으로 나뉜다.

### 바이트 스트림 (Byte Streams)
- **최상위 클래스**: `InputStream`, `OutputStream`
- **처리 단위**: 1바이트
- **특징**: 모든 스트림의 기본. **모든 종류의 데이터** (텍스트, 이미지, 오디오 등) 처리 가능

### 문자 스트림 (TextStream)
- **최상위 클래스**: `Reader`, `Writer`
- **처리 단위**: 16비트 (`char`)
- **특징**: **텍스트 데이터 전용**. **인코딩/디코딩**을 자동으로 처리해 줌

---

## 보조 스트림 (Chaining): 기능 및 성능 추가

- **목적**: 기본 스트림은 1바이트/1문자씩 처리해 비효율적. 기본 스트림에 연결(Wrapping)하여 성능과 편의 기능을 추가함.

### 버퍼 스트림 (성능 향상)
- `BufferedInputStream`, `BufferedOutputStream` (바이트용)
- `BufferedReader`, `BufferedWriter` (문자용)
- **역할**: 데이터를 '버퍼(임시 저장통)'에 모았다가 한 번에 처리. I/O 횟수를 줄여 **성능을 극적으로 향상**시킴.

### 편의 기능 스트림
- **BufferedReader**: `readLine()` 메소드를 제공 (한 줄을 통째로 읽어 `String` 반환)
- **PrintWriter**: `println()`, `printf()` 등 편리한 텍스트 출력 기능 제공

---

## 변환 스트림 (Bridging): 바이트와 문자의 연결

- **목적**: 바이트 스트림을 문자 스트림으로 변환해주는 **"다리(Bridge)"** 역할.
- **필요성**: `InputStream`은 바이트만 읽지만, 실제로는 '문자열'을 읽고 싶을 때 사용.

### InputStreamReader
- `InputStream` (바이트 입력) → `Reader` (문자 입력)로 변환
- **역할**: 지정된 문자셋(예: "UTF-8") 기준으로 바이트를 문자로 **'디코딩(Decoding)'** 함

### OutputStreamWriter
- `OutputStream` (바이트 출력) → `Writer` (문자 출력)로 변환
- **역할**: 자바 문자(`char`)를 지정된 문자셋 기준으로 바이트로 **'인코딩(Encoding)'** 함