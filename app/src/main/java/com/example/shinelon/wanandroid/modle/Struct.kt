package com.example.shinelon.wanandroid.modle

import java.io.Serializable
import java.util.ArrayList

data class StructData(val data: MutableList<SuperData>,val errorCode: Int,val errorMsg: String)
data class SuperData(val children: ArrayList<ChildData>,val courseId: Int,val id: Int,val name: String,
                     val order: Int,val parentChapterId: Int,val visible: Int)
data class ChildData(val courseId: Int,val id: Int,val name: String,val order: Int,val parentChapterId: Int,
                     val visible: Int): Serializable