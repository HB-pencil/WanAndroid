package com.example.shinelon.wanandroid

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import com.example.shinelon.wanandroid.helper.RetrofitClient
import com.example.shinelon.wanandroid.helper.toast
import com.example.shinelon.wanandroid.modle.RequestResult
import com.example.shinelon.wanandroid.networkimp.CollectStateRetrofit
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_common_web.*
import kotlinx.android.synthetic.main.activity_toolbar.*

class CommonWebViewActivity: AppCompatActivity(){
    val TAG = "CommonWebViewActivity"
    var webView: WebView? = null
    var isCollected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_web)

        setSupportActionBar(toolbar_base)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        webView = WebView(this)
        web_container.addView(webView)
        val layoutParams = webView!!.layoutParams as FrameLayout.LayoutParams
        layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
        layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT

        webView!!.settings.javaScriptEnabled = true
        webView!!.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView!!.settings.setAppCacheEnabled(true)
        webView!!.settings.setSupportZoom(false)
        webView!!.settings.domStorageEnabled = true

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView!!.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }

        var url = intent.getStringExtra("web_url")
        //玩安卓bug，http无法在P访问
        var regex = Regex("^(http://www.wanandroid.com)")
        url = url.replace(regex,"https://www.wanandroid.com")
        regex = Regex("http://blog.csdn.net")
        url = url.replace(regex,"https://blog.csdn.net")
        isCollected = intent.getBooleanExtra("collect_state",false)
        val id = intent.getLongExtra("id",-1)
        if (isCollected) article_collect_btn.setImageDrawable(resources.getDrawable(R.drawable.pic_collected_art,theme))
        Log.d(TAG,"url:$url id:$id isCollect:$isCollected")

        if(id<0) article_collect_btn.visibility = View.INVISIBLE

        webView!!.webViewClient = object : WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progress_bar.visibility = View.VISIBLE
                Log.d(TAG,"onPagedStarted")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progress_bar.visibility = View.GONE
                Log.d(TAG,"onPagedFinished")
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                Log.e(TAG,"onReceiveError $error")
            }

            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                super.onReceivedHttpError(view, request, errorResponse)
                Log.e(TAG,"onReceiveError $errorResponse")
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return true
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                super.onReceivedSslError(view, handler, error)
                Log.e(TAG,"onReceiveError $error")
                // TODO 证书验证
                handler?.proceed()
            }
        }

        webView!!.webChromeClient = object : WebChromeClient(){
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                return super.onJsAlert(view, url, message, result)
            }

            override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
                return super.onJsPrompt(view, url, message, defaultValue, result)
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                return super.onJsConfirm(view, url, message, result)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progress_bar.progress = newProgress
            }
        }
        webView!!.loadUrl(url)

        article_collect_btn.setOnClickListener {
            if (!UserInfo.INSTANCE.isOnline) {
                //TODO 此处为什么可以 this
                val intent = Intent(this,LoginActivityImpl::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                toast(this,"请先登录")
            } else {
                if (isCollected) {
                    cancelCollect(id)
                }else {
                    doCollect(id)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * WARNING: setResult()必须在finish()之前调用
     */
    override fun finish() {
        val intent = Intent()
        intent.putExtra("isCollected",isCollected)
        setResult(Activity.RESULT_OK,intent)
        Log.i(TAG,"回传：$isCollected")
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        val parent = webView?.parent as ViewGroup
        parent.removeView(webView)
        webView?.destroy()
    }


    override fun onBackPressed() {
        if(webView!!.canGoBack()){
            webView!!.goBack()
        }else {
            super.onBackPressed()
        }
    }

    /**
     * 收藏或者取消收藏文章
     */
    fun doCollect(id: Long){
        RetrofitClient.INSTANCE.retrofit.create(CollectStateRetrofit::class.java)
                .doCollect(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<RequestResult>{
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG,"onSubscribe")
                    }

                    override fun onNext(t: RequestResult) {
                        if(t.errorCode>=0){
                            toast(this@CommonWebViewActivity,"收藏成功")
                            article_collect_btn.setImageDrawable(resources.getDrawable(R.drawable.pic_collected_art,theme))
                            isCollected = true
                        }else {
                            toast(this@CommonWebViewActivity,"收藏失败，请重试")
                            isCollected = false
                        }
                    }

                    override fun onComplete() {
                        Log.d(TAG,"onComplete")
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG,e.message)
                        toast(this@CommonWebViewActivity,"收藏失败，请重试")
                        isCollected = false
                    }
                })
    }

    fun cancelCollect(id: Long){
        RetrofitClient.INSTANCE.retrofit.create(CollectStateRetrofit::class.java)
                .cancelCocllect(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<RequestResult>{
                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG,"onSubscribe")
                    }

                    override fun onNext(t: RequestResult) {
                        if(t.errorCode>=0){
                            toast(this@CommonWebViewActivity,"取消收藏成功")
                            article_collect_btn.setImageDrawable(resources.getDrawable(R.drawable.pic_collect_art,theme))
                            isCollected = false
                        }else {
                            toast(this@CommonWebViewActivity,"取消收藏失败，请重试")
                            isCollected = true
                        }
                    }

                    override fun onComplete() {
                        Log.d(TAG,"onComplete")
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG,e.message)
                        toast(this@CommonWebViewActivity,"取消收藏失败，请重试")
                        isCollected = true
                    }
                })
    }
}