package com.theateam.checkmate;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by AriPerkkio on 21/02/16.
 *
 * Class to hold squares. See class diagram for detailed info
 *
 */
public class Board {

    // Attributes
    // References to other classes
    private Square[][] squareList = new Square[8][8];

    /************************************************
    Board array [X, Y]:
    8 [0,7] [1,7] [2,7] [3,7] [4,7] [5,7] [6,7] [7,7]
    7 [0,6] [1,6] [2,6] [3,6] [4,6] [5,6] [6,6] [7,6]
    6 [0,5] [1,5] [2,5] [3,5] [4,5] [5,5] [6,5] [7,5]
    5 [0,4] [1,4] [2,4] [3,4] [4,4] [5,4] [6,4] [7,4]
    4 [0,3] [1,3] [2,3] [3,3] [4,3] [5,3] [6,3] [7,3]
    3 [0,2] [1,2] [2,2] [3,2] [4,2] [5,2] [6,2] [7,2]
    2 [0,1] [1,1] [2,1] [3,1] [4,1] [5,1] [6,1] [7,1]
    1 [0,0] [1,0] [2,0] [3,0] [4,0] [5,0] [6,0] [7,0]
        A     B     C     D     E     F     G     H
    *************************************************/

    public Board(Player playerOne, Player playerTwo){

        // Initialize array
        for(int x=0;x<8;x++) {
            char startChar = (char) ((int) 'A' +x); // Using ascii values of characters
            for (int y = 0; y < 8; y++)
                squareList[x][y] = new Square(startChar+""+(y+1));
        }

        // Player One
        squareList[0][0].setPiece(new Rook(squareList[0][0], playerOne, playerOne.pieceIds.get("playerOne_rook_1")));
        squareList[1][0].setPiece(new Knight(squareList[1][0], playerOne, playerOne.pieceIds.get("playerOne_knight_1")));
        squareList[2][0].setPiece(new Bishop(squareList[2][0], playerOne, playerOne.pieceIds.get("playerOne_bishop_1")));
        squareList[3][0].setPiece(new Queen(squareList[3][0], playerOne, playerOne.pieceIds.get("playerOne_queen")));
        squareList[4][0].setPiece(new King(squareList[4][0], playerOne, playerOne.pieceIds.get("playerOne_king")));
        squareList[5][0].setPiece(new Bishop(squareList[5][0], playerOne, playerOne.pieceIds.get("playerOne_bishop_2")));
        squareList[6][0].setPiece(new Knight(squareList[6][0], playerOne, playerOne.pieceIds.get("playerOne_knight_2")));
        squareList[7][0].setPiece(new Rook(squareList[7][0], playerOne, playerOne.pieceIds.get("playerOne_rook_2")));
        squareList[0][1].setPiece(new Pawn(squareList[0][1], playerOne, playerOne.pieceIds.get("playerOne_pawn_1")));
        squareList[1][1].setPiece(new Pawn(squareList[1][1], playerOne, playerOne.pieceIds.get("playerOne_pawn_2")));
        squareList[2][1].setPiece(new Pawn(squareList[2][1], playerOne, playerOne.pieceIds.get("playerOne_pawn_3")));
        squareList[3][1].setPiece(new Pawn(squareList[3][1], playerOne, playerOne.pieceIds.get("playerOne_pawn_4")));
        squareList[4][1].setPiece(new Pawn(squareList[4][1], playerOne, playerOne.pieceIds.get("playerOne_pawn_5")));
        squareList[5][1].setPiece(new Pawn(squareList[5][1], playerOne, playerOne.pieceIds.get("playerOne_pawn_6")));
        squareList[6][1].setPiece(new Pawn(squareList[6][1], playerOne, playerOne.pieceIds.get("playerOne_pawn_7")));
        squareList[7][1].setPiece(new Pawn(squareList[7][1], playerOne, playerOne.pieceIds.get("playerOne_pawn_8")));
        // Player Two
        squareList[0][7].setPiece(new Rook(squareList[0][7], playerTwo, playerTwo.pieceIds.get("playerTwo_rook_1")));
        squareList[1][7].setPiece(new Knight(squareList[1][7], playerTwo, playerTwo.pieceIds.get("playerTwo_knight_1")));
        squareList[2][7].setPiece(new Bishop(squareList[2][7], playerTwo, playerTwo.pieceIds.get("playerTwo_bishop_1")));
        squareList[3][7].setPiece(new Queen(squareList[3][7], playerTwo, playerTwo.pieceIds.get("playerTwo_queen")));
        squareList[4][7].setPiece(new King(squareList[4][7], playerTwo, playerTwo.pieceIds.get("playerTwo_king")));
        squareList[5][7].setPiece(new Bishop(squareList[5][7], playerTwo, playerTwo.pieceIds.get("playerTwo_bishop_2")));
        squareList[6][7].setPiece(new Knight(squareList[6][7], playerTwo, playerTwo.pieceIds.get("playerTwo_knight_2")));
        squareList[7][7].setPiece(new Rook(squareList[7][7], playerTwo, playerTwo.pieceIds.get("playerTwo_rook_2")));
        squareList[0][6].setPiece(new Pawn(squareList[0][6], playerTwo, playerTwo.pieceIds.get("playerTwo_pawn_1")));
        squareList[1][6].setPiece(new Pawn(squareList[1][6], playerTwo, playerTwo.pieceIds.get("playerTwo_pawn_2")));
        squareList[2][6].setPiece(new Pawn(squareList[2][6], playerTwo, playerTwo.pieceIds.get("playerTwo_pawn_3")));
        squareList[3][6].setPiece(new Pawn(squareList[3][6], playerTwo, playerTwo.pieceIds.get("playerTwo_pawn_4")));
        squareList[4][6].setPiece(new Pawn(squareList[4][6], playerTwo, playerTwo.pieceIds.get("playerTwo_pawn_5")));
        squareList[5][6].setPiece(new Pawn(squareList[5][6], playerTwo, playerTwo.pieceIds.get("playerTwo_pawn_6")));
        squareList[6][6].setPiece(new Pawn(squareList[6][6], playerTwo, playerTwo.pieceIds.get("playerTwo_pawn_7")));
        squareList[7][6].setPiece(new Pawn(squareList[7][6], playerTwo, playerTwo.pieceIds.get("playerTwo_pawn_8")));

    }

