package chessecgm;


import chessecgm.controller.MultiplayerModeController;
import chessecgm.network.Client;
import java.awt.Toolkit;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


public class Main extends Application {
    private static Stage stage;
     private Client client;
     private MultiplayerModeController multiplayermodecontroller; 
   
    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("view/lobby_screen.fxml"));
        primaryStage.setTitle("Jogo Xadrez");
        primaryStage.setScene(new Scene(root));
        
        primaryStage.setResizable(false);
        stage.initStyle(StageStyle.DECORATED);
      // caso deseje sair do jogo pelo X boton 
      
     
       primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
    @Override
    public void handle(WindowEvent event) {
        System.out.println("hidding");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Sair do Jogo!");
        alert.setHeaderText("Deseja mesmo fechar o jogo??");
        alert.setContentText("Se sim carregue em SIM, caso pretenda continuar NÃO...Tambêm podes usar os butões Sair ou Desistir...");            
        Toolkit.getDefaultToolkit().beep();
  
        
          
        ButtonType buttonTypeOne = new ButtonType("SIM");
        ButtonType buttonTypeTwo = new ButtonType("NÃO");
 
                    

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
                         
        Optional<ButtonType> result = alert.showAndWait();
                    
        if ( result.get() == buttonTypeOne ) {
         
         //desistir caso esteja em jogo
          if(MultiplayerModeController.online==true){
               Client.Instance.closeConnection(" Adversário desistiu!");
               Platform.exit();
               System.exit(0);
          }else{//caso ainda esteja no lobby
               Platform.exit();
               System.exit(0);     
          }
                
            
           
              
        }else if (result.get() == buttonTypeTwo){
      
        }
         event.consume();
    }
});
        primaryStage.show();
    }

    
    public static void main(String[] args) {
        launch(args);
    }
    
    

    public static Stage getStage() {
        return stage;
    }
}
