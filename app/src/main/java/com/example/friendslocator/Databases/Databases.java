package com.example.friendslocator.Databases;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.friendslocator.models.Contacts;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Databases extends SQLiteAssetHelper {
    private static final String DB_NAME = "locatorDB.db";
    private static final int DB_VER = 1;

    public Databases(Context context) {
        super(context, DB_NAME, null, DB_VER);


    }

    public List<Contacts> getContacts() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = {"name", "email", "phone", "imageURL","status"};
        String sqlTable = "Contacts";
        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
        final List<Contacts> result = new ArrayList<>();
        if (c.moveToFirst()) {

            do {
                result.add(new Contacts(c.getString(c.getColumnIndex("name")),
                        c.getString(c.getColumnIndex("imageURL")),
                        c.getString(c.getColumnIndex("email")),
                        c.getString(c.getColumnIndex("phone")),
                        c.getString(c.getColumnIndex("status"))
                ));

            }
            while (c.moveToNext());


        }
        return result;

    }

    public void addContact(Contacts contacts) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Contacts (name,email,phone,imageURL,lng,lat,status)VALUES('%s' , '%s' , '%s' , '%s' , '%s','%s','%s');",

                contacts.getName(), contacts.getEmail(), contacts.getPhone(), contacts.getImageURL(),contacts.getLng(),contacts.getLat(),contacts.getStatus());
        db.execSQL(query);


    }

    public void clearContacts() {

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Contacts");
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);

    }


    public boolean isContactInserted(String contactPhone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Contacts WHERE phone='%s';", contactPhone);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;

        }
        cursor.close();
        return true;

    }
}
