package chess;

import java.util.ArrayList;

import chess.Chess.Player;

public class Board {
    private Piece[][] board;
    private Square enPassantTarget;

    public Board(){
        board = new Piece[8][8];

        for(int i=0; i < 8; i++){
            board[1][i] = new Pawn(Chess.Player.black);
            board[6][i] = new Pawn(Chess.Player.white);
        }
        board[7][0] = new Rook(Chess.Player.white);
        board[7][1] = new Knight(Chess.Player.white);
        board[7][2] = new Bishop(Chess.Player.white);
        board[7][3] = new Queen(Chess.Player.white);
        board[7][4] = new King(Chess.Player.white);
        board[7][5] = new Bishop(Chess.Player.white);
        board[7][6] = new Knight(Chess.Player.white);
        board[7][7] = new Rook(Chess.Player.white);
        board[0][0] = new Rook(Chess.Player.black);
        board[0][1] = new Knight(Chess.Player.black);
        board[0][2] = new Bishop(Chess.Player.black);
        board[0][3] = new Queen(Chess.Player.black);
        board[0][4] = new King(Chess.Player.black);
        board[0][5] = new Bishop(Chess.Player.black);
        board[0][6] = new Knight(Chess.Player.black);
        board[0][7] = new Rook(Chess.Player.black);

    }  

    public Piece getPiece(Square square) {
        return board[square.row][square.col];
    }

    public void setPiece(Square target, Piece piece) {
        board[target.row][target.col] = piece;
    }

    public boolean isEmpty(int row, int col) {
        return board[row][col] == null;
    }

    public boolean isPathClear(Square from, Square to) {
        if(from.equals(to)) return true;
        int rowStep = Integer.compare(to.row, from.row);
        int colStep = Integer.compare(to.col, from.col);

        int r = from.row + rowStep;
        int c = from.col + colStep;

        while (r != to.row || c != to.col) {
            if (board[r][c] != null) return false;
            r += rowStep;
            c += colStep;
        }

        return true;
    }

    public boolean canCastle(Chess.Player player, Square kingFrom, Square kingTo) {
        Player opponent = (player == Player.white) ? Player.black : Player.white;
        int row = (player == Chess.Player.white) ? 7 : 0;  // row 0 = rank 1, row 7 = rank 8
        boolean kingSide = kingTo.col > kingFrom.col;

        Square rookSquare = new Square(row, kingSide ? 7 : 0);
        Piece rook = board[row][rookSquare.col];

        if (!(rook instanceof Rook) || rook.hasMoved()) return false;

        // Check path between king and rook
        if (!isPathClear(kingFrom, rookSquare)) return false;

        // Check king is not in, moving through, or ending in check
        int step = kingSide ? 1 : -1;
        for (int c = kingFrom.col; c != kingTo.col + step; c += step) {
            Square sq = new Square(row, c);
            if (isSquareAttacked(opponent, sq)) return false;
        }

        return true;
    }

    public boolean isSquareAttacked(Chess.Player byPlayer, Square square){
        for(int i = 0; i <board.length; i++){
            for(int j = 0; j < board.length; j++){
                Piece p = board[i][j];
                if(p != null && p.getOwner() == byPlayer){
                    if(p.canAttack(new Square(i,j), square, this)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void updateEnPassantTarget(Square from, Square to, Piece piece) {
        enPassantTarget = null; // default
        if (piece instanceof Pawn) {
            int dr = Math.abs(to.row - from.row);
            if (dr == 2) {
                int midRow = (from.row + to.row) / 2;
                enPassantTarget = new Square(midRow, from.col);
            }
        }
    }

    public boolean isEnPassantSquare(Square sq){
        if (enPassantTarget == null) return false;
        return sq.equals(enPassantTarget);
    }

    public Square findKing(Chess.Player player){
        for(int i=0; i<board.length; i++){
            for(int j=0; j<board.length; j++){
                Piece p = board[i][j];
                if(p instanceof King && p.getOwner() == player){
                    return new Square(i, j);
                }
            }
        }
        return null;
    }

    // Temporary execute a move for simulation
    public Piece executeMove(Square from, Square to) {
        Piece movingPiece = getPiece(from);
        Piece capturedPiece = getPiece(to);
        setPiece(to, movingPiece);
        setPiece(from, null);
        movingPiece.setMoved();
        return capturedPiece; // return captured for undo
    }

    // Undo a move after simulation
    public void undoMove(Square from, Square to, Piece capturedPiece, boolean movedBefore) {
        Piece movingPiece = getPiece(to);
        setPiece(from, movingPiece);
        setPiece(to, capturedPiece);
        if (!movedBefore) movingPiece.hasMoved = false;
    }

    public ArrayList<Move> generateAllLegalMoves(Chess.Player player) {
        Player opponent = (player == Player.white) ? Player.black : Player.white;
        ArrayList<Move> moves = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.getOwner() == player) {
                    Square from = new Square(r, c);
                    for (int tr = 0; tr < 8; tr++) {
                        for (int tc = 0; tc < 8; tc++) {
                            Square to = new Square(tr, tc);
                            if (p.isLegalMove(from, to, this)) {
                                // simulate
                                boolean movedBefore = p.hasMoved();
                                Piece captured = executeMove(from, to);
                                if (!isSquareAttacked(opponent, findKing(player))) {
                                    moves.add(new Move(from, to, p, captured));
                                }
                                undoMove(from, to, captured, movedBefore);
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }
}
