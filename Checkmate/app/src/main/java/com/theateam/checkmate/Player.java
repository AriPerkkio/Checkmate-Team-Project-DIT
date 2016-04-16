package com.theateam.checkmate;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by AriPerkkio on 21/02/16.
 *
 * Parent class for AI/Human. Holds info about Pieces etc
 * See class diagram for detailed info
 *
 */
public class Player {

    // Attributes
    // References to other classes
    private List<Piece> pieceList = new Vector<>();

    private boolean turn;
    private long timer=0;
    private ChessTimer chessTimer;
    private String color;
    private String type;
    private boolean first; // True if player is first to start / bottom one / white pieces
    public Map<String, Integer> pieceIds = new HashMap<>();
    private List<Integer> eliminatedIndex = new Vector<>();

    public Player(String _type, boolean isFirst){
        type = _type;
        first = isFirst;
        chessTimer = new ChessTimer(Long.MAX_VALUE, 1000, this);

        // Setup IDs for graphic drawing (TextureID)
        int startingId = (TextureGL.count+1); // This is dependent of the graphics coordinate order
        // Player One
        pieceIds.put("playerOne_pawn_1", startingId);
        pieceIds.put("playerOne_pawn_2", startingId + 1);
        pieceIds.put("playerOne_pawn_3", startingId + 2);
        pieceIds.put("playerOne_pawn_4", startingId + 3);
        pieceIds.put("playerOne_pawn_5", startingId + 4);
        pieceIds.put("playerOne_pawn_6", startingId + 5);
        pieceIds.put("playerOne_pawn_7", startingId + 6);
        pieceIds.put("playerOne_pawn_8", startingId + 7);
        pieceIds.put("playerOne_rook_1", startingId + 8);
        pieceIds.put("playerOne_knight_1", startingId + 9);
        pieceIds.put("playerOne_bishop_1", startingId + 10);
        pieceIds.put("playerOne_king", startingId + 11);
        pieceIds.put("playerOne_queen", startingId + 12);
        pieceIds.put("playerOne_bishop_2", startingId + 13);
        pieceIds.put("playerOne_knight_2", startingId + 14);
        pieceIds.put("playerOne_rook_2", startingId + 15);
        // Player Two
        pieceIds.put("playerTwo_pawn_1", startingId + 16);
        pieceIds.put("playerTwo_pawn_2", startingId + 17);
        pieceIds.put("playerTwo_pawn_3", startingId + 18);
        pieceIds.put("playerTwo_pawn_4", startingId + 19);
        pieceIds.put("playerTwo_pawn_5", startingId + 20);
        pieceIds.put("playerTwo_pawn_6", startingId + 21);
        pieceIds.put("playerTwo_pawn_7", startingId + 22);
        pieceIds.put("playerTwo_pawn_8", startingId + 23);
        pieceIds.put("playerTwo_rook_1", startingId + 24);
        pieceIds.put("playerTwo_knight_1", startingId + 25);
        pieceIds.put("playerTwo_bishop_1", startingId + 26);
        pieceIds.put("playerTwo_king", startingId + 27);
        pieceIds.put("playerTwo_queen", startingId + 28);
        pieceIds.put("playerTwo_bishop_2", startingId + 29);
        pieceIds.put("playerTwo_knight_2", startingId + 30);
        pieceIds.put("playerTwo_rook_2", startingId + 31);
    }

    public List<Piece> getPieceList(){
        return pieceList;
    }

    public Piece getPieceByType(String _type){
        int textParts = _type.split(" ").length;
        int pieceNumber = 1;
        if(textParts==2) pieceNumber = Integer.parseInt(_type.split(" ")[1]+"");
        int pieceCounter = 0;
        for(int i=0;i<pieceList.size();i++)
            if(pieceList.get(i).getPieceType().equals( _type.split(" ")[0])) {
                pieceCounter++;
                if (pieceCounter == pieceNumber)
                    return pieceList.get(i);
            }
        Log.e("getPieceByType", "Not found: "+_type);
        return null;
    }

    // Used for graphics to check each square for each piece. Works even when pawn is promoted since it uses index numbers - not pieceTypes
    public String getPieceLayout(){
        String returnString = ""; // Empty old one
        for(int i=0;i<16;i++) { // 16 Squares/Pieces per player
            if(eliminatedIndex.contains(i)) // Piece with this index is eliminated - set this square as empty
                returnString += "empty ";
            else // Not eliminated
                for(int ii=0;ii<pieceList.size();ii++) // Check all pieces
                    if (pieceList.get(ii).getListId() == i)  // This piece has listId of the index - correct piece
                        returnString += pieceList.get(ii).getSquare().getId() + " "; // Add its square to string
        }
        return returnString; // I.e. "A1 A2 A5 F4 F5 ..." Same piece order as used in graphics
    }

    public boolean isHuman(){
        return type.equals("Human");
    }

    public boolean isFirst(){
        return first;
    }

    public void addPiece(Piece _piece){
        pieceList.add(_piece);
        _piece.setPlayer(this);
        if(_piece.getPieceType()!=null) {
            Log.d("Player", _piece.getPieceType() + " added");
        }
        _piece.setListId(pieceList.indexOf(_piece));
    }

    public void removePiece(Piece _piece, boolean isPawnPromoting){
        int index = _piece.getListId();
        if(!isPawnPromoting) eliminatedIndex.add(index); // Add removed piece to eliminated list
        pieceList.remove(_piece);
    }

    public void startTimer(){
        if(!PreviousFenlist.getStatus())
            chessTimer.start();
    }

    public void increaseTimer(){
        timer ++;
        Log.d("increaseTimer", toString()+" time "+timer);
        if(this.isFirst())
            GameActivity.updateTimer(timer, 1);
        else
            GameActivity.updateTimer(timer, 2);
    }

    public void setTimer(long _timer){
        timer = _timer;
    }
    public void pauseTimer(){
        chessTimer.pause();
    }

    public void resumeTimer(){
        chessTimer.resume();
    }

    public long getTimer(){
        return timer;
    }

    // Used to print debug info about current player
    public String toString(){
        int playerNumber = 2;
        if(isFirst())           playerNumber = 1;
        return type+ " number "+playerNumber+": ";
    }

    /** DEBUG **/
    private void printIdLists(){
        Log.d("455", "Print, Player: "+type);
        for(int i=0;i<eliminatedIndex.size();i++)
            Log.d("455", "Print eliminated, I: "+i+". ID: "+eliminatedIndex.get(i));
        for(int i=0;i<pieceList.size();i++)
            Log.d("455", "Print pieceList, I: "+i+". Piece: "+pieceList.get(i).getPieceType()+". ID: "+pieceList.get(i).getListId());
    }
}
