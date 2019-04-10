package com.example.shinelon.wanandroid.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.shinelon.wanandroid.R
import com.example.shinelon.wanandroid.modle.NavigateData
import com.example.shinelon.wanandroid.presenter.NavigatePresenter
import com.example.shinelon.wanandroid.viewimp.INavigateFragmentView
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import com.example.shinelon.wanandroid.customview.FlowLayout
import com.example.shinelon.wanandroid.utils.dpToPx
import com.example.shinelon.wanandroid.utils.getRandomColor


class INavigateFragmentImpl: BaseFragment(),INavigateFragmentView {
    var presenter: NavigatePresenter? = null
    val TAG = "INavigateFragmentImpl"
    var flowLayout : FlowLayout? = null
    companion object {
        fun getInstance(bundle: Bundle?): INavigateFragmentImpl {
            val fragment = INavigateFragmentImpl()
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun getLayoutId() = R.layout.fragment_navigate

    override fun init() {
        flowLayout = rootView!!.findViewById(R.id.navigate_flowLayout)
        presenter?.getNavigateWebSites()
    }

    override fun setPresenter() {
        presenter = NavigatePresenter()
        presenter?.addView(this)
    }

    override fun showWebSiteName(response: MutableList<NavigateData>) {
        Log.d(TAG,"showWebSiteName")
        response.forEach {
            val title = TextView(context)
            val params = FlowLayout.FlowLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            params.topMargin =  dpToPx(20F,getActivityContext()).toInt()
            params.bottomMargin = dpToPx(10F,getActivityContext()).toInt()
            title.setPadding(25,25,25,25)
            title.textSize = 25F
            title.setTextColor(Color.BLACK)
            title.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            title.layoutParams = params
            title.text = it.name
            flowLayout?.addView(title)

            it.articles.forEach {
                val p = FlowLayout.FlowLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                p.setMargins(20,10,20,10)
                val text = Button(context)
                text.text = it.title
                text.layoutParams = p
                text.gravity = Gravity.CENTER
                text.textSize = 15F
                text.setTextColor(getRandomColor())
                text.setPadding(15,10,15,10)
                text.setBackgroundColor(Color.TRANSPARENT)
                flowLayout?.addView(text)
                val url = it.link
                text.setOnClickListener { view -> presenter?.loadWeb(url)}
            }
        }
    }

    override fun showErrorView(visible: Boolean) {
        val view = rootView!!.findViewById<TextView>(R.id.navigate_error)
        view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

}