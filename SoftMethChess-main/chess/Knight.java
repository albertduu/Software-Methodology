package chess;

public class Knight extends Piece{
    public Knight(Chess.Player owner) { super(owner, 'N'); }

    @Override
    public boolean isLegalMove(Square from, Square to, Board board) {
        if(from.equals(to)) return false;
        Piece target = board.getPiece(to);
        if (target != null && target.getOwner() == owner) {
            return false; // can’t capture your own piece
        }
        int dr = Math.abs(from.row - to.row);
        int dc = Math.abs(from.col - to.col);
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }

    public boolean canAttack(Square from, Square to, Board board) {
        int dr = Math.abs(from.row - to.row);
        int dc = Math.abs(from.col - to.col);
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }
}
