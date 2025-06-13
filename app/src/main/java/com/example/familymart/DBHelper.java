package com.example.familymart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create Table Userdetails(name TEXT primary key, password TEXT, email TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop Table if exists Userdetails");
    }

    public Boolean insertUserData(String name, String password, String email) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("password", password);
        contentValues.put("email", email);
        long result = DB.insert("Userdetails", null, contentValues);
        return result != -1;
    }

    public Cursor getdata() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Userdetails", null);
        return cursor;
    }

    public Boolean getLogin(String name, String password) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Userdetails where name = ? and password = ?", new String[]{name, password});
        return cursor.getCount() != 0;
    }


    public Boolean deleteData(String name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Userdetails where name = ?", new String[]{name});
        if (cursor.getCount() == 0)
            return false;
        long result = DB.delete("Userdetails", "name=?", new String[]{name});
        return result != -1;
    }

    public Cursor searchData(String name) {
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Userdetails where name = ?", new String[]{name});
        return cursor;
    }
}
