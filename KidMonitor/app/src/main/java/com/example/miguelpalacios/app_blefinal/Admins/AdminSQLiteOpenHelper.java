package com.example.miguelpalacios.app_blefinal.Admins;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by miguelpalacios on 08/05/15.
 */
public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table dispositivos(clave TEXT PRIMARY KEY, nombre TEXT, mac TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("drop table if exist dispositivos");
        sqLiteDatabase.execSQL("create table dispositivos(clave TEXT PRIMARY KEY, nombre TEXT, mac TEXT)");
    }
}
