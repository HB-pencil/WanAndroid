package com.example.shinelon.wanandroid.customview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup

/**
 * 自定义流式布局
 */
class FlowLayout : ViewGroup {
    val TAG = "FlowLayout"

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet): this(context,attr,0)
    constructor(context: Context, attr: AttributeSet, defStyle: Int) : super(context, attr, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val originWidth = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val originHeight = MeasureSpec.getSize(heightMeasureSpec)
        Log.d(TAG,"originW:$originWidth originH:$originHeight")

        for (i in 0 until childCount) {
            //使用此方法才能正确测量margin。widthUsed等为一些限制因素，通常为0即可
            measureChildWithMargins(getChildAt(i), widthMeasureSpec, 0, heightMeasureSpec, 0)
        }

        var width = 0 //最终宽度
        var height = 0 //最终高度

        var cwSum = paddingStart //当前行的宽度累加
        var wSum = 0 //最终行要确定的宽度
        var cHeight = 0 //当前行内子View的高度
        var hSum = paddingTop //最终ViewGroup的高度

        //考虑都为AT_MOST时
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val params = child.layoutParams as FlowLayoutParams
            cwSum += child.measuredWidth + params.leftMargin + params.rightMargin //假设都在同行，先累加

            if (cwSum > originWidth - paddingEnd) { //大于允许的宽度，则该换行
                cwSum -= child.measuredWidth - params.leftMargin - params.rightMargin //还原当前行的宽度
                wSum = if (cwSum > wSum) cwSum else wSum //取宽度最大的那行作为结果

                cwSum = child.measuredWidth + params.leftMargin + params.rightMargin + paddingStart //新一行的当前宽度累加
                hSum += cHeight // 换行才可以确定前一行的高度
                cHeight = child.measuredHeight + params.topMargin + params.bottomMargin //新一行的当前高度
            } else {
                //寻找行内最高的为当前行高
                val temp = child.measuredHeight + params.topMargin + params.bottomMargin
                cHeight = if (temp > cHeight) temp else cHeight
            }
        }
        hSum += cHeight + paddingBottom //记得还要加上最后一行高度
        wSum += paddingRight
        Log.d(TAG,"hSum:$hSum wSum:$wSum")

        if (widthMode == MeasureSpec.AT_MOST) {
            width = wSum
        } else {
            width = originWidth
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            height = hSum
        } else {
            //TODO 为啥这里ViewGroup wrap_content 是 EXACTLY并且还是0
            //height = originHeight
            height = hSum
        }

        setMeasuredDimension(width, height)
        Log.d(TAG,"width:$width  height:$height")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var left = 0
        var top = 0
        var right = 0
        var bottom = 0
        var cwSum = paddingStart
        var cHeight = 0
        var sHeight = paddingTop

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val params = child.layoutParams as FlowLayoutParams
            cwSum += child.measuredWidth + params.leftMargin + params.rightMargin

            if (cwSum > measuredWidth  - paddingRight ) { //超过换行，其他思路同上
                //cwSum -= child.measuredWidth + params.leftMargin + params.rightMargin
                sHeight += cHeight
                cHeight = child.measuredHeight + params.topMargin + params.bottomMargin
                cwSum = child.measuredWidth + params.leftMargin + params.rightMargin + paddingStart

                left = params.leftMargin + paddingStart
                top = sHeight + params.topMargin
                right = child.measuredWidth + params.leftMargin + paddingStart
                bottom = top + child.measuredHeight
            } else {
                val temp = child.measuredHeight + params.topMargin + params.bottomMargin
                cHeight = if (temp > cHeight) temp else cHeight

                left = cwSum - child.measuredWidth - params.rightMargin
                top = sHeight + params.topMargin
                right = cwSum - params.rightMargin
                bottom = top + child.measuredHeight
            }
            child.layout(left, top, right, bottom)
            Log.d(TAG, "left:$left  top:$top  right:$right  bottom:$bottom")
        }
        sHeight += cHeight + paddingBottom
    }

    /**
     * 覆盖原来的默认方法
     */
    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return FlowLayoutParams(this.context,attrs)
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return FlowLayoutParams(p)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return FlowLayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
    }

    /**
     * 继承该类重新定义LayoutParams，使margin生效
     */
    class FlowLayoutParams : MarginLayoutParams {
        internal constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
        internal constructor(source: ViewGroup.LayoutParams) : super(source)
        internal constructor(width: Int, height: Int) : super(width, height)
    }
}