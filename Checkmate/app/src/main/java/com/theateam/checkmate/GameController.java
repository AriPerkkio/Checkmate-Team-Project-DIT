package com.theateam.checkmate;

import java.util.List;
import java.util.Vector;

/**
 * Created by AriPerkkio on 21/02/16.
 *
 * Class to control game logic.
 * This class is set as singleton in order to avoid problems with multiple instances
 * See class diagram for detailed info
 *
 */
public class GameController {

    // Attributes
    String clickedSquare;

    //References for other classes
    private Board board;
    private Player playerOne = new Player("Human"); // Always Human
    static OpenGLRenderer graphics = OpenGLRenderer.getInstance();
    static GameController instance;

    // Initialized by methods of other classes
    private Player playerTwo; // Either Human or AI
    private List<Square> squareList = new Vector<>(); // Holds info about possible moves
    private Piece selectedPiece;

    private GameController(){
        OpenGLRenderer.gameController = this;
        graphics = OpenGLRenderer.getInstance();
    }

    public static GameController getInstance(){
        if(instance == null)
            instance = new GameController();
        return instance;
    }

    public void movePiece(Piece _piece, Square from, Square target){
        // TODO: Pieces should have ID number
        //graphics.movePiece(_piece.getPieceId(), from, target);
    }

    public void selectSquare(String _square){
        //selectedPiece = board.getSquare(_square).getPiece();
        // if(selectedPiece==null) break;
        // /squareList = selectedPiece.

        // TODO: Function tests are here now. Will be replaced later on
        clickedSquare = _square;
        int pieceSelect = 31;
        if(clickedSquare.equals("D2"))
            graphics.movePiece(pieceSelect, "D3", "D2");
        if(clickedSquare.equals("D3"))
            graphics.movePiece(pieceSelect, "D4", "D3");
        if(clickedSquare.equals("D4"))
            graphics.movePiece(pieceSelect, "D5", "D4");
        if(clickedSquare.equals("D5"))
            graphics.movePiece(pieceSelect, "D6", "D5");
        if(clickedSquare.equals("D6"))
            graphics.movePiece(pieceSelect, "E6", "D6");
        if(clickedSquare.equals("E6"))
            graphics.movePiece(pieceSelect, "E5", "E6");
        if(clickedSquare.equals("E5"))
            graphics.movePiece(pieceSelect, "E4", "E5");
        if(clickedSquare.equals("E4"))
            graphics.movePiece(pieceSelect, "E3", "E4");
        if(clickedSquare.equals("E3"))
            graphics.movePiece(pieceSelect, "D3", "E3");
        if(clickedSquare.equals("A1"))
            graphics.rotate();
        if(clickedSquare.equals("H5")) {
            graphics.eliminatePiece(46, "C7");
            graphics.eliminatePiece(47, "D7");
            graphics.eliminatePiece(48, "E7");
            graphics.eliminatePiece(49, "F7");
        }

        // Learning tool tests
        if(clickedSquare.equals("H4")){
            List<String[]> parameters = new Vector<>();
            parameters.add(new String[]{"C7", "circle", "blue"});
            parameters.add(new String[]{"D7", "circle", "red"});
            parameters.add(new String[]{"E7", "cross", "blue"});
            parameters.add(new String[]{"F7", "cross", "red"});
            parameters.add(new String[]{"C6", "circle", "grey"});
            parameters.add(new String[]{"D6", "circle", "green"});
            parameters.add(new String[]{"E6", "cross", "grey"});
            parameters.add(new String[]{"F6", "cross", "green"});
            graphics.highlight(parameters);
        }
        playerTwo = new Player("AI");
        board = new Board(playerOne, playerTwo);
        board.logBoardPrint();
    }
}

