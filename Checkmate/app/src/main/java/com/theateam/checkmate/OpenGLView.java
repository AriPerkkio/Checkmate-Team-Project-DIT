package com.theateam.checkmate;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by AriPerkkio on 23/02/16.
 */
public class OpenGLView extends GLSurfaceView {

    private final OpenGLRenderer renderer;
    public static OpenGLView instance;


    public OpenGLView(Context context){
        super(context);
        setEGLContextClientVersion(2); // Set OpenGL ES version
        renderer = new OpenGLRenderer(context); // New instance of renderer
        setRenderer(renderer); // Set renderer as this view's graphics renderer
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); // Render only when requestRender() called
        instance = this;
        OpenGLRenderer.viewInstance = this;
    }

    public static OpenGLView getInstance(){
        return instance;
    }

    // Captures users clicks
    public boolean onTouchEvent(final MotionEvent e) {
        if(PreviousFenlist.getStatus()) // If in preview game, disable clicking
            return true;

        if(e.getAction() == MotionEvent.ACTION_UP) { // Launch action only when touch ends
            renderer.processTouchEvent(e); // Pass touch event to renderer
            requestRender();
        }
        return true;
    }

}
