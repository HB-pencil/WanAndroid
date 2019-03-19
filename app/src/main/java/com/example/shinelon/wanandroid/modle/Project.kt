package com.example.shinelon.wanandroid.modle

data class ProjectInfo(val data: MutableList<ProjectCategory>,val errorCode: Int,val errorMsg: String)
data class ProjectCategory(val courseId: Int,val id: Int,val name: String,val order: Int,val parentChapterId: Int,val visible: Int)