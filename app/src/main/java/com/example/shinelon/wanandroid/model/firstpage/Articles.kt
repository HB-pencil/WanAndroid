package com.example.shinelon.wanandroid.model.firstpage

/**
 * Created by Shinelon on 2018/4/28.
 */
data class Articles(val datas: DatasBean,val errorCode: Int,val errorMessage: String)
data class DatasBean(val data: ArrayList<DataBean>,val offset: Int,val over: Boolean,val pageCount: Int,
                     val size: Int,val total: Int)
data class DataBean(val apLink: String,val author: String,val chapterId: Int,val chapterName: String,val collect: Boolean,
                    val courseId: Int,val desc: String,val envelopPic: String,val fresh: Boolean,val id: Int,val link: String,
                    val niceDate: String,val origin: String,val projectLink: String,val publishTime: Long,val superChapterId: String,
                    val superChapterName: String,val tags: ArrayList<TagType>,val title: String,val type: Int,val visible: Int,val zan: Int)
data class TagType(val name: String,val url: String)