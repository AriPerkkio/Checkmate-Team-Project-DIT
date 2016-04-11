package com.theateam.checkmate;

import android.util.Log;

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
    private boolean enPassSquare;

    // Id as in "A1", "A2", etc.
    public Square(String _id){
        id = _id;
        enPassSquare = false;
    }

    public Piece getPiece(){
        return this.piece;
    }

    public boolean hasPiece(){
        return (piece!=null);
    }

    public void setPiece(Piece _piece){
        this.piece = _piece;
    }

    public void setEnPassSquare(){
        //Log.d("Square", "1st: "+this.id+" enPass: "+enPassSquare+"->"+!enPassSquare);
        enPassSquare = !enPassSquare;
    }

    public boolean isEnPassSquare(){
        return enPassSquare;
    }

    public String getId(){
        return id;
    }
}
