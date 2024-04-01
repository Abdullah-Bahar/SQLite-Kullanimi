package com.bahar.sqlitekullanimi;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import java.io.ByteArrayOutputStream;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    // Global olarak işlem yaprığımız değişkenler
    private int SELECTED_ID; // Tıklanan Liste elemanlarının id'lerini tutmak için kullanılır
    private byte[] IMAGE_BYTES;

    // Mobil UI Nesneleri
    private EditText editTxtFirstName, editTxtLastName, editTxtEmail;
    private Button btnSave, btnUpdate, btnRemove, btnClear;
    private ListView listView;

    //Database nesnesi
    private DatabaseHelper dbHelper;

    // Fotoğraf İşlemleri İçin
    private ImageView imgProfilFoto;
    private ImageButton imgBtnFotoAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Değişken tanımlamaları
        InitializeVariables();

        // ListView'i doldur
        loadListViewData();

        // listView'de tıklama işlemi
        ListViewClick();
    }

    // seçilen resime erişmemizi sağlar
    ActivityResultLauncher<String> resimSecmePenceresi = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>()
            {
                @Override
                public void onActivityResult(Uri o)
                {
                    // Uğraştıracağı için burayı kullanamk yerine default olarak foto formatı JPEG olarak ayarlandı
                    // Uri nesnesini kullanarak format bilgisini elde etme
                    /*
                    ContentResolver contentResolver = getContentResolver();
                    String mimeType = contentResolver.getType(o);
                    */

                    try {
                        imgProfilFoto.setImageURI(o);
                        Bitmap bitmap = uriToBitmap(o); // Seçilen resmi Uri objesinden Bitmap'a çevirriyoruz
                        IMAGE_BYTES = bitmapToByteArray(bitmap, CompressFormat.JPEG, 100); // Bitmap'tan byte dizisine çeviriyoruz
                    } catch (FileNotFoundException e) {
                        Toast.makeText(MainActivity.this, "Fotoğraf Seçerken Bir Sorun Oluştu", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    ) ;

    public void InitializeVariables()
    {
        // UI Nesneler
        editTxtFirstName = (EditText) findViewById(R.id.editTxtFirstName);
        editTxtLastName = (EditText) findViewById(R.id.editTxtLastName);
        editTxtEmail = (EditText) findViewById(R.id.editTxtEmail);
        btnSave = findViewById(R.id.btnSave);
        btnUpdate = findViewById(R.id.button);
        btnRemove = findViewById(R.id.button2);
        btnClear = findViewById(R.id.button3);
        listView = findViewById(R.id.listView);

        // Foto Nesneleri
        imgProfilFoto = (ImageView) findViewById(R.id.imgProfilFoto);
        imgBtnFotoAdd = (ImageButton) findViewById(R.id.imgBtnFotoAdd);

        // Database nesnesi
        // dbHelper = new DatabaseHelper(this);                     // Veritabanı yalnızca bu Activity tarafından kullanılabilir.
        dbHelper = new DatabaseHelper(getApplicationContext());     // Tüm Activity'ler tarafından kullanılan bir veritabanı için
        dbHelper.CloseDatabase();   // Veri abanı bağlantısı açık kalmaması için
    }

    public void ListViewClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Calisanlar selectedCalisan = (Calisanlar) parent.getItemAtPosition(position);
                SELECTED_ID = selectedCalisan.getId();

                // EditText'lere ilgili bilgileri yaz
                editTxtFirstName.setText(selectedCalisan.getFirstName());
                editTxtLastName.setText(selectedCalisan.getLastName());
                editTxtEmail.setText(selectedCalisan.getEmail());

                IMAGE_BYTES = selectedCalisan.getImageBytes();
                if (IMAGE_BYTES != null) {
                    imgProfilFoto.setImageBitmap(BitmapFactory.decodeByteArray(IMAGE_BYTES, 0, IMAGE_BYTES.length));
                    Log.d("ForografIzleme", "Veritabından fotoğraf geldi");
                } else {
                    imgProfilFoto.setImageResource(R.drawable.baseline_insert_photo_24);
                    Log.d("ForografIzleme", "Varsayılan fotoğraf geldi");
                }
            }
        });
    }

    public void btnFotoAdd (View v)
    {
        /*
        // I. Proje içinden foto ekleme
        imgProfilFoto.setImageResource(R.drawable.profile);

        // II. Proje dışından foto ekleme
        String path = Environment.getExternalStorageDirectory() + "/Picture/hediye.png";
        File file = new File(path);
        if (file.exists())
        {
            Bitmap img = BitmapFactory.decodeFile(path);
            imgProfilFoto.setImageBitmap(img);
        }

        // III. Galeriden foto seçmek
        resimSecmePenceresi.launch("Image/*");
        */

        // Gerekli izin kontrolleri yapılacaksa image ekleme ayrı bir method'a taşındı
        openGallery();
    }

    // Galeriden resim seçmek için kullılır
    public void openGallery()
    {
        resimSecmePenceresi.launch("image/*");
    }

    // Bitmap'i byte dizisine çevirmek için
    public byte[] bitmapToByteArray(Bitmap bitmap, Bitmap.CompressFormat format, int quality)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(format, quality, stream);
        return stream.toByteArray();
    }

    // Uri'den Bitmap'a çevirme
    public Bitmap uriToBitmap(Uri uri) throws FileNotFoundException{
            // Uri'den ContentResolver'ı al
            ContentResolver resolver = getContentResolver();
            // Uri'den veri oku ve Bitmap'e dönüştür
            Bitmap bitmap = BitmapFactory.decodeStream(resolver.openInputStream(uri));

            return bitmap;
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
            /*
            if (IMAGE_BYTES == null)
                 IMAGE_BYTES = bitmapToByteArray(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_insert_photo_24),
                         CompressFormat.JPEG, 100);
            */  // Profoil Fotosu seçilmemişse default olarak foto ekleme
            long result = dbHelper.AddCalisan(new Calisanlar(0, firstName, lastName, email, IMAGE_BYTES));
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
            // Fotoğraf değişikliğini kontrol et
            byte[] updatedImageBytes;
            if (IMAGE_BYTES != null)
            {
                // Kullanıcı fotoğraf seçmiş, güncellemeyi yap
                Bitmap bitmap = BitmapFactory.decodeByteArray(IMAGE_BYTES, 0, IMAGE_BYTES.length);
                updatedImageBytes = bitmapToByteArray(bitmap, CompressFormat.JPEG, 100);
                System.out.println("foto eklenmis");
            }
            else
            {
                updatedImageBytes = IMAGE_BYTES;
                System.out.println("foto eklenmemis");
            }
            System.out.println(updatedImageBytes.length);

            Calisanlar calisan = new Calisanlar(SELECTED_ID, firstName, lastName, email, updatedImageBytes);
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

    public void btnTemizle (View v)
    {
        clearEditTexts();
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
        SELECTED_ID = 0; // tıklanan liste elemanının değerlerinin tutulduğu bu değişken 0'lanmazsa soruz çıkabiliyor
        IMAGE_BYTES = null;
        imgProfilFoto.setImageResource(R.drawable.baseline_insert_photo_24);
    }
}
