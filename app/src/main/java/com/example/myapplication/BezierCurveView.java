package com.example.myapplication;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class BezierCurveView extends View {
    private final Paint mPaint;
    private float mCurTime = 1.0f;
    private PointF P1 = new PointF(0,0),P2 = new PointF(500,500),P3 = new PointF(100,300);

    private boolean isP2 = false,isP1 = false,isP3 = false;

    private ValueAnimator mAnimator;

    private final Path mDstPath = new Path(),mBezierCurvePath = new Path();
    private final PathMeasure mPathMeasure = new PathMeasure();

    private static final int mPointRadius = 10;

    public BezierCurveView(Context context) {
        this(context,null);
    }

    public BezierCurveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BezierCurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public BezierCurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setBackgroundColor(Color.WHITE);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);

        getCurTime();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int a = event.getAction();
        float x = (int) event.getX(),y = (int) event.getY();

        switch (a){
            case MotionEvent.ACTION_DOWN:
                if (isInCircle(x,y,P2.x,P2.y,mPointRadius)){
                    isP2 = true;
                }else if (isInCircle(x,y,P1,mPointRadius)){
                    isP1 = true;
                }else if (isInCircle(x,y,P3,mPointRadius)){
                    isP3 = true;
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (isP2){
                    P2.x = x;
                    P2.y = y;
                }else if (isP1){
                    P1.x = x;
                    P1.y = y;
                }else if (isP3){
                    P3.x = x;
                    P3.y = y;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isP2 = false;
                isP1 = false;
                isP3 = false;
                clear();
                break;
        }
        return super.onTouchEvent(event);
    }


    private boolean isInCircle(float x, float y, float circleX, float circleY, float radius){
        return Math.sqrt(Math.pow(circleX - x,2) + Math.pow(circleY - y,2)) <= radius;
    }
    private boolean isInCircle(float x, float y, PointF circleCenter, float radius){
        return isInCircle(x,y,circleCenter.x,circleCenter.y,radius);
    }

    private void getCurTime(){
        mAnimator = ValueAnimator.ofFloat(0,1);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurTime = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.setDuration(5000);
    }

    public void startAnimator(){
        if (!mAnimator.isStarted()){
            generateSecondBezierCurvePath();
            mAnimator.start();
        }
    }

    public void clear(){
        mBezierCurvePath.reset();
        mDstPath.reset();
        mPathMeasure.setPath(null,false);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        secondBezier(canvas);
        drawPointName(canvas);
    }

    private void firstBezier(Canvas canvas){
        mPaint.setColor(Color.RED);
        canvas.drawCircle(P1.x,P1.y,5,mPaint);

        mPaint.setColor(Color.GREEN);
        for (float i = 0;i < mCurTime;i += 0.001)
            canvas.drawCircle((1 - i) * P1.x + i * P2.x,(1 - i) * P1.y + i * P2.y,5,mPaint);

        mPaint.setColor(Color.RED);
        canvas.drawCircle(P2.x,P2.y,5,mPaint);
    }

    private void secondBezier(Canvas canvas){

        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.GREEN);
        if (!mAnimator.isRunning()){
            drawSecondBezierCurve(canvas);
        }else {
            mPathMeasure.getSegment(0,mPathMeasure.getLength() * mCurTime,mDstPath,true);
            canvas.drawPath(mDstPath,mPaint);
        }
    }

    private void generateSecondBezierCurvePath(){
        mBezierCurvePath.reset();
        mBezierCurvePath.moveTo(P1.x,P1.y);
        for (float i = 0;i <= mCurTime;i += 0.001){
            float x = (1 - i) * (1 - i) * P1.x + 2 * i * (1- i) * P3.x + i * i *P2.x ;
            float y = (1 - i) * (1 - i) * P1.y + 2 * i * (1- i) * P3.y + i * i *P2.y ;

            mBezierCurvePath.lineTo(x,y);
        }
        mPathMeasure.setPath(mBezierCurvePath,false);
    }
    private void drawSecondBezierCurve(Canvas canvas){
        for (float i = 0;i <= mCurTime;i += 0.001){
            float x = (1 - i) * (1 - i) * P1.x + 2 * i * (1- i) * P3.x + i * i *P2.x ;
            float y = (1 - i) * (1 - i) * P1.y + 2 * i * (1- i) * P3.y + i * i *P2.y ;

            canvas.drawPoint(x,y,mPaint);
        }
    }

    private void drawPointName(Canvas canvas){

        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(18);

        //起点

        canvas.save();
        if (isP1){
            canvas.scale(2,2,P1.x,P1.y);
        }
        canvas.drawCircle(P1.x,P1.y,mPointRadius,mPaint);
        canvas.drawText("起点",P1.x,P1.y - mPointRadius,mPaint);

        canvas.restore();

        //终点
        canvas.save();
        if (isP2){
            canvas.scale(2,2,P2.x,P2.y);
        }
        mPaint.setColor(Color.RED);
        canvas.drawCircle(P2.x,P2.y,mPointRadius,mPaint);
        canvas.drawText("终点",P2.x,P2.y - mPointRadius,mPaint);

        canvas.restore();


        //控制点
        canvas.save();
        if (isP3){
            canvas.scale(2,2,P3.x,P3.y);
        }
        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(P3.x,P3.y,mPointRadius,mPaint);
        canvas.drawText("控制点",P3.x,P3.y - mPointRadius,mPaint);

        canvas.restore();

        //起点、控制点、终点连接线
        canvas.drawLine(P1.x,P1.y,P3.x,P3.y,mPaint);
        canvas.drawLine(P3.x,P3.y,P2.x,P2.y,mPaint);
    }
}
