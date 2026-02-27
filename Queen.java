package chess;

public class Queen extends Piece {

    public Queen(Chess.Player player) {
        super(player, player == Chess.Player.white ? "wQ" : "bQ");
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Piece[][] board) {
        boolean isStraight = (startRow == endRow || startCol == endCol);
        boolean isDiagonal = (Math.abs(startRow - endRow) == Math.abs(startCol - endCol));

        // 1. Must move like a Rook or a Bishop
        if (!isStraight && !isDiagonal) {
            return false;
        }

        // 2. Cannot capture friendly pieces
        Piece destinationPiece = board[endRow][endCol];
        if (destinationPiece != null && destinationPiece.getPlayer() == this.player) {
            return false;
        }

        // 3. Path must be clear
        return isPathClear(startRow, startCol, endRow, endCol, board);
    }
}