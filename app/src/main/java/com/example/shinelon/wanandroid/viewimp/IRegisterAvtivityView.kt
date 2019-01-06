package com.example.shinelon.wanandroid.viewimp

interface IRegisterAvtivityView: IView {
    fun showSuccess(message: String)
    fun showError(message: String)
    fun getAccount(): String
    fun getPassword(): String
    fun getConfPassword(): String
}