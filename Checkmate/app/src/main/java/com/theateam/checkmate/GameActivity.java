package com.theateam.checkmate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private DatabaseManager databaseManager = new DatabaseManager(this);
    private int gameId;
    private int themeId;
    private static boolean gameInCheckmate = false; // These will only be accessed from GameController
    private static boolean gameInStalemate = false; // Used to control GUI elements for checkmate and stalemate

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
        Log.d("GameAcrtivity", "GameID: "+gameId);
        gameController = new GameController(gameModeSelect, learningToolSwitch, gameStartingFen, gameFenHistory, themeId);
        if(gameController.initialRotate()) // When starting Two Player game with turn 'b', board will be rotated when started -> Black screen for a while
            Toast.makeText(this, "Setting up rotated board...", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_game);
        textField = (TextView) findViewById(R.id.textField);
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

    public static void setCheckmate(int status){
        switch(status) {
            case 0: // No checkmate
                gameInCheckmate = false;
                Log.d("GameActivity", "setCheckmate: "+status);
                textField.setText("");
                break;
            case 1: // Player One checkmate
                gameInCheckmate = true;
                Log.d("GameActivity", "setCheckmate playerOne: "+status);
                textField.setText("PlayerOne checkmate");
                instance.setupDialog("Player One Checkmate", "Checkmate");
            break;
            case 2: // Player Two checkmate
                gameInCheckmate = true;
                Log.d("GameActivity", "setCheckmate playerTwo: "+status);
                textField.setText("PlayerTwo checkmate");
                instance.setupDialog("Player Two Checkmate", "Checkmate");
            break;
        }
    }

    public static boolean getCheckmateStatus(){
        return gameInCheckmate;
    }

    public static void setStalemate(int status){
        switch(status) {
            case 0: // No checkmate
                gameInStalemate = false;
                Log.d("GameActivity", "setStalemate: "+status);
                textField.setText("");
            break;
            case 1: // Player One stalemate
                gameInStalemate = true;
                Log.d("GameActivity", "setStalemate playerOne: "+status);
                textField.setText("PlayerOne stalemate");
            break;
            case 2: // Player Two stalemate
                gameInStalemate = true;
                Log.d("GameActivity", "setStalemate playerTwo: "+status);
                textField.setText("PlayerTwo stalemate");
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

