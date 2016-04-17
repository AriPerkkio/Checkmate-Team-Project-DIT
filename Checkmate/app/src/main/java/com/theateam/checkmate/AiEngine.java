package com.theateam.checkmate;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by AriPerkkio on 05/04/16.
 */
public class AiEngine {

    private String enginePath = null; // Initialized in getAiMove()
    Process process = null;
    DataOutputStream out = null;
    BufferedReader in = null; // TODO: When Game is over -> in.close();

    public AiEngine(String _enginePath){
        enginePath = _enginePath;
        if(enginePath==null) Log.e("AiEngine", "Constructor, Engine Path Null");
        Log.d("AiEngine", "Constructor with path: "+enginePath);
    }

    public String getAiMove(String command, int level, int thinkTime, int thinkDepth){
        String text="";
        String oneLine = "";
        String move = ""; // I.e "a1a2"
        String ponderMove = "";

        try {
            if(process==null){
                process = Runtime.getRuntime().exec(enginePath); // Start stockfish by cmd-line
                out = new DataOutputStream(process.getOutputStream()); // Stream for communicating with AI Engine
                out.writeBytes("uci"+ "\n"); // Start UCI communication
                out.writeBytes("setoption name Skill Level value "+level+ "\n"); // Setup level for AI
                out.writeBytes("setoption name Slow Mover value 1000"+ "\n"); //
                out.writeBytes("ucinewgame"+ "\n"); // Start new game
                out.writeBytes("isready"+ "\n"); // GUI ready to start
            }
            out.writeBytes("position fen "+command+ "\n"); // Update board layout to AI Engine
            out.writeBytes("go depth "+thinkDepth+" movetime "+thinkTime+ "\n"); // Calculate new move with given depth and thinkTime
            out.flush(); // Write messages above
            // AI Engine is kept on during the game

            if(in==null) in = new BufferedReader(new InputStreamReader(process.getInputStream())); // Get inputStream for reading AI's responses

            do{
                text = in.readLine(); // Read AI's response
                if(text!=null){
                    Log.i("Full text", text); // Logging, should be removed later
                    if(text.split(" ").length>1) {
                        oneLine = text.split(" ")[0]; // First word
                        move = text.split(" ")[1]; // Second word / given move, i.e "bestmove a1a2"
                    }
                    if(text.split(" ").length==4) ponderMove = text.split(" ")[3]; // Ponder move is given as 4th string, i.e. "bestmove a1a2 ponder a7a6"
                }
            }while(text!=null && !oneLine.equals("bestmove"));
        } catch (IOException e) { // Read/Write errors
            e.printStackTrace();
        }
        Log.i("Ponder", ponderMove);
        Log.i("Returning AiMove", move);
        return move;
    }
}