    public Square getSquare(String _square){
        for(int x=0;x<8;x++)
            for(int y=0;y<8;y++){
                if(squareList[x][y].getId().equals(_square))
                    return squareList[x][y];
            }
        //Log.e("Board.getSquare", "Not found: "+_square);
        return null; // No square found - Error
    }


    // Returns [0] moves for empty squares, [1] movements for captures, [2] captures to expose king
    // Steps:
    // 1. Calculate squares within movement range to returnListOne and ones within capture range to returnlistTwo
    // 2. For pawns, recalculate captureMovements
    // 3. Check if calculated moves are valid by checking if there are pieces between the squares
    //    * First check vertical movement, up and down
    //    * Second check is for horizontal movements, left and right
    //    * Third check is for diagonal movements. Each of the four directions are checked individually
    // Exposed moves: 3. Same as 3. above, but ignore if the piece is king
    // TODO: Plenty of testing required here.

    public List<Square>[] getValidMoves(Piece _piece) {
        List<Square> returnListOne = new Vector<>(); // Holds movements for empty squares
        List<Square> returnListTwo = new Vector<>(); // Holds capture movements
        List<Square> returnListThree = new Vector<>(); // Holds squares that have player's own pieces - used for king expose movement calculation
        List<int[]> pieceMovements = new Vector<>(); // Holds pieces movement capabilities

        // Since pieceMovements list may be edited later we cannot use its reference
        // i.e. List<int[]> pieceMovements = _piece.getMovementList() is not allowed
        // Work-around is to loop through movementList and add them to pieceMovements list
        for(int i=0;i<_piece.getMovementList().size();i++)
            pieceMovements.add(_piece.getMovementList().get(i));

        char column; // As in 'A' - 'H'
        int row; // 1-8
        String fromSquare = _piece.getSquare().getId(); // Get piece's square
        int direction = 0; // Used for pawns to indicate movement direction
        if (!_piece.getPlayer().isFirst()) // Check if piece belongs to first player
            direction = -2; // Makes player two pawns go down
        // Check pawns for starting move - [0,2] moving.
        if (_piece.getPieceType().equals("Pawn") && // Pieces that are in x2 or x7 can move 2 steps - aka starting move
            (fromSquare.charAt(1)=='2' || fromSquare.charAt(1)=='7'))
            pieceMovements.add(new int[]{0, (2 + direction)}); // Add extra move to list

        // Calculate all the squares that are within piece's movement range
        for (int i = 0; i < pieceMovements.size(); i++) { // Check all the movements
            column = (char) ((int) fromSquare.charAt(0) + pieceMovements.get(i)[0]); // Calculate new column by adding initial column + movement's column
            row = Integer.parseInt("" + fromSquare.charAt(1)) + pieceMovements.get(i)[1]; // Calculate new row same way as above
            if (_piece.getPieceType().equals("Pawn")) // If it's pawn increase the row count by direction check number
                row = row + direction;
            if ((int) column >= (int) 'A' && (int) column <= (int) 'H' && row > 0 && row < 9) { // Check that new square is between A1-H8
                if (getSquare(column + "" + row).getPiece() == null) { // Check if square is empty
                    returnListOne.add(getSquare(column + "" + row)); // Add empty square to list
                    returnListThree.add(getSquare(column + "" + row)); // Add empty square to third list
                }
                else if (!getSquare(column + "" + row).getPiece().getPlayer().equals(_piece.getPlayer())) // Check if square has enemy piece in it
                    returnListTwo.add(getSquare(column + "" + row)); // Add enemy piece's square to other list
                else if (getSquare(column + "" + row).getPiece().getPlayer().equals(_piece.getPlayer()))
                    returnListThree.add(getSquare(column + "" + row)); // Add square with own piece to third list

            }
        }
        // For all the pawns capture movements are unique - so clear capture movements calculated above and get new ones
        if (_piece.getPieceType().equals("Pawn")) {
            returnListTwo.clear(); // Clear captures that were made above - pawns cant eliminate with [0,1] movement
            List<int[]> extraCaptures = ((Pawn) _piece).getCaptureMovement(); // Cast piece to pawn and get capture movements
            for (int i = 0; i < extraCaptures.size(); i++) { // Check all capture movements
                column = (char) ((int) fromSquare.charAt(0) + extraCaptures.get(i)[0]); // Calculate new column
                row = direction + Integer.parseInt("" + fromSquare.charAt(1)) + extraCaptures.get(i)[1]; // Calculate new row
                if ((int) column >= (int) 'A' && (int) column <= (int) 'H' && row > 0 && row < 9) { // Check that square is valid
                    // Check that square has enemy piece in it
                    if (getSquare(column + "" + row).getPiece() != null &&
                            !getSquare(column + "" + row).getPiece().getPlayer().equals(_piece.getPlayer()))
                        returnListTwo.add(getSquare(column + "" + row)); // Add enemy piece's square to list
                }
            }
        }

        List<Square> returnListFour = new Vector<>();
        for(int i=0;i<returnListThree.size(); i++)
            returnListFour.add(returnListThree.get(i));
        for(int i=0;i<returnListTwo.size(); i++)
            returnListFour.add(returnListTwo.get(i));

        returnListOne = checkMovementList(returnListOne, _piece, false); // Check if given squares are valid for movements
        returnListTwo = checkMovementList(returnListTwo, _piece, false); // Check if given capture movements are valid
        returnListThree = checkMovementList(returnListThree, _piece, true); // Check if given expose movements are valid


        return  new List[]{returnListOne, returnListTwo, returnListThree, returnListFour}; // return value [0] valid moves, [1] capture moves, [2] king's expose movements
    }


