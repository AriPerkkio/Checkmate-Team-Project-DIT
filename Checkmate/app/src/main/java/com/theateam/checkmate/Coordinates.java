package com.theateam.checkmate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by deadmadness on 25/02/16.
 */
public class Coordinates {

    public Map<String, float[]> coordinateList = new HashMap<>();

    //takes in floats 
    public ArrayList<float[]> rawCoords = new ArrayList<>();

    //size of square gap
    private float squareGap = 0.21831646f;

    //left side of square
    private float leftX = -0.90196836f;
    //bottom of square
    private float botY = -0.85619843f;

    //right side of sqaure
    private float rightX = leftX + squareGap;
    //top side of square
    private float topY = botY + squareGap;

    public Coordinates() {
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                rawCoords.add(new float[]{
                        (leftX+(squareGap*i)) ,(topY+(squareGap*j)) ,   //top left
                        (leftX+(squareGap*i)) , (botY+(squareGap*j)),   //bot left
                        (rightX+(squareGap*i)), (botY+(squareGap*j)),   //bot right
                        (rightX+(squareGap*i)), (topY+(squareGap*j))    //top right
                });
            }
        }

        coordinateList.put("A1", rawCoords.get(0));
        coordinateList.put("A2", rawCoords.get(1));
        coordinateList.put("A3", rawCoords.get(2));
        coordinateList.put("A4", rawCoords.get(3));
        coordinateList.put("A5", rawCoords.get(4));
        coordinateList.put("A6", rawCoords.get(5));
        coordinateList.put("A7", rawCoords.get(6));
        coordinateList.put("A8", rawCoords.get(7));
        coordinateList.put("B1", rawCoords.get(8));
        coordinateList.put("B2", rawCoords.get(9));
        coordinateList.put("B3", rawCoords.get(10));
        coordinateList.put("B4", rawCoords.get(11));
        coordinateList.put("B5", rawCoords.get(12));
        coordinateList.put("B6", rawCoords.get(13));
        coordinateList.put("B7", rawCoords.get(14));
        coordinateList.put("B8", rawCoords.get(15));
        coordinateList.put("C1", rawCoords.get(16));
        coordinateList.put("C2", rawCoords.get(17));
        coordinateList.put("C3", rawCoords.get(18));
        coordinateList.put("C4", rawCoords.get(19));
        coordinateList.put("C5", rawCoords.get(20));
        coordinateList.put("C6", rawCoords.get(21));
        coordinateList.put("C7", rawCoords.get(22));
        coordinateList.put("C8", rawCoords.get(23));
        coordinateList.put("D1", rawCoords.get(24));
        coordinateList.put("D2", rawCoords.get(25));
        coordinateList.put("D3", rawCoords.get(26));
        coordinateList.put("D4", rawCoords.get(27));
        coordinateList.put("D5", rawCoords.get(28));
        coordinateList.put("D6", rawCoords.get(29));
        coordinateList.put("D7", rawCoords.get(30));
        coordinateList.put("D8", rawCoords.get(31));
        coordinateList.put("E1", rawCoords.get(32));
        coordinateList.put("E2", rawCoords.get(33));
        coordinateList.put("E3", rawCoords.get(34));
        coordinateList.put("E4", rawCoords.get(35));
        coordinateList.put("E5", rawCoords.get(36));
        coordinateList.put("E6", rawCoords.get(37));
        coordinateList.put("E7", rawCoords.get(38));
        coordinateList.put("E8", rawCoords.get(39));
        coordinateList.put("F1", rawCoords.get(40));
        coordinateList.put("F2", rawCoords.get(41));
        coordinateList.put("F3", rawCoords.get(42));
        coordinateList.put("F4", rawCoords.get(43));
        coordinateList.put("F5", rawCoords.get(44));
        coordinateList.put("F6", rawCoords.get(45));
        coordinateList.put("F7", rawCoords.get(46));
        coordinateList.put("F8", rawCoords.get(47));
        coordinateList.put("G1", rawCoords.get(48));
        coordinateList.put("G2", rawCoords.get(49));
        coordinateList.put("G3", rawCoords.get(50));
        coordinateList.put("G4", rawCoords.get(51));
        coordinateList.put("G5", rawCoords.get(52));
        coordinateList.put("G6", rawCoords.get(53));
        coordinateList.put("G7", rawCoords.get(54));
        coordinateList.put("G8", rawCoords.get(55));
        coordinateList.put("H1", rawCoords.get(56));
        coordinateList.put("H2", rawCoords.get(57));
        coordinateList.put("H3", rawCoords.get(58));
        coordinateList.put("H4", rawCoords.get(59));
        coordinateList.put("H5", rawCoords.get(60));
        coordinateList.put("H6", rawCoords.get(61));
        coordinateList.put("H7", rawCoords.get(62));
        coordinateList.put("H8", rawCoords.get(63));


        // TODO: Add all the coordinates to list like this
    }

    /*

    //top left
    //bot left
    //bot right
    //top right
     */
    /*
    float[] A1 = new float[]{
            -0.90196836f,-0.637882f,      //top left
            -0.90196836f,-0.85619843f,       //bot left
            -0.6836519f, -0.85619843f,       //bot right
            -0.6836519f, -0.637882f,      //top right
    };

    float[] A2 = new float[]{
            -0.90196836f,-0.41956553f,      //top left
            -0.90196836f,-0.637882f,       //bot left
            -0.6836519f, -0.637882f,       //bot right
            -0.6836519f, -0.41956553f,      //top right
    };

    float[] A3 = new float[]{
            -0.90196836f,-0.20124906f,      //top left
            -0.90196836f,-0.41956553f,       //bot left
            -0.6836519f,-0.41956553f,       //bot right
            -0.6836519f,-0.20124906f,      //top right
    };

    float[] A4 = new float[]{
            -0.90196836f,0.017067432f,      //top left
            -0.90196836f,-0.20124906f,       //bot left
            -0.6836519f, -0.20124906f,       //bot right
            -0.6836519f, 0.017067432f,      //top right
    };

    float[] A5 = new float[]{
            -0.90196836f,0.23538387f,      //top left
            -0.90196836f,0.017067432f,       //bot left
            -0.6836519f, 0.017067432f,       //bot right
            -0.6836519f, 0.23538387f,      //top right
    };

    float[] A6 = new float[]{
            -0.90196836f,0.4537003f,      //top left
            -0.90196836f,0.23538387f,       //bot left
            -0.6836519f, 0.23538387f,       //bot right
            -0.6836519f, 0.4537003f,      //top right
    };

    float[] A7 = new float[]{
            -0.90196836f,0.67201686f,      //top left
            -0.90196836f,0.4537003f,       //bot left
            -0.6836519f, 0.4537003f,       //bot right
            -0.6836519f, 0.67201686f,      //top right
    };

    float[] A8 = new float[]{
            -0.90196836f,0.8903333f,      //top left
            -0.90196836f,0.67201686f,       //bot left
            -0.6836519f,0.67201686f,       //bot right
            -0.6836519f, 0.8903333f,      //top right
    };

    float[] B1 = new float[]{
            -0.6836519f,-0.637882f,      //top left
            -0.6836519f,-0.85619843f,       //bot left
            -0.46533546f, -0.85619843f,       //bot right
            -0.46533546f, -0.637882f,      //top right
    };

    float[] B2 = new float[]{
            -0.6836519f,-0.41956553f,      //top left
            -0.6836519f,-0.637882f,       //bot left
            -0.46533546f, -0.637882f,       //bot right
            -0.46533546f, -0.41956553f,      //top right
    };

    float[] B3 = new float[]{
            -0.6836519f,-0.20124906f,      //top left
            -0.6836519f,-0.41956553f,       //bot left
            -0.46533546f, -0.41956553f,       //bot right
            -0.46533546f, -0.20124906f,      //top right
    };

    float[] B4 = new float[]{
            -0.6836519f,0.017067432f,      //top left
            -0.6836519f,-0.20124906f,       //bot left
            -0.46533546f, -0.20124906f,       //bot right
            -0.46533546f, 0.017067432f,      //top right
    };

    float[] B5 = new float[]{
            -0.6836519f,0.23538387f,      //top left
            -0.6836519f,0.017067432f,       //bot left
            -0.46533546f, 0.017067432f,       //bot right
            -0.46533546f, 0.23538387f,      //top right
    };

    float[] B6 = new float[]{
            -0.6836519f,0.4537003f,      //top left
            -0.6836519f,0.23538387f,       //bot left
            -0.46533546f, 0.23538387f,       //bot right
            -0.46533546f, 0.4537003f,      //top right
    };

    float[] B7 = new float[]{
            -0.6836519f,0.67201686f,      //top left
            -0.6836519f,0.4537003f,       //bot left
            -0.46533546f, 0.4537003f,       //bot right
            -0.46533546f, 0.67201686f,      //top right
    };

    float[] B8 = new float[]{
            -0.6836519f,0.8903333f,      //top left
            -0.6836519f,0.67201686f,       //bot left
            -0.46533546f, 0.67201686f,       //bot right
            -0.46533546f, 0.8903333f,      //top right
    };

    float[] C1 = new float[]{
            -0.46533546f,-0.637882f,      //top left
            -0.46533546f,-0.85619843f,       //bot left
            -0.247019f, -0.85619843f,       //bot right
            -0.247019f, -0.637882f,      //top right
    };

    float[] C2 = new float[]{
            -0.46533546f,-0.41956553f,      //top left
            -0.46533546f,-0.637882f,       //bot left
            -0.247019f, -0.637882f,       //bot right
            -0.247019f, -0.41956553f,      //top right
    };

    float[] C3 = new float[]{
            -0.46533546f,-0.20124906f,      //top left
            -0.46533546f,-0.41956553f,       //bot left
            -0.247019f, -0.41956553f,       //bot right
            -0.247019f, -0.20124906f,      //top right
    };

    float[] C4 = new float[]{
            -0.46533546f,0.017067432f,      //top left
            -0.46533546f,-0.20124906f,       //bot left
            -0.247019f, -0.20124906f,       //bot right
            -0.247019f, 0.017067432f,      //top right
    };

    float[] C5 = new float[]{
            -0.46533546f,0.23538387f,      //top left
            -0.46533546f,0.017067432f,       //bot left
            -0.247019f, 0.017067432f,       //bot right
            -0.247019f, 0.23538387f,      //top right
    };

    float[] C6 = new float[]{
            -0.46533546f,0.4537003f,      //top left
            -0.46533546f,0.23538387f,       //bot left
            -0.247019f, 0.23538387f,       //bot right
            -0.247019f, 0.4537003f,      //top right
    };

    float[] C7 = new float[]{
            -0.46533546f,0.67201686f,      //top left
            -0.46533546f,0.4537003f,       //bot left
            -0.247019f, 0.4537003f,       //bot right
            -0.247019f, 0.67201686f,      //top right
    };

    float[] C8 = new float[]{
            -0.46533546f,0.8903333f,      //top left
            -0.46533546f,0.67201686f,       //bot left
            -0.247019f, 0.67201686f,       //bot right
            -0.247019f, 0.8903333f,      //top right
    };
    float[] D1 = new float[]{
            -0.247019f,-0.637882f,      //top left
            -0.247019f,-0.85619843f,       //bot left
            -0.028702497f, -0.85619843f,       //bot right
            -0.028702497f, -0.637882f,      //top right
    };

    float[] D2 = new float[]{
            -0.247019f,-0.41956553f,      //top left
            -0.247019f,-0.637882f,       //bot left
            -0.028702497f, -0.637882f,       //bot right
            -0.028702497f, -0.41956553f,      //top right
    };

    float[] D3 = new float[]{
            -0.247019f,-0.20124906f,      //top left
            -0.247019f,-0.41956553f,       //bot left
            -0.028702497f, -0.41956553f,       //bot right
            -0.028702497f, -0.20124906f,      //top right
    };

    float[] D4 = new float[]{
            -0.247019f,0.017067432f,      //top left
            -0.247019f,-0.20124906f,       //bot left
            -0.028702497f, -0.20124906f,       //bot right
            -0.028702497f, 0.017067432f,      //top right
    };

    float[] D5 = new float[]{
            -0.247019f,0.23538387f,      //top left
            -0.247019f,0.017067432f,       //bot left
            -0.028702497f, 0.017067432f,       //bot right
            -0.028702497f, 0.23538387f,      //top right
    };

    float[] D6 = new float[]{
            -0.247019f,0.4537003f,      //top left
            -0.247019f,0.23538387f,       //bot left
            -0.028702497f, 0.23538387f,       //bot right
            -0.028702497f, 0.4537003f,      //top right
    };

    float[] D7 = new float[]{
            -0.247019f,0.67201686f,      //top left
            -0.247019f,0.4537003f,       //bot left
            -0.028702497f, 0.4537003f,       //bot right
            -0.028702497f, 0.67201686f,      //top right
    };

    float[] D8 = new float[]{
            -0.247019f,0.8903333f,      //top left
            -0.247019f,0.67201686f,       //bot left
            -0.028702497f, 0.67201686f,       //bot right
            -0.028702497f, 0.8903333f,      //top right
    };
    float[] E1 = new float[]{
            -0.028702497f,-0.637882f,      //top left
            -0.028702497f,-0.85619843f,       //bot left
            0.18961394f, -0.85619843f,       //bot right
            0.18961394f, -0.637882f,      //top right
    };

    float[] E2 = new float[]{
            -0.028702497f,-0.41956553f,      //top left
            -0.028702497f,-0.637882f,       //bot left
            0.18961394f, -0.637882f,       //bot right
            0.18961394f, -0.41956553f,      //top right
    };

    float[] E3 = new float[]{
            -0.028702497f,-0.20124906f,      //top left
            -0.028702497f,-0.41956553f,       //bot left
            0.18961394f, -0.41956553f,       //bot right
            0.18961394f, -0.20124906f,      //top right
    };

    float[] E4 = new float[]{
            -0.028702497f,0.017067432f,      //top left
            -0.028702497f,-0.20124906f,       //bot left
            0.18961394f, -0.20124906f,       //bot right
            0.18961394f, 0.017067432f,      //top right
    };

    float[] E5 = new float[]{
            -0.028702497f,0.23538387f,      //top left
            -0.028702497f,0.017067432f,       //bot left
            0.18961394f, 0.017067432f,       //bot right
            0.18961394f,0.23538387f,      //top right
    };

    float[] E6 = new float[]{
            -0.028702497f,0.4537003f,      //top left
            -0.028702497f,0.23538387f,       //bot left
            0.18961394f, 0.23538387f,       //bot right
            0.18961394f, 0.4537003f,      //top right
    };

    float[] E7 = new float[]{
            -0.028702497f,0.67201686f,      //top left
            -0.028702497f,0.4537003f,       //bot left
            0.18961394f, 0.4537003f,       //bot right
            0.18961394f, 0.67201686f,      //top right
    };

    float[] E8 = new float[]{
            -0.028702497f,0.8903333f,      //top left
            -0.028702497f,0.67201686f,       //bot left
            0.18961394f, 0.67201686f,       //bot right
            0.18961394f, 0.8903333f,      //top right
    };

    float[] F1 = new float[]{
            0.18961394f,-0.637882f,      //top left
            0.18961394f,-0.85619843f,       //bot left
            0.40793037f, -0.85619843f,       //bot right
            0.40793037f, -0.637882f,      //top right
    };

    float[] F2 = new float[]{
            0.18961394f,-0.41956553f,      //top left
            0.18961394f,-0.637882f,       //bot left
            0.40793037f, -0.637882f,       //bot right
            0.40793037f, -0.41956553f,      //top right
    };
    float[] F3 = new float[]{
            0.18961394f,-0.20124906f,      //top left
            0.18961394f,-0.41956553f,       //bot left
            0.40793037f, -0.41956553f,       //bot right
            0.40793037f, -0.20124906f,      //top right
    };

    float[] F4 = new float[]{
            0.18961394f, 0.017067432f,      //top left
            0.18961394f,-0.20124906f,       //bot left
            0.40793037f, -0.20124906f,       //bot right
            0.40793037f, 0.017067432f,      //top right
    };

    float[] F5 = new float[]{
            0.18961394f,0.23538387f,      //top left
            0.18961394f,0.017067432f,       //bot left
            0.40793037f, 0.017067432f,       //bot right
            0.40793037f, 0.23538387f,      //top right
    };

    float[] F6 = new float[]{
            0.18961394f,0.4537003f,      //top left
            0.18961394f,0.23538387f,       //bot left
            0.40793037f, 0.23538387f,       //bot right
            0.40793037f, 0.4537003f,      //top right
    };

    float[] F7 = new float[]{
            0.18961394f,0.67201686f,      //top left
            0.18961394f,0.4537003f,       //bot left
            0.40793037f, 0.4537003f,       //bot right
            0.40793037f, 0.67201686f,      //top right
    };

    float[] F8 = new float[]{
            0.18961394f,0.8903333f,      //top left
            0.18961394f,0.67201686f,       //bot left
            0.40793037f, 0.67201686f,       //bot right
            0.40793037f, 0.8903333f,      //top right
    };

    float[] G1 = new float[]{
            0.40793037f,-0.63779366f,      //top left
            0.40793037f,-0.8617511f,       //bot left
            0.6262469f, -0.8617511f,       //bot right
            0.6262469f, -0.63779366f,      //top right
    };

    float[] G2 = new float[]{
            0.40793037f,-0.63779366f,      //top left
            0.40793037f,-0.85619843f,       //bot left
            0.6262469f, -0.85619843f,       //bot right
            0.6262469f, -0.63779366f,      //top right
    };

    float[] G3 = new float[]{
            0.40793037f,-0.63779366f,      //top left
            0.40793037f,-0.8617511f,       //bot left
            0.6262469f, -0.8617511f,       //bot right
            0.6262469f, -0.63779366f,      //top right
    };

    float[] G4 = new float[]{
            0.40793037f,-0.63779366f,      //top left
            0.40793037f,-0.8617511f,       //bot left
            0.6262469f, -0.8617511f,       //bot right
            0.6262469f, -0.63779366f,      //top right
    };

    float[] G5 = new float[]{
            0.40793037f,-0.63779366f,      //top left
            0.40793037f,-0.8617511f,       //bot left
            0.6262469f, -0.8617511f,       //bot right
            0.6262469f, -0.63779366f,      //top right
    };

    float[] G6 = new float[]{
            0.40793037f,-0.63779366f,      //top left
            0.40793037f,-0.8617511f,       //bot left
            0.6262469f, -0.8617511f,       //bot right
            0.6262469f, -0.63779366f,      //top right
    };

    float[] G7 = new float[]{
            0.40793037f,-0.63779366f,      //top left
            0.40793037f,-0.8617511f,       //bot left
            0.6262469f, -0.8617511f,       //bot right
            0.6262469f, -0.63779366f,      //top right
    };

    float[] G8 = new float[]{
            0.40793037f,-0.63779366f,      //top left
            0.40793037f,-0.8617511f,       //bot left
            0.6262469f, -0.8617511f,       //bot right
            0.6262469f, -0.63779366f,      //top right
    };

    float[] H1 = new float[]{
            0.6262469f,-0.63779366f,      //top left
            0.6262469f,-0.8617511f,       //bot left
            0.84456336f, -0.8617511f,       //bot right
            0.84456336f, -0.63779366f,      //top right
    };

    float[] H2 = new float[]{
            0.6262469f,-0.63779366f,      //top left
            0.6262469f,-0.8617511f,       //bot left
            0.84456336f, -0.8617511f,       //bot right
            0.84456336f, -0.63779366f,      //top right
    };

    float[] H3 = new float[]{
            0.6262469f,-0.63779366f,      //top left
            0.6262469f,-0.8617511f,       //bot left
            0.84456336f, -0.8617511f,       //bot right
            0.84456336f, -0.63779366f,      //top right
    };

    float[] H4 = new float[]{
            0.6262469f,-0.63779366f,      //top left
            0.6262469f,-0.8617511f,       //bot left
            0.84456336f, -0.8617511f,       //bot right
            0.84456336f, -0.63779366f,      //top right
    };

    float[] H5 = new float[]{
            0.6262469f,-0.63779366f,      //top left
            0.6262469f,-0.8617511f,       //bot left
            0.84456336f, -0.8617511f,       //bot right
            0.84456336f, -0.63779366f,      //top right
    };

    float[] H6 = new float[]{
            0.6262469f,-0.63779366f,      //top left
            0.6262469f,-0.8617511f,       //bot left
            0.84456336f, -0.8617511f,       //bot right
            0.84456336f, -0.63779366f,      //top right
    };

    float[] H7 = new float[]{
            0.6262469f,-0.63779366f,      //top left
            0.6262469f,-0.8617511f,       //bot left
            0.84456336f, -0.8617511f,       //bot right
            0.84456336f, -0.63779366f,      //top right
    };

    float[] H8 = new float[]{
            0.6262469f,-0.63779366f,      //top left
            0.6262469f,-0.8617511f,       //bot left
            0.84456336f, -0.8617511f,       //bot right
            0.84456336f, -0.63779366f,      //top right
    };*/

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












