package com.theateam.checkmate;


import android.util.Log;

import java.util.List;

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
        Square returnSquare;
        for(int x=0;x<8;x++)
            for(int y=0;y<8;y++){
                if(squareList[x][y].getId().equals(_square))
                    return squareList[x][y];
            }
        Log.e("Board.getSquare", "Not found: "+_square);
        return null; // No square found - Error
    }

    public List<Square> getValidMoves(Piece _piece){
        return null; // TODO
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
