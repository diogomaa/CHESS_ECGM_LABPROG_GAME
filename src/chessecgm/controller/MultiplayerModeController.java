package chessecgm.controller;

import chessecgm.Main;
import chessecgm.factory.PieceFactory;
import chessecgm.model.ChessBoard;
import chessecgm.model.Move;
import chessecgm.model.Piece;
import chessecgm.network.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.w3c.dom.NodeList;

public class MultiplayerModeController implements Initializable, Client.DataReceiveListener, ChessBoard.OnPieceMoveListener{
    @FXML
    private Label nameP2;

    @FXML
    private Label lastMoveP2;

    @FXML
    private Label nameP1;

    @FXML
    private Label lastMoveP1;
    
    @FXML
    private Label infocurrent;

    @FXML
    private GridPane chessPane;
    
    @FXML
    private Label lblChessTimer;

    @FXML
    private TextArea chatBox;

    @FXML
    private TextField textInput;
    
    @FXML
    public Button closeButton;
    
    @FXML
    public Button yesdraw;
     
    @FXML
    public Button nodraw;
        
    @FXML
    public Button drawask;
    
    @FXML
    public Button exitbotton;
      
    @FXML
    public SplitPane drawsplit;


    @FXML
    private Button btnSend;
    

    private Client client;
    private ChessBoard chessBoard;
    private String playerId;
    private String playerName;
    private String rivalName;
    public static boolean iwantdraw=false;  
    public static boolean wantnodraw=false;
    public static boolean online=false;
    int countdown= 240;

    Timer timer = new Timer();
    TimerTask task = new TimerTask(){
    
        
    //timer impressões na tela
    public void run()
       {
            if(countdown >0)
            countdown--;  
            
            if(countdown > 8){
            Platform.runLater(() ->lblChessTimer.setText("Falta: " + countdown+ " (seg)"));}
            else{
            Platform.runLater(() ->lblChessTimer.setText("Atenção! Falta: " + countdown+ " (seg)"));
            }
            if(countdown == 0){
            Platform.runLater(() ->lblChessTimer.setText("À espera de jogada!"));
            }
        }
    
    };
    
    //comunicação com o client , o outro jogador
    public MultiplayerModeController(Client connection, String playerId, String playerName, String rivalName) {
        this.client = connection;
        this.playerId = playerId;
        this.playerName = playerName;
        this.rivalName = rivalName;
        
        client.setOnDataReceiveListener(this);
        client.startDataThread();
        
        chessBoard = new ChessBoard(playerId);
        chessBoard.setOnPieceMoveListener(this);
     
        timer.scheduleAtFixedRate(task,1000,1000);
    }

    
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/main_screen.fxml"));
        Parent root = (Parent) loader.load();        
        primaryStage.setResizable(false);


