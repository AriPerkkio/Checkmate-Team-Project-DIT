package com.theateam.checkmate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class GameActivity extends Activity {

    //public static TextView coordinates;
    private GameController gameController = GameController.getInstance(); // At the moment this makes sure there is atleas one instance of gameController

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //Set full screen
        // Hide navigation bar and keep it hidden when pressing
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //coordinates = (TextView) findViewById(R.id.coordinateText);

    }
}
