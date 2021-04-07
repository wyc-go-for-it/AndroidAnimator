package com.example.myapplication;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.Nullable;

public class SegmentView extends View {
    private Paint mPaint;
    private Path mDstPath,mCirclePath;
    private PathMeasure mPathMeasure;
    private float mCurAnimValue;
    public SegmentView(Context context) {
        this(context,null);
    }

    public SegmentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SegmentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    @SuppressLint("ResourceAsColor")
    public SegmentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayerType (LAYER_TYPE_SOFTWARE , null);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(R.color.purple_200);

        mDstPath = new Path();

        mCirclePath = new Path();
        mCirclePath.addCircle(100,100,50,Path.Direction.CW);

        mPathMeasure = new PathMeasure(mCirclePath,true);

        ValueAnimator animator = ValueAnimator.ofFloat(0,1);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator((Interpolator) input -> 1 - input);
        animator.addUpdateListener(animation -> {
            mCurAnimValue = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.setDuration(2000).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float stop = mPathMeasure.getLength() * mCurAnimValue;
        mDstPath.reset();
        mPathMeasure.getSegment(0,stop,mDstPath,true);
        canvas.drawPath(mDstPath,mPaint);
    }

}
