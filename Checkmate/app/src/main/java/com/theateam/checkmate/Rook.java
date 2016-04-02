package com.theateam.checkmate;

import java.util.List;
import java.util.Vector;

/**
 * Created by deadmadness on 24/02/16.
 */
public class Rook extends Piece {

    //fields
    private boolean canCastle;
    private List<int[]> rookMoves = new Vector<>();

    //constructor
    public Rook(Square initialSquare, Player owner, int textureId) {
        super(initialSquare, owner, textureId);

        this.canCastle = true;

        this.setPieceType("Rook");

        //set initial movements
        for(int i=1;i<8;i++) {
            rookMoves.add(new int[]{i,0});     // right
            rookMoves.add(new int[]{0,i});     // up
            rookMoves.add(new int[]{-i,0});    // left
            rookMoves.add(new int[]{0,-i});    // down
        }
        this.setMovementList(rookMoves);
    }

    public void cantCastle(){
        canCastle = false;
    }

    public boolean checkCastling(){
        return canCastle;
    }
}
