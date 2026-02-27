package chess;

public class Pawn extends Piece {

	// --- NEW: Tracks if this pawn just moved two squares on the VERY LAST turn ---
    public boolean justDoubleStepped = false;
	
    public Pawn(Chess.Player player) {
    	//Player player inherited from Piece 
        super(player, player == Chess.Player.white ? "wP" : "bP");
    }

    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Piece[][] board) {
        // White moves "up" the board (decreasing row index). Black moves "down" (increasing row index).
        int direction = (this.player == Chess.Player.white) ? -1 : 1;
        
        int rowDiff = endRow - startRow;
        int colDiff = Math.abs(startCol - endCol);

        // 1. Standard single step forward
        if (colDiff == 0 && rowDiff == direction) {
            // Destination must be entirely empty
            return board[endRow][endCol] == null;
        }

        // 2. Double step forward
        if (colDiff == 0 && rowDiff == 2 * direction && !this.hasMoved) {
            // Both the intermediate square and the destination must be empty
            int intermediateRow = startRow + direction;
            return board[intermediateRow][startCol] == null && board[endRow][endCol] == null;
        }

        // 3. Diagonal Capture (Standard)
        if (colDiff == 1 && rowDiff == direction) {
            Piece targetPiece = board[endRow][endCol];
            if (targetPiece != null && targetPiece.getPlayer() != this.player) {
                return true;
            }
            
            // --- NEW: 4. En Passant Capture ---
            // If we are moving diagonally but the destination is empty...
            if (targetPiece == null) {
                // Check the square immediately adjacent to our starting position
                Piece adjacentPiece = board[startRow][endCol];
                if (adjacentPiece instanceof Pawn && adjacentPiece.getPlayer() != this.player) {
                    Pawn adjacentPawn = (Pawn) adjacentPiece;
                    // We can only capture if they JUST double stepped
                    if (adjacentPawn.justDoubleStepped) {
                        return true; 
                    }
                }
            }
            //------------------------------------
        }
        // If it doesn't match standard forward, double step, or capture, it's illegal
        return false;
    }
}