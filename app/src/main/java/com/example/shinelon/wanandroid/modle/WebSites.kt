package com.example.shinelon.wanandroid.modle

/**
 * Created by Shinelon on 2018/4/28.
 */
data class WebSites(val data: ArrayList<DataBeanWeb>, val errorCode: String, val errorMessage: String)
data class DataBeanWeb(val icon: String,val id: Int,val link: String,val name: String,val order: Int,val visible: Int)