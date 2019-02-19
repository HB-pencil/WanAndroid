package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import com.example.shinelon.wanandroid.CommonWebViewActivity
import com.example.shinelon.wanandroid.LoginActivityImpl
import com.example.shinelon.wanandroid.UserInfo
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.helper.toast
import com.example.shinelon.wanandroid.modle.Articles
import com.example.shinelon.wanandroid.modle.Banner
import com.example.shinelon.wanandroid.modle.DatasBean
import com.example.shinelon.wanandroid.modle.HotWord
import com.example.shinelon.wanandroid.networkimp.FirstPageRetrofit
import com.example.shinelon.wanandroid.networkimp.LogInOutRetrofit
import com.example.shinelon.wanandroid.utils.PreferenceUtil
import com.example.shinelon.wanandroid.viewimp.IMainActivityView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.Nullable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityPresenter : AbsPresenter<IMainActivityView>() {
    var view: IMainActivityView? = null
    val TAG = "MainActivityPresenter"
    override fun addView(v: IMainActivityView) {
        view = v
    }


    override
    fun checkPermissions(permissions: Array<String>) {
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
        val spUtli = PreferenceUtil.getInstance()
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
                                view?.setOnlineState(true)
                            } else {
                                view?.updateHeaderView(false, "")
                                view?.setOnlineState(false)
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            view?.updateHeaderView(false, "")
                            view?.setOnlineState(false)
                        }
                    })
        } else if (isAuto) {
            val spUtil = PreferenceUtil()
            spUtil.putString("cookie")
            spUtil.putLong("expireTime", Long.MIN_VALUE)
            spUtil.putBoolean("isAuto", false)
            view?.updateHeaderView(false, "")
            view?.setOnlineState(false)
        }
        Log.w(TAG, "保存的cookie:$cookie\n保存的时间:$expireTime")
    }

    override fun jumpToTarget(flag: ActionFlag) {
        if (flag != ActionFlag.LOGIN) return
        val context = view!!.getActivityContext()
        val intent = Intent(context, LoginActivityImpl::class.java)
        context.startActivity(intent)
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun login() {
        jumpToTarget(ActionFlag.LOGIN)
    }

    fun logout() {
        val spUtil = PreferenceUtil.getInstance()
        spUtil.putString("username")
        spUtil.putString("cookie")
        spUtil.putLong("expireTime")
        spUtil.putBoolean("isAuto")
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

    fun getBanner() {
        RetrofitClient.INSTANCE.retrofit.create(FirstPageRetrofit::class.java)
                .getBanner()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Banner> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "获取banner onSubscribe()")
                    }

                    override fun onNext(t: Banner) {
                        Log.d(TAG, "获取Banner onNext")
                        if (t.errorCode >= 0) {
                            view?.createBannerView(t.data)
                        } else {
                            view?.createBannerView(mutableListOf())
                        }
                    }

                    override fun onComplete() {
                        Log.d(TAG, "获取Banner onComplete")
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "获取Banner onError $e")
                        view?.createBannerView(mutableListOf())
                    }
                })
    }

    fun onPageItemClick(url: String) {
        loadWeb(url)
    }

    /**
     * 负责拿到网络请求结果给View，不用关心view怎么刷新UI
     */
    fun getArticleList(num: Int = 0) {
        RetrofitClient.INSTANCE.retrofit.create(FirstPageRetrofit::class.java)
                .getArticle(num)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Articles> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "获取文章 onSubscribe")
                    }

                    override fun onNext(t: Articles) {
                        Log.d(TAG, "获取文章 onNext")
                        if (t.errorCode >= 0) {
                            view?.createContentView(t.data)
                        } else {
                            view?.createContentView(null)
                        }
                    }

                    override fun onComplete() {
                        Log.d(TAG, "获取文章 onComplete")
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "获取文章 $e")
                        view?.createContentView(null)
                    }
                })
    }

    fun loadWeb(url: String){
        val context = view!!.getActivityContext()
        val intent = Intent(context,CommonWebViewActivity::class.java)
        intent.putExtra("web_url",url)
        context.startActivity(intent)
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}