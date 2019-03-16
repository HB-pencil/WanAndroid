package com.example.shinelon.wanandroid.networkimp

import com.example.shinelon.wanandroid.modle.Articles
import com.example.shinelon.wanandroid.modle.StructData
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StructPageRetrofit {
    @GET("/tree/json")
    fun getStructParent() : Observable<StructData>

    @GET("/article/list/{num}/json")
    fun getStructDetails(@Path("num") num: Int,@Query("cid") cid: Int) : Observable<Articles>
}