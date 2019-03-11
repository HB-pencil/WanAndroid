package com.example.shinelon.wanandroid.networkimp

import com.example.shinelon.wanandroid.modle.NavigateInfo
import io.reactivex.Observable
import retrofit2.http.GET

interface NavigatePageRetrofit {
    @GET("/navi/json")
    fun getNavigationInfo(): Observable<NavigateInfo>
}