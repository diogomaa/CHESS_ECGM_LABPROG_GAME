package chessecgm.model;


public class Move {
    private int oldRow;
    private int oldCol;
    private int newRow;
    private int newCol;
    //movimento contem antiga linha, antiga coluna, nova linha, nova coluna
    public Move(int oldRow, int oldCol, int newRow, int newCol) {
        this.oldRow = oldRow;
        this.oldCol = oldCol;
        this.newRow = newRow;
        this.newCol = newCol;
    }
    //move sting description que contem oldRow_oldCol_newRow_newCol
    public Move(String description) {
        String[] s = description.split("_");
        this.oldRow = Integer.parseInt(s[0]);
        this.oldCol = Integer.parseInt(s[1]);
        this.newRow = Integer.parseInt(s[2]);
        this.newCol = Integer.parseInt(s[3]);
    }
    // retorna o valor da variavel linha antiga
    public int getOldRow() {
        return oldRow;
    }
    // retorna o valor da variavel coluna antiga
    public int getOldCol() {
        return oldCol;
    }
    // retorna o valor da variavel linha nova
    public int getNewRow() {
        return newRow;
    }
    // retorna o valor da variavel coluna nova
    public int getNewCol() {
        return newCol;
    }

    @Override
    //decomposição da string moveque é enviada para o client
    public String toString() {
        return (Integer.toString(oldRow) + "_"
                + Integer.toString(oldCol) + "_"
                + Integer.toString(newRow) + "_"
                + Integer.toString(newCol));
    }
    //string historico do movimento das peças
    public String toBoardMove() {
        String[] r = {"8", "7", "6", "5", "4", "3", "2", "1"}; //linha
        String[] c = {"A", "B", "C", "D", "E", "F", "G", "H"}; //lcoluna
        return (c[oldCol] + r[oldRow] + " -> " + c[newCol] + r[newRow]);//envio da mensagem que indica o historico da ultima jogada por cada jogador
    }
}
