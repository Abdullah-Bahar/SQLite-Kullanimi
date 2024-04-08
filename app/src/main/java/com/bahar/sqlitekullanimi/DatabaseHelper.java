package com.bahar.sqlitekullanimi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "MyDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CALISANLAR_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TablesInfo.CalisanlarEntry.TABLE_NAME +
                    "(" +
                    TablesInfo.CalisanlarEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TablesInfo.CalisanlarEntry.COLUMN_FIRST_NAME + " VARCHAR NOT NULL, " +
                    TablesInfo.CalisanlarEntry.COLUMN_LAST_NAME + " VARCHAR NOT NULL, " +
                    TablesInfo.CalisanlarEntry.COLUMN_EMAIL + " VARCHAR NOT NULL" +
                    ")";

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Komutu çalıştırarak Database'i oluşturduk
        db.execSQL(TABLE_CALISANLAR_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Tabloyu sil
        db.execSQL("DROP TABLE IF EXISTS " + TablesInfo.CalisanlarEntry.TABLE_NAME);
        // Yeniden oluştur
        onCreate(db);
    }

    public void OpenDatabase()
    {
        if (db == null || !db.isOpen())
            db = this.getWritableDatabase();
    }

    public void CloseDatabase()
    {
        if (db != null && db.isOpen())
            db.close();;
    }

    public void AddCalisan(Calisanlar calisan)
    {
        OpenDatabase();
        String sqlQuery = "INSERT INTO " + TablesInfo.CalisanlarEntry.TABLE_NAME + " (" +
                TablesInfo.CalisanlarEntry.COLUMN_FIRST_NAME + ", " +
                TablesInfo.CalisanlarEntry.COLUMN_LAST_NAME + ", " +
                TablesInfo.CalisanlarEntry.COLUMN_EMAIL + ") " +
                "VALUES ('" + calisan.getFirstName() + "', '" + calisan.getLastName() + "', '" + calisan.getEmail() + "');";

        db.execSQL(sqlQuery);
        CloseDatabase();
    }

    public void UpdateCalisan(Calisanlar calisan)
    {
        OpenDatabase();
        String sqlQuery = "UPDATE " + TablesInfo.CalisanlarEntry.TABLE_NAME + " SET " +
                TablesInfo.CalisanlarEntry.COLUMN_FIRST_NAME + " = '" + calisan.getFirstName() + "', " +
                TablesInfo.CalisanlarEntry.COLUMN_LAST_NAME + " = '" + calisan.getLastName() + "', " +
                TablesInfo.CalisanlarEntry.COLUMN_EMAIL + " = '" + calisan.getEmail() + "' " +
                " WHERE " + TablesInfo.CalisanlarEntry.COLUMN_ID + " = " + calisan.getId() + ";";

        db.execSQL(sqlQuery);
        CloseDatabase();
    }

    public void DeleteCalisan(int id)
    {
        OpenDatabase();
        String sqlQuery = " DELETE FROM " + TablesInfo.CalisanlarEntry.TABLE_NAME +
                " WHERE " + TablesInfo.CalisanlarEntry.COLUMN_ID + " = " + id + ";";

        db.execSQL(sqlQuery);
        CloseDatabase();
    }

    public ArrayList<Calisanlar> getAllCalisanlar()
    {
        ArrayList<Calisanlar> list = new ArrayList<>();
        OpenDatabase();
        Cursor cursor = db.query (
                TablesInfo.CalisanlarEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(TablesInfo.CalisanlarEntry.COLUMN_ID));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(TablesInfo.CalisanlarEntry.COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(TablesInfo.CalisanlarEntry.COLUMN_LAST_NAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(TablesInfo.CalisanlarEntry.COLUMN_EMAIL));
            list.add(new Calisanlar(id, firstName, lastName, email));
        }

        cursor.close();
        CloseDatabase();
        return list;
    }

    public Calisanlar GetCalisanById(int id)
    {
        OpenDatabase();
        Cursor cursor = db.query(
                TablesInfo.CalisanlarEntry.TABLE_NAME,
                null,   // Almak istediğin sütunları belirtirsin (null -> yani tüm sütunlar)
                TablesInfo.CalisanlarEntry.COLUMN_ID + "=?", // where koşulu
                new String[]{String.valueOf(id)},
                null,   // GROUP BY
                null,           // HAVING
                null            // ORDER BY
        );

        Calisanlar calisan = new Calisanlar(
                cursor.getInt(cursor.getColumnIndexOrThrow(TablesInfo.CalisanlarEntry.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(TablesInfo.CalisanlarEntry.COLUMN_FIRST_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(TablesInfo.CalisanlarEntry.COLUMN_LAST_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(TablesInfo.CalisanlarEntry.COLUMN_EMAIL))
        );

        cursor.close();
        CloseDatabase();
        return calisan;
    }
}
