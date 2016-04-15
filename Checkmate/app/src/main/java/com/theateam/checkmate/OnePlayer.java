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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;



/**
 * Created by FionnMcguire on 31/03/2016.
 */
public class OnePlayer extends AppCompatActivity implements ExpandableListView.OnChildClickListener{
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
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    int themeId = R.mipmap.defaulttheme;

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

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        //Set up the list with headers and items
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(this);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + ": " + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
        switch(listDataHeader.get(groupPosition)){
            case "Textures":
                switch (listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition)){
                    case "Wooden":
                        themeId = R.mipmap.wooden;
                        Log.d("Textures", ""+childPosition);
                        highlightSelect(childPosition);
                    break;
                    case "Metallic":
                        //Log.d("Textures", "Metallic");
                        Log.d("Textures", ""+childPosition);
                        highlightSelect(childPosition);
                    break;
                    case "Blue & Red":
                        themeId = R.mipmap.defaulttheme;
                        Log.d("Textures", ""+childPosition);
                        highlightSelect(childPosition);
                    break;
                }
            case "Game Modes":
                switch(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition)){
                    case "Normal":
                        Log.d("Game Modes", "Normal");
                    break;
                    case "King of the hill":
                        Log.d("Game Modes", "King of the hill");
                    break;
                    case "Blitz":
                        Log.d("Game Modes", "Blitz");
                    break;
                }
        }
        return false;
    }

    private void highlightSelect(int id){
        for(int i=1;i<4;i++){
            if(i==id) /** This part won't reset colors back just yet **/
                expListView.getChildAt(id+1).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            else
                expListView.getChildAt(id+1).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public void StartOnePlayer_intent(View view) {

        Intent intent = new Intent(OnePlayer.this, GameActivity.class);
        //intent.putExtra("gameMode", "AiEasy"); // Value from setting
        //intent.putExtra("learningTool", true); // (learningTool.equals("ON"))); // Value from setting
        intent.putExtra("startingFen", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0"); // Always this one, it's starting position fen
        intent.putExtra("fenList", new ArrayList<String>()); // As in empty fenList
        intent.putExtra("themeId", themeId);
        int difficulty_status = difficulty.getProgress();

        if(learning_tool.isChecked())
            intent.putExtra("learningTool", true);
        else
            intent.putExtra("learningTool", false);
        if(difficulty_status <25)
            intent.putExtra("gameMode", "AiEasy");
        else if(difficulty_status >25 && difficulty_status <50)
            intent.putExtra("gameMode", "AiMedium");
        else if(difficulty_status >50 && difficulty_status <75)
            intent.putExtra("gameMode", "AiHard");
        else if(difficulty_status >75)
            intent.putExtra("gameMode", "AiInsane");
        else
            intent.putExtra("gameMode", "AiEasy");
        intent.putExtra("Difficulty", difficulty_status); //Setting Difficulty
        startActivity(intent);
    }

    private void prepareListData() {
        //Set up items in expandableList
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listDataHeader.add("Textures");
        listDataHeader.add("Game Modes");
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
