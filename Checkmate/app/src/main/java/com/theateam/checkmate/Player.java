package com.theateam.checkmate;

import java.util.List;

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

    public List<Piece> getPieceList(){
        return null; // TODO
    }

    public boolean isHuman(){
        return true; // TODO
    }
}
