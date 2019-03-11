package com.example.shinelon.wanandroid.fragment

import android.os.Bundle

import com.example.shinelon.wanandroid.R
import com.example.shinelon.wanandroid.presenter.ProjectPresenter
import com.example.shinelon.wanandroid.viewimp.IProjectFragmentView

class IProjectFragmentImpl:BaseFragment(),IProjectFragmentView{
    var presenter: ProjectPresenter? = null
    val TAG = "IProjectFragmentImpl"
    companion object {
        fun getInstance(bundle: Bundle?): IProjectFragmentImpl {
            val fragment = IProjectFragmentImpl()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun init() {

    }

    override fun setPresenter() {
        presenter = ProjectPresenter()
    }
}