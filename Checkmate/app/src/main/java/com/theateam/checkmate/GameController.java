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
    private boolean turn = true; // True indicates that it's playerOne's turn
    private boolean pawnPromoting = false; // Checks if waiting for user input to pawn promote window
    private boolean testsDone = false; // Tester

    //References for other classes
    private Board board;
    private Player playerOne = new Player("Human", true); // Always Human
    static OpenGLRenderer graphics = OpenGLRenderer.getInstance();
    static GameController instance;

    // Initialized by methods of other classes
    private Player playerTwo; // Either Human or AI
    private List<Square>[] bothSquareLists;
    private List<Square> squareList = new Vector<>(); // Holds info about possible moves
    private List<Square> squareListTwo = new Vector<>(); // Holds info about possible capture moves
    private Piece selectedPiece;


    private GameController() {
        OpenGLRenderer.gameController = this;
        graphics = OpenGLRenderer.getInstance();
        playerTwo = new Player("Human", false); // Can be set as AI or human
        board = new Board(playerOne, playerTwo);
    }

    public static GameController getInstance() {
        if (instance == null)
            instance = new GameController();
        return instance;
    }

    public void movePiece(Piece _piece, Square target, Square from) {
        graphics.movePiece(_piece.getTextureId(), target.getId(), from.getId()); // Move piece in graphics
        from.setPiece(null); // Set old square empty
        _piece.setSquare(target); // Place piece to the new square
    }

    // Boolean so that method can be broke if needed - see "return false" -statements
    // Graphics are calling this method for every square click that is made
    public boolean selectSquare(String _square) {

        clickedSquare = _square; // update attribute - clickedSquare is used in other methods in this class

        if(pawnPromoting) { // Check if pawn promote is on
            Log.i("pawnPromoting", pawnPromoting+"");
            processPromoting(clickedSquare); // Process users piece choosing
            return false; // Break function here
        }
        else
            checkClickMovement();  // Allow piece movement to highlighted squares

        // Check if click is OutOfBoard, empty square or same as on the same piece as before
        if (!checkSquare(clickedSquare)) // True means new piece was clicked
            return false; // Break function here

        // Get new piece
        selectedPiece = board.getSquare(clickedSquare).getPiece();

        // Check if clicked piece belongs to other player and break here
        if (turn && selectedPiece.getPlayer().equals(playerTwo) ||
           !turn && selectedPiece.getPlayer().equals(playerOne)) {
            Log.e("selectedPiece nulling", "#1");
            selectedPiece = null; // Reset selectedPiece back to null
            return false; // Break this function
        }

        // Get valid moves and captures and highlight them
        processMovements();

        /** DEBUG **/
        // TODO: GameActivity.coordinates.setText -passing is here just in testing phase
        Log.d("SelectedPiece", selectedPiece.getPlayer().toString() + "" + selectedPiece.getPieceType() + " ID: " + selectedPiece.getTextureId());
        String printText =
                "Square: " + clickedSquare +
                        "\nPiece: " + selectedPiece.getPlayer().toString() + "-" +
                        selectedPiece.getPieceType() + " \nPieceTextureID: " + selectedPiece.getTextureId();
        GameActivity.coordinates.setText(printText);

        board.logBoardPrint();
        /** DEBUG **/

        highlightsOff(); // Reset all the highlights
        //tests(); // See function
        return true;
    }

    // Check if new click was made to make selected piece to move or to eliminate a piece
    public boolean checkClickMovement() {
        if (selectedPiece != null && board.getSquare(clickedSquare) != null) { // Check that there was a piece selected and click is on the board
            if (squareList.contains(board.getSquare(clickedSquare))) { // Check if clicked square was valid move to empty square
                movePiece(selectedPiece, board.getSquare(clickedSquare), selectedPiece.getSquare()); // Move piece in game logic and graphics
                highlightsOff(); // Turn off all the highlights
                if(callForPromote()) // Check if made move allows pawn promoting
                    return false;
                turn = !turn;
                checkRotating(); // Check if playerTwo is AI and rotate
                return true;
            } else if (squareListTwo.contains(board.getSquare(clickedSquare))) { // Check if clicked square was valid capture movement
                int tempPieceId = board.getSquare(clickedSquare).getPiece().getTextureId(); // Catch old piece id safe temporary
                board.getSquare(clickedSquare).getPiece().remove(); // Eliminate old piece from game logic
                movePiece(selectedPiece, board.getSquare(clickedSquare), selectedPiece.getSquare());  // Move piece to chosen square
                graphics.eliminatePiece(tempPieceId, board.getSquare(clickedSquare).getId()); // Eliminate old piece from graphics
                highlightsOff(); // Turn off all the highlights
                if(callForPromote()) // Check if made move allows pawn promoting
                    return false;
                turn = !turn;
                checkRotating(); // Check if playerTwo is AI and rotate
                return true;
            }
        }
        return false; // No move was made
    }

    // Get valid moves and capture moves and highlight them
    public void processMovements() {
        squareList.clear(); // Empty all moves
        squareListTwo.clear();
        bothSquareLists = board.getValidMoves(selectedPiece);
        squareList = bothSquareLists[0]; // Valid moves
        squareListTwo = bothSquareLists[1]; // Valid capture moves

        highlights.add(new String[]{clickedSquare, "square", "green"}); // Highlight clicked square

        // Highlight valid moves
        for (int i = 0; i < squareList.size(); i++)
            if (squareList.get(i) != null)
                highlights.add(new String[]{squareList.get(i).getId(), "circle", "green"});
        // Highlight valid capture moves
        for (int i = 0; i < squareListTwo.size(); i++)
            if (squareListTwo.get(i) != null)
                highlights.add(new String[]{squareListTwo.get(i).getId(), "cross", "red"});
        graphics.highlight(highlights);
    }

    // Check if clicked square was made to disable selected piece
    public boolean checkSquare(String _square) {
        if (_square.equals("OutOfBoard")) {
            selectedPiece = null; // Set as null, so that third press re-enables it again.
            Log.e("selectedPiece nulling", "#2");
            highlightsOff();
            GameActivity.coordinates.setText(_square);
            return false;
        }

        // Check if there is a piece on the clicked square and enable it
        if (board.getSquare(_square).getPiece() == null) {
            selectedPiece = null; // Set as null, so that third press re-enables it again.
            Log.e("selectedPiece nulling", "#3");
            highlightsOff();
            GameActivity.coordinates.setText("Square: " + _square + "\nNo Piece.");
            return false;
        }

        // Check if previously clicked piece is the same one and disable it
        if (selectedPiece != null && selectedPiece.equals(board.getSquare(_square).getPiece()) && !pawnPromoting) {
            highlights.clear();
            selectedPiece = null;
            Log.e("selectedPiece nulling", "#4");
            highlightsOff();
            GameActivity.coordinates.setText("Square: " + _square + "\nNo Piece.");
            return false;
        }

        // Check if previous click was on own piece, and new click on enemy piece that can't be captured
        if (selectedPiece != null && !selectedPiece.getPlayer().equals(board.getSquare(clickedSquare).getPiece().getPlayer())) {
            highlights.clear();
            Log.e("selectedPiece nulling", "#5");
            selectedPiece = null;
            highlightsOff();
            GameActivity.coordinates.setText("Clicked piece is not in capturing rate");
            return false;
        }
        return true;
    }

    // Sets all the remaining highlighting textures as empty and calls graphics to show them
    public void highlightsOff() {
        String[] empty = new String[]{"hide", "empty", "empty"}; // Sets empty texture and empty coordinates
        int count = 28 - highlights.size(); // Get count for loop
        for (int i = 0; i < count; i++)
            highlights.add(empty);
        graphics.highlight(highlights);
        highlights.clear();
    }


    // Check if another player is not AI and does the rotating
    public void checkRotating() {
        if (!playerTwo.isHuman()) { // Check if playerTwo is AI
            SystemClock.sleep(500); // Sleep 500ms to make rotating and highlightsOff smoother
            graphics.rotate(); // Rotate board and pieces
        }
    }

    public boolean callForPromote(){
        if(selectedPiece != null && selectedPiece.getPieceType().equals("Pawn")){
            Log.d("Pawn", "#1");
            if(selectedPiece.getSquare().getId().charAt(1)=='1' ||
               selectedPiece.getSquare().getId().charAt(1)=='8'){
                Log.d("PawnPromote set on", "#2");
                pawnPromoting = true;
                // TODO: graphics.promotePawnWindow();
                return true;
            }
        }
        return false;
    }

    // This is only called when user has to pick specific piece for pawn promoting
    public boolean processPromoting(String _square){

        Player playerChooser = playerOne; // Initialize as playerOne
        String textureName = ""; // See Coordinates.java for constructing this

        if(!selectedPiece.getPlayer().isFirst()) // Check player and reinitialize if needed
            playerChooser = playerTwo;

        Square tempSquare = selectedPiece.getSquare(); // Hold current square
        int tempTextureId = selectedPiece.getTextureId(); // Hold textureId of the piece


        // Determine which piece was clicked
        // Cases' squares are determined in pawn promote window
        // They are inverted in rotated view
        switch (_square){
            case "A4": case "A5":case "B4":case "B5":
                if(!OpenGLRenderer.rotated)
                    textureName += "queen";
                else
                    textureName += "knight";
            break;

            case "C4": case "C5":case "D4":case "D5":
                if(!OpenGLRenderer.rotated)
                    textureName += "rook";
                else
                    textureName += "bishop";
            break;

            case "E4": case "E5":case "F4":case "F5":
                if(!OpenGLRenderer.rotated)
                    textureName += "bishop";
                else
                    textureName += "rook";
            break;

            case "G4": case "G5":case "H4":case "H5":
                if(!OpenGLRenderer.rotated)
                    textureName += "knight";
                else
                    textureName += "queen";
            break;

            // Click out of the window
            default:
                Log.e("processPawnPromote", "Invalid click");
                return false; // Break here and check next click as before
        }

        // Set new piece to square
        switch (textureName){
            case "queen":
                tempSquare.setPiece(new Queen(tempSquare, playerChooser, tempTextureId));
            break;
            case "rook":
                tempSquare.setPiece(new Rook(tempSquare, playerChooser, tempTextureId));
            break;
            case "bishop":
                tempSquare.setPiece(new Bishop(tempSquare, playerChooser, tempTextureId));
            break;
            case "knight":
                tempSquare.setPiece(new Knight(tempSquare, playerChooser, tempTextureId));
            break;

        }
        // Construct textureId to choose correct player's piece texture
        if(tempSquare.getPiece().getPlayer().isFirst())
            textureName+="PlayerOne";
        else
            textureName+="PlayerTwo";
        // TODO: graphics.pawnPromoteWindowOff();
        graphics.eliminatePiece(selectedPiece.getTextureId(), tempSquare.getId()); // Vanish old piece from graphics
        selectedPiece.remove(); // Get rid of current pawn
        graphics.movePiece(tempSquare.getPiece().getTextureId(), tempSquare.getId(), tempSquare.getId()); // Place new piece to the square
        graphics.promotePawn(tempTextureId, textureName); // Change texture to match chosen piece
        turn = !turn;
        checkRotating();
        highlightsOff();
        selectedPiece = null;
        Log.e("selectedPiece nulling", "#6");
        pawnPromoting = false; // Turn off promoting for pawn
        return true;
    }

    // Pawn Promoting tester
    public void tests(){
        if(selectedPiece==null)
            Log.e("tester", "selectedPiece null");

        if(!testsDone){
            for(int x=0;x<3;x++) {
                char startChar = (char) ((int) 'A' +x); // Using ascii values of characters
                for (int y = 1; y <= 8; y++) {
                    Log.d("tests()", startChar + "" + y);
                    if (board.getSquare(startChar + "" + y).getPiece()!=null && !board.getSquare(startChar + "" + y).getPiece().getPieceType().equals("Pawn")) {
                        Log.d("tests() eliminate", board.getSquare(startChar + "" + y).getPiece().getPieceType());
                        board.getSquare(startChar + "" + y).getPiece().remove(); // Eliminate old piece from game logic
                        graphics.eliminatePiece(board.getSquare(startChar + "" + y).getPiece().getTextureId(), board.getSquare(startChar + "" + y).getId()); // Eliminate old piece from graphics
                        board.getSquare(startChar + "" + y).setPiece(null);
                    }
                }
            }
            movePiece(board.getSquare("A7").getPiece(), board.getSquare("A3"), board.getSquare("A7"));
            movePiece(board.getSquare("A2").getPiece(), board.getSquare("A7"), board.getSquare("A2"));
            movePiece(board.getSquare("A3").getPiece(), board.getSquare("A2"), board.getSquare("A3"));
            movePiece(board.getSquare("B7").getPiece(), board.getSquare("B3"), board.getSquare("B7"));
            movePiece(board.getSquare("B2").getPiece(), board.getSquare("B7"), board.getSquare("B2"));
            movePiece(board.getSquare("B3").getPiece(), board.getSquare("B2"), board.getSquare("B3"));
            movePiece(board.getSquare("C7").getPiece(), board.getSquare("C3"), board.getSquare("C7"));
            movePiece(board.getSquare("C2").getPiece(), board.getSquare("C7"), board.getSquare("C2"));
            movePiece(board.getSquare("C3").getPiece(), board.getSquare("C2"), board.getSquare("C3"));

            selectedPiece = board.getSquare("D1").getPiece(); //Avoids null pointter for selected piece
            testsDone = true;
        }

    }
}
