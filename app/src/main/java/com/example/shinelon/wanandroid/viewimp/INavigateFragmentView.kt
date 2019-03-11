package com.example.shinelon.wanandroid.viewimp

import com.example.shinelon.wanandroid.modle.NavigateData

interface INavigateFragmentView: IView {
    fun showWebSiteName(response: MutableList<NavigateData>)
    fun showErrorView(visible: Boolean)
}