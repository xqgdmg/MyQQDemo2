package com.chris.myqq.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ThinkPad on 2016/8/13.
 * 插入联系人，查询联系人
 */
public class DBUtil {
    public static void insertContacts(Context context,String username,ArrayList<String> contacts){
        Log.e("chris","insertContacts");
        ContactHelper helper = new ContactHelper(context,username);
        SQLiteDatabase database = helper.getWritableDatabase();

        database.beginTransaction();
        //删除原来的数据
        database.delete(username,null,null);
        //添加
        for (String contact:contacts) {
            ContentValues values = new ContentValues();
            values.put("name",contact);
            database.insert(username,"name",values);
        }
        database.setTransactionSuccessful();
        database.endTransaction();

        database.close();
    }

    /*
     * 查询所有联系人
     */
    public static ArrayList<String> queryAllContacts(Context context, String username){
        //创建联系人集合
        ArrayList<String> contacts = new ArrayList<String>();
        ContactHelper helper = new ContactHelper(context,username);
        SQLiteDatabase database = helper.getReadableDatabase();
        //当前没有此表创建该用户名的好友table
        database.execSQL("create table if not exists "+username+"(_id integer primary key,name varchar(20))");

        Cursor cursor = database.query(username,new String[]{"name"},null,null,null,null,null);
        //解析cursor
        while (cursor.moveToNext()){
            contacts.add(cursor.getString(0));
        }
        cursor.close();
        database.close();
        return contacts;
    }
}
