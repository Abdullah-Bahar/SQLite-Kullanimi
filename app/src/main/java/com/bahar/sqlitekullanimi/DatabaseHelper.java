package com.bahar.sqlitekullanimi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                    TablesInfo.CalisanlarEntry.COLUMN_LAST_NAME + " CHAR(50) NOT NULL, " +
                    TablesInfo.CalisanlarEntry.COLUMN_EMAIL + " TEST UNIQUE" +
                    ")";

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        /*
            - Fonksiyonu uygulama yüklendiğinde sadece bir kere çağrılmaktadır.
            Bu metod ile uygulamada kullanılacak veritabanı ve bu veri tabanında kullanılacak olan tablolar oluşturulur.
        */

        // Komutu çalıştırarak Database'i oluşturduk
        db.execSQL(TABLE_CALISANLAR_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        /*
            - Tablolarda herhangi bir güncelleme işlemi gerektiğinde kullanılır.
            - Herhangi bir güncelleme işlemi olup olmadığına DATABASE_VERSION bilgisine bakılır.
            Tabloda herhangi bir değişiklik durumunda bu numaranın yükseltilmesi yeterli olacaktır.
        */
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

    public long mailControl(String email)
    {
        OpenDatabase();
        Cursor cursor = db.query(
                TablesInfo.CalisanlarEntry.TABLE_NAME,
                null,
                TablesInfo.CalisanlarEntry.COLUMN_EMAIL + " = ?",
                new String[]{email},
                null,
                null,
                null
        );

        long count = cursor.getCount(); // Sorgudan dönen satırların sayısı
        cursor.close();
        CloseDatabase();

        if (count > 0)
            return -2; // E-posta adresi zaten var, hata mesajı gösterin ve -2 döndürün

        return count;
    }

    public long AddCalisan(Calisanlar calisan)
    {
        if (mailControl(calisan.getEmail()) == -2)
            return -2;

        OpenDatabase();
        ContentValues cv = new ContentValues(); // Database' değer eklemek vb. işlemler için kullanılıyor

        // Sutun ile sutuna karşılık gelen değerler eşleştiriliyor.
        cv.put(TablesInfo.CalisanlarEntry.COLUMN_FIRST_NAME, calisan.getFirstName());
        cv.put(TablesInfo.CalisanlarEntry.COLUMN_LAST_NAME, calisan.getLastName());
        cv.put(TablesInfo.CalisanlarEntry.COLUMN_EMAIL, calisan.getEmail());

        long result = db.insert(TablesInfo.CalisanlarEntry.TABLE_NAME,
                TablesInfo.CalisanlarEntry.COLUMN_ID,   // NULL olamayacak sütun adı
                cv);

        CloseDatabase();
        return result;
    }

    public long UpdateCalisan(Calisanlar calisan)
    {
        OpenDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TablesInfo.CalisanlarEntry.COLUMN_FIRST_NAME, calisan.getFirstName());
        cv.put(TablesInfo.CalisanlarEntry.COLUMN_LAST_NAME, calisan.getLastName());
        cv.put(TablesInfo.CalisanlarEntry.COLUMN_EMAIL, calisan.getEmail());

        // güncellenen kayıt sayısını belirtir
        long result = db.update(TablesInfo.CalisanlarEntry.TABLE_NAME,
                cv,
                TablesInfo.CalisanlarEntry.COLUMN_ID+"=?", // Güncelleme işlemi için belirtilen koşulu içeren bir SQL ifadesi
                    new String[]{String.valueOf(calisan.getId())}); // Koşuldaki "?" işareti olan yet tutucunun yerine geçecek olan paramtre

        CloseDatabase();
        return result;
    }

    public long DeleteCalisan(int id)
    {
        OpenDatabase();
        long result = db.delete(TablesInfo.CalisanlarEntry.TABLE_NAME,
                TablesInfo.CalisanlarEntry.COLUMN_ID+"=?",
                new String[]{String.valueOf(id)});

        CloseDatabase();
        return result;
            // Silinen satır sayısını döndürür.
            // Eğer silinen satır sayısı yoksa 0 döndürür.
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
