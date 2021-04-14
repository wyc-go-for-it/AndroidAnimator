package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @ProjectName: Android Animator
 * @Package: com.example.myapplication
 * @ClassName: PorterDuffXferModeView
 * @Description: Paint 混合模式绘图
 * @Author: wyc
 * @CreateDate: 2021/4/14 18:32
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/4/14 18:32
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class PorterDuffXferModeView extends View {
    private float mPreX,mPreY;
    private final Path mBrushPath = new  Path();
    private final Paint mPaint = new Paint();

    private final Bitmap mSrc,mBackground;
    private Bitmap mDst;

    private final Canvas mDrawPathCanvas = new Canvas();


    private PorterDuffXfermode mDuffXferMode;

    public PorterDuffXferModeView(Context context) {
        this(context,null);
    }

    public PorterDuffXferModeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PorterDuffXferModeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public PorterDuffXferModeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayerType(LAYER_TYPE_SOFTWARE,null);

        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(50);

        mBackground = BitmapFactory.decodeResource(getResources(),R.drawable.other_background);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        mSrc = BitmapFactory.decodeResource(getResources(),R.drawable.background,options);

        setDst();

        mDuffXferMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
    }


    @SuppressLint("ClickableViewAccessibility")
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

        int id1 = canvas.save();
        mPaint.setXfermode(null);
        canvas.drawBitmap(mBackground,0,0,mPaint);
        canvas.restoreToCount(id1);

        int id = canvas.save();

        mDrawPathCanvas.drawPath(mBrushPath,mPaint);

        canvas.drawBitmap(mDst,0,0,mPaint);

        mPaint.setXfermode(mDuffXferMode);
        canvas.drawBitmap(mSrc,0,0,mPaint);
        mPaint.setXfermode(null);

        canvas.restoreToCount(id);
    }

    private void setDst(){
        mDst = Bitmap.createBitmap(mSrc.getWidth(),mSrc.getHeight(), Bitmap.Config.ARGB_8888);
        mDrawPathCanvas.setBitmap(mDst);
    }

    public void reset(){
        mBrushPath.reset();
        setDst();
        invalidate();
    }

}
