package com.theateam.checkmate;

import java.util.List;

/**
 * Created by AriPerkkio on 21/02/16.
 *
 * Parent class for each piece.
 * See class diagram for detailed info
 *
 */
public class Piece {

    // Attributes
    // References to other classes
    private Square square; // TODO Check initializing
    private Player player; // TODO Check initializing

    private String pieceType;
    private List<int[]> movementList;

    public Piece(){

    }

    public Player getPlayer(){
        return null; // TODO
    }

    public void enablePiece(){
        // TODO
    }

    public void remove(Player _player, Square _square){
        // TODO
    }

    public void setPieceType(String _pieceType){
        this.pieceType = _pieceType;
    }

    public String getPieceType(){
        return this.pieceType;
    }

    public void setMovementList(List<int[]> movements){
        this.movementList = movements;
    }

    public List<int[]> getMovementList(){
        return this.movementList;
    }
}
