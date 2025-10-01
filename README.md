# :rocket: SQLite Kullanımı

## :bulb: Proje Hakkında

Bu proje, Android üzerinde SQLite kullanarak veritabanı işlem örneklerini içeren bir uygulamadır.
Proje kapsamında *Calisanlar* üzerinden CRUD işlemleri yapılmaktadır.

Uygulama ile kullanıcılar :

- Çalışan ekleyebilir,
- Güncelleyebilir,
- Listeden silebilir,
- Çalışan bilgilerini listeleyebilir,
- Çalışan profili için fotoğraf ekleyebilir ve görüntüleyebilir.

> NOT : Çok yüksek çözünürlüklü fotoğraflarlar bazı cihazlarda görüntülenmeyebilir.

## Uygulama Özellikleri

| Özellik | Açıklama |
| - | - |
| **Veritabanı** | SQLite kullanılarak lokal veritabanı oluşturulur ve yönetilir. |
| **CRUD İşlemleri**   | Çalışan ekleme, güncelleme, silme ve listeleme yapılabilir.                           |
| **Fotoğraf Desteği** | Galeriden fotoğraf seçip profil resmi olarak kaydetme.                                |
| **ListView**         | Çalışanlar listede gösterilir ve seçilen çalışan bilgileri EditText alanlarına gelir. |
| **Email Kontrolü**   | Email benzersiz olmalıdır, aynı email ile çalışan eklenemez.                          |

## Uygulama Kullanımı

- Çalışan Ekleme
	- Ad, soyad ve email girin.
	- İsteğe bağlı olarak profil fotoğrafı ekleyin.
	- "Kaydet" butonuna basın.

- Çalışan Güncelleme
	- Listeden bir çalışan seçin.
	- Bilgileri değiştirin veya yeni fotoğraf ekleyin.
	- "Güncelle" butonuna basın.

- Çalışan Silme
	- Listeden bir çalışan seçin.
	- "Sil" butonuna basın.

- Temizle
	- Girdi alanlarını temizlemek için "Temizle" butonuna basın.

## Uygulama Örnekleri

| Çalışan Ekleme | Çalışan Listeleme |
| :-: | :-: |
| ![Çalışan Ekleme](assets/01-calisan-ekleme.gif) | ![Çalışan Listeleme](assets/02-calisan-listeleme.gif)  |

## UML Diagram

```mermaid
classDiagram
    %% --------------------------
    %% MainActivity Sınıfı
    %% --------------------------
    MainActivity --> DatabaseHelper
    MainActivity --> Calisanlar
    MainActivity --> ImageView
    MainActivity --> ImageButton
    MainActivity --> EditText
    MainActivity --> Button
    MainActivity --> ListView

    class MainActivity {
        - int SELECTED_ID
        - byte[] IMAGE_BYTES
        - EditText editTxtFirstName
        - EditText editTxtLastName
        - EditText editTxtEmail
        - Button btnSave
        - Button btnUpdate
        - Button btnRemove
        - Button btnClear
        - ListView listView
        - ImageView imgProfilFoto
        - ImageButton imgBtnFotoAdd
        - DatabaseHelper dbHelper
        + onCreate(Bundle)
        + InitializeVariables()
        + ListViewClick()
        + openGallery()
        + bitmapToByteArray(Bitmap, CompressFormat, int) : byte[]
        + uriToBitmap(Uri) : Bitmap
        + btnAdd(View)
        + btnUpdate(View)
        + btnRemove(View)
        + btnTemizle(View)
        + loadListViewData()
        + clearEditTexts()
    }

    %% --------------------------
    %% Calisanlar Sınıfı
    %% --------------------------
    class Calisanlar {
        - int id
        - String firstName
        - String lastName
        - String email
        - byte[] imageBytes
        + Calisanlar(int, String, String, String, byte[])
        + getId() : int
        + setId(int)
        + getFirstName() : String
        + setFirstName(String)
        + getLastName() : String
        + setLastName(String)
        + getEmail() : String
        + setEmail(String)
        + getImageBytes() : byte[]
        + setImageBytes(byte[])
        + toString() : String
    }

    %% --------------------------
    %% DatabaseHelper Sınıfı
    %% --------------------------
    DatabaseHelper --> Calisanlar

    class DatabaseHelper {
        - SQLiteDatabase db
        + OpenDatabase()
        + CloseDatabase()
        + mailControl(String) : long
        + AddCalisan(Calisanlar) : long
        + UpdateCalisan(Calisanlar) : long
        + DeleteCalisan(int) : long
        + getAllCalisanlar() : ArrayList~Calisanlar~
        + GetCalisanById(int) : Calisanlar
        + onCreate(SQLiteDatabase)
        + onUpgrade(SQLiteDatabase, int, int)
    }

    %% --------------------------
    %% TablesInfo Sınıfı (sabitler)
    %% --------------------------
    class TablesInfo {
        + TABLE_NAME : String
        + COLUMN_ID : String
        + COLUMN_FIRST_NAME : String
        + COLUMN_LAST_NAME : String
        + COLUMN_EMAIL : String
        + COLUMN_IMG : String
    }

    %% --------------------------
    %% Sınıflar arası ilişkiler
    %% --------------------------
    MainActivity --> TablesInfo
    DatabaseHelper --> TablesInfo

```

## Bilinen Sınırlamalar

- Uygulama çok yüksek çözünürlüklü fotoğrafları (örn. 3000x4000 px) düzgün gösteremeyebilir.
- Bu durum, Android Bitmap bellek sınırlamaları ve ListView ile görüntüleme nedeniyle oluşur.
- Öneri: Fotoğrafları önceden optimize ederek uygulamaya ekleyin. Ya da yüksek kalitede fotoğraf eklemeye çalışmayın.

## Notlar

- Email alanı benzersiz olmalıdır; aynı email ile çalışan eklenemez.
- Profil fotoğrafı opsiyoneldir; eklenmezse varsayılan görsel gösterilir.
- SQLite veritabanı uygulama içinde lokal olarak tutulur ve sadece bu uygulama tarafından erişilebilir.