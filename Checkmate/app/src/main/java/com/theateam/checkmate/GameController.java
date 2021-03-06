package com.theateam.checkmate;

import android.os.SystemClock;
import android.support.annotation.IntegerRes;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by AriPerkkio on 21/02/16.
 *
 * Class to control game logic.
 * See class diagram for detailed info
 *
 */
public class GameController {

    // Attributes
    String clickedSquare;
    List<String[]> highlights = new Vector<>();
    private boolean turn = true; // True indicates that it's playerOne's turn
    private boolean pawnPromoting = false; // Checks if waiting for user input to pawn promote window
    public boolean learningTool; // Used in OpenGLRenderer.highlight()
    private int themeId;
    private String fenString = ""; // Board layout using FEN
    private int drawCounter = 0;   //counter for fifty move rule draw
    private boolean timerRunning = false; // Indicator for both timers' status

    //References for other classes
    private Board board;
    private Player playerOne = new Player("Human", true); // Always Human
    static OpenGLRenderer graphics = OpenGLRenderer.getInstance();
    static GameController instance; // The only instance of GameController
    private FenParser fenParser = new FenParser(); // Used to create FEN-string

    // AI
    private AiEngine aiEngine; // Initialized in aiMove()
    private int thinkTime; // Time given for AI to think for a move - has an effect on AI difficult level
    private int thinkDepth; // Depth of moves used in calculation
    private int level;

    // Initialized by methods
    private Player playerTwo; // Either Human or AI
    private List<Square>[] bothSquareLists;
    private List<Square> squareList = new Vector<>(); // Holds info about possible moves
    private List<Square> squareListTwo = new Vector<>(); // Holds info about possible capture moves
    private Piece selectedPiece; // Piece that is selected by player or AI
    private List<Piece> kingCapturePieces = new Vector<>(); // Holds pieces that can capture king
    private List<Piece> allowedPieces = new Vector<>(); // Holds pieces that are allowed to be moved when in check
    private List<Square> allowedSquares = new Vector<>(); // Holds squares where it is allowed to move when in check
    private List<Square> castlingSquares = new Vector<>(); // Holds squares where king can do castling move
    private Map<Square, Rook> rookForSquare = new HashMap<>(); // Holds rook for kings castling square
    private Map<Rook, Square> squareForRook = new HashMap<>(); // Holds square for rooks castling move
    private Map<Integer, Piece> textureIdToPiece; // Holds Texture IDs to each piece
    private String startingFenString; // StartPosFEN: "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    private List<String> fenList = new Vector<>(); // Holds FEN-Strings from each move
    private Map<String, long[]> mapFenToTimers = new HashMap<>(); // FenList + TimerOne, TimerTwo
    private boolean modeKingOfTheHill = false;
    private boolean modeBlitz = false;

    public GameController(String gameMode, boolean _learningTool, String _startingFenString, List<String> fenHistory, Map<String, long[]> fenToTimers,int _timeLimit, int _themeId) {
        // Instances
        OpenGLRenderer.gameController = this;
        graphics = OpenGLRenderer.getInstance();
        instance = this;

        // If OpenGLRenderer was previously left as rotated, rotate it back
        if (OpenGLRenderer.rotated)
            graphics.rotate();

        // Game options
        learningTool = _learningTool;
        startingFenString = _startingFenString;
        for (int i = 0; i < fenHistory.size(); i++) { // Add FEN history to list
            fenList.add(fenHistory.get(i));
            if(!modeBlitz)
                mapFenToTimers.put(fenHistory.get(i), fenToTimers.get(fenHistory.get(i)));
            else
                mapFenToTimers.put(fenHistory.get(i), new long[]{2, 2});
        }
        themeId = _themeId;

        switch (gameMode) { // Determine game mode
            case "Two Player":
                playerTwo = new Player("Human", false); // Player two is human
                break;
            case "King Of The Hill":
                modeKingOfTheHill = true;
                playerTwo = new Player("Human", false); // Player two is human
                break;
            case "Blitz":
                modeBlitz = true;
                playerTwo = new Player("Human", false); // Player two is human
                break;
            case "AI Easy":
                playerTwo = new Player("AI", false);
                thinkTime = 1; // AI ThinkTime 1ms
                thinkDepth = 1; // Thinks only one move
                level = 0; // Minimum level
                break;
            case "AI Medium":
                playerTwo = new Player("AI", false); // Can be set as "AI" or "Human"
                thinkTime = 3; // AI ThinkTime 3ms
                thinkDepth = 3; // Thinks only three moves ahead
                level = 0; // Minimum level
                break;
            case "AI Hard":
                playerTwo = new Player("AI", false); // Can be set as "AI" or "Human"
                thinkTime = 5; // AI ThinkTime 1ms
                thinkDepth = 5; // Thinks only one move
                level = 5; // Level 5
                break;
            case "AI Insane":
                playerTwo = new Player("AI", false); // Can be set as "AI" or "Human"
                thinkTime = 100; // AI ThinkTime 0.1s
                thinkDepth = 20; // Think plenty of moves ahead
                level = 20; // Maximum level
                break;

        }
        board = new Board(); // Initialized board
        textureIdToPiece = fenParser.setupFromFen(startingFenString, playerOne, playerTwo, board); // Get latest textureId&Piece pairs
        if (!fenList.contains(startingFenString)) {
            fenList.add(startingFenString); // Add starting board layout to fenList
            mapFenToTimers.put(startingFenString, new long[]{_timeLimit, _timeLimit});
        }
        // Set enPassSquare from startingFen
        String enPassSquare = fenParser.getEnPassSquare(startingFenString);
        if (enPassSquare != null) {
            board.getSquare(enPassSquare).setEnPassSquare();
            if (enPassSquare.charAt(1) == '3')// Pawn on row 4
                ((Pawn) board.getSquare(enPassSquare.charAt(0) + "" + '4').getPiece()).setEnPassPiece();
            else // Pawn on row 5
                ((Pawn) board.getSquare(enPassSquare.charAt(0) + "" + '5').getPiece()).setEnPassPiece();
        }
        // Set turn from startingFen
        turn = fenParser.getTurn(_startingFenString);
        // Set timers from mapFenToTimers to latest values
        playerOne.setTimer(mapFenToTimers.get(fenList.get(fenList.size() - 1))[0]);
        playerTwo.setTimer(mapFenToTimers.get(fenList.get(fenList.size() - 1))[1]);
        if (turn) playerOne.startTimer(); // Start player's timer
        else playerTwo.startTimer();
        timerRunning = true;
        if(!PreviousFenlist.getStatus() && !turn && !playerTwo.isHuman()) aiMove(); // Game continued from specific FEN-String + it's AI game + AI's turn -> Make move
    }

    public static GameController getInstance() {
        /** ERROR LOG **/if (instance == null) Log.e("GameController", "getInstance null");
        return instance;
    }

