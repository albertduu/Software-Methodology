package chess;

public class Rook extends Piece{
    public Rook(Chess.Player owner) { super(owner, 'R'); }

    @Override
    public boolean isLegalMove(Square from, Square to, Board board) {
        if(from.equals(to)) return false;
        Piece target = board.getPiece(to);
        if (target != null && target.getOwner() == owner) {
            return false; // can’t capture your own piece
        }
        if(!(from.row == to.row) && !(from.col == to.col)) return false;
        if (!board.isPathClear(from, to)){
            return false;
        }
        if(from.row == to.row || from.col == to.col) return true;
        return false;
    }
    
    public boolean canAttack(Square from, Square to, Board board) {
        if(!(from.row == to.row) && !(from.col == to.col)) return false;
        if (!board.isPathClear(from, to)){
            return false;
        }
        if(from.row == to.row || from.col == to.col) return true;
        return false;
    }
}