    // Isolated into its own function in order to use it for movements and capture moves
    // Function to check if given squareList has invalid movements - It checks if there are pieces between two squares
    // I.e. Checks if between A1 and A5 there is a piece in A3. If there is a piece, A5 is removed
    // boolean exposeMoves is true, when function is used to calculate kings exposing movements. Otherwise false
    // I.e. Expose movements check if kings capture movements cause it exposed
    public List<Square> checkMovementList(List<Square> _squareList, Piece _piece, boolean exposeMoves){
        char column; // As in 'A' - 'H'
        int row; // 1-8
        String fromSquare = _piece.getSquare().getId(); // Get piece's square

        // Check if there is a piece between valid move and current position - if true, remove the move
        for (int i = 0; i < _squareList.size(); i++) { // Test all moves

            column = _squareList.get(i).getId().charAt(0); // get current move column
            row = Integer.parseInt("" + _squareList.get(i).getId().charAt(1)); // get current move row
            char currentColumn = fromSquare.charAt(0); // get start square column
            int currentRow = Integer.parseInt(fromSquare.charAt(1) + ""); // get start square row
            int difRow = row - currentRow; // Calculate amount of difference between rows

            // Check and remove vertical movements
            for (int y = 1; y < Math.abs(difRow); y++) {
                // Moving from lower row number to higher one, i.e. 1-8
                if (row>(currentRow + y) && (currentRow + y)>currentRow ) { // Don't check squares that are not between the two
                    if (column == currentColumn && // In vertical movements columns match
                        getSquare(column + "" + (currentRow + y)) != null && // Check that given square is within range - board return null if square is not found
                        getSquare(column + "" + (currentRow + y)).getPiece() != null) {  // Check that there is a piece in the square
                        if(!exposeMoves || !getSquare(column + "" + (currentRow + y)).getPiece().getPieceType().equals("King")){ // For expose moves the squares with king are skipped
                            _squareList.remove(getSquare(column + "" + row)); // Remove square from list
                            i = -1; // Start looping from beginning again since size of list and order of items has changed. -1 since iteration increases it automatically to 0
                        }
                    }
                }
                // Moving from higher row number to lower one, i.e. 8-1
                if (row<(currentRow - y) && (currentRow - y)<currentRow ) { // Don't check squares that are not between the two
                    if (column == currentColumn && // In vertical movements columns match
                        getSquare(column + "" + (currentRow -y)) != null && // Check that given square is within range - board return null if square is not found
                        getSquare(column + "" + (currentRow -y)).getPiece() != null) {  // Check that there is a piece in the square
                        if(!exposeMoves || !getSquare(column + "" + (currentRow - y)).getPiece().getPieceType().equals("King")){ // For expose moves the squares with king are skipped
                            _squareList.remove(getSquare(column + "" + row)); // Remove square from list
                            i = -1; // Start looping from beginning again since size of list and order of items has changed. -1 since iteration increases it automatically to 0
                        }
                    }
                }
            }
            // Check and remove horizontal movements
            int difColumn = ((int) column - (int) currentColumn);
            for (int x = 1; x < Math.abs(difColumn); x++) {

                // Moving from H-A
                if ((int) currentColumn < ((int) currentColumn + x) && ((int) currentColumn + x)< (int) column ){ // Don't check squares that are not between the two
                    if (row == currentRow && // In horizontal movement rows are the same
                        getSquare((char) ((int) currentColumn + x) + "" + row) != null && // Moving right: check that square exists
                        getSquare((char) ((int) currentColumn + x) + "" + row).getPiece() != null) {// Moving right: Check square for a piece
                        if(!exposeMoves || !getSquare((char) ((int) currentColumn + x) + "" + row).getPiece().getPieceType().equals("King")){ // For expose moves the squares with king are skipped
                            _squareList.remove(getSquare(column + "" + row)); // Remove square from list
                            i = -1; // Start looping from beginning again since size of list and order of items has changed. -1 since iteration increases it automatically to 0
                        }
                    }
                }
                // Moving from A-H
                if ((int) currentColumn > ((int) currentColumn - x) && ((int) currentColumn - x)> (int) column ){ // Don't check squares that are not between the two
                    if (row == currentRow && // In horizontal movement rows are the same
                        getSquare((char) ((int) currentColumn - x) + "" + row) != null && // Moving right: check that square exists
                        getSquare((char) ((int) currentColumn - x) + "" + row).getPiece() != null) {// Moving right: Check square for a piece
                        if(!exposeMoves || !getSquare((char) ((int) currentColumn - x) + "" + row).getPiece().getPieceType().equals("King")){ // For expose moves the squares with king are skipped
                            _squareList.remove(getSquare(column + "" + row)); // Remove square from list
                            i = -1; // Start looping from beginning again since size of list and order of items has changed. -1 since iteration increases it automatically to 0
                        }
                    }
                }
            }
            // Check and remove diagonally movements
            if(Math.abs(difColumn)==Math.abs(difRow)){ // Difference between columns and rows are the same - it's diagonally
                for(int xy=1;xy<Math.abs(difColumn);xy++){
                    // Moving up-right, i.e. A1 - D4
                    if((int) currentColumn < (int) currentColumn+xy && (int) currentColumn+xy < (int) column // Column between current col and checking col
                       && currentRow < currentRow+xy && currentRow+xy < row){ // Row between current row and checking row
                        if(getSquare((char) (int) (currentColumn+xy)+""+(currentRow+xy))!= null && // Check if square is between A-H, 1-8
                           getSquare((char) (int) (currentColumn+xy)+""+(currentRow+xy)).getPiece()!=null){ // Check if there's a piece
                            if(!exposeMoves || !getSquare((char) (int) (currentColumn+xy)+""+(currentRow+xy)).getPiece().getPieceType().equals("King")){ // For expose moves the squares with king are skipped
                                _squareList.remove(getSquare(column + "" + row)); // Remove square from list
                                i = -1; // Start looping from beginning again since size of list and order of items has changed. -1 since iteration increases it automatically to 0
                            }
                        }
                    }
                    // Moving down-right, i.e. A8 - C6
                    if((int) currentColumn < (int) currentColumn+xy && (int) currentColumn+xy < (int) column // Column between current col and checking col
                       && currentRow > currentRow-xy && currentRow-xy > row){ // Row between current row and checking row
                        if(getSquare((char) (int) (currentColumn+xy)+""+(currentRow-xy))!= null && // Check if square is between A-H, 1-8
                           getSquare((char) (int) (currentColumn+xy)+""+(currentRow-xy)).getPiece()!=null){ // Check if there's a piece
                            if(!exposeMoves || !getSquare((char) (int) (currentColumn+xy)+""+(currentRow-xy)).getPiece().getPieceType().equals("King")){ // For expose moves the squares with king are skipped
                                _squareList.remove(getSquare(column + "" + row)); // Remove square from list
                                i = -1; // Start looping from beginning again since size of list and order of items has changed. -1 since iteration increases it automatically to 0
                            }
                        }
                    }
                    // Moving up-left, i.e. C6-A8
                    if((int) currentColumn > (int) currentColumn-xy && (int) currentColumn-xy > (int) column // Column between current col and checking col
                       && currentRow < currentRow+xy && currentRow+xy < row){ // Row between current row and checking row
                        if(getSquare((char) (int) (currentColumn-xy)+""+(currentRow+xy))!= null && // Check if square is between A-H, 1-8
                           getSquare((char) (int) (currentColumn-xy)+""+(currentRow+xy)).getPiece()!=null){ // Check if there's a piece
                            if(!exposeMoves || !getSquare((char) (int) (currentColumn-xy)+""+(currentRow+xy)).getPiece().getPieceType().equals("King")){ // For expose moves the squares with king are skipped
                                _squareList.remove(getSquare(column + "" + row)); // Remove square from list
                                i = -1; // Start looping from beginning again since size of list and order of items has changed. -1 since iteration increases it automatically to 0
                            }
                        }
                    }
                    // Moving down-left, i.e. C6-A8
                    if((int) currentColumn > (int) currentColumn-xy && (int) currentColumn-xy > (int) column // Column between current col and checking col
                       && currentRow > currentRow-xy && currentRow-xy > row){ // Row between current row and checking row
                        if(getSquare((char) (int) (currentColumn-xy)+""+(currentRow-xy))!= null && // Check if square is between A-H, 1-8
                           getSquare((char) (int) (currentColumn-xy)+""+(currentRow-xy)).getPiece()!=null){ // Check if there's a piece
                            if(!exposeMoves || !getSquare((char) (int) (currentColumn-xy)+""+(currentRow-xy)).getPiece().getPieceType().equals("King")){ // For expose moves the squares with king are skipped
                                _squareList.remove(getSquare(column + "" + row)); // Remove square from list
                                i = -1; // Start looping from beginning again since size of list and order of items has changed. -1 since iteration increases it automatically to 0
                            }
                        }
                    }
                }
            }
        }
        return _squareList;
    }

