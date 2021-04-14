package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

public class TelescopeView extends View {
    private final Paint mPaint;
    private final Bitmap mBitmap;
    private Bitmap mBGBitmap;
    private float mDx = 100,mDy = 100,mScale = 1;
    private BitmapShader mShader;
    final Matrix mShaderMatrix = new Matrix();

    private final  ScaleGestureDetector mScaleGestureDetector  ;

    private boolean isScale = false;

    public TelescopeView(Context context) {
        this(context,null);
    }

    public TelescopeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0,0);
    }

    public TelescopeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public TelescopeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.background);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(),new MyScaleGestureDetector());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!isScale){
                    mDx = event.getX();
                    mDy = event.getY();
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!isScale){
                    mDx = event.getX();
                    mDy = event.getY();
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                break;
        }
        return mScaleGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBGBitmap == null){
            mBGBitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
            final Canvas stretch = new Canvas(mBGBitmap);
            stretch.drawBitmap(mBitmap,null,new Rect(0,0,getWidth(),getHeight()),mPaint);

            mShader = new BitmapShader(mBGBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }else {
            mShaderMatrix.setScale(mScale,mScale,mDx,mDy);
            mShader.setLocalMatrix(mShaderMatrix);
        }

        mPaint.setShader(mShader);
        canvas.drawCircle(mDx,mDy,200,mPaint);
    }

    public void scale(final float x){
        mScale = x;
    }

    private class MyScaleGestureDetector implements ScaleGestureDetector.OnScaleGestureListener{
        private float scale = 1;
        private float preScale = 1;// 默认前一次缩放比例为1
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float previousSpan = detector.getPreviousSpan();
            float currentSpan = detector.getCurrentSpan();
            if (currentSpan < previousSpan) {
                // 缩小
                scale = preScale - (previousSpan - currentSpan) / 1000;
            } else {
                // 放大
                scale = preScale + (currentSpan - previousSpan) / 1000;
            }
            // 缩放view
            // scale表示缩放比，等于0时组件消失，为1时撑满父容器
            if (scale <= 0) {
                scale = 0;
            }
            scale(scale);
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            isScale = true;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            isScale = false;
            preScale = scale;
        }
    }
}
