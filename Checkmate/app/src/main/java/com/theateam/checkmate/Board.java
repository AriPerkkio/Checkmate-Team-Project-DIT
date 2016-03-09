package com.theateam.checkmate;


import android.util.Log;

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


    // Returns [0] moves for empty squares, [1] movements for captures
    // Steps:
    // 1. Calculate squares within movement range to returnListOne and ones within capture range to returnlistTwo
    // 2. For pawns, recalculate captureMovements
    // 3. Check if calculated moves are valid by checking if there are pieces between the squares
    //    * First check vertical movement, up and down
    //    * Second check is for horizontal movements, left and right
    //    * Third check is for diagonal movements. Each of the four directions are checked individually
    // TODO: Plenty of testing required here.

    public List<Square>[] getValidMoves(Piece _piece) {
        List<Square> returnListOne = new Vector<>(); // Holds movements for empty squares
        List<Square> returnListTwo = new Vector<>(); // Holds capture movements
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
                if (getSquare(column + "" + row).getPiece() == null) // Check if square is empty
                    returnListOne.add(getSquare(column + "" + row)); // Add empty square to list
                else if (!getSquare(column + "" + row).getPiece().getPlayer().equals(_piece.getPlayer())) // Check if square has enemy piece in it
                    returnListTwo.add(getSquare(column + "" + row)); // Add enemy piece's square to other list
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
        returnListOne = checkMovementList(returnListOne, _piece); // Check if given squares are valid for movements
        returnListTwo = checkMovementList(returnListTwo, _piece); // Check if given capture movements are valid

        return new List[]{returnListOne, returnListTwo}; // return value [0] valid moves, [1] capture moves
    }


    // Isolated into its own function in order to use it for movements and capture moves
    // Function to check if given squareList has invalid movements - It checks if there are pieces between two squares
    // I.e. Checks if between A1 and A5 there is a piece in A3. If there is a piece, A5 is removed
    public List<Square> checkMovementList(List<Square> _squareList, Piece _piece){
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
                        _squareList.remove(getSquare(column + "" + row)); // Remove square from list
                        i = -1; // Start looping from beginning again since size of list and order of items has changed. -1 since iteration increases it automatically to 0
                    }
                }
                // Moving from higher row number to lower one, i.e. 8-1
                if (row<(currentRow - y) && (currentRow - y)<currentRow ) { // Don't check squares that are not between the two
                    if (column == currentColumn && // In vertical movements columns match
                            getSquare(column + "" + (currentRow -y)) != null && // Check that given square is within range - board return null if square is not found
                            getSquare(column + "" + (currentRow -y)).getPiece() != null) {  // Check that there is a piece in the square
                        _squareList.remove(getSquare(column + "" + row)); // Remove square from list
                        i = -1; // Start looping from beginning again since size of list and order of items has changed. -1 since iteration increases it automatically to 0
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
                            getSquare((char) ((int) currentColumn + x) + "" + row).getPiece() != null// Moving right: Check square for a piece
                            ) {
                        _squareList.remove(getSquare(column + "" + row)); // Remove square from list
                        i = -1; // Start looping from beginning again since size of list and order of items has changed. -1 since iteration increases it automatically to 0
                    }
                }
                // Moving from A-H
                if ((int) currentColumn > ((int) currentColumn - x) && ((int) currentColumn - x)> (int) column ){ // Don't check squares that are not between the two
                    if (row == currentRow && // In horizontal movement rows are the same
                            getSquare((char) ((int) currentColumn - x) + "" + row) != null && // Moving right: check that square exists
                            getSquare((char) ((int) currentColumn - x) + "" + row).getPiece() != null// Moving right: Check square for a piece
                            ) {
                        _squareList.remove(getSquare(column + "" + row)); // Remove square from list
                        i = -1; // Start looping from beginning again since size of list and order of items has changed. -1 since iteration increases it automatically to 0
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
                            _squareList.remove(getSquare(column + "" + row)); // Remove from list
                            i = -1; // Reset counter
                        }
                    }
                    // Moving down-right, i.e. A8 - C6
                    if((int) currentColumn < (int) currentColumn+xy && (int) currentColumn+xy < (int) column // Column between current col and checking col
                            && currentRow > currentRow-xy && currentRow-xy > row){ // Row between current row and checking row
                        if(getSquare((char) (int) (currentColumn+xy)+""+(currentRow-xy))!= null && // Check if square is between A-H, 1-8
                                getSquare((char) (int) (currentColumn+xy)+""+(currentRow-xy)).getPiece()!=null){ // Check if there's a piece
                            _squareList.remove(getSquare(column + "" + row)); // Remove from list
                            i = -1; // Reset counter
                        }
                    }
                    // Moving up-left, i.e. C6-A8
                    if((int) currentColumn > (int) currentColumn-xy && (int) currentColumn-xy > (int) column // Column between current col and checking col
                            && currentRow < currentRow+xy && currentRow+xy < row){ // Row between current row and checking row
                        if(getSquare((char) (int) (currentColumn-xy)+""+(currentRow+xy))!= null && // Check if square is between A-H, 1-8
                                getSquare((char) (int) (currentColumn-xy)+""+(currentRow+xy)).getPiece()!=null){ // Check if there's a piece
                            _squareList.remove(getSquare(column + "" + row)); // Remove from list
                            i = -1; // Reset counter
                        }
                    }
                    // Moving down-left, i.e. C6-A8
                    if((int) currentColumn > (int) currentColumn-xy && (int) currentColumn-xy > (int) column // Column between current col and checking col
                            && currentRow > currentRow-xy && currentRow-xy > row){ // Row between current row and checking row
                        if(getSquare((char) (int) (currentColumn-xy)+""+(currentRow-xy))!= null && // Check if square is between A-H, 1-8
                                getSquare((char) (int) (currentColumn-xy)+""+(currentRow-xy)).getPiece()!=null){ // Check if there's a piece
                            _squareList.remove(getSquare(column + "" + row)); // Remove from list
                            i = -1; // Reset counter
                        }
                    }
                }
            }
        }
        return _squareList;
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
