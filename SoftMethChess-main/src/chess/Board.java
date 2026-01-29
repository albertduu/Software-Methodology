package chess;

import java.util.ArrayList;

public class Board {
    private final Piece[][] board = new Piece[8][8];
    private boolean whiteKingMoved, blackKingMoved, whiteRookKingSideMoved, whiteRookQueenSideMoved, blackRookKingSideMoved, blackRookQueenSideMoved;
    private int[] enPassant = {-1, -1};

    public Board() {
        initializeBoard();
    }

    private void initializeBoard() {
        setupPieces(Piece.Player.black, 0, 1);
        setupPieces(Piece.Player.white, 7, 6);
    }

    private void setupPieces(Piece.Player player, int backRank, int pawnRank) {
        boolean isWhite = player == Piece.Player.white;
        int offset = isWhite ? 0 : 2;
        board[backRank][0] = new Rook(Piece.PieceType.values()[offset], player, backRank, 0);
        board[backRank][1] = new Knight(Piece.PieceType.values()[offset + 1], player, backRank, 1);
        board[backRank][2] = new Bishop(Piece.PieceType.values()[offset + 2], player, backRank, 2);
        board[backRank][3] = new Queen(Piece.PieceType.values()[offset + 3], player, backRank, 3);
        board[backRank][4] = new King(Piece.PieceType.values()[offset + 4], player, backRank, 4);
        board[backRank][5] = new Bishop(Piece.PieceType.values()[offset + 2], player, backRank, 5);
        board[backRank][6] = new Knight(Piece.PieceType.values()[offset + 1], player, backRank, 6);
        board[backRank][7] = new Rook(Piece.PieceType.values()[offset], player, backRank, 7);
        for (int i = 0; i < 8; i++)
            board[pawnRank][i] = new Pawn(Piece.PieceType.values()[offset + 5], player, pawnRank, i);
    }

    public ReturnPlay movePiece(String from, String to, Piece.Player player) {
        String[] parts = to.split(" ");
        String toSquare = parts[0];
        char promotionPiece = (parts.length > 1) ? parts[1].charAt(0) : 'Q';

        int fromRow = 8 - Character.getNumericValue(from.charAt(1));
        int fromCol = from.charAt(0) - 'a';
        int toRow = 8 - Character.getNumericValue(toSquare.charAt(1));
        int toCol = toSquare.charAt(0) - 'a';

        Piece piece = board[fromRow][fromCol];
        if (piece == null || piece.getPlayer() != player) return illegalMove();

        if (piece.isValidMove(toRow, toCol, board)) {
            Piece captured = board[toRow][toCol];
            board[toRow][toCol] = piece;
            board[fromRow][fromCol] = null;
            piece.setPosition(toRow, toCol);

            if (isInCheck(player)) {
                revertMove(piece, captured, fromRow, fromCol, toRow, toCol);
                return illegalMove();
            }

            if (piece instanceof Pawn) {
                if (enPassant[0] == toRow && enPassant[1] == toCol)
                    board[player == Piece.Player.white ? toRow + 1 : toRow - 1][toCol] = null;

                enPassant = Math.abs(toRow - fromRow) == 2 ? new int[]{(fromRow + toRow) / 2, toCol} : new int[]{-1, -1};

                if (toRow == 0 || toRow == 7)
                    return promotePawn((Pawn) piece, toRow, toCol, promotionPiece);
            } else {
                enPassant = new int[]{-1, -1};
            }

            updateMoveFlags(piece, fromRow, fromCol, player);
            return evaluateBoardAfterMove(player);
        } else if (piece instanceof King && Math.abs(toCol - fromCol) == 2) {
            return attemptCastling(fromRow, fromCol, toRow, toCol, player);
        } else if (piece instanceof Pawn && enPassant[0] == toRow && enPassant[1] == toCol) {
            return handleEnPassant(fromRow, fromCol, toRow, toCol, player);
        }
        return illegalMove();
    }

    private void revertMove(Piece piece, Piece captured, int fromRow, int fromCol, int toRow, int toCol) {
        board[fromRow][fromCol] = piece;
        board[toRow][toCol] = captured;
        piece.setPosition(fromRow, fromCol);
    }

    private void updateMoveFlags(Piece piece, int fromRow, int fromCol, Piece.Player player) {
        if (piece instanceof King)
            if (player == Piece.Player.white) whiteKingMoved = true; else blackKingMoved = true;
        else if (piece instanceof Rook) {
            if (player == Piece.Player.white) {
                if (fromRow == 7 && fromCol == 0) whiteRookQueenSideMoved = true;
                else if (fromRow == 7 && fromCol == 7) whiteRookKingSideMoved = true;
            } else {
                if (fromRow == 0 && fromCol == 0) blackRookQueenSideMoved = true;
                else if (fromRow == 0 && fromCol == 7) blackRookKingSideMoved = true;
            }
        }
    }

    private ReturnPlay promotePawn(Pawn pawn, int toRow, int toCol, char promotionPiece) {
        Piece.Player player = pawn.getPlayer();
        Piece newPiece = switch (Character.toUpperCase(promotionPiece)) {
            case 'Q' -> new Queen(player == Piece.Player.white ? Piece.PieceType.WQ : Piece.PieceType.BQ, player, toRow, toCol);
            case 'R' -> new Rook(player == Piece.Player.white ? Piece.PieceType.WR : Piece.PieceType.BR, player, toRow, toCol);
            case 'B' -> new Bishop(player == Piece.Player.white ? Piece.PieceType.WB : Piece.PieceType.BB, player, toRow, toCol);
            case 'N' -> new Knight(player == Piece.Player.white ? Piece.PieceType.WN : Piece.PieceType.BN, player, toRow, toCol);
            default -> null;
        };
        if (newPiece == null) return illegalMove();
        board[toRow][toCol] = newPiece;
        return evaluateBoardAfterMove(player);
    }

