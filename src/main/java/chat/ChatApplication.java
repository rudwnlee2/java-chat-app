package chat;

import chat.server.ChatServer;

public class ChatApplication {
    public static void main(String[] args) {
        // 1. 우리 서버를 9090 포트로 생성합니다.
        ChatServer chatServer = new ChatServer(9090);

        // 2. 서버를 실행시킵니다.
        chatServer.start();
    }
}
