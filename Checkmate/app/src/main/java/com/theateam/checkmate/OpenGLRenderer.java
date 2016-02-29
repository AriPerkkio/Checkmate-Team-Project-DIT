package com.theateam.checkmate;



import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
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
    static OpenGLView viewInstance;

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
        viewInstance = OpenGLView.getInstance();
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        Log.d("Renderer","onSurfaceCreated");
        // Order of coordinateList must match TextureGL's textureCoordinates
        coordinateList.add(allCoordinates.boardCoordinates); // Board
        // Player one
        coordinateList.add(allCoordinates.coordinateList.get("A2")); // Pawn #1 Player One
        coordinateList.add(allCoordinates.coordinateList.get("B2")); // Pawn #2 Player One
        coordinateList.add(allCoordinates.coordinateList.get("C2")); // Pawn #3 Player One
        coordinateList.add(allCoordinates.coordinateList.get("D2")); // Pawn #4 Player One
        coordinateList.add(allCoordinates.coordinateList.get("E2")); // Pawn #5 Player One
        coordinateList.add(allCoordinates.coordinateList.get("F2")); // Pawn #6 Player One
        coordinateList.add(allCoordinates.coordinateList.get("G2")); // Pawn #7 Player One
        coordinateList.add(allCoordinates.coordinateList.get("H2")); // Pawn #8 Player One
        coordinateList.add(allCoordinates.coordinateList.get("A1")); // Rook #1 Player One
        coordinateList.add(allCoordinates.coordinateList.get("B1")); // Knight #1 Player One
        coordinateList.add(allCoordinates.coordinateList.get("C1")); // Bishop #1 Player One
        coordinateList.add(allCoordinates.coordinateList.get("D1")); // King Player One
        coordinateList.add(allCoordinates.coordinateList.get("E1")); // Queen Player One
        coordinateList.add(allCoordinates.coordinateList.get("F1")); // Bishop #2 Player One
        coordinateList.add(allCoordinates.coordinateList.get("G1")); // Knight #2 Player One
        coordinateList.add(allCoordinates.coordinateList.get("H1")); // Rook #2 Player One
        // Player two
        coordinateList.add(allCoordinates.coordinateList.get("A7")); // Pawn #1 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("B7")); // Pawn #2 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("C7")); // Pawn #3 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("D7")); // Pawn #4 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("E7")); // Pawn #5 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("F7")); // Pawn #6 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("G7")); // Pawn #7 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("H7")); // Pawn #8 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("A8")); // Rook #1 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("B8")); // Knight #1 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("C8")); // Bishop #1 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("D8")); // King Player Two
        coordinateList.add(allCoordinates.coordinateList.get("E8")); // Queen Player Two
        coordinateList.add(allCoordinates.coordinateList.get("F8")); // Bishop #2 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("G8")); // Knight #2 Player Two
        coordinateList.add(allCoordinates.coordinateList.get("H8")); // Rook #2 Player Two

        coordinates = new float[coordinateList.size()*8];
        coordinates = setupMatrixCoordinates(coordinateList);

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
        float screenHeight =  1920*0.5625f; // 9/16 = 0.5625

        float sceneX = (x / screenWidth) * 2.0f - 1.0f;
        float sceneY = (y / screenHeight) * -2.0f + 1.0f; //if bottom is at -1. Otherwise same as X

        String clickedSquare = coordinatesToSquare(sceneX, sceneY);
        Log.d("Clicked Square: ", clickedSquare);

        //TODO: This is just testing movement. Calls for movement will come from game logic
        int pieceSelect = 12;
        if(clickedSquare.equals("D2"))
            movePiece(pieceSelect, "D3", "D2");
        if(clickedSquare.equals("D3"))
            movePiece(pieceSelect, "D4", "D3");
        if(clickedSquare.equals("D4"))
            movePiece(pieceSelect, "D5", "D4");
        if(clickedSquare.equals("D5"))
            movePiece(pieceSelect, "D6", "D5");
        if(clickedSquare.equals("D6"))
            movePiece(pieceSelect, "E6", "D6");
        if(clickedSquare.equals("E6"))
            movePiece(pieceSelect, "E5", "E6");
        if(clickedSquare.equals("E5"))
            movePiece(pieceSelect, "E4", "E5");
        if(clickedSquare.equals("E4"))
            movePiece(pieceSelect, "E3", "E4");
        if(clickedSquare.equals("E3"))
            movePiece(pieceSelect, "D3", "E3");

        //TODO: This is just testing piece eliminating. Calls for it will come from game logic
        if(clickedSquare.equals("H5")) {
            eliminatePiece(19, "C7");
            eliminatePiece(20, "D7");
            eliminatePiece(21, "E7");
            eliminatePiece(22, "F7");
            eliminatePiece(23, "G7");
        }

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
    public float[] setupMatrixCoordinates(List<float[]> coordinateList){
        float[] returnArray = new float[coordinateList.size()*8];

        for(int i=0;i<coordinateList.size();i++) { // All the float[]
            for(int x=0;x<8;x++)
                returnArray[i * 8 + x] = coordinateList.get(i)[x];
        }
        return returnArray;
    }

    // Moves specific piece from source to target
    public void movePiece(int pieceSelect, String targetSquare, String sourceSquare){
        // TODO: Work-around for pieceSelect. One option is to add attribute to piece class

        // Get float[] coordinates for the given String square
        float[] target = allCoordinates.coordinateList.get(targetSquare);
        float[] source = allCoordinates.coordinateList.get(sourceSquare);
        float left = source[0];
        float right = source[4];
        float top = source[1];
        float bot = source[3];

        int movementCount = 250;
        for(int i=0;i<movementCount;i++){
            left  += ((target[0]-source[0])/movementCount);
            right += ((target[4]-source[4])/movementCount);
            top   += ((target[1]-source[1])/movementCount);
            bot   += ((target[3]-source[3])/movementCount);
            picture.changePieceCoordinates(pieceSelect, left, right, top, bot);
            SystemClock.sleep(1);
            viewInstance.requestRender(); // Render picture after each movement
                                          // Careful with this one. Too fast rendering throws runtime errors
        }
    }

    // Makes pieces shrink until they disappear
    public void eliminatePiece(int pieceSelect, String _square){

        float[] square = allCoordinates.coordinateList.get(_square);
        float left = square[0];
        float right = square[4];
        float top = square[1];
        float bot = square[3];

        int fadingCount = 250;
        for(int i=0;i<fadingCount/2;i++){
            left  -= ((square[0]-square[4])/fadingCount);
            right -= ((square[4]-square[0])/fadingCount);
            top   -= ((square[1]-square[3])/fadingCount);
            bot   -= ((square[3]-square[1])/fadingCount);
            picture.changePieceCoordinates(pieceSelect, left, right, top, bot);
            SystemClock.sleep(5);
            viewInstance.requestRender(); // Render picture after each movement
            // Careful with this one. Too fast rendering throws runtime errors
        }
        // After making it disappear set it to 0,0,0,0 coordinates
        picture.changePieceCoordinates(pieceSelect, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    // Convert coordinates to String square
    public String coordinatesToSquare(float x, float y){
        String returnText = ""; // Consist of Column (A-H) and row (1-8)

        // Find column
        // Check if x is between left and right
        if(x>allCoordinates.coordinateList.get("A1")[0] && x<allCoordinates.coordinateList.get("A1")[4])
            returnText += "A";
        if(x>allCoordinates.coordinateList.get("B1")[0] && x<allCoordinates.coordinateList.get("B1")[4])
            returnText += "B";
        if(x>allCoordinates.coordinateList.get("C1")[0] && x<allCoordinates.coordinateList.get("C1")[4])
            returnText += "C";
        if(x>allCoordinates.coordinateList.get("D1")[0] && x<allCoordinates.coordinateList.get("D1")[4])
            returnText += "D";
        if(x>allCoordinates.coordinateList.get("E1")[0] && x<allCoordinates.coordinateList.get("E1")[4])
            returnText += "E";
        if(x>allCoordinates.coordinateList.get("F1")[0] && x<allCoordinates.coordinateList.get("F1")[4])
            returnText += "F";
        if(x>allCoordinates.coordinateList.get("G1")[0] && x<allCoordinates.coordinateList.get("G1")[4])
            returnText += "G";
        if(x>allCoordinates.coordinateList.get("H1")[0] && x<allCoordinates.coordinateList.get("H1")[4])
            returnText += "H";

        // Find row
        // Check if y is between bottom and top
            //                                    BOT <   y <  TOP
        if(allCoordinates.coordinateList.get("A1")[3] < y && y < allCoordinates.coordinateList.get("A1")[1])
            returnText += "1";
        if(y>allCoordinates.coordinateList.get("A2")[3] && y<allCoordinates.coordinateList.get("A2")[1])
            returnText += "2";
        if(y>allCoordinates.coordinateList.get("A3")[3] && y<allCoordinates.coordinateList.get("A3")[1])
            returnText += "3";
        if(y>allCoordinates.coordinateList.get("A4")[3] && y<allCoordinates.coordinateList.get("A4")[1])
            returnText += "4";
        if(y>allCoordinates.coordinateList.get("A5")[3] && y<allCoordinates.coordinateList.get("A5")[1])
            returnText += "5";
        if(y>allCoordinates.coordinateList.get("A6")[3] && y<allCoordinates.coordinateList.get("A6")[1])
            returnText += "6";
        if(y>allCoordinates.coordinateList.get("A7")[3] && y<allCoordinates.coordinateList.get("A7")[1])
            returnText += "7";
        if(y>allCoordinates.coordinateList.get("A8")[3] && y<allCoordinates.coordinateList.get("A8")[1])
            returnText += "8";

        // Check if click was out of the board
        if(returnText.length()!=2) // This includes especially cases like "A", "1" etc. Also "A1234567" if it still occurs
            returnText = "OutOfBoard"; // Receiver should 1st check if "OutOfBoard"

        return returnText;
    }
}