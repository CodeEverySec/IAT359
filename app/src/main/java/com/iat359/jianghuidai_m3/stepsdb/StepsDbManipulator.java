package com.iat359.jianghuidai_m3.stepsdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class StepsDbManipulator {
    // the helper
    private StepsDbOpenHelper helper;

    // the database
    private SQLiteDatabase db;

    public StepsDbManipulator(Context context) {
        helper = new StepsDbOpenHelper(context);
        // get the database
        db = helper.getWritableDatabase();
    }

    // Close database
    public void close() {
        db.close();
    }

    // Insert new data, return the id
    public long insertToStepsEntry(String name, String type) {
        return db.insert(StepsDbContract.StepsEntry.TABLE_NAME,
                null,
                // create ContentValues from given name and type
                StepsDbContract.StepsEntry.fromData(name, type));
    }

    // Select all data
    public List<ContentValues> selectAllFromStepsEntry() {
        Cursor cursor = db.query(
                StepsDbContract.StepsEntry.TABLE_NAME,
                StepsDbContract.StepsEntry.COLUMN_LIST,
                null, null, null, null, null);
        return readAllValuesFromCursor(cursor);
    }

    // Select rows based on given steps type (user) in steps entry
    public List<ContentValues> selectTypeFromStepsEntry(String stepsType) {
        // filtering condition
        String condition = String.format("%s=?", StepsDbContract.StepsEntry.COLUMN_TYPE);
        String[] args = new String[]{stepsType};

        // use cursor to iterate row by row of the result
        Cursor cursor = db.query(
                StepsDbContract.StepsEntry.TABLE_NAME,
                StepsDbContract.StepsEntry.COLUMN_LIST,
                condition, args, null, null, null);

        return readAllValuesFromCursor(cursor);
    }

    // Clear all rows in steps entry
    public void clearStepsEntry() {
        db.execSQL(String.format("DELETE FROM %s", StepsDbContract.StepsEntry.TABLE_NAME));
    }

    // Read all rows from given cursor
    private List<ContentValues> readAllValuesFromCursor(Cursor cursor) {
        List<ContentValues> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(StepsDbContract.StepsEntry.fromCursor(cursor));
        }

        cursor.close();
        return results;
    }
}
