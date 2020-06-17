package chessecgm.model;

import chessecgm.controller.MultiplayerModeController;
import chessecgm.factory.PieceFactory;
import chessecgm.network.Client;
import java.util.Optional;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Boardpiece implements Piece.OnDragCompleteListener {
    public static final String DARK = "dark";
    public static final String LIGHT = "light";
    
    private Boardpiece[][] boardpieces ;
    private ChessBoard board;
    private Pane pane;
    private String color;
    private Piece piece;
    private int row, col;
    private Client client;
    public static boolean winyestrue=false;
    public static boolean pawnaskmessage=false;
    public static boolean iChangePiece =false;
    public static boolean iRemovePiece;
    //defenir tabuleiro
    public Boardpiece(ChessBoard board, int row, int col) {
        this.board = board;
        this.row = row; //Linha
        this.col = col; //coluna
        this.pane = new Pane();
        if ((row % 2 == 0 && col % 2 == 1) || (row % 2 == 1 && col % 2 == 0)) {
            this.color = DARK;
            this.pane.setStyle("-fx-background-color: #1389C8"); //azul fundo tabuleiro escuro
        } else {
            this.color = LIGHT;
            this.pane.setStyle("-fx-background-color: #F37723"); //laranja fundo tabuleiro claro
        }

        createDragDropEvent(); //criação do evento de Drag and Drop de peças
    }

    @Override
    public void onDragComplete() {
        //quando o evento de Drag andDrop está Completo este remove a pecça da posição anterior
        removePiece();
    }
    
    
    //Drag and Drop EVENT
    private void createDragDropEvent() {
        this.pane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                
                if (event.getGestureSource() != pane &&
                        event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }

                event.consume();
            }
        });

        this.pane.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                  Dragboard db = event.getDragboard();
                  boolean success = false; 
                //string base de dados com a info das peças linha_coluna
                if(db.hasString()) {
                    String[] pieceInfo = db.getString().split("_");
                    int oldRow = Integer.parseInt(pieceInfo[2]);
                    int oldCol = Integer.parseInt(pieceInfo[3]);
                    Piece newPiece = PieceFactory.getPiece(pieceInfo[1], pieceInfo[0]);
                    newPiece.setBoardpieceOn(board.getBoardpiece(oldRow, oldCol));
                    //caso o moviemnto seja legas 
                    if (newPiece.isLegalMove(board, row, col)) {
                   
                        success = true; //sucesso
                    }
                  
                }
                  
               
                //caso possa fazer drop da peça aparecerá
                if (success) {
                    pane.setStyle("-fx-background-color: #669438"); // verde para sucesso do Drop
                }else{
                    pane.setStyle("-fx-background-color: #b50b0b"); //Vermelho para não insucesso do Drop
                }
                
                
                event.consume();
            }
        });
        
        //Apos o Drag concluido
        this.pane.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            //as cores voltam ao normal após o drag concluido
            public void handle(DragEvent event) {
                if (color.equals(DARK)) {
                    pane.setStyle("-fx-background-color: #1389C8"); //azul fundo tabuleiro escuro
                } else {
                    pane.setStyle("-fx-background-color: #F37723"); //laranja fundo tabuleiro claro
                }

                event.consume();
            }
        });
        //Drag Dropped concluido
        this.pane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                 //string base de dados com a info das peças linha_coluna
                if(db.hasString()) {
                    
                    String[] pieceInfo = db.getString().split("_");
                    int oldRow = Integer.parseInt(pieceInfo[2]);
                    int oldCol = Integer.parseInt(pieceInfo[3]);
                    Piece newPiece = PieceFactory.getPiece(pieceInfo[1], pieceInfo[0]); //string que contem a info da peça nova coluna, nova linha
                    newPiece.setBoardpieceOn(board.getBoardpiece(oldRow, oldCol));// nova peça que altera o valor da peça antiga no no local especifico da peça no tabuleiro
                     //caso o moviemnto seja legas 
                    if (newPiece.isLegalMove(board, row, col)) {
                        
                        //caso hajo uma peça para ser comida no movimento legal
                        if (hasPiece()) {
                            
                            // e essa peça for um rei 
                            if(piece.getName()=="king"){//Xeque-Mate
                            winyestrue=true;//serve para não imprimir para o jogar que ganhou a mensagem a baixo
                            Client.Instance.winyes("Adversário ganhou o jogo!"); //envia uma mensagem para o client com a seguinte informação
                            Alert alert = new Alert(AlertType.CONFIRMATION);
                            alert.setTitle("Xeque-mate!");//alert pop up a avisar o vencedor
                            alert.setHeaderText("Parabéns ganhas-te o jogo conseguiste comer o Rei do teu adversário!");                           
                            alert.setContentText("Esperemos que voltes a jogar...");
                            ButtonType buttonTypeOne = new ButtonType("Sair");
                            ButtonType buttonTypeTwo = new ButtonType("Ok");
                            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
                            
                            Optional<ButtonType> result = alert.showAndWait();
                            
                            if (result.get() == buttonTypeOne){
                                
                                System.exit(0);       // quando o jogador carrega em Sair a aplicação dá shut down
                            }else {
                                System.exit(0); 
                                
                            } 
                            
                            }  else{  
                                removePiece();// caso a peça não seja um rei remove a peça normalmente
                            
                            }}
                        
                        if(Piece.onPassant==true){ //caso se verifique o enpassat
                        board.getBoardpiece(Piece.rowToEat, Piece.colToEat).removePiece();  //ele remove a peça no local 
                        iRemovePiece =true; // troca de informações no client para a remoção da peça
                        
                        board.setRemovePiecePawn(Piece.rowToEat, Piece.colToEat); //envia a informação de eliminara a peça para o outro jogador
                        board.setLastMove(new Move(oldRow, oldCol, row, col)); // ultimo movimento (antigalinha, antigacolua, linha, coluna)
                        success = true; //fim da jogada
                      
                        Piece.onPassant = false;
                        }
                    
                       
                        newPiece.addImageDragEvent(); // nova peça dropada com uma nova imagem
                        setPiece(newPiece); // nova peça
                        
                        board.setLastMove(new Move(oldRow, oldCol, row, col)); // ultimo movimento (antigalinha, antigacolua, linha, coluna)
                        success = true;// sucesso no drop
                        
                       
                    }
                    
              // caso os peão consigam chegar ao outro lado do tabuleiro row= 0 ou row=7 alert change Piece -> promoção de peão BRANCAS
              if(  getRow()==0 && piece.getName().equals("pawn") && piece.getColor().equals(Piece.WHITE)){ 
                          
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("PROMOÇÃO DO PEÃO A OUTRA PEÇA...");
                    alert.setHeaderText("Carregue no botão com a peça que deseja substituir...");
                    alert.setContentText("Escolha a sua peça!");
                    
                    ButtonType buttonTypeOne = new ButtonType("Bispo");
                    ButtonType buttonTypeTwo = new ButtonType("Torre");
                    ButtonType buttonTypeThree = new ButtonType("Rainha");
                    ButtonType buttonTypeFour = new ButtonType("Cavalo");
                    pawnaskmessage=true;//serve para não imprimir para o jogar que não trocou a peça a mensagem a baixo
                    Client.Instance.pawnask("Troca peão por outra peça."); //envia mensagem para o adversario

                    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree, buttonTypeFour);
                         
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == buttonTypeOne){
                       
                        removePiece(); //remove o peao
                        Piece newPieces = PieceFactory.getPiece("bishop", Piece.WHITE); // cria uma nova peça
                        newPieces.setBoardpieceOn(board.getBoardpiece(oldRow, oldCol));// nova peça que altera o valor da peça antiga no no local especifico da peça no tabuleiro
                        newPieces.addImageDragEvent();// nova peça dropada com uma nova imagem
                        
                        //mudança da peça para o adversário
                        iChangePiece=true; //mudança da peça
                        board.setNewPiece(row, col, "bishop#"+Piece.WHITE); // nova peça string (linha,coluna,peça,cor)
                        board.setLastMove(new Move(oldRow, oldCol, row, col));// ultimo movimento (antigalinha, antigacolua, linha, coluna)
                        newPieces.setBoardpieceOn(board.getBoardpiece(oldRow, oldCol));
                        success = true; //sucesso na conclusao da jogada
                        setPiece(newPieces); // nova peça
                    
                    } else if (result.get() == buttonTypeTwo) {
                        
                        removePiece();
                        Piece newPieces = PieceFactory.getPiece("rook", Piece.WHITE);
                        newPieces.setBoardpieceOn(board.getBoardpiece(oldRow, oldCol));
                        newPieces.addImageDragEvent();
                       
                        iChangePiece=true;
                        board.setNewPiece(row, col, "rook#"+Piece.WHITE);
                        board.setLastMove(new Move(oldRow, oldCol, row, col));
                        success = true;
                        setPiece(newPieces);
                    } else if (result.get() == buttonTypeThree) {
                        
                        removePiece();
                        Piece newPieces = PieceFactory.getPiece("queen", Piece.WHITE);
                        newPieces.setBoardpieceOn(board.getBoardpiece(oldRow, oldCol));
                        newPieces.addImageDragEvent();
                       
                        iChangePiece=true;
                        board.setNewPiece(row, col, "queen#"+Piece.WHITE);
                        board.setLastMove(new Move(oldRow, oldCol, row, col));
                        success = true;
                        setPiece(newPieces);
                    } else if (result.get() == buttonTypeFour){
                       
                        removePiece();
                        Piece newPieces = PieceFactory.getPiece("knight", Piece.WHITE);
                        newPieces.setBoardpieceOn(board.getBoardpiece(oldRow, oldCol));
                        newPieces.addImageDragEvent();
                       
                        iChangePiece=true;
                        board.setNewPiece(row, col, "knight#"+Piece.WHITE);
                        board.setLastMove(new Move(oldRow, oldCol, row, col));
                        success = true;
                        setPiece(newPieces);
                    }
                    // caso os peão consigam chegar ao outro lado do tabuleiro row= 0 ou row=7 alert change Piece -> promoção de peão     PRETAS
                   }else if(  getRow()==7 && piece.getName().equals("pawn") && piece.getColor().equals(Piece.BLACK)){ 
                          
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("PROMOÇÃO DO PEÃO A OUTRA PEÇA...");
                    alert.setHeaderText("Carregue no botão com a peça que deseja substituir...");
                    alert.setContentText("Escolha a sua peça!");

                    ButtonType buttonTypeOne = new ButtonType("Bispo");
                    ButtonType buttonTypeTwo = new ButtonType("Torre");
                    ButtonType buttonTypeThree = new ButtonType("Rainha");
                    ButtonType buttonTypeFour = new ButtonType("Cavalo");
                    pawnaskmessage=true;//serve para não imprimir para o jogar que não trocou a peça a mensagem a baixo
                    Client.Instance.pawnask("Troca peão por outra peça.");

                    alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree, buttonTypeFour);
                         
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == buttonTypeOne){
                       
                       removePiece();
                       Piece newPieces = PieceFactory.getPiece("bishop", Piece.BLACK);
                       newPieces.setBoardpieceOn(board.getBoardpiece(oldRow, oldCol));
                       newPieces.addImageDragEvent();
                       
                       iChangePiece=true;
                       board.setNewPiece(row, col, "bishop#"+Piece.BLACK);
                       board.setLastMove(new Move(oldRow, oldCol, row, col));
                       success = true;
                      setPiece(newPieces);
                        
                    } else if (result.get() == buttonTypeTwo) {
                      
                        removePiece();
                        Piece newPieces = PieceFactory.getPiece("rook", Piece.BLACK);
                        newPieces.setBoardpieceOn(board.getBoardpiece(oldRow, oldCol));
                        newPieces.addImageDragEvent();
                       
                        iChangePiece=true;
                        board.setNewPiece(row, col, "rook#"+Piece.BLACK);
                        board.setLastMove(new Move(oldRow, oldCol, row, col));
                        success = true;
                        setPiece(newPieces);
                    } else if (result.get() == buttonTypeThree) {
                       
                        removePiece();
                        Piece newPieces = PieceFactory.getPiece("queen", Piece.BLACK);
                        newPieces.setBoardpieceOn(board.getBoardpiece(oldRow, oldCol));
                        newPieces.addImageDragEvent();
                        
                        iChangePiece=true;
                        board.setNewPiece(row, col, "queen#"+Piece.BLACK);
                        board.setLastMove(new Move(oldRow, oldCol, row, col));
                        success = true;
                        setPiece(newPieces);
                    } else if (result.get() == buttonTypeFour){
                      
                        removePiece();
                        Piece newPieces = PieceFactory.getPiece("knight", Piece.BLACK);
                        newPieces.setBoardpieceOn(board.getBoardpiece(oldRow, oldCol));
                        newPieces.addImageDragEvent();
                        
                        iChangePiece=true;
                        board.setNewPiece(row, col, "knight#"+Piece.BLACK);
                        board.setLastMove(new Move(oldRow, oldCol, row, col));
                        success = true;
                        setPiece(newPieces);
                    }
                  
                        }        
                            
                
                }
                //EVENTO DROP CONCLUIDO COM SUCESSO APOS TODAS AS VERIFICAÇÕES
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }

    //função que retorna se existe alguma peça numa determina casa
    public boolean hasPiece() {
      
        return (this.piece != null);
    }
    
    
    public boolean hasPiecePawnBlack() { //VERIFICA QUE A PEÇA É UM PEÃO PRETO
        if (piece.getName().equals("pawn") && piece.getColor().equals(Piece.BLACK)) {
            return (this.piece != null); 
        }else{
           return (this.piece == null); 
        }}
    
    public boolean hasPiecePawnWhite() { //VERIFICA QUE A PEÇA É UM PEÃO BRANCA
        if (piece.getName().equals("pawn") && piece.getColor().equals(Piece.WHITE)) {
            return (this.piece != null); 
        }else{
            return (this.piece == null);
        }}    
    
    
    //vais buscar uma peça especifica
    public Piece getPiece() {
        return this.piece;
    }

    // alteral o valor da variavel peça pelo que foi passado pelo parametro
    public void setPiece(Piece piece) {
        this.piece = piece; 
        this.piece.setBoardpieceOn(this);
        this.pane.getChildren().add(piece.getImage());
        this.piece.setOnDragCompleteListener(this);//drag completo
    }
    //variavel igual ao setPiece a unica diferença é que serve para mudar os peões
    public void changePiece(Piece piece){
        this.piece = piece;
        this.piece.setBoardpieceOn(this);
        this.pane.getChildren().add(piece.getImage());
        this.piece.setOnDragCompleteListener(this);//drag completo
    }
    //remove uma peça selecionada
    public void removePiece() {
        this.pane.getChildren().remove(this.piece.getImage());
        this.piece = null;
      
    }
    //retorna o valor da variavel tabuleiro
    public Pane getPane() {
        return this.pane;
    }
    //retorna o valor da variavel linha
    public int getRow() {
        return row;
    }
    // retorna o valor da variavel coluna
    public int getCol() {
        return col;
    }
}
