package com.theateam.checkmate;

import android.util.Log;

/**
 * Created by arska on 05/04/16.
 */
public class FenParser {

    public String refreshFen(Board board, boolean turn, Player playerOne, Player playerTwo){
        /**
         https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
         A FEN "record" defines a particular game position, all in one text line and using only the ASCII character set. A text file with only FEN data records should have the file extension ".fen".[1]
         A FEN record contains six fields. The separator between fields is a space. The fields are:
         1. Piece placement (from white's perspective). Each rank is described, starting with rank 8 and ending with rank 1; within each rank, the contents of each square are described from file "a" through file "h". Following the Standard Algebraic Notation (SAN), each piece is identified by a single letter taken from the standard English names (pawn = "P", knight = "N", bishop = "B", rook = "R", queen = "Q" and king = "K").[1] White pieces are designated using upper-case letters ("PNBRQK") while black pieces use lowercase ("pnbrqk"). Empty squares are noted using digits 1 through 8 (the number of empty squares), and "/" separates ranks.
         2. Active color. "w" means White moves next, "b" means Black.
         3. Castling availability. If neither side can castle, this is "-". Otherwise, this has one or more letters: "K" (White can castle kingside), "Q" (White can castle queenside), "k" (Black can castle kingside), and/or "q" (Black can castle queenside).
         4. En passant target square in algebraic notation. If there's no en passant target square, this is "-". If a pawn has just made a two-square move, this is the position "behind" the pawn. This is recorded regardless of whether there is a pawn in position to make an en passant capture.[2]
         5. Halfmove clock: This is the number of halfmoves since the last capture or pawn advance. This is used to determine if a draw can be claimed under the fifty-move rule.
         6. Fullmove number: The number of the full move. It starts at 1, and is incremented after Black's move.

         Examples
         Here is the FEN for the starting position:
         rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 (WIKIPEDIA)
         Here is the FEN after the move 1. e4:
         rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1 (WIKIPEDIA)
         And then after 1. ... c5:
         rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2 (WIKIPEDIA)
         And then after 2. Nf3:
         rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2 (WIKIPEDIA)
         */

        String fenString = ""; // Clear old board layout
        int emptyCounter=0;

        // Construct piece placement
        for(int x=8;x>=1;x--) { // X = Row
            if(emptyCounter!=0) fenString+=""+emptyCounter;
            if(x<8) fenString+="/";
            emptyCounter = 0;
            for (int y = (int) 'A'; y <= (int) 'H'; y++) { // Y = Column
                Square checkSquare = board.getSquare((char) y + "" + x);
                if (checkSquare.getPiece() != null) {
                    if(emptyCounter!=0) fenString+=""+emptyCounter;
                    fenString+=fenForPiece(checkSquare.getPiece(), playerTwo);
                    emptyCounter = 0;
                } else {
                    emptyCounter++;
                }
            }
        }
        // Piece placements complete
        // Construct Active Color
        if(turn) fenString+=" w ";
        else fenString+=" b ";

        // Construct castling indicators
        if(board.getSquare("H1").getPiece()!=null &&
                board.getSquare("H1").getPiece().getPieceType().equals("Rook") &&
                ((Rook)board.getSquare("H1").getPiece()).checkCastling() &&
                ((King) playerOne.getPieceByType("King")).checkCastling())
            fenString+="K";
        else fenString+="-";
        if(board.getSquare("A1").getPiece()!=null &&
                board.getSquare("A1").getPiece().getPieceType().equals("Rook") &&
                ((Rook)board.getSquare("A1").getPiece()).checkCastling() &&
                ((King) playerOne.getPieceByType("King")).checkCastling())
            fenString+="Q";
        else fenString+="-";
        if(board.getSquare("H8").getPiece()!=null &&
                board.getSquare("H8").getPiece().getPieceType().equals("Rook") &&
                ((Rook)board.getSquare("H8").getPiece()).checkCastling() &&
                ((King) playerTwo.getPieceByType("King")).checkCastling())
            fenString+="k";
        else fenString+="-";
        if(board.getSquare("A8").getPiece()!=null &&
                board.getSquare("A8").getPiece().getPieceType().equals("Rook") &&
                ((Rook)board.getSquare("A8").getPiece()).checkCastling() &&
                ((King) playerTwo.getPieceByType("King")).checkCastling())
            fenString+="q";
        else fenString+="-";

        // Construct en passant target square
        // TODO
        fenString+= " - "; // Atm no en passants, waiting for Jamal's implementation

        // Construct halfMoves
        fenString+= "0";

        // Construct fullMoves
        fenString+=" 0";

        //Log.d("Final FenString", fenString);
        return fenString;
    }

    private char fenForPiece(Piece _piece, Player playerTwo){
        /**
         * pawn = "P"
         * knight = "N"
         * bishop = "B"
         * rook = "R"
         * queen = "Q"
         * king = "K"
         * White pieces are designated using upper-case
         */
        char fenChar;
        switch(_piece.getPieceType()){
            case "Pawn":
                fenChar = 'P';
                break;
            case "Knight":
                fenChar = 'N';
                break;
            case "Bishop":
                fenChar = 'B';
                break;
            case "Rook":
                fenChar = 'R';
                break;
            case "Queen":
                fenChar = 'Q';
                break;
            case "King":
                fenChar = 'K';
                break;
            default:
                fenChar = 'E'; // Error
                break;
        }
        if(fenChar=='E') Log.e("FenChar E", "Piece "+_piece.getPieceType());
        if(_piece.getPlayer().equals(playerTwo))
            fenChar = Character.toLowerCase(fenChar);
        return fenChar;
    }
}
