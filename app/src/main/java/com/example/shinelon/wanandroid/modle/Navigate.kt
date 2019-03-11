package com.example.shinelon.wanandroid.modle

data class NavigateInfo(val data: MutableList<NavigateData>,val errorCode: Int,val errorMsg: String)
data class NavigateData(val articles: MutableList<WebsiteInfo>,val cid: Int,val name: String)
data class WebsiteInfo(val apkLink: String,val author: String,val chapterId: Int,val chapterName: String,
                       val collect: Boolean,val courseId: Int,val desc: String,val envelopePic: String,val fresh: Boolean,
                       val id: Int,val link: String,val niceDate: String,val origin: String,val projectLink: String,val publishTime: String,
                       val superChapterId: Int,val superChapterName: String,val title: String,val type: Int,val visible: Int,val zan: Int)
