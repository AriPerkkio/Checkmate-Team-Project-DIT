package com.theateam.checkmate;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;

public class PreviousFenlist extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener{

    private Button btnBack;
    private ImageButton btnNext;
    private ImageButton btnPrev;
    private ImageButton btnPlay;
    private ListView listFenlist;
    private DatabaseManager databaseManager;
    private Cursor cursorFenlist;
    private ArrayList<String> fenList = new ArrayList<String>();
    private ArrayAdapter arrayAdapter;
    private int gameId;
    private String gameMode;
    private String learningTool;
    private int themeId;
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
        Log.d("ThemeID: ", themeId+".");

        databaseManager = new DatabaseManager(this);
        try{
            databaseManager.open();
            cursorFenlist = databaseManager.getFenStringsById(gameId);
            databaseManager.close();
        }catch(SQLException e){
            Log.e("previousFenlist", "Read DB, e: "+e);
        }

        do{
            fenList.add(cursorFenlist.getString(1));
        }
        while(cursorFenlist.moveToNext());

        gameController = new GameController(gameMode, (learningTool.equals("ON")), fenList.get(0), fenList, themeId);
        setContentView(R.layout.activity_previous_fenlist);

        btnBack = (Button) findViewById(R.id.btnPrevfenlistBack);
        btnBack.setOnClickListener(this);
        btnNext = (ImageButton) findViewById(R.id.btnPrevfenNext);
        btnNext.setOnClickListener(this);
        btnPrev = (ImageButton) findViewById(R.id.btnPrevfenPrev);
        btnPrev.setOnClickListener(this);
        btnPlay = (ImageButton) findViewById(R.id.btnPrevfenPlay);
        btnPlay.setOnClickListener(this);
        listFenlist = (ListView) findViewById(R.id.prevfenlistList);
        listFenlist.setOnItemClickListener(this);

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.fenstringrow, fenList);
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
            case R.id.btnPrevfenlistBack:
                gameController = null;
                status = false;
                finish();
            break;
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
                Intent play =  new Intent(PreviousFenlist.this, GameActivity.class);
                play.putExtra("gameMode", gameMode);
                play.putExtra("learningTool", (learningTool.equals("ON")));
                play.putExtra("startingFen", fenList.get(fenList.size()-1)); // Last FEN from the fenList
                play.putExtra("fenList", fenList);
                play.putExtra("gameId", gameId);
                play.putExtra("themeId", themeId);
                startActivity(play);
                this.finish();
            break;
        }
    }
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long arg){
        gameController.previewFen(fenList.get(position));
        selectedFen = fenList.get(position);
    }


}
