package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
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
    private Path mPenPath;
    private final Path mErasePath;
    private final Paint mPaint;

    private final Bitmap mSrcBitmap, mBackgroundBitmap;
    private Bitmap mBrushBitmap,mEraseBitmap;
    private boolean isErase = false;

    private final Canvas mEraseCanvas = new Canvas();

    private final PorterDuffXfermode mDuffXferMode;

    private final Path[] paths = new Path[]{new  Path(),new  Path(),new  Path(),new  Path(),new  Path()};

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

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        //mPaint.setShader(new BitmapShader(BitmapFactory.decodeResource(getResources(),R.drawable.arrows), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));

        mBackgroundBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.other_background);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        mSrcBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.background,options);
        setDst();

        mDuffXferMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);

        mErasePath = new Path();
    }

    private void setDst(){
        if (mEraseBitmap == null)
            mEraseBitmap = Bitmap.createBitmap(mSrcBitmap.getWidth(),mSrcBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        mEraseCanvas.setBitmap(mEraseBitmap);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(),y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (isErase){
                    mErasePath.moveTo(x,y);
                }else {
                    mPenPath = getContentPath();
                    mPenPath.moveTo(x,y);
                }
                mPreX = x;
                mPreY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                float endX = (mPreX + x) / 2f,endY = (mPreY + y) /2f;
                if (isErase){
                    mErasePath.lineTo(endX,endY);
                    //擦除
                    erase();
                }else if (mPenPath != null){
                    mPenPath.quadTo(mPreX,mPreY,endX,endY);
                }
                mPreX = x;
                mPreY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isErase){
                    isErase = false;
                    mErasePath.reset();
                }
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("measureWidth", String.valueOf(getMeasuredWidth()));


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, 100, 50, 200, 100);
        Log.d("width", String.valueOf(getWidth()));
    }

    @Override
    protected void onDraw(Canvas canvas) {



        //绘制底层背景
        canvas.drawBitmap(mBackgroundBitmap,0,0,mPaint);

        canvas.saveLayer(0,0,getWidth(),getHeight(),mPaint);

        //绘制画笔内容
        drawContentPath(canvas);

        mPaint.setXfermode(mDuffXferMode);

        canvas.drawBitmap(mSrcBitmap,0,0,mPaint);

        mPaint.setXfermode(null);

        canvas.restore();

        //绘制擦除刷子
        drawBrush(canvas);

        drawErasePath(canvas);

    }

    private void drawErasePath(final Canvas canvas){
        int c = mPaint.getColor();
        mPaint.setColor(Color.GREEN);
        canvas.drawPath(mErasePath,mPaint);
        mPaint.setColor(c);
    }

    private Path getContentPath(){
        Path path = null,next;
        int len = paths.length,first = len - 2;
        for (int i = first;i >= 0;i --){
            path = paths[i];
            if (!path.isEmpty()){
                next = paths[i + 1];
                if (i == first){
                    next.addPath(path);
                    path.reset();
                }else {
                    final Path tmp = path;
                    path = next;
                    paths[i] =  path;
                    paths[i + 1] = tmp;
                }
            }
        }
        return path;
    }

    private void drawContentPath(final Canvas canvas){
        for (Path p : paths){
            if (p.isEmpty())continue;
            canvas.drawPath(p,mPaint);
        }
    }

    private void clearContentPath(){
        for (Path p : paths){
            p.reset();
        }
    }

    public void revocation(){
        for (Path p : paths){
            if (p.isEmpty())continue;
            p.reset();
            invalidate();
            return;
        }
    }

    public void reset(){
        clearContentPath();
        invalidate();
    }

    public void initErase(){
        isErase = true;
        if (mBrushBitmap == null)mBrushBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.brush);
    }
    private void drawBrush(final Canvas canvas){
        if (isErase){
            canvas.drawBitmap(mBrushBitmap,mPreX - (mBrushBitmap.getWidth() >> 1),mPreY - (mBrushBitmap.getHeight() >> 1),mPaint);
        }
    }
    private void erase(){
        if (isErase){
            for (Path p : paths){
                if (p.isEmpty())continue;
                p.op(mErasePath, Path.Op.DIFFERENCE);
            }
        }
    }

}
