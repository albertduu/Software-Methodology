package chess;

public abstract class Piece {
    protected Chess.Player owner;
    protected char symbol;
    protected boolean hasMoved = false;

    public Piece(Chess.Player owner, char symbol) {
        this.owner = owner;
        this.symbol = symbol;
    }

    public Chess.Player getOwner() { return owner; }
    public char getSymbol() { return symbol; }
    public boolean hasMoved() { return hasMoved; }
    public void setMoved() { hasMoved = true; }

    public abstract boolean isLegalMove(Square from, Square to, Board board);
    public abstract boolean canAttack(Square from, Square to, Board board);
}
