package com.example.myapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class AnimWaveView extends View {
    private final Path mWavePath = new  Path();
    private final Paint mPaint = new Paint();
    private int mItemWaveLength = 1200;
    private int dx;
    private int mWaveOriginY = 0;
    public AnimWaveView(Context context) {
        this(context,null);
    }

    public AnimWaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AnimWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public AnimWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);


        startAnim();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        mWaveOriginY = getMeasuredHeight() - getMeasuredHeight() / 3;
        mItemWaveLength = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mWavePath.reset();

        int halfWaveLen = mItemWaveLength /5;
        mWavePath.moveTo(-mItemWaveLength + dx, mWaveOriginY);
        for (int i = 0; i < 5; i ++) {
            mWavePath.rQuadTo(halfWaveLen / 2f, -100, halfWaveLen, 0);
            mWavePath.rQuadTo(halfWaveLen / 2f, 100, halfWaveLen, 0);

            canvas.drawLine(i * halfWaveLen ,0,i * halfWaveLen ,mWaveOriginY,mPaint);
        }
        mWavePath.lineTo(getWidth(),getHeight());
        mWavePath.lineTo(0,getHeight());
        mWavePath.close();

        canvas.drawPath(mWavePath,mPaint);
    }

    public void startAnim() {
        ValueAnimator animator= ValueAnimator.ofInt(0, mItemWaveLength);
        animator.setDuration(2000) ;
        animator.setRepeatCount(ValueAnimator.INFINITE) ;
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dx = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
    }
}
