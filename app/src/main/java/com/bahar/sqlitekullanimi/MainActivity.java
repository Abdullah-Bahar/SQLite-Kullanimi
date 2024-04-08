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
    // Mobil UI Nesneleri
    private EditText editTxtFirstName, editTxtLastName, editTxtEmail;
    private Button btnSave, btnUpdate, btnRemove;
    private ListView listViewCalisanlar;
    private DatabaseHelper dbHelper;

    private int SELECTED_ID; // Guncelleme ve Silme islemleri icin kullanılan global değişken

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Değişken tanımlamaları
        InitializeVariables();

        // ListView'i doldur
        loadListViewData();

        listViewCalisanlar.setOnItemClickListener(new AdapterView.OnItemClickListener()
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
        btnUpdate = findViewById(R.id.btnUpdate);
        btnRemove = findViewById(R.id.btnRemove);
        listViewCalisanlar = findViewById(R.id.listViewCalisanlar);

        // Database nesnesi
        // dbHelper = new DatabaseHelper(this);                     // Veritabanı yalnızca bu Activity tarafından kullanılabilir.
        dbHelper = new DatabaseHelper(getApplicationContext());     // Tüm Activity'ler tarafından kullanılan bir veritabanı için
        dbHelper.CloseDatabase();   // Veri abanı bağlantısı açık kalmaması için

        // Global değişken
        SELECTED_ID = 0;
    }

    public void btnSaveClick(View v)
    {
        String[] editTxts = getDataFromEditText(); // EditText'lerdeki değerleri al (0 = FirstName, 1 = LastName, 2 = Email)

        // EditText değerleri boş mu kontrolü
        if (isEmptyEditTxt(editTxts))
            Toast.makeText(MainActivity.this, "Tüm alanları doldurun", Toast.LENGTH_SHORT).show();
        else
        {
            dbHelper.OpenDatabase();
            dbHelper.AddCalisan(new Calisanlar(0, editTxts[0], editTxts[1], editTxts[2]));
            dbHelper.CloseDatabase();
            Toast.makeText(MainActivity.this, "Çalışan başarıyla eklendi", Toast.LENGTH_SHORT).show();
            clearEditTexts();
            loadListViewData();
        }
    }

    public void btnUpdateClick(View v)
    {
        String[] editTxts = getDataFromEditText();

        // EditText değerleri boş mu kontrolü
        if (isEmptyEditTxt(editTxts))
            Toast.makeText(MainActivity.this, "Tüm alanları doldurun", Toast.LENGTH_SHORT).show();
        else
        {
            Calisanlar calisan = new Calisanlar(SELECTED_ID, editTxts[0], editTxts[1], editTxts[2]);
            dbHelper.OpenDatabase();
            dbHelper.UpdateCalisan(calisan);
            dbHelper.CloseDatabase();
            Toast.makeText(MainActivity.this, "Çalışan başarıyla güncellendi", Toast.LENGTH_SHORT).show();
            clearEditTexts();
            loadListViewData();
        }
    }

    public void btnRemoveClick(View v)
    {
        dbHelper.OpenDatabase();
        dbHelper.DeleteCalisan(SELECTED_ID);
        dbHelper.CloseDatabase();
        if (SELECTED_ID != 0)
        {
            Toast.makeText(MainActivity.this, "Çalışan başarıyla silindi", Toast.LENGTH_SHORT).show();
            clearEditTexts();
            loadListViewData();
        }
        else
            Toast.makeText(MainActivity.this, "Silinecek elemanı seçin", Toast.LENGTH_SHORT).show();
    }

    private void loadListViewData()
    {
        dbHelper.OpenDatabase();
        ArrayList<Calisanlar> calisanList = dbHelper.getAllCalisanlar();
        ArrayAdapter<Calisanlar> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, calisanList);
        listViewCalisanlar.setAdapter(adapter);
        dbHelper.CloseDatabase();
    }

    // EditText'leri ve GLOBAL ID'yi sıfırlar
    private void clearEditTexts()
    {
        editTxtFirstName.setText("");
        editTxtLastName.setText("");
        editTxtEmail.setText("");
        SELECTED_ID = 0;
    }

    // EditText'lerdeki verileri bir String Array olarak döndürür.
    private String[] getDataFromEditText()
    {
        String[] userInput = new String[3];
        userInput[0] = editTxtFirstName.getText().toString().trim();
        userInput[1] = editTxtLastName.getText().toString().trim();
        userInput[2] = editTxtEmail.getText().toString().trim();
        return userInput;
    }

    private boolean isEmptyEditTxt(String[] editTxts)
    {
        return editTxts[0].isEmpty() || editTxts[1].isEmpty() || editTxts[2].isEmpty();
    }
}
