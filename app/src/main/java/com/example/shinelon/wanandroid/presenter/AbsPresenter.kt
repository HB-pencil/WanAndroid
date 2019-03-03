package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.viewimp.IView

/**
 * Created by Shinelon on 2018/4/28.
 */
abstract class AbsPresenter<V: IView>{
    /**
     * 添加视图activity或者fragment
     */
    abstract fun addView(view: V)
    abstract fun jumpToTarget(flag: ActionFlag,intent: Intent)

    open fun checkPermissions(permissions: Array<String>){}
}