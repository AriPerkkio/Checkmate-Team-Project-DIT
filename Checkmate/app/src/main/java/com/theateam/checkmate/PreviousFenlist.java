package com.theateam.checkmate;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PreviousFenlist extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener{

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.drawer_recyclerView)
    RecyclerView drawerRecyclerView;
    private ImageButton btnNext;
    private ImageButton btnPrev;
    private ImageButton btnPlay;
    private ListView listFenlist;
    private DatabaseManager databaseManager;
    private Cursor cursorFenlist;
    private ArrayList<String> fenList = new ArrayList<String>();
    private ArrayList<String> parsedFenList = new ArrayList<String>();
    private ArrayList<String> moveList = new ArrayList<String>();
    private Map<String, long[]> fenToTimers = new HashMap<String, long[]>();
    private long[] timerOne;
    private long[] timerTwo;
    private ArrayAdapter arrayAdapter;
    private int gameId;
    private String gameMode;
    private String learningTool;
    private int themeId;
    private int timeLimit;
    private GameController gameController;
    private static boolean status;
    private String selectedFen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        status = true;
        gameId = getIntent().getExtras().getInt("GameId");
        gameMode = getIntent().getExtras().getString("GameMode");
        learningTool = getIntent().getExtras().getString("LearningTool");
        themeId = getIntent().getExtras().getInt("ThemeId");
        timeLimit = getIntent().getExtras().getInt("TimeLimit");

        databaseManager = new DatabaseManager(this);
        try{
            databaseManager.open();
            cursorFenlist = databaseManager.getFenStringsById(gameId);
            databaseManager.close();
        }catch(SQLException e){
            Log.e("previousFenlist", "Read DB, e: "+e);
        }

        timerOne = new long[cursorFenlist.getCount()];
        timerTwo = new long[cursorFenlist.getCount()];
        int i=0;
        do{
            fenList.add(cursorFenlist.getString(1));
            fenToTimers.put(cursorFenlist.getString(1), new long[]{(long) cursorFenlist.getInt(2), (long) cursorFenlist.getInt(3)});
            timerOne[i] = (long) cursorFenlist.getInt(2);
            timerTwo[i] = (long) cursorFenlist.getInt(3);
            i++;
        }
        while(cursorFenlist.moveToNext());

        gameController = new GameController(gameMode, (learningTool.equals("ON")), fenList.get(0), fenList, fenToTimers, timeLimit, themeId);
        setContentView(R.layout.activity_previous_fenlist);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(this.getResources().getColor(R.color.colorAccent));
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

        btnNext = (ImageButton) findViewById(R.id.btnPrevfenNext);
        btnNext.setOnClickListener(this);
        btnPrev = (ImageButton) findViewById(R.id.btnPrevfenPrev);
        btnPrev.setOnClickListener(this);
        btnPlay = (ImageButton) findViewById(R.id.btnPrevfenPlay);
        btnPlay.setOnClickListener(this);
        listFenlist = (ListView) findViewById(R.id.prevfenlistList);
        listFenlist.setOnItemClickListener(this);

        int j=0;
        do{
            parsedFenList.add(fenList.get(j));

            if(j == fenList.size()-2){
                j++;
            } else if(j == fenList.size() -1){
                j  = fenList.size();
            } else {
                j+=2;
            }
        }while(j != fenList.size());

        for(j=0;j<parsedFenList.size();j++){
            moveList.add("Move " + (j));
        }

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.fenstringrow, moveList);
        listFenlist.setAdapter(arrayAdapter);
        selectedFen = fenList.get(0);
    }

    // Indicates if this activity is on
    public static boolean getStatus(){
        return status;
    }
    public static void setStatus(boolean _status){ status = _status; }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnPrevfenNext:
                if(fenList.indexOf(selectedFen)==fenList.size()-1)
                    break;
                selectedFen = fenList.get(fenList.indexOf(selectedFen)+1);
                gameController.previewFen(selectedFen);
            break;
            case R.id.btnPrevfenPrev:
                if(fenList.indexOf(selectedFen)==0)
                    break;
                selectedFen = fenList.get(fenList.indexOf(selectedFen)-1);
                gameController.previewFen(selectedFen);
            break;
            case R.id.btnPrevfenPlay:
                gameController = null;
                status = false;

                Log.i("Play","**************************");
                int index = fenList.indexOf(selectedFen); // Index of selected fen
                Log.d("Play", "Picked index "+index);
                Log.d("Play", "Picked FEN "+selectedFen);
                Log.d("Play", "FenList last index: "+ (fenList.size()-1));
                for(int i=0;i<fenList.size();i++)
                    Log.i("Play "+i, fenList.get(i));

                do{ // Remove all fens after selected one
                    fenList.remove(fenList.size()-1); // Remove last one
                }while(fenList.size()-1!=index); // Reached selected fen

                Log.d("Play", "Modified FenList, last index: "+ (fenList.size()-1));
                for(int i=0;i<fenList.size();i++)
                    Log.i("Play "+i, fenList.get(i));

                Intent play =  new Intent(PreviousFenlist.this, GameActivity.class);
                play.putExtra("gameMode", gameMode);
                play.putExtra("learningTool", (learningTool.equals("ON")));
                play.putExtra("startingFen", selectedFen); // Last FEN from the fenList | EDIT , start from selected fen
                play.putExtra("fenList", fenList);
                play.putExtra("gameId", gameId);
                play.putExtra("themeId", themeId);
                play.putExtra("timerOne", timerOne);
                play.putExtra("timerTwo", timerTwo);
                play.putExtra("timeLimit", timeLimit);
                startActivity(play);
                this.finish();
            break;
        }
    }
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long arg){
        gameController.previewFen(parsedFenList.get(position));
        selectedFen = parsedFenList.get(position);
    }

    public void drawerClick(View v) {
        String clickedText = ((TextView) v).getText().toString();
        switch (clickedText) {
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
            case "Resume":
                try {
                    databaseManager.open();
                    int latestId = databaseManager.getHighestId();
                    if (latestId == 0) {
                        Toast.makeText(this, "No games found", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    Cursor fenCursor = databaseManager.getFenStringsById(latestId);
                    Cursor gameSettings = databaseManager.getSettingsById(latestId);
                    databaseManager.close();
                    ArrayList<String> fenList = new ArrayList<>();
                    long[] timerOne = new long[fenCursor.getCount()];
                    long[] timerTwo = new long[fenCursor.getCount()];
                    ;
                    int i = 0;
                    do {
                        fenList.add(fenCursor.getString(1));
                        timerOne[i] = (long) fenCursor.getInt(2);
                        timerTwo[i] = (long) fenCursor.getInt(3);
                        i++;
                    }
                    while (fenCursor.moveToNext());
                    intent = new Intent(this, GameActivity.class);
                    intent.putExtra("gameMode", gameSettings.getString(0));
                    intent.putExtra("learningTool", (gameSettings.getString(1).equals("ON")));
                    intent.putExtra("themeId", gameSettings.getInt(2));
                    intent.putExtra("fenList", fenList);
                    intent.putExtra("startingFen", fenList.get(fenList.size() - 1));
                    intent.putExtra("timerOne", timerOne);
                    intent.putExtra("timerTwo", timerTwo);
                    intent.putExtra("gameId", latestId);
                    intent.putExtra("timeLimit", gameSettings.getInt(3));
                    status = false;
                    startActivity(intent);
                } catch (SQLException e) {
                    Log.e("DrawerResume", "e: " + e.toString());
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        status = false;
    }
}
