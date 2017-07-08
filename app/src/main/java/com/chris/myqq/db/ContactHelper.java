package com.chris.myqq.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ThinkPad on 2016/8/13.
 * 联系人本地数据库
 */
public class ContactHelper extends SQLiteOpenHelper {
    private String username;
    public ContactHelper(Context context,String username){
        super(context,"contact.db",null,1);
        this.username = username;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+username+"(_id integer primary key,name varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("chris","contact.db onUpgrade");
    }
}
