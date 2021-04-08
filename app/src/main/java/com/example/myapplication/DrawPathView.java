package com.example.myapplication;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class DrawPathView extends View {
    private Paint mPaint;
    private Path mDstPath,mCirclePath;
    private PathMeasure mPathMeasure;
    private float mCurAnimValue;
    private Bitmap mArrows;

    private float[] arrowsPos =new float[2];
    private float[] arrowsTan= new float[2];

    private final Matrix matrix= new Matrix() ;

    private ValueAnimator animator;
    public DrawPathView(Context context) {
        this(context,null);
    }

    public DrawPathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DrawPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    @SuppressLint("ResourceAsColor")
    public DrawPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayerType (LAYER_TYPE_SOFTWARE , null);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(R.color.purple_200);

        mDstPath = new Path();

        mCirclePath = new Path();
        mCirclePath.addCircle(0,0,200,Path.Direction.CW);
        mCirclePath.addPath(getSin());
        mPathMeasure = new PathMeasure(mCirclePath,false);

        mArrows = BitmapFactory.decodeResource(getResources(),R.drawable.arrows);

        animator = ValueAnimator.ofFloat(0,2);
/*        animator.setRepeatCount(ValueAnimator.INFINITE);*/
        /*animator.setInterpolator((Interpolator) input -> 1 - input);*/
        animator.addUpdateListener(animation -> {
            mCurAnimValue = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.setDuration(10000).start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            if (!animator.isStarted()){
                mPathMeasure.setPath(mCirclePath,false);
                animator.start();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(getWidth() >> 1,getHeight() >> 1);

        mDstPath.reset();

        float length = mPathMeasure.getLength();

        float stop = 0.0f;

        float start= (float) (stop - ((0.5 - Math.abs(mCurAnimValue - 0.5)) *length)) ;

        if (mCurAnimValue < 1.0){
            stop = length * mCurAnimValue;
        }else if (Math.abs(mCurAnimValue - 1.0) < 0.0001){
            mPathMeasure.nextContour();
        }else {
            stop = length * (mCurAnimValue - 1);
        }
        mPathMeasure.getSegment(0,stop,mDstPath,true);
        mPathMeasure.getPosTan(stop,arrowsPos,arrowsTan);

        float degrees = (float) (Math.atan2(arrowsTan[1],arrowsTan[0]) * 180.0/Math.PI) ;
        matrix.reset();
        int half_w = mArrows.getWidth() >> 1,half_h = mArrows.getHeight() >> 1;
        matrix .postRotate(degrees, half_w , half_h) ;
        matrix . postTranslate(arrowsPos[0] - half_w , arrowsPos[1] - half_h) ;
        canvas.drawBitmap(mArrows, matrix ,mPaint );
        canvas.drawPath(mDstPath,mPaint);

        canvas.restore();
    }

    private Path getSin(){
        final Path path = new Path();

        for (float step = 0; step <= 15 * Math.PI; step += 0.01){
            path.lineTo(20 * step, 150*(float) Math.sin(step));
        }
        return path;
    }

}
