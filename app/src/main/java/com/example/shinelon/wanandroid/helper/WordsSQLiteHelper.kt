package com.example.shinelon.wanandroid.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class WordsSQLiteHelper(context: Context,version: Int): SQLiteOpenHelper(context,
        DataBase.DATABASE_NAME,null,version){
    val TAG = "WordsSQLiteHelper"
    val CREATE_TABLE = "create table ${DataBase.TABLE_NAME} " +
            "(words varchar(50) primary key," +
            "times integer not null)"
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
        Log.d(TAG,"数据库表建立")
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}