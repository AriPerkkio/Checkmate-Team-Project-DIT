package com.theateam.checkmate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;



/**
 * Created by FionnMcguire on 31/03/2016.
 */
public class OnePlayer extends AppCompatActivity{
    TextView mProgressText;
    TextView mTrackingText;
    //final Context mCtx;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.drawer_recyclerView)
    RecyclerView drawerRecyclerView;
    private SeekBar difficulty;
    Switch learning_tool;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_player);
        ButterKnife.inject(this);

        //setSupportActionBar(toolbar);
        learning_tool = (Switch)findViewById(R.id.learning_tool_switch);
        difficulty = (SeekBar)findViewById(R.id.difficulty_bar);
        int difficulty_status = difficulty.getProgress();
        System.out.println("Current Progress = " + difficulty_status);




        ActionBarDrawerToggle drawerToggle;
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        List<String> rows = new ArrayList<>();
        rows.add("Update");
        rows.add("Home");
        rows.add("Analysis");
        rows.add("Settings");
        rows.add("Resume");


        DrawerAdapter drawerAdapter = new DrawerAdapter(rows);
        drawerRecyclerView.setAdapter(drawerAdapter);
        drawerRecyclerView.setHasFixedSize(true);
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    public void StartOnePlayer_intent(View view) {


        Intent intent = new Intent(OnePlayer.this, GameActivity.class);
        //intent.putExtra("gameMode", "AiEasy"); // Value from setting
        //intent.putExtra("learningTool", true); // (learningTool.equals("ON"))); // Value from setting
        intent.putExtra("startingFen", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0"); // Always this one, it's starting position fen
        intent.putExtra("fenList", new ArrayList<String>()); // As in empty fenList
        int difficulty_status = difficulty.getProgress();

        if(learning_tool.isChecked()){
            intent.putExtra("learningTool", true);
        }
        else
        {
            intent.putExtra("learningTool", false);
        }
        if(difficulty_status <25) {
            intent.putExtra("gameMode", "AiEasy");
        }
        else if(difficulty_status >25 && difficulty_status <50){
            intent.putExtra("gameMode", "AiMedium");
        }
        else if(difficulty_status >50 && difficulty_status <75){
            intent.putExtra("gameMode", "AiHard");
        }
        else if(difficulty_status >75){
            intent.putExtra("gameMode", "AiInsane");
        }
        else
        {
            intent.putExtra("gameMode", "AiEasy");
        }
        intent.putExtra("Difficulty", difficulty_status); //Setting Difficulty
        startActivity(intent);
    }
}
