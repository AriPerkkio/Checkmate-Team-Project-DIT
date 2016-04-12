package com.theateam.checkmate;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.graphics.Point;
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


public class GameActivity extends Activity implements View.OnClickListener{

    //public static TextView coordinates;
    private GameController gameController;
    private static GameActivity instance;
    private static String directory;
    public static TextView textField;
    private Button btnUndoMove;
    private Button btnSave;
    private String gameModeSelect;
    private boolean learningToolSwitch;
    private String gameStartingFen;
    private List<String> gameFenHistory;
    private DatabaseManager databaseManager;
    private int gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //Set full screen
        // Hide navigation bar and keep it hidden when pressing
        /** Chris work here **/
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

        // TODO: Read values from previous activity - i.e. getIntent().getExtras().
        /** Get values for these from settings menu **/
        gameModeSelect = "AiMedium"; //getIntent().getExtras().getString("gameMode");
        learningToolSwitch = true; //getIntent().getExtras().getBoolean("learningTool");
        gameStartingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0"; //getIntent().getExtras().getString("startingFen");
        gameFenHistory = new ArrayList<>(); // getIntent().getExtras().getStringArrayList() ???
        /*********************************************/
        gameController = new GameController(gameModeSelect, learningToolSwitch, gameStartingFen, gameFenHistory);
        setContentView(R.layout.activity_game);

        databaseManager = new DatabaseManager(this);
        try {
            databaseManager.open(); // Open DB
            databaseManager.insertIntoGames(gameModeSelect, learningToolSwitch); // Insert new game into table
            gameId = databaseManager.getHighestId();
            databaseManager.close(); // Close DB
        }catch (SQLException e){ // Sql error
            Log.e("GameActivity", "insrtGame, e: "+e.toString()); // Print error
        }
        textField = (TextView) findViewById(R.id.textField);
        btnUndoMove = (Button) findViewById(R.id.btnRedo);
        btnUndoMove.setOnClickListener(this);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        instance = this;
        writeEngineToDevice();
        directory = getFilesDir().toString()+"/engines/";

    }
    public static GameActivity getInstance(){
        if(instance==null) Log.e("Instance", "null");
        return instance;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRedo:
                if(!gameController.undoMove())
                    Toast.makeText(this, "Board still at starting positions", Toast.LENGTH_LONG).show();
            break;
            case R.id.btnSave:
                gameFenHistory = gameController.getFenList();
                Log.d("GameActivity", "gameFenHistGet, Size: "+gameFenHistory.size());
                try{
                    databaseManager.open();
                    for(int i=0;i<gameFenHistory.size();i++)
                        databaseManager.insertIntoFenList(gameId, gameFenHistory.get(i));
                    databaseManager.close();
                }catch(SQLException e){
                    Log.e("GameActivity", "insrtFen, e: "+e.toString()); // Print error
                }
                Toast.makeText(this, "Game saved, shutting down...", Toast.LENGTH_SHORT).show();
                finish();
            break;
        }
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