    // Boolean so that method can be broke if needed - see "return false" -statements
    // Graphics are calling this method for every square click that is made
    public boolean selectSquare(String _square) {

        clickedSquare = _square; // update attribute - clickedSquare is used in other methods in this class

        if(modeKingOfTheHill)
            checkKingOfTheHill();

        if (checkKingsForCheckmate()) // Check if either of kings  are in checkmate
            return true; // One king in checkmate, break here

        if (checkForStalemate())    //check if either player is in stalemate
            return true;    //if player cannot move, stalemate

        if (!turn && !playerTwo.isHuman()) // AI clicks
            checkAiClick(clickedSquare);

        if (pawnPromoting) { // Check if pawn promote is on
            if (processPromoting(clickedSquare) && !turn && !playerTwo.isHuman()) // Process users piece choosing
                aiMove(); // If turn was for AI, make its move
            else
                return false; // Break function here
        }

        if (checkClickMovement()) {  // Allow piece movement to highlighted squares
            if (turn) {
                playerOne.resumeTimer();
                playerTwo.pauseTimer();
            } else {
                playerOne.pauseTimer();
                playerTwo.resumeTimer();
            }
            if (!turn && !playerTwo.isHuman())
                aiMove(); // PlayerOne made a movement, make AI make the next move
            return true;
        }
        // Check if click is OutOfBoard, empty square or same as on the same piece as before
        if (!checkSquare(clickedSquare)) // True means new piece was clicked
            return false; // Break function here

        // When in check, only allowed pieces are allowed to be selected
        if ((kingInCheck(playerOne) || kingInCheck(playerTwo)) && !allowedPieces.contains(board.getSquare(clickedSquare).getPiece())) {
            /** ERROR LOG **/
            Log.e("CheckAllowedPiece", board.getSquare(clickedSquare).getPiece().getPieceType() + " not allowed");
            return false;
        }

        // Get new piece
        selectedPiece = board.getSquare(clickedSquare).getPiece();

        // Check if clicked piece belongs to other player and break here
        if (turn && selectedPiece.getPlayer().equals(playerTwo) ||
                !turn && selectedPiece.getPlayer().equals(playerOne)) {
            selectedPiece = null; // Reset selectedPiece back to null
            return false; // Break this function
        }

        // Get valid moves and captures and highlight them
        processMovements();

        highlightsOff(); // Reset all the highlights
        return true;
    }

    // Makes checks for stalemate. Checks if king or other pieces can move
    // totals up moves for each player on their turn
    private boolean stalemate(Player player) {

        int total = 0;  //total moves
        Piece tempPiece;
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                if (board.getSquare((char) ((int) 'A' + x) + "" + (y + 1)).getPiece() != null) {  //if square has a piece
                    //storing current square's piece into tempPiece
                    tempPiece = board.getSquare((char) ((int) 'A' + x) + "" + (y + 1)).getPiece();
                    if (tempPiece.getPieceType().equals("King") && tempPiece.getPlayer().equals(player)) {    //if piece is a king
                        // Check if king has any moves
                        List<Square> kingMovements = board.getValidMoves(player.getPieceByType("King"))[0];
                        List<Square> kingCaptureMoves = board.getValidMoves(player.getPieceByType("King"))[1];
                        preventExposeMove(player.getPieceByType("King"), kingMovements, false); // Process king's movements
                        preventExposeMove(player.getPieceByType("King"), kingCaptureMoves, false);
                        //add up remaining moves for king
                        total += kingMovements.size();
                        total += kingCaptureMoves.size();
                    } else if (!tempPiece.getPieceType().equals("King") && tempPiece.getPlayer().equals(player)) {    //else if piece is not king
                        bothSquareLists = board.getValidMoves(tempPiece);   //get movements
                        board.checkPinningMoves(bothSquareLists, tempPiece);    //check if pinned
                        //add up remaining moves
                        total += bothSquareLists[0].size();
                        total += bothSquareLists[1].size();
                    }
                }
        return (total < 1); //if 0 moves exist, stalemate is true
    }

    private boolean insufficientMaterial(Player player) {
        //temporary storage of pieceLists
        List<Piece>[] pieces = new List[2];
        pieces[0] = playerOne.getPieceList();   //player 1 pieces
        pieces[1] = playerTwo.getPieceList();   //player 2 pieces

        //check if only kings left on the board
        if (pieces[0].size() == 1 && pieces[1].size() == 1)
            return true;

        //other default draws
        if (player.getPieceList().size() == 1) {  //if player has only king, check who he is
            if (player.isFirst())   //if its player 1
                if (pieces[1].size() == 2)   //check if player two has only 2 pieces left
                    for (int i = 0; i < pieces[1].size(); i++) {
                        if (pieces[1].get(i).getPieceType().equals("Bishop"))  //if one of the pieces is a bishop
                            return true;
                        if (pieces[1].get(i).getPieceType().equals("Knight"))  //if one of the pieces is a knight
                            return true;
                    }
                else //if its player 2
                    if (pieces[0].size() == 2)   //check if player one has only 2 pieces left
                        for (int i = 0; i < pieces[0].size(); i++) {
                            if (pieces[0].get(i).getPieceType().equals("Bishop")) //if one of the pieces is a bishop
                                return true;
                            if (pieces[0].get(i).getPieceType().equals("Knight")) //if one of the pieces is a knight
                                return true;
                        }
        }
        return false;
    }

    // Called when board layout has changed in game logic
    private void updateFen() {
        if (!fenList.contains(fenString = fenParser.refreshFen(board, turn, playerOne, playerTwo, board.getEnPassSquare()))) {
            fenList.add(fenString); // Add latest FEN-String
            mapFenToTimers.put(fenString, new long[]{playerOne.getTimer(), playerTwo.getTimer()});
            Log.d("updateFen", "Map size: " + mapFenToTimers.size() + ". FenList size: " + fenList.size());
        }
        if(modeBlitz)
            setBlitzTimers();
    }

    /* Method to check for 50 move rule
     * 1. if each player has made 50 moves each
     * 2. no pieces have been captured
     * 3. no pawns have been advanced
     * Game results in stalemate
     * NOTE: if boolean captureReset is true, auto reset counter, else check for pawn moves */
    private void checkDraw(Piece _piece, Square _square, boolean captureReset) {
        //if movement was a capture movement, immediately reset
        if (captureReset)
            drawCounter = 0;
        else {
            if (_piece.getPieceType().equals("Pawn")) //check if piece moved is a pawn
                drawCounter = 0;
            else if (squareListTwo.contains(board.getSquare(_square.getId())))//then check if move was a capture movement(just in case)
                drawCounter = 0;
            else { //if neither, increase counter
                drawCounter++;
                if (drawCounter == 100)
                    GameActivity.setEnding(3); // Draw
                else
                    GameActivity.setEnding(0); // Not draw
            }
        }
    }


