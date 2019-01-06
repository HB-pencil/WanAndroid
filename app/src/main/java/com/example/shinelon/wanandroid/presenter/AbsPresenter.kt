package com.example.shinelon.wanandroid.presenter

import android.app.Activity
import com.example.shinelon.wanandroid.helper.ActionFlag
import io.reactivex.annotations.Nullable

/**
 * Created by Shinelon on 2018/4/28.
 */
abstract class AbsPresenter<IView>{
    /**
     * 添加视图activity或者fragment
     */
    abstract fun addView(view: IView)
    abstract fun jumpToTarget(flag: ActionFlag)

    open fun checkPermissions(permissions: Array<String>){}
}