    public void checkPinningMoves(List<Square>[] _squareLists, Piece _selectedPiece){
        /*****
         [E] [0] [0] [P] [0] [K] [0] [0]
         Check if piece is between enemy piece and king in vertical/horizontal/diagonal lines
         Catch that piece and calculate its X-Ray capture moves
         If own king is in the range, remove all the moves expect the ones on the same line
         (Check piece and king position - Diagonal, horizontal, vertical -> check if pieces in the line
         - pick that piece and check its x-ray capture range!)

         1. If clicked piece is between enemy and king
         2. No other pieces between these three
         3. Enemy piece's x-ray moves contain king
         4. Remove other moves from the piece

         ******/

        List<Square> _squareListOne = _squareLists[0]; // Valid moves
        List<Square> _squareListTwo = _squareLists[1]; // Capture moves

        Square kingSquare = _selectedPiece.getPlayer().getPieceByType("King").getSquare(); // Player's own king is here
        Log.d("KingSquare", kingSquare.getId());
        Square pieceSquare = _selectedPiece.getSquare(); // Clicked piece is here
        Log.d("PieceSquare", pieceSquare.getId());
        char kingCol = kingSquare.getId().charAt(0); // Column where king is
        int kingRow = Integer.parseInt(kingSquare.getId().charAt(1)+""); // Row where king is
        int colDif = (int) pieceSquare.getId().charAt(0) - (int) kingSquare.getId().charAt(0); // Different between squares vertically
        int rowDif = Integer.parseInt(pieceSquare.getId().charAt(1)+"") - Integer.parseInt(kingSquare.getId().charAt(1)+""); // Difference horizontally
        // Vertical testing
        if(colDif==0){ // Same column = Vertical movement
            for(int i=1;i<=8;i++){ // Check all squares vertically
               squareVerticalCheckLoop:
                if(getSquare(pieceSquare.getId().charAt(0)+""+i).getPiece()!=null && // Check for a piece
                  !getSquare(pieceSquare.getId().charAt(0)+""+i).getPiece().getPlayer().equals(_selectedPiece.getPlayer())){ // Only enemy pieces

                    Square enemySquare = getSquare(pieceSquare.getId().charAt(0)+""+i); // Enemy piece is here
                    Log.d("EnemySquareV", enemySquare.getId()+" with "+enemySquare.getPiece().getPieceType());

                    // At this point enemy square is the closest enemy piece
                    // Check that selected piece is vertically between enemy and king
                    if((int) enemySquare.getId().charAt(1) > (int) pieceSquare.getId().charAt(1) && // [E] [0] [P] [0] [K] = E<P<K
                       (int) pieceSquare.getId().charAt(1) > (int) kingSquare.getId().charAt(1) ||
                       (int) enemySquare.getId().charAt(1) < (int) pieceSquare.getId().charAt(1) && // [K] [0] [P] [0] [E] = E>P>K
                       (int) pieceSquare.getId().charAt(1) < (int) kingSquare.getId().charAt(1)){

                        // Check that between piece and enemy piece there are no other pieces
                        int difPieceEnemy = Integer.parseInt(pieceSquare.getId().charAt(1)+"") - Integer.parseInt(enemySquare.getId().charAt(1)+"");
                        int difPieceKing = Integer.parseInt(pieceSquare.getId().charAt(1)+"") - Integer.parseInt(kingSquare.getId().charAt(1)+"");

                        Log.e("difPieceEnemy "+ difPieceEnemy, "difPieceKing "+ difPieceKing);

                        // EnemySquare < ClickedPiece < KingSquare
                        if((int) pieceSquare.getId().charAt(1) < (int) kingSquare.getId().charAt(1)) {
                            Log.d("1st","EnemySquare "+enemySquare.getId()+" PieceSquare "+pieceSquare.getId()+" KingSquare "+kingSquare.getId());
                            // Check that there are no pieces between enemy and selected piece
                            for (int ii = 1; ii < difPieceEnemy; ii++) {
                                Log.d("1.1", "Check "+kingCol + "" + (Integer.parseInt(pieceSquare.getId().charAt(1) + "") - ii));
                                if (getSquare(kingCol + "" + (Integer.parseInt(pieceSquare.getId().charAt(1) + "") - ii)).getPiece() != null)
                                    break squareVerticalCheckLoop;
                            }
                            // Check that there are no pieces between selected piece and king
                            for(int ii=1;ii < Math.abs(difPieceKing); ii++) {
                                Log.d("1.2", "Check " + kingCol + "" + (Integer.parseInt(pieceSquare.getId().charAt(1) + "") + ii));
                                if (getSquare(kingCol + "" + (Integer.parseInt(pieceSquare.getId().charAt(1) + "") + ii)).getPiece() != null)
                                    break squareVerticalCheckLoop;
                            }
                        }

                        // EnemySquare > ClickedPiece > KingSquare
                        if((int) pieceSquare.getId().charAt(1) > (int) kingSquare.getId().charAt(1)) {
                            Log.d("2nd","EnemySquare "+enemySquare.getId()+" PieceSquare "+pieceSquare.getId()+" KingSquare "+kingSquare.getId());
                            // Check that there are no pieces between enemy and clicked piece
                            for (int ii = 1; ii < Math.abs(difPieceEnemy); ii++) {
                                Log.d("2.1", "Check "+kingCol + "" + (Integer.parseInt(pieceSquare.getId().charAt(1) + "") + ii));
                                if (getSquare(kingCol + "" + (Integer.parseInt(pieceSquare.getId().charAt(1) + "") + ii)).getPiece() != null)
                                    break squareVerticalCheckLoop;
                            }
                            // Check that there are no pieces between selected piece and king
                            for (int ii = 1; ii < difPieceKing; ii++) {
                                Log.d("2.2", "Check "+kingCol + "" + (Integer.parseInt(pieceSquare.getId().charAt(1) + "") - ii));
                                if (getSquare(kingCol + "" + (Integer.parseInt(pieceSquare.getId().charAt(1) + "") - ii)).getPiece() != null)
                                    break squareVerticalCheckLoop;
                            }
                        }

                        List<Square> pinCaptures = getValidMoves(enemySquare.getPiece())[3]; // Get all the moves + captures for that piece
                        if(pinCaptures.contains(kingSquare)) { // Check if enemy piece's moves or captures have the square where king is
                            Log.d("PINNING","Enemy "+enemySquare.getPiece().getPieceType() + " at " + enemySquare.getId() +" and own " + _selectedPiece.getPieceType() + " at " + _selectedPiece.getSquare().getId() + " and king at " + kingSquare.getId());
                            // Recalculate valid moves based on pinning line
                            for(int y=0;y<_squareListOne.size();y++) {
                                if (_squareListOne.get(y).getId().charAt(0) != pieceSquare.getId().charAt(0)) {
                                    _squareListOne.remove(_squareListOne.get(y));
                                    y = -1; // Reset
                                }
                            }
                            // Recalculate valid moves based on pinning line
                            for(int y=0;y<_squareListTwo.size();y++) {
                                if (_squareListTwo.get(y).getId().charAt(0) != pieceSquare.getId().charAt(0)) {
                                    _squareListTwo.remove(_squareListTwo.get(y));
                                    y = -1; // Reset
                                }
                            }
                        }
                    }
                } // close squareCheckLoop
            }
        } // Close vertical testing
        // Horizontal testing
        if(rowDif==0){ // Same row = Horizontal movement
            for(int i=0;i<8;i++){ // Check all squares horizontally
                squareRowCheckLoop:
                if(getSquare((char)((int) 'A' +i)+""+kingRow).getPiece()!=null && // Check for a piece
                  !getSquare((char)((int) 'A' +i)+""+kingRow).getPiece().getPlayer().equals(_selectedPiece.getPlayer())){ // Only enemy pieces

                    Square enemySquare = getSquare((char)((int) 'A' +i)+""+kingRow); // Enemy piece is here
                    Log.d("EnemySquareH", enemySquare.getId()+" with "+enemySquare.getPiece().getPieceType());

                    // At this point enemy square is the closest enemy piece
                    // Check that selected piece is vertically between enemy and king
                    if((int) enemySquare.getId().charAt(0) > (int) pieceSquare.getId().charAt(0) && // [E] [0] [P] [0] [K] = E<P<K
                       (int) pieceSquare.getId().charAt(0) > (int) kingSquare.getId().charAt(0) ||
                       (int) enemySquare.getId().charAt(0) < (int) pieceSquare.getId().charAt(0) && // [K] [0] [P] [0] [E] = E>P>K
                       (int) pieceSquare.getId().charAt(0) < (int) kingSquare.getId().charAt(0)){
                        Log.d("#2", "EnemySquare "+enemySquare.getId());

                        // Check that between piece and enemy piece there are no other pieces
                        int difPieceEnemy = (int) pieceSquare.getId().charAt(0) - (int) enemySquare.getId().charAt(0);
                        int difPieceKing = (int) pieceSquare.getId().charAt(0) - (int) kingSquare.getId().charAt(0);

                        Log.e("difPieceEnemy "+ difPieceEnemy, "difPieceKing "+ difPieceKing);

                        // EnemySquare < ClickedPiece < KingSquare
                        if((int) pieceSquare.getId().charAt(0) < (int) kingSquare.getId().charAt(0)) {
                            Log.d("1st","EnemySquare "+enemySquare.getId()+" PieceSquare "+pieceSquare.getId()+" KingSquare "+kingSquare.getId());
                            // Check that there are no pieces between enemy and selected piece
                            for (int ii = 1; ii < difPieceEnemy; ii++) {
                                Log.d("1.1", "Check "+(char) ((int) pieceSquare.getId().charAt(0) - ii)+ "" + kingRow);
                                if (getSquare((char) ((int) pieceSquare.getId().charAt(0) - ii)+ "" + kingRow).getPiece() != null)
                                    break squareRowCheckLoop;
                            }
                            // Check that there are no pieces between selected piece and king
                            for(int ii=1;ii < Math.abs(difPieceKing); ii++) {
                                Log.d("1.2", "Check "+(char) ((int) pieceSquare.getId().charAt(0) + ii)+ "" + kingRow);
                                if (getSquare((char) ((int) pieceSquare.getId().charAt(0) + ii)+ "" + kingRow).getPiece() != null)
                                    break squareRowCheckLoop;
                            }
                        }

                        // EnemySquare > ClickedPiece > KingSquare
                        if((int) pieceSquare.getId().charAt(0) > (int) kingSquare.getId().charAt(0)) {
                            Log.d("2nd","EnemySquare "+enemySquare.getId()+" PieceSquare "+pieceSquare.getId()+" KingSquare "+kingSquare.getId());
                            // Check that there are no pieces between enemy and clicked piece
                            for (int ii = 1; ii < Math.abs(difPieceEnemy); ii++) {
                                Log.d("2.1", "Check "+(char) ((int) pieceSquare.getId().charAt(0) + ii)+ "" + kingRow);
                                if (getSquare((char) ((int) pieceSquare.getId().charAt(0) + ii)+ "" + kingRow).getPiece() != null)
                                    break squareRowCheckLoop;
                            }
                            // Check that there are no pieces between selected piece and king
                            for (int ii = 1; ii < difPieceKing; ii++) {
                                Log.d("2.2", "Check "+(char) ((int) pieceSquare.getId().charAt(0) - ii)+ "" + kingRow);
                                if (getSquare((char) ((int) pieceSquare.getId().charAt(0) - ii)+ "" + kingRow).getPiece() != null)
                                    break squareRowCheckLoop;
                            }
                        }

                        List<Square> pinCaptures = getValidMoves(enemySquare.getPiece())[3]; // Get all the moves + captures for that piece
                        if(pinCaptures.contains(kingSquare)) { // Check if enemy piece's moves or captures have the square where king is
                            Log.d("PINNING","Enemy "+enemySquare.getPiece().getPieceType() + " at " + enemySquare.getId() +" and own " + _selectedPiece.getPieceType() + " at " + _selectedPiece.getSquare().getId() + " and king at " + kingSquare.getId());
                            // Recalculate valid moves based on pinning line
                            for(int y=0;y<_squareListOne.size();y++) {
                                if (_squareListOne.get(y).getId().charAt(1) != pieceSquare.getId().charAt(1)) {
                                    _squareListOne.remove(_squareListOne.get(y));
                                    y = -1; // Reset
                                }
                            }
                            // Recalculate valid moves based on pinning line
                            for(int y=0;y<_squareListTwo.size();y++) {
                                if (_squareListTwo.get(y).getId().charAt(1) != pieceSquare.getId().charAt(1)) {
                                    _squareListTwo.remove(_squareListTwo.get(y));
                                    y = -1; // Reset
                                }
                            }
                        }
                    }
                } // close squareCheckLoop
            }
        } // Close horizontal testing
    }

