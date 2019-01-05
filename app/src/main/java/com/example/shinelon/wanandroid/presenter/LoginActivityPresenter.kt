package com.example.shinelon.wanandroid.presenter

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.annotation.MainThread
import android.util.Log
import com.example.shinelon.wanandroid.MainActivityImpl
import com.example.shinelon.wanandroid.UserInfo
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.model.firstpage.LoginRsp
import com.example.shinelon.wanandroid.networkimp.LogInOutRetrofit
import com.example.shinelon.wanandroid.utils.PreferenceUtil
import com.example.shinelon.wanandroid.viewimp.ILoginActivityView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

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
                            isSaveCookies(isAutoLogin())
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

    fun isSaveCookies(isSave: Boolean) {
        var cookieString = ""
        var expireTime = Long.MIN_VALUE
        val spUtil = PreferenceUtil.getInstance()
        if(isSave) {
            cookieString = UserInfo.INSTANCE.cookie
            expireTime = UserInfo.INSTANCE.expire
        }
        spUtil.putString("cookie",cookieString)
        spUtil.putLong("expireTime",expireTime)
        spUtil.putString("username",UserInfo.INSTANCE.userName)
        spUtil.commit()
    }

    fun initCookie(response: Response<LoginRsp>){
        val list = response.headers().values("Set-Cookie")
        val src = list[list.size-1].split(";")[2].replace("Expires=","")
        val expire = parseTime(src)
        UserInfo.INSTANCE.expire = expire
        val res = parseCookie(response)
        UserInfo.INSTANCE.cookie = res
        Log.d(TAG,"cookie:$res")
    }

    fun parseCookie(response: Response<LoginRsp>): String{
        val list = response.headers().values("Set-Cookie")
        val sb = StringBuilder()
        list.forEach{
            val target = it.split(";")[0]
            sb.append("$target;")
        }
        return sb.toString()
    }

    override fun jumpToTarget() {
        val context = view?.getActivityContext()
        val intent = Intent(context, MainActivityImpl::class.java)
        intent.putExtra("name",getAccount())
        intent.putExtra("isOnline",true)
        context!!.startActivity(intent)
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun saveAutoLoginState(isAuto: Boolean){
        val spUtil = PreferenceUtil.getInstance()
        spUtil.putBoolean("isAuto",isAuto)
        spUtil.commit()
        Log.d(TAG,"是否自动登录：$isAuto")
    }

    fun isAutoLogin(): Boolean{
        val spUtil = PreferenceUtil.getInstance()
        return spUtil.getBoolean("isAuto")
    }

    /**
     * @param time 时间格式  Fri, 01-Feb-2019 15:40:01 GMT
     */
    fun parseTime(time: String): Long{
        try{
            //FIXME Date.parse()应该找到替代的方法
            val res = Date.parse(time)
            Log.d(TAG,"$res")
            return res
        }catch (e: Exception){
            Log.e(TAG,"日期格式化错误 ${e.printStackTrace()}")
        }
        return Long.MIN_VALUE
    }
}