    // Refresh textureId&Piece pairs using current FEN-string of board
    public void updateTextureIdToPiece() {
        textureIdToPiece.clear();
        fenString = fenParser.refreshFen(board, turn, playerOne, playerTwo, board.getEnPassSquare());
        textureIdToPiece = fenParser.setupFromFen(fenString, playerOne, playerTwo, board);
    }

    // This is only called by PreviousFenlist.java activity
    // Used to refresh board into given FEN-String board layout
    public void previewFen(String _fenString) {
        textureIdToPiece.clear();
        board.clearBoard();
        fenString = _fenString;
        textureIdToPiece = fenParser.setupFromFen(fenString, playerOne, playerTwo, board);
        graphics.refresh(); // Refresh graphics
    }

    public Map<Integer, Piece> getTextureIdToPiece() {
        return textureIdToPiece;
    }

    public boolean getTurn() {
        return turn;
    }

    public long[] getTimers() {
        return new long[]{playerOne.getTimer(), playerTwo.getTimer()};
    }

    public List<String> getFenList() {
        Log.d("getFenList", "Size: " + fenList.size());
        return fenList;
    }

    public Map<String, long[]> getMapFenToTimers() {
        Log.d("getMapFenToTimers", "Size: " + mapFenToTimers.size());
        return mapFenToTimers;
    }

    public int getThemeId() {
        return themeId;
    }

    // When starting from FEN that has 'b' as turn board will be rotated in two player games
    public boolean initialRotate() {
        return playerTwo.isHuman() && !turn;
    }

    int aiRequestRound = 0;
    public void aiMove() {
        // Since this function is called inside the selectSquare() after playerOne made the move, it cannot be interrupted by tapping screen multiple time
        highlightsOff();
        String clickedSquareOne;
        String clickedSquareTwo;
        char pawnPromoteClick = 0;
        boolean movementMade = false;
        int _thinkTime = thinkTime; // Use default think time first, increase if needed
        do {
            if (!playerTwo.isHuman() && !turn) { // AI's turn
                highlights.clear();
                selectedPiece = null;
                fenString = fenParser.refreshFen(board, turn, playerOne, playerTwo, board.getEnPassSquare()); // Get fresh FEN-string
                Log.d("FEN-String", " \n" + fenString);
                if (aiEngine == null)
                    aiEngine = new AiEngine(GameActivity.getDirectory() + "stockfish"); // Initialize AI Engine
                String bestMove = aiEngine.getAiMove(fenString, level, _thinkTime, thinkDepth); // Get best move from AI calculations
                if(bestMove.length()<4){
                    GameActivity.setEnding(4); // AI couldn't make a move with 15 thinking rounds
                    break;
                }
                clickedSquareOne = Character.toUpperCase(bestMove.charAt(0)) + "" + bestMove.charAt(1); // From square - Get piece from this one
                clickedSquareTwo = Character.toUpperCase(bestMove.charAt(2)) + "" + bestMove.charAt(3); // Target square - Move here
                if (bestMove.length() == 5) pawnPromoteClick = bestMove.charAt(4);
                selectSquare(clickedSquareOne); // AI makes "click" to select piece
                if (selectedPiece == null || // Invalid pieceSelect
                        !(board.getValidMoves(selectedPiece)[0].contains(board.getSquare(clickedSquareTwo))) && // Piece with no moves
                                !(board.getValidMoves(selectedPiece)[1].contains(board.getSquare(clickedSquareTwo)))) {
                    thinkTime++; // Give AI more time to think for a proper move
                    aiRequestRound ++;
                    Log.d("aiMove", "RequestRound: "+aiRequestRound);
                    selectedPiece = null; // Search for new piece
                    if(aiRequestRound>15) {
                        GameActivity.setEnding(4); // AI couldn't make a move with 15 thinking rounds
                        break;
                    }
                }
                if (selectedPiece != null)
                    movementMade = (selectSquare(clickedSquareTwo) || checkKingsForCheckmate()); // AI makes "click" to move the piece
            }// Movement made is boolean when selectSquare() return true (when valid move was made) or when checkmate
        } while (!movementMade); // Exit when AI got a proper move calculated

        if (pawnPromoteClick != 0) { //Convert AI's pawn promoting to square
            if (pawnPromoteClick == 'q') // Queen
                selectSquare("A4");
            if (pawnPromoteClick == 'r') // Rook
                selectSquare("C4");
            if (pawnPromoteClick == 'b') // Bishop
                selectSquare("E4");
            if (pawnPromoteClick == 'k') // Knight
                selectSquare("G4");
        }
        selectedPiece = null; // Move has been made, null the piece
        if (kingInCheck(playerOne) || kingInCheck(playerTwo)) // Highlight possible checked king
            checkKingsForCheckmate(); // Check for checkmate
        highlightsOff(); // Highlight
        aiRequestRound = 0; // Reset round counter
    }

    public void checkAiClick(String _clickedSquare) {
        // AI can give move "(none)" - in that case check new move. / com.theateam.checkmate I/Engine: bestmove (none)
        // Also when it's starting it can give odd messages - only allow it to give squares
        if (!((int) 'A' <= _clickedSquare.charAt(0) && _clickedSquare.charAt(0) >= (int) 'H') && // First char A-H
                !((int) '1' <= _clickedSquare.charAt(1) && _clickedSquare.charAt(1) >= (int) '8') && // Second char 1-8
                clickedSquare.length() != 2) { // And it has only two characters
            /** ERROR LOG **/Log.e("checkAiClick", "AI Clicks " + _clickedSquare);
            aiMove(); // Not a match -> ask new move
        }
    }

    public void movePiece(Piece _piece, Square target, Square from) {
        // Reset enPassSquare and piece every time a movement is done
        if (board.getEnPassPiece() != null) board.getEnPassPiece().setEnPassPiece();
        if (board.getEnPassSquare() != null) board.getEnPassSquare().setEnPassSquare();

        // When piece to be moved is pawn, and it does double step activate it as enPassPiece
        if (_piece.getPieceType().equals("Pawn") && ((from.getId().charAt(1) == '2' && target.getId().charAt(1) == '4'))) { // Row 2 to 4
            ((Pawn) _piece).setEnPassPiece();
            board.getSquare(from.getId().charAt(0) + "" + 3).setEnPassSquare(); // And square behind it as enPassSquare - Row 3
        } else if (_piece.getPieceType().equals("Pawn") && ((from.getId().charAt(1) == '7' && target.getId().charAt(1) == '5'))) { // Row 7 to 5
            ((Pawn) _piece).setEnPassPiece();
            board.getSquare(from.getId().charAt(0) + "" + 6).setEnPassSquare(); // Row 6
        }

        graphics.movePiece(_piece.getTextureId(), target.getId(), from.getId()); // Move piece in graphics
        from.setPiece(null); // Set old square empty
        _piece.setSquare(target); // Place piece to the new square
        if (_piece.getPieceType().equals("Rook"))
            ((Rook) _piece).cantCastle(); // When Rook is moved once, it cannot castle anymore
        if (_piece.getPieceType().equals("King"))
            ((King) _piece).cantCastle(); // Applies to kings also
    }

