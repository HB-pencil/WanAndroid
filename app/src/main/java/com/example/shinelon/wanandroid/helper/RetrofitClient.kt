package com.example.shinelon.wanandroid.helper

import com.example.shinelon.wanandroid.utils.CustomInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor(){
    /**
     * 线程安全的单例模式
     */
    companion object {
        val INSTANCE: RetrofitClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitClient()
        }
    }

    private val client = OkHttpClient.Builder()
            .addNetworkInterceptor(CustomInterceptor())
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
}