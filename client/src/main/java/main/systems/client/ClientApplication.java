package main.systems.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ClientApplication extends Application {
    private Stage stageStorage;
    private Stage stageAuth;
    private FXMLLoader storageLoader;
    private FXMLLoader authLoader;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.stageStorage = primaryStage;

        initViews();
        stageStorage.show();
        stageAuth.show();
    }

    private void initViews() throws IOException {
        initStorageWindow();
        initAuthWindow();
    }

    private void initStorageWindow() throws IOException {
        storageLoader = new FXMLLoader();
        storageLoader.setLocation(ClientApplication.class.getResource("client-template.fxml"));

        Parent root = storageLoader.load();
        Scene scene = new Scene(root);

        stageStorage.initStyle(StageStyle.TRANSPARENT);
        stageStorage.setScene(scene);
    }

    private void initAuthWindow() throws IOException {
        authLoader = new FXMLLoader();
        authLoader.setLocation(ClientApplication.class.getResource("authDialog.fxml"));
        AnchorPane authDialogPanel = authLoader.load();

        stageAuth = new Stage();
        stageAuth.initOwner(stageStorage);
        stageAuth.initStyle(StageStyle.TRANSPARENT);
        stageAuth.initModality(Modality.WINDOW_MODAL);
        stageAuth.setScene(new Scene(authDialogPanel));
    }
}