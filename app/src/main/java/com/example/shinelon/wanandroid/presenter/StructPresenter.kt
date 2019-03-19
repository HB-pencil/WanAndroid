package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import android.util.Log
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.modle.StructData
import com.example.shinelon.wanandroid.networkimp.StructPageRetrofit
import com.example.shinelon.wanandroid.viewimp.IStructFragmentView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class StructPresenter: AbsPresenter<IStructFragmentView>() {
    var view: IStructFragmentView? = null
    val TAG = "StructPresenter"

    override fun addView(view: IStructFragmentView) {
        this.view = view
    }

    override fun jumpToTarget(flag: ActionFlag, intent: Intent) {
        val context =  view!!.getActivityContext()
        context.startActivity(intent)
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun checkPermissions(permissions: Array<String>) {
    }

    fun getStructDetails() {
        RetrofitClient.INSTANCE.retrofit.create(StructPageRetrofit::class.java)
                .getStructParent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<StructData>{
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG,"onSubscribe")
                    }

                    override fun onNext(t: StructData) {
                        Log.d(TAG,"onNext")
                        if (t.errorCode >= 0) {
                            view?.initPages(t.data)
                            view?.hideErrorView()
                        } else {
                            view?.showErrorView()
                            Log.e(TAG,t.errorMsg)
                        }
                    }

                    override fun onComplete() {
                        Log.d(TAG,"onComplete")
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG,e.message)
                        view?.showErrorView()
                    }
                })
    }
}