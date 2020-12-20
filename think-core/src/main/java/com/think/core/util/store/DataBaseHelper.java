package com.think.core.util.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 数据库访问工具类
 *
 * @author : zzp
 * @date : 2020/8/6
 **/
public class DataBaseHelper extends SQLiteOpenHelper {

    /**
     * 数据库名称
     */
    private static final String DB_NAME = "Think";

    /**
     * 数据库版本号
     */
    private static final int VERSION = 1;

    private static DataBaseHelper instance = null;

    public static DataBaseHelper getInstance(Context context) {
        synchronized (DataBaseHelper.class) {
            if (instance == null) {
                synchronized (DataBaseHelper.class) {
                    instance = new DataBaseHelper(context);
                }
            }
        }
        return instance;
    }

    public DataBaseHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
