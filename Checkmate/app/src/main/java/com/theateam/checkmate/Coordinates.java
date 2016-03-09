package com.theateam.checkmate;

import android.util.Log;

import java.io.CharArrayReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by deadmadness on 25/02/16.
 */
public class Coordinates {

    public Map<String, float[]> coordinateList = new HashMap<>();
    public Map<String, float[]> learningToolList = new HashMap<>();
    public Map<String, float[]> promotePieces = new HashMap<>();

    //size of square gap
    private float squareGap = 0.224f;

    //left side of square
    private float leftX = -0.90f;
    //bottom of square
    private float botY = -0.89f;

    //right side of sqaure
    private float rightX = leftX + squareGap;
    //top side of square
    private float topY = botY + squareGap;

    float[] promotePawnWindow;
    float[] promotePawnOne;
    float[] promotePawnTwo;
    float[] promotePawnThree;
    float[] promotePawnFour;

    public Coordinates() {

        //Add coords to list with loop
        StringBuilder sb = new StringBuilder("A1");
        char startChar = 'A';
        int c1_ = (int)startChar;
        for( int i = 0; i < 8; i++ ) {
            sb.setCharAt(0,(char)(c1_ + i));
            for( int j = 0; j < 8; j++ ) {
                sb.setCharAt(1, Character.forDigit((j+1),10));
                coordinateList.put(sb.toString(), new float[]{
                        (leftX + (squareGap * i)), (topY + (squareGap * j)),   //top left
                        (leftX + (squareGap * i)), (botY + (squareGap * j)),   //bot left
                        (rightX + (squareGap * i)), (botY + (squareGap * j)),   //bot right
                        (rightX + (squareGap * i)), (topY + (squareGap * j))    //top right
                });
            }
        }
        // Add learning tool textures to map
        learningToolList.put("blue circle", learningTool_circle_blue);
        learningToolList.put("green circle", learningTool_circle_green);
        learningToolList.put("grey circle", learningTool_circle_grey);
        learningToolList.put("red circle", learningTool_circle_red);
        learningToolList.put("blue cross", learningTool_cross_blue);
        learningToolList.put("green cross", learningTool_cross_green);
        learningToolList.put("grey cross", learningTool_cross_grey);
        learningToolList.put("red cross", learningTool_cross_red);
        learningToolList.put("green square",learningTool_square_green);
        learningToolList.put("blue square",learningTool_square_blue);
        learningToolList.put("grey square",learningTool_square_grey);
        learningToolList.put("red square",learningTool_square_red);
        learningToolList.put("empty empty", hideCoordinates);

        // Add textures for pawn promoting
        promotePieces.put("rookPlayerOne", rookPlayerOne);
        promotePieces.put("bishopPlayerOne", bishopPlayerOne);
        promotePieces.put("queenPlayerOne", queenPlayerOne);
        promotePieces.put("knightPlayerOne", knightPlayerOne);
        promotePieces.put("rookPlayerTwo", rookPlayerTwo);
        promotePieces.put("bishopPlayerTwo", bishopPlayerTwo);
        promotePieces.put("queenPlayerTwo", queenPlayerTwo);
        promotePieces.put("knightPlayerTwo", knightPlayerTwo);

        promotePawnWindow = new float[] {
                // Top 6, Bottom 3, Left A, Right H
                coordinateList.get("A6")[0], coordinateList.get("A5")[1], // Top Left
                coordinateList.get("A6")[2], coordinateList.get("A4")[3], // Bot Left
                coordinateList.get("H6")[4], coordinateList.get("A4")[5], // Bot right
                coordinateList.get("H6")[6], coordinateList.get("A5")[7]  // Top right
        };
        promotePawnOne = new float[] {
                coordinateList.get("A5")[0], coordinateList.get("A5")[1], // Top Left
                coordinateList.get("A5")[2], coordinateList.get("B4")[3], // Bot Left
                coordinateList.get("B5")[4], coordinateList.get("B4")[5], // Bot right
                coordinateList.get("B5")[6], coordinateList.get("A5")[7]  // Top right
        };

        promotePawnTwo = new float[] {
                coordinateList.get("C5")[0], coordinateList.get("A5")[1], // Top Left
                coordinateList.get("C5")[2], coordinateList.get("B4")[3], // Bot Left
                coordinateList.get("D5")[4], coordinateList.get("B4")[5], // Bot right
                coordinateList.get("D5")[6], coordinateList.get("A5")[7]  // Top right
        };

        promotePawnThree = new float[] {
                coordinateList.get("E5")[0], coordinateList.get("A5")[1], // Top Left
                coordinateList.get("E5")[2], coordinateList.get("B4")[3], // Bot Left
                coordinateList.get("F5")[4], coordinateList.get("B4")[5], // Bot right
                coordinateList.get("F5")[6], coordinateList.get("A5")[7]  // Top right
        };

        promotePawnFour = new float[] {
                coordinateList.get("G5")[0], coordinateList.get("A5")[1], // Top Left
                coordinateList.get("G5")[2], coordinateList.get("B4")[3], // Bot Left
                coordinateList.get("H5")[4], coordinateList.get("B4")[5], // Bot right
                coordinateList.get("H5")[6], coordinateList.get("A5")[7]  // Top right
        };

    }

