package com.theateam.checkmate;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AriPerkkio on 21/02/16.
 *
 * Parent class for AI/Human. Holds info about Pieces etc
 * See class diagram for detailed info
 *
 */
public class Player {

    // Attributes
    // References to other classes
    private List<Piece> pieceList; // TODO Check initializing

    private boolean turn;
    private double timer;
    private String color;
    private String type;
    private boolean first; // True if player is first to start / bottom one / white pieces
    public Map<String, Integer> pieceIds = new HashMap<>();

    public Player(String _type, boolean isFirst){
        type = _type;
        first = isFirst;

        // Setup IDs for graphic drawing (TextureID)
        int startingId = (TextureGL.count+1); // This is dependent of the graphics coordinate order
        // Player One
        pieceIds.put("playerOne_pawn_1", startingId);
        pieceIds.put("playerOne_pawn_2", startingId + 1);
        pieceIds.put("playerOne_pawn_3", startingId + 2);
        pieceIds.put("playerOne_pawn_4", startingId + 3);
        pieceIds.put("playerOne_pawn_5", startingId + 4);
        pieceIds.put("playerOne_pawn_6", startingId + 5);
        pieceIds.put("playerOne_pawn_7", startingId + 6);
        pieceIds.put("playerOne_pawn_8", startingId + 7);
        pieceIds.put("playerOne_rook_1", startingId + 8);
        pieceIds.put("playerOne_knight_1", startingId + 9);
        pieceIds.put("playerOne_bishop_1", startingId + 10);
        pieceIds.put("playerOne_queen", startingId + 11);
        pieceIds.put("playerOne_king", startingId + 12);
        pieceIds.put("playerOne_bishop_2", startingId + 13);
        pieceIds.put("playerOne_knight_2", startingId + 14);
        pieceIds.put("playerOne_rook_2", startingId + 15);
        // Player Two
        pieceIds.put("playerTwo_pawn_1", startingId + 16);
        pieceIds.put("playerTwo_pawn_2", startingId + 17);
        pieceIds.put("playerTwo_pawn_3", startingId + 18);
        pieceIds.put("playerTwo_pawn_4", startingId + 19);
        pieceIds.put("playerTwo_pawn_5", startingId + 20);
        pieceIds.put("playerTwo_pawn_6", startingId + 21);
        pieceIds.put("playerTwo_pawn_7", startingId + 22);
        pieceIds.put("playerTwo_pawn_8", startingId + 23);
        pieceIds.put("playerTwo_rook_1", startingId + 24);
        pieceIds.put("playerTwo_knight_1", startingId + 25);
        pieceIds.put("playerTwo_bishop_1", startingId + 26);
        pieceIds.put("playerTwo_queen", startingId + 27);
        pieceIds.put("playerTwo_king", startingId + 28);
        pieceIds.put("playerTwo_bishop_2", startingId + 29);
        pieceIds.put("playerTwo_knight_2", startingId + 30);
        pieceIds.put("playerTwo_rook_2", startingId + 31);
    }

    public List<Piece> getPieceList(){
        return null; // TODO
    }

    public boolean isHuman(){
        return !type.equals("Human");
    }

    public boolean isFirst(){
        return first;
    }

    public void addPiece(Piece _piece){
        pieceList.add(_piece);
        _piece.setPlayer(this);
        Log.d("Player", _piece.getPieceType()+" added");
    }

    // Used to print debug info about current player
    public String toString(){
        return type+ "";
    }
}
