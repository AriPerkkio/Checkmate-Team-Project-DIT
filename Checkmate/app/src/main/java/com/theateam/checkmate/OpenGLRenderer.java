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
        coordinateList.add(allCoordinates.boardCoordinates); // Board
        coordinateList.add(allCoordinates.A2); // Pawn #1 Player One
        coordinateList.add(allCoordinates.B2); // Pawn #2 Player One
        coordinateList.add(allCoordinates.C2); // Pawn #3 Player One
        coordinateList.add(allCoordinates.D2); // Pawn #4 Player One
        coordinateList.add(allCoordinates.E2); // Pawn #5 Player One
        coordinateList.add(allCoordinates.F2); // Pawn #6 Player One
        coordinateList.add(allCoordinates.G2); // Pawn #7 Player One
        coordinateList.add(allCoordinates.H2); // Pawn #8 Player One

        coordinates = new float[coordinateList.size()*8];
        coordinates = setupTextureCoordinates(coordinateList);

       // Initialize the 1st drawn picture
       picture = new TextureGL(mActivityContext,
               coordinates,
               R.mipmap.wooden); // Picture for the theme package

    }

    public void onDrawFrame(GL10 unused)
    {
        Log.d("Renderer", "onDrawFrame");

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
}

