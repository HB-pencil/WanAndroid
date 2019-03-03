package com.example.shinelon.wanandroid.viewimp

import com.example.shinelon.wanandroid.modle.DataBean

interface ICollectedActivityView: IView {
    fun showLoadingView()
    fun hideLoadingView()
    fun createContentView(dataBean: DataBean?)
}