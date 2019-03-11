package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import android.util.Log
import com.example.shinelon.wanandroid.CommonWebViewActivity
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.modle.NavigateInfo
import com.example.shinelon.wanandroid.networkimp.NavigatePageRetrofit
import com.example.shinelon.wanandroid.viewimp.INavigateFragmentView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class NavigatePresenter: AbsPresenter<INavigateFragmentView>() {
    var view: INavigateFragmentView? =null
    val TAG = "NavigatePresenter"
    override fun addView(view: INavigateFragmentView) {
        this.view = view
    }

    override fun jumpToTarget(flag: ActionFlag, intent: Intent) {

    }

    override fun checkPermissions(permissions: Array<String>) {}


    fun getNavigateWebSites(){
        RetrofitClient.INSTANCE.retrofit.create(NavigatePageRetrofit::class.java)
                .getNavigationInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<NavigateInfo> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG,"onSubscribe")
                    }

                    override fun onNext(t: NavigateInfo) {
                        Log.d(TAG,"onNext")
                        if (t.errorCode>=0){
                            view?.showErrorView(false)
                            view?.showWebSiteName(t.data)
                        }else{
                            view?.showErrorView(true)
                            Log.e(TAG,t.errorMsg)
                        }
                    }

                    override fun onComplete() {
                        Log.d(TAG,"onComplete")
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG,e.message)
                        view?.showErrorView(true)
                    }
                })
    }

    fun loadWeb(url: String){
        val context = view!!.getActivityContext()
        val intent = Intent(context, CommonWebViewActivity::class.java)
        context.startActivity(intent)
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}