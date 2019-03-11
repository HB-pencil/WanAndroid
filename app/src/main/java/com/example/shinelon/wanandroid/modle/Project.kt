package com.example.shinelon.wanandroid.modle

data class ProjectInfo(val data: MutableList<ProjectCategory>,val errorCode: Int,val errorMsg: String)
data class ProjectCategory(val courseId: Int,val id: Int,val name: String,val order: Int,val parentChapterId: Int,val visible: Int)

data class ProjectItemInfo(val data: MutableList<ProjectData>,val errorCode: Int,val errorMsg: String)
data class ProjectData(val curPage: Int,val datas: MutableList<ProjectItemData>,val offset: Int,val over: Boolean,
                       val pageCount: Int,val size: Int,val total: Int)
data class  ProjectItemData(val apkLink: String,val author: String,val chapterId: Int,val chapterName: String,val collect: Boolean,
                            val id: Int,val link: String,val niceDate: String,val origin: String,val projectLink: String,
                            val publishTime: String,val superChapterName: String,val title: String,val tyoe: Int,val visible: Int,val zan: Int)
