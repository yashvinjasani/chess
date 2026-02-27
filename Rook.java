package chess;

public class Rook extends Piece {

    public Rook(Chess.Player player) {
        super(player, player == Chess.Player.white ? "wR" : "bR");
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Piece[][] board) {
        // 1. Must move in a straight line
        if (startRow != endRow && startCol != endCol) {
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