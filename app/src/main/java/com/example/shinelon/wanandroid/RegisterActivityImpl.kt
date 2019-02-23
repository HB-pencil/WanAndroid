package com.example.shinelon.wanandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.shinelon.wanandroid.helper.toast
import com.example.shinelon.wanandroid.presenter.RegisterActivityPresenter
import com.example.shinelon.wanandroid.viewimp.IRegisterAvtivityView
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_toolbar.*

class RegisterActivityImpl:AppCompatActivity(), IRegisterAvtivityView {
    var presenter: RegisterActivityPresenter? = null
    val TAG = "RegisterActivityImpl"

    override fun setPresenter() {
        if(presenter == null) {
            presenter = RegisterActivityPresenter()
            presenter?.addView(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(toolbar_base)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_base.setNavigationOnClickListener {
            finish()
        }

        if (presenter == null) setPresenter()

        register_btn.setOnClickListener {
            presenter?.register()
        }
    }

    override fun getActivityContext() = this

    override fun showSuccess(message: String) = toast(this,message)

    override fun showError(message: String) = toast(this,message)

    override fun getAccount() = username.text.toString()

    override fun getPassword() = password.text.toString()

    override fun getConfPassword() = confPassword.text.toString()
}
