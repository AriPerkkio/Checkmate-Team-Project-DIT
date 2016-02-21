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
    private Piece piece = new Piece();

    public Piece getPiece(){
        return this.piece;
    }

    public boolean hasPiece(){
        return true; // TODO
    }
}
