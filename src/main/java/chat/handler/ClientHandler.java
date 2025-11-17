package chat.handler;

import chat.server.ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final ChatServer server;
    private PrintWriter out;
    private String clientAddress;

    public ClientHandler(Socket clientSocket, ChatServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.clientAddress = clientSocket.getInetAddress().toString();
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {

                if ("exit".equals(line)) {
                    System.out.println("클라이언트 종료: " + clientSocket.getInetAddress());
                    break;
                }

                String message = "[" + this.clientAddress + "]: " + line;
                server.broadcastMessage(message, this);
            }

        } catch (IOException e) {
            System.err.println("클라이언트 통신 오류: " + e.getMessage());
        } finally {
            server.removeClient(this);
            try {
                clientSocket.close();
            } catch (IOException e) {
                // ...
            }
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            out.flush();
        }
    }

    public String getClientAddress() {
        return this.clientAddress;
    }

}
