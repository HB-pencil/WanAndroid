package com.example.shinelon.wanandroid.networkimp

import com.example.shinelon.wanandroid.modle.HotWord
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Shinelon on 2018/4/28. 首页相关的请求接口
 */
interface FirstPageRetrofit {

    /**
     *http://www.wanandroid.com/article/list/0/json
     *方法：GET
     *参数：页码，拼接在连接中，从0开始。
     *"superChapterId": 153,
     *"superChapterName": "framework", // 一级分类的名称
     */
    @GET("/article/list/{pageNum}/json")
    fun getArticle(@Path("pageNum") pageNum: Int)

    /**
     * 首页banner
     * http://www.wanandroid.com/banner/json
     * 方法：GET
     *参数：无
     */
    @GET("/banner/json")
    fun getBanner()

    /**
     * 常用网站
     * http://www.wanandroid.com/friend/json
     *方法：GET
     *参数：无
     */
    @GET("/friend/json")
    fun getFriendWebSite()

    /**
     * 搜索热词
     *  http://www.wanandroid.com//hotkey/json
     *方法：GET
     *参数：无
     */
    @GET("/hotkey/json")
    fun getHotWord(): Observable<HotWord>

    /**
     *
     */
}