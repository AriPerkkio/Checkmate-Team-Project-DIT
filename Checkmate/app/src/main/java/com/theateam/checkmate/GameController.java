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
    private List<Piece> kingCapturePieces = new Vector<>(); // Holds pieces that can capture king
    private List<Piece> allowedPieces = new Vector<>(); // Holds pieces that are allowed to be moved when in check
    private List<Square> allowedSquares = new Vector<>(); // Holds squares where it is allowed to move when in check


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

    // Boolean so that method can be broke if needed - see "return false" -statements
    // Graphics are calling this method for every square click that is made
    public boolean selectSquare(String _square) {

        clickedSquare = _square; // update attribute - clickedSquare is used in other methods in this class

        if(kingInCheck(playerOne))  // Check if king is already in check
            if (turn && kingInCheckmate(playerOne))
                Log.d("selectSquare", "Player One CHECKMATE");
        if(kingInCheck(playerTwo))
            if (!turn && kingInCheckmate(playerTwo))
                Log.d("selectSquare", "Player Two CHECKMATE");

        if(pawnPromoting) { // Check if pawn promote is on
            processPromoting(clickedSquare); // Process users piece choosing
            return false; // Break function here
        }

        checkClickMovement();  // Allow piece movement to highlighted squares

        // Check if click is OutOfBoard, empty square or same as on the same piece as before
        if (!checkSquare(clickedSquare)) // True means new piece was clicked
            return false; // Break function here

        // When in check, only allowed pieces are allowed to be selected
        if( (kingInCheck(playerOne) || kingInCheck(playerTwo)) && !allowedPieces.contains(board.getSquare(clickedSquare).getPiece()))
            return false;

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

        highlightsOff(); // Reset all the highlights
        //tests(); // See function
        return true;
    }

    public void movePiece(Piece _piece, Square target, Square from) {
        graphics.movePiece(_piece.getTextureId(), target.getId(), from.getId()); // Move piece in graphics
        from.setPiece(null); // Set old square empty
        _piece.setSquare(target); // Place piece to the new square
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
                 // Check if the move caused check
                if(kingInCheck(playerOne))
                    if (turn && kingInCheckmate(playerOne)){
                        Log.d("checkClickMovement1", "Player One CHECKMATE");
                        return false;
                    }
                if(kingInCheck(playerTwo))
                    if (!turn && kingInCheckmate(playerTwo)){
                        Log.d("checkClickMovement2", "Player Two CHECKMATE");
                        return false;
                    }
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
                // Check if the move caused check
                if(kingInCheck(playerOne))
                    if (turn && kingInCheckmate(playerOne)){
                        Log.d("checkClickMovement3", "Player One CHECKMATE");
                        return false;
                    }
                if(kingInCheck(playerTwo))
                    if (!turn && kingInCheckmate(playerTwo)){
                        Log.d("checkClickMovement4", "Player Two CHECKMATE");
                        return false;
                    }
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
        if(!selectedPiece.getPieceType().equals("King"))
            board.checkPinningMoves(bothSquareLists, selectedPiece); // Check piece and moves for pinning moves
        squareList = bothSquareLists[0]; // Valid moves
        squareListTwo = bothSquareLists[1]; // Valid capture moves

        // Revalidate king's movements - remove the ones that expose it
        if(selectedPiece.getPieceType().equals("King")){
            preventExposeMove(selectedPiece, squareList, true); // Check if valid moves expose king
            preventExposeMove(selectedPiece, squareListTwo, true); // Check if valid capture moves expose king
        }

        // When king is in check use list of allowed squares when picking valid moves and capture moves
        if(( kingInCheck(playerOne) || kingInCheck(playerTwo) )&& !selectedPiece.getPieceType().equals("King")){ // Don't apply it to king
            for(int i=0;i<squareList.size();i++)
                if(!allowedSquares.contains(squareList.get(i))) {
                    squareList.remove(i);
                    i = -1; // Reset counter;
                }
            for(int i=0;i<squareListTwo.size();i++)
                if(!allowedSquares.contains(squareListTwo.get(i))) {
                    squareListTwo.remove(i);
                    i = -1; // Reset counter;
                }
        }
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
            return false;
        }

        // Check if there is a piece on the clicked square and enable it
        if (board.getSquare(_square).getPiece() == null) {
            selectedPiece = null; // Set as null, so that third press re-enables it again.
            Log.e("selectedPiece nulling", "#3");
            highlightsOff();
            return false;
        }

        // Check if previously clicked piece is the same one and disable it
        if (selectedPiece != null && selectedPiece.equals(board.getSquare(_square).getPiece()) && !pawnPromoting) {
            selectedPiece = null;
            Log.e("selectedPiece nulling", "#4");
            highlightsOff();
            return false;
        }

        // Check if previous click was on own piece, and new click on enemy piece that can't be captured
        if (selectedPiece != null && !selectedPiece.getPlayer().equals(board.getSquare(clickedSquare).getPiece().getPlayer())) {
            //highlights.clear();
            Log.e("selectedPiece nulling", "#5");
            selectedPiece = null;
            highlightsOff();
            return false;
        }
        return true;
    }

    // Sets all the remaining highlighting textures as empty and calls graphics to show them
    public void highlightsOff() {
        String[] empty = new String[]{"hide", "empty", "empty"}; // Sets empty texture and empty coordinates
        int count = TextureGL.count - highlights.size(); // Get count for loop
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
            if(selectedPiece.getSquare().getId().charAt(1)=='1' ||
               selectedPiece.getSquare().getId().charAt(1)=='8'){
                pawnPromoting = true;
                if(selectedPiece.getPlayer().isFirst())
                    graphics.pawnPromoteOn("PlayerOne");
                else
                    graphics.pawnPromoteOn("PlayerTwo");
                return true;
            }
        }
        return false;
    }

    // TODO: Bug found here. When rotating not enables, Player Two has still inverted popup window (Check log of OpenGLRenderer.rotated)
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
        graphics.pawnPromoteOff();
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

    // Check if king is in check
    // Compare enemy piece's capture range to kings location
    public boolean kingInCheck(Player _player){

        List<Square> checkMoves;
        kingCapturePieces.clear();
        Player enemy = playerTwo;
        if(!_player.isFirst())
            enemy = playerOne;

        // Player One
        for(int i=0;i<enemy.getPieceList().size();i++){
            checkMoves = board.getValidMoves(enemy.getPieceList().get(i))[1]; // Get valid capture moves for piece
            for(int ii=0;ii<checkMoves.size();ii++){
                if(checkMoves.get(ii).equals(_player.getPieceByType("King").getSquare())){ // One of the capture movements matches to kings position - check
                    kingCapturePieces.add(enemy.getPieceList().get(i));
                    highlights.add(new String[]{checkMoves.get(ii).getId(),"square", "red"}); // Highlight king
                    graphics.highlight(highlights);
                    Log.d("Player "+_player.toString(), "Check");
                    return true;
                }
            }
        }
        return false;
    }

    public boolean kingInCheckmate(Player _player) {
        /***
         Checkmate = Check + Kings has no moves + Capturing pieces can't be eliminated + No moving between king and enemy piece

         1. Capture moves contain kingSquare (Check)
         2. Own capture moves don't contain enemySquare
         3. Own valid moves don't contain move for square between king and enemy piece
         4. King has no valid moves

         This function should be called when kingInCheck() returns true.

         ***/

        boolean canCaptureEnemy = false; // Indicates if it's possible to capture enemy piece which is capturing king
        boolean canMoveBetween = false; // Indicates if it's possible to move between king and enemy capturing piece
        boolean kingHasMoves = false; // Indicates if king has any valid move to avoid check
        List<Piece> capturePieces = new Vector<>(); // Holds pieces which are capturing king
        allowedPieces.clear(); // Clear old allowed pieces and squares from the list
        allowedSquares.clear();
        allowedPieces.add(_player.getPieceByType("King")); // King is always able to be selected

        // Check if any player's piece's capture movements contain enemy square
        for (int i = 0; i < kingCapturePieces.size(); i++) // Check all the pieces that can capture king (the ones that are causing check)
            if (!kingCapturePieces.get(i).getPlayer().equals(_player)) // Check pieces that belong to enemy
                for (int ii = 0; ii < _player.getPieceList().size(); ii++){ // Check all pieces for player
                    List<Square> captureList = board.getValidMoves(_player.getPieceList().get(ii))[1];
                    if (!_player.getPieceList().get(ii).getPieceType().equals("King") && // Don't apply this to king
                        captureList.contains(kingCapturePieces.get(i).getSquare())) { // Check if piece's capture moves contain enemy square
                        highlights.add(new String[]{ _player.getPieceList().get(ii).getSquare().getId(), "square", "blue"});
                        if(!allowedPieces.contains(_player.getPieceList().get(ii)))
                            allowedPieces.add(_player.getPieceList().get(ii)); // This is one of the pieces that is allowed to be moved
                        if(!allowedSquares.contains(kingCapturePieces.get(i).getSquare()))
                            allowedSquares.add(kingCapturePieces.get(i).getSquare()); // One of the allowed squares where to move
                        if(!capturePieces.contains(kingCapturePieces.get(i)))
                            capturePieces.add(kingCapturePieces.get(i)); // Update capturePieces list with new piece
                    }
                }
        if(capturePieces.size()==kingCapturePieces.size()) // All the enemy pieces that can capture king can be captured by own pieces
            canCaptureEnemy = true;

        // Check if any player's piece's valid movements contain any square between enemy and king
        for (int i = 0; i < kingCapturePieces.size(); i++) // Check all the pieces that can capture king (the ones that are causing check)
            if (!kingCapturePieces.get(i).getPlayer().equals(_player)) // Check pieces that belong to enemy
                for (int ii = 0; ii < _player.getPieceList().size(); ii++) { // Check all pieces for player
                    if(!_player.getPieceList().get(ii).getPieceType().equals("King")){
                        int difVerticalKing = (int) _player.getPieceByType("King").getSquare().getId().charAt(0) - (int) kingCapturePieces.get(i).getSquare().getId().charAt(0); // Difference between king and enemy piece
                        int difHorizontalKing = (int) _player.getPieceByType("King").getSquare().getId().charAt(1) - (int) kingCapturePieces.get(i).getSquare().getId().charAt(1);
                        List<Square> moveList = board.getValidMoves(_player.getPieceList().get(ii))[0];

                        for (int iii = 0; iii < moveList.size(); iii++) { // Check all the moves for the piece
                            // Vertical check
                            if (difVerticalKing==0 && moveList.get(iii).getId().charAt(0) == kingCapturePieces.get(i).getSquare().getId().charAt(0) && ( // Same column
                                (int) kingCapturePieces.get(i).getSquare().getId().charAt(1) > (int) moveList.get(iii).getId().charAt(1) && // Enemy>Square>King
                                (int) moveList.get(iii).getId().charAt(1) > (int) _player.getPieceByType("King").getSquare().getId().charAt(1) ||
                                (int) kingCapturePieces.get(i).getSquare().getId().charAt(1) < (int) moveList.get(iii).getId().charAt(1) && // Enemy<Square<King
                                (int) moveList.get(iii).getId().charAt(1) < (int) _player.getPieceByType("King").getSquare().getId().charAt(1))){
                                highlights.add(new String[]{ _player.getPieceList().get(ii).getSquare().getId(), "square", "blue"});
                                if(!allowedPieces.contains(_player.getPieceList().get(ii)))
                                    allowedPieces.add(_player.getPieceList().get(ii)); // This is one of the pieces that is allowed to be moved
                                if(!allowedSquares.contains(moveList.get(iii)))
                                    allowedSquares.add(moveList.get(iii)); // One of the allowed squares where to move
                                if(!capturePieces.contains(kingCapturePieces.get(i)))
                                    capturePieces.add(kingCapturePieces.get(i)); // Update capturePieces list with new piece
                            }

                            // Horizontal check
                            if (difHorizontalKing==0 && moveList.get(iii).getId().charAt(1) == kingCapturePieces.get(i).getSquare().getId().charAt(1) && ( // Same row
                                (int) kingCapturePieces.get(i).getSquare().getId().charAt(0) > (int) moveList.get(iii).getId().charAt(0) && // Enemy>Square>King
                                (int) moveList.get(iii).getId().charAt(0) > (int) _player.getPieceByType("King").getSquare().getId().charAt(0) ||
                                (int) kingCapturePieces.get(i).getSquare().getId().charAt(0) < (int) moveList.get(iii).getId().charAt(0) && // Enemy<Square<King
                                (int) moveList.get(iii).getId().charAt(0) < (int) _player.getPieceByType("King").getSquare().getId().charAt(0))){
                                highlights.add(new String[]{ _player.getPieceList().get(ii).getSquare().getId(), "square", "blue"});
                                if(!allowedPieces.contains(_player.getPieceList().get(ii)))
                                    allowedPieces.add(_player.getPieceList().get(ii)); // This is one of the pieces that is allowed to be moved
                                if(!allowedSquares.contains(moveList.get(iii)))
                                    allowedSquares.add(moveList.get(iii)); // One of the allowed squares where to move
                                if(!capturePieces.contains(kingCapturePieces.get(i)))
                                    capturePieces.add(kingCapturePieces.get(i)); // Update capturePieces list with new piece
                            }

                            // Diagonal check
                            int difVerticalSquare = (int) kingCapturePieces.get(i).getSquare().getId().charAt(0) - (int) moveList.get(iii).getId().charAt(0);
                            int difHorizontalSquare = (int) kingCapturePieces.get(i).getSquare().getId().charAt(1) - (int) moveList.get(iii).getId().charAt(1);
                            if (Math.abs(difHorizontalKing)==Math.abs(difVerticalKing) &&  // Same row
                                Math.abs(difHorizontalSquare)==Math.abs(difVerticalSquare) &&(
                                (int) kingCapturePieces.get(i).getSquare().getId().charAt(0) > (int) moveList.get(iii).getId().charAt(0) && // Enemy>Square>King
                                (int) moveList.get(iii).getId().charAt(0) > (int) _player.getPieceByType("King").getSquare().getId().charAt(0) ||
                                (int) kingCapturePieces.get(i).getSquare().getId().charAt(0) < (int) moveList.get(iii).getId().charAt(0) && // Enemy<Square<King
                                (int) moveList.get(iii).getId().charAt(0) < (int) _player.getPieceByType("King").getSquare().getId().charAt(0))){
                                highlights.add(new String[]{ _player.getPieceList().get(ii).getSquare().getId(), "square", "blue"});
                                if(!allowedPieces.contains(_player.getPieceList().get(ii)))
                                    allowedPieces.add(_player.getPieceList().get(ii)); // This is one of the pieces that is allowed to be moved
                                if(!allowedSquares.contains(moveList.get(iii)))
                                    allowedSquares.add(moveList.get(iii)); // One of the allowed squares where to move
                                if(!capturePieces.contains(kingCapturePieces.get(i)))
                                    capturePieces.add(kingCapturePieces.get(i)); // Update capturePieces list with new piece
                            }

                        }
                    }
                }

        if(capturePieces.size()==kingCapturePieces.size()) // Can move between all the pieces
            canMoveBetween = true;

        // Check if king has any moves
        List<Square> kingMovements = board.getValidMoves(_player.getPieceByType("King"))[0];
        List<Square> kingCaptureMoves = board.getValidMoves(_player.getPieceByType("King"))[1];

        preventExposeMove(_player.getPieceByType("King"), kingMovements, false); // Process king's movements
        preventExposeMove(_player.getPieceByType("King"), kingCaptureMoves, false);

        if(kingMovements.size() != 0 || kingCaptureMoves.size() != 0)
            kingHasMoves = true;

        Log.d("checkMateChecker","canCaptureEnemy "+canCaptureEnemy+". canMoveBetween "+canMoveBetween+". kingHasMoves "+kingHasMoves+".");
        return !canCaptureEnemy && !canMoveBetween && !kingHasMoves || // If any of these is true it's not checkmate
                capturePieces.size()>1 && !kingHasMoves; // Or if king has no movements and it's exposed by more than one piece
    }

    // TODO: May be moved to Board.java, returning third square list for exposing moves (Requires work-around for player references - could be read from piece tho)
    // Check if king's valid moves are exposed
    // I.e. Check if moving king to A4 makes it exposed to enemy pieces' captures.
    // Also works for capture movements. Checks if king becomes exposed after eliminating enemy piece
    public void preventExposeMove(Piece _piece, List<Square> _moveList, boolean highLightOn) {
        List<Square> preventMoves = new Vector<>();
        Player player = playerOne;
        int direction = 1; // Used for pawn
        if(_piece.getPlayer().isFirst()) {
            player = playerTwo;
            direction = -1;
        }

        for (int i = 0; i < player.getPieceList().size(); i++) { // Check all the enemy pieces
            // TODO: Found bug here. PawnSquare X7, King can't capture X5 pieces. Check printscreens
            // Pawns capture movements have to be constructed manually
            if(player.getPieceList().get(i).getPieceType().equals("Pawn")) {
                // Construct possible capture movements from pawns square
                String initialSquare = player.getPieceList().get(i).getSquare().getId();
                String captureOne = (char) ((int) initialSquare.charAt(0)+1)+""+(Integer.parseInt(initialSquare.charAt(1)+"")+direction);
                String captureTwo = (char) ((int) initialSquare.charAt(0)-1)+""+(Integer.parseInt(initialSquare.charAt(1)+"")+direction);
                Square captureSqrOne = board.getSquare(captureOne);
                Square captureSqrTwo = board.getSquare(captureTwo);
                if(captureSqrOne!=null) // Squares are null when they are out of board, i.e. A9
                    preventMoves.add(captureSqrOne);
                if(captureSqrTwo!=null)
                    preventMoves.add(captureSqrTwo);
            }
            else
                preventMoves = board.getValidMoves(player.getPieceList().get(i))[2]; // Other pieces can getValidMoves()

            for (int ii = 0; ii < preventMoves.size(); ii++) {
                if (_moveList.contains(preventMoves.get(ii))) { // Check if enemy's valid moves match with king's valid moves
                    _moveList.remove(preventMoves.get(ii)); // Remove move when match
                    if(highLightOn)
                        highlights.add(new String[]{preventMoves.get(ii).getId(), "cross", "grey"}); // Highlight expose squares
                }
            }
        }
    }

    // TODO: King + castle castling rule
    public void castlingCheck(){
        /**
         1. King cannot have previously moved
         2. Rook that is castling also cannot have previously moved
         3. No pieces can be between the king and the rook that is castling
         4. The king cannot be in check
         5. The king cannot walk into check
         - The king moves normally only one step, in a castling move, he moves 2 steps in the direction towards the rook
         - So an enemy piece cannot be looking at
            1. his current position
            2. the middle square he passes over
            3. his destination square
         - if an enemy is looking at those squares, he cannot castle that side
        **/
    }

    public void tests(){

        if(selectedPiece==null)
            Log.e("tester", "selectedPiece null");

        if(!testsDone){
            for(int x=0;x<8;x++) {
                char startChar = (char) ((int) 'A' +x); // Using ascii values of characters
                for (int y = 1; y <= 8; y++) {
                    if (board.getSquare(startChar + "" + y).getPiece()!=null &&
                        board.getSquare(startChar + "" + y).getPiece().getPieceType().equals("Pawn")){
                        board.getSquare(startChar + "" + y).getPiece().remove(); // Eliminate old piece from game logic
                        graphics.eliminatePiece(board.getSquare(startChar + "" + y).getPiece().getTextureId(), board.getSquare(startChar + "" + y).getId()); // Eliminate old piece from graphics
                        board.getSquare(startChar + "" + y).setPiece(null);
                    }
                }
            }

            selectedPiece = board.getSquare("E1").getPiece(); //Avoids null pointter for selected piece
            testsDone = true;
        }
    }
}