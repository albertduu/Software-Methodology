package chess;

public class Move {
    public Square from, to;
    public Piece piece, capturedPiece;

    public Move(Square from, Square to, Piece piece, Piece captured) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.capturedPiece = captured;
    }
}