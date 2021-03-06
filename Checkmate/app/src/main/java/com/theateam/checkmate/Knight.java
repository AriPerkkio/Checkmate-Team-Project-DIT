package com.theateam.checkmate;

import java.util.List;
import java.util.Vector;

/**
 * Created by arska on 23/02/16.
 */
public class Knight extends Piece{

    private List<int[]> knightMoves = new Vector<>();

    public Knight(Square initialSquare, Player owner, int textureId) {
        super(initialSquare, owner, textureId);

        // Set piece type
        this.setPieceType("Knight");

        // Initialize movementList runtime
        knightMoves.add(new int[]{1 ,2});
        knightMoves.add(new int[]{2 ,1});
        knightMoves.add(new int[]{-1 ,2});
        knightMoves.add(new int[]{-2 ,1});
        knightMoves.add(new int[]{-1,-2});
        knightMoves.add(new int[]{-2,-1});
        knightMoves.add(new int[]{1, -2});
        knightMoves.add(new int[]{2, -1});
        this.setMovementList(knightMoves);
    }
}
