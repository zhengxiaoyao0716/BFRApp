package com.zhengxiaoyao0716.facesign.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据管理.
 * Created by zhengxiaoyao0716 on 2015/12/9.
 */
public class DataManager extends SQLiteOpenHelper {
    private Context context;
    public DataManager(Context context)
    {
        super(context, "FaceSign.db", null, 0);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("CREATE TABLE signed (name varchar(50)), ")
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
