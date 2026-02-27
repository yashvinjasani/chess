package chess;

public abstract class Piece {
    protected Chess.Player player;
    protected String pieceName; 
    protected boolean hasMoved;

    public Piece(Chess.Player player, String pieceName) {
        this.player = player;
        this.pieceName = pieceName;
        this.hasMoved = false;
    }

    public Chess.Player getPlayer() {
        return player;
    }

    public String getPieceName() {
        return pieceName;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Every specific piece must implement its own movement rules.
     * * @param startRow The starting rank index (0-7)
     * @param startCol The starting file index (0-7)
     * @param endRow   The ending rank index (0-7)
     * @param endCol   The ending file index (0-7)
     * @param board    The current 2D array of pieces
     * @return true if the piece can legally move to the destination, false otherwise
     */
    public abstract boolean isValidMove(int startRow, int startCol, int endRow, int endCol, Piece[][] board);
    
    /**
     * Helper method for sliding pieces (Rook, Bishop, Queen).
     * Checks if all squares between the start and end coordinates are empty.
     */
    protected boolean isPathClear(int startRow, int startCol, int endRow, int endCol, Piece[][] board) {
        // Integer.compare returns -1 (if less), 0 (if equal), or 1 (if greater).
        // This perfectly gives us our step direction for any straight or diagonal line!
        int rowStep = Integer.compare(endRow, startRow);
        int colStep = Integer.compare(endCol, startCol);

        int currentRow = startRow + rowStep;
        int currentCol = startCol + colStep;

        // Traverse the path until we reach the destination square
        while (currentRow != endRow || currentCol != endCol) {
            if (board[currentRow][currentCol] != null) {
                return false; // Path is blocked by another piece
            }
            currentRow += rowStep;
            currentCol += colStep;
        }

        return true; // Path is perfectly clear
    }
}