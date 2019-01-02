package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.example.shinelon.wanandroid.LoginActivityImpl
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.viewimp.IMainActivityView

class MainActivityPresenter: AbsPresenter<IMainActivityView>() {
    var view: IMainActivityView? = null
    var userState = false
    override fun addView(v: IMainActivityView) {
        view = v
    }


    override
    fun checkPermissions(permissions: Array<String>){
        val res = isGrantedPermissions(permissions)
        if(!res){
            ActivityCompat.requestPermissions(view!!.getActivityContext(),permissions,0)
        }
    }

    fun isGrantedPermissions(permissions: Array<String>): Boolean{
        permissions.forEach {
            if(ContextCompat.checkSelfPermission(view!!.getActivityContext(),it) == PackageManager.PERMISSION_DENIED){
                return false
            }
        }
        return true
    }

    fun checkAutoLogin(){
        //TODO
    }

    override fun jumpToTarget() {
        val context = view!!.getActivityContext()
        val intent = Intent(context,LoginActivityImpl::class.java)
        context.startActivity(intent)
        context.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    }

    fun login(){
        jumpToTarget()
    }
}