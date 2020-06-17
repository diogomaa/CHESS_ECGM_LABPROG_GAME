package chessecgm.model;


public class King extends Piece {
    public King(String color) {
        //super é usado principalmente para chamar o construtor pai, mas basicamente representa apenas a classe pai e pode ser usado para acessar membros da classe pai
        super(color);
    }

    @Override
    public String getName() {
        return "king";
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

        //o rei só se pode mover uma casa em toda a sua volta
        if (Math.abs(newRow - oldRow) < 2 && Math.abs(newCol - oldCol) < 2) {
            return true;
        }

        /*
           Roque//Castling REI
           basicamente quando ambas as peças estão na Row inicial e o Rei 
           estiver na posição destinada a torre pode pasar por cima dela
         */
        if (this.color.equals(Piece.WHITE)) {
            
            //lado direito roque brancas
            if (oldRow == 7 && oldCol == 4 && newRow == 7 && newCol == 6) {
                if (b.hasPiece(7, 7)) {
                    if (b.getPiece(7, 7).getName().equals("rook")
                            && !b.hasPiece(7, 5)) {
                        return true;
                    }
                }
                
             //lado esquerdo roque brancas
            } else if (oldRow == 7 && oldCol == 4 && newRow == 7 && newCol == 2) {
                if (b.hasPiece(7, 0)) {
                    if (b.getPiece(7, 0).getName().equals("rook")
                            && !b.hasPiece(7, 1)
                            && !b.hasPiece(7, 3)) {
                        return true;
                    }
                }
            }
        } else if (this.color.equals(Piece.BLACK)) {
             //lado direito roque pretas
            if (oldRow == 0 && oldCol == 4 && newRow == 0 && newCol == 6) {
                if (b.hasPiece(0, 7)) {
                    if (b.getPiece(0, 7).getName().equals("rook")
                            && !b.hasPiece(0, 5)) {
                        return true;
                    }
                }
                
            //lado esquerdo roque pretas     
            } else if (oldRow == 0 && oldCol == 4 && newRow == 0 && newCol == 2) {
                if (b.hasPiece(0, 0)) {
                    if (b.getPiece(0, 0).getName().equals("rook")
                            && !b.hasPiece(0, 1)
                            && !b.hasPiece(0, 3)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
