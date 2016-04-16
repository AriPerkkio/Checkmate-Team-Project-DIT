package com.theateam.checkmate;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 * Created by FionnMcguire on 31/03/2016.
 */
public class TwoPlayer extends AppCompatActivity implements ExpandableListView.OnChildClickListener
{
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.drawer_recyclerView)
    RecyclerView drawerRecyclerView;
    Switch learning_tool;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    int themeId = R.mipmap.defaulttheme;
    Button startGameButton;
    HashMap<String, List<String>> listDataChild;
    DatabaseManager databaseManager;


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
        setContentView(R.layout.activity_two_player);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
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

        learning_tool = (Switch)findViewById(R.id.learning_tool_switch2);
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
        // Game Mode = normal
        databaseManager = new DatabaseManager(this);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        int increase = 0;
        if(expListView.getChildCount()==8) // Number of themes + game modes
            increase = 3; // Number of themes

        switch(listDataHeader.get(groupPosition)){
            case "Theme":
                switch (listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition)){
                    case "Wooden": // Group 0, Child 0, Id 0
                        themeId = R.mipmap.wooden;
                        expListView.collapseGroup(0); // Hide list after click
                        ((TextView) expListView.getChildAt(0).findViewById(R.id.groupSecText)).setText(": Wooden");
                        break;
                    case "Metallic": // Group 0, Child 1, Id 1
                        // Uncomment these once theme implemented
                        //expListView.collapseGroup(0); // Hide list after click
                        //((TextView) expListView.getChildAt(0).findViewById(R.id.groupSecText)).setText(": Metallic");
                        Toast.makeText(this, "Metallic theme is not supported yet", Toast.LENGTH_LONG).show();
                        break;
                    case "Blue & Red": // Group 0, Child 2, Id 2
                        themeId = R.mipmap.defaulttheme;
                        expListView.collapseGroup(0); // Hide list after click
                        ((TextView) expListView.getChildAt(0).findViewById(R.id.groupSecText)).setText(": Blue & Red");
                        break;
                }
            case "Game Mode":
                switch(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition)){
                    case "Normal": // Group 1, Child 0, Id 0
                        ((TextView) expListView.getChildAt(1+increase).findViewById(R.id.groupSecText)).setText(": Normal");
                        expListView.collapseGroup(1); // Hide list after click
                        break;
                    case "King of the hill": // Group 1, Child 1, Id 1
                        // Uncomment these once game mode implemented
                        //((TextView) expListView.getChildAt(1+increase).findViewById(R.id.groupSecText)).setText(": King of the hill");
                        //expListView.collapseGroup(1); // Hide list after click
                        Toast.makeText(this, "King of the hill is not supported yet", Toast.LENGTH_LONG).show();
                        break;
                    case "Blitz": // Group 1, Child 2, Id 2
                        // Uncomment these once game mode implemented
                        //((TextView) expListView.getChildAt(1+increase).findViewById(R.id.groupSecText)).setText(": Blitz");
                        //expListView.collapseGroup(1); // Hide list after click
                        Toast.makeText(this, "Blitz is not supported yet", Toast.LENGTH_LONG).show();
                        break;
                }
        }
        return false;
    }

    public void StartTwoPlayer_intent(View view) {

        Intent intent = new Intent(TwoPlayer.this, GameActivity.class);
        intent.putExtra("gameMode", "TwoPlayer"); // Always TwoPlayer
        intent.putExtra("startingFen", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0"); // Always this one, it's starting position fen
        intent.putExtra("fenList", new ArrayList<String>()); // As in empty fenList
        intent.putExtra("themeId", themeId);
        intent.putExtra("learningTool", (learning_tool.isChecked()));
        intent.putExtra("timerOne", new long[1]);
        intent.putExtra("timerTwo", new long[1]);
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
            case "Settings":
                Log.d("onClick", "Settings");
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
                    startActivity(intent);
                }catch(SQLException e){
                    Log.e("DrawerResume", "e: "+e.toString());
                }
                break;
        }
    }

    private void prepareListData() {
        //Set up items in expandableList
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listDataHeader.add("Theme");
        listDataHeader.add("Game Mode");
        List<String> textures = new ArrayList<String>();
        textures.add("Wooden");
        textures.add("Metallic");
        textures.add("Blue & Red");
        List<String> gamemodes = new ArrayList<String>();
        gamemodes.add("Normal");
        gamemodes.add("King of the hill");
        gamemodes.add("Blitz");
        listDataChild.put(listDataHeader.get(0), textures);
        listDataChild.put(listDataHeader.get(1), gamemodes);
    }
}
