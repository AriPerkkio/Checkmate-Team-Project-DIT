package com.theateam.checkmate;

import android.os.SystemClock;
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
    private Player playerOne = new Player("Human", true); // Always Human
    static OpenGLRenderer graphics = OpenGLRenderer.getInstance();
    static GameController instance;

    // Initialized by methods of other classes
    private Player playerTwo; // Either Human or AI
    private List<Square> squareList = new Vector<>(); // Holds info about possible moves
    private List<Square> squareListTwo = new Vector<>(); // Holds info about possible capture moves
    private Piece selectedPiece;
    private boolean turn = true; // True indicates that it's playerOne's turn

    private GameController(){
        OpenGLRenderer.gameController = this;
        graphics = OpenGLRenderer.getInstance();
        playerTwo = new Player("Human", false); // Can be set as AI or human
        board = new Board(playerOne, playerTwo);
    }

    public static GameController getInstance(){
        if(instance == null)
            instance = new GameController();
        return instance;
    }

    public void movePiece(Piece _piece, Square target, Square from){
        graphics.movePiece(_piece.getTextureId(), target.getId(), from.getId()); // Move piece in graphics
        from.setPiece(null); // Set old square empty
        _piece.setSquare(target); // Place piece to the new square
    }

    // Boolean so that method can be broke if needed - see "return false" -statements
    // Graphics are calling this method for every square click that is made
    public boolean selectSquare(String _square){

        clickedSquare = _square; // update attribute - clickedSquare is used in other methods in this class

        // Allow piece movement to highlighted squares
        checkClickMovement();

        // Check if click is OutOfBoard, empty square or same as on the same piece as before
        if(!checkSquare(clickedSquare)) // True means new piece was clicked
            return false; // Break function here

        // Get new piece
        selectedPiece = board.getSquare(clickedSquare).getPiece();

        // Check if clicked piece belongs to other player and break here
        if(turn && selectedPiece.getPlayer().equals(playerTwo)){
            selectedPiece = null; // Reset selectedPiece back to null
            return false; // Break this function
        }
        if(!turn && selectedPiece.getPlayer().equals(playerOne)){
            selectedPiece = null; // Reset selectedPiece back to null
            return false; // Break this function
        }

        // Get valid moves and captures and highlight them
        processMovements();

        /** DEBUG **/
        // TODO: GameActivity.coordinates.setText -passing is here just in testing phase
        Log.d("SelectedPiece",selectedPiece.getPlayer().toString()+""+selectedPiece.getPieceType()+ " ID: "+selectedPiece.getTextureId());
        String printText =
                "Square: "+ clickedSquare+
                "\nPiece: "+selectedPiece.getPlayer().toString()+"-"+
                selectedPiece.getPieceType()+ " \nPieceTextureID: "+selectedPiece.getTextureId();
        GameActivity.coordinates.setText(printText);

        board.logBoardPrint();
        /** DEBUG **/

        highlightsOff(); // Reset all the highlights
        return true;
    }

    // Check if new click was made to make selected piece to move or to eliminate a piece
    public void checkClickMovement(){
        if(selectedPiece!=null && board.getSquare(clickedSquare)!=null) { // Check that there was a piece selected and click is on the board
            if (squareList.contains(board.getSquare(clickedSquare))){ // Check if clicked square was valid move to empty square
                movePiece(selectedPiece, board.getSquare(clickedSquare), selectedPiece.getSquare()); // Move piece in game logic and graphics
                highlightsOff(); // Turn off all the highlights
                turn = !turn;
                checkRotating(); // Check if playerTwo is AI and rotate
            }
            else if(squareListTwo.contains(board.getSquare(clickedSquare))){ // Check if clicked square was valid capture movement
                int tempPieceId = board.getSquare(clickedSquare).getPiece().getTextureId(); // Catch old piece id safe temporary
                board.getSquare(clickedSquare).getPiece().remove(); // Eliminate old piece from game logic
                movePiece(selectedPiece, board.getSquare(clickedSquare), selectedPiece.getSquare());  // Move piece to chosen square
                graphics.eliminatePiece(tempPieceId, board.getSquare(clickedSquare).getId()); // Eliminate old piece from graphics
                highlightsOff(); // Turn off all the highlights
                turn = !turn;
                checkRotating(); // Check if playerTwo is AI and rotate
            }
        }
    }

    // Get valid moves and capture moves and highlight them
    public void processMovements(){
        squareList.clear(); // Empty all moves
        squareListTwo.clear();
        squareList = getValidMoves(selectedPiece)[0]; // Valid moves
        squareListTwo = getValidMoves(selectedPiece)[1]; // Valid capture moves

        highlights.add(new String[]{clickedSquare, "square", "green"}); // Highlight clicked square

        // Highlight valid moves
        for(int i=0;i<squareList.size();i++)
            if(squareList.get(i)!=null)
                highlights.add(new String[]{squareList.get(i).getId(), "circle", "green"});
        // Highlight valid capture moves
        for(int i=0;i<squareListTwo.size();i++)
            if(squareListTwo.get(i)!=null)
                highlights.add(new String[]{squareListTwo.get(i).getId(), "cross", "red"});
        graphics.highlight(highlights);
    }

    // Check if clicked square was made to disable selected piece
    public boolean checkSquare(String _square){
        if(_square.equals("OutOfBoard")) {
            selectedPiece = null; // Set as null, so that third press re-enables it again.
            highlightsOff();
            GameActivity.coordinates.setText(_square);
            return false;
        }

        // Check if there is a piece on the clicked square and enable it
        if(board.getSquare(_square).getPiece()==null){
            selectedPiece = null; // Set as null, so that third press re-enables it again.
            highlightsOff();
            GameActivity.coordinates.setText("Square: "+ _square+ "\nNo Piece.");
            return false;
        }

        // Check if previously clicked piece is the same one and disable it
        if(selectedPiece != null && selectedPiece.equals(board.getSquare(_square).getPiece())){
            highlights.clear();
            selectedPiece = null;
            highlightsOff();
            GameActivity.coordinates.setText("Square: "+ _square+ "\nNo Piece.");
            return false;
        }

        // Check if previous click was on own piece, and new click on enemy piece that can't be captured
        if(selectedPiece!=null && !selectedPiece.getPlayer().equals(board.getSquare(clickedSquare).getPiece().getPlayer())){
            highlights.clear();
            selectedPiece = null;
            highlightsOff();
            GameActivity.coordinates.setText("Clicked piece is not in capturing rate");
            return false;
        }
        return true;
    }

    // Sets all the remaining highlighting textures as empty and calls graphics to show them
    public void highlightsOff(){
        String[] empty = new String[]{"hide", "empty", "empty"}; // Sets empty texture and empty coordinates
        int count = 27-highlights.size(); // Get count for loop
        for(int i=0;i<count;i++)
            highlights.add(empty);
        graphics.highlight(highlights);
        highlights.clear();
    }

    // Returns [0] moves for empty squares, [1] movements for captures
    public List<Square>[] getValidMoves(Piece _piece){
        List<Square> returnListOne = new Vector<>(); // Holds movements for empty squares
        List<Square> returnListTwo = new Vector<>(); // Holds capture movements
        List<int[]> pieceMovements = _piece.getMovementList(); // Get pieces movements into int[]

        char column; // As in 'A' - 'H'
        int row; // 1-8
        String fromSquare = _piece.getSquare().getId(); // Get piece's square
        int direction = 0; // Used for pawns to indicate movement direction
        if(!_piece.getPlayer().isFirst()) // Check if piece belongs to first player
            direction = -2;

        for(int i=0;i<pieceMovements.size();i++) { // Check all the movements
            column = (char) ((int) fromSquare.charAt(0) + pieceMovements.get(i)[0]); // Calculate new column by adding initial column + movement's column
            row = Integer.parseInt("" + fromSquare.charAt(1)) + pieceMovements.get(i)[1]; // Calculate new row same way as above
            if(_piece.getPieceType().equals("Pawn")) // If it's pawn increase the row count by direction check number
                row = row+direction;
            if ((int) column >= (int) 'A' && (int) column <= (int) 'H' && row > 0 && row < 9) { // Check that new square is between A1-H8
                Log.d("getValidMoves", column + "" + row);
                if(board.getSquare(column + "" + row).getPiece()==null) // Check if square is empty
                    returnListOne.add(board.getSquare(column + "" + row)); // Add empty square to list
                else if(!board.getSquare(column + "" + row).getPiece().getPlayer().equals(selectedPiece.getPlayer())) // Check if square has enemy piece in it
                    returnListTwo.add(board.getSquare(column + "" + row)); // Add enemy piece's square to other list
            }
        }
        // For all the pieces capture movements are unique - so clear capture movements calculated above and get new ones
        if(_piece.getPieceType().equals("Pawn")) {
            returnListTwo.clear(); // Clear captures that were made above - pawns cant eliminate with [0,1] movement
            List<int[]> extraCaptures = ((Pawn) _piece).getCaptureMovement(); // Cast piece to pawn and get capture movements
            for (int i = 0; i < extraCaptures.size(); i++) { // Check all capture movements
                column = (char) ((int) fromSquare.charAt(0) + extraCaptures.get(i)[0]); // Calculate new column
                row = direction + Integer.parseInt("" + fromSquare.charAt(1)) + extraCaptures.get(i)[1]; // Calculate new row
                if ((int) column >= (int) 'A' && (int) column <= (int) 'H' && row > 0 && row < 9) { // Check that square is valid
                    Log.d("extraMoves", column + "" + row);
                    // Check that square has enemy piece in it
                    if(board.getSquare(column + "" + row).getPiece()!=null &&
                      !board.getSquare(column + "" + row).getPiece().getPlayer().equals(selectedPiece.getPlayer()))
                        returnListTwo.add(board.getSquare(column + "" + row)); // Add enemy piece's square to list
                }
            }
        }
        return new List[]{returnListOne, returnListTwo}; // return value [0] valid moves, [1] capture moves
    }

    // Check if another player is not AI and does the rotating
    public void checkRotating() {
        if (!playerTwo.isHuman()) { // Check if playerTwo is AI
            SystemClock.sleep(500); // Sleep 500ms to make rotating and highlightsOff smoother
            graphics.rotate(); // Rotate board and pieces
        }
    }
}

