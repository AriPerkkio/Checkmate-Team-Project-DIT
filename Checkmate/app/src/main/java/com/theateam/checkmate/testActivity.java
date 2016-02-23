package com.theateam.checkmate;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by AriPerkkio on 23/02/16.
 *
 * Activity for testing layouts etc.
 *
 * Add this activity into manifest-file
 * and set it as launcher activity.
 * See end of manifest file for instructions.
 * After testing, remove activity from manifest file.
 *
 *
 * For OpenGL:
 * - Initialize OpenGLView object in constructor
 * - setContentView() method with that object
 *
 */
public class testActivity extends Activity {

    OpenGLView openGLView;
    private String TAG = "TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openGLView = new OpenGLView(this);
        setContentView(openGLView);
        Log.d(TAG, "Constructor");
    }
}
