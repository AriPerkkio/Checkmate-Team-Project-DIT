package com.theateam.checkmate;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;



/**
 * Created by FionnMcguire on 31/03/2016.
 */
public class OnePlayer extends AppCompatActivity implements ExpandableListView.OnChildClickListener, SeekBar.OnSeekBarChangeListener{
    TextView timeLimitText;
    TextView difficultyText;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.drawer_recyclerView)
    RecyclerView drawerRecyclerView;
    private SeekBar difficulty;
    private SeekBar timeLimitBar;
    private int timeLimitStatus;
    Switch learning_tool;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    int themeId = R.mipmap.defaulttheme;
    DatabaseManager databaseManager;
    HashMap<String, List<String>> listDataChild;

    public void onCreate(Bundle savedInstanceState) {
        // Hide navigation bar and keep it hidden when pressing
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_player);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(this.getResources().getColor(R.color.colorAccent));
        timeLimitText = (TextView) findViewById(R.id.playerOneTimeText);
        difficultyText = (TextView) findViewById(R.id.playerOneDifficulty);
        learning_tool = (Switch)findViewById(R.id.learning_tool_switch);
        difficulty = (SeekBar)findViewById(R.id.difficulty_bar);
        difficulty.setOnSeekBarChangeListener(this);
        timeLimitBar = (SeekBar) findViewById(R.id.playerOnetimelimit_bar);
        timeLimitBar.setOnSeekBarChangeListener(this);
        timeLimitBar.setProgress(100);
        timeLimitStatus = 1800; // Initialize as max value

        ActionBarDrawerToggle drawerToggle;
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        List<String> rows = new ArrayList<>();
        rows.add("Resume");
        rows.add("Home");
        rows.add("Analysis");
        rows.add("Update");
        rows.add("Exit");

        DrawerAdapter drawerAdapter = new DrawerAdapter(rows);
        drawerRecyclerView.setAdapter(drawerAdapter);
        drawerRecyclerView.setHasFixedSize(true);
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        //Set up the list with headers and items
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(this);

        // Initialize default settings
        themeId = R.mipmap.defaulttheme;
        databaseManager = new DatabaseManager(this);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        switch(listDataHeader.get(groupPosition)){
            case "Theme":
                switch (listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition)){
                    case "Wooden": // Group 0, Child 0, Id 0
                        themeId = R.mipmap.wooden;
                        expListView.collapseGroup(0); // Hide list after click
                        ((TextView) expListView.getChildAt(0).findViewById(R.id.groupSecText)).setText(": Wooden");
                        break;
                    case "Marble": // Group 0, Child 1, Id 1
                        themeId = R.mipmap.marble;
                        expListView.collapseGroup(0); // Hide list after click
                        ((TextView) expListView.getChildAt(0).findViewById(R.id.groupSecText)).setText(": Marble");
                        break;
                    case "Blue & Red": // Group 0, Child 2, Id 2
                        themeId = R.mipmap.defaulttheme;
                        expListView.collapseGroup(0); // Hide list after click
                        ((TextView) expListView.getChildAt(0).findViewById(R.id.groupSecText)).setText(": Blue & Red");
                        break;
                    case "Gold & Silver": // Group 0, Child 3, Id 3
                        themeId = R.mipmap.silvergold;
                        expListView.collapseGroup(0); // Hide list after click
                        ((TextView) expListView.getChildAt(0).findViewById(R.id.groupSecText)).setText(": Gold & Silver");
                        break;
                }
        }
        return false;
    }

    public void StartOnePlayer_intent(View view) {

        Intent intent = new Intent(OnePlayer.this, GameActivity.class);
        intent.putExtra("startingFen", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0"); // Always this one, it's starting position fen
        intent.putExtra("fenList", new ArrayList<String>()); // As in empty fenList
        intent.putExtra("themeId", themeId);
        intent.putExtra("learningTool", (learning_tool.isChecked()));
        intent.putExtra("timerOne", new long[]{});
        intent.putExtra("timerTwo", new long[]{});
        intent.putExtra("timeLimit", timeLimitStatus);

        int difficulty_status = difficulty.getProgress();
        if(difficulty_status <25)
            intent.putExtra("gameMode", "AI Easy");
        else if(difficulty_status >25 && difficulty_status <50)
            intent.putExtra("gameMode", "AI Medium");
        else if(difficulty_status >50 && difficulty_status <75)
            intent.putExtra("gameMode", "AI Hard");
        else if(difficulty_status >75)
            intent.putExtra("gameMode", "AI Insane");
        else
            intent.putExtra("gameMode", "AI Easy");
        PreviousFenlist.setStatus(false);
        startActivity(intent);
    }

    public void drawerClick(View v){
        String clickedText = ((TextView) v).getText().toString();
        switch(clickedText){
            case "Update":
                Toast.makeText(this, "You are already running ad-free version", Toast.LENGTH_SHORT).show();
                break;
            case "Home":
                Intent intent = new Intent(this, Home.class);
                startActivity(intent);
                break;
            case "Analysis":
                intent = new Intent(this, PreviousGames.class);
                startActivity(intent);
                break;
            case "Resume":
                try{
                    databaseManager.open();
                    int latestId = databaseManager.getHighestId();
                    if(latestId==0){
                        Toast.makeText(this, "No games found", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    Cursor fenCursor = databaseManager.getFenStringsById(latestId);
                    Cursor gameSettings = databaseManager.getSettingsById(latestId);
                    databaseManager.close();
                    ArrayList<String> fenList = new ArrayList<>();
                    long[] timerOne = new long[fenCursor.getCount()];
                    long[] timerTwo = new long[fenCursor.getCount()];;
                    int i=0;
                    do{
                        fenList.add(fenCursor.getString(1));
                        timerOne[i] = (long) fenCursor.getInt(2);
                        timerTwo[i] = (long) fenCursor.getInt(3);
                        i++;
                    }
                    while(fenCursor.moveToNext());
                    intent = new Intent(this, GameActivity.class);
                    intent.putExtra("gameMode", gameSettings.getString(0));
                    intent.putExtra("learningTool", (gameSettings.getString(1).equals("ON")));
                    intent.putExtra("themeId", gameSettings.getInt(2));
                    intent.putExtra("fenList", fenList);
                    intent.putExtra("startingFen", fenList.get(fenList.size()-1));
                    intent.putExtra("timerOne", timerOne);
                    intent.putExtra("timerTwo", timerTwo);
                    intent.putExtra("timeLimit", gameSettings.getInt(3));
                    intent.putExtra("gameId", latestId);
                    PreviousFenlist.setStatus(false);
                    startActivity(intent);
                }catch(SQLException e){
                    Log.e("DrawerResume", "e: "+e.toString());
                }
                break;
            case "Exit":
                intent = new Intent(this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Exit", true);
                startActivity(intent);
                break;
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar){ } // Not used
    public void onStartTrackingTouch(SeekBar seekBar){ } // Not used

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
        if(seekBar == difficulty){
            if(progress <25)
                difficultyText.setText("Easy");
            else if(progress >25 && progress <50)
                difficultyText.setText("Medium");
            else if(progress >50 && progress <75)
                difficultyText.setText("Hard");
            else if(progress >75)
                difficultyText.setText("Insane");
        }else {

            // Min value 60s, Max value 1800s
            /**
             * 0-100
             * 0x + b= 60, b=60
             * 100x +b = 1800
             * 100x = 1800-60
             * 100x = 1740
             * x = 17,4
             * 60-1800
             */

            int totalSeconds = (int) Math.round(progress * 17.4 + 60);
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds - minutes * 60;

            Log.d("****", "Minutes: " + minutes + " & Seconds: " + seconds);
            Log.d("seconds from", totalSeconds + "-" + minutes * 60);

            String timeUpdate = "";

            //rounds off total seconds to the minute
            if (seconds != 0) {
                totalSeconds -= seconds;
                minutes = totalSeconds / 60;
            }

            if (minutes < 10)
                timeUpdate += "0";
            timeUpdate += minutes + ":00";


            timeLimitText.setText(timeUpdate);
            timeLimitStatus = totalSeconds;
        }
    }

    private void prepareListData() {
        //Set up items in expandableList
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listDataHeader.add("Theme");
        List<String> textures = new ArrayList<String>();
        textures.add("Wooden");
        textures.add("Marble");
        textures.add("Gold & Silver");
        textures.add("Blue & Red");
        listDataChild.put(listDataHeader.get(0), textures);
    }

    @Override
    public void onResume(){
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
