package com.example.shinelon.wanandroid.viewimp

import com.example.shinelon.wanandroid.modle.DataBean

interface ICommonItemActivityView: IView{
    fun showLoadingView()
    fun hideLoadingView()
    fun createContentView(dataBean: DataBean?)
    fun changeLoveView(isCollected: Boolean)
    fun showErrorView()
    fun hideErrorView()
}