package com.example.shinelon.wanandroid.modle

/**
 * Created by Shinelon on 2018/4/28.
 */
data class Banner(val data: MutableList<DataBeanBanner>, val errorCode: Int, val errorMsg: String)
data class DataBeanBanner(val desc: String,val id: Int,val imagePath: String,val isVisible: Int,
                    val order: Int,val title: String,val type: Int,val url: String)