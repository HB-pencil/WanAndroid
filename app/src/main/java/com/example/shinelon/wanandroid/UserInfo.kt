package com.example.shinelon.wanandroid

class UserInfo private constructor() {
    var userName: String = ""
    /**
     * 将cookie转化为单例中的字符串
     */
    var isAuto = false
    var cookie = ""
    var expire = Long.MIN_VALUE
    var isOnline = false

    companion object {
        val INSTANCE by lazy (LazyThreadSafetyMode.SYNCHRONIZED){
            UserInfo()
        }
    }
}