package chessecgm.model;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;

import java.io.Serializable;


public abstract class Piece {
    public static final String BLACK = "black";
    public static final String WHITE = "white";
    public static boolean onPassant;
 
    public static int rowToEat=-1;
    public static int colToEat=-1;


    protected ImageView image;
    protected String color;
    protected Boardpiece boardpieceOn;
    protected OnDragCompleteListener mListener;
    
    
    //funçaõ que destingue cor, tamanho e tipo das peças
    public Piece(String color) {
        this.color = color;
        String filePath = "chessecgm/designs/pieces/" + this.getColor() + "_" + this.getName() + ".png";
        this.image = new ImageView(filePath);
        this.image.setFitWidth(62.5);
        this.image.setFitHeight(62.5);
    }
    //retorna a imagem da peça
    public ImageView getImage() {
        return this.image;
    }
    //retorna a cor da peça
    public String getColor() {
        return this.color;
    }
    //função que premite arrastar a peça ao longo da board
    public void addImageDragEvent() {
        this.image.setOnDragDetected(new ImageDragDetectedEvent());// deteta que a imagem vaiser arrastada
        this.image.setOnDragDone(new ImageDragDoneEvent());// determina quadndo o drag acaba
    }
     // alteral o valor da variavel setOnDragCompleteListener/ Drag completo  que foi passado pelo parametro
    public void setOnDragCompleteListener(OnDragCompleteListener listener) {
        this.mListener = listener;
    }
    //retorna a peça que está no tabuleiro
    public Boardpiece getBoardpieceOn() {
        return boardpieceOn;
    }
    // alteral o valor da variavel setBoardpieceOn/ da peça que está no tabuleiro que foi passado pelo parametro
    public void setBoardpieceOn(Boardpiece boardpieceOn) {
        this.boardpieceOn = boardpieceOn;
    }
    //retorna o nome de cada uma da função das peças
    public abstract String getName();
    //se o movimento é legal
    public abstract boolean isLegalMove(ChessBoard b, int newRow, int newCol);
    //interface que teteta que o Drag esta a funcionar
    public interface OnDragCompleteListener {
        void onDragComplete();
    }
    //decteta quando a imagem é amarrada
    private class ImageDragDetectedEvent implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {//funçao de quando a amarra
            Dragboard db = image.startDragAndDrop(TransferMode.MOVE);
            Image dragShadow = image.getImage();//amarrar a imagem e chamala peça sombra
            db.setDragView(dragShadow, dragShadow.getWidth()/2, dragShadow.getHeight()/2 );//tamanho da peça sombra
            ClipboardContent content = new ClipboardContent();// string que contem cor, tipo da peça ,linha e coluna
            content.putString(getColor() + "_"
                    + getName() + "_"
                    + Integer.toString(boardpieceOn.getRow()) + "_"
                    + Integer.toString(boardpieceOn.getCol()));
            db.setContent(content);
            
            event.consume();
        }
    }
    //quando o drag está completo ou seja drop
    private class ImageDragDoneEvent implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            if (event.getTransferMode() == TransferMode.MOVE) {
                onPassant=false;
                mListener.onDragComplete();
           
            }
            event.consume();
        }
    }
}
