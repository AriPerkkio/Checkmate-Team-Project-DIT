package com.theateam.checkmate;

import android.util.Log;

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
    private Square square;
    private Player player;

    private String pieceType;
    private List<int[]> movementList;
    private int textureId;

    public Piece(Square initialSquare, Player owner, int _textureId){
        square = initialSquare;
        player = owner;
        textureId = _textureId;
    }

    public Player getPlayer(){
        return player;
    }

    public void setPlayer(Player _player){
        this.player = _player;
    }

    public void enablePiece(){
        // TODO: Is this every used
    }

    // Used when piece is eliminated
    public void remove(){
        this.player = null;
        this.square = null;
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

    public int getTextureId(){
        return textureId;
    }

    public void setSquare(Square _square){
        square = _square;
        square.setPiece(this);
    }

    public Square getSquare(){
        return square;
    }
}
