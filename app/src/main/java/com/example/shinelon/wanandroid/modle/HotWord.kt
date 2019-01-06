package com.example.shinelon.wanandroid.modle

/**
 * Created by Shinelon on 2018/4/28.
 */
data class HotWord(val data: ArrayList<DataBeanHot>, val errorCode: Int, val errorMessage: String)
data class DataBeanHot(val id: Int,val link: String,val name: String,val visible: Int)