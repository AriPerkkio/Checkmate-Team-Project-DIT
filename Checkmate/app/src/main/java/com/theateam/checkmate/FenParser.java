package com.theateam.checkmate;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AriPerkkio on 05/04/16.
 */
public class FenParser {

    public String refreshFen(Board board, boolean turn, Player playerOne, Player playerTwo, Square enPassSquare){
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
        // TODO: Fix 1rb1kr22/1p1p3p/2n3p1/2bpp3/p7/4B1K1/PPP3PP/RN3BNR b ---- - 0 0

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
                    if(checkSquare.getId().equals("H1"))
                        fenString+=""+emptyCounter;
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
        if(enPassSquare!=null)
            fenString += " "+enPassSquare.getId().toLowerCase()+" ";
        else
            fenString += " - ";

        // Construct halfMoves
        fenString+= "0";

        // Construct fullMoves
        fenString+=" 0";

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
        /** ERROR LOG **/ if(fenChar=='E') Log.e("FenChar E", "Piece "+_piece.getPieceType());
        if(_piece.getPlayer().equals(playerTwo))
            fenChar = Character.toLowerCase(fenChar);
        return fenChar;
    }

    // Reads FEN-string and adds piece's to player with correct square from board. Returns map with pairs of (textureId & piece)
    public Map<Integer, Piece> setupFromFen(String _fenString, Player playerOne, Player playerTwo, Board board){

        String[] allRows = _fenString.split("/"); // Split FEN-string into each row
        String firstParse = ""; // First parse will modify empty squares into 0's, i.e. 8 -> 00000000 and 3p4 -> 000p0000
        Map<Integer, Piece> textureIdToPiece = new HashMap<>(); // Holds reserved texture id and the piece

        for(int i=0;i<8;i++) // Check all rows
            for(int ii=0;ii<allRows[i].length();ii++) { // Check all characters
                if(allRows[i].charAt(ii)==' ') // Row 1 ends with space. See definition on FEN-string for info
                    break; // Reached last square, end here
                if (!Character.isDigit(allRows[i].charAt(ii))) // Picked character is not number
                    firstParse += allRows[i].charAt(ii); // Add to list
                else // Picked character is number
                    for(int iii=0;iii<Integer.parseInt(allRows[i].charAt(ii)+"");iii++) // I.e. Picked character is 3, run loop 3 times
                        firstParse+=0; // I.e. Picked character is 3, make it 000
        }
        /** ERROR LOG **/ if(firstParse.length()!=64) Log.e("fenParser", "firstParseLength "+firstParse.length()+"\n"+firstParse);

        int charPick = 0; // From 0-63
        int pieceCount = 0; // Used to keep track for given textureIds
        for(int i=0;i<playerOne.getPieceList().size();i++)
            playerOne.getPieceList().get(i).remove(false);
        for(int i=0;i<playerTwo.getPieceList().size();i++)
            playerTwo.getPieceList().get(i).remove(false);
        playerOne.getPieceList().clear(); // Clear all pieceLists
        playerTwo.getPieceList().clear();

        for(int y=8;y>=1;y--)// From column 'A' to 'H'
            for(int x= (int) 'A';x<=((int) 'A' +7);x++){ // From Row 8 to 1
                char checkChar = firstParse.charAt(charPick);
                if(checkChar!='0'){ // Ignore empty squares
                    pieceCount++; // Piece found, increase textureId
                    Square square = board.getSquare((char)x+""+y); // Current square
                    Player _player = playerOne;
                    if(Character.isLowerCase(checkChar)) // Lowercase indicates playerTwo pieces
                        _player = playerTwo;

                    switch(Character.toLowerCase(checkChar)){
                        case 'p': // Pawns
                            if(_player==playerOne)
                                reverseFen(checkChar, TextureGL.count+pieceCount, playerOne, square); // Add piece to player and square
                            else
                                reverseFen(checkChar, TextureGL.count+pieceCount, playerTwo, square);
                        break;

                        case 'n': // Knights
                            if(_player==playerOne)
                                reverseFen(checkChar, TextureGL.count+pieceCount, playerOne, square);
                            else
                                reverseFen(checkChar, TextureGL.count+pieceCount, playerTwo, square);
                        break;

                        case 'b': // Bishops
                            if(_player==playerOne)
                                reverseFen(checkChar, TextureGL.count+pieceCount, playerOne, square);
                            else
                                reverseFen(checkChar, TextureGL.count+pieceCount, playerTwo, square);
                        break;

                        case 'r': // Rooks
                            if(_player==playerOne)
                                reverseFen(checkChar, TextureGL.count+pieceCount, playerOne, square);
                            else
                                reverseFen(checkChar, TextureGL.count+pieceCount, playerTwo, square);
                        break;

                        case 'q':
                            if(_player==playerOne)
                                reverseFen(checkChar, TextureGL.count+pieceCount, playerOne, square);
                            else
                                reverseFen(checkChar, TextureGL.count+pieceCount, playerTwo, square);
                        break;

                        case 'k':
                            if(_player==playerOne)
                                reverseFen(checkChar, TextureGL.count+pieceCount, playerOne, square);
                            else
                                reverseFen(checkChar, TextureGL.count+pieceCount, playerTwo, square);
                        break;
                    }

                }
                charPick++;
            }

        // Construct textureId&Piece pairs from both players' pieceLists
        for(int i=0;i<playerOne.getPieceList().size();i++)
            textureIdToPiece.put(playerOne.getPieceList().get(i).getTextureId(), playerOne.getPieceList().get(i));
        for(int i=0;i<playerTwo.getPieceList().size();i++)
            textureIdToPiece.put(playerTwo.getPieceList().get(i).getTextureId(), playerTwo.getPieceList().get(i));

        // Set castling
        String castlingChars = _fenString.split(" ")[2];
        // Rooks
        if(castlingChars.charAt(0)!='K' && board.getSquare("H1").getPiece()!=null && board.getSquare("H1").getPiece().getPieceType().equals("Rook"))
            ((Rook) board.getSquare("H1").getPiece()).cantCastle();
        if(castlingChars.charAt(1)!='Q' && board.getSquare("A1").getPiece()!=null && board.getSquare("A1").getPiece().getPieceType().equals("Rook"))
            ((Rook) board.getSquare("A1").getPiece()).cantCastle();
        if(castlingChars.charAt(2)!='k' && board.getSquare("H8").getPiece()!=null && board.getSquare("H8").getPiece().getPieceType().equals("Rook"))
            ((Rook) board.getSquare("H8").getPiece()).cantCastle();
        if(castlingChars.charAt(3)!='q' && board.getSquare("A8").getPiece()!=null && board.getSquare("A8").getPiece().getPieceType().equals("Rook"))
            ((Rook) board.getSquare("A8").getPiece()).cantCastle();
        // Kings
        if(castlingChars.charAt(0)!='K' && castlingChars.charAt(1)!='Q')
            ((King) playerOne.getPieceByType("King")).cantCastle();
        if(castlingChars.charAt(2)!='k' && castlingChars.charAt(3)!='q')
            ((King) playerTwo.getPieceByType("King")).cantCastle();

        return textureIdToPiece; // Map<Integer, Piece>
    }

