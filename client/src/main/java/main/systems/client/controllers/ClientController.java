package main.systems.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
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

public class ClientController {
    private static final String LOCALHOST = "localhost";
    private static final int PORT = 8189;
    private static final String SERVER_USER_DIR = "server/storage/";
    private static String userName;

    @FXML
    private Label clientLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TreeView<String> treeView;

    private static final Logger logger = LogManager.getLogger(ClientController.class);

    @FXML
    public void handleDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void handleDropped(DragEvent dragEvent) {
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
    }

    public void handleMouseClicked(MouseEvent mouseEvent) {
        Node source = (Node) mouseEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
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
}