        primaryStage.initStyle(StageStyle.DECORATED);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    
    
    
}
    
    @Override
    //inicia o main jogo
    public void initialize(URL location, ResourceBundle resources) {
        //cabeçalho
        setStageTitle("Jogo Xadrez - Jogador: " + playerName +"  --> Id: "+ playerId);
        //desenha o tabuleiro
        drawChessPane();
        //mostra os nomes
        displayPlayerName();
        //alerta para enviar mensagem de desistir só quando está online
        online=true;
 
      //quando o jogo começa as pretas não podem se mexer
        if (playerId.equals("2"))   
            chessPane.setDisable(true);
          lblChessTimer.setText("Falta: 240");
    }

     //receber mensagens no chat
    @Override
    public void onChatReceive(String message) {
        chatBox.appendText(message + "\n");
    }
 
     
     //botão de sair
     public void onButtonleave(ActionEvent event) {
     
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        
    }
    // botão desistir 
      @FXML
    public void onButtongivup(ActionEvent event) {
     
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        client.closeConnection(playerName + " desistiu! " );
    }
  //informação recebida quando alguem desite   
    public void onGiveUpReceive(String messages) {
        chessPane.setDisable(true);
        infocurrent.setText(messages );
        yesdraw.setVisible(false);
        nodraw.setVisible(false);
        drawask.setVisible(false);  
        closeButton.setVisible(false);
        lblChessTimer.setVisible(false);
        exitbotton.setVisible(true);
        btnSend.setDisable(true);
        textInput.setDisable(true);   
        
        
      }

  
      
     // botão de empate
    public void onButtondraw(ActionEvent event) {
         iwantdraw=true;
         client.draw("Pedido de Empate de "+ playerName+ "!!" );
         infocurrent.setText(" Pedido de Empate. Aguarde!!" );
         chessPane.setDisable(true);
         drawask.setVisible(false);  
         closeButton.setVisible(false);
         exitbotton.setVisible(false);
      
        
    }
  
     //rececão informação de empate 
    public void onDrawReceive(String messagess) {
      
         chessPane.setDisable(true);
         infocurrent.setText(messagess );
         yesdraw.setVisible(true);
         nodraw.setVisible(true);
         drawask.setVisible(false);  
         closeButton.setVisible(false);
         exitbotton.setVisible(false);
     
        
        
    }  

    //botão de negar empate
 public void onButtonnodraw(ActionEvent event) {
      
         chessPane.setDisable(false);
         wantnodraw=true;
         yesdraw.setVisible(false);
         nodraw.setVisible(false);
         drawask.setVisible(false); 
         closeButton.setVisible(true);
         exitbotton.setVisible(false);
         
         
      if (playerId.equals("1")) {
        
           infocurrent.setText("É a sua vez " +playerName+"!");
           client.draw("REJEITADO! É a sua vez " +playerName+"!" );
           
        } else if (playerId.equals("2")) {
      
           infocurrent.setText("É a sua vez " +playerName+"!");
           client.draw("REJEITADO! É a sua vez " +playerName+"!" );
        }
        
    }     
 
    // botão de receção de negação de empate
    public void onnodrawReceive(String messagessss) {
      
         chessPane.setDisable(true);
         infocurrent.setText(messagessss );
         nodraw.setVisible(false);
         drawask.setVisible(true); 
         closeButton.setVisible(true);
         exitbotton.setVisible(false);
           
        
    }  
 
    //botão aceitar empate
    public void onButtonyesdraw(ActionEvent event) {
        
         chessPane.setDisable(true);
         yesdraw.setVisible(false);
         nodraw.setVisible(false);
         drawask.setVisible(false);  
         closeButton.setVisible(false);
         exitbotton.setVisible(true);
         btnSend.setDisable(true);
         textInput.setDisable(true); 
         lblChessTimer.setVisible(false);
         client.draw(playerName + " aceitou o Empate!" );
     
    }   
    
    //rececão de aceitar empate
    public void onyesdrawReceive(String messagess) {
   
         chessPane.setDisable(true);
         infocurrent.setText( messagess );
         yesdraw.setVisible(false);
         nodraw.setVisible(false);
         drawask.setVisible(false);  
         closeButton.setVisible(false);
         exitbotton.setVisible(true);
         btnSend.setDisable(true);
         textInput.setDisable(true); 
         lblChessTimer.setVisible(false);
        
        
    }  
    
   //receção de quando alguem faz xeque-mate       
    public void onwinReceive(String winmessage) {
   
         lblChessTimer.setVisible(false);
         chessPane.setDisable(true);
         infocurrent.setText( winmessage );
         yesdraw.setVisible(false);
         nodraw.setVisible(false);
         drawask.setVisible(false);  
         closeButton.setVisible(false);
         exitbotton.setVisible(true);
         btnSend.setDisable(true);
         
        
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Xeque-mate!");//alert pop up a avisar o vencedor
        alert.setTitle("Xeque-mate!Adversário ganhou o jogo!");//alert pop up a avisar o vencedor
        alert.setHeaderText("Desta vez perdeste o jogo, Tenta outra vez!");        
        ButtonType buttonTypeOne = new ButtonType("Sair");
        ButtonType buttonTypeTwo = new ButtonType("Ok");
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
                            
        Optional<ButtonType> result = alert.showAndWait();
                            
        if (result.get() == buttonTypeOne){
                                
        System.exit(0);       // quando o jogador carrega em Sair a aplicação dá shut down
        }else {
        System.exit(0); 
                                
        }
        
    }  
    
    
    //receção da informação que o adversário vai trocar o peão por alguma peça  
     public void onPawnAskReceive(String pawnmessage) {

         infocurrent.setText( pawnmessage );
         yesdraw.setVisible(false);
         nodraw.setVisible(false);
        
      
    }  
      
 
   //receção de movimento de peça pelo adversário  
    @Override
    public void onMoveReceive(String move) {
        chessPane.setDisable(false);
        Move m = new Move(move);
        yesdraw.setVisible(false);
        nodraw.setVisible(false);
        drawask.setVisible(false); 
       
        if (playerId.equals("1")) {
          //histórico da ultima peça do jogador atualizado para o adversário
          lastMoveP2.setText(m.toBoardMove());
          //atualizzação do status de quem joga para o rival
          infocurrent.setText("É a sua vez " +playerName+"!");
             
             
        } else if (playerId.equals("2")) {
          //histórico da ultima peça do jogador atualizado para o adversário   
          lastMoveP1.setText(m.toBoardMove());
          //atualizzação do status de quem joga para o rival
          infocurrent.setText("É a sua vez " +playerName+"!");
        }
        
    
        chessBoard.updateBoard(m);
        countdown = 240;
    }
    //
    @Override
    public void onPieceMove(Move move) {
        chessPane.setDisable(true);
        yesdraw.setVisible(false);
        nodraw.setVisible(false);
        drawask.setVisible(true);  
        
        //envia a posição da peça para o Client
        client.sendMessage(playerId + "_" + move.toString());
        
        if (playerId.equals("1")) {
            //histórico da ultima peça do jogador atualizado para o jogador   
            lastMoveP1.setText(move.toBoardMove());
            //atualizzação do status de quem joga para o jogador a jogar
            infocurrent.setText("É a sua vez " +rivalName+"!");
        } else if (playerId.equals("2")) {
             //histórico da ultima peça do jogador atualizado para o jogador   
            lastMoveP2.setText(move.toBoardMove());
            //atualizzação do status de quem joga para o jogador a jogar
            infocurrent.setText("É a sua vez " +rivalName+"!");
        }
        
         countdown = 240;
    }
    
    //envio da substituição do peão por outra peça  na promoção do mesmo
    @Override
    public void onPieceChange(int row, int col, String piece){
        client.sendMessage("?-"+row+"-"+col+"-"+piece); //envia uma mensage para o adversario com a seguinte informação ?-linha-coluna-piece.cor
    }
    //receção da substituição do peão por outra peça  na promoção do mesmo pelo adversário
    @Override
    public void onChangePieceReceive(String changemessage){
        String[] s = changemessage.split("-"); //desfaz a string em partes
        int col = Integer.parseInt(s[1]); //linha
        int row = Integer.parseInt(s[2]); //coluna
        String[] t = s[3].split("#");//peça trocada começa por #peca.cor
        Piece piece = PieceFactory.getPiece(t[0], t[1]);//nova peça criada na fabrica de peças
        chessBoard.changePiece(col,row,piece);
    }
    //envio da remoção do peão enpassant
    public void onPieceRemove(int row, int col){
        client.sendMessage("!-"+row+"-"+col);
    }
     //receção da remoção do peão enpassant envio paraoadversario
       @Override
    public void onRemovePieceReceive(String changemessage){
       
        String[] s = changemessage.split("-");
        int col = Integer.parseInt(s[1]);
        int row = Integer.parseInt(s[2]);
        chessBoard.removePiece(col,row);
    }
   
    @FXML
    //envio de mensagens quando carregado no botão       
    void onButtonSendClick(ActionEvent event) {
        client.sendMessage(playerName + ": " + textInput.getText());
        textInput.clear();
    }

    @FXML
    //quando carrega na caixa de texto        
    void onTextInputPress(KeyEvent event) {
        // caso carregue na tecla ENTER
        if (event.getCode() == KeyCode.ENTER) {
            client.sendMessage(playerName + ": " + textInput.getText());
            textInput.clear();
        }
    }
    //desenho do tabuleiro de xadrez
    private void drawChessPane() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                chessPane.add(chessBoard.getBoardpiece(row, col).getPane(), col, row);
            }
        }
    }
    
    //mostrar os nome dos jogadores no painel quando inicia
    private void displayPlayerName() {
        if (playerId.equals("1")) {
           //mostra quem começa a jogar quando o jogo começa
            infocurrent.setText("É a sua vez " +playerName+"!");
            nameP1.setText(playerName);
            nameP2.setText(rivalName);
     
        } else {
            //mostra quem começa a jogar quando o jogo começa
            infocurrent.setText("É a sua vez " +rivalName+"!");
            nameP1.setText(rivalName);
            nameP2.setText(playerName);
           
        }
    }

    private void setStageTitle(String title) {
        Main.getStage().setTitle(title);
    }
}
