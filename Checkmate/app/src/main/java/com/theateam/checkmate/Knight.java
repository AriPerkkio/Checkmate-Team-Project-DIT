package com.theateam.checkmate;

import java.util.List;
import java.util.Vector;

/**
 * Created by arska on 23/02/16.
 */
public class Knight extends Piece{

    private List<int[]> knightMoves = new Vector<>();

    public Knight(){
        super();

        // Set piece type
        this.setPieceType("Knight");

        // Initialize movementList runtime
        knightMoves.add(new int[]{1 ,3});
        knightMoves.add(new int[]{3 ,1});
        knightMoves.add(new int[]{-1 ,3});
        knightMoves.add(new int[]{-3 ,1});
        knightMoves.add(new int[]{-1,-3});
        knightMoves.add(new int[]{-3,-1});
        knightMoves.add(new int[]{1, -3});
        knightMoves.add(new int[]{3, -1});
        this.setMovementList(knightMoves);
    }
}
