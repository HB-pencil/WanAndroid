package com.example.shinelon.wanandroid.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import com.example.shinelon.wanandroid.R
import com.example.shinelon.wanandroid.helper.toast
import kotlinx.android.synthetic.main.hot_search_window.view.*

class HotSearchPopupWin(val context: Context): PopupWindow(context),View.OnClickListener {
    init {
        //inflate进来的view要注意它自身的约束是否有效，通常把它放进父容器
        contentView = LayoutInflater.from(context).inflate(R.layout.hot_search_window,null,true)
        setBackgroundDrawable(ColorDrawable(Color.WHITE))
        isTouchable = true
        isOutsideTouchable = true
        animationStyle = R.style.HotSearchWindowsAnim
    }

    var listener: HotSearchPopupWinListener? = null

    /**
     * 每次添加两个搜索热词
     * @param word1 搜索热词1
     * @param word2 搜索热词2
     */
    fun addHotWord(word1: String,word2: String){
        val container = LinearLayout(context)
        contentView.search_container.addView(container)

        val params = LayoutParams(0,LayoutParams.WRAP_CONTENT)
        params.weight = 1.0F

        val text1 = Button(context)
        text1.text = word1
        text1.layoutParams = params
        text1.gravity = Gravity.CENTER
        text1.setTextColor(getRandomColor())
        text1.setPadding(10,10,10,10)
        text1.background = container.resources.getDrawable(R.drawable.ripple_win)
        container.addView(text1)

        val text2 = Button(context)
        text2.text = word2
        text2.layoutParams = params
        text2.gravity = Gravity.CENTER
        text2.setTextColor(getRandomColor())
        text2.setPadding(15,20,15,20)
        text2.background = container.resources.getDrawable(R.drawable.ripple_win)
        container.addView(text2)

        text1.setOnClickListener (this)
        text2.setOnClickListener (this)
    }

    fun isErrorViewShow(isShow: Boolean){
        val root = contentView as FrameLayout
        root.getChildAt(0).error_view.visibility = if(isShow) View.VISIBLE else View.GONE
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

    fun getRandomColor(): Int{
        var res = (Math.random()*10/2).toInt()
        when (res) {
            0 -> res = Color.BLUE
            1 -> res = Color.RED
            2 -> res = Color.YELLOW
            3 -> res = Color.GREEN
            else -> res = Color.MAGENTA
        }
        return res
    }

    interface HotSearchPopupWinListener{
        fun onClick(hotWord: String)
    }
}