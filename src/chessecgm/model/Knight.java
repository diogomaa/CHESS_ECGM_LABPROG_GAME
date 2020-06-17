package chessecgm.model;


public class Knight extends Piece {
    public Knight(String color) {
        super(color);
        //super é usado principalmente para chamar o construtor pai, mas basicamente representa apenas a classe pai e pode ser usado para acessar membros da classe pai
    }

    @Override
    public String getName() {
        return "knight";
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
        // o cavalo move-se em L 
        return ((Math.abs(newRow - oldRow) == 2 && Math.abs(newCol - oldCol) == 1)
                || (Math.abs(newRow - oldRow) == 1 && Math.abs(newCol - oldCol) == 2));
    }
}
