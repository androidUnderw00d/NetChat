package client;

import client.controllers.AuthController;
import client.controllers.ChatController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import client.models.Network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class NetworkClient extends Application {


    public Stage primaryStage;
    private Stage authStage;
    private Network network;
    private ChatController chatController;
    public static final List<String> USERS_TEST_DATA = List.of("Чубака", "Оби Ван Кеноби", "Йода");


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        network = new Network();
        if (!network.connect()) {
            showErrorMessage("Проблемы с соединением", " ","Ошибка подключения к серверу");
            return;
        }
        openAuthDialog(primaryStage);
        createChatDialog(primaryStage);
    }


    private void openAuthDialog(Stage primaryStage) throws IOException {
        FXMLLoader authLoader = new FXMLLoader();
        authLoader.setLocation(NetworkClient.class.getResource("/auth-view.fxml"));
        Parent page = authLoader.load();
        authStage = new Stage();

        authStage.setTitle("Авторизация");
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        authStage.setScene(scene);
        authStage.show();

        AuthController authController = authLoader.getController();
        authController.setNetwork(network);
        authController.setNetworkClient(this);
    }

    private void createChatDialog(Stage primaryStage) throws IOException {
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(NetworkClient.class.getResource("/chat-view.fxml"));

        Parent root = mainLoader.load();

        primaryStage.setTitle("Messenger");
        primaryStage.setScene(new Scene(root, 600, 400));

        chatController = mainLoader.getController();
        chatController.setNetwork(network);


        primaryStage.setOnCloseRequest(event -> network.close());
    }

    public static void showErrorMessage(String title, String message, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(errorMessage);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void openMainChatWindow() {
        authStage.close();
        primaryStage.show();
        primaryStage.setTitle(network.getUsername());
        System.out.println(network.getUsername());
        chatController.setLabel(network.getUsername());
        network.waitMessage(chatController);

        try {
            File file = new File("ChatClient/src/main/resources/history.txt");
            FileReader reader = new FileReader(file);
            List<String> lines = Files.lines(Paths.get("ChatClient/src/main/resources/history.txt")).collect(Collectors.toList());
            int count = lines.size();
            int lastHundredLines = (count - 100);
            for (int i = lastHundredLines; i < count; i++) {
                chatController.appendMessage(lines.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

/*    private static class Launcher{
        public static void main(String[] args) {
            NetworkClient.main(args);
        }
    }*/
}
