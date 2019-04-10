package com.example.shinelon.wanandroid.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import com.example.shinelon.wanandroid.R
import com.example.shinelon.wanandroid.customview.FlowLayout
import com.example.shinelon.wanandroid.utils.dpToPx
import com.example.shinelon.wanandroid.utils.getRandomColor
import kotlinx.android.synthetic.main.hot_search_window.view.*

class HotSearchPopupWin(val context: Context): PopupWindow(context),View.OnClickListener {
    var container: FlowLayout? = null
    var listener: HotSearchPopupWinListener? = null

    init {
        //inflate进来的view要注意它自身的约束是否有效，通常把它放进父容器
        contentView = LayoutInflater.from(context).inflate(R.layout.hot_search_window,null,true)
        setBackgroundDrawable(ColorDrawable(Color.WHITE))
        isTouchable = true
        isOutsideTouchable = true
        animationStyle = R.style.HotSearchWindowsAnim
        container = contentView.findViewById(R.id.search_win_flow)
    }

    fun addTitle(title: String){
        val params = FlowLayout.FlowLayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        params.leftMargin = dpToPx(4F,context).toInt()
        params.topMargin = dpToPx(4F,context).toInt()
        val text = Button(context)
        text.text = title
        text.layoutParams = params
        text.gravity = Gravity.CENTER_VERTICAL
        text.setTextColor(Color.BLACK)
        text.setPadding(dpToPx(4F,context).toInt(),dpToPx(4F,context).toInt(),
                dpToPx(4F,context).toInt(),dpToPx(4F,context).toInt())
        text.background = container!!.resources.getDrawable(R.drawable.ripple_win)
        text.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        container!!.addView(text)
        text.setTextColor(Color.BLACK)
        text.isClickable = false
    }

    fun addWord(word: String){
        val params = FlowLayout.FlowLayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        params.leftMargin = dpToPx(6F,context).toInt()
        params.rightMargin = dpToPx(6F,context).toInt()
        val text = Button(context)
        text.text = word
        text.layoutParams = params
        text.gravity = Gravity.CENTER
        text.setTextColor(getRandomColor())
        text.setPadding(dpToPx(4F,context).toInt(),dpToPx(4F,context).toInt(),
                dpToPx(4F,context).toInt(),dpToPx(4F,context).toInt())
        text.background = container!!.resources.getDrawable(R.drawable.ripple_win)
        container!!.addView(text)
        text.setTextColor(getRandomColor())
        text.textSize = 14F
        text.setOnClickListener (this)
    }

    fun isErrorViewShow(isShow: Boolean){
        val root = contentView as FrameLayout
        root.findViewById<TextView>(R.id.error_view).visibility = if (isShow) View.VISIBLE  else View.GONE
    }

    fun showWindow(view: View){
        showAsDropDown(view)
    }

    fun addClickListener(listener: HotSearchPopupWinListener){
        this.listener = listener
    }

    override fun onClick(v: View?) {
        val view = v as? Button
        listener?.onClick(view?.text.toString())
    }

    interface HotSearchPopupWinListener{
        fun onClick(hotWord: String)
    }
}