package com.example.shinelon.wanandroid.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shinelon.wanandroid.R
import com.example.shinelon.wanandroid.viewimp.IView

abstract class BaseFragment: Fragment(),IView{
    var rootView: View? = null
    val ROOT = "BaseFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayoutId(),container,false)
        rootView = view
        Log.i(ROOT,"当前Fragment加载:$tag")
        init()
        retainInstance = true
        return view
    }

    abstract fun getLayoutId(): Int
    abstract fun init()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPresenter()
        retainInstance
    }

    override fun getActivityContext() = activity!!
}