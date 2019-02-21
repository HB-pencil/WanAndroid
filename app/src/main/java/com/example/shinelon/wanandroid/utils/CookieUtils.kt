package com.example.shinelon.wanandroid.utils

import android.util.Log
import com.example.shinelon.wanandroid.UserInfo
import com.example.shinelon.wanandroid.modle.loginout.LoginRsp
import retrofit2.Response
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

class CookieUtils{
    companion object {
        val TAG = "CookieUtil"
        fun initCookie(response: Response<LoginRsp>){
            val list = response.headers().values("Set-Cookie")
            val src = list[list.size-1].split(";")[2].replace("Expires=","")
            val expire = parseTime(src)
            UserInfo.INSTANCE.expire = expire
            val res = parseCookie(response)
            UserInfo.INSTANCE.cookie = res
            Log.d(TAG,"cookie:$res")
        }

        fun parseCookie(response: Response<LoginRsp>): String{
            val list = response.headers().values("Set-Cookie")
            val sb = StringBuilder()
            list.forEach{
                val target = it.split(";")[0]
                sb.append("$target;")
            }
            return sb.toString()
        }

        fun isSaveCookies(isSave: Boolean) {
            var cookieString = ""
            var expireTime = Long.MIN_VALUE
            val spUtil = PreferenceUtils.getInstance()
            if(isSave) {
                cookieString = UserInfo.INSTANCE.cookie
                expireTime = UserInfo.INSTANCE.expire
            }
            spUtil.putString("cookie",cookieString)
            spUtil.putLong("expireTime",expireTime)
            spUtil.putString("username",UserInfo.INSTANCE.userName)
            spUtil.commit()
        }

        /**
         * @param time 时间格式  Fri, 01-Feb-2019 15:40:01 GMT
         */
        fun parseTime(time: String): Long{
            try{
                //FIXME Date.parse()应该找到替代的方法
                val res = Date.parse(time)
                Log.d(TAG,"$res")
                return res
            }catch (e: Exception){
                Log.e(TAG,"日期格式化错误 ${e.printStackTrace()}")
            }
            return Long.MIN_VALUE
        }

        fun isAutoLogin(): Boolean{
            val spUtil = PreferenceUtils.getInstance()
            return spUtil.getBoolean("isAuto")
        }
    }
}