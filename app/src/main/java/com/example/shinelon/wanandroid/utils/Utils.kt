package com.example.shinelon.wanandroid.utils

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

fun Any.toast(context: Context,msg: String,time: Int = Toast.LENGTH_SHORT ){
    launch(UI){
        Toast.makeText(context,msg,time).show()
    }
}