package com.passeapp.dark_legion.cradioapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class AppDataBase extends SQLiteOpenHelper{

    SQLiteDatabase db;
    String sqlCreate = "CREATE TABLE Sponsor (id INTEGER, name TEXT, link TEXT, lat TEXT, lon TEXT, address TEXT, imageLink TEXT)";


    public AppDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Sponsor");
        sqLiteDatabase.execSQL(sqlCreate);
    }

    public void setSponsor(SQLiteDatabase sqLiteDatabase, SponsorsClass sponsor){
        ContentValues cv = new ContentValues();
        cv.put("name",sponsor.getName());
        cv.put("link",sponsor.getLink());
        cv.put("lat",sponsor.getLat().toString());
        cv.put("lon",sponsor.getLon().toString());
        cv.put("address",sponsor.getAddress());
        cv.put("imageLink",sponsor.getImageLink());
        db = this.getWritableDatabase();
        db.insert("Sponsor",null, cv);
    }

    public Cursor getSponsor(String name) {
        this.db = this.getReadableDatabase();
        String[] args = new String[]{name};
        Cursor c = this.db.rawQuery("SELECT * FROM Sponsor WHERE name=?",args);
        c.moveToFirst();
        return c;
    }

    public boolean hasSponsor(String name){
        Cursor c = getSponsor(name);
        return c.isFirst();
    }

    public boolean deleteSponsor(SQLiteDatabase sqLiteDatabase, String name){
        sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete("Sponsor","name=?", new String[] { name }) > 0;
    }


    public boolean deleteAllSponsor(SQLiteDatabase sqLiteDatabase){
        db = this.getWritableDatabase();
        return db.delete("Sponsor",null, null) > 0;
    }

    public ArrayList<SponsorsClass> getSponsorsRows(){
        ArrayList<SponsorsClass> sponsorsList = new ArrayList<>();
        this.db = this.getReadableDatabase();
        Cursor c = this.db.rawQuery("SELECT * FROM Sponsor",null);
        try {
            while (c.moveToNext()) {
                SponsorsClass aux = new SponsorsClass(c.getInt(0),c.getString(1),c.getString(2),Double.parseDouble(c.getString(3)),Double.parseDouble(c.getString(4)),c.getString(5),c.getString(6));
                sponsorsList.add(aux);
            }
        } finally {
            c.close();
        }
        return sponsorsList;
    }



}
