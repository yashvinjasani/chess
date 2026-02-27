package chess;

public class Knight extends Piece {

    public Knight(Chess.Player player) {
        // Pass the player color and the correct string representation ("wN" or "bN")
        super(player, player == Chess.Player.white ? "wN" : "bN");
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Piece[][] board) {
        // 1. Calculate the absolute difference in rows and columns
        int rowDiff = Math.abs(startRow - endRow);
        int colDiff = Math.abs(startCol - endCol);

        // 2. Check if the movement matches the "L" shape
        boolean isLShape = (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
        
        if (!isLShape) {
            return false;
        }

        // 3. Check the destination square. 
        // A knight can land on an empty square or capture an opponent's piece,
        // but it CANNOT land on a square occupied by a piece of its own color.
        Piece destinationPiece = board[endRow][endCol];
        if (destinationPiece != null && destinationPiece.getPlayer() == this.player) {
            return false;
        }

        return true;
    }
}