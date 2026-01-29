package chess;

public class Square {
    public final int row, col;
    public Square(int row, int col) { this.row = row; this.col = col; }
    public boolean equals(Object o){
        if(o instanceof Square){
            Square other = (Square)o;
            return other.row == this.row && other.col == this.col;
        }
        return false;
    }
}