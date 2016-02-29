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

    //size of square gap
    private float squareGap = 0.224f;

    //left side of square
    private float leftX = -0.90f;
    //bottom of square
    private float botY = -0.90f;

    //right side of sqaure
    private float rightX = leftX + squareGap;
    //top side of square
    private float topY = botY + squareGap;

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
    }

    // Board on matrix
    float[] boardCoordinates = new float[]{
            // Board
            -1.0f, 1.0f,   // top left
            -1.0f, -1.0f,   // bottom left
            1.0f, -1.0f,   // bottom right
            1.0f, 1.0f,  //top right
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
}

