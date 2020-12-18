package com.think.core.util.download;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DownloadDaoImpl implements DownloadDao {

    public static final String DOWNLOAD_TABLE_NAME = "download_info";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS download_info(" +
                    "did INTEGER PRIMARY key autoincrement, " +
                    "totalSize INTEGER default -1," +
                    "currentSize INTEGER default 0 ," +
                    "url TEXT, " +
                    "fileName TEXT, " +
                    "status INTEGER default 0 ," +
                    "path TEXT, " +
                    "multiThread INTEGER default " + DownloadStatus.DOWNLOAD_READY + ");";

    private static final String D_ID = "did";
    private static final String TOTAL_SIZE = "totalSize";
    private static final String CURRENT_SIZE = "currentSize";
    private static final String URL = "url";
    private static final String FILE_NAME = "fileName";
    private static final String STATUS = "status";
    private static final String PATH = "path";
    private static final String MULTI_THREAD = "multiThread";

    private final DbHelper dbHelper;

    public DownloadDaoImpl() {
        this.dbHelper = DbHelper.getInstance();
    }

    public DownloadDaoImpl(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public int insertRetPrimary(DownloadEntry downloadEntry) {
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        Cursor cursor = null;
        int insertId = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TOTAL_SIZE, downloadEntry.totalSize);
            contentValues.put(CURRENT_SIZE, downloadEntry.currentSize);
            contentValues.put(URL, downloadEntry.url);
            contentValues.put(FILE_NAME, downloadEntry.fileName);
            contentValues.put(STATUS, downloadEntry.status);
            contentValues.put(PATH, downloadEntry.path);
            contentValues.put(MULTI_THREAD, downloadEntry.multiThread);
            writableDatabase.insert(DOWNLOAD_TABLE_NAME, null, contentValues);

            cursor = writableDatabase.rawQuery("select last_insert_rowid() from " + DOWNLOAD_TABLE_NAME, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
        if (cursor.moveToFirst()) {
            insertId = cursor.getInt(0);
        }
        cursor.close();
        return insertId;
    }

    @Override
    public void insert(DownloadEntry downloadEntry) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TOTAL_SIZE, downloadEntry.totalSize);
        contentValues.put(CURRENT_SIZE, downloadEntry.currentSize);
        contentValues.put(URL, downloadEntry.url);
        contentValues.put(FILE_NAME, downloadEntry.fileName);
        contentValues.put(STATUS, downloadEntry.status);
        contentValues.put(PATH, downloadEntry.path);
        contentValues.put(MULTI_THREAD, downloadEntry.multiThread);
        db.insert(DOWNLOAD_TABLE_NAME, null, contentValues);
        contentValues.clear();
        db.close();
    }

    @Override
    public void update(DownloadEntry downloadEntry) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(downloadEntry.totalSize != 0){
            contentValues.put(TOTAL_SIZE, downloadEntry.totalSize);
        }
        if(downloadEntry.currentSize != 0){
            contentValues.put(CURRENT_SIZE, downloadEntry.currentSize);
        }
        contentValues.put(URL, downloadEntry.url);
        contentValues.put(FILE_NAME, downloadEntry.fileName);
        contentValues.put(STATUS, downloadEntry.status);
        contentValues.put(PATH, downloadEntry.path);
        if(downloadEntry.multiThread != 0){
            contentValues.put(MULTI_THREAD, downloadEntry.multiThread);
        }
        db.update(DOWNLOAD_TABLE_NAME, contentValues, "did="+downloadEntry.did, null);
        contentValues.clear();
        db.close();
}

    @Override
    public void update(int did, int status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, status);
        db.update(DOWNLOAD_TABLE_NAME, contentValues, "did="+did, null);
        contentValues.clear();
        db.close();
    }

    @Override
    public void delete(DownloadEntry downloadEntry) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DOWNLOAD_TABLE_NAME,D_ID+"="+downloadEntry.did + "and" + URL + "=" + downloadEntry.url,null);
        db.close();
    }

    @Override
    public void delete(int did) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DOWNLOAD_TABLE_NAME,D_ID+"="+did ,null);
        db.close();
    }

    @Override
    public DownloadEntry queryDownloadTask(DownloadEntry downloadEntry) {
        return null;
    }

    @Override
    public List<DownloadEntry> queryDownloadTask() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DOWNLOAD_TABLE_NAME, null, "status not in(" + DownloadStatus.DOWNLOAD_COMPLETED + ","
                + DownloadStatus.DOWNLOAD_CANCEL + ")", null, null, null, null);

        List<DownloadEntry> list = new ArrayList<>();
        while (cursor.moveToNext()){
            DownloadEntry entry = new DownloadEntry();
            entry.did = cursor.getInt(cursor.getColumnIndex(D_ID));
            entry.url = cursor.getString(cursor.getColumnIndex(URL));
            entry.multiThread = cursor.getInt(cursor.getColumnIndex(MULTI_THREAD));
            entry.currentSize = cursor.getLong(cursor.getColumnIndex(CURRENT_SIZE));
            entry.totalSize = cursor.getLong(cursor.getColumnIndex(TOTAL_SIZE));
            entry.status = cursor.getInt(cursor.getColumnIndex(STATUS));
            entry.path = cursor.getString(cursor.getColumnIndex(PATH));
            entry.fileName = cursor.getString(cursor.getColumnIndex(FILE_NAME));
//            entry.threadNum = cursor.getInt(cursor.getColumnIndex("threadNum"));
            list.add(entry);
        }
        cursor.close();
        return list;
    }

    @Override
    public DownloadEntry queryDownloadUnCompleteTask(int did) {
        return null;
    }

}