    private ReturnPlay attemptCastling(int fromRow, int fromCol, int toRow, int toCol, Piece.Player player) {
        boolean isKingSide = toCol > fromCol;
        int rookFromCol = isKingSide ? 7 : 0;
        int rookToCol = isKingSide ? 5 : 3;

        if (player == Piece.Player.white) {
            if (whiteKingMoved || (isKingSide ? whiteRookKingSideMoved : whiteRookQueenSideMoved)) return illegalMove();
        } else {
            if (blackKingMoved || (isKingSide ? blackRookKingSideMoved : blackRookQueenSideMoved)) return illegalMove();
        }

        int step = isKingSide ? 1 : -1;
        for (int col = fromCol + step; col != toCol + step; col += step)
            if (board[fromRow][col] != null) return illegalMove();

        Piece king = board[fromRow][fromCol];
        for (int col = fromCol; col != toCol + step; col += step) {
            board[fromRow][col] = king;
            board[fromRow][fromCol] = null;
            king.setPosition(fromRow, col);
            if (isInCheck(player)) {
                revertMove(king, null, fromRow, fromCol, fromRow, col);
                return illegalMove();
            }
            revertMove(king, null, fromRow, fromCol, fromRow, col);
        }
        return performCastling(fromRow, fromCol, toRow, toCol, fromRow, rookFromCol, fromRow, rookToCol);
    }

    private ReturnPlay performCastling(int kfR, int kfC, int ktR, int ktC, int rfR, int rfC, int rtR, int rtC) {
        board[ktR][ktC] = board[kfR][kfC];
        board[kfR][kfC] = null;
        board[rtR][rtC] = board[rfR][rfC];
        board[rfR][rfC] = null;
        board[ktR][ktC].setPosition(ktR, ktC);
        board[rtR][rtC].setPosition(rtR, rtC);
        return evaluateBoardAfterMove(board[ktR][ktC].getPlayer());
    }

    private ReturnPlay handleEnPassant(int fromRow, int fromCol, int toRow, int toCol, Piece.Player player) {
        if (Math.abs(toCol - fromCol) == 1 && ((player == Piece.Player.white && toRow == fromRow - 1) || (player == Piece.Player.black && toRow == fromRow + 1))) {
            Piece piece = board[fromRow][fromCol];
            Piece captured = board[player == Piece.Player.white ? toRow + 1 : toRow - 1][toCol];
            board[toRow][toCol] = piece;
            board[fromRow][fromCol] = null;
            board[player == Piece.Player.white ? toRow + 1 : toRow - 1][toCol] = null;
            piece.setPosition(toRow, toCol);
            if (isInCheck(player)) {
                revertMove(piece, captured, fromRow, fromCol, toRow, toCol);
                board[player == Piece.Player.white ? toRow + 1 : toRow - 1][toCol] = captured;
                return illegalMove();
            }
            enPassant = new int[]{-1, -1};
            return evaluateBoardAfterMove(player);
        }
        return illegalMove();
    }

    private boolean isInCheck(Piece.Player player) {
        int kR = -1, kC = -1;
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (board[r][c] instanceof King && board[r][c].getPlayer() == player) {
                    kR = r; kC = c; break;
                }
        if (kR == -1) return false;
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (board[r][c] != null && board[r][c].getPlayer() != player && board[r][c].isValidMove(kR, kC, board))
                    return true;
        return false;
    }

    private boolean isCheckmate(Piece.Player player) {
        if (!isInCheck(player)) return false;
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.getPlayer() == player)
                    for (int tr = 0; tr < 8; tr++)
                        for (int tc = 0; tc < 8; tc++)
                            if (p.isValidMove(tr, tc, board)) {
                                Piece t = board[tr][tc];
                                board[tr][tc] = p;
                                board[r][c] = null;
                                int pr = p.getRow(), pc = p.getCol();
                                p.setPosition(tr, tc);
                                boolean check = isInCheck(player);
                                board[r][c] = p;
                                board[tr][tc] = t;
                                p.setPosition(pr, pc);
                                if (!check) return false;
                            }
            }
        return true;
    }

    private ReturnPlay evaluateBoardAfterMove(Piece.Player player) {
        ReturnPlay r = new ReturnPlay();
        r.piecesOnBoard = getPieces();
        Piece.Player opp = player == Piece.Player.white ? Piece.Player.black : Piece.Player.white;
        if (isInCheck(opp)) {
            r.message = isCheckmate(opp)
                    ? (player == Piece.Player.white ? ReturnPlay.Message.CHECKMATE_WHITE_WINS : ReturnPlay.Message.CHECKMATE_BLACK_WINS)
                    : ReturnPlay.Message.CHECK;
        }
        return r;
    }

    private ReturnPlay illegalMove() {
        ReturnPlay r = new ReturnPlay();
        r.piecesOnBoard = getPieces();
        r.message = ReturnPlay.Message.ILLEGAL_MOVE;
        return r;
    }

    public ArrayList<ReturnPiece> getPieces() {
        ArrayList<ReturnPiece> list = new ArrayList<>();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (board[r][c] != null)
                    list.add(board[r][c].toReturnPiece());
        return list;
    }
}
