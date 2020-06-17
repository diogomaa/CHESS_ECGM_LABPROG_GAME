package chessecgm.controller;

import chessecgm.Main;

import chessecgm.network.Client;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LobbyController implements Initializable {

    @FXML
    private TextField textUserName;

    @FXML
    private RadioButton GameIPMultiplayer;

    @FXML
    private TextField textIp;

    @FXML
    private Button btnStart;
    
    @FXML
    private Button btnClose;

    private ToggleGroup gameMode;

    
 
    @Override
    //iniciar
    public void initialize(URL location, ResourceBundle resources) {
        setupGameModeMiltiP();// escolha do modo de jogo já predefenido num unico toogle
        btnStart.setDisable(true);// botao start true
    }

    @FXML
    //botao Iniciar Partida
    void onButtonStartClick(ActionEvent event) throws IOException {
        
        if (isValidToStart()) {//inicia o MultiplayerModeController
                Client client = new Client(textIp.getText());
                //envia usernames
                client.sendMessage(textUserName.getText());
                //conexão estabelecida
                establishConnection(client);
            } 
       btnStart.setDisable(true);
        
    }
    
     @FXML
    //botão SAIR        
    void onButtonClose(ActionEvent event) throws IOException {
         Platform.exit();
         System.exit(0);    
    }

    @FXML
    //introdução do IP na boxtext        
    void onTextIpPress(KeyEvent event) {
        if (isValidToStart()) {
            if (event.getCode() == KeyCode.ENTER) { //caso carregue enter inicia  o MultiplayerModeController
                Client client = new Client(textIp.getText()); //ip dos jogadores
                //envia usernames
                client.sendMessage(textUserName.getText());
                //conexão estabelecida
                establishConnection(client);
            }
        }else{
            //sem IP não deixa começar
                btnStart.setDisable(false);
            }
    }
    //seleção do toggle, visto que só existe um modo de jogo está predefenidamente selecionado
    private void setupGameModeMiltiP() {
        gameMode = new ToggleGroup();
        GameIPMultiplayer.setToggleGroup(gameMode);
        GameIPMultiplayer.setSelected(true);
      
        gameMode.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (gameMode.getSelectedToggle() == GameIPMultiplayer) {
                    textIp.setDisable(false);
                } 
            }
        });
    }
    //validação de iniciar 
    public boolean isValidToStart() {
          
        //ter username
        if (!textUserName.getText().equals("")) {
            //seleção do modo de jogo multiplayer
            if (gameMode.getSelectedToggle() == GameIPMultiplayer) {
                //envio do IP
                return (!textIp.getText().equals(""));
            }
            return true;
        }
        return false;
    }

    //iniciar o Toggle GameIPMultiplayer 
    private void startGameIPMultiplayer(Client connection, String playerId, String playerName, String rivalName) {
        try {
            //inicir o jogo
            MultiplayerModeController MultiplayerModeController = new MultiplayerModeController(connection, playerId, playerName, rivalName);
            //envia para uma nova pagina 
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/main_screen.fxml"));
            //iniciar toda a nova cena
            Stage stage = (Stage) btnStart.getScene().getWindow();
            loader.setController(MultiplayerModeController);
            Parent root = loader.load();
            stage.getScene().setRoot(root);
            stage.show();
            
            //barrar recize
            stage.sizeToScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //conexão estabelecida entre jogadores
    private void establishConnection(Client client) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //string com indormação enviada por cada jogador separadas por _ ou seja Client_playerID_playerName_RivalName
                String[] info = client.receiveMessage().split("_");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        startGameIPMultiplayer(client, info[0], info[1], info[2]);
                    }
                });
            }
        });
        t.start();
    }

 
}
