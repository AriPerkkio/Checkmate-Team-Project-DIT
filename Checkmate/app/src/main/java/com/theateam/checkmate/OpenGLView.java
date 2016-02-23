package com.theateam.checkmate;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by arska on 23/02/16.
 */
public class OpenGLView extends GLSurfaceView {

    private final OpenGLRenderer renderer;

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
        float screenWidth = 1080;
        float screenHeight = 1920;

        float sceneX = (x/screenWidth)*2.0f - 1.0f;
        float sceneY = (y/screenHeight) * -2.0f + 1.0f; //if bottom is at -1. Otherwise same as X

        Log.d("OpenGLView", "Click "+sceneX+", "+sceneY);
        renderer.processTouchEvent(e); // Pass touch event to renderer
        requestRender();
        return true;
    }

}
