package chessecgm.model;

import chessecgm.factory.PieceFactory;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javax.swing.SwingUtilities;

public class ChessBoard {
    

    private Piece piece;  
    private Boardpiece[][] boardpieces ;
    private Move lastMove;
    private String playerId;
 
    private OnPieceMoveListener mListener = null;

    public ChessBoard(String playerId) { 
        boardpieces = new Boardpiece[8][8];//string com todos os quadradinhos 8*8
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boardpieces[row][col] = new Boardpiece(this, row, col);
                setInitPieces(row, col);
            }
        }
        this.playerId = playerId; // id do jogador
        addDragEvent(playerId); //evento de drag co playerID
    }

    public ChessBoard(ChessBoard board) {
        boardpieces = new Boardpiece[8][8];//string com todos os quadradinhos 8*8
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boardpieces[row][col] = new Boardpiece(this, row, col);
                if (board.hasPiece(row, col)) {
                    Piece piece = board.getPiece(row, col);
                    boardpieces[row][col].setPiece(PieceFactory.getPiece(piece.getName(), piece.getColor()));
                }
            }
        }
    }

    
   //retorna o valor da variavel todos os quadrados do tabuleiro
    public Boardpiece[][] getBoardpieces() {
        return this.boardpieces;
    }
    //retorna um e eum só quadrado do tabuleiro
    public Boardpiece getBoardpiece(int row, int col) {
        return this.boardpieces[row][col]; 
    }
     // alteral o valor da variavel setLasMove/ultimo moviemnto pelo que foi passado pelo parametro
    public void setLastMove(Move move) {
        this.lastMove = move;
        mListener.onPieceMove(move);
    }
     // alteral o valor da variavel setNewPiece/nova peça pelo que foi passado pelo parametro
    public void setNewPiece(int row, int col,String piece) {
        mListener.onPieceChange(row,col,piece); //TROCA PEÃO POR PEÇA
    }
    // alteral o valor da variavel setRemovePiecePawn/nremoção do peão enpassant pelo que foi passado pelo parametro
    public void setRemovePiecePawn(int row, int col) {
        mListener.onPieceRemove(row,col);// REMOÇÃO PEÃO EN PASSANT
    }
    //muda  a peça do jogador que recebeu essa informação
    public void changePiece(int col, int row, Piece piece){
        System.out.println(piece);
        if (hasPiece(col, row)) {// se caso no monimento tiver uma peça
            getBoardpiece(col, row).removePiece();// remove a peça 
        }
        getBoardpiece(col,row).changePiece(piece);//alteral o valor da variavel e indica a nova posição e o novo tipo de peça que foi passado pelo parametro 

    }
    //remove a peça do jogador que recebeu essa informação
    public void removePiece(int col, int row){
     
    getBoardpiece(col, row).removePiece();
       
    }
    //inicia as peças todas nas suas posições por tipo de peça e cor
    private void setInitPieces(int row, int col) {
        String pieceName = "";
        String pieceColor = "";

        if (row == 1) { //linha todos peões pretos
            pieceName = "pawn";
            pieceColor = Piece.BLACK;
            boardpieces[row][col].setPiece(PieceFactory.getPiece(pieceName, pieceColor));
        } else if (row == 6) {//linha todos os peãos brancos
            pieceName = "pawn";
            pieceColor = Piece.WHITE;
            boardpieces[row][col].setPiece(PieceFactory.getPiece(pieceName, pieceColor));
        } else if (row == 0 || row == 7) { //caso as linhas sejas 7 ou 0
            if (row == 0)   pieceColor = Piece.BLACK; //linha 0 só brancas
            if (row == 7)   pieceColor = Piece.WHITE;// linha 7 só pretas

            switch (col) {//colunas com as seguintes peças após já terem as linhas defenidas
                case 0://2 torres na coluna 0
                case 7://2 torres na coluna 7
                    pieceName = "rook";
                    break;
                case 1://2 cavalos na coluna 1
                case 6://2 cavalos na coluna 6
                    pieceName = "knight";
                    break;
                case 2://2 bispos na coluna 2
                case 5://2 bispos na coluna 5
                    pieceName = "bishop";
                    break;
                case 3://2 rainhas na coluna 3
                    pieceName = "queen";
                    break;
                case 4:// 2 reis na coluna 4
                    pieceName = "king";
                    break;
            }

            boardpieces[row][col].setPiece(PieceFactory.getPiece(pieceName, pieceColor));
        }
    }
    //verifica se existe peça num determinado quadrado
    public boolean hasPiece(int row, int col) {
        
        return boardpieces[row][col].hasPiece();
    }
    
    //VERIFICA SE A PEÇA NO LOCAL É BRANCA -ENPASSANT
    public boolean hasPiecePawnBlack(int row, int col) {
            
        return boardpieces[row][col].hasPiecePawnBlack();
    }
        
    //VERIFICA SE A PEÇA NO LOCAL É PRETA -ENPASSANT
    public boolean hasPiecePawnWhite(int row, int col) {

        return boardpieces[row][col].hasPiecePawnWhite();
    }
    
    
  
    //retorna o valor da variavel todos peça
    public Piece getPiece(int row, int col) {
        return boardpieces[row][col].getPiece();
    }
    //atualização do tabuleiro
    public void updateBoard(Move move) {
        int oldRow = move.getOldRow();//antiga linha
        int oldCol = move.getOldCol();//antiga coluna
        int newRow = move.getNewRow();//nova  linha
        int newCol = move.getNewCol();//nova coluna

        Piece piece = getPiece(oldRow, oldCol); //busca a peça na posição antiga
        getBoardpiece(oldRow, oldCol).removePiece();//elimina a peça antiga no quadrado
        if (hasPiece(newRow, newCol)) {// se caso no monimento tiver uma peça
            getBoardpiece(newRow, newCol).removePiece();// remove a peça 
        }
        getBoardpiece(newRow, newCol).setPiece(piece);//alteral o valor da variavel e indica a nova posição da peça  que foi passado pelo parametro 
    }

    //Adicionar evento de arrasto para peças
    public void addDragEvent(String playerId) {
        for (int row = 0; row < 8; row++) { //verificação linhas
            for (int col = 0; col < 8; col++) { //verificação colunas
                if (hasPiece(row, col)) {// caso exista peça
                    Piece piece = getPiece(row, col); //seleção da peça
                    //ID do jogador - 1 para o lado branco e 2 para o lado preto
                    if ((playerId.equals("1") && piece.getColor().equals(Piece.WHITE))
                            || (playerId.equals("2") && piece.getColor().equals(Piece.BLACK))) {
                        piece.addImageDragEvent(); //Imagem Arrasto
                    }
                }
            }
        }
    }
    // alteral o valor da variavel setOnPieceMoveListener/Moviemento da peça que foi passado pelo parametro
    public void setOnPieceMoveListener(OnPieceMoveListener listener) {
        this.mListener = listener;
    }
    //envia o movimento das peças para a interface 
    public interface OnPieceMoveListener {
        void onPieceMove(Move move);
        void onPieceChange(int row, int col, String piece);// e caso o peão chegue ao outro lado contem a string para a mudança de peça
        void onPieceRemove(int row, int col); // string que contem o local para a remoção da peça
    }
}
