package com.example.shinelon.wanandroid.helper

import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.Animation

/**
 * Created by Shinelon on 2018/5/13.
 */
class MyBehavior: CoordinatorLayout.Behavior<View>(){
    var flagTop = false
    var flagBottom = false
    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {

        return axes==ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        //向上滑动，scroller坐标与view坐标系是不同的
        if(dy>0 && !flagTop){
            child.animate().translationY(child.height.toFloat()).setDuration(200).start()
            flagTop = true
        }
        if(dy<0 && !flagBottom){
            child.animate().translationY(-child.height.toFloat()).setDuration(200).start()
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }
}