package com.theateam.checkmate;

import android.app.Fragment;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by arska on 24/02/16.
 */
public class GameFragment extends Fragment {

    private GLSurfaceView openGlView;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        openGlView = new OpenGLView(this.getActivity()); //I believe you may also use getActivity().getApplicationContext();
        return openGlView;
    }
}
