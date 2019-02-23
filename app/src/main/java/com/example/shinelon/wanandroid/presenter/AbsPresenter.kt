package com.example.shinelon.wanandroid.presenter

import android.app.Activity
import android.content.Intent
import com.example.shinelon.wanandroid.helper.ActionFlag
import io.reactivex.annotations.Nullable

/**
 * Created by Shinelon on 2018/4/28.
 */
abstract class AbsPresenter<in IView>{
    /**
     * 添加视图activity或者fragment
     */
    abstract fun addView(view: IView)
    abstract fun jumpToTarget(flag: ActionFlag,intent: Intent)

    open fun checkPermissions(permissions: Array<String>){}
}