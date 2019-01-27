package com.example.shinelon.wanandroid.viewimp

import com.example.shinelon.wanandroid.modle.DataBean
import com.example.shinelon.wanandroid.modle.DataBeanBanner

/**
 * 不同页面所应具有的不同具体行为，各个页面在属于自己的View里面扩展
 */
interface IMainActivityView: IView {
    fun updateHeaderView(isOnline: Boolean,name: String?)
    fun getOnlineState(): Boolean
    fun setOnlineState(isOnline: Boolean)
    fun showHotWords(list: MutableList<String>)
    fun hideHotWords()
    fun createBannerView(mutableList: MutableList<DataBeanBanner>)
    fun createContentView(data: DataBean?)
}