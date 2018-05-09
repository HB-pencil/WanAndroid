package com.example.shinelon.wanandroid.model.firstpage

/**
 * Created by Shinelon on 2018/4/28.
 */
data class Banner(val data: ArrayList<DataBeanBanner>,val errorCode: Int,val errorMessage: String)
data class DataBeanBanner(val desc: String,val id: Int,val imagePath: String,val isVisible: Boolean,
                    val order: Int,val title: String,val type: Int,val url: String)