    // Board on matrix
    float[] boardCoordinates = new float[]{
            // Board
            -1.0f, 1.0f,   // top left
            -1.0f, -1.0f,   // bottom left
            1.0f, -1.0f,   // bottom right
            1.0f, 1.0f,  //top right
    };

    float[] hideCoordinates = new float[]{
            0.0f, 0.0f, //top left
            0.0f, 0.0f, //bot left
            0.0f, 0.0f, //bot right
            0.0f, 0.0f,  //top right
    };

    // Texture coordinates
    float[] boardTexture = new float[]{
            0.0f, 1.0f- 1.0f, // top left
            0.0f, 1.0f- 0.5f, // left bot
            0.5f, 1.0f- 0.5f, // bot right
            0.5f, 1.0f- 1.0f, // top right
    };

    // Player One
    float[] queenPlayerOne = new float[]{
            0.0f, 1.0f - 0.2f, // top left
            0.0f, 1.0f - 0.1f, // left bot
            0.1f, 1.0f - 0.1f, // bot right
            0.1f, 1.0f - 0.2f, // top right
    };

    float[] kingPlayerOne = new float[]{
            0.1f, 1.0f - 0.2f, // top left
            0.1f, 1.0f - 0.1f, // left bot
            0.2f, 1.0f - 0.1f, // bot right
            0.2f, 1.0f - 0.2f, // top right
    };

    float[] bishopPlayerOne = new float[]{
            0.2f, 1.0f - 0.2f, // top left
            0.2f, 1.0f - 0.1f, // left bot
            0.3f, 1.0f - 0.1f, // bot right
            0.3f, 1.0f - 0.2f, // top right
    };

    float[] knightPlayerOne = new float[]{
            0.3f, 1.0f - 0.2f, // top left
            0.3f, 1.0f - 0.1f, // left bot
            0.4f, 1.0f - 0.1f, // bot right
            0.4f, 1.0f - 0.2f, // top right
    };

    float[] rookPlayerOne = new float[]{
            0.4f, 1.0f - 0.2f, // top left
            0.4f, 1.0f - 0.1f, // left bot
            0.5f, 1.0f - 0.1f, // bot right
            0.5f, 1.0f - 0.2f, // top right
    };

    float[] pawnPlayerOne = new float[]{
            0.5f, 1.0f- 0.2f, // top left
            0.5f, 1.0f- 0.1f, // left bot
            0.6f, 1.0f- 0.1f, // bot right
            0.6f, 1.0f- 0.2f, // top right
    };

    // Player Two
    float[] queenPlayerTwo = new float[]{
            0.0f, 1.0f - 0.1f, // top left
            0.0f, 1.0f - 0.0f, // left bot
            0.1f, 1.0f - 0.0f, // bot right
            0.1f, 1.0f - 0.1f, // top right
    };

    float[] kingPlayerTwo = new float[]{
            0.1f, 1.0f - 0.1f, // top left
            0.1f, 1.0f - 0.0f, // left bot
            0.2f, 1.0f - 0.0f, // bot right
            0.2f, 1.0f - 0.1f, // top right
    };

    float[] bishopPlayerTwo = new float[]{
            0.2f, 1.0f - 0.1f, // top left
            0.2f, 1.0f - 0.0f, // left bot
            0.3f, 1.0f - 0.0f, // bot right
            0.3f, 1.0f - 0.1f, // top right
    };

