package chess;

public class Square {
    public int row;
    public int col;

    // Pass in a string like "e4"
    public Square(String algebraic) {
        char fileChar = algebraic.charAt(0); // 'a' through 'h'
        char rankChar = algebraic.charAt(1); // '1' through '8'

        // 'a' is ASCII 97. 'a' - 'a' = 0 (col 0). 'b' - 'a' = 1 (col 1).
        this.col = fileChar - 'a'; 
        
        // '1' is ASCII 49. '8' - '1' = 7 (row 7 - bottom of board).
        // Using Character.getNumericValue converts '1' to int 1.
        this.row = 8 - Character.getNumericValue(rankChar); 
    }
}