package com.theateam.checkmate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by AriPerkkio on 12/04/16.
 * SQLite Database for storing game's options + FEN-Strings
 *
 * Table Games:
 * Column 1: GameID, Integer, AutoIncrement, i.e. 4
 * Column 2: GameMode, String, i.e. "AiEasy"
 * Column 3: Date, Datetime, Default as current date, I.e. 1-1-2016
 *
 * Table FenList:
 * Column 1: GameID, Integer, Reference as foreign key to Games-table
 * Column 2: FenString, String i.e. "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0"
 *
 */
public class DatabaseManager {

    // Table Options and its columns
    private static final String DATABASE_TABLE_GAMES = "Games"; // Table Games
    public static final String games_GameId = "_id"; // Game ID, Integer
    public static final String games_GameMode = "GameMode"; // Game mode, String
    public static final String games_LearningTool = "LearningTool"; // Learning Tool On/Off, String
    public static final String games_date = "Date";

    // Table FenList and its columns
    private static final String DATABASE_TABLE_FENLIST = "FenList"; // Table FenList
    public static final String fenList_GameId = "_id"; // Game ID, Integer
    public static final String fenList_FenString = "FenString"; // FenString, String

    private static final int DATABASE_VERSION = 1; // Database version, 1st release as v.1

    // SQL-command to create Games table with its columns
    private static final String CREATE_TABLE_GAMES =
            "CREATE TABLE Games(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "GameMode TEXT, " +
                    "LearningTool TEXT, " +
                    "Date DATETIME DEFAULT CURRENT_DATE)";

    // SQL-command to create Fenlist table with its columns
    private static final String CREATE_TABLE_FENLIST =
            "CREATE TABLE FenList(_id integer, " +
                    "FenString TEXT, " +
                    "FOREIGN KEY (_id) REFERENCES Games(_id))";

    private final Context context;
    private MyDatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DatabaseManager(Context ctx) {
        this.context = ctx;
        DBHelper = new MyDatabaseHelper(context); // Create DbHelper class to handle db create/update
    }

    // Database helper inner class to handle db create/update
    private static class MyDatabaseHelper extends SQLiteOpenHelper {

        MyDatabaseHelper(Context context) {
            super(context, "Games", null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Run database creation statements
            db.execSQL(CREATE_TABLE_GAMES);
            db.execSQL(CREATE_TABLE_FENLIST);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE Games;");
            db.execSQL("DROP TABLE FenList;");
            db.execSQL(CREATE_TABLE_GAMES);
            db.execSQL(CREATE_TABLE_FENLIST);
        }
    } //End inner class

    // Open DB for r/w
    public DatabaseManager open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    // Close DB safely
    public void close() {
        DBHelper.close();
    }

    // Insert new row to Games table
    public long insertIntoGames(String GameMode, boolean learningTool) {
        // GameId is Auto incremented, Time is CURRENT_DATE by default
        ContentValues initialValues = new ContentValues();
        initialValues.put(games_GameMode, GameMode);
        if (learningTool) // Learning tool on
            initialValues.put(games_LearningTool, "ON");
        else // Learning tool off
            initialValues.put(games_LearningTool, "OFF");
        return db.insert(DATABASE_TABLE_GAMES, null, initialValues);
    }

    // Insert new row to Fenlist table
    public long insertIntoFenList(int gameId, String fenList) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(fenList_GameId, gameId);
        initialValues.put(fenList_FenString, fenList);
        return db.insert(DATABASE_TABLE_FENLIST, null, initialValues);
    }

    // Get all rows from Games table
    public Cursor getGames() throws SQLException {
        Cursor mCursor = db.query(true, DATABASE_TABLE_GAMES, new String[]{
                games_GameId, games_GameMode, games_LearningTool, games_date}, null, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    // Get highest gameid from Games table
    public int getHighestId() {
        final SQLiteStatement statement = db.compileStatement("SELECT MAX(_id) FROM Games");
        return (int) statement.simpleQueryForLong();
    }

    // Get FenStrings from Fenlist table
    public Cursor getFenStringsById(int rowId) throws SQLException {
        Cursor mCursor = db.query(true, DATABASE_TABLE_FENLIST, new String[]{
                fenList_GameId, fenList_FenString}, fenList_GameId + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    // Delete game and its fenStrings
    public void deleteGame(int gameId) throws SQLException {
        db.delete(DATABASE_TABLE_GAMES, "_id=" + gameId, null);
        db.delete(DATABASE_TABLE_FENLIST, "_id=" + gameId, null);
    }
}