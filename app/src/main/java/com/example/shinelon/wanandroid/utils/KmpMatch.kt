package com.example.shinelon.wanandroid.utils

class KmpMatch {
    companion object {
        fun isMatch(parentStr: String, childStr: String): Boolean{
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