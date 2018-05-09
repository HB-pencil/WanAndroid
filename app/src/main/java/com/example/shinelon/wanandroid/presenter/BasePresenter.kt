package com.example.shinelon.wanandroid.presenter

import com.example.shinelon.wanandroid.viewimp.BaseView

/**
 * Created by Shinelon on 2018/4/28.
 */
interface BasePresenter {
    /**
     * 添加视图activity或者fragment
     */
    fun addView(baseView: BaseView)
    /**
     * 移除视图activity或者fragment防止内存泄漏
     */
    fun removeView(baseView: BaseView)
}