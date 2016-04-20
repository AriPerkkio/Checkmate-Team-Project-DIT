

package com.theateam.checkmate;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by FionnMcguire on 31/03/2016.
 */
public class Home extends AppCompatActivity{
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.drawer_recyclerView)
    RecyclerView drawerRecyclerView;
    private DatabaseManager db;
    private static String directory;
    public static String screenRatio;

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
        setContentView(R.layout.activity_layout);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        db = new DatabaseManager(this);
        directory = getFilesDir().toString()+"/engines/";
        writeEngineToDevice();

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

        if(getIntent().hasExtra("Exit")) finish();
        double rawRatio = getScreenRatio();
        if((int) (rawRatio*16) == 9) screenRatio = "16:9"; // I.e. 1920x1080, 2560x1440
        if((int) (rawRatio*16) == 10) screenRatio = "16:10"; // I.e. 1280×800
        if((int) (rawRatio*5) == 3) screenRatio = "5:3"; // I.e. 800*480 | Update: Same thing as 15:9...
        if((int) (rawRatio*3) == 2) screenRatio = "3:2"; // I.e. 1200x800
        if((int) (rawRatio*15) == 9) screenRatio = "15:9"; // I.e. 800*480
        if(screenRatio==null) Toast.makeText(this, "Device screen ratio is not supported", Toast.LENGTH_LONG).show();
        Log.d("Home", "Picked screen ratio: "+screenRatio);
    }
    public void OnePlayer_intent(View view) {
        Intent intent = new Intent(this, OnePlayer.class);
        startActivity(intent);
    }
    public void TwoPlayer_intent(View view) {
        Intent intent = new Intent(this, TwoPlayer.class);
        startActivity(intent);
    }

    public void Analysis_intent(View view) {
        Intent intent = new Intent(this, PreviousGames.class);
        startActivity(intent);
    }

    public void drawerClick(View v){
        String clickedText = ((TextView) v).getText().toString();
        switch(clickedText){
            case "Update":
                Toast.makeText(this, "You are already running ad-free version", Toast.LENGTH_SHORT).show();
            break;
            case "Home":
                drawerLayout.closeDrawers();
            break;
            case "Analysis":
                Analysis_intent(v);
            break;
            case "Resume":
                try{
                    db.open();
                    int latestId = db.getHighestId();
                    if(latestId==0){
                        Toast.makeText(this, "No games found", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    Cursor fenCursor = db.getFenStringsById(latestId);
                    Cursor gameSettings = db.getSettingsById(latestId);
                    db.close();
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
                    Intent intent = new Intent(this, GameActivity.class);
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
                Intent intent = new Intent(this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Exit", true);
                startActivity(intent);
            break;
        }
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

    private double getScreenRatio() {
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return (double) size.x / (double) size.y;
    }
}

