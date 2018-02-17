package sample;

import comunication.Comunication;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TextArea onlineBox;

    @FXML
    private TextArea chatBox;

    @FXML
    private TextField msgBox;
    @FXML
    private Button btnSend;


    Comunication comunication;
    String id;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        id = JOptionPane.showInputDialog("Informe seu ID");
        comunication = new Comunication(id);
        comunication.iniciarGrupo();
        comunication.notificarEntrada();
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
        String[] aux = comunication.getClients().split(",");
        for (int i=1; i<aux.length;i++)
            onlineBox.setText(onlineBox.getText()+aux[i]+"\n");
    }
}
