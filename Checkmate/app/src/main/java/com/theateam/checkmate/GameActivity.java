package com.theateam.checkmate;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class GameActivity extends Activity {

    public static TextView coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //Set full screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        coordinates = (TextView) findViewById(R.id.coordinateText);
    }
}
