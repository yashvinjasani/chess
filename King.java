package chess;

public class King extends Piece {

    public King(Chess.Player player) {
        super(player, player == Chess.Player.white ? "wK" : "bK");
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Piece[][] board) {
        int rowDiff = Math.abs(startRow - endRow);
        int colDiff = Math.abs(startCol - endCol);

        // 1. Standard Move: One square in any direction
        if (rowDiff <= 1 && colDiff <= 1) {
            Piece destinationPiece = board[endRow][endCol];
            // Can move to an empty square or capture an opponent
            return destinationPiece == null || destinationPiece.getPlayer() != this.player;
        }

        // 2. Castling: King moves two squares left or right on the same row
        if (rowDiff == 0 && colDiff == 2 && !this.hasMoved) {
            // Moving right (col 4 to 6) is Kingside. Moving left (col 4 to 2) is Queenside.
            boolean isKingside = endCol > startCol;
            int rookCol = isKingside ? 7 : 0;
            
            // Look at the corner square to find the Rook
            Piece cornerPiece = board[startRow][rookCol];
            
            if (cornerPiece instanceof Rook && !cornerPiece.hasMoved()) {
                // Verify every square between the King and the Rook is empty
                int step = isKingside ? 1 : -1;
                for (int c = startCol + step; c != rookCol; c += step) {
                    if (board[startRow][c] != null) {
                        return false; // Path is blocked by a piece
                    }
                }
                return true; // Path is clear, hasn't moved, Rook is ready!
            }
        }

        // Not a valid 1-square move, and not a valid castling setup
        return false;
    }
}