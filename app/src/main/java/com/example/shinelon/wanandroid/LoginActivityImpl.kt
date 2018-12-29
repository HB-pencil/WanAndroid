package com.example.shinelon.wanandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.shinelon.wanandroid.helper.toast
import com.example.shinelon.wanandroid.presenter.LoginActivityPresenter
import com.example.shinelon.wanandroid.viewimp.ILoginActivityView
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivityImpl:AppCompatActivity(),ILoginActivityView {
    var presenter: LoginActivityPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if(presenter == null) setPresenter()
        login_btn.setOnClickListener {
            presenter!!.login()
        }
    }

    override fun setPresenter() {
        presenter = LoginActivityPresenter()
        presenter?.addView(this)
    }

    override fun showSuccess(sucMsg: String) {
        toast(this,"登录成功！")
    }

    override fun showError(errMsg: String) {
        toast(this,"登录失败，请重试！")
    }

    override fun getAccount() = username.text.toString()

    override fun getPassword() = password.text.toString()

    override fun getActivityContext() = this
}
