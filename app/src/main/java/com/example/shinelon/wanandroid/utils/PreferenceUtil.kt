package com.example.shinelon.wanandroid.utils

import android.preference.PreferenceManager
import com.example.shinelon.wanandroid.MyApplication

class PreferenceUtil{
    private val  preferenceManager = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext())
    private val editor = preferenceManager.edit()

    companion object {
        fun getInstance() = PreferenceUtil()
    }
    fun putString(key: String,value: String = "") = editor.putString(key,value)

    fun putLong(key: String,value: Long = Long.MIN_VALUE) = editor.putLong(key,value)

    fun putBoolean(key: String,value: Boolean = false) = editor.putBoolean(key,value)

    fun getString(key: String) = preferenceManager.getString(key,"")

    fun getLong(key: String) = preferenceManager.getLong(key,Long.MIN_VALUE)

    fun getBoolean(key: String) = preferenceManager.getBoolean(key,false)

    fun commit() = editor.apply()
}