    // Check if new click was made to make selected piece to move or to eliminate a piece
    public boolean checkClickMovement() {
        if (selectedPiece != null && board.getSquare(clickedSquare) != null) { // Check that there was a piece selected and click is on the board
            if (squareList.contains(board.getSquare(clickedSquare))) { // Check if clicked square was valid move to empty square
                movePiece(selectedPiece, board.getSquare(clickedSquare), selectedPiece.getSquare()); // Move piece in game logic and graphics
                highlightsOff(); // Turn off all the highlights
                if (callForPromote()) // Check if made move allows pawn promoting
                    return false;
                turn = !turn;
                updateFen(); // Update FEN before changing board layout
                checkDraw(selectedPiece, board.getSquare(clickedSquare), false); //checks for 50 move rule
                if (selectedPiece.getPieceType().equals("King")) // Check castling for kings
                    if (castlingSquares.contains(board.getSquare(clickedSquare))) // Target square is one of the castling rules squares. Make move for rook also
                        movePiece(rookForSquare.get(board.getSquare(clickedSquare)), squareForRook.get(rookForSquare.get(board.getSquare(clickedSquare))), rookForSquare.get(board.getSquare(clickedSquare)).getSquare());
                // Check if the move caused check
                if (checkKingsForCheckmate() || checkForStalemate() || (modeKingOfTheHill && checkKingOfTheHill()))
                    return false; // One king in checkmate, break here
                checkRotating(); // Check if playerTwo is AI and rotate
                return true;
            } else if (squareListTwo.contains(board.getSquare(clickedSquare))) { // Check if clicked square was valid capture movement
                int tempPieceId; // Temporarily stores id for texture
                Square enPassSquare; // Replaces square when enPassant is active
                if (board.getSquare(clickedSquare).isEnPassSquare() && // Check if the square clicked is an enPassant move
                        selectedPiece.getPieceType().equals("Pawn")) { // And selected piece is pawn
                    if (selectedPiece.getPlayer().isFirst()) // Checks if player one
                        enPassSquare = board.getSquare(clickedSquare.charAt(0) + "" + 5); // Show piece on doubled move
                    else   // Or player 2
                        enPassSquare = board.getSquare(clickedSquare.charAt(0) + "" + 4); // Show piece on doubled move (other side of board)
                    tempPieceId = enPassSquare.getPiece().getTextureId();   //set texture id to the square with the piece on it
                    board.getEnPassPiece().getSquare().setPiece(null); // Eliminate piece from en pass square
                    movePiece(selectedPiece, board.getSquare(clickedSquare), selectedPiece.getSquare());  // Move piece to chosen square
                    graphics.eliminatePiece(tempPieceId, enPassSquare.getId()); // Eliminate old piece from graphics
                } else { // Normal capture move - not enPassantCapture
                    tempPieceId = board.getSquare(clickedSquare).getPiece().getTextureId(); // Catch old piece id safe temporary
                    board.getSquare(clickedSquare).getPiece().remove(false); // Eliminate old piece from game logic
                    movePiece(selectedPiece, board.getSquare(clickedSquare), selectedPiece.getSquare());  // Move piece to chosen square
                    graphics.eliminatePiece(tempPieceId, board.getSquare(clickedSquare).getId()); // Eliminate old piece from graphics
                }
                highlightsOff(); // Turn off all the highlights
                if (callForPromote()) // Check if made move allows pawn promoting
                    return false;
                turn = !turn;
                updateFen(); // Update FEN before changing board layout
                checkDraw(null, null, true); //checks for 50 move rule
                // Check if the move caused check
                if (checkKingsForCheckmate() || checkForStalemate() || (modeKingOfTheHill && checkKingOfTheHill()))
                    return false; // One king in checkmate, break here
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
        if (!selectedPiece.getPieceType().equals("King"))
            board.checkPinningMoves(bothSquareLists, selectedPiece); // Check piece and moves for pinning moves
        squareList = bothSquareLists[0]; // Valid moves
        squareListTwo = bothSquareLists[1]; // Valid capture moves

        // Revalidate king's movements - remove the ones that expose it
        if (selectedPiece.getPieceType().equals("King")) {
            castlingCheck();
            preventExposeMove(selectedPiece, squareList, true); // Check if valid moves expose king
            preventExposeMove(selectedPiece, squareListTwo, true); // Check if valid capture moves expose king
        }

        // When king is in check use list of allowed squares when picking valid moves and capture moves
        if ((kingInCheck(playerOne) || kingInCheck(playerTwo)) && !selectedPiece.getPieceType().equals("King")) { // Don't apply it to king
            for (int i = 0; i < squareList.size(); i++)
                if (!allowedSquares.contains(squareList.get(i))) {
                    squareList.remove(i);
                    i = -1; // Reset counter;
                }
            for (int i = 0; i < squareListTwo.size(); i++)
                if (!allowedSquares.contains(squareListTwo.get(i))) {
                    squareListTwo.remove(i);
                    i = -1; // Reset counter
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
            highlightsOff();
            return false;
        }

        // Check if there is a piece on the clicked square and enable it
        if (board.getSquare(_square).getPiece() == null) {
            selectedPiece = null; // Set as null, so that third press re-enables it again.
            highlightsOff();
            return false;
        }

        // Check if previously clicked piece is the same one and disable it
        if (selectedPiece != null && selectedPiece.equals(board.getSquare(_square).getPiece()) && !pawnPromoting) {
            selectedPiece = null;
            highlightsOff();
            return false;
        }

        // Check if previous click was on own piece, and new click on enemy piece that can't be captured
        if (selectedPiece != null && !selectedPiece.getPlayer().equals(board.getSquare(clickedSquare).getPiece().getPlayer())) {
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
    public boolean checkRotating() {
        if (playerTwo.isHuman()) { // Check if playerTwo is AI
            SystemClock.sleep(500); // Sleep 500ms to make rotating and highlightsOff smoother
            graphics.rotate(); // Rotate board and pieces
            selectedPiece = null;
            highlightsOff();
            return true;
        }
        return false;
    }

    // Check if pawn promoting is possible
    public boolean callForPromote() {
        if (selectedPiece != null && selectedPiece.getPieceType().equals("Pawn"))
            if (selectedPiece.getSquare().getId().charAt(1) == '1' ||
                    selectedPiece.getSquare().getId().charAt(1) == '8') {
                pawnPromoting = true;
                if (selectedPiece.getPlayer().isFirst())
                    graphics.pawnPromoteOn("PlayerOne"); // Tell graphics to setup window for pawn promoting
                else
                    graphics.pawnPromoteOn("PlayerTwo");
                return true; // Pawn promoting is possible
            }
        return false; // Pawn promoting not possible
    }

    public boolean getPawnPromoting() {
        return pawnPromoting;
    }

    // This is only called when user has to pick specific piece for pawn promoting
    public boolean processPromoting(String _square) {

        // Pause timers
        playerOne.pauseTimer();
        playerTwo.pauseTimer();

        Player playerChooser = playerOne; // Initialize as playerOne
        String textureName = ""; // See Coordinates.java for constructing this

        if (!selectedPiece.getPlayer().isFirst()) // Check player and reinitialize if needed
            playerChooser = playerTwo;

        Square tempSquare = selectedPiece.getSquare(); // Hold current square
        int tempTextureId = selectedPiece.getTextureId(); // Hold textureId of the piece

        // Determine which piece was clicked
        // Cases' squares are determined in pawn promote window
        // They are inverted in rotated view
        switch (_square) {
            case "A4":
            case "A5":
            case "B4":
            case "B5":
                if (!OpenGLRenderer.rotated)
                    textureName += "queen";
                else
                    textureName += "knight";
                break;

            case "C4":
            case "C5":
            case "D4":
            case "D5":
                if (!OpenGLRenderer.rotated)
                    textureName += "rook";
                else
                    textureName += "bishop";
                break;

            case "E4":
            case "E5":
            case "F4":
            case "F5":
                if (!OpenGLRenderer.rotated)
                    textureName += "bishop";
                else
                    textureName += "rook";
                break;

            case "G4":
            case "G5":
            case "H4":
            case "H5":
                if (!OpenGLRenderer.rotated)
                    textureName += "knight";
                else
                    textureName += "queen";
                break;

            // Click out of the window
            default:
                /** ERROR LOG **/Log.e("processPawnPromote", "Invalid click");
                return false; // Break here and check next click as before
        }

        // Set new piece to square
        switch (textureName) {
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
        if (tempSquare.getPiece().getPlayer().isFirst())
            textureName += "PlayerOne";
        else
            textureName += "PlayerTwo";

        graphics.pawnPromoteOff(); // New piece type has been chosen - close pawn promoting window
        graphics.eliminatePiece(selectedPiece.getTextureId(), tempSquare.getId()); // Vanish old piece from graphics
        tempSquare.getPiece().setListId(selectedPiece.getListId()); // Set listId from old piece to new one
        selectedPiece.remove(true); // Get rid of current pawn
        graphics.movePiece(tempSquare.getPiece().getTextureId(), tempSquare.getId(), tempSquare.getId()); // Place new piece to the square
        graphics.promotePawn(tempTextureId, textureName); // Change texture to match chosen piece
        turn = !turn; // Change turn
        checkRotating();
        highlightsOff();
        selectedPiece = null;
        pawnPromoting = false; // Turn off promoting for pawn
        updateFen(); // Update FEN since new piece on board
        // Resume timers
        if (turn) {
            playerOne.resumeTimer();
            playerTwo.pauseTimer();
        } else {
            playerOne.pauseTimer();
            playerTwo.resumeTimer();
        }
        return true;
    }

    // Check if king is in check
    // Compare enemy piece's capture range to kings location
    public boolean kingInCheck(Player _player) {

        List<Square> checkMoves;
        kingCapturePieces.clear(); // Pieces that could capture king
        Player enemy = playerTwo;
        if (!_player.isFirst())
            enemy = playerOne; // Enemy is always the other player

        for (int i = 0; i < enemy.getPieceList().size(); i++) {
            checkMoves = board.getValidMoves(enemy.getPieceList().get(i))[1]; // Get valid capture moves for piece
            for (int ii = 0; ii < checkMoves.size(); ii++)
                if (checkMoves.get(ii).equals(_player.getPieceByType("King").getSquare())) { // One of the capture movements matches to kings position - check
                    kingCapturePieces.add(enemy.getPieceList().get(i)); // Add piece to capturing pieces
                    highlights.add(new String[]{checkMoves.get(ii).getId(), "square", "red"}); // Highlight king
                    graphics.highlight(highlights); // Paint king red
                    return true; // Player's king is in check
                }
        }
        return false; // Player's king not in check
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

        // Check if any player's piece's capture movements contain enemy square
        for (int i = 0; i < kingCapturePieces.size(); i++) // Check all the pieces that can capture king (the ones that are causing check)
            if (!kingCapturePieces.get(i).getPlayer().equals(_player)) // Check pieces that belong to enemy
                for (int ii = 0; ii < _player.getPieceList().size(); ii++) { // Check all pieces for player
                    List<Square> captureList = board.getValidMoves(_player.getPieceList().get(ii))[1];
                    if (!_player.getPieceList().get(ii).getPieceType().equals("King") && // Don't apply this to king
                            captureList.contains(kingCapturePieces.get(i).getSquare())) { // Check if piece's capture moves contain enemy square
                        if (!allowedPieces.contains(_player.getPieceList().get(ii)))
                            allowedPieces.add(_player.getPieceList().get(ii)); // This is one of the pieces that is allowed to be moved
                        if (!allowedSquares.contains(kingCapturePieces.get(i).getSquare()))
                            allowedSquares.add(kingCapturePieces.get(i).getSquare()); // One of the allowed squares where to move
                        if (!capturePieces.contains(kingCapturePieces.get(i)))
                            capturePieces.add(kingCapturePieces.get(i)); // Update capturePieces list with new piece
                    }
                }

        if (capturePieces.size() == kingCapturePieces.size()) // All the enemy pieces that can capture king can be captured by own pieces
            canCaptureEnemy = true;

        // Check if any player's piece's valid movements contain any square between enemy and king
        for (int i = 0; i < kingCapturePieces.size(); i++) // Check all the pieces that can capture king (the ones that are causing check)
            if (!kingCapturePieces.get(i).getPlayer().equals(_player)) // Check pieces that belong to enemy
                for (int ii = 0; ii < _player.getPieceList().size(); ii++) { // Check all pieces for player
                    if (!_player.getPieceList().get(ii).getPieceType().equals("King")) {
                        int difVerticalKing = (int) _player.getPieceByType("King").getSquare().getId().charAt(0) - (int) kingCapturePieces.get(i).getSquare().getId().charAt(0); // Difference between king and enemy piece
                        int difHorizontalKing = (int) _player.getPieceByType("King").getSquare().getId().charAt(1) - (int) kingCapturePieces.get(i).getSquare().getId().charAt(1);
                        List<Square> moveList = board.getValidMoves(_player.getPieceList().get(ii))[0];

                        for (int iii = 0; iii < moveList.size(); iii++) { // Check all the moves for the piece
                            // Vertical check
                            if (difVerticalKing == 0 && moveList.get(iii).getId().charAt(0) == kingCapturePieces.get(i).getSquare().getId().charAt(0) && ( // Same column
                                    (int) kingCapturePieces.get(i).getSquare().getId().charAt(1) > (int) moveList.get(iii).getId().charAt(1) && // Enemy>Square>King
                                            (int) moveList.get(iii).getId().charAt(1) > (int) _player.getPieceByType("King").getSquare().getId().charAt(1) ||
                                            (int) kingCapturePieces.get(i).getSquare().getId().charAt(1) < (int) moveList.get(iii).getId().charAt(1) && // Enemy<Square<King
                                                    (int) moveList.get(iii).getId().charAt(1) < (int) _player.getPieceByType("King").getSquare().getId().charAt(1))) {
                                if (!allowedPieces.contains(_player.getPieceList().get(ii)))
                                    allowedPieces.add(_player.getPieceList().get(ii)); // This is one of the pieces that is allowed to be moved
                                if (!allowedSquares.contains(moveList.get(iii)))
                                    allowedSquares.add(moveList.get(iii)); // One of the allowed squares where to move
                                if (!capturePieces.contains(kingCapturePieces.get(i)))
                                    capturePieces.add(kingCapturePieces.get(i)); // Update capturePieces list with new piece
                            }

                            // Horizontal check
                            if (difHorizontalKing == 0 && moveList.get(iii).getId().charAt(1) == kingCapturePieces.get(i).getSquare().getId().charAt(1) && ( // Same row
                                    (int) kingCapturePieces.get(i).getSquare().getId().charAt(0) > (int) moveList.get(iii).getId().charAt(0) && // Enemy>Square>King
                                            (int) moveList.get(iii).getId().charAt(0) > (int) _player.getPieceByType("King").getSquare().getId().charAt(0) ||
                                            (int) kingCapturePieces.get(i).getSquare().getId().charAt(0) < (int) moveList.get(iii).getId().charAt(0) && // Enemy<Square<King
                                                    (int) moveList.get(iii).getId().charAt(0) < (int) _player.getPieceByType("King").getSquare().getId().charAt(0))) {
                                if (!allowedPieces.contains(_player.getPieceList().get(ii)))
                                    allowedPieces.add(_player.getPieceList().get(ii)); // This is one of the pieces that is allowed to be moved
                                if (!allowedSquares.contains(moveList.get(iii)))
                                    allowedSquares.add(moveList.get(iii)); // One of the allowed squares where to move
                                if (!capturePieces.contains(kingCapturePieces.get(i)))
                                    capturePieces.add(kingCapturePieces.get(i)); // Update capturePieces list with new piece
                            }

                            // Diagonal check
                            int difVerticalSquare = (int) kingCapturePieces.get(i).getSquare().getId().charAt(0) - (int) moveList.get(iii).getId().charAt(0);
                            int difHorizontalSquare = (int) kingCapturePieces.get(i).getSquare().getId().charAt(1) - (int) moveList.get(iii).getId().charAt(1);
                            if (Math.abs(difHorizontalKing) == Math.abs(difVerticalKing) &&  // Same row
                                    Math.abs(difHorizontalSquare) == Math.abs(difVerticalSquare) && (
                                    (int) kingCapturePieces.get(i).getSquare().getId().charAt(0) > (int) moveList.get(iii).getId().charAt(0) && // Enemy>Square>King
                                            (int) moveList.get(iii).getId().charAt(0) > (int) _player.getPieceByType("King").getSquare().getId().charAt(0) ||
                                            (int) kingCapturePieces.get(i).getSquare().getId().charAt(0) < (int) moveList.get(iii).getId().charAt(0) && // Enemy<Square<King
                                                    (int) moveList.get(iii).getId().charAt(0) < (int) _player.getPieceByType("King").getSquare().getId().charAt(0)) && (
                                    (int) kingCapturePieces.get(i).getSquare().getId().charAt(1) > (int) moveList.get(iii).getId().charAt(1) && // Enemy>Square>King
                                            (int) moveList.get(iii).getId().charAt(1) > (int) _player.getPieceByType("King").getSquare().getId().charAt(1) ||
                                            (int) kingCapturePieces.get(i).getSquare().getId().charAt(1) < (int) moveList.get(iii).getId().charAt(1) && // Enemy<Square<King
                                                    (int) moveList.get(iii).getId().charAt(1) < (int) _player.getPieceByType("King").getSquare().getId().charAt(1))) {
                                if (!allowedPieces.contains(_player.getPieceList().get(ii)))
                                    allowedPieces.add(_player.getPieceList().get(ii)); // This is one of the pieces that is allowed to be moved
                                if (!allowedSquares.contains(moveList.get(iii)))
                                    allowedSquares.add(moveList.get(iii)); // One of the allowed squares where to move
                                if (!capturePieces.contains(kingCapturePieces.get(i)))
                                    capturePieces.add(kingCapturePieces.get(i)); // Update capturePieces list with new piece
                            }
                        }
                    }
                }

        // Check if allowedPieces have actually moves that could be done
        for (int i = 0; i < allowedPieces.size(); i++) {
            List<Square>[] allMoves = board.getValidMoves(allowedPieces.get(i)); // Get moves for the piece
            board.checkPinningMoves(allMoves, allowedPieces.get(i)); // Get rid of pinning moves

            for (int ii = 0; ii < allMoves[0].size(); ii++) // Check all valid moves
                if (!allowedSquares.contains(allMoves[0].get(ii))) { // Remove all but allowed squares
                    allMoves[0].remove(ii);
                    ii = -1; // Reset
                }
            for (int ii = 0; ii < allMoves[1].size(); ii++) // Check all the capture moves
                if (allMoves[1].size() > ii && !allowedSquares.contains(allMoves[1].get(ii))) {
                    allMoves[1].remove(ii);
                    ii = -1; // Reset
                }
            if (allMoves[0].size() == 0 && allMoves[1].size() == 0) { // Check if piece has any valid moves left
                allowedPieces.remove(i);
                i = -1; // Reset
            }
        }
        allowedPieces.add(_player.getPieceByType("King")); // King is always able to be selected

        for (int i = 0; i < allowedPieces.size(); i++) // Highlight allowed pieces
            if (!allowedPieces.get(i).getPieceType().equals("King")) // Keep king highlighted as red
                highlights.add(new String[]{allowedPieces.get(i).getSquare().getId(), "square", "blue"});

        if (capturePieces.size() == kingCapturePieces.size()) // Can move between all the pieces
            canMoveBetween = true;

        // Check if king has any moves
        List<Square> kingMovements = board.getValidMoves(_player.getPieceByType("King"))[0];
        List<Square> kingCaptureMoves = board.getValidMoves(_player.getPieceByType("King"))[1];

        preventExposeMove(_player.getPieceByType("King"), kingMovements, false); // Process king's movements
        preventExposeMove(_player.getPieceByType("King"), kingCaptureMoves, false);

        if (kingMovements.size() != 0 || kingCaptureMoves.size() != 0)
            kingHasMoves = true;

        return !canCaptureEnemy && !canMoveBetween && !kingHasMoves || // If any of these is true it's not checkmate
                capturePieces.size() > 1 && !kingHasMoves; // Or if king has no movements and it's exposed by more than one piece
    }

    public boolean checkKingsForCheckmate() {
        if (kingInCheck(playerOne))
            if (turn && kingInCheckmate(playerOne)) {
                GameActivity.setCheckmate(1);
                return true;
            }
        if (kingInCheck(playerTwo))
            if (!turn && kingInCheckmate(playerTwo)) {
                GameActivity.setCheckmate(2);
                return true;
            }
        GameActivity.setCheckmate(0); // No checkmate
        return false;
    }

    public boolean checkForStalemate() {
        if (turn && stalemate(playerOne) || !turn && stalemate(playerTwo)) {
            GameActivity.setEnding(1);
            return true;
        }
        if (turn && insufficientMaterial(playerOne) || !turn && insufficientMaterial(playerTwo)) {
            GameActivity.setEnding(3);
            return true;
        }
        GameActivity.setEnding(0); // No stalemate
        return false;
    }

    public boolean checkKingOfTheHill(){
        if(!modeKingOfTheHill)
            return false;
        List<Square> hillSquares = new Vector<>();
        hillSquares.add(board.getSquare("D4"));
        hillSquares.add(board.getSquare("D5"));
        hillSquares.add(board.getSquare("E4"));
        hillSquares.add(board.getSquare("E5"));
        if(hillSquares.contains(playerOne.getPieceByType("King").getSquare())){
            GameActivity.setKingOfTheHill(1);
            return true;
        }
        if(hillSquares.contains(playerTwo.getPieceByType("King").getSquare())){
            GameActivity.setKingOfTheHill(2);
            return true;
        }
        return false;
    }

    public boolean setBlitzTimers(){
        if(!modeBlitz) // Wrong call
            return false;
        playerOne.pauseTimer();
        playerTwo.pauseTimer();
        playerOne.setTimer(120); // 2 min timers
        playerTwo.setTimer(120);
        GameActivity.updateTimer(playerOne.getTimer(), 1); // Refresh timers in GUI
        GameActivity.updateTimer(playerTwo.getTimer(), 2);
        return true;
    }

    // Check if king's valid moves are exposed
    // I.e. Check if moving king to A4 makes it exposed to enemy pieces' captures.
    // Also works for capture movements. Checks if king becomes exposed after eliminating enemy piece
    public void preventExposeMove(Piece _piece, List<Square> _moveList, boolean highLightOn) {
        List<Square> preventMoves = new Vector<>();
        Player player = playerOne;
        int direction = 1; // Used for pawn
        if (_piece.getPlayer().isFirst()) {
            player = playerTwo;
            direction = -1;
        }

        for (int i = 0; i < player.getPieceList().size(); i++) { // Check all the enemy pieces
            // Pawns capture movements have to be constructed manually
            if (player.getPieceList().get(i).getPieceType().equals("Pawn")) {
                // Construct possible capture movements from pawns square
                String initialSquare = player.getPieceList().get(i).getSquare().getId();
                String captureOne = (char) ((int) initialSquare.charAt(0) + 1) + "" + (Integer.parseInt(initialSquare.charAt(1) + "") + direction);
                String captureTwo = (char) ((int) initialSquare.charAt(0) - 1) + "" + (Integer.parseInt(initialSquare.charAt(1) + "") + direction);
                Square captureSqrOne = board.getSquare(captureOne);
                Square captureSqrTwo = board.getSquare(captureTwo);
                if (captureSqrOne != null) // Squares are null when they are out of board, i.e. A9
                    preventMoves.add(captureSqrOne);
                if (captureSqrTwo != null)
                    preventMoves.add(captureSqrTwo);
            } else
                preventMoves = board.getValidMoves(player.getPieceList().get(i))[2]; // Other pieces can use getValidMoves()

            for (int ii = 0; ii < preventMoves.size(); ii++) {
                if (_moveList.contains(preventMoves.get(ii))) { // Check if enemy's valid moves match with king's valid moves
                    _moveList.remove(preventMoves.get(ii)); // Remove move when match
                    if (highLightOn)
                        highlights.add(new String[]{preventMoves.get(ii).getId(), "cross", "grey"}); // Highlight expose squares
                }
            }
        }
    }

    public boolean castlingCheck() {
        /**
         1. King cannot have made its first move
         2. Rook that is castling also cannot have made its first move
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
        castlingSquares.clear(); // BugFix for movePiece(), OK.

        Player enemy = playerTwo;
        if (selectedPiece.getPlayer().equals(enemy))
            enemy = playerOne;

        // Apply this function only for kings that can castle
        if (!selectedPiece.getPieceType().equals("King") ||
                !((King) selectedPiece).checkCastling() ||
                kingInCheck(selectedPiece.getPlayer()))
            return false;

        // Initialize squares for player one
        Square rookOneSquare = board.getSquare("A1");
        Square rookTwoSquare = board.getSquare("H1");
        Square castleSquareOne = board.getSquare("C1");
        Square castleSquareTwo = board.getSquare("G1");
        Square rookMoveOneSquare = board.getSquare("D1");
        Square rookMoveTwoSquare = board.getSquare("F1");
        if (selectedPiece.getPlayer().equals(playerTwo)) { // Change values for player two
            rookOneSquare = board.getSquare("A8");
            rookTwoSquare = board.getSquare("H8");
            castleSquareOne = board.getSquare("C8");
            castleSquareTwo = board.getSquare("G8");
            rookMoveOneSquare = board.getSquare("D8");
            rookMoveTwoSquare = board.getSquare("F8");
            if (board.getSquare("B8").getPiece() != null || // Squares between king and rook have to be empty
                    board.getSquare("C8").getPiece() != null ||
                    rookMoveOneSquare.getPiece() != null)
                castleSquareOne = null;
            if (rookMoveTwoSquare.getPiece() != null ||
                    board.getSquare("G8").getPiece() != null)
                castleSquareTwo = null;
        } else {
            if (board.getSquare("B1").getPiece() != null ||
                    board.getSquare("C1").getPiece() != null ||
                    rookMoveOneSquare.getPiece() != null)
                castleSquareOne = null;
            if (rookMoveTwoSquare.getPiece() != null ||
                    board.getSquare("G1").getPiece() != null)
                castleSquareTwo = null;
        }
        if (rookOneSquare.getPiece() == null || // If rookSquare has no rook set castleSquare as null
                !rookOneSquare.getPiece().getPieceType().equals("Rook") ||
                !((Rook) rookOneSquare.getPiece()).checkCastling())
            castleSquareOne = null;

        if (rookTwoSquare.getPiece() == null ||
                !rookTwoSquare.getPiece().getPieceType().equals("Rook") ||
                !((Rook) rookTwoSquare.getPiece()).checkCastling())
            castleSquareTwo = null;

        // Check if any of the squares are exposed by enemy piece
        for (int i = 0; i < enemy.getPieceList().size(); i++) {
            List<Square> movementList = board.getValidMoves(enemy.getPieceList().get(i))[0]; // EnemyPiece's movements
            List<Square> captureList = board.getValidMoves(enemy.getPieceList().get(i))[1];  //              captures
            if (enemy.equals(playerTwo)) {
                if (movementList.contains(board.getSquare("C1")) ||
                        movementList.contains(board.getSquare("D1")))
                    castleSquareOne = null;
                if (movementList.contains(board.getSquare("F1")) ||
                        movementList.contains(board.getSquare("G1")))
                    castleSquareTwo = null;
            } else {
                if (movementList.contains(board.getSquare("C8")) ||
                        movementList.contains(board.getSquare("D8")))
                    castleSquareOne = null;
                if (movementList.contains(board.getSquare("F8")) ||
                        movementList.contains(board.getSquare("G8")))
                    castleSquareTwo = null;
            }
        }

        if (castleSquareOne == null && castleSquareTwo == null)
            return false; // There are no valid castlingSquares, end here

        if (castleSquareOne != null) { // squareOne is valid
            squareList.add(castleSquareOne); // Add extra move for king
            castlingSquares.add(castleSquareOne); // Save square to other list - it's used for comparing in checkClickMovement()
            rookForSquare.put(castleSquareOne, (Rook) rookOneSquare.getPiece()); // Save pair of king's target square and rook
            squareForRook.put((Rook) rookOneSquare.getPiece(), rookMoveOneSquare); // Save pair of rook and its target square
        }
        if (castleSquareTwo != null) {
            squareList.add(castleSquareTwo);
            castlingSquares.add(castleSquareTwo);
            rookForSquare.put(castleSquareTwo, (Rook) rookTwoSquare.getPiece());
            squareForRook.put((Rook) rookTwoSquare.getPiece(), rookMoveTwoSquare);
        }
        return true; // Castling can be done
    }

    // Used to give graphics squares' of all the pieces
    public String getPieceLayout() {
        String returnString = "";
        updateTextureIdToPiece(); // Get latest textureID&Piece pairs
        for (int i = 51; i < 83; i++) {
            if (textureIdToPiece.get(i) != null) // Piece is not eliminated
                returnString += textureIdToPiece.get(i).getSquare().getId() + " "; // Piece's square
            else
                returnString += "empty "; // Eliminated piece is empty square
        }
        return returnString;
    }

    // Called from GameActivity ONLY
    // Undo previous move and setup board with previous FEN
    public boolean undoMove() {
        if (fenList.size() == 1) // No moves has been made
            return false; // Unable to undo move
        playerOne.pauseTimer();
        playerTwo.pauseTimer();
        mapFenToTimers.remove(fenList.get(fenList.size() - 1)); // Remove timers of fen
        fenList.remove(fenList.size() - 1); // Remove current layout
        graphics.pawnPromoteOff(); // Remove pawn promoting window if it's visible
        if (!pawnPromoting) // Don't change turn when pawn promoting was going on
            turn = !turn; // Revert turn
        highlights.clear(); // Clear old highlights
        fenString = fenList.get(fenList.size() - 1); // Get old FEN-string
        playerOne.setTimer(mapFenToTimers.get(fenString)[0]);
        playerTwo.setTimer(mapFenToTimers.get(fenString)[1]);
        GameActivity.updateTimer(playerOne.getTimer(), 1);
        GameActivity.updateTimer(playerTwo.getTimer(), 2);
        String enPassSquareId = ""; // Holds enPassSquare's ID from previous FEN temporary
        if (fenString.charAt(fenString.length() - 5) != '-')
            enPassSquareId = board.getSquare(Character.toUpperCase(fenString.charAt(fenString.length() - 6)) + "" + Integer.parseInt(fenString.charAt(fenString.length() - 5) + "")).getId(); // Character index for enPassSquare in FEN
        textureIdToPiece.clear(); // Clear old textureId&Piece pairs
        board.clearBoard(); // Clear board from pieces and squares - after this whole board is empty!
        textureIdToPiece = fenParser.setupFromFen(fenString, playerOne, playerTwo, board); // Get latest textureId&Piece pairs using new FEN-string
        graphics.refresh(); // Refresh graphics
        if (!enPassSquareId.equals("") &&  // Previous FEN had enPassSquare - Set it aga enPassSquare
                !fenString.equals(startingFenString)) {
            board.getSquare(enPassSquareId).setEnPassSquare(); // Set enPassSquare
            if (enPassSquareId.charAt(1) == '3')  // PlayerOne Pawn
                ((Pawn) board.getSquare(enPassSquareId.charAt(0) + "" + '4').getPiece()).setEnPassPiece(); // Pawn at row 4
            else  // PlayerTwo Pawn
                ((Pawn) board.getSquare(enPassSquareId.charAt(0) + "" + '5').getPiece()).setEnPassPiece(); // Pawn at Row 5
        }
        if (!turn && !playerTwo.isHuman()) // Opponent is AI
            undoMove(); // When playing against AI, undo 1st AI's latest move, then players own move
        highlightsOff();
        if (!pawnPromoting) // Don't rotate when pawnPromoting is on
            checkRotating();
        pawnPromoting = false; // Turn off promoting for pawn
        selectedPiece = board.checkPromoteRows(); // Check if undo a move caused pawn promoting
        if (selectedPiece != null) // Piece null when pawn promoting is off
            if (!callForPromote()) { // False when pawn promoting not possible
                checkRotating(); // No promoting - rotate board
                turn = !turn; // And flip turn
            }
        // Fix conflicts with rotated board
        if ((OpenGLRenderer.rotated && turn) || (!OpenGLRenderer.rotated && !turn && playerTwo.isHuman()))
            graphics.rotate();
        if (checkForStalemate())
            checkKingsForCheckmate();
        updateFen(); // Add latest FEN
        if (turn) playerOne.resumeTimer();
        else playerTwo.resumeTimer();
        return true; // Move was undone
    }

    public String getHelpMove(){
        if (aiEngine == null) aiEngine = new AiEngine(GameActivity.getDirectory() + "stockfish"); // Initialize AI Engine
        String move = aiEngine.getAiMove(fenList.get(fenList.size()-1), 20, 500, 20);
        Log.d("getHelpMove", move);
        if(move.length()!=4) return "No help move available";
        if(Character.isDigit(move.charAt(0)) || Character.isDigit(move.charAt(2)) ||
          !Character.isDigit(move.charAt(1)) || !Character.isDigit(move.charAt(3))) return "No help move available";
        Square from = board.getSquare(Character.toUpperCase(move.charAt(0))+""+Integer.parseInt(move.charAt(1)+""));
        Square target = board.getSquare(Character.toUpperCase(move.charAt(2))+""+Integer.parseInt(move.charAt(3)+""));
        if(from==null || target == null || from.getPiece()==null) return "No help move available";
        return from.getPiece().getPieceType()+" from "+from.getId()+ " to "+target.getId();
    }

    public void pauseGame() {
        playerOne.pauseTimer();
        playerTwo.pauseTimer();
        timerRunning = false;
    }

    public void resumeGame() {
        if (!timerRunning) {
            if (turn) playerOne.startTimer();
            else playerTwo.startTimer();
        }
    }
}