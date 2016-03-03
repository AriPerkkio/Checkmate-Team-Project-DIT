
package com.theateam.checkmate;

import java.util.List;
import java.util.Vector;

/**
 * Created by AriPerkkio on 24/02/16.
 */
public class King extends Piece {

    //fields
    private List<int[]> kingMoves = new Vector<>();
    private boolean inCheck;
    private boolean canCastle;

    //constructor
    public King(Square initialSquare, Player owner, int textureId) {
        super(initialSquare, owner, textureId);

        //initial state of king
        inCheck = false;
        canCastle = false;

        // Set piece type
        this.setPieceType("King");

        // Initialize movementList runtime
        kingMoves.add(new int[]{1 ,1});
        kingMoves.add(new int[]{-1 ,-1});
        kingMoves.add(new int[]{1 ,-1});
        kingMoves.add(new int[]{-1 ,1});
        kingMoves.add(new int[]{1 ,0});
        kingMoves.add(new int[]{-1 ,0});
        kingMoves.add(new int[]{0 ,1});
        kingMoves.add(new int[]{0 ,-1});

        this.setMovementList(kingMoves);
    }

    //methods
}