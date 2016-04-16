package com.theateam.checkmate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.graphics.Point;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.drawer_recyclerView)
    RecyclerView drawerRecyclerView;
    private GameController gameController;
    private static GameActivity instance;
    private static String directory;
    private Button btnUndoMove;
    private Button btnSave;
    private String gameModeSelect;
    private boolean learningToolSwitch;
    private String gameStartingFen;
    private List<String> gameFenHistory;
    private DatabaseManager databaseManager = new DatabaseManager(this);
    private int gameId;
    private int themeId;
    private static boolean gameInCheckmate = false; // These will only be accessed from GameController
    private static boolean gameEnd = false; // Used to control GUI elements for checkmate and stalemate

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
        gameModeSelect = getIntent().getExtras().getString("gameMode");
        learningToolSwitch = getIntent().getExtras().getBoolean("learningTool");
        gameStartingFen = getIntent().getExtras().getString("startingFen");
        gameFenHistory = getIntent().getExtras().getStringArrayList("fenList");
        themeId = getIntent().getExtras().getInt("themeId");
        if(getIntent().getExtras().containsKey("gameId")) gameId = getIntent().getExtras().getInt("gameId");
        else gameId = 0; // New games have no gameId-key. Initialize gameId as 0.
        gameController = new GameController(gameModeSelect, learningToolSwitch, gameStartingFen, gameFenHistory, themeId);
        if(gameController.initialRotate()) // When starting Two Player game with turn 'b', board will be rotated when started -> Black screen for a while
            Toast.makeText(this, "Setting up rotated board...", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_game);
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
        DrawerAdapter drawerAdapter = new DrawerAdapter(rows);
        drawerRecyclerView.setAdapter(drawerAdapter);
        drawerRecyclerView.setHasFixedSize(true);
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnUndoMove = (Button) findViewById(R.id.btnRedo);
        btnUndoMove.setOnClickListener(this);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        instance = this;
        writeEngineToDevice();
        directory = getFilesDir().toString()+"/engines/";

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRedo:
                if(!gameController.undoMove())
                    Toast.makeText(this, "Board still at starting positions", Toast.LENGTH_LONG).show();
            break;
            case R.id.btnSave:
                gameFenHistory = gameController.getFenList();
                try{
                    databaseManager.open();
                    if(gameId==0){ // New game
                        databaseManager.insertIntoGames(gameModeSelect, learningToolSwitch, themeId); // Insert new game into table
                        gameId = databaseManager.getHighestId(); // Get its id
                    }
                    else // Saved game was continued
                        databaseManager.clearFenById(gameId); // Clear old game rows
                    for(int i=0;i<gameFenHistory.size();i++) // Insert all FEN-Strings
                        databaseManager.insertIntoFenList(gameId, gameFenHistory.get(i));
                    databaseManager.close();
                }catch(SQLException e){
                    Log.e("GameActivity", "insertFen, e: "+e.toString()); // Print error
                }
                Toast.makeText(this, "Game saved.", Toast.LENGTH_SHORT).show();
                finish();
            break;
        }
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
                drawerLayout.closeDrawers();
                break;
        }
    }

    public static void setCheckmate(int status){
        switch(status) {
            case 0: // No checkmate
                gameInCheckmate = false;
                break;
            case 1: // Player One checkmate
                gameInCheckmate = true;
                instance.setupDialog("Player One Checkmate", "Checkmate");
            break;
            case 2: // Player Two checkmate
                gameInCheckmate = true;
                instance.setupDialog("Player Two Checkmate", "Checkmate");
            break;
        }
    }

    public static void setEnding(int status){
        switch(status) {
            case 0: // Not ended by draw
                gameEnd = false;
            break;
            case 1: // Stalemate
                gameEnd = true;
                instance.setupDialog("Stalemate", "Game over");
            break;
            case 2: // Draw
                gameEnd = true;
                instance.setupDialog("Players draw", "Game over");
            break;
            case 3: // Insufficient material
                gameEnd = true;
                instance.setupDialog("Insufficient material", "Game over");
            break;
        }
    }

    private void setupDialog(String _message, String _title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(_message).setTitle(_title); // Add message and title for dialog
        builder.setPositiveButton("Save & exit", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                btnSave.callOnClick(); // Make click for save button
            }
        });
        builder.setNegativeButton("UndoMove", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                btnUndoMove.callOnClick(); // Click undoMove button
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String getDirectory(){
        return directory;
    }

    private void writeEngineToDevice() {
        directory = getApplicationContext().getFilesDir().toString()+"/engines/";

        File engineDir = new File(directory);
        if (!engineDir.exists()) {
            Log.i("Writing", "Engine");
            Log.i("Writing", "Directory"+directory);
            engineDir.mkdirs();
            copyEngineDir();
        }
        else
            Log.d("writeEngineToDevice", "Directory exists");
    }

    // Write chess AI engine from /res/raw/ to applications directory
    private void copyEngineDir() {
        InputStream in;
        OutputStream out;
        try {
            in = getResources().openRawResource(R.raw.stockfish); // Get stockfish raw binary file from /res/raw/
            out = new FileOutputStream(directory + "stockfish"); // Create new file to applications directory
            copyFile(in, out); // Copy file
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("copyEngineDir", e.toString());
        }
        try {
            Runtime.getRuntime().exec("chmod 777 "+directory+"stockfish"); // Set executable
        }catch(Exception e){
            Log.e("Chmod777", e.toString());
        }
    }
    // Reference: http://stackoverflow.com/a/5450828
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    public static int getscreenwidth()
    {
        Display display = instance.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;

    }
    public static int getscreenheight()
    {
        Display display = instance.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        return height;
    }
    public static String getorientation()
    {
        Display display = ((WindowManager)instance.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        String orientation = "";
        if (Surface.ROTATION_0 == rotation) {
            orientation = "portrait";
        } else if(Surface.ROTATION_180 == rotation) {
            orientation = "portrait";
        } else if(Surface.ROTATION_90 == rotation) {
            orientation = "landscape";
        } else if(Surface.ROTATION_270 == rotation) {
            orientation = "landscape";
        }
        return orientation;
    }
 }

