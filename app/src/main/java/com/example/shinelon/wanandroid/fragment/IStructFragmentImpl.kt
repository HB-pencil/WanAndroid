package com.example.shinelon.wanandroid.fragment

import android.os.Bundle
import com.example.shinelon.wanandroid.R
import com.example.shinelon.wanandroid.presenter.StructPresenter
import com.example.shinelon.wanandroid.viewimp.IStructFragmentView

class IStructFragmentImpl: BaseFragment(),IStructFragmentView {
    var presenter: StructPresenter? = null
    val TAG = "IStructFragmentImpl"

    companion object {
        fun getInstance(bundle: Bundle?): IStructFragmentImpl {
            val fragment = IStructFragmentImpl()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId() = R.layout.fragment_home

    override fun init() {

    }

    override fun setPresenter() {
        presenter?.addView(this)
    }
}