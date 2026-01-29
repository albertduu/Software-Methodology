package chess;

public class King extends Piece{
    public King(Chess.Player owner) { super(owner, 'k'); }

    @Override
    public boolean isLegalMove(Square from, Square to, Board board) {
        if(from.equals(to)) return false;
        Piece target = board.getPiece(to);
        if (target != null && target.getOwner() == owner) {
            return false; // can’t capture your own piece
        }
        int rowDiff = Math.abs(to.row - from.row);
        int colDiff = Math.abs(to.col - from.col);

        // normal 1-square move
        if (rowDiff <= 1 && colDiff <= 1) return true;

        // possible castling
        if (!this.hasMoved && rowDiff == 0 && (colDiff == 2)) {
            return board.canCastle(this.owner, from, to);
        }

        return false;
    }

    public boolean canAttack(Square from, Square to, Board board){
        if(from.equals(to)) return false;
        int dr = Math.abs(to.row - from.row);
        int dc = Math.abs(to.col - from.col);
        return dr <= 1 && dc <= 1;
    }
}
