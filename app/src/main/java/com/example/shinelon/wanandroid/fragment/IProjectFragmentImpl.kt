package com.example.shinelon.wanandroid.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

import com.example.shinelon.wanandroid.R
import com.example.shinelon.wanandroid.customview.FlowLayout
import com.example.shinelon.wanandroid.modle.ProjectCategory
import com.example.shinelon.wanandroid.presenter.ProjectPresenter
import com.example.shinelon.wanandroid.utils.dpToPx
import com.example.shinelon.wanandroid.viewimp.IProjectFragmentView

class IProjectFragmentImpl:BaseFragment(),IProjectFragmentView{
    var presenter: ProjectPresenter? = null

    val TAG = "IProjectFragmentImpl"
    var container: FlowLayout? = null

    companion object {
        fun getInstance(bundle: Bundle?): IProjectFragmentImpl {
            val fragment = IProjectFragmentImpl()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_project

    override fun init() {
        container = rootView?.findViewById(R.id.fragment_project_root)
        presenter?.getProjectDetails()
    }

    override fun setPresenter() {
        presenter = ProjectPresenter()
        presenter?.addView(this)
    }

    override fun initPages(data: MutableList<ProjectCategory>) {
        data.forEach {
            val p = FlowLayout.FlowLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            p.setMargins(30,10,30,10)
            val text = Button(context)
            text.text = it.name
            text.layoutParams = p
            text.gravity = Gravity.CENTER
            text.textSize = 18F
            text.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            text.setTextColor(Color.BLACK)
            text.setPadding(20,10,20,10)
            text.setBackgroundColor(Color.TRANSPARENT)
            container!!.addView(text)
            val cid = it.id
            text.setOnClickListener {
                presenter?.getArticles(cid)
            }
            Log.d(TAG,"add a itemView")
        }
    }

    override fun showErrorView() {
        rootView!!.findViewById<TextView>(R.id.load_error).visibility = View.VISIBLE
    }

    override fun hideErrorView() {
        rootView!!.findViewById<TextView>(R.id.load_error).visibility = View.INVISIBLE
    }
}