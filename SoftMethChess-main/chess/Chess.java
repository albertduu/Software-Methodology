//Chimezie Agba and Albert Du
package chess;

import java.util.ArrayList;

public class Chess {
    enum Player { white, black }

    private static Board board;
    private static Player currentPlayer;

    public static void start() {
        board = new Board();
        currentPlayer = Player.white;
    }

    public static ReturnPlay play(String moveStr) {
        moveStr = moveStr.trim();
        ReturnPlay result = new ReturnPlay();
        result.piecesOnBoard = getPiecesOnBoard();
		Player opponent = (currentPlayer == Player.white) ? Player.black : Player.white;

        // Handle resign
        if (moveStr.equalsIgnoreCase("resign")) {
            result.message = (currentPlayer == Player.white)
                    ? ReturnPlay.Message.RESIGN_BLACK_WINS
                    : ReturnPlay.Message.RESIGN_WHITE_WINS;
            return result;
        }

        // Parse move string
		// Check for draw offer
		boolean drawOffered = false;
		if (moveStr.toLowerCase().endsWith("draw?")) {
			drawOffered = true;
			moveStr = moveStr.substring(0, moveStr.length() - 5).trim(); // remove "draw?"
		}
        String[] parts = moveStr.split(" ");
        if (parts.length < 2) {
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            return result;
        }

        Square from = parseSquare(parts[0]);
        Square to   = parseSquare(parts[1]);
        char promotionChar = (parts.length == 3) ? parts[2].charAt(0) : 'Q';

        if(from.row > 7 || from.row < 0 || to.row > 7 || to.row < 0 || from.col > 7 || from.col < 0 || to.row > 7 || to.row < 0){
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            return result;
        }
        Piece piece = board.getPiece(from);

        // Check if there is a piece and it belongs to current player
        if (piece == null || piece.getOwner() != currentPlayer) {
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            return result;
        }


		// Check legality including king safety
        boolean movedBefore = piece.hasMoved();
        Piece capturedPiece = board.executeMove(from, to); // simulate
        boolean leavesKingInCheck = board.isSquareAttacked(opponent, board.findKing(currentPlayer));
        board.undoMove(from, to, capturedPiece, movedBefore);

        if (!piece.isLegalMove(from, to, board) || leavesKingInCheck) {
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            return result;
        }
        // --- Execute Move ---
        Piece target = board.getPiece(to);
        if(target != null && target.getOwner() == currentPlayer){
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            return result;
        }
        // Handle en passant capture
        if (piece instanceof Pawn && board.isEnPassantSquare(to)) {
            int capturedRow = (currentPlayer == Player.white) ? to.row + 1 : to.row - 1;
            board.setPiece(new Square(capturedRow, to.col), null);
        }

        // Handle castling
        if (piece instanceof King && Math.abs(to.col - from.col) == 2) {
            boolean kingSide = to.col > from.col;
            int row = from.row;
            int rookColFrom = kingSide ? 7 : 0;
            int rookColTo = kingSide ? 5 : 3;
            Piece rook = board.getPiece(new Square(row, rookColFrom));
            board.setPiece(new Square(row, rookColTo), rook);
            board.setPiece(new Square(row, rookColFrom), null);
            if (rook != null) rook.setMoved();
        }

        // Move the piece
        board.setPiece(to, piece);
        board.setPiece(from, null);
        piece.setMoved();

        // Handle promotion
        if (piece instanceof Pawn) {
            int lastRank = (currentPlayer == Player.white) ? 0 : 7;
            if (to.row == lastRank) {
                Piece promoted = createPromotionPiece(promotionChar, currentPlayer);
                board.setPiece(to, promoted);
            }
        }

        // Update en passant target (pawn moved 2 squares)
        board.updateEnPassantTarget(from, to, piece);

	
		if (board.isSquareAttacked(currentPlayer, board.findKing(opponent))) {
			// King is in check
			if (board.generateAllLegalMoves(opponent).isEmpty()) {
				// Checkmate
				result.message = (currentPlayer == Player.white) 
								? ReturnPlay.Message.CHECKMATE_WHITE_WINS
								: ReturnPlay.Message.CHECKMATE_BLACK_WINS;
			} else {
				// Just check
				result.message = ReturnPlay.Message.CHECK;
			}
		}

        result.piecesOnBoard = getPiecesOnBoard();

        // Switch player
        currentPlayer = (currentPlayer == Player.white) ? Player.black : Player.white;
        if (drawOffered) {
			result.message = ReturnPlay.Message.DRAW;
		}
        return result;
    }

    // ----------------- Helpers -----------------
    private static Square parseSquare(String s) {
        int col = s.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(s.charAt(1)); // rank 1 -> row 7
        return new Square(row, col);
    }

    private static ArrayList<ReturnPiece> getPiecesOnBoard() {
        ArrayList<ReturnPiece> list = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(new Square(r, c));
                if (p != null) {
                    ReturnPiece rp = new ReturnPiece();
                    rp.pieceFile = ReturnPiece.PieceFile.values()[c];
                    rp.pieceRank = 8 - r;
                    rp.pieceType = mapPieceType(p);
                    list.add(rp);
                }
            }
        }
        return list;
    }

    private static ReturnPiece.PieceType mapPieceType(Piece p) {
        if (p instanceof Pawn)   return (p.getOwner() == Player.white) ? ReturnPiece.PieceType.WP : ReturnPiece.PieceType.BP;
        if (p instanceof Rook)   return (p.getOwner() == Player.white) ? ReturnPiece.PieceType.WR : ReturnPiece.PieceType.BR;
        if (p instanceof Knight) return (p.getOwner() == Player.white) ? ReturnPiece.PieceType.WN : ReturnPiece.PieceType.BN;
        if (p instanceof Bishop) return (p.getOwner() == Player.white) ? ReturnPiece.PieceType.WB : ReturnPiece.PieceType.BB;
        if (p instanceof Queen)  return (p.getOwner() == Player.white) ? ReturnPiece.PieceType.WQ : ReturnPiece.PieceType.BQ;
        if (p instanceof King)   return (p.getOwner() == Player.white) ? ReturnPiece.PieceType.WK : ReturnPiece.PieceType.BK;
        return null;
    }

    private static Piece createPromotionPiece(char c, Player owner) {
        switch (c) {
            case 'Q': return new Queen(owner);
            case 'R': return new Rook(owner);
            case 'B': return new Bishop(owner);
            case 'N': return new Knight(owner);
            default:  return new Queen(owner);
        }
    }
}
