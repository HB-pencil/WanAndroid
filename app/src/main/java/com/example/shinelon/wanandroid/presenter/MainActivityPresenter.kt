package com.example.shinelon.wanandroid.presenter

import com.example.shinelon.wanandroid.viewimp.IMainActivityView

class MainActivityPresenter: AbsPresenter<IMainActivityView>() {
    var view: IMainActivityView? = null
    override fun addView(v: IMainActivityView) {
        view = v
    }
}