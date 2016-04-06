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

    public String getAiMove(String command){
        String text="";
        String oneLine = "";
        String move = "";
        String ponderMove = "";

        try {
            if(process==null){
                process = Runtime.getRuntime().exec(enginePath); // Start stockfish by cmd-line
                out = new DataOutputStream(process.getOutputStream()); // Stream for communicating with AI Engine
                out.writeBytes("uci"+ "\n");
                out.writeBytes("setoption name Skill Level value 1"+ "\n"); // TODO: Set skill level
                out.writeBytes("setoption name Slow Mover value 1000");
                out.writeBytes("ucinewgame"+ "\n");
                out.writeBytes("isready"+ "\n");
            }
            out.writeBytes("position fen "+command+ "\n");
            out.writeBytes("go depth 1"+ "\n"); // Calculate new move
            out.flush();

            if(in==null) in = new BufferedReader(new InputStreamReader(process.getInputStream()));

            do{
                text = in.readLine();
                if(text!=null){
                    Log.i("Full text", text);
                    if(text.split(" ").length>1) {
                        oneLine = text.split(" ")[0];
                        move = text.split(" ")[1];
                    }
                    if(text.split(" ").length==4) ponderMove = text.split(" ")[3];
                }
            }while(text!=null && !oneLine.equals("bestmove"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Engine", text);
        Log.i("Ponder", ponderMove);
        Log.i("Returning AiMove", move);
        return move;
    }
}
