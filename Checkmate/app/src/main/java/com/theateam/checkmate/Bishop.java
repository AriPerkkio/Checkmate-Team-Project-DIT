package com.theateam.checkmate;

import java.util.List;
import java.util.Vector;

/**
 * Created by arska on 24/02/16.
 */
public class Bishop extends Piece{

    private List<int[]> bishopMoves = new Vector<>();

    public Bishop(){
        super();

        // Set piece type
        this.setPieceType("Bishop");

        // Initialize movementList runtime
        for(int i=1;i<=8;i++){
            bishopMoves.add(new int[]{i ,i});
            bishopMoves.add(new int[]{-i ,-i});
            bishopMoves.add(new int[]{i ,-i});
            bishopMoves.add(new int[]{-i ,i});
        }
        this.setMovementList(bishopMoves);
    }
}
