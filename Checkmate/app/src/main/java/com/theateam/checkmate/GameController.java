package com.theateam.checkmate;

import java.util.List;
import java.util.Vector;

/**
 * Created by AriPerkkio on 21/02/16.
 *
 * Class to control game logic. See class diagram for detailed info
 *
 */
public class GameController {

    // Attributes
    //References for other classes
    private Board board = new Board();
    private Player playerOne = new Player(); // Always Human

    // Initialized by methods of other classes
    private Player playerTwo; // Either Human or AI
    private List<Square> squareList = new Vector<>(); // Holds info about possible moves
    private Piece selectedPiece;

    public List<Square> selectSquare(String _square){
        return null; // TODO
    }

    public void movePiece(Piece _piece, Square from, Square target){
        // TODO
    }

}
