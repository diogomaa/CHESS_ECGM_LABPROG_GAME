package chessecgm.model;


public class Bishop extends Piece{
    public Bishop(String color) {
        //super é usado principalmente para chamar o construtor pai, mas basicamente representa apenas a classe pai e pode ser usado para acessar membros da classe pai
        super(color);
    }

    @Override
    public String getName() {
        return "bishop";
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
        //Verifique o movimento nas linhas diagonais
        //Verifique se a nova posição na linha diagonal está bloqueada por outra peça
        if (Math.abs(newRow - oldRow) == Math.abs(newCol - oldCol)) {
          
            //Verifique à direita - parte inferior
            
            if (newRow > oldRow && newCol > oldCol) {
                for (int i = 1; i < newRow - oldRow; i++) {
                    if (b.hasPiece(oldRow + i, oldCol + i)) {
                        return false;
                    }
                }
                return true;
            }

            //Verificar à direita - parte superior
            
            if (newRow < oldRow && newCol > oldCol) {
                for (int i = 1; i < newCol - oldCol; i++) {
                    if (b.hasPiece(oldRow - i, oldCol + i)) {
                        return false;
                    }
                }
                return true;
            }

            //Verifique a parte inferior esquerda
             
            if (newRow > oldRow && newCol < oldCol) {
                for (int i = 1; i < newRow - oldRow; i++) {
                    if (b.hasPiece(oldRow + i, oldCol - i)) {
                        return false;
                    }
                }
                return true;
            }

            //Verifique à esquerda - parte superior
            
            if (newRow < oldRow && newCol < oldCol) {
                for (int i = 1; i < oldCol - newCol; i++) {
                    if (b.hasPiece(oldRow - i, oldCol - i)) {
                        return false;
                    }
                }
                return true;
            }
            return true;
        }
        return false;
    }
}
