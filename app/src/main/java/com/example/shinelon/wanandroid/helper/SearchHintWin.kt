package com.example.shinelon.wanandroid.helper

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.example.shinelon.wanandroid.R

class SearchHintWin(val context: Context): PopupWindow(context) {
    var container: LinearLayout? = null
    var searchHitWinListener: SearchHitWinListener? = null

    init {
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        container = LinearLayout(context)
        container?.orientation = LinearLayout.VERTICAL
        container?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        contentView = container
        isTouchable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(Color.WHITE))
    }

    fun addHintWords(list: MutableList<String>){
        container?.removeAllViews() //移除原来的View
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        list.forEach {
            val word = Button(context)
            word.layoutParams = params
            word.background = context.resources.getDrawable(R.drawable.ripple_struct)
            word.gravity = Gravity.CENTER
            word.text = it
            word.setAllCaps(false)
            container?.addView(word)
            val key = it
            word.setOnClickListener {
                searchHitWinListener?.onClick(key)
            }
        }
    }

    fun showWindow(view: View){
        showAsDropDown(view)
    }

    fun hideWindow(){
        dismiss()
    }

    fun registerListener(listener: SearchHitWinListener){
        searchHitWinListener = listener
    }

    interface SearchHitWinListener{
        fun onClick(word: String)
    }
}