package chess;

public class Bishop extends Piece{
    public Bishop(Chess.Player owner) { super(owner, 'B'); }

    @Override
    public boolean isLegalMove(Square from, Square to, Board board) {
        if(from.equals(to)) return false;
        Piece target = board.getPiece(to);
        if (target != null && target.getOwner() == owner) {
            return false; // can’t capture your own piece
        }
        if(Math.abs(to.col-from.col) != Math.abs(to.row-from.row)) return false;
        if (!board.isPathClear(from, to)){
            return false;
        }
        if(Math.abs(to.col-from.col) == Math.abs(to.row-from.row)) return true;
        return false;
    }

    public boolean canAttack(Square from, Square to, Board board) {
        if(Math.abs(to.col-from.col) != Math.abs(to.row-from.row)) return false;
        if (!board.isPathClear(from, to)){
            return false;
        }
        if(Math.abs(to.col-from.col) == Math.abs(to.row-from.row)) return true;
        return false;
    }
}
