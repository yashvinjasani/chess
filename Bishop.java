package chess;

public class Bishop extends Piece {

    public Bishop(Chess.Player player) {
        super(player, player == Chess.Player.white ? "wB" : "bB");
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Piece[][] board) {
        // 1. Must move in a perfect diagonal
        if (Math.abs(startRow - endRow) != Math.abs(startCol - endCol)) {
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