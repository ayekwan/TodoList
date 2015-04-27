package com.example.student.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by student on 2/23/15 AD.
 */
public class TodoDBHelper extends SQLiteOpenHelper {
    private static final String name = "todo.sqlite3";
    private static final int version = 2;


    public TodoDBHelper(Context ctx) {
        super(ctx, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE todo (" +
                "_id integer primary key autoincrement," +
                "title text not null," +
                "detail text not null);";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS todo;";
        db.execSQL(sql);
        this.onCreate(db);
    }
}
