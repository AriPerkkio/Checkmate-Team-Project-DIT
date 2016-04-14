package com.theateam.checkmate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuActivity extends Activity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    Button startGameButton;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //Set full screen
        setContentView(R.layout.activity_menu);

        //get expandablelistview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        //Set up the list with headers and items
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        //Onclick Listener for the items in each list
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //Show which list element was clicked
                Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + ": " + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        startGameButton = (Button) findViewById(R.id.startGame);
        startGameButton.setText("START");
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                //use putExtra to send values through after starting gameActivity
                intent.putExtra("gameMode", "AiEasy"); // Value from setting
                intent.putExtra("learningTool", true); // (learningTool.equals("ON"))); // Value from setting
                intent.putExtra("startingFen", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0"); // Always this one, it's starting position fen
                intent.putExtra("fenList", new ArrayList<String>()); // As in empty fenList
                MenuActivity.this.startActivity(intent);
            }
        });
    }

    private void prepareListData() {
        //Set up items in expandableList
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("Menu list #1");
        listDataHeader.add("Menu list #2");
        listDataHeader.add("Menu list #3");

        List<String> menu1 = new ArrayList<String>();
        menu1.add("Menu1 Item1");
        menu1.add("Menu1 Item2");
        menu1.add("Menu1 Item3");


        List<String> menu2 = new ArrayList<String>();
        menu2.add("Menu2 Item1");
        menu2.add("Menu2 Item2");
        menu2.add("Menu2 Item3");
        menu2.add("Menu2 Item4");
        menu2.add("Menu2 Item5");
//
        List<String> menu3 = new ArrayList<String>();
        menu3.add("Menu3 Item1");
        menu3.add("Menu3 Item2");
        menu3.add("Menu3 Item3");

        listDataChild.put(listDataHeader.get(0), menu1);
        listDataChild.put(listDataHeader.get(1), menu2);
        listDataChild.put(listDataHeader.get(2), menu3);
    }

}
