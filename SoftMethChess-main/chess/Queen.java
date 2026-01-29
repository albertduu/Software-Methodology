package chess;

public class Queen extends Piece{
    public Queen(Chess.Player owner) { super(owner, 'Q'); }

    @Override
    public boolean isLegalMove(Square from, Square to, Board board) {
        if(from.equals(to)) return false;
        Piece target = board.getPiece(to);
        if (target != null && target.getOwner() == owner) {
            return false; // can’t capture your own piece
        }
        if(!(from.row == to.row) && !(from.col == to.col) && Math.abs(to.col-from.col) != Math.abs(to.row-from.row)) return false;
        if (!board.isPathClear(from, to)){
            return false;
        }
        if(from.row == to.row || from.col == to.col) return true;
        if(Math.abs(to.col-from.col) == Math.abs(to.row-from.row)) return true;
        return false;
    }
    
    public boolean canAttack(Square from, Square to, Board board){
        if(!(from.row == to.row) && !(from.col == to.col) && Math.abs(to.col-from.col) != Math.abs(to.row-from.row)) return false;
        if (!board.isPathClear(from, to)){
            return false;
        }
        if(from.row == to.row || from.col == to.col) return true;
        if(Math.abs(to.col-from.col) == Math.abs(to.row-from.row)) return true;
        return false;
    
    }
}
