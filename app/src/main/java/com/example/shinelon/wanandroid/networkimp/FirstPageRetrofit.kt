package com.example.shinelon.wanandroid.networkimp

import com.example.shinelon.wanandroid.modle.Articles
import com.example.shinelon.wanandroid.modle.Banner
import com.example.shinelon.wanandroid.modle.CollectedArticles
import com.example.shinelon.wanandroid.modle.HotWord
import io.reactivex.Observable
import io.reactivex.Observer
import retrofit2.Call
import retrofit2.http.*

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
    fun getArticle(@Path("pageNum") pageNum: Int): Observable<Articles>

    /**
     * 收藏文章列表
     */
    @GET("/lg/collect/list/{pageNum}/json")
    fun getCollectedArtls(@Path("pageNum") pageNum: Int): Observable<CollectedArticles>

    /**
     * 搜索文章
     */
    @POST("/article/query/{pageNum}/json")
    @FormUrlEncoded
    fun getArticleSearch(@Path("pageNum") pageNum: Int,@Field("k") k: String): Observable<Articles>

    /**
     * 首页banner
     * http://www.wanandroid.com/banner/json
     * 方法：GET
     *参数：无
     */
    @GET("/banner/json")
    fun getBanner(): Observable<Banner>

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
}