package chessecgm.model;


import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;



public class Pawn extends Piece{
    public Pawn(String color) {
       //super é usado principalmente para chamar o construtor pai, mas basicamente representa apenas a classe pai e pode ser usado para acessar membros da classe pai
        super(color);
    }
    


    @Override
    public String getName() {
        return "pawn";
    }

    @Override
    //movimento possivel
    public boolean isLegalMove(ChessBoard b, int newRow, int newCol) {
        int oldRow = this.boardpieceOn.getRow(); //linha antiga da peça
        int oldCol = this.boardpieceOn.getCol(); //linha antiga da peça
          
        //Verifique se o boardpiece/tabuleiro  tem a peça do mesmo lado
        if (b.hasPiece(newRow, newCol)) {
            if (b.getPiece(newRow, newCol).getColor().equals(getColor())) {
                return false;
            }
        }

        if (this.getColor().equals(Piece.WHITE)) {//peças brancas
            if (oldRow == 6) { //O peão pode dar 2 passos apenas se estiver na posição original
                if (Math.abs(newCol - oldCol) == 1 && newRow == oldRow - 1 && b.hasPiece(newRow, newCol)) { //come lateralmente
                    return true;
                } else if (newCol == oldCol && newRow == oldRow - 1 && !b.hasPiece(newRow, newCol)) { //primeira casa só anda para a frente caso não exista peça
                    return true;
                //segunda casa só anda para a frente caso não exista peça
                } else if (newCol == oldCol && newRow == oldRow - 2 && !b.hasPiece(newRow, newCol) && !b.hasPiece(newRow + 1, newCol)) { 
                    
                    return true;
                }
               
                return false;
            }else  if ( oldRow == 3 ) { //"EN PASSANT BRANCAS"
                if (Math.abs(newCol - oldCol) == 1 && newRow == oldRow - 1 && b.hasPiece(newRow, newCol)) {//come lateralmente
                    onPassant = false; //necessário para não eliminar caso coma uma peça lateral e tenha passado por uma area onde poderia ter feito enpassant
                    return true;
                } else if (newCol == oldCol && newRow == oldRow - 1 && !b.hasPiece(newRow, newCol) ) {//primeira casa só anda para a frente caso não exista peça
                    onPassant = false;//necessario pois se passa-se em drag por cima de uma area enpassant este ficaria true e eliminaria a peça
                    return true;
                }else if (newCol == oldCol - 1 && newRow == oldRow - 1  && b.hasPiecePawnBlack(newRow + 1, newCol )  ) {//movimento diagonal para a esquerda
                    //caso tenha um peão ao seu lado onpassant ativa hasPiecePawn...
                    colToEat = newCol; //envia a posição da peça preta da coluna preta 
                    rowToEat = newRow + 1; //envia a posição da peça preta da linha  
                    onPassant = true;
                    return true ;
                }else if (newCol == oldCol + 1 && newRow == oldRow - 1  && b.hasPiecePawnBlack(newRow + 1, newCol )  ) { //moviemnto diagonal para a direita
                    //caso tenha um peão ao seu lado onpassant ativa hasPiecePawn...
                    colToEat = newCol; //envia a posição da peça preta da coluna preta 
                    rowToEat = newRow + 1; //envia a posição da peça preta da linha  
                    onPassant = true;
                     return true;
                }
                return false; 
            }
            
            else  { //O peão não está mais na posição original
                if (Math.abs(newCol - oldCol) == 1 && newRow == oldRow - 1 && b.hasPiece(newRow, newCol)) { //come lateralmente
                    return true;
                } else if (newCol == oldCol && newRow == oldRow - 1 && !b.hasPiece(newRow, newCol)) { // só anda para a frente caso não exista peça
                    return true;
                }
                return false;
            }
        } else { //peças pretas
            if (oldRow == 1) { //O peão pode dar 2 passos apenas se estiver na posição original
                if (Math.abs(newCol - oldCol) == 1 && newRow == oldRow + 1 && b.hasPiece(newRow, newCol)) {//come lateralmente
                    return true;
                } else if (newCol == oldCol && newRow == oldRow + 1 && !b.hasPiece(newRow, newCol)) {//primeira casa só anda para a frente caso não exista peça
                    return true;
                //segunda casa só anda para a frente caso não exista peça    
                } else if (newCol == oldCol  && newRow == oldRow + 2 && !b.hasPiece(newRow, newCol) && !b.hasPiece(newRow - 1, newCol)) {
                    return true;
                }
                return false;
            
            }else  if (oldRow == 4 ) { //"EN PASSANT PRETAS"
               if (Math.abs(newCol - oldCol) == 1 && newRow == oldRow + 1 && b.hasPiece(newRow, newCol)) {//come lateralmente
                   onPassant = false; //necessário para não eliminar caso coma uma peça lateral e tenha passado por uma area onde poderia ter feito enpassant
                    return true;
                } else if (newCol == oldCol && newRow == oldRow + 1 && !b.hasPiece(newRow, newCol)) {//primeira casa só anda para a frente caso não exista peça
                    onPassant = false;//necessario pois se passa-se em drag por cima de uma area enpassant este ficaria true e eliminaria a peça
                    return true;
                } else if (newCol == oldCol - 1 && newRow == oldRow + 1  && b.hasPiecePawnWhite(newRow - 1, newCol )) { //movimento diagonal para a esquerda
                    //caso tenha um peão ao seu lado onpassant ativa hasPiecePawn...
                    colToEat = newCol; //envia a posição da peça branca da coluna  
                    rowToEat = newRow - 1; //envia a posição da peça branca da linha  
                    onPassant = true;
                    return true;
                }else if (newCol == oldCol + 1 && newRow == oldRow + 1  && b.hasPiecePawnWhite(newRow - 1, newCol )) { //moviemnto diagonal para a direita
                    //caso tenha um peão ao seu lado onpassant ativa hasPiecePawn...
                    colToEat = newCol; //envia a posição da peça branca da coluna  
                    rowToEat = newRow - 1; //envia a posição da peça branca da linha  
                    onPassant = true;
                    return true;
                }
                onPassant = false;
                return false;
            }
            else { //O peão não está mais na posição original
                if (Math.abs(newCol - oldCol) == 1 && newRow == oldRow + 1 && b.hasPiece(newRow, newCol)) {//come lateralmente
                    return true;
                } else if (newCol == oldCol && newRow == oldRow + 1 && !b.hasPiece(newRow, newCol)) {// só anda para a frente caso não exista peça
                    return true;
                }
                return false;
            }
        }
    }
}
