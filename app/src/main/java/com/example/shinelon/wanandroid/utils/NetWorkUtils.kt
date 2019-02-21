package com.example.shinelon.wanandroid.utils

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager

class NetWorkUtils{
    companion object {
        fun isNetWorkAvailable(context: Context): Boolean{
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = manager.activeNetworkInfo
            if (info != null) {
                return info.isAvailable && info.isConnected
            }
            return false
        }
    }
}