package com.example.shinelon.wanandroid.modle

data class CollectedArticles(val data: CDataBean, val errorCode: Int, val errorMessage: String)
data class CDataBean(val datas: MutableList<CDatasBean>, val curPage: Int,val offset: Int, val over: Boolean, val pageCount: Int,
                    val size: Int, val total: Int)
data class CDatasBean(val apLink: String, val author: String, val chapterId: Int, val chapterName: String, val courseId: Int,
                      val desc: String, val envelopPic: String,val id: Long, val link: String,val niceDate: String, val origin: String,
                      val originId: Long, val publishTime: Long, val superChapterId: String,val title: String, val userId: Long, val visible: Int, val zan: Int)