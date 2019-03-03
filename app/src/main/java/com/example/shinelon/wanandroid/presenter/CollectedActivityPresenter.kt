package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import android.util.Log
import com.example.shinelon.wanandroid.CommonWebViewActivity
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.modle.Articles
import com.example.shinelon.wanandroid.networkimp.FirstPageRetrofit
import com.example.shinelon.wanandroid.viewimp.ICollectedActivityView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CollectedActivityPresenter: AbsPresenter<ICollectedActivityView>() {
    var view: ICollectedActivityView? = null
    val TAG = "ISearchArticleActivityP"
    override fun addView(view: ICollectedActivityView) {
        this.view = view
    }

    override fun jumpToTarget(flag: ActionFlag,intent: Intent) {

    }

    fun loadWeb(url: String,id: Long){
        val context = view!!.getActivityContext()
        val intent = Intent(context, CommonWebViewActivity::class.java)
        intent.putExtra("web_url",url)
        //这里是 originId 也就是原始 id
        intent.putExtra("id",id)
        intent.putExtra("collect_state",true)
        context.startActivityForResult(intent,6)
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun getCollectedArticles(num: Int = 0){
        RetrofitClient.INSTANCE.retrofit.create(FirstPageRetrofit::class.java)
                .getCollectedArtls(num)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Articles> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "获取收藏文章 onSubscribe")
                    }

                    override fun onNext(t: Articles) {
                        Log.d(TAG, "获取收藏文章 onNext")
                        if (t.errorCode >= 0) {
                            view?.createContentView(t.data)
                        } else {
                            view?.createContentView(null)
                        }
                    }

                    override fun onComplete() {
                        Log.d(TAG, "获取收藏文章 onComplete")
                        view?.createContentView(null)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "获取收藏文章 $e")
                    }
                })
    }
}