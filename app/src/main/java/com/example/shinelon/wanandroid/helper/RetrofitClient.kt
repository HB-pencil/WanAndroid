package com.example.shinelon.wanandroid.helper

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor(){
    companion object {
        private var instance: RetrofitClient? = null
            get() {
                if(field == null){
                    field = RetrofitClient()
                }
                return field
            }

        fun get(): RetrofitClient = instance!!
    }

    val retrofit = Retrofit.Builder()
            .baseUrl("http://www.wanandroid.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
}