package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import android.support.annotation.MainThread
import android.util.Log
import com.example.shinelon.wanandroid.LoginActivityImpl
import com.example.shinelon.wanandroid.MainActivityImpl
import com.example.shinelon.wanandroid.UserInfo
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.modle.loginout.LoginRsp
import com.example.shinelon.wanandroid.modle.loginout.RegisterRsp
import com.example.shinelon.wanandroid.networkimp.LogInOutRetrofit
import com.example.shinelon.wanandroid.utils.CookieUtil
import com.example.shinelon.wanandroid.viewimp.IRegisterAvtivityView
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class RegisterActivityPresenter: AbsPresenter<IRegisterAvtivityView>(){
    var view: IRegisterAvtivityView? = null
    val TAG = "RegisterPresenter"
    var disposable: Disposable? = null

    override fun addView(v: IRegisterAvtivityView) {
        if (view == null){
            view = v
        }
    }

    override fun jumpToTarget(flag: ActionFlag) {
        if (flag != ActionFlag.HOME) return
        val context = view!!.getActivityContext()
        val intent = Intent(context, MainActivityImpl::class.java)
        intent.putExtra("name",view?.getAccount())
        intent.putExtra("isOnline",true)
        context.startActivity(intent)
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

   fun register(){
        RetrofitClient.INSTANCE.retrofit.create(LogInOutRetrofit::class.java)
                .register(view?.getAccount()?:"",view?.getPassword()?:"",view?.getConfPassword()?:"")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(object: Consumer<RegisterRsp>{
                    override fun accept(t: RegisterRsp) {
                        Log.d(TAG,"注册中 consume -> accept")
                        if(t.errorCode < 0){
                            view?.showError(t.errorMsg)
                            //取消订阅
                            disposable?.dispose()
                        }else {
                            view?.showSuccess("注册成功自动登录！")
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(object: Function<RegisterRsp,ObservableSource<Response<LoginRsp>> >{
                    override fun apply(t: RegisterRsp): ObservableSource<Response<LoginRsp>> {
                        return RetrofitClient.INSTANCE.retrofit.create(LogInOutRetrofit::class.java)
                                .login(view?.getAccount(),view?.getPassword())
                    }
                })
                .subscribe(object: Observer<Response<LoginRsp>>{
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG,"onSubscribe")
                        disposable = d
                    }

                    override fun onNext(response: Response<LoginRsp>) {
                        Log.d(TAG,"onNext")
                        if (response.body()!!.errorCode >= 0) {
                            view?.showSuccess("登录成功！")
                            jumpToTarget(ActionFlag.HOME)
                            UserInfo.INSTANCE.userName = view?.getAccount()?:""
                            CookieUtil.initCookie(response)
                            CookieUtil.isSaveCookies(CookieUtil.isAutoLogin())
                        } else {
                            view?.showError(response.body()?.errorMsg?:"登录失败！")
                        }
                    }

                    override fun onComplete() {
                        Log.d(TAG,"onComplete")
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG,"注册并登录失败：${e.printStackTrace()}")
                        view?.showError("注册并失败，请重试！")
                    }
                })
   }


}