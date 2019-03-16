package com.example.shinelon.wanandroid.viewimp

import com.example.shinelon.wanandroid.modle.SuperData

interface IStructFragmentView: IView {
    fun showErrorView()
    fun hideErrorView()
    fun initPages(data: MutableList<SuperData>)
}