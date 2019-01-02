package com.example.shinelon.wanandroid.presenter

import android.app.Activity
import android.content.Intent
import android.preference.PreferenceManager
import android.support.annotation.MainThread
import android.util.Log
import com.example.shinelon.wanandroid.MainActivityImpl
import com.example.shinelon.wanandroid.UserInfo
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.model.firstpage.LoginRsp
import com.example.shinelon.wanandroid.networkimp.LogInOutRetrofit
import com.example.shinelon.wanandroid.viewimp.ILoginActivityView
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.lang.StringBuilder

class LoginActivityPresenter : AbsPresenter<ILoginActivityView>() {
    val TAG = "LoginActivityPresenter"
    var view: ILoginActivityView? = null

    override fun addView(v: ILoginActivityView) {
        view = v
    }

    fun login() {
        RetrofitClient.INSTANCE.retrofit.create(LogInOutRetrofit::class.java)
                .login(getAccount(), getPassWord())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //注意此处的参数，不是函数！不能直接传入lambadas
                .subscribe(object : Observer<Response<LoginRsp>> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe")
                    }

                    override fun onNext(response: Response<LoginRsp>) {
                        Log.d(TAG, "onNext -> errorCode:" + response.body()!!.errorCode)
                        if (response.body()!!.errorCode >= 0) {
                            view?.showSuccess("登录成功！")
                            jumpToTarget()
                            UserInfo.INSTANCE.userName = getAccount()
                            initCookie(response)
                        } else {
                            view?.showError("登录失败，请重试！")
                        }
                    }

                    override fun onComplete() {
                        Log.d(TAG, "onComplete")
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, "onError")
                        view?.showError("登录失败！")
                    }
                })
        Log.d(TAG, "登录执行！")
    }

    fun getAccount() = view?.getAccount()?:""

    fun getPassWord() = view?.getPassword()?:""

    fun savedCookies(response: Response<LoginRsp>) {
        response.headers()
    }

    fun initCookie(response: Response<LoginRsp>){
        val list = response.headers().values("Set-Cookie")
        val sb = StringBuilder()
        list.forEach{
            val target = it.split(";")[0]
            sb.append("$target;")
        }
        val res = sb.toString()
        UserInfo.INSTANCE.cookie = res
        Log.d(TAG,"cookie:$res")
    }

    override fun jumpToTarget() {
        val context = view?.getActivityContext()
        val intent = Intent(context, MainActivityImpl::class.java)
        intent.putExtra("name",getAccount())
        intent.putExtra("onLineState",true)
        context!!.startActivity(intent)
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun saveAutoLoginState(isAuto: Boolean){
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(view!!.getActivityContext())
        val editor = preferenceManager.edit()
        editor.putBoolean("isAuto",isAuto)
        editor.apply()
        Log.d(TAG,"是否自动登录：$isAuto")
    }
}