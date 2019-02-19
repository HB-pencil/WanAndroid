package com.example.shinelon.wanandroid.presenter

import android.content.Intent
import com.example.shinelon.wanandroid.CommonWebViewActivity
import com.example.shinelon.wanandroid.helper.ActionFlag
import com.example.shinelon.wanandroid.viewimp.ISearchArticleActivityView

class ISearchArticleActivityPresebter: AbsPresenter<ISearchArticleActivityView>(){
    var view: ISearchArticleActivityView? =null;
    override fun addView(view: ISearchArticleActivityView) {
        this.view = view
    }

    override fun jumpToTarget(flag: ActionFlag) {

    }

    fun loadWeb(url: String){
        val context = view!!.getActivityContext()
        val intent = Intent(context, CommonWebViewActivity::class.java)
        intent.putExtra("web_url",url)
        context.startActivity(intent)
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}