package com.example.myapplication;

import android.animation.Animator;
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
    private final PointF P1 = new PointF(0,0),P2 = new PointF(500,500),
            P3 = new PointF(100,300),P4 = new PointF(150,400);

    //二阶点
    private final PointF C_P1 = new PointF(0,0),C_P2 = new PointF(500,500),
            C_P3 = new PointF(100,300);
    //一阶点
    private final PointF C_C_P1 = new PointF(0,0),C_C_P2 = new PointF(500,500);

    //曲线点
    private final PointF CurvePoint = new PointF(0,0);

    private boolean isP2 = false,isP1 = false,isP3 = false,isP4 = false;

    private ValueAnimator mAnimator;

    private final Path mDstPath = new Path(),mBezierCurvePath = new Path(),mTrackPath = new Path();
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

        generateCurTimeAnimator();

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
                }else if (isInCircle(x,y,P4,mPointRadius)){
                    isP4 = true;
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
                }else if (isP4){
                    P4.x = x;
                    P4.y = y;
                }
                C_P1.x = P1.x;
                C_P1.y = P1.y;

                C_P2.x = P3.x;
                C_P2.y = P3.y;

                C_P3.x = P4.x;
                C_P3.y = P4.y;

                C_C_P1.x = C_P1.x;
                C_C_P1.y = C_P1.y;

                C_C_P2.x = C_P2.x;
                C_C_P2.y = C_P2.y;

                CurvePoint.x = C_C_P1.x;
                CurvePoint.y = C_C_P1.y;

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isP2 = false;
                isP1 = false;
                isP3 = false;
                isP4 = false;

                mTrackPath.moveTo(x,y);

                clear();

                invalidate();
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

    private void generateCurTimeAnimator(){
        mAnimator = ValueAnimator.ofFloat(0,1);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurTime = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTrackPath.reset();
                mTrackPath.moveTo(P1.x,P1.y);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                C_P1.x = P1.x;
                C_P1.y = P1.y;

                C_P2.x = P3.x;
                C_P2.y = P3.y;

                C_P3.x = P4.x;
                C_P3.y = P4.y;

                C_C_P1.x = C_P1.x;
                C_C_P1.y = C_P1.y;

                C_C_P2.x = C_P2.x;
                C_C_P2.y = C_P2.y;

                CurvePoint.x = C_C_P1.x;
                CurvePoint.y = C_C_P1.y;

                clear();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.setDuration(10000);
    }

    public void startAnimator(){
        if (!mAnimator.isStarted()){
            generateThirdBezierCurvePathByPath();
            mAnimator.start();
        }
    }

    public void pauseAnimator(){
        mAnimator.pause();
    }
    public void resumeAnimator(){
        mAnimator.resume();
    }

    public void clear(){
        mBezierCurvePath.reset();
        mDstPath.reset();
        mPathMeasure.setPath(null,false);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBezier(canvas);
        drawThirdBezierPointName(canvas);
    }

    //一阶移动轨迹算法
    private void firstBezier(Canvas canvas){
        mPaint.setColor(Color.RED);
        canvas.drawCircle(P1.x,P1.y,5,mPaint);

        mPaint.setColor(Color.GREEN);
        for (float i = 0;i < mCurTime;i += 0.001)
            canvas.drawCircle((1 - i) * P1.x + i * P2.x,(1 - i) * P1.y + i * P2.y,5,mPaint);

        mPaint.setColor(Color.RED);
        canvas.drawCircle(P2.x,P2.y,5,mPaint);
    }

    private void drawBezier(Canvas canvas){

        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.GREEN);
        if (!mAnimator.isRunning()){
            drawThirdBezierCurve(canvas);
            drawTrackPath(canvas);
        }else {
            mPathMeasure.getSegment(0,mPathMeasure.getLength() * mCurTime,mDstPath,true);
            canvas.drawPath(mDstPath,mPaint);

            drawTrackPoint(canvas);
        }
    }

    //二阶移动轨迹算法
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

    //二阶
    private void generateSecondBezierCurvePathByPath(){
        mBezierCurvePath.reset();
        mBezierCurvePath.moveTo(P1.x,P1.y);
        mBezierCurvePath.quadTo(P3.x,P3.y,P2.x,P2.y);
        mPathMeasure.setPath(mBezierCurvePath,false);
    }

    //三阶
    private void generateThirdBezierCurvePathByPath(){
        mBezierCurvePath.reset();
        mBezierCurvePath.moveTo(P1.x,P1.y);
        mBezierCurvePath.cubicTo(P3.x,P3.y,P4.x,P4.y,P2.x,P2.y);
        mPathMeasure.setPath(mBezierCurvePath,false);
    }

    private void drawSecondBezierCurve(Canvas canvas){
        for (float i = 0;i <= mCurTime;i += 0.001){
            float x = (1 - i) * (1 - i) * P1.x + 2 * i * (1- i) * P3.x + i * i *P2.x ;
            float y = (1 - i) * (1 - i) * P1.y + 2 * i * (1- i) * P3.y + i * i *P2.y ;

            canvas.drawPoint(x,y,mPaint);
        }
    }

    //三阶移动轨迹算法
    private void drawThirdBezierCurve(Canvas canvas){
        //B(t)= P0(1 - t)3+ 3P1t(1-t)2+3P2t2(1 -t)+P3t3,t[ O, 1]
        for (float i = 0;i <= mCurTime;i += 0.001){
            float x = (float) (Math.pow((1 - i),3)* P1.x + 3 * P3.x * i * Math.pow((1- i),2) + 3 * P4.x*Math.pow(i,2) *(1 - i) + i * i + P2.x * Math.pow(i,3));
            float y = (float) (Math.pow((1 - i),3)* P1.y + 3 * P3.y * i * Math.pow((1- i),2) + 3 * P4.y*Math.pow(i,2) *(1 - i) + i * i + P2.y * Math.pow(i,3));
            canvas.drawPoint(x,y,mPaint);
        }
    }

    private void drawSecondBezierPointName(Canvas canvas){

        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(24);

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


        //控制点3
        canvas.save();
        if (isP3){
            canvas.scale(2,2,P3.x,P3.y);
        }
        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(P3.x,P3.y,mPointRadius,mPaint);
        canvas.drawText("控制点3",P3.x,P3.y - mPointRadius,mPaint);

        canvas.restore();


        //起点、控制点、终点连接线
        canvas.drawLine(P1.x,P1.y,P3.x,P3.y,mPaint);
        canvas.drawLine(P3.x,P3.y,P4.x,P4.y,mPaint);
        canvas.drawLine(P4.x,P4.y,P2.x,P2.y,mPaint);
    }

    private void drawThirdBezierPointName(Canvas canvas){
        drawSecondBezierPointName(canvas);

        //控制点4
        canvas.save();
        if (isP4){
            canvas.scale(2,2,P4.x,P4.y);
        }
        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(P4.x,P4.y,mPointRadius,mPaint);
        canvas.drawText("控制点4",P4.x,P4.y - mPointRadius,mPaint);

        canvas.restore();

    }

    private void updateTrackPoint(){

        C_P1.x = C_P1.x + mCurTime * (P3.x - C_P1.x);
        C_P1.y = C_P1.y + mCurTime * (P3.y - C_P1.y);

        C_P2.x =(1 - mCurTime) * C_P2.x + mCurTime * P4.x;
        C_P2.y =(1 - mCurTime) * C_P2.y + mCurTime * P4.y;

        C_P3.x =(1 - mCurTime) * C_P3.x + mCurTime * P2.x;
        C_P3.y =(1 - mCurTime) * C_P3.y + mCurTime * P2.y;

        C_C_P1.x = C_C_P1.x + mCurTime * (C_P2.x - C_C_P1.x);
        if (!Utils.equalDouble(C_P1.x,C_P2.x)) {
            C_C_P1.y = (C_P1.y - C_P2.y) / (C_P1.x - C_P2.x) * (C_C_P1.x - C_P2.x) + C_P2.y;
        }

        C_C_P2.x = C_C_P2.x + mCurTime * (C_P3.x - C_C_P2.x);
        if (!Utils.equalDouble(C_P2.x ,C_P3.x)){
            C_C_P2.y = (C_P2.y - C_P3.y ) / (C_P2.x - C_P3.x) * ( C_C_P2.x - C_P3.x) + C_P3.y;
        }

        CurvePoint.x = CurvePoint.x + mCurTime * (C_C_P2.x - CurvePoint.x);
        if (!Utils.equalDouble(C_C_P1.x,C_C_P2.x)){
            CurvePoint.y = (C_C_P1.y - C_C_P2.y ) / (C_C_P1.x - C_C_P2.x) * ( CurvePoint.x - C_C_P2.x) + C_C_P2.y;
        }

        if(!Utils.equalDouble(CurvePoint.x,C_C_P2.x)){
            mTrackPath.lineTo(CurvePoint.x,CurvePoint.y);
        }
    }


    @SuppressLint("ResourceAsColor")
    private void drawTrackPoint(Canvas canvas){

        updateTrackPoint();

        mPaint.setStrokeWidth(0);
        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(C_P1.x,C_P1.y,mPointRadius,mPaint);
        canvas.drawText("C_P1",C_P1.x,C_P1.y - mPointRadius,mPaint);

        canvas.drawCircle(C_P2.x,C_P2.y,mPointRadius,mPaint);
        canvas.drawText("C_P2",C_P2.x,C_P2.y - mPointRadius,mPaint);

        canvas.drawCircle(C_P3.x,C_P3.y,mPointRadius,mPaint);
        canvas.drawText("C_P3",C_P3.x,C_P3.y - mPointRadius,mPaint);

        canvas.drawLine(C_P1.x,C_P1.y,C_P2.x,C_P2.y,mPaint);
        canvas.drawLine(C_P2.x,C_P2.y,C_P3.x,C_P3.y,mPaint);

        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(C_C_P1.x,C_C_P1.y,mPointRadius,mPaint);
        canvas.drawText("C_C_P1",C_C_P1.x,C_C_P1.y - mPointRadius,mPaint);


        canvas.drawCircle(C_C_P2.x,C_C_P2.y,mPointRadius,mPaint);
        canvas.drawText("C_C_P2",C_C_P2.x,C_C_P2.y - mPointRadius,mPaint);

        canvas.drawLine(C_C_P1.x,C_C_P1.y,C_C_P2.x,C_C_P2.y,mPaint);

        mPaint.setColor(R.color.teal_200);
        canvas.drawCircle(CurvePoint.x,CurvePoint.y,mPointRadius,mPaint);
        canvas.drawText("CurvePoint",CurvePoint.x,CurvePoint.y - mPointRadius,mPaint);

        drawTrackPath(canvas);
    }

    @SuppressLint("ResourceAsColor")
    private void drawTrackPath(Canvas canvas){
        mPaint.setColor(R.color.teal_200);
        canvas.drawPath(mTrackPath,mPaint);
    }

}
