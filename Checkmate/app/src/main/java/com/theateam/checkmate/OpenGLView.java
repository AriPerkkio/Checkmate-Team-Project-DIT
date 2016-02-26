package com.theateam.checkmate;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by arska on 23/02/16.
 */
public class OpenGLView extends GLSurfaceView {

    private final OpenGLRenderer renderer;
    private GameActivity gameActivity = new GameActivity();


    public OpenGLView(Context context){
        super(context);
        setEGLContextClientVersion(2); // Set OpenGL ES version
        renderer = new OpenGLRenderer(context); // New instance of renderer
        setRenderer(renderer); // Set renderer as this view's graphics renderer
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); // Render only when requestRender() called
    }

    // Captures users clicks
    public boolean onTouchEvent(final MotionEvent e) {

        float x = e.getX();
        float y = e.getY();
        float screenWidth = 1080; // TODO: Read device's screen attributes and calculate ratio
        float screenHeight = 1920*0.5625f; // 9/16 = 0.5625

        float sceneX = (x/screenWidth)*2.0f - 1.0f;
        float sceneY = (y/screenHeight) * -2.0f + 1.0f; //if bottom is at -1. Otherwise same as X

        if(e.getAction() == MotionEvent.ACTION_UP) { // Launch action only when touch ends
            GameActivity.coordinates.setText("X: " + sceneX + "\nY: " + sceneY);
            renderer.processTouchEvent(e); // Pass touch event to renderer
            requestRender();
        }
        return true;
    }

}
