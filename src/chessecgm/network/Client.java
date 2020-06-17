package chessecgm.network;

import chessecgm.controller.MultiplayerModeController;
import static chessecgm.controller.MultiplayerModeController.wantnodraw;
import chessecgm.controller.LobbyController;
import chessecgm.model.Boardpiece;
import static chessecgm.model.Boardpiece.winyestrue;
import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;



public class Client {
    private String ip;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private PrintWriter givupp;
    private PrintWriter draww;
    private PrintWriter winmessages;
    private PrintWriter pawnasks;
    public static Client Instance;
   
    private DataReceiveListener mListener;

    //comunicação com o servidor
    public Client(String ip) {
       
        Instance= this;
        this.ip = ip;
        try {
            socket = new Socket(ip, 5000); //ip e porta predefenida
            InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(isReader);
            writer = new PrintWriter(socket.getOutputStream());
            givupp = new PrintWriter(socket.getOutputStream());
            draww = new PrintWriter(socket.getOutputStream());
            winmessages = new PrintWriter(socket.getOutputStream());
            pawnasks = new PrintWriter(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //receção de mensagens pelo servidor
    public void setOnDataReceiveListener(DataReceiveListener listener) {
        this.mListener = listener;
    }
    // envia mensagens ENTRE JOGADORES
    public void sendMessage(String message) {
        writer.println(message);
        writer.flush();
      
    }
    //recebe mensagens entre jogadores
    public String receiveMessage() {
        String result = null;
     
        try {
            result = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void startDataThread() {
     
        Thread t = new Thread(new IncomingDataReader());
        t.start();
    }
    // mensagem de envio de encerramento de sessao / desistir
    public void closeConnection(String messages) {
        givupp.println(messages);
        givupp.flush();
        
        try {
            socket.close();
              
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //mensagens de empate
      public void draw(String messagess) {
         draww.println(messagess);
        draww.flush();
    
    }
   //mensagens de vitoria
        public void winyes(String winmessage) {
         winmessages.println(winmessage);
        winmessages.flush();
     
    }
    //mensagens de troca de peao por outra peça
         public void pawnask(String pawnmessage) {
         pawnasks.println(pawnmessage);
        pawnasks.flush();
     
    }
   //esta classe lê todas as mensagens enviadas e e define diferentes tipos de feedbacks para cada uma delas , e dá o feedback entre ambos os jogadores     
   //Leitor de dados recebidos      
    private class IncomingDataReader implements  Runnable {
        
        @Override
        public void run() {
            String message;
             //quando estas mensagens são enviadas entre jogadores acontece o seguinte:
            while ((message = receiveMessage()) != null) {
              
                final String s = message;
                if (message.contains("_")) {// movimento de peças
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("recebi movimento");
                            mListener.onMoveReceive(s);
                            
                        }
                    });
                } else if(message.contains("desistiu!")){//
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {//desistiu
                            mListener.onGiveUpReceive(s);
                        
                        }
                    });
                
                }else if(message.contains("?-")){//trocar peça peão por outra peça
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(s);
                            if(!Boardpiece.iChangePiece){
                                mListener.onChangePieceReceive(s);
                            }
                             Boardpiece.iChangePiece=false;
                        }
                    }); 
                }else if(message.contains("!-")){//remoção peão enpassant
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(s);
                            if(!Boardpiece.iRemovePiece){
                                mListener.onRemovePieceReceive(s);
                               
                            }
                             Boardpiece.iRemovePiece=false;
                        }
                    });
                }else if(message.contains("Pedido de Empate")){ //pedido de empate
                  if(MultiplayerModeController.iwantdraw==true){
                    MultiplayerModeController.iwantdraw=false;//para enviar uma mensagem ao adversário e outra ao jogador a pedir empate
                    }else{
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                               mListener.onDrawReceive(s);
                        }
                    });}
                }else if(message.contains("aceitou o Empate!")){//aceita empate
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                           
                            mListener.onyesdrawReceive(s);
                        }
                    });
                }else if(message.contains("REJEITADO!")){//pedido de empate rejeitado
                  if(MultiplayerModeController.wantnodraw==true){
                    MultiplayerModeController.wantnodraw=false;//para enviar uma mensagem ao adversário e outra ao jogador a pedir empate
                    }else{
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            
                            mListener.onnodrawReceive(s);
                        }
                    });}
                }else if(message.contains("Adversário ganhou o jogo!")){//vitoria
                  if(Boardpiece.winyestrue==true){
                    Boardpiece.winyestrue=false;//para enviar uma mensagem ao adversário e outra ao jogador
                    }else{
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            
                            mListener.onwinReceive(s);
                        }
                    });}
                }else if(message.contains("Desculpe! Servidor Caiu!")){//vitoria
                 
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            
                            mListener.onGiveUpReceive(s);
                        }
                    });
                }
                
                else if(message.contains("Troca peão por outra peça.")){ //troca peão por outra peça
                    if(Boardpiece.pawnaskmessage==true){
                    Boardpiece.pawnaskmessage=false;//para enviar uma mensagem ao adversário e outra ao jogador
                    }else{
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                         
                            mListener.onPawnAskReceive(s);
                        }
                    });}
                }else{
                     mListener.onChatReceive(s);
                }
            
                }
        }
    }
    //Interface de Rececao de dados
    public interface DataReceiveListener {
        void onChatReceive(String message);
        void onMoveReceive(String move);
        void onGiveUpReceive(String messages);
        void onDrawReceive(String messagess);
        void onyesdrawReceive(String messagess);
        void onnodrawReceive(String messagessss);
        void onwinReceive(String winmessage);
        void onPawnAskReceive(String pawnmessage);
        void onChangePieceReceive(String changemessage);
        void onRemovePieceReceive(String changemessage);
    }
}
