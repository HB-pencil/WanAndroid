package com.example.shinelon.wanandroid.utils

import android.util.Log
import com.example.shinelon.wanandroid.UserInfo
import okhttp3.Interceptor
import okhttp3.Response

class CustomInterceptor : Interceptor {
    val TAG = "CustomInterceptor"
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (UserInfo.INSTANCE.isAuto) {
            request = request.newBuilder()
                    .addHeader("cookie", UserInfo.INSTANCE.cookie)
                    .build()
        Log.d(TAG,"拦截器加了cookie: ${UserInfo.INSTANCE.cookie}")
        }
        return chain.proceed(request)
    }
}