    // Tester
    public void logBoardPrint() {

        for (int i = 7; i >= 0; i--) {
            int idOne = 0,
                    idTwo = 0,
                    idThree = 0,
                    idFour = 0,
                    idFive = 0,
                    idSix = 0,
                    idSeven = 0,
                    idEight = 0;

            if(squareList[0][i].getPiece()!= null)
                idOne = squareList[0][i].getPiece().getTextureId();
            if(squareList[1][i].getPiece()!= null)
                idTwo = squareList[1][i].getPiece().getTextureId();
            if(squareList[2][i].getPiece()!= null)
                idThree = squareList[2][i].getPiece().getTextureId();
            if(squareList[3][i].getPiece()!= null)
                idFour = squareList[3][i].getPiece().getTextureId();
            if(squareList[4][i].getPiece()!= null)
                idFive = squareList[4][i].getPiece().getTextureId();
            if(squareList[5][i].getPiece()!= null)
                idSix = squareList[5][i].getPiece().getTextureId();
            if(squareList[6][i].getPiece()!= null)
                idSeven = squareList[6][i].getPiece().getTextureId();
            if(squareList[7][i].getPiece()!= null)
                idEight = squareList[7][i].getPiece().getTextureId();

            Log.d("Board", "[" +
                    idOne + "] [" +
                    idTwo + "] [" +
                    idThree + "] [" +
                    idFour+ "] [" +
                    idFive + "] [" +
                    idSix + "] [" +
                    idSeven + "] [" +
                    idEight + "]");
        }
    }
}
