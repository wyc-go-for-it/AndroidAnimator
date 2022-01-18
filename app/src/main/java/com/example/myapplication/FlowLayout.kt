package com.example.myapplication

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.*
import com.wyc.logger.Logger

/**
 *
 * @ProjectName:    Android Animator
 * @Package:        com.example.myapplication
 * @ClassName:      FlowLayout
 * @Description:    流式布局
 * @Author:         wyc
 * @CreateDate:     2022-01-18 9:32
 * @UpdateUser:     更新者
 * @UpdateDate:     2022-01-18 9:32
 * @UpdateRemark:   更新说明
 * @Version:        1.0
 */
class FlowLayout(context: Context, attributes: AttributeSet?, defStyleAttr:Int, defStyleRes:Int) : ViewGroup(context,attributes,defStyleAttr,defStyleRes) {
    constructor(context: Context):this(context,null)
    constructor(context: Context,attributes: AttributeSet?):this(context,attributes,0)
    constructor(context: Context,attributes: AttributeSet?,defStyleAttr:Int):this(context,attributes,defStyleAttr,0)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthSpec = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightSpec = MeasureSpec.getMode(heightMeasureSpec)

        var maxHeight = 0
        var maxWidth = 0

        var childWeight: Float

        val heightList:MutableList<Int> = mutableListOf()
        var lineWidth = 0

        forEach {
            if (it.isGone)return@forEach

            val lp = it.layoutParams as LayoutParams
            childWeight = lp.weight

            if ((lp.height == ViewGroup.LayoutParams.WRAP_CONTENT || lp.height == 0) && childWeight > 0f && heightSpec == MeasureSpec.EXACTLY){
                lp.height = (childWeight * (heightSize - (paddingTop + paddingBottom))).toInt()
            }
            if ((lp.width == ViewGroup.LayoutParams.WRAP_CONTENT || lp.width == 0) && childWeight > 0f && widthSpec == MeasureSpec.EXACTLY){
                lp.width = (childWeight * (widthSize - (paddingLeft + paddingRight))).toInt()
            }
            measureChild(it,widthMeasureSpec,heightMeasureSpec)

            lineWidth += it.measuredWidth + lp.leftMargin + lp.rightMargin
            if (lineWidth > measuredWidth - paddingLeft - paddingRight){
                maxHeight += getMaxHeightOfChildren(heightList)
                lineWidth = it.measuredWidth
                heightList.clear()
            }
            heightList.add(it.measuredHeight + lp.topMargin + lp.bottomMargin)

            maxWidth += it.measuredWidth + lp.leftMargin + lp.rightMargin
        }
        if (heightList.isNotEmpty()){
            maxHeight += getMaxHeightOfChildren(heightList)
        }

        maxHeight += (paddingTop + paddingBottom)
        maxWidth += (paddingLeft + paddingRight)

        var resultSize = 0
        when(heightSpec){
            MeasureSpec.EXACTLY -> {
                resultSize = heightSize
            }
            MeasureSpec.AT_MOST -> {
                resultSize = maxHeight
            }
            MeasureSpec.UNSPECIFIED -> {
                resultSize = maxHeight
            }
        }
        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(resultSize,heightSpec)

        when(widthSpec){
            MeasureSpec.EXACTLY -> {
                resultSize = widthSize
            }
            MeasureSpec.AT_MOST -> {
                resultSize = maxWidth
            }
            MeasureSpec.UNSPECIFIED -> {
                resultSize = maxWidth
            }
        }
        val newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(resultSize,widthSpec)

        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (changed){
            var lineWidth = 0
            var lineHeight = 0
            var leftChild = 0

            val heightList:MutableList<Int> = mutableListOf()
            forEach{
                if (it.isGone)return@forEach

                val lp = it.layoutParams as MarginLayoutParams

                lineWidth += it.measuredWidth + lp.leftMargin + lp.rightMargin
                if (lineWidth > measuredWidth - paddingLeft - paddingRight){
                    lineHeight += getMaxHeightOfChildren(heightList)
                    lineWidth = it.measuredWidth
                    leftChild = 0
                    heightList.clear()
                }
                heightList.add(it.measuredHeight + lp.topMargin + lp.bottomMargin)

                leftChild += lp.leftMargin

                setChildFrame(it,leftChild + paddingLeft,paddingTop + lineHeight + lp.topMargin,it.measuredWidth,it.measuredHeight)

                leftChild += it.measuredWidth + lp.rightMargin
            }

        }
    }
    private fun setChildFrame(child: View, left: Int, top: Int, width: Int, height: Int) {
        child.layout(left, top, left + width, top + height)
    }

    private fun getMaxHeightOfChildren(heightList:MutableList<Int>):Int{
        heightList.sortDescending()
        return if (heightList.isEmpty()) 0 else heightList[0]
    }

    override fun generateLayoutParams(attrs:AttributeSet): LayoutParams {
         return LayoutParams(context,attrs)
    }

    override fun generateLayoutParams(lp:ViewGroup.LayoutParams):ViewGroup.LayoutParams{
        if (lp is FrameLayout.LayoutParams) {
            return FrameLayout.LayoutParams((lp as FrameLayout.LayoutParams?)!!)
        } else if (lp is MarginLayoutParams) {
            return FrameLayout.LayoutParams((lp as MarginLayoutParams?)!!)
        }
        return LayoutParams(lp)
    }

    class LayoutParams : MarginLayoutParams {
        var weight = 0f

        constructor(context: Context,attrs:AttributeSet?) : super(context,attrs){
            val a: TypedArray = context.obtainStyledAttributes(attrs,R.styleable.FlowLayout_Layout)
            weight = a.getFloat(R.styleable.FlowLayout_Layout_android_layout_weight, 0f)
            a.recycle()
        }
        constructor(width:Int,height:Int) : super(width,height)
        constructor(source: ViewGroup.LayoutParams) : super(source)
        constructor(source: MarginLayoutParams) : super(source)
        constructor(source: LayoutParams) : super(source){
            weight = source.weight
        }
    }
}