package chat.auth;

import chat.User;

import java.util.List;

import static chat.MyServer.LOGGER;

public class BaseAuthService implements AuthService {

    private static final List<User> clients = List.of(
            new User("user1", "1111", "Чубака"),
            new User("user2", "2222", "Оби Ван Кеноби"),
            new User("user3", "3333", "Йода")
    );

    @Override
    public void start() {
        LOGGER.info("Сервер запущен!");
//        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User client : clients) {
            if(client.getLogin().equals(login) && client.getPassword().equals(password)) {
                return client.getUsername();
            }
        }
        return null;
    }

    @Override
    public void close() {
        System.out.println("Сервис аутентификации завершен");

    }
}
