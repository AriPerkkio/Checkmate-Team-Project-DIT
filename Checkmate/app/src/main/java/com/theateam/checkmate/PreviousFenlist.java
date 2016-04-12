package com.theateam.checkmate;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class PreviousFenlist extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener{

    private Button btnBack;
    private ListView listFenlist;
    private DatabaseManager databaseManager;
    private Cursor cursorFenlist;
    private ArrayList<String> fenList = new ArrayList<String>();
    private ArrayAdapter arrayAdapter;
    private int gameId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_fenlist);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //Set full screen

        btnBack = (Button) findViewById(R.id.btnPrevfenlistBack);
        btnBack.setOnClickListener(this);
        listFenlist = (ListView) findViewById(R.id.prevfenlistList);
        listFenlist.setOnItemClickListener(this);
        gameId = getIntent().getExtras().getInt("GameId");

        databaseManager = new DatabaseManager(this);
        try{
            databaseManager.open();
            cursorFenlist = databaseManager.getFenStringsById(gameId);
            databaseManager.close();
        }catch(SQLException e){
            Log.e("previousFenlist", "Read DB, e: "+e);
        }

        while(cursorFenlist.moveToNext())
            fenList.add(cursorFenlist.getString(1));

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.fenstringrow, fenList);
        listFenlist.setAdapter(arrayAdapter);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnPrevfenlistBack:
                finish();
            break;
        }
    }
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long arg){
        Toast.makeText(this, "Clicked row "+position, Toast.LENGTH_SHORT).show();
    }


}
