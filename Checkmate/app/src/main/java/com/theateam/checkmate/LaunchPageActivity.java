package com.theateam.checkmate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class LaunchPageActivity extends AppCompatActivity {

    Button button;
    private Button btnPrevGames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //Set full screen
        setContentView(R.layout.activity_launch_page);

        btnPrevGames = (Button) findViewById(R.id.btnPrevGames);
        btnPrevGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchPageActivity.this, PreviousGames.class);
                LaunchPageActivity.this.startActivity(intent);
            }
        });

        button = (Button) findViewById(R.id.launchPageButton);
        button.setText("PLAY");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchPageActivity.this, MenuActivity.class);
                LaunchPageActivity.this.startActivity(intent);
            }
        });
    }
}
