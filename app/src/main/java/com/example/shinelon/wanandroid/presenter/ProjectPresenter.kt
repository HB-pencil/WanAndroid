package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.viewimp.IProjectFragmentView

class ProjectPresenter: AbsPresenter<IProjectFragmentView>() {
    var view: IProjectFragmentView? = null
    override fun addView(view: IProjectFragmentView) {
        this.view = view
    }

    override fun jumpToTarget(flag: ActionFlag, intent: Intent) {

    }

    override fun checkPermissions(permissions: Array<String>) {}
}