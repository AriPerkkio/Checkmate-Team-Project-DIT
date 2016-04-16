package com.theateam.checkmate;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class PreviousGames extends AppCompatActivity implements ListView.OnItemClickListener {

    private ListView listGames;
    private Button btnBack;
    private Cursor cursorGames;
    private prevGamesCursorAdapter cursorAdapter;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //Set full screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_games);

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
        this.startActivity(prevFenIntent);
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
}
