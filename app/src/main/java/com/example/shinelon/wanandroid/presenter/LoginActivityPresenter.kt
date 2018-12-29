package com.example.shinelon.wanandroid.presenter

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.example.shinelon.wanandroid.MainActivityImpl
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.model.firstpage.LoginRsp
import com.example.shinelon.wanandroid.networkimp.LogInOutRetrofit
import com.example.shinelon.wanandroid.viewimp.ILoginActivityView
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LoginActivityPresenter: AbsPresenter<ILoginActivityView>(){
    val TAG = "LoginActivityPresenter"
    var view: ILoginActivityView? = null

    override fun addView(v: ILoginActivityView) {
        view = v
    }

    fun login(){
        val retrofit = RetrofitClient.get().retrofit
        val result = retrofit.create(LogInOutRetrofit::class.java)
                .login(getAccount(),getPassWord())
                .subscribeOn(Schedulers.io())
                .subscribe { object: Observer<LoginRsp>{
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG,"onSubscribe")
                    }
                    override fun onNext(t: LoginRsp) {
                        Log.d(TAG,"onNext")
                        view?.showSuccess("登录成功！")
                    }
                    override fun onComplete() {
                        Log.d(TAG,"onComplete")
                        jumpToTarget()
                    }
                    override fun onError(e: Throwable) {
                        Log.d(TAG,"onError")
                        view?.showError("登录失败！")
                    }
                }}
        if(!result.isDisposed) result.dispose()

    }

    fun getAccount() = view?.getAccount()

    fun getPassWord() = view?.getPassword()

    override fun jumpToTarget() {
        val context = view?.getActivityContext()
        val intent = Intent(context,MainActivityImpl::class.java)
        context!!.startActivity(intent)
    }
}