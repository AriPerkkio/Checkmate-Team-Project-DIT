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
    private int listId;

    public Piece(Square initialSquare, Player owner, int _textureId){
        square = initialSquare;
        square.setPiece(this); // Add piece to square
        player = owner;
        player.addPiece(this); // Add piece to players pieceList
        textureId = _textureId;
    }

    public void setListId(int id){
        listId=id;
    }
    public int getListId(){
        return listId;
    }
    public Player getPlayer(){
        return player;
    }

    public void setPlayer(Player _player){
        this.player = _player;
    }

    // Used when piece is eliminated
    public void remove(){
        player.removePiece(this);
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
