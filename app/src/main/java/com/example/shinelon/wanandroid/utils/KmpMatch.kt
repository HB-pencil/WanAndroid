package com.example.shinelon.wanandroid.utils

import android.util.Log
import java.lang.Exception

class KmpMatch {
    companion object {
        val TAG = "KmpMatch"
        fun isMatch(parentStr: String, childStr: String): Boolean{
            try {
                var i = 0
                var j = 0
                val next = getNext(childStr.toCharArray())
                while (i<parentStr.length && j<childStr.length) {
                    if (j == -1 || parentStr[i] == childStr[j] ||
                            Math.abs(parentStr[i] - childStr[j]) == Math.abs('a' - 'A')) {
                        i++
                        j++
                    }else {
                        j = next[j]
                    }
                }
                return j == childStr.length
            }catch (e: Exception){
                Log.e(TAG,e.message)
                return false
            }
        }

        private fun getNext(array: CharArray): IntArray{
            val next = IntArray(array.size)
            next[0] = -1
            var k = -1
            var j = 0
            while (j < array.size -1) {
                if (k == -1 || next[j] == next[k]) {
                    next[++j] = ++k
                }else {
                    k = next[k]
                }
            }
            return next
        }
    }
}