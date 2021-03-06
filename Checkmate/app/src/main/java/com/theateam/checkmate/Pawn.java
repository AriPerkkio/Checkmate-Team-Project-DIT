package com.theateam.checkmate;

import java.util.List;
import java.util.Vector;

/**
 * Created by AriPerkkio on 03/03/16.
 */
public class Pawn extends Piece {


    // Attributes
    private List<int[]> pawnMoves = new Vector<>();
    private List<int[]> captureMovement = new Vector<>();
    private boolean enPassPiece;//allows pawn to be captured with enPassant



    //constructor
    public Pawn(Square initialSquare, Player owner, int textureId) {
        super(initialSquare, owner, textureId);

        enPassPiece = false;

        this.setPieceType("Pawn");

        pawnMoves.add(new int[]{0,1});
        captureMovement.add(new int[]{1,1});
        captureMovement.add(new int[]{-1,1});

        this.setMovementList(pawnMoves);
    }

    public List<int[]> getCaptureMovement(){
        return captureMovement;
    }

    public Piece promote(Piece newPiece){
        // TODO: Cast this to given piece
        return null;
    }

    //sets enPassant to be true
    public void setEnPassPiece(){
        this.enPassPiece = !enPassPiece;
    }

    public boolean isEnPassPiece(){
        return enPassPiece;
    }
}
