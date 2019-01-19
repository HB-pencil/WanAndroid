package com.example.shinelon.wanandroid.helper

import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.View
import android.view.ViewGroup

class ViewPagerAdapter(val pageList: MutableList<View>): PagerAdapter() {
    var listener: OnItemClickListener? = null
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(pageList[position%pageList.size])
        pageList[position%pageList.size].setOnClickListener {
            listener?.onItemClick(position%pageList.size)
        }
        return pageList[position%pageList.size]
    }

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(pageList[position%pageList.size])
    }

    override fun getCount() = Integer.MAX_VALUE

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
    }

    fun addItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener{
        fun onItemClick(realPosition: Int)
    }
}