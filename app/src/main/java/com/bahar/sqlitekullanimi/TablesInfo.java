package com.bahar.sqlitekullanimi;

import android.provider.BaseColumns;

public class TablesInfo
{
    public static final class CalisanlarEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "Calisanlar";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_LAST_NAME = "LastName";
        public static final String COLUMN_FIRST_NAME = "FirstName";
        public static final String COLUMN_EMAIL = "Email";
        public static final String COLUMN_IMG = "Resim";
    }
}
