package com.theateam.checkmate;



import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;


import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by AriPerkkio on 21/02/16.
 *
 * Class to control graphic rendering
 * See class diagram for detailed info
 *
 */
public class OpenGLRenderer implements GLSurfaceView.Renderer{

    private final Context mActivityContext;
    private Coordinates allCoordinates = new Coordinates();

    // Matrix Initializations
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private float[] mRotationMatrix = new float[16];

    // Angle of the view
    public volatile float mAngle;

    // Picture for drawing
    private TextureGL picture;
    private float[] coordinates;
    private List<float[]> coordinateList = new Vector<>();

    public OpenGLRenderer(final Context activityContext)
    {
        Log.d("Renderer", "Constructor");
        mActivityContext = activityContext; // catch the context
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        Log.d("Renderer","onSurfaceCreated");
        // Order of coordinateList must match TextureGL's textureCoordinates
        coordinateList.add(allCoordinates.boardCoordinates); // Board
        // Player one
        coordinateList.add(allCoordinates.A2); // Pawn #1 Player One
        coordinateList.add(allCoordinates.B2); // Pawn #2 Player One
        coordinateList.add(allCoordinates.C2); // Pawn #3 Player One
        coordinateList.add(allCoordinates.D2); // Pawn #4 Player One
        coordinateList.add(allCoordinates.E2); // Pawn #5 Player One
        coordinateList.add(allCoordinates.F2); // Pawn #6 Player One
        coordinateList.add(allCoordinates.G2); // Pawn #7 Player One
        coordinateList.add(allCoordinates.H2); // Pawn #8 Player One
        coordinateList.add(allCoordinates.A1); // Rook #1 Player One
        coordinateList.add(allCoordinates.B1); // Knight #1 Player One
        coordinateList.add(allCoordinates.C1); // Bishop #1 Player One
        coordinateList.add(allCoordinates.D1); // King Player One
        coordinateList.add(allCoordinates.E1); // Queen Player One
        coordinateList.add(allCoordinates.F1); // Bishop #2 Player One
        coordinateList.add(allCoordinates.G1); // Knight #2 Player One
        coordinateList.add(allCoordinates.H1); // Rook #2 Player One
        // Player two
        coordinateList.add(allCoordinates.A7); // Pawn #1 Player Two
        coordinateList.add(allCoordinates.B7); // Pawn #2 Player Two
        coordinateList.add(allCoordinates.C7); // Pawn #3 Player Two
        coordinateList.add(allCoordinates.D7); // Pawn #4 Player Two
        coordinateList.add(allCoordinates.E7); // Pawn #5 Player Two
        coordinateList.add(allCoordinates.F7); // Pawn #6 Player Two
        coordinateList.add(allCoordinates.G7); // Pawn #7 Player Two
        coordinateList.add(allCoordinates.H7); // Pawn #8 Player Two
        coordinateList.add(allCoordinates.A8); // Rook #1 Player Two
        coordinateList.add(allCoordinates.B8); // Knight #1 Player Two
        coordinateList.add(allCoordinates.C8); // Bishop #1 Player Two
        coordinateList.add(allCoordinates.D8); // King Player Two
        coordinateList.add(allCoordinates.E8); // Queen Player Two
        coordinateList.add(allCoordinates.F8); // Bishop #2 Player Two
        coordinateList.add(allCoordinates.G8); // Knight #2 Player Two
        coordinateList.add(allCoordinates.H8); // Rook #2 Player Two

        coordinates = new float[coordinateList.size()*8];
        coordinates = setupTextureCoordinates(coordinateList);

       // Initialize the drawn picture
       picture = new TextureGL(mActivityContext,
               coordinates,
               R.mipmap.wooden); // Picture for the theme package

    }

