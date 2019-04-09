package com.example.shinelon.wanandroid.viewimp


import android.support.v4.app.DialogFragment
import com.example.shinelon.wanandroid.fragment.CommonDialogListener

interface IMainActivityView: IView {
    fun showWarnDialog(listener: CommonDialogListener, message: String, title: String,tag: String): DialogFragment
    fun showHotWords(list: MutableList<String>)
    fun hideHotWords()
    fun updateHeaderView(isOnline: Boolean,name: String?)
    fun getOnlineState(): Boolean
}