package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.example.shinelon.wanandroid.CommonWebViewActivity
import com.example.shinelon.wanandroid.LoginActivityImpl
import com.example.shinelon.wanandroid.UserInfo
import com.example.shinelon.wanandroid.fragment.CommonDialogFragment
import com.example.shinelon.wanandroid.fragment.CommonDialogListener
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.helper.toast
import com.example.shinelon.wanandroid.modle.Articles
import com.example.shinelon.wanandroid.modle.Banner
import com.example.shinelon.wanandroid.modle.HotWord
import com.example.shinelon.wanandroid.networkimp.FirstPageRetrofit
import com.example.shinelon.wanandroid.networkimp.LogInOutRetrofit
import com.example.shinelon.wanandroid.utils.NetWorkUtils
import com.example.shinelon.wanandroid.utils.PreferenceUtils
import com.example.shinelon.wanandroid.viewimp.IMainActivityView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityPresenter : AbsPresenter<IMainActivityView>(),CommonDialogListener {
    var view: IMainActivityView? = null
    val TAG = "MainActivityPresenter"
    override fun addView(v: IMainActivityView) {
        view = v
    }


    override fun checkPermissions(permissions: Array<String>) {
        val res = isGrantedPermissions(permissions)
        if (!res) {
            ActivityCompat.requestPermissions(view!!.getActivityContext(), permissions, 0)
        }
    }

    fun isGrantedPermissions(permissions: Array<String>): Boolean {
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(view!!.getActivityContext(), it) == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }

    fun checkAutoLogin() {
        val spUtli = PreferenceUtils.getInstance()
        val isAuto = spUtli.getBoolean("isAuto")
        val expireTime = spUtli.getLong("expireTime")
        val userName = spUtli.getString("username")
        val cookie = spUtli.getString("cookie")

        UserInfo.INSTANCE.isAuto = isAuto
        UserInfo.INSTANCE.expire = expireTime
        UserInfo.INSTANCE.userName = userName
        UserInfo.INSTANCE.cookie = cookie

        if (isAuto && expireTime > System.currentTimeMillis()) {
            val retrofitClient = RetrofitClient.INSTANCE.retrofit
            retrofitClient.create(LogInOutRetrofit::class.java)
                    .checkAutoLogin()
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            //FIXME 自动登录成功验证，不知道具体什么是登录成功？
                            if (response.isSuccessful) {
                                view?.updateHeaderView(true, userName)
                                UserInfo.INSTANCE.isOnline = true
                            } else {
                                view?.updateHeaderView(false, "")
                                UserInfo.INSTANCE.isOnline = false
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            view?.updateHeaderView(false, "")
                            UserInfo.INSTANCE.isOnline = false
                        }
                    })
        } else if (isAuto) {
            UserInfo.INSTANCE.isAuto = false
            val spUtil = PreferenceUtils()
            spUtil.putString("cookie")
            spUtil.putLong("expireTime", Long.MIN_VALUE)
            spUtil.putBoolean("isAuto", false)
            view?.updateHeaderView(false, "")
            UserInfo.INSTANCE.isOnline = false
        }
        Log.w(TAG, "保存的cookie:$cookie\n保存的时间:$expireTime")
    }

    override fun jumpToTarget(flag: ActionFlag,intent: Intent) {
        Log.d(TAG,"jumpToTarget ${flag.name}")
        val context = view!!.getActivityContext()
        context.startActivity(intent)
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun login() {
        val context = view!!.getActivityContext()
        val intent = Intent(context, LoginActivityImpl::class.java)
        jumpToTarget(ActionFlag.LOGIN,intent)
    }

    fun logout() {
        val spUtil = PreferenceUtils.getInstance()
        spUtil.putString("username")
        spUtil.putString("cookie")
        spUtil.putLong("expireTime")
        spUtil.putBoolean("isAuto")
        UserInfo.INSTANCE.isOnline = false
        spUtil.commit()
        view?.updateHeaderView(false, "")
    }

    fun getHotWords() {
        val list = mutableListOf<String>()
        RetrofitClient.INSTANCE.retrofit.create(FirstPageRetrofit::class.java)
                .getHotWord()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<HotWord> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "搜素热词 onSubscribe")
                    }

                    override fun onNext(t: HotWord) {
                        Log.d(TAG, "搜索热词 onNext")
                        if (t.errorCode >= 0) {
                            t.data.forEach {
                                list.add(it.name)
                            }
                        } else {
                            toast(view!!.getActivityContext(), t.errorMessage)
                        }
                        view?.showHotWords(list)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "搜索热词 onComplete")
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, "搜索热词 onError")
                        view?.showHotWords(list)
                    }
                })
    }

    fun checkNetworkState(){
        val res = NetWorkUtils.isNetWorkAvailable(view!!.getActivityContext())
        if (!res){
            view?.showWarnDialog(this,"检测不到可用网络，请确保网络通畅！","警告")
        }
    }


    override fun onPositiveClick() {
        return
    }

    override fun onNegativeClick() {
        view?.getActivityContext()?.finish()
    }
}