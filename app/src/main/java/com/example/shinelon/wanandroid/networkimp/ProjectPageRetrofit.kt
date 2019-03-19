package com.example.shinelon.wanandroid.networkimp

import com.example.shinelon.wanandroid.modle.Articles
import com.example.shinelon.wanandroid.modle.ProjectInfo
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProjectPageRetrofit {
    @GET("/project/tree/json")
    fun getProjectInfo(): Observable<ProjectInfo>

    @GET("/project/list/{num}/json")
    fun getProjectItemInfo(@Path("num") num: Int, @Query("cid") cid: Int): Observable<Articles>
}