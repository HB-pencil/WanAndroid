package com.example.shinelon.wanandroid.networkimp

import com.example.shinelon.wanandroid.modle.RequestResult
import io.reactivex.Observable
import retrofit2.http.POST
import retrofit2.http.Path

interface CollectStateRetrofit {
    @POST("/lg/collect/{id}/json")
    fun doCollect(@Path("id") id: Long): Observable<RequestResult>

    @POST("/lg/uncollect_originId/{id}/json")
    fun cancelCocllect(@Path("id") id: Long): Observable<RequestResult>
}