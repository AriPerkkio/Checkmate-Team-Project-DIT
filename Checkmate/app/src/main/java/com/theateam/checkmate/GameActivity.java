package com.theateam.checkmate;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.view.Display;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



public class GameActivity extends Activity {

    //public static TextView coordinates;
    private GameController gameController = GameController.getInstance(); // At the moment this makes sure there is atleas one instance of gameController
    private static GameActivity instance;
    private static String directory;
    public static TextView textField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //Set full screen
        // Hide navigation bar and keep it hidden when pressing
        /** Chris work here **/
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        textField = (TextView) findViewById(R.id.textField);
        instance = this;
        writeEngineToDevice();
        directory = getFilesDir().toString()+"/engines/";
    }
    public static GameActivity getInstance(){
        if(instance==null) Log.e("Instance", "null");
        return instance;
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

