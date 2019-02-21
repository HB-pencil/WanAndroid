package com.example.shinelon.wanandroid.viewimp

import com.example.shinelon.wanandroid.modle.DataBean

interface ISearchArticleActivityView: IView{
    fun showLoadingView()
    fun hideLoadingView()
    fun createContentView(dataBean: DataBean?)
}