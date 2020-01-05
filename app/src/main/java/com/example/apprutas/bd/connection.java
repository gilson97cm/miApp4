package com.example.apprutas.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class connection extends SQLiteOpenHelper {
    public connection(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user (id TEXT PRIMARY KEY, name TEXT,  lastName TEXT, avatar BLOG)");
        db.execSQL("CREATE TABLE route (id INTEGER PRIMARY KEY, city TEXT, lat TEXT, lang TEXT, idUser TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}