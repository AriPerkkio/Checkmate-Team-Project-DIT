package com.theateam.checkmate;

import java.util.List;
import java.util.Vector;

/**
 * Created by arska on 24/02/16.
 */
public class Queen extends Piece{

    private List<int[]>queenMoves = new Vector<>();

    public Queen(){
        super();

        // Set piece type
        this.setPieceType("Queen");

        // Initialize movementList runtime
        for(int i=1;i<=8;i++){
            queenMoves.add(new int[]{i ,i});
            queenMoves.add(new int[]{-i ,-i});
            queenMoves.add(new int[]{i ,-i});
            queenMoves.add(new int[]{-i ,i});
            queenMoves.add(new int[]{i ,0});
            queenMoves.add(new int[]{-i ,0});
            queenMoves.add(new int[]{0 ,i});
            queenMoves.add(new int[]{0 ,-i});
        }
        this.setMovementList(queenMoves);
    }
}
