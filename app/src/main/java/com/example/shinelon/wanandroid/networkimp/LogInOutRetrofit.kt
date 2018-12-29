package com.example.shinelon.wanandroid.networkimp

import com.example.shinelon.wanandroid.model.firstpage.LoginRsp
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface LogInOutRetrofit {
    /**
     * 登录后会在cookie中返回账号密码，只要在客户端做cookie持久化存储即可自动登录验证。
     * http://www.wanandroid.com/user/login
     * 方法：POST
     * 参数：
     * username，password
     */
    @POST("/user/login")
    @FormUrlEncoded
    fun login(@Field("username") username: String?,@Field("password") password: String?): Observable<LoginRsp>

    @POST("/user/register")
    @FormUrlEncoded
    fun register(@Field("username") username: String,@Field("password") password: String,
                 @Field("repassword") repassword: String)

    /**
     * http://www.wanandroid.com/user/logout/json
     *访问了 logout 后，服务端会让客户端清除 Cookie（即cookie max-Age=0），如果客户端 Cookie 实现合理，可以实现自动清理，如果本地做了用户账号密码和保存，及时清理
     */
    @GET("http://www.wanandroid.com/user/logout/json")
    fun logout()
}