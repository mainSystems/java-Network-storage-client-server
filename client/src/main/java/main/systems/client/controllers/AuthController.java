package main.systems.client.controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.systems.client.Client;
import main.systems.client.ClientApplication;
import main.systems.client.Dialogs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import systems.common.Message;
import systems.common.SqlCommands;

import java.io.IOException;


public class AuthController {

    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Button authButton;
    @FXML
    public Button regButton;

    private static final String LOCALHOST = "localhost";
    private static final int PORT = 8189;
    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @FXML
    public void executeAuth() throws IOException {
        String login = loginField.getText();

        //---------transfer field between controllers
        ClientController clientController = ClientApplication.getINSTANCE().getClientController();
        clientController.setUserName(login);
        //-------------------------------------------

        String password = passwordField.getText();
        if (checkCredentials()) {
            Message message = new Message(SqlCommands.SELECT_USERNAME_AUTH, login, password);
            new Client(LOCALHOST, PORT).send(message, (response) -> {
                logger.info("auth command: " + SqlCommands.SELECT_USERNAME_AUTH);
            });
        }
    }

    @FXML
    public void executeReg() {
        String login = loginField.getText();
        String password = passwordField.getText();
        if (checkCredentials()) {
            Message message = new Message(SqlCommands.INSERT, login, password);
            new Client(LOCALHOST, PORT).send(message, (response) -> {
                logger.info("reg command: " + SqlCommands.INSERT);
            });
        }
    }

    private boolean checkCredentials() {
        String login = loginField.getText();
        String password = passwordField.getText();
        boolean check = true;

        if (login == null || password == null || login.isBlank() || password.isBlank()) {
            check = false;
            Dialogs.AuthError.EMPTY_CREDENTIALS.show();
            logger.error(Dialogs.AuthError.EMPTY_CREDENTIALS);
        }

        return check;
    }
}
