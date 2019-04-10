package com.iat359.jianghuidai_m3.stepsdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StepsDbOpenHelper extends SQLiteOpenHelper {
    // Tag for logging messages
    private static final String LOGGING_TAG = "StepsDB";

    public StepsDbOpenHelper(Context context) {
        super(context, StepsDbContract.DATABASE_NAME, null, StepsDbContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // create the tables
            db.execSQL(StepsDbContract.StepsEntry.CREATE_COMMAND);
            Log.i(LOGGING_TAG, "tables has been successfully created!");
        } catch (Exception e) {
            Log.i(LOGGING_TAG, "Failed to create tables");
        }
    }

    // This method will be called when the version of the database has been upgraded
    // (newVersion > oldVersion)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // recreate the Steps table
        try {
            // delete all tables first
            db.execSQL(StepsDbContract.StepsEntry.DELETE_COMMAND);
            // create the tables again
            onCreate(db);
            Log.i(LOGGING_TAG, "tables has been successfully upgraded");
        } catch (Exception e) {
            Log.i(LOGGING_TAG, "Failed to upgrade tables");
        }
    }

    // The method will be called when the version has been downgraded
    // (newVersion < oldVersion)
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onUpgrade(db, oldVersion, newVersion);
    }
}
