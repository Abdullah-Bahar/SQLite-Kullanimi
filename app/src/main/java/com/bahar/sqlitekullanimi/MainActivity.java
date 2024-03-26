package com.bahar.sqlitekullanimi;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private EditText editTxtFirstName, editTxtLastName, editTxtEmail;
    private Button btnSave, btnUpdate, btnRemove;
    private ListView listView;
    private DatabaseHelper dbHelper;
    private int SELECTED_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Değişken tanımlamaları
        InitializeVariables();

        // ListView'i doldur
        loadListViewData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Calisanlar selectedCalisan = (Calisanlar) parent.getItemAtPosition(position);
                SELECTED_ID = selectedCalisan.getId();

                // EditText'lere ilgili bilgileri yaz
                editTxtFirstName.setText(selectedCalisan.getFirstName());
                editTxtLastName.setText(selectedCalisan.getLastName());
                editTxtEmail.setText(selectedCalisan.getEmail());
            }
        });
    }

    public void InitializeVariables()
    {
        // UI Nesneler
        editTxtFirstName = (EditText) findViewById(R.id.editTxtFirstName);
        editTxtLastName = (EditText) findViewById(R.id.editTxtLastName);
        editTxtEmail = (EditText) findViewById(R.id.editTxtEmail);
        btnSave = findViewById(R.id.btnSave);
        btnUpdate = findViewById(R.id.button);
        btnRemove = findViewById(R.id.button2);
        listView = findViewById(R.id.listView);

        // Database nesnesi
        // dbHelper = new DatabaseHelper(this);                     // Veritabanı yalnızca bu Activity tarafından kullanılabilir.
        dbHelper = new DatabaseHelper(getApplicationContext());     // Tüm Activity'ler tarafından kullanılan bir veritabanı için
        dbHelper.CloseDatabase();   // Veri abanı bağlantısı açık kalmaması için
    }

    public void btnAdd(View v)
    {
        String firstName = editTxtFirstName.getText().toString().trim();
        String lastName = editTxtLastName.getText().toString().trim();
        String email = editTxtEmail.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty())
        {
            Toast.makeText(MainActivity.this, "Tüm alanları doldurun", Toast.LENGTH_SHORT).show();
        }
        else
        {
            dbHelper.OpenDatabase();
            long result = dbHelper.AddCalisan(new Calisanlar(0, firstName, lastName, email));
            dbHelper.CloseDatabase();
            if (result > -1)
            {
                Toast.makeText(MainActivity.this, "Çalışan başarıyla eklendi", Toast.LENGTH_SHORT).show();
                clearEditTexts();
                loadListViewData();
            }
            else if (result == -2)
            {
                Toast.makeText(MainActivity.this, "Email Zaten Mevcut", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(MainActivity.this, "Çalışan eklenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void btnUpdate(View v)
    {
        String firstName = editTxtFirstName.getText().toString().trim();
        String lastName = editTxtLastName.getText().toString().trim();
        String email = editTxtEmail.getText().toString().trim();

        // Secilen calisani guncelle
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty())
        {
            Toast.makeText(MainActivity.this, "Tüm alanları doldurun", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calisanlar calisan = new Calisanlar(SELECTED_ID, firstName, lastName, email);
            dbHelper.OpenDatabase();
            long result = dbHelper.UpdateCalisan(calisan);
            dbHelper.CloseDatabase();
            if (result > 0)
            {
                Toast.makeText(MainActivity.this, "Çalışan başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                clearEditTexts();
                loadListViewData(); // ListView'i güncelle
            }
            else if (result == -2)
            {
                Toast.makeText(MainActivity.this, "Email Zaten Mevcut", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(MainActivity.this, "Çalışan güncellenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void btnRemove(View v)
    {
        dbHelper.OpenDatabase();
        long result = dbHelper.DeleteCalisan(SELECTED_ID);
        dbHelper.CloseDatabase();
        if (result > 0)
        {
            Toast.makeText(MainActivity.this, "Çalışan başarıyla silindi", Toast.LENGTH_SHORT).show();
            clearEditTexts();
            loadListViewData(); // ListView'i güncelle
        }
        else
        {
            Toast.makeText(MainActivity.this, "Çalışan silinirken bir hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadListViewData()
    {
        dbHelper.OpenDatabase();
        ArrayList<Calisanlar> calisanList = dbHelper.getAllCalisanlar();
        ArrayAdapter<Calisanlar> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, calisanList);
        listView.setAdapter(adapter);
        dbHelper.CloseDatabase();
    }

    private void clearEditTexts()
    {
        editTxtFirstName.setText("");
        editTxtLastName.setText("");
        editTxtEmail.setText("");
        SELECTED_ID = 0;
    }
}
