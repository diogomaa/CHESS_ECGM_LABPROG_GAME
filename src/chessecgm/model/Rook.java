package chessecgm.model;


public class Rook extends Piece {
    public Rook(String color) {
        super(color);
    }

    @Override
    public String getName() {
        return "rook";
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

        /*
         * Roque//Castling TORRE
         basicamente quando ambas as peças estão na Row inicial e o 
         Rei estiver na posição destinada a torre pode pasar por cima dela
         */
        if (color.equals(Piece.WHITE)) {
            
            
            //lado direito roque brancas
            if (oldRow == 7 && oldCol == 7 && newRow == 7 && newCol == 5) {
                if (b.hasPiece(7, 6)) {
                    Piece piece = b.getPiece(7, 6);
                    if (piece.getName().equals("king") && piece.getColor().equals(Piece.WHITE)) {
                        return true;
                    }
                }
            }
            //lado esquerdo roque brancas
            if (oldRow == 7 && oldCol == 0 && newRow == 7 && newCol == 3) {
                if (b.hasPiece(7, 2) && !b.hasPiece(7, 1) && !b.hasPiece(7, 3)) {
                    Piece piece = b.getPiece(7, 2);
                    if (piece.getName().equals("king") && piece.getColor().equals(Piece.WHITE)) {
                        return true;
                    }
                }
            }
        } else if (color.equals(Piece.BLACK)) {
            
            //lado direito roque pretas
            if (oldRow == 0 && oldCol == 7 && newRow == 0 && newCol == 5) {
                if (b.hasPiece(0, 6)) {
                    Piece piece = b.getPiece(0, 6);
                    if (piece.getName().equals("king") && piece.getColor().equals(Piece.BLACK)) {
                        return true;
                    }
                }
            }
            //lado esquerdo roque pretas
            if (oldRow == 0 && oldCol == 0 && newRow == 0 && newCol == 3) {
                if (b.hasPiece(0, 2) && !b.hasPiece(0, 1) && !b.hasPiece(0, 3)) {
                    Piece piece = b.getPiece(0, 2);
                    if (piece.getName().equals("king") && piece.getColor().equals(Piece.BLACK)) {
                        return true;
                    }
                }
            }
        }

        // Verifique o movimento na linha horizontal
        if (newRow == oldRow) {
            if (newCol > oldCol) {
                for (int i = oldCol + 1; i < newCol; i++) {
                    if (b.hasPiece(oldRow, i)) {
                        return false;
                    }
                }
                return true;
            }
            if (newCol < oldCol) {
                for (int i = oldCol - 1; i > newCol; i--) {
                    if (b.hasPiece(oldRow, i)) {
                        return false;
                    }
                }
                return true;
            }
            if (newCol == oldCol) {
                return true;
            }
            return true;
        }

        //Verifique o movimento na linha vertical
        if (newCol == oldCol) {
            if (newRow > oldRow) {
                for (int i = oldRow + 1; i < newRow; i++) {
                    if (b.hasPiece(i, oldCol)) {
                        return false;
                    }
                }
                return true;
            }
            if (newRow < oldRow) {
                for (int i = oldRow - 1; i > newRow; i--) {
                    if (b.hasPiece(i, oldCol)) {
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
