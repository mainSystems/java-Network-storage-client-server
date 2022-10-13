package main.systems.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.systems.client.controllers.ClientController;

import java.io.IOException;

public class ClientApplication extends Application {
    private static ClientApplication INSTANCE;
    private Stage stageClient;
    private Stage stageAuth;

    private FXMLLoader clientLoader;
    private FXMLLoader authLoader;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.stageClient = primaryStage;

        initViews();
//        stageClient.show();
        stageAuth.show();
    }

    private void initViews() throws IOException {
        initStorageWindow();
        initAuthWindow();
    }

    private void initStorageWindow() throws IOException {
        clientLoader = new FXMLLoader();
        clientLoader.setLocation(getClass().getResource("client-template.fxml"));

        BorderPane clientPanel = clientLoader.load();

        stageClient = new Stage();
        stageClient.initStyle(StageStyle.TRANSPARENT);
        stageClient.setScene(new Scene(clientPanel));
    }

    private void initAuthWindow() throws IOException {
        authLoader = new FXMLLoader();
        authLoader.setLocation(getClass().getResource("authDialog.fxml"));
        AnchorPane authDialogPanel = authLoader.load();

        stageAuth = new Stage();
//        stageAuth.initOwner(stageClient);
        stageAuth.initStyle(StageStyle.TRANSPARENT);
        stageAuth.initModality(Modality.WINDOW_MODAL);
        stageAuth.setScene(new Scene(authDialogPanel));
    }


    @Override
    public void init() throws Exception {
        INSTANCE = this;
    }

    public static ClientApplication getINSTANCE() {
        return INSTANCE;
    }

    public void switchToMainWindow(String username) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stageAuth.close();

                getClientController().getTreeFiles();
                stageClient.show();
                stageClient.setTitle(username);
                getClientController().setClientLabel(username);
            }
        });
    }

    public ClientController getClientController() {
        return clientLoader.getController();
    }
}