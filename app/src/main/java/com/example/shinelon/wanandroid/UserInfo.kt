package com.example.shinelon.wanandroid

class UserInfo private constructor() {
    var userName: String = ""
    /**
     * 将cookie转化为单例中的字符串
     */
    var isAuto = false
    var cookie: String = ""
    var expire: Long = Long.MIN_VALUE
    var isOnline: Boolean = false

    companion object {
        val INSTANCE by lazy (LazyThreadSafetyMode.SYNCHRONIZED){
            UserInfo()
        }
    }
}