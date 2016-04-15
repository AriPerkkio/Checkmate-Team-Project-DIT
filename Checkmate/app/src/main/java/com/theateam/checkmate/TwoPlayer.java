package com.theateam.checkmate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 * Created by FionnMcguire on 31/03/2016.
 */
public class TwoPlayer extends AppCompatActivity
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
    Button startGameButton;
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
        setContentView(R.layout.activity_two_player);
        ButterKnife.inject(this);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        //Set up the list with headers and items
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //Show which list element was clicked
                Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + ": " + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //setSupportActionBar(toolbar);

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
    }

    private void prepareListData() {
        //Set up items in expandableList
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("Wooden");
        listDataHeader.add("Metalic");
        listDataHeader.add("Blue and Red");

        List<String> menu1 = new ArrayList<String>();
        menu1.add("Menu1 Item1");
        menu1.add("Menu1 Item2");
        menu1.add("Menu1 Item3");


        listDataChild.put(listDataHeader.get(0), menu1);
    }
    public void StartTwoPlayer_intent(View view) {


        Intent intent = new Intent(TwoPlayer.this, GameActivity.class);
        intent.putExtra("gameMode", "TwoPlayer"); // Value from setting
        intent.putExtra("startingFen", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0"); // Always this one, it's starting position fen
        intent.putExtra("fenList", new ArrayList<String>()); // As in empty fenList
        intent.putExtra("themeId", R.mipmap.defaulttheme);
        //int difficulty_status = difficulty.getProgress();

        if(learning_tool.isChecked()){
            intent.putExtra("learningTool", true);
        }
        else
        {
            intent.putExtra("learningTool", false);
        }
        /*
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
        }*/
        //intent.putExtra("Difficulty", difficulty_status); //Setting Difficulty
        startActivity(intent);
    }
}