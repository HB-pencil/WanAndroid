package com.example.shinelon.wanandroid.helper

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class StructPageAdapter(val pages: MutableList<Fragment>,val titles: MutableList<String>,fm: FragmentManager): FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = pages[position]

    override fun getCount() = pages.size

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}