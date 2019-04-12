package com.example.shinelon.wanandroid.utils

import android.util.Log
import com.example.shinelon.wanandroid.modle.Entry
import java.util.*

class TopNFilter {
    companion object {
        val TAG = "TopNFilter"
        fun getTopN(n: Int,list: MutableList<Entry>): MutableList<String>{
            val res = mutableListOf<String>()
            if (list.size <= n) {
                list.forEach {
                    res.add(it.key)
                }
                return res
            }
            val queue = PriorityQueue<Entry>(n){e1,e2->(e1.value - e2.value)}
            var i = 0
            list.forEach {
                if (i<n) {
                    queue.offer(it)
                    i++
                }else {
                    if(queue.peek().value<it.value) {
                        queue.poll()
                        queue.offer(it)
                    }
                }
            }
            queue.forEach { res.add(it.key) }
            res.reverse()
            Log.d(TAG,"获取Top$n 成功 size:${res.size}")
            return res
        }
    }
}