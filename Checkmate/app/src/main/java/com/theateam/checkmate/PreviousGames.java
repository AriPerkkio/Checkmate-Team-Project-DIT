package com.theateam.checkmate;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class PreviousGames extends AppCompatActivity implements ListView.OnItemClickListener{

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.drawer_recyclerView)
    RecyclerView drawerRecyclerView;
    private ListView listGames;
    private Button btnBack;
    private Cursor cursorGames;
    private prevGamesCursorAdapter cursorAdapter;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_previous_games);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(this.getResources().getColor(R.color.colorAccent));
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

        listGames = (ListView) findViewById(R.id.listPrevGames);
        listGames.setOnItemClickListener(this);

        databaseManager = new DatabaseManager(this);
        try{
            databaseManager.open();
            cursorGames = databaseManager.getGames();
            databaseManager.close();
        }catch(SQLException e){
            Log.e("previousGames", "Read DB, e: "+e);
        }

        cursorAdapter = new prevGamesCursorAdapter(this, cursorGames, 0);
        listGames.setAdapter(cursorAdapter);

    }


    public void onItemClick(AdapterView<?> adapterView, View v, int position, long arg){
        Cursor selectedObject = (Cursor) (listGames.getItemAtPosition(position));
        Intent prevFenIntent = new Intent(PreviousGames.this, PreviousFenlist.class);
        prevFenIntent.putExtra("GameId", selectedObject.getInt(0));
        prevFenIntent.putExtra("GameMode", selectedObject.getString(1));
        prevFenIntent.putExtra("LearningTool", selectedObject.getString(2));
        prevFenIntent.putExtra("ThemeId", selectedObject.getInt(3));
        prevFenIntent.putExtra("TimeLimit", getTimitLimit(selectedObject.getInt(0)));
        this.startActivity(prevFenIntent);
    }

    public int getTimitLimit(int _gameId){
        int returnInteger = 1800; // Default initialize
        try{
            databaseManager.open();
            Cursor timeCursor = databaseManager.getSettingsById(_gameId);
            databaseManager.close();
            returnInteger = timeCursor.getInt(3);
        }catch(SQLException e){
            Log.e("getTimiLimit", "e: "+e.toString());
        }
        return returnInteger;
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
                drawerLayout.closeDrawers();
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
                    intent.putExtra("gameId", latestId);
                    intent.putExtra("timeLimit", gameSettings.getInt(3));
                    startActivity(intent);
                }catch(SQLException e){
                    Log.e("DrawerResume", "e: "+e.toString());
                }
                break;
        }
    }

    // Cursor adapter for Games
    private class prevGamesCursorAdapter extends CursorAdapter {
        private LayoutInflater cursorInflater;

        public prevGamesCursorAdapter (Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
            cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void bindView(View view, Context context, Cursor cursor) {
            TextView gameId = (TextView) view.findViewById(R.id.prevgamerowId);
            TextView gameMode = (TextView) view.findViewById(R.id.prevgamerowGamemode);
            TextView gameLearningTool = (TextView) view.findViewById(R.id.prevgamerowLearnTool);
            TextView gameDate = (TextView) view.findViewById(R.id.prevgamerowDate);

            gameId.setText(""+cursor.getInt(0));
            gameMode.setText(cursor.getString(1));
            gameLearningTool.setText(cursor.getString(2));
            gameDate.setText(cursor.getString(4));
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return cursorInflater.inflate(R.layout.prevgamesrow, parent, false);
        }
    }// Close inner class

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
