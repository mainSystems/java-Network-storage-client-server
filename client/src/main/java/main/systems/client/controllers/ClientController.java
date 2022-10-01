package main.systems.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import main.systems.client.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import systems.common.Message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ClientController {
    private static final String LOCALHOST = "localhost";
    private static final int PORT = 8189;
    private static final String PUT = "put";
    @FXML
    private Label welcomeText;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TreeView treeView;

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
                Message message = new Message(PUT, file, Files.readAllBytes(file.toPath()));
                new Client(LOCALHOST, PORT).send(message, (response) -> {
                    logger.info("Sending file: " + file);
                });
            } catch (IOException e) {
                logger.error("File not found: " + file);
                e.printStackTrace();
            }
        }
    }
}