package com.example.shinelon.wanandroid.networkimp

import retrofit2.http.GET
import retrofit2.http.Query

interface ProjectPageRetrofit {
    @GET("/project/tree/json")
    fun getProjectInfo()

    @GET("/project/list/1/json")
    fun getProjectItemInfo(@Query("cid") cid: Int)
}