package com.example.shinelon.wanandroid.viewimp

import android.support.v4.app.Fragment
import com.example.shinelon.wanandroid.fragment.CommonDialogListener
import com.example.shinelon.wanandroid.modle.DataBean
import com.example.shinelon.wanandroid.modle.DataBeanBanner

/**
 * 不同页面所应具有的不同具体行为，各个页面在属于自己的View里面扩展
 */
interface IHomeFragmentView: IView {
    fun createBannerView(mutableList: MutableList<DataBeanBanner>)
    fun createContentView(data: DataBean?)
    fun showLoadMoreView()
    fun hideLoadMoreView()
    fun showLoadMoreErrorView()
    fun hideLoadMoreErrorView()
    fun getPageFragment(): Fragment
    fun changeLoveView(isCollected: Boolean)
}