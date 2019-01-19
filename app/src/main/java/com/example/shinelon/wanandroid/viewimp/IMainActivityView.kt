package com.example.shinelon.wanandroid.viewimp

import android.view.View
import com.example.shinelon.wanandroid.modle.Banner
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
    fun createBannerView(mutableList: MutableList<DataBeanBanner>): MutableList<View>
}