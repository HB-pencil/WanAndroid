package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import android.util.Log
import com.example.shinelon.wanandroid.CommomItemActivityImpl
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.modle.Articles
import com.example.shinelon.wanandroid.modle.ProjectInfo
import com.example.shinelon.wanandroid.modle.StructData
import com.example.shinelon.wanandroid.networkimp.ProjectPageRetrofit
import com.example.shinelon.wanandroid.networkimp.StructPageRetrofit
import com.example.shinelon.wanandroid.viewimp.IProjectFragmentView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ProjectPresenter: AbsPresenter<IProjectFragmentView>() {
    var view: IProjectFragmentView? = null
    val TAG = "ProjectPresenter"

    override fun addView(view: IProjectFragmentView) {
        this.view = view
    }

    override fun jumpToTarget(flag: ActionFlag, intent: Intent) {
        val context = view?.getActivityContext()
        context?.startActivity(intent)
        context?.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    }

    fun getArticles(cid: Int) {
        val intent = Intent(view!!.getActivityContext(),CommomItemActivityImpl::class.java)
        intent.putExtra("cid",cid)
        jumpToTarget(ActionFlag.OTHER,intent)
    }

    override fun checkPermissions(permissions: Array<String>) {}

    fun getProjectDetails(){
            RetrofitClient.INSTANCE.retrofit.create(ProjectPageRetrofit::class.java)
                    .getProjectInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<ProjectInfo> {
                        override fun onSubscribe(d: Disposable) {
                            Log.d(TAG,"onSubscribe")
                        }

                        override fun onNext(t: ProjectInfo) {
                            Log.d(TAG,"onNext")
                            if (t.errorCode >= 0) {
                                view?.initPages(t.data)
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
