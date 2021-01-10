package chat;

import chat.auth.AuthService;
import chat.auth.BaseAuthService;
import chat.handler.ClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final ServerSocket serverSocket;
    private final AuthService authService;
    private final List<ClientHandler> clients = new ArrayList<>();

    public static final Logger LOGGER = LogManager.getLogger(MyServer.class);

    public MyServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.authService = new BaseAuthService();
    }


    public void start() throws IOException {

//        LOGGER.debug("Debug");
//        LOGGER.info("Info");
//        LOGGER.warn("Warn");
//        LOGGER.error("Error");
//        LOGGER.fatal("Fatal");
//        LOGGER.info("String: {}." , "Hello, World");

//        System.out.println("Сервер запущен!");
        LOGGER.info("Сервер запущен!");
        authService.start();

        try {
            while (true) {
                waitAndProcessNewClientConnection();

            }
        } catch (IOException e) {
            LOGGER.error("Ошибка создания нового подключения");
//            System.out.println("Ошибка создания нового подключения");
            e.printStackTrace();
        }
        finally {
            serverSocket.close();
        }
    }

    private void waitAndProcessNewClientConnection() throws IOException {
        LOGGER.info("Ожидание пользователя...");
//        System.out.println("Ожидание пользователя...");
        Socket clientSocket = serverSocket.accept();
        LOGGER.info("Клиент подключился!");
//        System.out.println("Клиент подключился!");
        processClientConnection(clientSocket);
    }

    private void processClientConnection(Socket clientSocket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.handle();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }
    public void unSubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public boolean isUsernameBusy(String username) {

        for (ClientHandler client : clients) {
            if(client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;

    }

    public void broadcastMessage(String message, ClientHandler sender, boolean isServerInfoMsg) throws IOException {
        for (ClientHandler client : clients) {
            if(client == sender) {
                continue;
            }
            client.sendMessage(isServerInfoMsg ? null : sender.getUsername(), message);
        }
    }
}
