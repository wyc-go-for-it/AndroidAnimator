package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class DrawTextView extends View {
    private final Paint mPaint;
    private float mBaseLineX,mBaseLineY;
    private static final float BaseLineLength = 1000f;
    private static final String mText = "have a blog";
    private final Rect mTextBound = new Rect();

    public DrawTextView(Context context) {
        this(context,null);
    }

    public DrawTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DrawTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    @SuppressLint("ResourceAsColor")
    public DrawTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setBackgroundColor(Color.WHITE);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(R.color.purple_200);

        mBaseLineX = 0;
        mBaseLineY = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.translate(getMeasuredWidth() / 2f,getMeasuredHeight() / 2f);
        canvas.scale(5,5);

        //画原点
        canvas.drawCircle(0,0,5,mPaint);


        mPaint.setTextSize(36);
        //mPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm = mPaint.getFontMetrics( ) ;
        float ascent = mBaseLineY + fm.ascent ;
        float descent = mBaseLineY + fm.descent ;
        float top = mBaseLineY + fm.top;
        float bottom = mBaseLineY + fm.bottom;

        //画最文字最小矩形
        mPaint.setColor(Color.BLACK);
        mPaint.getTextBounds(mText,0,mText.length(),mTextBound);//获取的矩形以原点Y坐标为基线的Y坐标，所以当绘制矩形时需要按基线的Y坐标更新矩形坐标
        mTextBound.top = (int) (mTextBound.top + mBaseLineY);
        mTextBound.bottom = (int) (mTextBound.bottom + mBaseLineY);
        canvas.drawRect(mTextBound,mPaint);
        //画文字
        mPaint.setColor(Color.RED);
        canvas.drawText(mText,mBaseLineX,mBaseLineY,mPaint);



        //画基线
        mPaint.setColor(Color.RED);
        canvas . drawLine(mBaseLineX , mBaseLineY , BaseLineLength ,mBaseLineY , mPaint) ;
        //画 top
        mPaint.setColor(Color.BLUE );
        canvas.drawLine(mBaseLineX , top ,BaseLineLength , top, mPaint) ;
        //画 ascent
        mPaint.setColor(Color . GREEN ) ;
        canvas.drawLine(mBaseLineX , ascent , BaseLineLength ,ascent, mPaint) ;

        //画 descent
        mPaint.setColor(Color.GREEN );
        canvas.drawLine(mBaseLineX , descent , BaseLineLength , descent, mPaint) ;
        //画 bottom
        mPaint.setColor(Color.BLUE);
        canvas.drawLine(mBaseLineX, bottom, BaseLineLength , bottom, mPaint) ;

    }
}
