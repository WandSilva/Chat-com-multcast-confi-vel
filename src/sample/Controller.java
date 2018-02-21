package sample;

import comunication.Comunication;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TextArea onlineBox;

    @FXML
    private TextArea chatBox;

    @FXML
    private TextField msgBox;

    @FXML
    private TextField loginField;


    @FXML
    private PasswordField psField;

    @FXML
    private AnchorPane chatScreen;

    @FXML
    private AnchorPane loginScreen;


    Comunication comunication;
    String id;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comunication = Comunication.getInstance();
        comunication.iniciarGrupo();
        onlineBox.setEditable(false);
        atualizarTela();
    }

    public void atualizarTela() {
        Task t = new Task() {
            @Override
            protected Object call() throws Exception {
                while (true) {
                    Platform.runLater(() -> {
                        atualizarMsg();
                        showOnlineClients();
                    });
                    Thread.sleep(500);
                }
            }
        };
        new Thread(t).start();

    }

    private void atualizarMsg() {
        chatBox.setText(comunication.getConversation());
    }

    @FXML
    void send(ActionEvent event) {
        comunication.enviarMsg(id, msgBox.getText());
        msgBox.clear();
    }

    @FXML
    void send2(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            comunication.enviarMsg(id, msgBox.getText());
            msgBox.clear();
        }
    }

    public void showOnlineClients() {
        onlineBox.clear();
        ArrayList<String> clients = comunication.getClients();
        for (String c : clients)
            onlineBox.setText(onlineBox.getText() + c + "\n");
    }

    public void login() {
        
        boolean status = comunication.login(loginField.getText(), psField.getText());
        if (status){
            loginScreen.setVisible(false);
            chatScreen.setVisible(true);
            id = comunication.getMyId();
            comunication.notificarEntrada();

        }
        else
            JOptionPane.showMessageDialog(null, "Wrong login or password!", "ERROR", JOptionPane.ERROR_MESSAGE);

    }
}
