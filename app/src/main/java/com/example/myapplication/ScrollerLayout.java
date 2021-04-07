package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import androidx.core.view.ViewConfigurationCompat;

public class ScrollerLayout extends ViewGroup {
         /**
         * 用于完成滚动操作的实例
         */
        private Scroller mScroller;

        /**
         * 判定为拖动的最小移动像素数
         */
        private int mTouchSlop;

        /**
         * 手机按下时的屏幕坐标
         */
        private float mXDown;

        /**
         * 手机当时所处的屏幕坐标
         */
        private float mXMove;

        /**
         * 上次触发ACTION_MOVE事件时的屏幕坐标
         */
        private float mXLastMove;

        /**
         * 界面可滚动的左边界
         */
        private int leftBorder;

        /**
         * 界面可滚动的右边界
         */
        private int rightBorder;

        public ScrollerLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            // 第一步，创建Scroller的实例
            mScroller = new Scroller(context);
            ViewConfiguration configuration = ViewConfiguration.get(context);
            // 获取TouchSlop值
            mTouchSlop = configuration.getScaledPagingTouchSlop();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                // 为ScrollerLayout中的每一个子控件测量大小
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (changed) {
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childView = getChildAt(i);
                    // 为ScrollerLayout中的每一个子控件在水平方向上进行布局
                    //childView.layout(i * childView.getMeasuredWidth(), 0, (i + 1) * childView.getMeasuredWidth(), childView.getMeasuredHeight());
                    childView.layout(0,i * childView.getMeasuredHeight(),childView.getMeasuredWidth(),(i + 1) * childView.getMeasuredHeight());
                }
                // 初始化左右边界值
                leftBorder = getChildAt(0).getTop();
                rightBorder = getChildAt(getChildCount() - 1).getBottom();
            }
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mXDown = ev.getRawY();
                    mXLastMove = mXDown;
                    break;
                case MotionEvent.ACTION_MOVE:
                    mXMove = ev.getRawY();
                    float diff = Math.abs(mXMove - mXDown);
                    mXLastMove = mXMove;
                    // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                    if (diff > mTouchSlop) {
                        return true;
                    }
                    break;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    mXMove = event.getRawY();
                    int scrolledY = (int) (mXLastMove - mXMove);
                    Log.d("getScrollY()", String.valueOf(getScrollY()));
                    if (getScrollY() + scrolledY < leftBorder) {
                        scrollTo(0, leftBorder);
                        return true;
                    } else if (getScrollY() + getHeight() + scrolledY > rightBorder) {
                        scrollTo(0, rightBorder - getHeight());
                        return true;
                    }
                    scrollBy(0, scrolledY);
                    mXLastMove = mXMove;
                    break;
                case MotionEvent.ACTION_UP:
                    // 当手指抬起时，根据当前的滚动值来判定应该滚动到哪个子控件的界面
                    View first = getChildAt(0);
                    int targetIndeY = (getScrollY() + first.getHeight() / 2) / first.getHeight();
                    int dY = targetIndeY * first.getHeight() - getScrollY();
                    // 第二步，调用startScroll()方法来初始化滚动数据并刷新界面
                    mScroller.startScroll(0, getScrollY(), 0, dY);
                    invalidate();
                    break;
            }
            return super.onTouchEvent(event);
        }

        @Override
        public void computeScroll() {
            // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
            if (mScroller.computeScrollOffset()) {
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
                invalidate();
            }
        }
}
