package com.think.core.util.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.time.Instant;

public class DbHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private DbHelper(@Nullable Context context) {
        this(context, null);
    }

    private DbHelper(@Nullable Context context,  @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, DownloadDaoImpl.DOWNLOAD_TABLE_NAME, factory, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DownloadDaoImpl.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DownloadDaoImpl.DOWNLOAD_TABLE_NAME);
        db.execSQL(DownloadDaoImpl.CREATE_TABLE);
    }


    private static DbHelper Instance = null;

    private static boolean init = false;

    public static DbHelper getInstance(){
        if(!init){
            throw new RuntimeException("must call getInstance(Context) init");
        }
        return Instance;
    }

    public static DbHelper getInstance(Context context){
        synchronized (DbHelper.class){
            if(Instance == null){
                synchronized (DbHelper.class){
                    Instance = new DbHelper(context.getApplicationContext());
                    init = true;
                }
            }
        }
        return Instance;
    }
}
