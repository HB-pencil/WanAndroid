package com.example.shinelon.wanandroid

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.utils.toast
import com.example.shinelon.wanandroid.presenter.LoginActivityPresenter
import com.example.shinelon.wanandroid.viewimp.ILoginActivityView
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivityImpl:AppCompatActivity(),ILoginActivityView {
    var presenter: LoginActivityPresenter? = null
    val TAG = "LoginActivityImpl"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if(presenter == null) setPresenter()
        login_btn.setOnClickListener {
            presenter!!.login()
        }

        remember_cb.setOnCheckedChangeListener{
            _, isChecked -> presenter?.saveAutoLoginState(isChecked)
        }
        register_tv.setOnClickListener {
            intent = Intent(this,RegisterActivityImpl::class.java)
            presenter?.jumpToTarget(ActionFlag.REGISTER,intent)
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
