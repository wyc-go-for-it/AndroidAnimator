package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SwipeLayout extends FrameLayout {
    private OverScroller mScroller;

    private int mTouchSlop;

    private float mXDown,mXMove,mLastXMove;

    private View mContentView;

    private int mLeftBorder;

    private int mRightBorder;

    private LinearLayout mRightView;

    private List<MenuItem> mMenuList;

    public SwipeLayout(Context context) {
        this(context,null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(final Context context){
        mScroller = new OverScroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
        mContentView = View.inflate(context,R.layout.bezier_curve_layout,this);

        mRightView = new LinearLayout(context);
        mRightView.setOrientation(LinearLayout.HORIZONTAL);
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRightView.setLayoutParams(layoutParams);
        addView(mRightView);

        mMenuList = new ArrayList<>();

        addMenuItem("第一个", new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"第一个",Toast.LENGTH_LONG).show();
            }
        },Color.RED);
        addMenuItem("第二个", new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"第二个",Toast.LENGTH_LONG).show();
            }
        },Color.BLUE);
        addMenuItem("第三个", new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"第三个",Toast.LENGTH_LONG).show();
            }
        },Color.GREEN);
        addMenuItem("第三个", new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"第三个",Toast.LENGTH_LONG).show();
            }
        },Color.BLACK);
    }

    public void addMenuItem(final String title,View.OnClickListener listener,int color){
        mMenuList.add(MenuItem.Builder.create().setTitle(title).
                setListener(listener).
                setBackgroundColor(color).
                builder());
    }

    private static class MenuItem{
        public MenuItem(Builder builder){
            mTitle = builder.mTitle;
            mListener = builder.mListener;
            mBackgroundColor = builder.mBackgroundColor;
        }
        private final String mTitle;
        private final View.OnClickListener mListener;
        private final int mBackgroundColor;

        static class Builder{
            private String mTitle;
            private View.OnClickListener mListener;
            private int mBackgroundColor;

            public Builder setTitle(String mTitle) {
                this.mTitle = mTitle;
                return this;
            }

            public Builder setBackgroundColor(int mBackgroundColor) {
                this.mBackgroundColor = mBackgroundColor;
                return this;
            }

            public Builder setListener(OnClickListener mListener) {
                this.mListener = mListener;
                return this;
            }

            public MenuItem builder(){
                return new MenuItem(this);
            }
            public static Builder create(){
                return new Builder();
            }
        }
    }

    private void addMenu(){
        for (MenuItem item : mMenuList){
            final TextView tv = generateMenuView();
            tv.setText(item.mTitle);
            tv.setOnClickListener(item.mListener);
            tv.setBackgroundColor(item.mBackgroundColor);
            mRightView.addView(tv);
        }
    }
    private TextView generateMenuView(){
        final TextView tv = new TextView(getContext());
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(16);
        tv.setTextColor(getResources().getColor(R.color.white,null));
        tv.setLayoutParams(new LinearLayout.LayoutParams(getMeasuredHeight(), ViewGroup.LayoutParams.MATCH_PARENT));
        tv.setClickable(true);
        return tv;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastXMove = mXDown = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = ev.getRawX();
                float diff = Math.abs(mXMove - mXDown);
                mLastXMove = mXMove;
                if (diff > mTouchSlop){
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int moveX = 0,w = getWidth();
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                mXMove = event.getRawX();
                moveX = (int) (mLastXMove - mXMove);
                int scrollX = getScrollX();
                if (scrollX + moveX < mLeftBorder){
                    scrollTo(mLeftBorder,0);
                    return true;
                }else if (scrollX + moveX + w > mRightBorder){
                    scrollTo(mRightBorder - w,0);
                    return true;
                }
                scrollBy(moveX,0);
                mLastXMove = mXMove;
                break;
            case MotionEvent.ACTION_UP:
                moveX = getScrollX() + w;
                int dx = 0;
                if (moveX > mContentView.getRight() + mRightView.getWidth() / 2){
                    dx = mRightBorder - moveX;
                }else {
                    dx = mContentView.getRight() - moveX;
                }
                mScroller.startScroll(getScrollX(),0,dx,0);
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mRightView.getChildCount() == 0)addMenu();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            mContentView.layout(left,top,right,bottom);
            mRightView.layout(right,top,mRightView.getChildCount() * (bottom - top) + right,bottom);
            mRightBorder = mRightView.getRight();
            mLeftBorder = left;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
}
