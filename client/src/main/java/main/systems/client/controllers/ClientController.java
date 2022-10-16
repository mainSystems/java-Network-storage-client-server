package main.systems.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.*;
import javafx.stage.Stage;
import main.systems.client.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import systems.common.Message;
import systems.common.StorageCommands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;
import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

public class ClientController {
    private static final String LOCALHOST = "localhost";
    private static final int PORT = 8189;
    private static final String SERVER_USER_DIR = "server/storage/";
    private static String userName;
    private static String itemName;

    @FXML
    private Label clientLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TreeView<String> treeView;

    private static final Logger logger = LogManager.getLogger(ClientController.class);

    public void handleDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void handleOnDragDetected(MouseEvent mouseEvent) {
//        Dragboard db = treeView.setOnDragDetected(TransferMode.ANY);
//        File fileContent = new File();

    }

    public void handleOnDragDropped(DragEvent dragEvent) {
        List<File> files = dragEvent.getDragboard().getFiles();

        for (File file : files) {
            try {
                Message message = new Message(StorageCommands.PUT, userName, file, Files.readAllBytes(file.toPath()));
                new Client(LOCALHOST, PORT).send(message, (response) -> {
                    logger.info("Sending file: " + file);
                });
            } catch (IOException e) {
                logger.error("File not found: " + file);
                e.printStackTrace();
            }
        }
        getTreeFiles();
    }


    public void getTreeFiles() {
        Path root = Path.of(SERVER_USER_DIR + userName);

        if (Files.exists(root)) {
            treeView.setRoot(getNodesForDirectory(new File(SERVER_USER_DIR + userName)));
        }
    }

    public TreeItem<String> getNodesForDirectory(File dir) {
        TreeItem<String> root = new TreeItem<String>(dir.getName());

        for (File f : Objects.requireNonNull(dir.listFiles())) {
            if (f.isDirectory()) //if dir than recursion
                root.getChildren().add(getNodesForDirectory(f));
            else //if file than get name
                root.getChildren().add(new TreeItem<String>(f.getName()));
        }
        return root;
    }

    public void setUserName(String userName) {
        ClientController.userName = userName;
    }

    public void setClientLabel(String username) {
        this.clientLabel.setText("NetworkStorage: " + username);
    }

    public void handleMouse(MouseEvent mouseEvent) {
        if (mouseEvent.getEventType().equals(MOUSE_CLICKED)) {
            switch (mouseEvent.getSource().getClass().getSimpleName()) {
                case "Label" -> {
                    Node source = (Node) mouseEvent.getSource();
                    Stage stage = (Stage) source.getScene().getWindow();
                    stage.close();
                }
                case "TreeView" -> {
                    TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
                    if (item != null) {
                        itemName = item.getValue();
                    }
                }
            }
        }
    }

    public void keyPressed(KeyEvent keyEvent) {
        String filePath = Path.of(SERVER_USER_DIR + userName + "\\" + itemName).toString();

        if (keyEvent.getEventType().equals(KEY_PRESSED)) {
            switch (keyEvent.getCode()) {
                case DELETE -> {
                    Message message = new Message(StorageCommands.DEL, filePath);
                    new Client(LOCALHOST, PORT).send(message, (response) -> {
                        logger.info("File to delete: " + filePath);
                    });
                }
            }
        }
        getTreeFiles();
    }
}