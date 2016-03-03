package com.theateam.checkmate;

/**
 * Created by AriPerkkio on 21/02/16.
 *
 * Class to hold pieces. See class diagram for detailed info
 *
 */
public class Square {

    // Attributes
    // References to other clases
    private Piece piece = null; // Initialize as null - it's always first thing to check
    private String id;

    // Id as in "A1", "A2", etc.
    public Square(String _id){
        id = _id;
    }

    public Piece getPiece(){
        return this.piece;
    }

    public boolean hasPiece(){
        return true; // TODO
    }

    public void setPiece(Piece _piece){
        this.piece = _piece;
    }

    public String getId(){
        return id;
    }
}
