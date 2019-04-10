package com.iat359.jianghuidai_m3.stepsdb;

import android.content.ContentValues;
import android.database.Cursor;

// learn how to implement this class from week 6's lab code
public final class StepsDbContract {
    public static final String DATABASE_NAME = "StepsDatabase";
    public static final int DATABASE_VERSION = 1;

    // Inner class to define Steps table's schema
    public static class StepsEntry {

        // name of the table
        public static final String TABLE_NAME = "Steps";

        // columns of the table
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_TYPE = "Type";

        // database creation command
        public static final String CREATE_COMMAND =
                String.format("CREATE TABLE %s ( %s %s, %s %s  );",
                        TABLE_NAME,
                        COLUMN_NAME, "TEXT",
                        COLUMN_TYPE, "TEXT");

        // database deletion command
        public static final String DELETE_COMMAND =
                String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);

        // database order command base on steps
        public static final String ORDER_COMMAND =
                String.format("%s DESC", COLUMN_TYPE);

        // get column lists
        public static final String[] COLUMN_LIST = {"_rowid_", COLUMN_NAME, COLUMN_TYPE};

        // get ContentValues from given data
        public static ContentValues fromData(String name, String type) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_TYPE, type);
            return values;
        }

        // get ContentValues from current cursor
        // Iterate through all columns
        public static ContentValues fromCursor(Cursor cursor) {
            ContentValues values = new ContentValues();

            // Name
            int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
            if (nameIndex > -1) {
                values.put(COLUMN_NAME, cursor.getString(nameIndex));
            }
            // Type
            int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);
            if (nameIndex > -1) {
                values.put(COLUMN_TYPE, cursor.getString(typeIndex));
            }
            return values;
        }
    }
}
