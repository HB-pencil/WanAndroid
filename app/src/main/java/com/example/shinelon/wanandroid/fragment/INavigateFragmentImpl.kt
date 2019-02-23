package com.example.shinelon.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shinelon.wanandroid.R
import com.example.shinelon.wanandroid.presenter.NavigatePresenter
import com.example.shinelon.wanandroid.viewimp.INavigateFragmentView

class INavigateFragmentImpl: BaseFragment(),INavigateFragmentView {
    var presenter: NavigatePresenter? = null
    val TAG = "INavigateFragmentImpl"
    companion object {
        fun getInstance(bundle: Bundle?): INavigateFragmentImpl {
            val fragment = INavigateFragmentImpl()
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun getLayoutId(id: Int) = R.layout.fragment_home

    override fun init() {

    }

    override fun setPresenter() {
        presenter = NavigatePresenter()
        presenter?.addView(this)
    }
}