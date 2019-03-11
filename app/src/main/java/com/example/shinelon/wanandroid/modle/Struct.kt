package com.example.shinelon.wanandroid.modle

data class StructDataC(val data: StructContentC,val errorCode: Int,val errorMsg: String)
data class StructContentC(val curPage: Int,val datas: DatasContentC,val offset: Int,val over: Boolean,
                          val pageCount: Int, val size: Int,val total: Int)
data class DatasContentC(val contentList: MutableList<StructDetailsC>)
data class StructDetailsC(val apkLink: String,val author: String,val chapterId: Int,val chapterName: String,
                          val collect: Boolean,val courseId: Int,val desc: String,val envelopePic: String,
                          val fresh: Boolean,val id: Int,val link: String,val niceDate: String,val origin: String,
                          val projectLink: String,val publishTime: Long,val superChapterId: Int,val superChapterName: String,
                          val title: String,val type: Int,val visible: Int,val zan: Int)


data class StructData(val data: MutableList<SuperData>,val errorCode: Int,val errorMsg: String)
data class SuperData(val children: MutableList<ChildData>,val courseId: Int,val id: Int,val name: String,
                     val order: Int,val parentChapterId: Int,val visible: Int)
data class ChildData(val courseId: Int,val id: Int,val name: String,val order: Int,val parentChapterId: Int,
                     val visible: Int)