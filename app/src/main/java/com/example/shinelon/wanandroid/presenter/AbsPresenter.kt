package com.example.shinelon.wanandroid.presenter

import android.app.Activity

/**
 * Created by Shinelon on 2018/4/28.
 */
abstract class AbsPresenter<IView>{
    /**
     * 添加视图activity或者fragment
     */
    abstract fun addView(view: IView)
    abstract fun jumpToTarget()

    open fun checkPermissions(permissions: Array<String>){}
}