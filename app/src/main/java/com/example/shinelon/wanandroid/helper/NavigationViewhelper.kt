package com.example.shinelon.wanandroid.helper

import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.util.Log

class NavigationViewhelper{
    companion object {
        fun disableShiftMode(view: BottomNavigationView){
            val menuView = view.getChildAt(0) as BottomNavigationMenuView
            try {
                val shiftMode = menuView::class.java.getDeclaredField("mShiftingMode")
                shiftMode.isAccessible = true
                shiftMode.setBoolean(menuView,false)
                shiftMode.isAccessible = false

                for (i in 0 until menuView.childCount){
                    val itemView = menuView.getChildAt(i) as BottomNavigationItemView
                    itemView.setShiftingMode(false)
                    itemView.setChecked(itemView.itemData.isChecked)
                }
            }catch (e: NoSuchFieldException){
                Log.e("BNHelper","Unable to get shift mode field",e)
            }catch (e: IllegalAccessException){
                Log.e("BNHelper","Unable to change value of shift mode")
            }
        }
    }
}