package com.theateam.checkmate;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by arska on 05/04/16.
 */
public class AiEngine {

    private String enginePath = null; // Initialized in getAiMove()
    Process process =null;
    DataOutputStream out = null;
    BufferedReader in = null; // TODO: When Game is over -> in.close();

    public String getAiMove(String command){
        if(enginePath==null) enginePath = GameActivity.getInstance().getDirectory() +"/stockfish"; // Update AI Engine path
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
            }

            out.writeBytes("position fen "+command+ "\n");
            out.writeBytes("go"+ "\n"); // Calculate new move
            out.flush();

            if(in==null) in = new BufferedReader(new InputStreamReader(process.getInputStream()));

            do{
                text = in.readLine();
                if(text!=null){
                    //Log.i("Full text", text);
                    oneLine = text.split(" ")[0];
                    if(text.split(" ").length>1) move = text.split(" ")[1];
                    if(text.split(" ").length==4) ponderMove = text.split(" ")[3];
                }
            }while(text!=null && (!oneLine.equals("bestmove") ||!oneLine.equals("no")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Engine", text);
        Log.i("Ponder", ponderMove);
        Log.i("Returning AiMove", move);
        return move;
    }
}
