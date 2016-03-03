package com.theateam.checkmate;

import android.util.Log;

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
    List<String[]> highlights = new Vector<>();

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
        playerTwo = new Player("AI"); // Can be set as AI or human
        board = new Board(playerOne, playerTwo);
    }

    public static GameController getInstance(){
        if(instance == null)
            instance = new GameController();
        return instance;
    }

    public void movePiece(Piece _piece, Square from, Square target){
        graphics.movePiece(_piece.getTextureId(), from.getId(), target.getId());
    }

    // Boolean so that method can be broke if needed - see "return false" -statements
    public boolean selectSquare(String _square){

        // TODO: GameActivity.coordinates.setText -passing is here just in testing phase

        clickedSquare = _square;

        if(clickedSquare.equals("OutOfBoard")) {
            highlightsOff();
            GameActivity.coordinates.setText(_square);
            return false;
        }

        // Check if there is a piece on the clicked square and enable it
        if(board.getSquare(_square).getPiece()==null){
            highlightsOff();
            GameActivity.coordinates.setText("Square: "+ clickedSquare+ "\nNo Piece.");
            return false;
        }

        // Check if previously clicked piece is the same one and disable it
        if(selectedPiece != null && selectedPiece.equals(board.getSquare(_square).getPiece())){
            highlightsOff();
            GameActivity.coordinates.setText("Square: "+ clickedSquare+ "\nNo Piece.");
            return true;
        }

        selectedPiece = board.getSquare(_square).getPiece();
        highlights.clear();
        highlights.add(new String[]{clickedSquare, "cross", "red"});
        graphics.highlight(highlights);



        /** DEBUG **/
        Log.d("SelectedPiece",selectedPiece.getPlayer().toString()+""+selectedPiece.getPieceType()+ " ID: "+selectedPiece.getTextureId());
        String printText =
                "Square: "+ clickedSquare+
                "\nPiece: "+selectedPiece.getPlayer().toString()+"-"+
                selectedPiece.getPieceType()+ " \nPieceTextureID: "+selectedPiece.getTextureId();
        GameActivity.coordinates.setText(printText);
        //board.logBoardPrint();
        /** DEBUG **/

        return true;
    }

    public void highlightsOff(){
        selectedPiece = null; // Set as null, so that third press re-enables it again.
        highlights.clear();
        highlights.add(new String[]{"hide", "empty", "empty"});
        graphics.highlight(highlights);
    }


    // Adding tests here
    public void tests(String _square){

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
            highlights.clear();
            highlights.add(new String[]{"C7", "circle", "blue"});
            highlights.add(new String[]{"D7", "circle", "red"});
            highlights.add(new String[]{"E7", "cross", "blue"});
            highlights.add(new String[]{"F7", "cross", "red"});
            highlights.add(new String[]{"C6", "circle", "grey"});
            highlights.add(new String[]{"D6", "circle", "green"});
            highlights.add(new String[]{"E6", "cross", "grey"});
            highlights.add(new String[]{"F6", "cross", "green"});
            graphics.highlight(highlights);
        }
    }
}

