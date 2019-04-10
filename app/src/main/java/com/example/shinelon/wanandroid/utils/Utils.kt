package com.example.shinelon.wanandroid.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.widget.Toast
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

fun Any.toast(context: Context,msg: String,time: Int = Toast.LENGTH_SHORT ){
    launch(UI){
        Toast.makeText(context,msg,time).show()
    }
}

fun dpToPx(value: Float,context: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, context.resources.displayMetrics)

fun getRandomColor(): Int{
    var res = (Math.random()*10/2).toInt()
    when (res) {
        0 -> res = Color.BLUE
        1 -> res = Color.RED
        2 -> res = Color.CYAN
        3 -> res = Color.GREEN
        else -> res = Color.MAGENTA
    }
    return res
}