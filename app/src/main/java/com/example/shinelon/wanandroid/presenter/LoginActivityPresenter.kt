package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import android.util.Log
import com.example.shinelon.wanandroid.MainActivityImpl
import com.example.shinelon.wanandroid.RegisterActivityImpl
import com.example.shinelon.wanandroid.UserInfo
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.modle.loginout.LoginRsp
import com.example.shinelon.wanandroid.networkimp.LogInOutRetrofit
import com.example.shinelon.wanandroid.utils.CookieUtils
import com.example.shinelon.wanandroid.utils.PreferenceUtils
import com.example.shinelon.wanandroid.viewimp.ILoginActivityView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

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
                            var intent = Intent(view!!.getActivityContext(), RegisterActivityImpl::class.java)
                            jumpToTarget(ActionFlag.HOME,intent)
                            UserInfo.INSTANCE.userName = getAccount()
                            UserInfo.INSTANCE.isOnline = true
                            CookieUtils.initCookie(response)
                            CookieUtils.isSaveCookies(CookieUtils.isAutoLogin())
                        } else {
                            view?.showError(response.body()?.errorMsg?:"登录失败！")
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



    override fun jumpToTarget(flag: ActionFlag,intent: Intent) {
        val context = view?.getActivityContext()
        if(flag  == ActionFlag.HOME) {
            intent.putExtra("name",getAccount())
            intent.putExtra("isOnline",true)
        }
        context!!.startActivity(intent)
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun saveAutoLoginState(isAuto: Boolean){
        val spUtil = PreferenceUtils.getInstance()
        spUtil.putBoolean("isAuto",isAuto)
        spUtil.commit()
        Log.d(TAG,"是否自动登录：$isAuto")
    }

}