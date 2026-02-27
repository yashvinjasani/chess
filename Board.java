package chess;

public class Board {
    // The 8x8 grid holding our Piece objects. Null means the square is empty.
    private Piece[][] grid;

    public Board() {
        grid = new Piece[8][8];
        setupBoard();
    }

    /**
     * Places all pieces in their starting positions.
     * We will leave the other pieces commented out until you build their classes.
     */
    private void setupBoard() {
        // --- Black Pieces (Row 0 and 1) ---
        grid[0][0] = new Rook(Chess.Player.black);
        grid[0][1] = new Knight(Chess.Player.black);
        grid[0][2] = new Bishop(Chess.Player.black);
        grid[0][3] = new Queen(Chess.Player.black);
        grid[0][4] = new King(Chess.Player.black);
        grid[0][5] = new Bishop(Chess.Player.black);
        grid[0][6] = new Knight(Chess.Player.black);
        grid[0][7] = new Rook(Chess.Player.black);
        
        for (int col = 0; col < 8; col++) {
             grid[1][col] = new Pawn(Chess.Player.black);
        }

        // --- White Pieces (Row 6 and 7) ---
        for (int col = 0; col < 8; col++) {
             grid[6][col] = new Pawn(Chess.Player.white);
        }

        grid[7][0] = new Rook(Chess.Player.white);
        grid[7][1] = new Knight(Chess.Player.white);
        grid[7][2] = new Bishop(Chess.Player.white);
        grid[7][3] = new Queen(Chess.Player.white);
        grid[7][4] = new King(Chess.Player.white);
        grid[7][5] = new Bishop(Chess.Player.white);
        grid[7][6] = new Knight(Chess.Player.white);
        grid[7][7] = new Rook(Chess.Player.white);
    }

    public Piece getPiece(int row, int col) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            return grid[row][col];
        }
        return null; // Out of bounds
    }

    /**
     * Moves a piece on the board without checking rules (rule checking happens before this is called).
     */
    public void movePiece(int startRow, int startCol, int endRow, int endCol) {
        grid[endRow][endCol] = grid[startRow][startCol];
        grid[startRow][startCol] = null;
        
        // Mark the piece as having moved (useful for Pawns, Rooks, and Kings later)
        if (grid[endRow][endCol] != null) {
            grid[endRow][endCol].setMoved(true);
        }
    }
    /**
     * Checks if the specified player's King is currently under attack.
     */
    public boolean isInCheck(Chess.Player player) {
        int kingRow = -1;
        int kingCol = -1;

        // 1. Find the King's coordinates
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c];
                if (p != null && p instanceof King && p.getPlayer() == player) {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
            if (kingRow != -1) break; // Break outer loop once found
        }

        // 2. Iterate through the board to see if any opponent can attack those coordinates
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c];
                // If it's an opponent's piece
                if (p != null && p.getPlayer() != player) {
                    // Ask the piece if it can legally move to the King's square
                    if (p.isValidMove(r, c, kingRow, kingCol, grid)) {
                        return true; // The King is in check!
                    }
                }
            }
        }

        return false; // No piece is attacking the King
    }
    
    /**
     * Determines if the specified player is in checkmate.
     */
    public boolean isCheckmate(Chess.Player player) {
        // 1. If the player isn't in check, it can't be checkmate
        // (If they are not in check but have no legal moves, it is Stalemate, 
        // which your assignment says you are not required to implement).
        if (!isInCheck(player)) {
            return false;
        }

        // 2. Iterate through every square to find the player's pieces
        for (int startRow = 0; startRow < 8; startRow++) {
            for (int startCol = 0; startCol < 8; startCol++) {
                Piece currentPiece = grid[startRow][startCol];
                
                if (currentPiece != null && currentPiece.getPlayer() == player) {
                    
                    // 3. For this piece, try moving it to every possible square on the board
                    for (int endRow = 0; endRow < 8; endRow++) {
                        for (int endCol = 0; endCol < 8; endCol++) {
                            
                            // If the piece is physically allowed to make this move...
                            if (currentPiece.isValidMove(startRow, startCol, endRow, endCol, grid)) {
                                
                            	// --- SIMULATE THE MOVE ---
                                Piece capturedPiece = grid[endRow][endCol];
                                
                                boolean isEnPassant = (currentPiece instanceof Pawn && startCol != endCol && capturedPiece == null);
                                Piece epCapturedPawn = null;
                                if (isEnPassant) {
                                    epCapturedPawn = grid[startRow][endCol];
                                    grid[startRow][endCol] = null;
                                }

                                grid[endRow][endCol] = currentPiece;
                                grid[startRow][startCol] = null;

                                boolean stillInCheck = isInCheck(player);

                                // --- UNDO THE MOVE ---
                                grid[startRow][startCol] = currentPiece;
                                grid[endRow][endCol] = capturedPiece;
                                
                                if (isEnPassant) {
                                    grid[startRow][endCol] = epCapturedPawn;
                                }                            }
                        }
                    }
                }
            }
        }

        // 4. We tried every possible move for every single piece, and the King is still in check.
        return true; 
    }
    
    // Optional but helpful: Getter for the raw grid if Chess.java needs to iterate over it
    public Piece[][] getGrid() {
        return grid;
    }
}