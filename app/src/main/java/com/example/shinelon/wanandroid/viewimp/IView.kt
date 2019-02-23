package com.example.shinelon.wanandroid.viewimp

import android.app.Activity
import android.support.v4.app.FragmentActivity

/**
 * Created by Shinelon on 2018/4/28. MVP 基础视图接口,最基础的行为
 */
interface IView {
    fun setPresenter()
    fun getActivityContext(): FragmentActivity
}