    // Creates piece from given FEN-String character to player with given textureid and square
    private void reverseFen(char _char,int textureId, Player _player, Square square){

        switch(Character.toLowerCase(_char)){
            case 'p': // Pawn
                 new Pawn(square, _player, textureId);
            break;
            case 'n': // Knight
                 new Knight(square, _player, textureId);
            break;
            case 'b': // Bishop
                 new Bishop(square, _player, textureId);
            break;
            case 'r': // Rook
                 new Rook(square, _player, textureId);
            break;
            case 'q': // Queen
                 new Queen(square, _player, textureId);
            break;
            case 'k': // King
                 new King(square, _player, textureId);
            break;
            default:
                /** ERROR LOG **/ Log.e("reverseFen", "Char: "+_char); // Error - not a FEN-string character
        }
    }

    public String getEnPassSquare(String _fenString){
        String returnString = null;
        if(_fenString.charAt(_fenString.length()-5) != '-')
            returnString = Character.toUpperCase(_fenString.charAt(_fenString.length()-6))
                    +""+
                    Integer.parseInt(_fenString.charAt(_fenString.length() - 5) + ""); // Character index for enPassSquare in FEN

        if(returnString!=null)
            Log.d("fenParser", "getEnPassSquare: "+returnString);
        else
            Log.d("fenParser", "getEnPassSquare: null");
        return returnString;
    }

    public boolean getTurn(String _fenString){
        String playerChar = _fenString.split(" ")[1];

        Log.d("fenParser", "getTurn: "+playerChar);
        return playerChar.equals("w");
    }
}
