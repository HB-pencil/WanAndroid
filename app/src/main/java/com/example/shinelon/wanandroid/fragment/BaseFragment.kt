package com.example.shinelon.wanandroid.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shinelon.wanandroid.R
import com.example.shinelon.wanandroid.viewimp.IView

abstract class BaseFragment: Fragment(),IView{
    var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home,container,false)
        rootView = view
        init()
        return view
    }

    abstract fun getLayoutId(id: Int): Int
    abstract fun init()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPresenter()
        retainInstance
    }

    override fun getActivityContext() = activity!!
}