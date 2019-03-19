package com.example.shinelon.wanandroid.viewimp

import com.example.shinelon.wanandroid.modle.ProjectCategory

interface IProjectFragmentView: IView{
    fun showErrorView()
    fun hideErrorView()
    fun initPages(data: MutableList<ProjectCategory>)
}