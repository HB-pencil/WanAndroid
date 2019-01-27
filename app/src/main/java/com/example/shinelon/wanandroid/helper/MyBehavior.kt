package com.example.shinelon.wanandroid.helper

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.Animation

/**
 * Created by Shinelon on 2018/5/13.
 */
class MyBehavior(context: Context,attribute: AttributeSet): CoordinatorLayout.Behavior<View>(context,attribute){

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return axes==ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        //向上滑动，scroller坐标与view坐标系是不同的
        //x y是相对父的，left top也是但是距离不变，translateX是相对最开始的平移
        val animate = child.animate()
        if(dy>0){
            animate.translationY(child.height.toFloat()).setDuration(200).start()
        }
        if(dy<0){
            animate.translationY(0F).setDuration(200).start()
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }
}