    float[] knightPlayerTwo = new float[]{
            0.3f, 1.0f - 0.1f, // top left
            0.3f, 1.0f - 0.0f, // left bot
            0.4f, 1.0f - 0.0f, // bot right
            0.4f, 1.0f - 0.1f, // top right
    };

    float[] rookPlayerTwo = new float[]{
            0.4f, 1.0f - 0.1f, // top left
            0.4f, 1.0f - 0.0f, // left bot
            0.5f, 1.0f - 0.0f, // bot right
            0.5f, 1.0f - 0.1f, // top right
    };

    float[] pawnPlayerTwo = new float[]{
            0.5f, 1.0f- 0.1f, // top left
            0.5f, 1.0f- 0.0f, // left bot
            0.6f, 1.0f- 0.0f, // bot right
            0.6f, 1.0f- 0.1f, // top right
    };

    float[] learningTool_cross_red = new float[] {
            0.6f, 1.0f - 1.0f, //top left
            0.6f, 1.0f - 0.9f, //bot left
            0.7f, 1.0f - 0.9f, //bot right
            0.7f, 1.0f - 1.0f  //top right
    };

    float[] learningTool_cross_green = new float[] {
            0.6f, 1.0f - 0.9f, //top left
            0.6f, 1.0f - 0.8f, //bot left
            0.7f, 1.0f - 0.8f, //bot right
            0.7f, 1.0f - 0.9f  //top right
    };

    float[] learningTool_cross_blue = new float[] {
            0.6f, 1.0f - 0.8f, //top left
            0.6f, 1.0f - 0.7f, //bot left
            0.7f, 1.0f - 0.7f, //bot right
            0.7f, 1.0f - 0.8f  //top right
    };

    float[] learningTool_cross_grey = new float[] {
            0.6f, 1.0f - 0.7f, //top left
            0.6f, 1.0f - 0.6f, //bot left
            0.7f, 1.0f - 0.6f, //bot right
            0.7f, 1.0f - 0.7f  //top right
    };

    float[] learningTool_circle_red = new float[] {
            0.5f, 1.0f - 1.0f, //top left
            0.5f, 1.0f - 0.9f, //bot left
            0.6f, 1.0f - 0.9f, //bot right
            0.6f, 1.0f - 1.0f  //top right
    };

    float[] learningTool_circle_green = new float[] {
            0.5f, 1.0f - 0.9f, //top left
            0.5f, 1.0f - 0.8f, //bot left
            0.6f, 1.0f - 0.8f, //bot right
            0.6f, 1.0f - 0.9f  //top right
    };

    float[] learningTool_circle_blue = new float[] {
            0.5f, 1.0f - 0.8f, //top left
            0.5f, 1.0f - 0.7f, //bot left
            0.6f, 1.0f - 0.7f, //bot right
            0.6f, 1.0f - 0.8f  //top right
    };

    float[] learningTool_circle_grey = new float[] {
            0.5f, 1.0f - 0.7f, //top left
            0.5f, 1.0f - 0.6f, //bot left
            0.6f, 1.0f - 0.6f, //bot right
            0.6f, 1.0f - 0.7f  //top right
    };

    float[] learningTool_square_red = new float[] {
            0.7f, 1.0f - 1.0f, //top left
            0.7f, 1.0f - 0.9f, //bot left
            0.8f, 1.0f - 0.9f, //bot right
            0.8f, 1.0f - 1.0f  //top right
    };

    float[] learningTool_square_green = new float[] {
            0.7f, 1.0f - 0.9f, //top left
            0.7f, 1.0f - 0.8f, //bot left
            0.8f, 1.0f - 0.8f, //bot right
            0.8f, 1.0f - 0.9f  //top right
    };

    float[] learningTool_square_blue = new float[] {
            0.7f, 1.0f - 0.8f, //top left
            0.7f, 1.0f - 0.7f, //bot left
            0.8f, 1.0f - 0.7f, //bot right
            0.8f, 1.0f - 0.8f  //top right
    };

    float[] learningTool_square_grey = new float[] {
            0.7f, 1.0f - 0.7f, //top left
            0.7f, 1.0f - 0.6f, //bot left
            0.8f, 1.0f - 0.6f, //bot right
            0.8f, 1.0f - 0.7f  //top right
    };

}

