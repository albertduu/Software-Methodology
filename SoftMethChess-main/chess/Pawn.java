package chess;

public class Pawn extends Piece {
    public Pawn(Chess.Player owner) { super(owner, 'P'); }

    @Override
    public boolean isLegalMove(Square from, Square to, Board board) {
        if(from.equals(to)) return false;
        int dir = (owner == Chess.Player.white) ? -1 : 1;
        int dr = to.row - from.row;
        int dc = to.col - from.col;
        Piece target = board.getPiece(to);
    
        if (target != null && target.getOwner() == owner) {
            return false; // can’t capture your own piece
        }

        // forward moves
        if (dc == 0) {
            if (dr == dir && target == null) return true;
            if (dr == 2 * dir && !hasMoved() && target == null && board.isEmpty(from.row + dir, from.col))
                return true;
        }

        // diagonal captures
        if (Math.abs(dc) == 1 && dr == dir) {
            // normal capture
            if (target != null && target.getOwner() != owner) return true;
            // en passant
            if (board.isEnPassantSquare(to)) return true;
        }

        return false;
    }

    public boolean canAttack(Square from, Square to, Board board) {
        if(from.equals(to)) return false;
        int dir = (owner == Chess.Player.white) ? -1 : 1;  // white moves up (-1), black down (+1)
        int dr = to.row - from.row;
        int dc = Math.abs(to.col - from.col);
        return (dr == dir && dc == 1);
    }
}

