package chat.server;

import chat.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {

    private final int port;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {

                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트 접속: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);
                new Thread(handler).start();
            }

        } catch (IOException e) {
            System.err.println("서버 오류: " + e.getMessage());
        }
    }

    public void broadcastMessage(String message, ClientHandler sender) {
        System.out.println("브로드캐스트: " + message);
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("클라이언트 접속 종료: " + client.getClientAddress());
    }

}
