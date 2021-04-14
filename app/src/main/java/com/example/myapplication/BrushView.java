package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;


public class BrushView extends View {
    private float mPreX,mPreY;
    private final Path mBrushPath = new  Path();
    private final Paint mPaint = new Paint();
    public BrushView(Context context) {
        this(context,null);
    }

    public BrushView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BrushView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public BrushView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(),y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mBrushPath.moveTo(x,y);
                mPreX = x;
                mPreY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                float endX = (mPreX + x) / 2f,endY = (mPreY + y) /2f;
                mBrushPath.quadTo(mPreX,mPreY,endX,endY);
                mPreX = x;
                mPreY = y;
                invalidate();
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mBrushPath,mPaint);
    }


}