    public void onDrawFrame(GL10 unused)
    {
        //Log.d("Renderer", "onDrawFrame");

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set eyes to selected place, xyz
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Sets the view I guess
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        // Rotate the view by mAngle as degrees
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);
        // Apply the rotation
        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);
        // Draw the picture using matrix values as set above
        picture.Draw(mMVPMatrix);
    }

    // Used in TextureGL
    public static int loadShader(int type, String shaderCode)
    {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void processTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        float screenWidth = 1080;
        float screenHeight = 1920/2;

        float sceneX = (x / screenWidth) * 2.0f - 1.0f;
        float sceneY = (y / screenHeight) * -2.0f + 1.0f; //if bottom is at -1. Otherwise same as X

        String clickedSquare = coordinatesToSquare(sceneX, sceneY);
        Log.d("Clicked Square: ", clickedSquare);

        // TODO
        // gameController.selectedSquare(clickedSquare);

    }

    // Called i.e. when rotating screen landscape - portrait
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        Log.d("Renderer","onSurfaceChanged");
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    // Combine multiple float[] together
    public float[] setupTextureCoordinates(List<float[]> coordinateList){
        float[] returnArray = new float[coordinateList.size()*8];
        Log.e("Size of coordslist",""+coordinateList.size());

        for(int i=0;i<coordinateList.size();i++) { // All the float[]
            for(int x=0;x<8;x++)
                returnArray[i * 8 + x] = coordinateList.get(i)[x];
        }
        return returnArray;
    }

    // Called from game logic
    public void movePiece(int pieceSelect, String square){
        // TODO: Work-around for pieceSelect. One option is to add attribute to piece class
        // Get float[] coordinates for the given String square
        float[] target = allCoordinates.coordinateList.get(square);

        // TODO: Loop changePieceCoordinates in order to make it look like piece is moving

        picture.changePieceCoordinates(pieceSelect, target[0], target[1], target[2], target[3]);
    }

    public String coordinatesToSquare(float x, float y){
        String returnText = ""; // Consist of Column (A-H) and row (1-8)

        // TODO: Check comparing once Coordinates.java is complete
        // TODO: Checking positive and negative coordinates - how it should be done

        // Find column
        // Check if x is between left and right
        if(x>allCoordinates.A1[0] && x<allCoordinates.A1[4])
            returnText += "A";
        if(x>allCoordinates.B1[0] && x<allCoordinates.B1[4])
            returnText += "B";
        if(x>allCoordinates.C1[0] && x<allCoordinates.C1[4])
            returnText += "C";
        if(x>allCoordinates.D1[0] && x<allCoordinates.D1[4])
            returnText += "D";
        if(x>allCoordinates.E1[0] && x<allCoordinates.E1[4])
            returnText += "E";
        if(x>allCoordinates.F1[0] && x<allCoordinates.F1[4])
            returnText += "F";
        if(x>allCoordinates.G1[0] && x<allCoordinates.G1[4])
            returnText += "G";
        if(x>allCoordinates.H1[0] && x<allCoordinates.H1[4])
            returnText += "H";

        // Find row
        // Check if y is between bottom and top
        if(y<allCoordinates.A1[1] && x>allCoordinates.A1[3])
            returnText += "1";
        if(y<allCoordinates.B1[1] && x>allCoordinates.B1[3])
            returnText += "2";
        if(y<allCoordinates.C1[1] && x>allCoordinates.C1[3])
            returnText += "3";
        if(y<allCoordinates.D1[1] && x>allCoordinates.D1[3])
            returnText += "4";
        if(y<allCoordinates.E1[1] && x>allCoordinates.E1[3])
            returnText += "5";
        if(y<allCoordinates.F1[1] && x>allCoordinates.F1[3])
            returnText += "6";
        if(y<allCoordinates.G1[1] && x>allCoordinates.G1[3])
            returnText += "7";
        if(y<allCoordinates.H1[1] && x>allCoordinates.H1[3])
            returnText += "8";

        //if(returnText.length()!=2)
          //  returnText = "OutOfBoard"; // Receiver should 1st check if "OutOfBoard"

        return returnText;
    }
}