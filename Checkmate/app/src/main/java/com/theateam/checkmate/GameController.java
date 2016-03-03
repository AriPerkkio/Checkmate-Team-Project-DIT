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

    public void movePiece(Piece _piece, Square target, Square from){
        graphics.movePiece(_piece.getTextureId(), target.getId(), from.getId());
        from.setPiece(null);
        _piece.setSquare(target);
    }

    // Boolean so that method can be broke if needed - see "return false" -statements
    public boolean selectSquare(String _square){

        // TODO: GameActivity.coordinates.setText -passing is here just in testing phase
        //tests(_square);
        clickedSquare = _square;

        // Check if click is OutOfBoard, empty square or same as on the same piece as before
        if(!checkSquare(clickedSquare)) // True means new piece was clicked
            return false;

        // Get piece
        selectedPiece = board.getSquare(clickedSquare).getPiece();
        // Get valid moves
        squareList.clear(); // Empty all moves
        squareList = getValidMoves(selectedPiece);
        // TODO JAMAL: Highlight squareList with learningTool's green X

        highlights.clear();
        highlights.add(new String[]{clickedSquare, "square", "green"});
        graphics.highlight(highlights);


        /** DEBUG **/
        Log.d("SelectedPiece",selectedPiece.getPlayer().toString()+""+selectedPiece.getPieceType()+ " ID: "+selectedPiece.getTextureId());
        String printText =
                "Square: "+ clickedSquare+
                "\nPiece: "+selectedPiece.getPlayer().toString()+"-"+
                selectedPiece.getPieceType()+ " \nPieceTextureID: "+selectedPiece.getTextureId();
        GameActivity.coordinates.setText(printText);

        board.logBoardPrint();

        /** DEBUG **/

        return true;
    }

    public boolean checkSquare(String _square){
        if(_square.equals("OutOfBoard")) {
            highlightsOff();
            GameActivity.coordinates.setText(_square);
            return false;
        }

        // Check if there is a piece on the clicked square and enable it
        if(board.getSquare(_square).getPiece()==null){
            highlightsOff();
            GameActivity.coordinates.setText("Square: "+ _square+ "\nNo Piece.");
            return false;
        }

        // Check if previously clicked piece is the same one and disable it
        if(selectedPiece != null && selectedPiece.equals(board.getSquare(_square).getPiece())){
            highlightsOff();
            GameActivity.coordinates.setText("Square: "+ _square+ "\nNo Piece.");
            return false;
        }
        return true;
    }

    public void highlightsOff(){
        selectedPiece = null; // Set as null, so that third press re-enables it again.
        highlights.clear();
        highlights.add(new String[]{"hide", "empty", "empty"});
        graphics.highlight(highlights);
    }

    // TODO JAMAL: Check that movements are in A-H, 1-8. At the moment they go over the board
    public List<Square> getValidMoves(Piece _piece){
        List<Square> returnList = new Vector<>();
        List<int[]> pieceMovements = _piece.getMovementList();
        char column;
        int row;
        String fromSquare = _piece.getSquare().getId();

        for(int i=0;i<pieceMovements.size();i++) {
            column = (char) ((int) fromSquare.charAt(0) + pieceMovements.get(i)[0]);
            row = Integer.parseInt(""+fromSquare.charAt(1)) + pieceMovements.get(i)[1];
            Log.d("getValidMoves", column+""+row);
            returnList.add(board.getSquare(column+""+row));
        }
        return returnList;
    }


    // Adding tests here
    public void tests(String _square){

        if(_square.equals("D3")) {
            if(selectedPiece!=null)
                movePiece(selectedPiece, board.getSquare("D3"),board.getSquare("D2"));
        }
        if(_square.equals("D4")) {
            if(selectedPiece!=null)
                movePiece(selectedPiece, board.getSquare("D4"),board.getSquare("D3"));
        }
        if(_square.equals("D5")) {
            if(selectedPiece!=null)
                movePiece(selectedPiece, board.getSquare("D5"),board.getSquare("D4"));
        }
        if(_square.equals("D6")) {
            if(selectedPiece!=null)
                movePiece(selectedPiece, board.getSquare("D6"),board.getSquare("D5"));
        }

        if(_square.equals("A1"))
            graphics.rotate();
        if(_square.equals("H5")) {
            graphics.eliminatePiece(46, "C7");
            graphics.eliminatePiece(47, "D7");
            graphics.eliminatePiece(48, "E7");
            graphics.eliminatePiece(49, "F7");
        }

        // Learning tool tests
        if(_square.equals("H4")){
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

