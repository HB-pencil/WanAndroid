package com.example.shinelon.wanandroid.viewimp

interface ILoginActivityView: IView {
    fun showSuccess(sucMsg: String)
    fun showError(errMsg: String)
    fun getAccount(): String
    fun getPassword(): String
}