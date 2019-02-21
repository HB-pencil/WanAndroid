package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import android.util.Log
import com.example.shinelon.wanandroid.CommonWebViewActivity
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.modle.Articles
import com.example.shinelon.wanandroid.networkimp.FirstPageRetrofit
import com.example.shinelon.wanandroid.viewimp.ISearchArticleActivityView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ISearchArticleActivityPresenter: AbsPresenter<ISearchArticleActivityView>(){
    var view: ISearchArticleActivityView? =null
    val TAG = "ISearchArticleActivityP"
    override fun addView(view: ISearchArticleActivityView) {
        this.view = view
    }

    override fun jumpToTarget(flag: ActionFlag,intent: Intent) {

    }

    fun loadWeb(url: String){
        val context = view!!.getActivityContext()
        val intent = Intent(context, CommonWebViewActivity::class.java)
        intent.putExtra("web_url",url)
        context.startActivity(intent)
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun getSearchArticle(num: Int = 0,k: String){
        RetrofitClient.INSTANCE.retrofit.create(FirstPageRetrofit::class.java)
                .getArticleSearch(num,k)
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
                        view?.createContentView(null)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "获取文章 $e")
                    }
                })
    }
}