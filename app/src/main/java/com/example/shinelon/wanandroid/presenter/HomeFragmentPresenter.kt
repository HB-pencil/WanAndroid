package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import android.util.Log
import com.example.shinelon.wanandroid.CommonWebViewActivity
import com.example.shinelon.wanandroid.fragment.IHomeFragmentImpl
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.modle.Articles
import com.example.shinelon.wanandroid.modle.Banner
import com.example.shinelon.wanandroid.networkimp.FirstPageRetrofit
import com.example.shinelon.wanandroid.viewimp.IHomeFragmentView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class HomeFragmentPresenter: AbsPresenter<IHomeFragmentView>() {
    var view: IHomeFragmentView? = null
    val TAG = "MainActivityPresenter"
    override fun addView(v: IHomeFragmentView) {
        view = v
    }

    override fun jumpToTarget(flag: ActionFlag, intent: Intent) {

    }

    override fun checkPermissions(permissions: Array<String>) {

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

    fun onPageItemClick(url: String,collect: Boolean) {
        loadWeb(url,collect)
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

    fun loadWeb(url: String,collect: Boolean){
        val context = view!!.getActivityContext()
        val intent = Intent(context, CommonWebViewActivity::class.java)
        intent.putExtra("web_url",url)
        intent.putExtra("collect_state",collect)
        context.startActivity(intent)
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}