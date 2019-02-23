package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.viewimp.INavigateFragmentView

class NavigatePresenter: AbsPresenter<INavigateFragmentView>() {
    var view: INavigateFragmentView? =null
    override fun addView(view: INavigateFragmentView) {
        this.view = view
    }

    override fun jumpToTarget(flag: ActionFlag, intent: Intent) {

    }

    override fun checkPermissions(permissions: Array<String>) {}
}