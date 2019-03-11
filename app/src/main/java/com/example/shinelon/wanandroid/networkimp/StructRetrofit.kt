package com.example.shinelon.wanandroid.networkimp

import com.example.shinelon.wanandroid.modle.StructData
import com.example.shinelon.wanandroid.modle.StructDataC
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface StructPageRetrofit {
    @GET("/tree/json")
    fun getStructParent() : Observable<StructData>

    @GET("/article/list/0/json")
    fun getStructDetails(@Query("cid") cid: Int) : Observable<StructDataC>
}