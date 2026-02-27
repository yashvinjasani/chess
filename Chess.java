package chess;

import java.util.ArrayList;

public class Chess {

    enum Player { white, black }
    
    // --- Add these static fields to track game state ---
    public static Board gameBoard;
    public static Player currentPlayer;

    /**
     * Plays the next move for whichever player has the turn.
     */
    public static ReturnPlay play(String move) {
        ReturnPlay result = new ReturnPlay();
        
        // 1. Sanitize the input to remove extra spaces
        move = move.trim();

        // 2. Handle Resignation immediately
        if (move.equals("resign")) {
            result.message = currentPlayer == Player.white ? 
                             ReturnPlay.Message.RESIGN_BLACK_WINS : 
                             ReturnPlay.Message.RESIGN_WHITE_WINS;
            result.piecesOnBoard = getBoardState();
            return result; // Game over
        }

        // 3. Tokenize the string (split by one or more spaces)
        String[] tokens = move.split("\\s+");
        
        // A valid move must have at least a start and end position (e.g., "e2" "e4")
        if (tokens.length < 2) {
            result.message = ReturnPlay.Message.ILLEGAL_MOVE;
            result.piecesOnBoard = getBoardState();
            return result;
        }

        String startPos = tokens[0];
        String endPos = tokens[1];
        
        boolean drawRequested = false;
        String promotionPiece = null;

        // Parse optional 3rd or 4th tokens (Promotion piece or "draw?")
        for (int i = 2; i < tokens.length; i++) {
            if (tokens[i].equals("draw?")) {
                drawRequested = true;
            } else if (tokens[i].matches("[RNBQ]")) { // Matches Rook, Knight, Bishop, or Queen
                promotionPiece = tokens[i];
            }
        }

        // 4. Convert Algebraic notation (e.g., "e2") to 2D Array indices (e.g., row 6, col 4)
        int startCol = startPos.charAt(0) - 'a';
        int startRow = 8 - Character.getNumericValue(startPos.charAt(1));
        int endCol = endPos.charAt(0) - 'a';
        int endRow = 8 - Character.getNumericValue(endPos.charAt(1));

        // Boundary check: Ensure coordinates are within the 8x8 board
        if (startRow < 0 || startRow > 7 || startCol < 0 || startCol > 7 ||
            endRow < 0 || endRow > 7 || endCol < 0 || endCol > 7) {
            return illegalMoveResult();
        }

        Piece pieceToMove = gameBoard.getPiece(startRow, startCol);

        // 5. Validation Check: Is there a piece? Does it belong to the current player?
        if (pieceToMove == null || pieceToMove.getPlayer() != currentPlayer) {
            return illegalMoveResult();
        }

        // 6. Rules Check: Can this specific piece physically make this move?
        if (!pieceToMove.isValidMove(startRow, startCol, endRow, endCol, gameBoard.getGrid())) {
            return illegalMoveResult();
        }

        // --- PREVIOUS: SIMULATE THE MOVE TO CHECK FOR SELF-CHECK ---
        Piece capturedPiece = gameBoard.getPiece(endRow, endCol);
        
        // NEW: Check if this is an En Passant move
        boolean isEnPassant = (pieceToMove instanceof Pawn && startCol != endCol && capturedPiece == null);
        Piece epCapturedPawn = null;
        
        if (isEnPassant) {
            epCapturedPawn = gameBoard.getGrid()[startRow][endCol];
            gameBoard.getGrid()[startRow][endCol] = null; // Temporarily remove it
        }

        // Temporarily execute the move
        gameBoard.getGrid()[endRow][endCol] = pieceToMove;
        gameBoard.getGrid()[startRow][startCol] = null;

        // Did this move put the CURRENT player in check?
        boolean putsSelfInCheck = gameBoard.isInCheck(currentPlayer);

        // Undo the move immediately 
        gameBoard.getGrid()[startRow][startCol] = pieceToMove;
        gameBoard.getGrid()[endRow][endCol] = capturedPiece;
        
        if (isEnPassant) {
            gameBoard.getGrid()[startRow][endCol] = epCapturedPawn; // Put the captured pawn back
        }

        if (putsSelfInCheck) {
            return illegalMoveResult();
        }
        // ------------------------------------------------------
        
        // (Later: We will add a check here to ensure the move doesn't put their own King in check)

        // 7. Execute the REAL move 
        gameBoard.movePiece(startRow, startCol, endRow, endCol);

        // --- NEW: EXECUTE EN PASSANT CAPTURE ---
        if (isEnPassant) {
            // Permanently remove the captured pawn from the board
            gameBoard.getGrid()[startRow][endCol] = null; 
        }

        // --- NEW: MANAGE DOUBLE-STEP FLAGS ---
        // Clear the flag for ALL pawns on the board (since the right to En Passant expires after 1 turn)
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = gameBoard.getGrid()[r][c];
                if (p instanceof Pawn) {
                    ((Pawn) p).justDoubleStepped = false;
                }
            }
        }

        // If the move we just made was a double-step, set the flag to true for THIS piece
        if (pieceToMove instanceof Pawn && Math.abs(startRow - endRow) == 2) {
            ((Pawn) pieceToMove).justDoubleStepped = true;
        }

        // ... (Castling and Promotion Logic goes here) ...
        // --- NEW: CASTLING ROOK MOVEMENT LOGIC ---
        // If the piece that just moved was a King, and it traveled 2 columns, it was a castle.
        if (pieceToMove instanceof King && Math.abs(startCol - endCol) == 2) {
            boolean isKingside = endCol > startCol;
            int rookStartCol = isKingside ? 7 : 0;
            int rookEndCol = isKingside ? 5 : 3; // Rook lands on the opposite side of the King
            
            // Move the rook on the board
            gameBoard.movePiece(startRow, rookStartCol, startRow, rookEndCol);
        }
        
        // --- NEW: PAWN PROMOTION LOGIC ---
        if (pieceToMove instanceof Pawn) {
            // Check if the pawn reached the last rank (Row 0 for White, Row 7 for Black)
            if (endRow == 0 || endRow == 7) {
                // Default to Queen if no promotion piece was provided
                if (promotionPiece == null) {
                    promotionPiece = "Q"; 
                }
                
                // Replace the pawn with the new piece on the board
                Piece promotedPiece;
                switch (promotionPiece) {
                    case "R": promotedPiece = new Rook(currentPlayer); break;
                    case "N": promotedPiece = new Knight(currentPlayer); break;
                    case "B": promotedPiece = new Bishop(currentPlayer); break;
                    case "Q": 
                    default:  promotedPiece = new Queen(currentPlayer); break;
                }
                // Mark as moved since it's already in the middle of a game
                promotedPiece.setMoved(true); 
                gameBoard.getGrid()[endRow][endCol] = promotedPiece;
            }
        }   
        // --- NEW: CHECK AND CHECKMATE DETECTION ---
        Player opponent = (currentPlayer == Player.white) ? Player.black : Player.white;
        
        if (gameBoard.isInCheck(opponent)) {
            if (gameBoard.isCheckmate(opponent)) {
                // Return the specific win message based on who just played
                result.message = (currentPlayer == Player.white) ? 
                                 ReturnPlay.Message.CHECKMATE_WHITE_WINS : 
                                 ReturnPlay.Message.CHECKMATE_BLACK_WINS;
            } else {
                result.message = ReturnPlay.Message.CHECK;
            }
        }

        // 8. Handle Draw requests
        if (drawRequested) {
            result.message = ReturnPlay.Message.DRAW;
        }

        // 9. Switch Turns
        currentPlayer = (currentPlayer == Player.white) ? Player.black : Player.white;

        // 10. Package the final board state and return
        result.piecesOnBoard = getBoardState();
        return result;
    }

    /**
     * Helper method to keep code clean when returning an illegal move.
     * Notice that the turn DOES NOT change when an illegal move is made.
     */
    private static ReturnPlay illegalMoveResult() {
        ReturnPlay result = new ReturnPlay();
        result.message = ReturnPlay.Message.ILLEGAL_MOVE;
        result.piecesOnBoard = getBoardState(); // Returns the unchanged board
        return result;
    }    
    
    /**
     * This method should reset the game, and start from scratch.
     */
    public static void start() {
        gameBoard = new Board(); // Creates a fresh board with starting pieces
        currentPlayer = Player.white; // White always goes first
    }

    /**
     * TRANSLATOR HELPER METHOD:
     * Scans our 2D internal grid and converts it into the ArrayList of ReturnPiece 
     * objects required by the autograder.
     */
    private static ArrayList<ReturnPiece> getBoardState() {
        ArrayList<ReturnPiece> piecesOnBoard = new ArrayList<>();
        
        if (gameBoard == null) {
            return piecesOnBoard;
        }

        Piece[][] grid = gameBoard.getGrid();
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece internalPiece = grid[row][col];
                
                if (internalPiece != null) {
                    ReturnPiece rp = new ReturnPiece();
                    
                    // 1. Translate PieceType (e.g., "wN" -> PieceType.WN)
                    rp.pieceType = ReturnPiece.PieceType.valueOf(internalPiece.getPieceName().toUpperCase());
                    
                    // 2. Translate File (column 0-7 -> a-h)
                    // Since PieceFile enum is ordered a, b, c... we can just use the column index
                    rp.pieceFile = ReturnPiece.PieceFile.values()[col];
                    
                    // 3. Translate Rank (row 0-7 -> rank 8-1)
                    // Row 0 is Rank 8, Row 7 is Rank 1.
                    rp.pieceRank = 8 - row;
                    
                    piecesOnBoard.add(rp);
                }
            }
        }
        
        return piecesOnBoard;
    }
}